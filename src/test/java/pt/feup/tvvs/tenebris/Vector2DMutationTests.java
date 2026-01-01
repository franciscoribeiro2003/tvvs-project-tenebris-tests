package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.tenebris.utils.Vector2D;
import static org.junit.jupiter.api.Assertions.*;

class Vector2DMutationTests {

    @Test
    void testGetMajorDirectionBoundaries() {
        // Horizontal
        Vector2D vRight = new Vector2D(10, 0);
        assertEquals(Vector2D.Direction.RIGHT, vRight.getMajorDirection());

        // Vertical (Negative Y is UP in screen coords)
        Vector2D vUp = new Vector2D(0, -10);
        assertEquals(Vector2D.Direction.UP, vUp.getMajorDirection());

        // Diagonal (Positive X = Right, Positive Y = Down)
        Vector2D vDownRight = new Vector2D(10, 10);
        assertEquals(Vector2D.Direction.DOWN_RIGHT, vDownRight.getMajorDirection());
    }
}