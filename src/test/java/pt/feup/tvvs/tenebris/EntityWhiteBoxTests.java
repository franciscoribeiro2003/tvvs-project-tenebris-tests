package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.gui.GUI;
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
import static org.mockito.ArgumentMatchers.any;

class EntityWhiteBoxTests {

    @BeforeEach
    void setup() {
        // We don't need explicit setup here as we mock statics inside try-resources
    }

    @Test
    void testMonsterDiesAndReturnsDeleteCommand() {
        // Arrange
        Vector2D pos = new Vector2D(10, 10);
        TenebrisPeon peon = new TenebrisPeon(pos, 20, 1, 10, 50);

        // Mock SoundManager to prevent audio errors
        SoundManager mockSound = Mockito.mock(SoundManager.class);

        try (MockedStatic<SoundManager> staticSound = Mockito.mockStatic(SoundManager.class)) {
            staticSound.when(SoundManager::getInstance).thenReturn(mockSound);

            // Act: Deal lethal damage (20 HP - 20 Damage = 0)
            peon.takeDamage(20);
            assertFalse(peon.isAlive());

            // Interact with a dummy wall to trigger command generation
            List<Command> commands = peon.interact(new Wall(new Vector2D(0,0)));

            // Assert
            assertTrue(commands.stream().anyMatch(c -> c instanceof DeleteMonster));
        }
    }

    @Test
    void testProjectileMovement() {
        // Arrange
        Vector2D start = new Vector2D(0, 0);
        // Bullet velocity is usually 8 (defined in Bullet.java)
        Bullet bullet = new Bullet(start, Vector2D.Direction.RIGHT);

        // Act
        bullet.update();

        // Assert: Position should change by velocity (8, 0)
        assertEquals(8, bullet.getPosition().x());
        assertEquals(0, bullet.getPosition().y());

        // Update again
        bullet.update();
        assertEquals(16, bullet.getPosition().x());
    }

    @Test
    void testParticleLifecycle() {
        DamageBlood particle = new DamageBlood(new Vector2D(0,0));

        // DamageBlood has 5 frames (defined in GUI constant)
        // Check initial
        assertEquals(1, particle.getCurrentFrame());
        assertFalse(particle.isOver());

        // Update 5 times
        for (int i = 0; i < 5; i++) {
            particle.update();
        }

        // Should be > 5 now (frame 6)
        assertTrue(particle.isOver());
    }
}