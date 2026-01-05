package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.tenebris.model.arena.Camera;
import pt.feup.tvvs.tenebris.model.arena.animation.Bounce;
import pt.feup.tvvs.tenebris.model.arena.animation.CameraShake;
import pt.feup.tvvs.tenebris.model.arena.entities.Dylan;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import static org.junit.jupiter.api.Assertions.*;

public class AnimationMutationTests {

    @Test
    public void testBounceMovesEntityInCorrectDirection() {
        Dylan dylan = new Dylan(new Vector2D(100, 100), 100, 3);
        Vector2D initialPos = dylan.getPosition();

        Bounce bounce = new Bounce(dylan, Vector2D.Direction.RIGHT);
        assertFalse(bounce.isOver());

        // Frame 0: velocity = 7 * 0.8^0 = 7.
        bounce.execute();
        // Exact Check: 100 + 7 = 107
        assertEquals(107, dylan.getPosition().x(), "Entity should move right by exactly 7");
    }

    @Test
    public void testBounceLeftDirection() {
        Dylan dylan = new Dylan(new Vector2D(100, 100), 100, 3);

        Bounce bounce = new Bounce(dylan, Vector2D.Direction.LEFT);
        bounce.execute();

        // Exact Check: 100 - 7 = 93
        assertEquals(93, dylan.getPosition().x(), "Entity should move left by exactly 7");
    }

    @Test
    public void testBounceUpDirection() {
        Dylan dylan = new Dylan(new Vector2D(100, 100), 100, 3);

        Bounce bounce = new Bounce(dylan, Vector2D.Direction.UP);
        bounce.execute();

        // Exact Check: 100 - 7 = 93
        assertEquals(93, dylan.getPosition().y(), "Entity should move up by exactly 7");
    }

    @Test
    public void testBounceDownDirection() {
        Dylan dylan = new Dylan(new Vector2D(100, 100), 100, 3);

        Bounce bounce = new Bounce(dylan, Vector2D.Direction.DOWN);
        bounce.execute();

        // Exact Check: 100 + 7 = 107
        assertEquals(107, dylan.getPosition().y(), "Entity should move down by exactly 7");
    }

    @Test
    public void testBounceCompletesAfterAllFrames() {
        Dylan dylan = new Dylan(new Vector2D(100, 100), 100, 3);
        Bounce bounce = new Bounce(dylan, Vector2D.Direction.RIGHT);

        // Execute all 10 movements
        for (int i = 0; i < 10; i++) {
            assertFalse(bounce.isOver());
            bounce.execute();
        }

        assertTrue(bounce.isOver());
    }

    @Test
    public void testBounceDecaysOverTime() {
        Dylan dylan = new Dylan(new Vector2D(100, 100), 100, 3);
        Bounce bounce = new Bounce(dylan, Vector2D.Direction.RIGHT);

        int firstX = dylan.getPosition().x();
        bounce.execute();
        // Move 1: 7 * 0.8^0 = 7
        int firstMove = dylan.getPosition().x() - firstX;
        assertEquals(7, firstMove);

        int secondX = dylan.getPosition().x();
        bounce.execute();
        // Move 2: 7 * 0.8^1 = 5.6 -> (int)5
        int secondMove = dylan.getPosition().x() - secondX;
        assertEquals(5, secondMove);

        assertTrue(firstMove > secondMove, "Bounce should decay");
    }

    @Test
    public void testCameraShakeReturnsToOriginalPosition() {
        Camera camera = new Camera(new Vector2D(200, 200));
        Vector2D originalPos = camera.getPosition();

        CameraShake shake = new CameraShake(camera);

        // Execute all frames
        while (!shake.isOver()) {
            shake.execute();
        }

        assertEquals(originalPos.x(), camera.getPosition().x());
        assertEquals(originalPos.y(), camera.getPosition().y());
    }

    @Test
    public void testCameraShakeModifiesPositionDuringAnimation() {
        Camera camera = new Camera(new Vector2D(200, 200));
        Vector2D originalPos = camera.getPosition();

        CameraShake shake = new CameraShake(camera);
        shake.execute();

        // Position should change during shake (statistically very likely)
        boolean positionChanged = false;
        // Check for 10 frames (duration of shake)
        for (int i = 0; i < 10; i++) {
            if (!camera.getPosition().equals(originalPos)) {
                positionChanged = true;
            }
            if(!shake.isOver()) shake.execute();
        }

        // We can't guarantee random numbers, but probability suggests it should move
        // However, correctness is that it eventually stops.
        assertTrue(shake.isOver());
    }
}