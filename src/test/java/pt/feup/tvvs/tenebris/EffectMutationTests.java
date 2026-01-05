package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.tenebris.gui.GUI;
import pt.feup.tvvs.tenebris.model.arena.effects.Explosion;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import static org.junit.jupiter.api.Assertions.*;

public class EffectMutationTests {

    @Test
    public void testExplosionDamageDecaysOverTime() {
        int initialInputDamage = 50;
        // Constructor sets damage = 50 * 2 = 100
        Explosion explosion = new Explosion(new Vector2D(0, 0), initialInputDamage);

        assertEquals(100, explosion.getEntityDamage());

        explosion.update();
        // Math.pow(100, 0.8) = 39.81... -> cast to int = 39
        assertEquals(39, explosion.getEntityDamage(), "Damage should decay to exactly 39");

        explosion.update();
        // Math.pow(39, 0.8) = 18.78... -> cast to int = 18
        assertEquals(18, explosion.getEntityDamage(), "Damage should decay to exactly 18");
    }

    @Test
    public void testExplosionIsOverAfterAllFrames() {
        Explosion explosion = new Explosion(new Vector2D(0, 0), 10);

        assertFalse(explosion.isOver());

        for (int i = 0; i < GUI.EXPLOSION_FRAME_COUNT; i++) {
            explosion.update();
        }

        assertTrue(explosion.isOver());
    }

    @Test
    public void testExplosionNotOverDuringAnimation() {
        Explosion explosion = new Explosion(new Vector2D(0, 0), 10);

        for (int i = 0; i < GUI.EXPLOSION_FRAME_COUNT - 1; i++) {
            assertFalse(explosion.isOver());
            explosion.update();
        }
    }

    @Test
    public void testExplosionCurrentFrameIncrements() {
        Explosion explosion = new Explosion(new Vector2D(0, 0), 10);

        assertEquals(1, explosion.getCurrentFrame());
        explosion.update();
        assertEquals(2, explosion.getCurrentFrame());
        explosion.update();
        assertEquals(3, explosion.getCurrentFrame());
    }

    @Test
    public void testExplosionInitialDamageIsDoubled() {
        int inputDamage = 25;
        Explosion explosion = new Explosion(new Vector2D(0, 0), inputDamage);

        // Initial damage should be 2x input (before first decay)
        assertEquals(inputDamage * 2, explosion.getEntityDamage());
    }
}