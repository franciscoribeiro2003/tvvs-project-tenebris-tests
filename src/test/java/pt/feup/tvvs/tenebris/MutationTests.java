package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.model.arena.entities.Dylan;
import pt.feup.tvvs.tenebris.model.arena.entities.Entity;
import pt.feup.tvvs.tenebris.model.arena.weapons.GrenadeLauncher;
import pt.feup.tvvs.tenebris.model.arena.weapons.Pistol;
import pt.feup.tvvs.tenebris.sound.SoundManager;
import pt.feup.tvvs.tenebris.utils.HitBox;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public class MutationTests {

    @Test
    public void testHitBoxExactBoundary() {
        Vector2D p1 = new Vector2D(0, 0);
        HitBox b1 = new HitBox(new Vector2D(0, 0), new Vector2D(10, 10));

        Vector2D p2 = new Vector2D(10, 10);
        HitBox b2 = new HitBox(new Vector2D(0, 0), new Vector2D(10, 10));

        assertTrue(HitBox.collide(p1, b1, p2, b2));

        Vector2D p3 = new Vector2D(11, 11);
        assertFalse(HitBox.collide(p1, b1, p3, b2));
    }

    @Test
    public void testHitBoxNoCollision() {
        Vector2D p1 = new Vector2D(0, 0);
        HitBox b1 = new HitBox(new Vector2D(0, 0), new Vector2D(10, 10));

        Vector2D p2 = new Vector2D(100, 100);
        HitBox b2 = new HitBox(new Vector2D(0, 0), new Vector2D(10, 10));

        assertFalse(HitBox.collide(p1, b1, p2, b2));
    }

    @Test
    public void testHitBoxCollisionLeftEdge() {
        Vector2D p1 = new Vector2D(10, 0);
        HitBox b1 = new HitBox(new Vector2D(0, 0), new Vector2D(10, 10));

        Vector2D p2 = new Vector2D(0, 0);
        HitBox b2 = new HitBox(new Vector2D(0, 0), new Vector2D(10, 10));

        assertTrue(HitBox.collide(p1, b1, p2, b2));
    }

    @Test
    public void testHitBoxCollisionTopEdge() {
        Vector2D p1 = new Vector2D(0, 10);
        HitBox b1 = new HitBox(new Vector2D(0, 0), new Vector2D(10, 10));

        Vector2D p2 = new Vector2D(0, 0);
        HitBox b2 = new HitBox(new Vector2D(0, 0), new Vector2D(10, 10));

        assertTrue(HitBox.collide(p1, b1, p2, b2));
    }

    @Test
    public void testPistolAmmoCount() {
        try (MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class)) {
            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            Pistol pistol = new Pistol();
            int initialAmmo = pistol.getAmmoCount();
            assertEquals(10, initialAmmo);

            // Wait for cooldown and shoot
            for (int i = 0; i < 10; i++) pistol.tickWeaponTimer();
            if (pistol.canShoot()) {
                pistol.shot();
            }

            assertEquals(initialAmmo - 1, pistol.getAmmoCount());
        }
    }

    @Test
    public void testPistolReloadRestoresAmmo() {
        try (MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class)) {
            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            Pistol pistol = new Pistol();

            for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 10; j++) pistol.tickWeaponTimer();
                if (pistol.canShoot()) pistol.shot();
            }

            pistol.startReload();
            for (int i = 0; i < 50; i++) pistol.tickWeaponTimer();
            pistol.reload();

            assertEquals(10, pistol.getAmmoCount());
        }
    }

    @Test
    public void testGrenadeLauncherAmmo() {
        try (MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class)) {
            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            GrenadeLauncher gl = new GrenadeLauncher();
            assertEquals(1, gl.getAmmoCount());

            if (gl.canShoot()) {
                gl.shot();
            }

            assertEquals(0, gl.getAmmoCount());
        }
    }

    @Test
    public void testDylanMovementStates() {
        Dylan dylan = new Dylan(new Vector2D(0, 0), 100, 5);

        Set<Entity.State> moves = new TreeSet<>();
        moves.add(Entity.State.FRONT);
        moves.add(Entity.State.BACK);
        dylan.setMoving(moves);

        assertEquals(Entity.State.IDLE, dylan.getMoving());
    }

    @Test
    public void testDylanTakeDamage() {
        try (MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class)) {
            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            Dylan dylan = new Dylan(new Vector2D(0, 0), 100, 5);
            dylan.takeDamage(30);
            assertEquals(70, dylan.getHp());
        }
    }

    @Test
    public void testDylanDies() {
        try (MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class)) {
            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            Dylan dylan = new Dylan(new Vector2D(0, 0), 100, 5);
            dylan.takeDamage(100);
            assertFalse(dylan.isAlive());
        }
    }

    @Test
    public void testDylanOverkill() {
        try (MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class)) {
            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            Dylan dylan = new Dylan(new Vector2D(0, 0), 100, 5);
            dylan.takeDamage(200);
            assertEquals(0, dylan.getHp());
            assertFalse(dylan.isAlive());
        }
    }

    @Test
    public void testVector2DMajorDirectionBoundaries() {
        assertEquals(Vector2D.Direction.RIGHT, new Vector2D(100, 0).getMajorDirection());
        assertEquals(Vector2D.Direction.LEFT, new Vector2D(-100, 0).getMajorDirection());
        assertEquals(Vector2D.Direction.DOWN, new Vector2D(0, 100).getMajorDirection());
        assertEquals(Vector2D.Direction.UP, new Vector2D(0, -100).getMajorDirection());
        assertEquals(Vector2D.Direction.DOWN_RIGHT, new Vector2D(100, 100).getMajorDirection());
        assertEquals(Vector2D.Direction.DOWN_LEFT, new Vector2D(-100, 100).getMajorDirection());
        assertEquals(Vector2D.Direction.UP_RIGHT, new Vector2D(100, -100).getMajorDirection());
        assertEquals(Vector2D.Direction.UP_LEFT, new Vector2D(-100, -100).getMajorDirection());
    }
}