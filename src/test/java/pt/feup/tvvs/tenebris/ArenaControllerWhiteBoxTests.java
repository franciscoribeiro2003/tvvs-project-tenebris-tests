package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.controller.arena.ArenaController;
import pt.feup.tvvs.tenebris.gui.Action;
import pt.feup.tvvs.tenebris.gui.GUI;
import pt.feup.tvvs.tenebris.model.arena.Arena;
import pt.feup.tvvs.tenebris.model.arena._commands.*;
import pt.feup.tvvs.tenebris.model.arena.effects.Explosion;
import pt.feup.tvvs.tenebris.model.arena.entities.Dylan;
import pt.feup.tvvs.tenebris.model.arena.entities.monster.TenebrisPeon;
import pt.feup.tvvs.tenebris.model.arena.particles.ParticleType;
import pt.feup.tvvs.tenebris.model.arena.projectiles.Bullet;
import pt.feup.tvvs.tenebris.model.arena.static_elements.BreakableWall;
import pt.feup.tvvs.tenebris.model.arena.static_elements.Wall;
import pt.feup.tvvs.tenebris.savedata.SaveData;
import pt.feup.tvvs.tenebris.savedata.SaveDataProvider;
import pt.feup.tvvs.tenebris.sound.SoundManager;
import pt.feup.tvvs.tenebris.state.MenuState;
import pt.feup.tvvs.tenebris.state.StateChanger;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ArenaControllerWhiteBoxTests {

    private Arena arena;
    private ArenaController controller;
    private SaveDataProvider mockSaveProvider;
    private SaveData mockSaveData;

    @BeforeEach
    public void setup() throws IOException {
        arena = new Arena();
        controller = new ArenaController(arena);

        // Fix for NPE: Always provide a valid SaveData Mock
        mockSaveProvider = Mockito.mock(SaveDataProvider.class);
        mockSaveData = Mockito.mock(SaveData.class);
        when(mockSaveProvider.getSaveData()).thenReturn(mockSaveData);
        when(mockSaveData.getLevel()).thenReturn(1);
    }

    @Test
    public void testQuitActionTriggersStateChange() throws IOException, InterruptedException {
        StateChanger stateChanger = Mockito.mock(StateChanger.class);
        GUI mockGUI = Mockito.mock(GUI.class);

        try (MockedStatic<GUI> staticGUI = Mockito.mockStatic(GUI.class)) {
            staticGUI.when(GUI::getGUI).thenReturn(mockGUI);
            when(mockGUI.getAction()).thenReturn(Action.QUIT);
            when(mockGUI.getActiveActions()).thenReturn(new HashSet<>());

            controller.tick(stateChanger, mockSaveProvider);

            verify(stateChanger).setState(null);
        }
    }

    @Test
    public void testEscActionTriggersPauseMenu() throws IOException, InterruptedException {
        StateChanger stateChanger = Mockito.mock(StateChanger.class);
        GUI mockGUI = Mockito.mock(GUI.class);

        try (MockedStatic<GUI> staticGUI = Mockito.mockStatic(GUI.class)) {
            staticGUI.when(GUI::getGUI).thenReturn(mockGUI);
            when(mockGUI.getAction()).thenReturn(Action.ESC);
            when(mockGUI.getActiveActions()).thenReturn(new HashSet<>());

            controller.tick(stateChanger, mockSaveProvider);

            verify(stateChanger).setState(any(MenuState.class));
        }
    }

    @Test
    public void testCollision_DylanVsMonster_DamageAndPush() throws IOException {
        Vector2D pos = new Vector2D(100, 100);
        Dylan dylan = new Dylan(pos, 100, 1);
        TenebrisPeon monster = new TenebrisPeon(pos, 100, 1, 10, 100);

        arena.setDylan(dylan);
        arena.addElement(monster);

        try (MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class)) {
            sound.when(SoundManager::getInstance).thenReturn(Mockito.mock(SoundManager.class));

            controller.checkCollisions();

            assertEquals(90, dylan.getHp(), "Dylan should take 10 damage");
            assertNotNull(dylan.getAnimation(), "Dylan should bounce back (animation set)");
        }
    }

    @Test
    public void testCollision_ProjectileVsWall_Deletion() throws IOException {
        Vector2D pos = new Vector2D(100, 100);
        Bullet bullet = new Bullet(pos, Vector2D.Direction.RIGHT);
        Wall wall = new Wall(pos);

        arena.getProjectiles().add(bullet);
        arena.addElement(wall);

        ArenaController spyController = spy(controller);
        spyController.checkCollisions();

        verify(spyController).handleCommand(any(DeleteProjectile.class));
    }

    @Test
    public void testCollision_ProjectileVsMonster() throws IOException {
        Vector2D pos = new Vector2D(50, 50);
        TenebrisPeon monster = new TenebrisPeon(pos, 10, 1, 10, 100);
        Bullet bullet = new Bullet(pos, Vector2D.Direction.RIGHT);

        arena.addElement(monster);
        arena.getProjectiles().add(bullet);

        ArenaController spyController = spy(controller);

        try (MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class)) {
            sound.when(SoundManager::getInstance).thenReturn(Mockito.mock(SoundManager.class));
            spyController.checkCollisions();
        }

        verify(spyController).handleCommand(any(DeleteMonster.class));
        verify(spyController).handleCommand(any(DeleteProjectile.class));
    }

    @Test
    public void testCollision_EffectVsMonster() {
        Vector2D pos = new Vector2D(50, 50);
        TenebrisPeon monster = new TenebrisPeon(pos, 10, 1, 10, 100);
        Explosion explosion = new Explosion(pos, 20);

        arena.addElement(monster);
        arena.getEffects().add(explosion);

        ArenaController spyController = spy(controller);
        try (MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class)) {
            sound.when(SoundManager::getInstance).thenReturn(Mockito.mock(SoundManager.class));
            spyController.checkCollisions();
        }

        verify(spyController).handleCommand(any(DeleteMonster.class));
    }

    @Test
    public void testCommandProcessing_AllTypes() throws IOException, InterruptedException {
        Bullet bullet = new Bullet(new Vector2D(0,0), Vector2D.Direction.RIGHT);
        Explosion explosion = new Explosion(new Vector2D(0,0), 10);
        TenebrisPeon monster = new TenebrisPeon(new Vector2D(0,0), 10, 1, 1, 1);
        BreakableWall wall = new BreakableWall(new Vector2D(0,0), 10);

        arena.getProjectiles().add(bullet);
        arena.getEffects().add(explosion);
        arena.addElement(monster);
        arena.addElement(wall);

        // Queue commands
        controller.handleCommand(new DeleteProjectile(bullet));
        controller.handleCommand(new DeleteEffect(explosion));
        controller.handleCommand(new DeleteMonster(monster));
        controller.handleCommand(new DeleteBreakableWall(wall));
        controller.handleCommand(new CreateParticle(new Vector2D(0,0), ParticleType.DAMAGE_BLOOD));
        controller.handleCommand(new CreateProjectile(new Bullet(new Vector2D(10,10), Vector2D.Direction.LEFT)));
        controller.handleCommand(new CreateEffect(new Explosion(new Vector2D(20,20), 5)));
        controller.handleCommand(new ShakeCamera());
        controller.handleCommand(new KillDylan());

        try (MockedStatic<GUI> gui = Mockito.mockStatic(GUI.class)) {
            GUI mockGUI = Mockito.mock(GUI.class);
            gui.when(GUI::getGUI).thenReturn(mockGUI);
            when(mockGUI.getActiveActions()).thenReturn(Collections.emptySet());

            controller.tick(Mockito.mock(StateChanger.class), mockSaveProvider);
        }

        assertTrue(arena.getProjectiles().isEmpty() == false);
        assertTrue(arena.getEffects().isEmpty() == false);
        assertFalse(arena.getMonsters().contains(monster));
        assertFalse(arena.getElements().contains(wall));
        assertNull(arena.getDylan());
    }
}