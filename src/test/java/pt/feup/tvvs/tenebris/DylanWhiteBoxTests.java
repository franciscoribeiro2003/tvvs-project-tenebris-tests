package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.tenebris.model.arena.entities.Dylan;
import pt.feup.tvvs.tenebris.model.arena.entities.Entity;
import pt.feup.tvvs.tenebris.model.arena.weapons.GrenadeLauncher;
import pt.feup.tvvs.tenebris.model.arena.weapons.Pistol;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public class DylanWhiteBoxTests {

    @Test
    public void testWeaponSwitching() {
        Dylan dylan = new Dylan(new Vector2D(0, 0), 100, 5);
        assertTrue(dylan.getEquipedWeapon() instanceof Pistol);

        dylan.setSelectedWeapon(1);
        assertTrue(dylan.getEquipedWeapon() instanceof GrenadeLauncher);
    }

    @Test
    public void testInvalidWeaponSelectionThrowsException() {
        Dylan dylan = new Dylan(new Vector2D(0, 0), 100, 5);
        assertThrows(RuntimeException.class, () -> dylan.setSelectedWeapon(5));
    }

    @Test
    public void testMovementPriority() {
        Dylan dylan = new Dylan(new Vector2D(0, 0), 100, 5);
        Set<Dylan.State> moves = new TreeSet<>();

        moves.add(Entity.State.LEFT);
        moves.add(Entity.State.RIGHT);
        dylan.setMoving(moves);

        assertEquals(Entity.State.IDLE, dylan.getMoving());
    }
}