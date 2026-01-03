package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.tenebris.utils.HitBox;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MutationTests {

    @Test
    void testHitBoxExactBoundary() {
        // Many mutants survive on "Off by one" errors in collision detection.
        // We test EXACT boundaries to kill them.

        // Box 1: 0,0 to 10,10
        Vector2D p1 = new Vector2D(0,0);
        HitBox b1 = new HitBox(new Vector2D(0,0), new Vector2D(10,10));

        // Box 2: Exactly touching Box 1 at 10,10
        Vector2D p2 = new Vector2D(10,10);
        HitBox b2 = new HitBox(new Vector2D(0,0), new Vector2D(10,10));

        // Should collide (Edges touch)
        assertTrue(HitBox.collide(p1, b1, p2, b2));

        // Box 3: Just 1 pixel away at 11,11
        Vector2D p3 = new Vector2D(11,11);
        // Box 1 (0 to 10) vs Box 3 (11 to 21) -> No collision
        assertFalse(HitBox.collide(p1, b1, p3, b2));
    }
}