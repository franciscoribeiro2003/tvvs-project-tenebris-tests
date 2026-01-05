package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.gui.GUI;
import pt.feup.tvvs.tenebris.gui.LanternaGUI;
import pt.feup.tvvs.tenebris.model.arena.Arena;
import pt.feup.tvvs.tenebris.model.arena.effects.Explosion;
import pt.feup.tvvs.tenebris.model.arena.entities.Dylan;
import pt.feup.tvvs.tenebris.model.arena.entities.Entity;
import pt.feup.tvvs.tenebris.model.arena.entities.monster.*;
import pt.feup.tvvs.tenebris.model.arena.particles.*;
import pt.feup.tvvs.tenebris.model.arena.projectiles.*;
import pt.feup.tvvs.tenebris.model.arena.static_elements.*;
import pt.feup.tvvs.tenebris.model.arena.weapons.*;
import pt.feup.tvvs.tenebris.model.menu.*;
import pt.feup.tvvs.tenebris.savedata.SaveData;
import pt.feup.tvvs.tenebris.savedata.SaveDataManager;
import pt.feup.tvvs.tenebris.savedata.SaveDataProvider;
import pt.feup.tvvs.tenebris.utils.Difficulty;
import pt.feup.tvvs.tenebris.utils.Vector2D;
import pt.feup.tvvs.tenebris.view.arena.ArenaView;
import pt.feup.tvvs.tenebris.view.arena.effects.ExplosionView;
import pt.feup.tvvs.tenebris.view.arena.entity.DylanView;
import pt.feup.tvvs.tenebris.view.arena.entity.EntityView;
import pt.feup.tvvs.tenebris.view.arena.entity.monster.*;
import pt.feup.tvvs.tenebris.view.arena.particles.*;
import pt.feup.tvvs.tenebris.view.arena.projectiles.*;
import pt.feup.tvvs.tenebris.view.arena.staticelement.*;
import pt.feup.tvvs.tenebris.view.arena.weapon.*;
import pt.feup.tvvs.tenebris.view.menu.*;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class ViewWhiteBoxTests {

    private GUI mockGUI;
    private GUI originalGUI;

    @BeforeEach
    public void setup() throws Exception {
        // Reflection to inject Mock GUI into Singleton
        Field instance = LanternaGUI.class.getDeclaredField("guiInstance");
        instance.setAccessible(true);
        originalGUI = (GUI) instance.get(null);
        mockGUI = Mockito.mock(GUI.class);
        instance.set(null, mockGUI);
    }

    @AfterEach
    public void tearDown() throws Exception {
        Field instance = LanternaGUI.class.getDeclaredField("guiInstance");
        instance.setAccessible(true);
        instance.set(null, originalGUI);
    }

    @Test
    public void testEntityViewAnimationLogic() {
        // 1. Setup Mock Entity
        Entity mockEntity = Mockito.mock(Entity.class);
        when(mockEntity.getPosition()).thenReturn(new Vector2D(0, 0));

        // 2. Local concrete class to expose protected members
        class TestEntityView extends EntityView<Entity> {
            public TestEntityView(Entity model) {
                super(model);
            }
            @Override
            public void draw(Vector2D camera) {}

            public void runTick() { this.tickState(); }
            public GUI.AnimationState getState() { return this.state; }
        }

        TestEntityView view = new TestEntityView(mockEntity);

        // 3. Verify Initial State
        assertEquals(GUI.AnimationState.IDLE_1, view.getState());

        // 4. Run ticks until state switches to IDLE_2 (Robust against frame count)
        int safetyCounter = 0;
        while (view.getState() == GUI.AnimationState.IDLE_1 && safetyCounter++ < 100) {
            view.runTick();
        }
        assertEquals(GUI.AnimationState.IDLE_2, view.getState(), "Animation state should switch to IDLE_2");

        // 5. Run ticks until state switches back to IDLE_1
        safetyCounter = 0;
        while (view.getState() == GUI.AnimationState.IDLE_2 && safetyCounter++ < 100) {
            view.runTick();
        }
        assertEquals(GUI.AnimationState.IDLE_1, view.getState(), "Animation state should cycle back to IDLE_1");
    }

    @Test
    public void testEntityView_ExhaustiveStateTransitions() {
        Entity mockEntity = Mockito.mock(Entity.class);
        when(mockEntity.getPosition()).thenReturn(new Vector2D(0, 0));

        class TestEntityView extends EntityView<Entity> {
            public TestEntityView(Entity model) { super(model); }
            @Override public void draw(Vector2D camera) {}
            public void runTick() { this.tickState(); }
            public void runUpdate() { this.updateState(); }
            public GUI.AnimationState getState() { return this.state; }
            public void setState(GUI.AnimationState s) { this.state = s; }
        }
        TestEntityView view = new TestEntityView(mockEntity);

        // --- KILL MUTANTS IN tickState() (Animation Frame Cycling) ---
        GUI.AnimationState[][] cycles = {
                {GUI.AnimationState.IDLE_1, GUI.AnimationState.IDLE_2},
                {GUI.AnimationState.FRONT_1, GUI.AnimationState.FRONT_2},
                {GUI.AnimationState.BACK_1, GUI.AnimationState.BACK_2},
                {GUI.AnimationState.LEFT_1, GUI.AnimationState.LEFT_2},
                {GUI.AnimationState.RIGHT_1, GUI.AnimationState.RIGHT_2}
        };

        for (GUI.AnimationState[] cycle : cycles) {
            view.setState(cycle[0]);

            // Advance until state changes
            int safety = 0;
            while(view.getState() == cycle[0] && safety++ < 100) {
                view.runTick();
            }
            assertEquals(cycle[1], view.getState(), "Should switch from " + cycle[0] + " to " + cycle[1]);

            // Advance until state changes back
            safety = 0;
            while(view.getState() == cycle[1] && safety++ < 100) {
                view.runTick();
            }
            assertEquals(cycle[0], view.getState(), "Should switch from " + cycle[1] + " to " + cycle[0]);
        }

        // --- KILL MUTANTS IN updateState() ---
        // (This part of your previous test was fine, keeping it)
        when(mockEntity.getMoving()).thenReturn(Entity.State.IDLE);
        when(mockEntity.getLooking()).thenReturn(null);
        view.setState(GUI.AnimationState.RIGHT_1);
        view.runUpdate();

        boolean isIdle = view.getState() == GUI.AnimationState.IDLE_1 || view.getState() == GUI.AnimationState.IDLE_2;
        assertTrue(isIdle, "Should reset to IDLE when entity is idle");

        Entity.State[] moveStates = {Entity.State.LEFT, Entity.State.RIGHT, Entity.State.FRONT, Entity.State.BACK};
        GUI.AnimationState[] expectedViews = {GUI.AnimationState.LEFT_1, GUI.AnimationState.RIGHT_1, GUI.AnimationState.FRONT_1, GUI.AnimationState.BACK_1};

        for (int i = 0; i < moveStates.length; i++) {
            when(mockEntity.getMoving()).thenReturn(moveStates[i]);
            when(mockEntity.getLooking()).thenReturn(null);

            view.setState(GUI.AnimationState.IDLE_1);
            view.runUpdate();

            String stateName = view.getState().name();
            String expectedBase = expectedViews[i].name().substring(0, expectedViews[i].name().length() - 2);

            assertTrue(stateName.contains(expectedBase),
                    "Moving " + moveStates[i] + " should result in " + expectedBase + " animation");
        }
    }

    // --- ARENA VIEW TEST ---
    @Test
    public void testArenaView_DrawsEverything() throws IOException {
        // Arrange
        Arena arena = new Arena();
        Dylan dylan = new Dylan(new Vector2D(50, 50), 100, 1);
        TenebrisPeon monster = new TenebrisPeon(new Vector2D(60, 60), 1, 1, 1, 1);
        Wall wall = new Wall(new Vector2D(10, 10));

        arena.setDylan(dylan);
        arena.addElement(monster);
        arena.addElement(wall);

        ArenaView view = new ArenaView(arena);

        // Act
        view.draw();

        // Assert
        verify(mockGUI).drawArenaBackGround();
        verify(mockGUI).drawArenaUI(anyInt(), anyInt());

        verify(mockGUI).drawStaticElement(eq(new Vector2D(10, 10)), eq(GUI.StaticElement.WALL));
        verify(mockGUI).drawDylan(eq(new Vector2D(50, 50)), any());
        verify(mockGUI).drawMonster(eq(new Vector2D(60, 60)), eq(GUI.Monster.TENEBRIS_PEON), any());
    }

    // --- EFFECTS TEST ---
    @Test
    public void testEffectView_Explosion() {
        Vector2D pos = new Vector2D(20, 20);
        Explosion explosion = new Explosion(pos, 10);

        new ExplosionView(explosion).draw(new Vector2D(0,0));

        verify(mockGUI).drawParticleEffect(eq(pos), eq(GUI.ParticleEffect.EXPLOSION), anyInt());
    }

    // --- ENTITY VIEWS ---
    @Test
    public void testEntityViews_StrictPositioning() {
        Vector2D worldPos = new Vector2D(100, 100);
        Vector2D cameraPos = new Vector2D(10, 10);
        Vector2D expectedPos = new Vector2D(90, 90);

        new DylanView(new Dylan(worldPos, 100, 1)).draw(cameraPos);
        verify(mockGUI).drawDylan(eq(expectedPos), any());

        new TenebrisPeonView(new TenebrisPeon(worldPos, 1, 1, 1, 1)).draw(cameraPos);
        verify(mockGUI).drawMonster(eq(expectedPos), eq(GUI.Monster.TENEBRIS_PEON), any());

        new TenebrisHeavyView(new TenebrisHeavy(worldPos, 1, 1, 1, 1)).draw(cameraPos);
        verify(mockGUI).drawMonster(eq(expectedPos), eq(GUI.Monster.TENEBRIS_HEAVY), any());

        new TenebrisHarbingerView(new TenebrisHarbinger(worldPos, 1, 1, 1, 1, 1)).draw(cameraPos);
        verify(mockGUI).drawMonster(eq(expectedPos), eq(GUI.Monster.TENEBRIS_HARBINGER), any());

        new TenebrisWardenView(new TenebrisWarden(worldPos, 1, 1, 1, 1)).draw(cameraPos);
        verify(mockGUI).drawMonster(eq(expectedPos), eq(GUI.Monster.TENEBRIS_WARDEN), any());

        new TenebrisSpikedScoutView(new TenebrisSpikedScout(worldPos, 1, 1, 1, 1)).draw(cameraPos);
        verify(mockGUI).drawMonster(eq(expectedPos), eq(GUI.Monster.TENEBRIS_SPIKED_SCOUT), any());
    }

    // --- PROJECTILES ---
    @Test
    public void testProjectileViews_Strict() {
        Vector2D worldPos = new Vector2D(50, 50);
        Vector2D cameraPos = new Vector2D(0, 0);

        new BulletView(new Bullet(worldPos, Vector2D.Direction.RIGHT)).draw(cameraPos);
        verify(mockGUI).drawProjectile(eq(worldPos), eq(GUI.Projectile.BULLET), eq(Vector2D.Direction.RIGHT));

        new ExplosiveBulletView(new ExplosiveBullet(worldPos, Vector2D.Direction.UP)).draw(cameraPos);
        verify(mockGUI).drawProjectile(eq(worldPos), eq(GUI.Projectile.EXPLOSIVE), any());

        new SpellView(new Spell(worldPos, Vector2D.Direction.LEFT, 10)).draw(cameraPos);
        verify(mockGUI).drawProjectile(eq(worldPos), eq(GUI.Projectile.SPELL), any());
    }

    // --- STATIC ELEMENTS ---
    @Test
    public void testStaticElementViews_Strict() {
        Vector2D worldPos = new Vector2D(20, 20);
        Vector2D cameraPos = new Vector2D(5, 5);
        Vector2D expectedPos = new Vector2D(15, 15);

        new WallView(new Wall(worldPos)).draw(cameraPos);
        verify(mockGUI).drawStaticElement(eq(expectedPos), eq(GUI.StaticElement.WALL));

        new BreakableWallView(new BreakableWall(worldPos, 1)).draw(cameraPos);
        verify(mockGUI).drawStaticElement(eq(expectedPos), eq(GUI.StaticElement.BREAKABLE_WALL));

        new SandbagView(new SandBag(worldPos)).draw(cameraPos);
        verify(mockGUI).drawStaticElement(eq(expectedPos), eq(GUI.StaticElement.SANDBAG));

        new SpikeView(new Spike(worldPos, 1)).draw(cameraPos);
        verify(mockGUI).drawStaticElement(eq(expectedPos), eq(GUI.StaticElement.SPIKE));

        new VisionBlockerView(new VisionBlocker(worldPos)).draw(cameraPos);
    }

    // --- PARTICLES ---
    @Test
    public void testParticleViews() {
        Vector2D pos = new Vector2D(0, 0);

        new DamageBloodView(new DamageBlood(pos)).draw(pos);
        verify(mockGUI).drawParticleEffect(any(), eq(GUI.ParticleEffect.DAMAGE_BLOOD), anyInt());

        new DeathBloodView(new DeathBlood(pos)).draw(pos);
        verify(mockGUI).drawParticleEffect(any(), eq(GUI.ParticleEffect.DEATH_BLOOD), anyInt());

        new SpellExplosionView(new SpellExplosion(pos)).draw(pos);
        verify(mockGUI).drawParticleEffect(any(), eq(GUI.ParticleEffect.SPELL_EXPLOSION), anyInt());

        new BreakableWallDamageView(new BreakableWallDamage(pos)).draw(pos);
        verify(mockGUI).drawParticleEffect(any(), eq(GUI.ParticleEffect.BREAKABLE_WALL_DAMAGE), anyInt());
    }

    // --- WEAPONS ---
    @Test
    public void testWeaponViews() {
        new PistolView(new Pistol()).draw();
        verify(mockGUI).drawWeapon(eq(GUI.Weapon.PISTOL), anyInt());

        new GrenadeLauncherView(new GrenadeLauncher()).draw();
        verify(mockGUI).drawWeapon(eq(GUI.Weapon.GRENADE_LAUNCHER), anyInt());
    }

    // --- MENUS ---
    @Test
    public void testMainMenuWithAllOptions() throws Exception {
        SaveDataProvider mockProvider = Mockito.mock(SaveDataProvider.class);
        SaveData mockSave = Mockito.mock(SaveData.class);
        when(mockProvider.getSaveData()).thenReturn(mockSave);

        try (MockedStatic<SaveDataManager> manager = Mockito.mockStatic(SaveDataManager.class)) {
            SaveDataManager mockManager = Mockito.mock(SaveDataManager.class);
            manager.when(SaveDataManager::getInstance).thenReturn(mockManager);
            when(mockManager.getSaveCount()).thenReturn(1);

            MainMenu menu = new MainMenu(mockProvider);
            MainMenuView view = new MainMenuView(menu);
            view.draw();

            verify(mockGUI).drawMainMenu(argThat(list ->
                    list.contains(GUI.Menu_Options.MAIN_MENU_CONTINUE) &&
                            list.contains(GUI.Menu_Options.MAIN_MENU_LOAD_GAME) &&
                            list.contains(GUI.Menu_Options.MAIN_MENU_NEW_GAME)
            ), anyInt());
        }
    }

    @Test
    public void testAllOtherMenus() throws IOException {
        // Simple Menus
        new NewGameMenuView(new NewGameMenu()).draw();
        verify(mockGUI).drawNewGameMenu(any(), anyInt());

        new CreditsMenuView(new CreditsMenu()).draw();
        verify(mockGUI).drawCreditsMenu();

        new VictoryMenuView(new VictoryMenu()).draw();
        verify(mockGUI).drawVictoryMenu();

        new GameOverMenuView(new GameOverMenu()).draw();
        verify(mockGUI).drawGameOverMenu();

        new DeathMenuView(new DeathMenu()).draw();
        verify(mockGUI).drawDeathMenu(any(), anyInt());

        new LevelCompletedMenuView(new LevelCompletedMenu()).draw();
        verify(mockGUI).drawLevelCompletedMenu(any(), anyInt());

        new HowToPlayMenuView(new HowToPlayMenu()).draw();
        verify(mockGUI).drawHowToPlayMenu(anyInt());

        // Complex Menus (Need Mocking) - LevelsMenu depends on SaveData being available
        SaveDataProvider mockProvider = Mockito.mock(SaveDataProvider.class);
        SaveData mockSave = Mockito.mock(SaveData.class);
        when(mockProvider.getSaveData()).thenReturn(mockSave);
        when(mockSave.getLevel()).thenReturn(1);
        when(mockSave.getDifficulty()).thenReturn(Difficulty.Normal);

        // We wrap in MockedStatic to safely instantiate LevelsMenu which relies on Singleton calls
        try (MockedStatic<SaveDataManager> manager = Mockito.mockStatic(SaveDataManager.class)) {
            SaveDataManager mockManager = Mockito.mock(SaveDataManager.class);
            manager.when(SaveDataManager::getInstance).thenReturn(mockManager);
            // Stub static calls that might happen inside constructor
            when(mockManager.getLastOpen()).thenReturn(mockSave);

            LevelsMenu levelsMenu = new LevelsMenu(mockProvider);
            new LevelsMenuView(levelsMenu).draw();
            verify(mockGUI).drawLevelsMenu(anyInt(), anyInt(), any());
        }

        // 2. Pause Menu
        Arena mockArena = Mockito.mock(Arena.class);
        new PauseMenuView(new PauseMenu(mockArena)).draw();
        verify(mockGUI).drawPauseMenuMenu(any(), anyInt());
    }
}