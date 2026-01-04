package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.tenebris.gui.GUI;
import pt.feup.tvvs.tenebris.model.arena.effects.Explosion;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import static org.junit.jupiter.api.Assertions.*;

public class EffectMutationTests {

    @Test
    public void testExplosionDamageDecaysOverTime() {
        Explosion explosion = new Explosion(new Vector2D(0, 0), 50);

        int initialDamage = explosion.getEntityDamage();
        explosion.update();
        int damageAfterUpdate = explosion.getEntityDamage();

        assertTrue(damageAfterUpdate < initialDamage, "Damage should decay over time");
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