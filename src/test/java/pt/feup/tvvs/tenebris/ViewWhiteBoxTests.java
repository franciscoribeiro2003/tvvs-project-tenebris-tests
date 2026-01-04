package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.gui.GUI;
import pt.feup.tvvs.tenebris.gui.LanternaGUI;
import pt.feup.tvvs.tenebris.model.arena.effects.Explosion;
import pt.feup.tvvs.tenebris.model.arena.entities.Dylan;
import pt.feup.tvvs.tenebris.model.arena.entities.monster.*;
import pt.feup.tvvs.tenebris.model.arena.particles.*;
import pt.feup.tvvs.tenebris.model.arena.projectiles.*;
import pt.feup.tvvs.tenebris.model.arena.static_elements.*;
import pt.feup.tvvs.tenebris.model.arena.weapons.*;
import pt.feup.tvvs.tenebris.model.menu.*;
import pt.feup.tvvs.tenebris.savedata.SaveDataProvider;
import pt.feup.tvvs.tenebris.utils.Vector2D;
import pt.feup.tvvs.tenebris.view.arena.effects.ExplosionView;
import pt.feup.tvvs.tenebris.view.arena.entity.DylanView;
import pt.feup.tvvs.tenebris.view.arena.entity.monster.*;
import pt.feup.tvvs.tenebris.view.arena.particles.*;
import pt.feup.tvvs.tenebris.view.arena.projectiles.*;
import pt.feup.tvvs.tenebris.view.arena.staticelement.*;
import pt.feup.tvvs.tenebris.view.arena.weapon.*;
import pt.feup.tvvs.tenebris.view.menu.*;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

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

    // --- Entity Views ---
    @Test
    public void testDylanView() {
        Dylan dylan = new Dylan(new Vector2D(0,0), 100, 1);
        new DylanView(dylan).draw(new Vector2D(0,0));
        verify(mockGUI).drawDylan(any(), any());
    }

    @Test
    public void testMonsterViews() {
        new TenebrisPeonView(new TenebrisPeon(new Vector2D(0,0), 1, 1, 1, 1)).draw(new Vector2D(0,0));
        verify(mockGUI).drawMonster(any(), eq(GUI.Monster.TENEBRIS_PEON), any());

        new TenebrisHeavyView(new TenebrisHeavy(new Vector2D(0,0), 1, 1, 1, 1)).draw(new Vector2D(0,0));
        verify(mockGUI).drawMonster(any(), eq(GUI.Monster.TENEBRIS_HEAVY), any());

        new TenebrisHarbingerView(new TenebrisHarbinger(new Vector2D(0,0), 1, 1, 1, 1, 1)).draw(new Vector2D(0,0));
        verify(mockGUI).drawMonster(any(), eq(GUI.Monster.TENEBRIS_HARBINGER), any());

        new TenebrisWardenView(new TenebrisWarden(new Vector2D(0,0), 1, 1, 1, 1)).draw(new Vector2D(0,0));
        verify(mockGUI).drawMonster(any(), eq(GUI.Monster.TENEBRIS_WARDEN), any());

        new TenebrisSpikedScoutView(new TenebrisSpikedScout(new Vector2D(0,0), 1, 1, 1, 1)).draw(new Vector2D(0,0));
        verify(mockGUI).drawMonster(any(), eq(GUI.Monster.TENEBRIS_SPIKED_SCOUT), any());
    }

    // --- Static Element Views ---
    @Test
    public void testStaticElementViews() {
        new WallView(new Wall(new Vector2D(0,0))).draw(new Vector2D(0,0));
        verify(mockGUI).drawStaticElement(any(), eq(GUI.StaticElement.WALL));

        new BreakableWallView(new BreakableWall(new Vector2D(0,0), 10)).draw(new Vector2D(0,0));
        verify(mockGUI).drawStaticElement(any(), eq(GUI.StaticElement.BREAKABLE_WALL));

        new SandbagView(new SandBag(new Vector2D(0,0))).draw(new Vector2D(0,0));
        verify(mockGUI).drawStaticElement(any(), eq(GUI.StaticElement.SANDBAG));

        new SpikeView(new Spike(new Vector2D(0,0), 10)).draw(new Vector2D(0,0));
        verify(mockGUI).drawStaticElement(any(), eq(GUI.StaticElement.SPIKE));

        new VisionBlockerView(new VisionBlocker(new Vector2D(0,0))).draw(new Vector2D(0,0));
        // VisionBlocker typically draws nothing or debug, check implementation, usually empty
    }

    // --- Projectile Views ---
    @Test
    public void testProjectileViews() {
        new BulletView(new Bullet(new Vector2D(0,0), Vector2D.Direction.RIGHT)).draw(new Vector2D(0,0));
        verify(mockGUI).drawProjectile(any(), eq(GUI.Projectile.BULLET), any());

        new ExplosiveBulletView(new ExplosiveBullet(new Vector2D(0,0), Vector2D.Direction.RIGHT)).draw(new Vector2D(0,0));
        verify(mockGUI).drawProjectile(any(), eq(GUI.Projectile.EXPLOSIVE), any());

        new SpellView(new Spell(new Vector2D(0,0), Vector2D.Direction.RIGHT, 10)).draw(new Vector2D(0,0));
        verify(mockGUI).drawProjectile(any(), eq(GUI.Projectile.SPELL), any());
    }

    // --- Particle Views ---
    @Test
    public void testParticleViews() {
        new DamageBloodView(new DamageBlood(new Vector2D(0,0))).draw(new Vector2D(0,0));
        verify(mockGUI).drawParticleEffect(any(), eq(GUI.ParticleEffect.DAMAGE_BLOOD), any(Integer.class));

        new DeathBloodView(new DeathBlood(new Vector2D(0,0))).draw(new Vector2D(0,0));
        verify(mockGUI).drawParticleEffect(any(), eq(GUI.ParticleEffect.DEATH_BLOOD), any(Integer.class));

        new SpellExplosionView(new SpellExplosion(new Vector2D(0,0))).draw(new Vector2D(0,0));
        verify(mockGUI).drawParticleEffect(any(), eq(GUI.ParticleEffect.SPELL_EXPLOSION), any(Integer.class));

        new BreakableWallDamageView(new BreakableWallDamage(new Vector2D(0,0))).draw(new Vector2D(0,0));
        verify(mockGUI).drawParticleEffect(any(), eq(GUI.ParticleEffect.BREAKABLE_WALL_DAMAGE), any(Integer.class));
    }

    // --- Weapon Views ---
    @Test
    public void testWeaponViews() {
        new PistolView(new Pistol()).draw();
        verify(mockGUI).drawWeapon(eq(GUI.Weapon.PISTOL), any(Integer.class));

        new GrenadeLauncherView(new GrenadeLauncher()).draw();
        verify(mockGUI).drawWeapon(eq(GUI.Weapon.GRENADE_LAUNCHER), any(Integer.class));
    }

    // --- Menu Views ---
    @Test
    public void testMenuViews() throws Exception {
        new MainMenuView(new MainMenu(Mockito.mock(SaveDataProvider.class))).draw();
        verify(mockGUI).drawMainMenu(any(), any(Integer.class));

        new NewGameMenuView(new NewGameMenu()).draw();
        verify(mockGUI).drawNewGameMenu(any(), any(Integer.class));

        new CreditsMenuView(new CreditsMenu()).draw();
        verify(mockGUI).drawCreditsMenu();

        new VictoryMenuView(new VictoryMenu()).draw();
        verify(mockGUI).drawVictoryMenu();

        new GameOverMenuView(new GameOverMenu()).draw();
        verify(mockGUI).drawGameOverMenu();

        new DeathMenuView(new DeathMenu()).draw();
        verify(mockGUI).drawDeathMenu(any(), any(Integer.class));

        new LevelCompletedMenuView(new LevelCompletedMenu()).draw();
        verify(mockGUI).drawLevelCompletedMenu(any(), any(Integer.class));

        new HowToPlayMenuView(new HowToPlayMenu()).draw();
        verify(mockGUI).drawHowToPlayMenu(any(Integer.class));
    }

    @Test
    public void testEffectView() {
        new ExplosionView(new Explosion(new Vector2D(0,0), 10)).draw(new Vector2D(0,0));
        verify(mockGUI).drawParticleEffect(any(), eq(GUI.ParticleEffect.EXPLOSION), any(Integer.class));
    }
}