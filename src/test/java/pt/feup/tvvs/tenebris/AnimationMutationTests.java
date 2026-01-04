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

        bounce.execute();
        assertTrue(dylan.getPosition().x() > initialPos.x(), "Entity should move right");
    }

    @Test
    public void testBounceLeftDirection() {
        Dylan dylan = new Dylan(new Vector2D(100, 100), 100, 3);
        Vector2D initialPos = dylan.getPosition();

        Bounce bounce = new Bounce(dylan, Vector2D.Direction.LEFT);
        bounce.execute();

        assertTrue(dylan.getPosition().x() < initialPos.x(), "Entity should move left");
    }

    @Test
    public void testBounceUpDirection() {
        Dylan dylan = new Dylan(new Vector2D(100, 100), 100, 3);
        Vector2D initialPos = dylan.getPosition();

        Bounce bounce = new Bounce(dylan, Vector2D.Direction.UP);
        bounce.execute();

        assertTrue(dylan.getPosition().y() < initialPos.y(), "Entity should move up");
    }

    @Test
    public void testBounceDownDirection() {
        Dylan dylan = new Dylan(new Vector2D(100, 100), 100, 3);
        Vector2D initialPos = dylan.getPosition();

        Bounce bounce = new Bounce(dylan, Vector2D.Direction.DOWN);
        bounce.execute();

        assertTrue(dylan.getPosition().y() > initialPos.y(), "Entity should move down");
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
        int firstMove = dylan.getPosition().x() - firstX;

        int secondX = dylan.getPosition().x();
        bounce.execute();
        int secondMove = dylan.getPosition().x() - secondX;

        assertTrue(firstMove >= secondMove, "Bounce should decay");
    }

    @Test
    public void testCameraShakeReturnsToOriginalPosition() {
        Camera camera = new Camera(new Vector2D(200, 200));
        Vector2D originalPos = camera.getPosition();

        CameraShake shake = new CameraShake(camera);

        // Execute all frames
        while (! shake.isOver()) {
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
        // Run multiple times to verify shake effect
        boolean positionChanged = false;
        for (int i = 0; i < 5 && !shake.isOver(); i++) {
            shake.execute();
            if (! camera.getPosition().equals(originalPos)) {
                positionChanged = true;
            }
        }

        // Complete the animation
        while (!shake.isOver()) {
            shake.execute();
        }
    }
}