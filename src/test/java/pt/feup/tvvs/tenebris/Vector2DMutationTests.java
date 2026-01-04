package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.tenebris.utils.Vector2D;
import static org.junit.jupiter.api.Assertions.*;

public class Vector2DMutationTests {

    @Test
    public void testGetMajorDirectionBoundaries() {
        Vector2D vRight = new Vector2D(10, 0);
        assertEquals(Vector2D.Direction.RIGHT, vRight.getMajorDirection());

        Vector2D vUp = new Vector2D(0, -10);
        assertEquals(Vector2D.Direction.UP, vUp.getMajorDirection());

        Vector2D vDownRight = new Vector2D(10, 10);
        assertEquals(Vector2D.Direction.DOWN_RIGHT, vDownRight.getMajorDirection());
    }

    @Test
    public void testDotProduct() {
        Vector2D v1 = new Vector2D(2, 3);
        Vector2D v2 = new Vector2D(4, 5);
        assertEquals(23, v1.dot(v2));
    }

    @Test
    public void testMultiplyDouble() {
        Vector2D v = new Vector2D(10, 20);
        Vector2D res = v.multiply(0.5);
        assertEquals(5, res.x());
        assertEquals(10, res.y());
    }
}