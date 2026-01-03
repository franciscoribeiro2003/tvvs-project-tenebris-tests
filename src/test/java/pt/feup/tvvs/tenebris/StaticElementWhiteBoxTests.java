package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.tenebris.model.arena.static_elements.BreakableWall;
import pt.feup.tvvs.tenebris.model.arena.static_elements.Spike;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import static org.junit.jupiter.api.Assertions.*;

class StaticElementWhiteBoxTests {

    @Test
    void testSpikeDamage() {
        Spike spike = new Spike(new Vector2D(0,0), 50);
        assertEquals(50, spike.getEntityDamage());
    }

    @Test
    void testBreakableWallDegradation() {
        BreakableWall wall = new BreakableWall(new Vector2D(0,0), 30);

        assertTrue(wall.isAlive());

        wall.takeDamage(10);
        assertEquals(20, wall.getHp());
        assertTrue(wall.isAlive());

        wall.takeDamage(20);
        assertEquals(0, wall.getHp());
        assertFalse(wall.isAlive());
    }
}