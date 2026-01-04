package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.model.arena._commands.Command;
import pt.feup.tvvs.tenebris.model.arena._commands.DeleteMonster;
import pt.feup.tvvs.tenebris.model.arena.entities.monster.TenebrisPeon;
import pt.feup.tvvs.tenebris.model.arena.particles.DamageBlood;
import pt.feup.tvvs.tenebris.model.arena.projectiles.Bullet;
import pt.feup.tvvs.tenebris.model.arena.static_elements.Wall;
import pt.feup.tvvs.tenebris.sound.SoundManager;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EntityWhiteBoxTests {

    @BeforeEach
    public void setup() {
    }

    @Test
    public void testMonsterDiesAndReturnsDeleteCommand() {
        Vector2D pos = new Vector2D(10, 10);
        TenebrisPeon peon = new TenebrisPeon(pos, 20, 1, 10, 50);
        SoundManager mockSound = Mockito.mock(SoundManager.class);

        try (MockedStatic<SoundManager> staticSound = Mockito.mockStatic(SoundManager.class)) {
            staticSound.when(SoundManager::getInstance).thenReturn(mockSound);

            peon.takeDamage(20);
            assertFalse(peon.isAlive());

            List<Command> commands = peon.interact(new Wall(new Vector2D(0,0)));
            assertTrue(commands.stream().anyMatch(c -> c instanceof DeleteMonster));
        }
    }

    @Test
    public void testProjectileMovement() {
        Vector2D start = new Vector2D(0, 0);
        Bullet bullet = new Bullet(start, Vector2D.Direction.RIGHT);

        bullet.update();
        assertEquals(8, bullet.getPosition().x());
        assertEquals(0, bullet.getPosition().y());

        bullet.update();
        assertEquals(16, bullet.getPosition().x());
    }

    @Test
    public void testParticleLifecycle() {
        DamageBlood particle = new DamageBlood(new Vector2D(0,0));
        assertEquals(1, particle.getCurrentFrame());
        assertFalse(particle.isOver());

        for (int i = 0; i < 5; i++) {
            particle.update();
        }
        assertTrue(particle.isOver());
    }
}