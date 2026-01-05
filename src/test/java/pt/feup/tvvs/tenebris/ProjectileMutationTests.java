package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.model.arena.projectiles.Bullet;
import pt.feup.tvvs.tenebris.model.arena.projectiles.ExplosiveBullet;
import pt.feup.tvvs.tenebris.model.arena.projectiles.Spell;
import pt.feup.tvvs.tenebris.model.arena.static_elements.Wall;
import pt.feup.tvvs.tenebris.model.arena._commands.*;
import pt.feup.tvvs.tenebris.sound.SoundManager;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectileMutationTests {

    @Test
    public void testBulletMovesRight() {
        Bullet bullet = new Bullet(new Vector2D(0, 0), Vector2D.Direction.RIGHT);
        bullet.update();
        assertEquals(8, bullet.getPosition().x());
        assertEquals(0, bullet.getPosition().y());
    }

    @Test
    public void testBulletMovesLeft() {
        Bullet bullet = new Bullet(new Vector2D(100, 0), Vector2D.Direction.LEFT);
        bullet.update();
        assertEquals(92, bullet.getPosition().x());
    }

    @Test
    public void testBulletMovesUp() {
        Bullet bullet = new Bullet(new Vector2D(0, 100), Vector2D.Direction.UP);
        bullet.update();
        assertEquals(92, bullet.getPosition().y());
    }

    @Test
    public void testBulletMovesDown() {
        Bullet bullet = new Bullet(new Vector2D(0, 0), Vector2D.Direction.DOWN);
        bullet.update();
        assertEquals(8, bullet.getPosition().y());
    }

    @Test
    public void testBulletMovesDiagonalUpRight() {
        Bullet bullet = new Bullet(new Vector2D(0, 100), Vector2D.Direction.UP_RIGHT);
        bullet.update();
        assertEquals(8, bullet.getPosition().x());
        assertEquals(92, bullet.getPosition().y());
    }

    @Test
    public void testBulletMovesDiagonalDownLeft() {
        Bullet bullet = new Bullet(new Vector2D(100, 0), Vector2D.Direction.DOWN_LEFT);
        bullet.update();
        assertEquals(92, bullet.getPosition().x());
        assertEquals(8, bullet.getPosition().y());
    }

    @Test
    public void testBulletDamage() {
        Bullet bullet = new Bullet(new Vector2D(0, 0), Vector2D.Direction.RIGHT);
        assertEquals(10, bullet.getEntityDamage());
    }

    @Test
    public void testBulletDeletedOnWallCollision() {
        Bullet bullet = new Bullet(new Vector2D(0, 0), Vector2D.Direction.RIGHT);
        Wall wall = new Wall(new Vector2D(0, 0));

        List<Command> commands = bullet.interact(wall);

        assertTrue(commands.stream().anyMatch(c -> c instanceof DeleteProjectile));
    }

    @Test
    public void testExplosiveBulletCreatesExplosionOnImpact() {
        try (MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class)) {
            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            ExplosiveBullet bullet = new ExplosiveBullet(new Vector2D(50, 50), Vector2D.Direction.RIGHT);
            Wall wall = new Wall(new Vector2D(50, 50));

            List<Command> commands = bullet.interact(wall);

            assertTrue(commands.stream().anyMatch(c -> c instanceof CreateEffect));
            assertTrue(commands.stream().anyMatch(c -> c instanceof ShakeCamera));
        }
    }

    @Test
    public void testSpellCreatesParticleOnImpact() {
        try (MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class)) {
            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            Spell spell = new Spell(new Vector2D(50, 50), Vector2D.Direction.RIGHT, 10);
            Wall wall = new Wall(new Vector2D(50, 50));

            List<Command> commands = spell.interact(wall);

            assertTrue(commands.stream().anyMatch(c -> c instanceof CreateParticle));
        }
    }

    @Test
    public void testSpellDamageValue() {
        Spell spell = new Spell(new Vector2D(0, 0), Vector2D.Direction.RIGHT, 15);
        assertEquals(15, spell.getEntityDamage());
    }
}