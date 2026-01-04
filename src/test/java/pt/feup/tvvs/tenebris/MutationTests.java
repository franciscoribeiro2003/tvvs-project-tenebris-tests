package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.tenebris.utils.HitBox;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MutationTests {

    @Test
    public void testHitBoxExactBoundary() {
        Vector2D p1 = new Vector2D(0,0);
        HitBox b1 = new HitBox(new Vector2D(0,0), new Vector2D(10,10));

        Vector2D p2 = new Vector2D(10,10);
        HitBox b2 = new HitBox(new Vector2D(0,0), new Vector2D(10,10));

        assertTrue(HitBox.collide(p1, b1, p2, b2));

        Vector2D p3 = new Vector2D(11,11);
        assertFalse(HitBox.collide(p1, b1, p3, b2));
    }
}