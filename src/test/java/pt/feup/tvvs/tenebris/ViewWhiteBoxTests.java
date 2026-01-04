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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class ViewWhiteBoxTests {

    private GUI mockGUI;
    private GUI originalGUI;

    @BeforeEach
    public void setup() throws Exception {
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
    public void testEntityViews() {
        new DylanView(new Dylan(new Vector2D(0,0), 1, 1)).draw(new Vector2D(0,0));
        verify(mockGUI).drawDylan(any(), any());

        new TenebrisPeonView(new TenebrisPeon(new Vector2D(0,0), 1, 1, 1, 1)).draw(new Vector2D(0,0));
        verify(mockGUI).drawMonster(any(), eq(GUI.Monster.TENEBRIS_PEON), any());

        new TenebrisHeavyView(new TenebrisHeavy(new Vector2D(0,0), 1, 1, 1, 1)).draw(new Vector2D(0,0));
        verify(mockGUI).drawMonster(any(), eq(GUI.Monster.TENEBRIS_HEAVY), any());
    }

    @Test
    public void testProjectileViews() {
        new BulletView(new Bullet(new Vector2D(0,0), Vector2D.Direction.RIGHT)).draw(new Vector2D(0,0));
        verify(mockGUI).drawProjectile(any(), eq(GUI.Projectile.BULLET), any());
    }

    @Test
    public void testStaticElementViews() {
        new WallView(new Wall(new Vector2D(0,0))).draw(new Vector2D(0,0));
        verify(mockGUI).drawStaticElement(any(), eq(GUI.StaticElement.WALL));

        new BreakableWallView(new BreakableWall(new Vector2D(0,0), 1)).draw(new Vector2D(0,0));
        verify(mockGUI).drawStaticElement(any(), eq(GUI.StaticElement.BREAKABLE_WALL));
    }

    @Test
    public void testParticleViews() {
        new DamageBloodView(new DamageBlood(new Vector2D(0,0))).draw(new Vector2D(0,0));
        verify(mockGUI).drawParticleEffect(any(), eq(GUI.ParticleEffect.DAMAGE_BLOOD), any(Integer.class));
    }

    @Test
    public void testMenuViews() throws Exception {
        new MainMenuView(new MainMenu(Mockito.mock(SaveDataProvider.class))).draw();
        verify(mockGUI).drawMainMenu(any(), any(Integer.class));

        new CreditsMenuView(new CreditsMenu()).draw();
        verify(mockGUI).drawCreditsMenu();
    }

    @Test
    public void testWeaponViews() {
        new PistolView(new Pistol()).draw();
        verify(mockGUI).drawWeapon(eq(GUI.Weapon.PISTOL), any(Integer.class));
    }
}