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

class DylanWhiteBoxTests {

    @Test
    void testWeaponSwitching() {
        Dylan dylan = new Dylan(new Vector2D(0, 0), 100, 5);

        // Default is Pistol (index 0)
        assertTrue(dylan.getEquipedWeapon() instanceof Pistol);

        // Switch to Grenade Launcher (index 1)
        dylan.setSelectedWeapon(1);
        assertTrue(dylan.getEquipedWeapon() instanceof GrenadeLauncher);
    }

    @Test
    void testInvalidWeaponSelectionThrowsException() {
        Dylan dylan = new Dylan(new Vector2D(0, 0), 100, 5);
        assertThrows(RuntimeException.class, () -> dylan.setSelectedWeapon(5));
    }

    @Test
    void testMovementPriority() {
        Dylan dylan = new Dylan(new Vector2D(0, 0), 100, 5);
        Set<Dylan.State> moves = new TreeSet<>();

        // Add conflicting movements
        moves.add(Entity.State.LEFT);
        moves.add(Entity.State.RIGHT);
        dylan.setMoving(moves);

        // Logic dictates if both Left and Right are present, result is IDLE
        assertEquals(Entity.State.IDLE, dylan.getMoving());
    }
}