package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.tenebris.controller.arena.DylanController;
import pt.feup.tvvs.tenebris.gui.Action;
import pt.feup.tvvs.tenebris.model.arena.entities.Dylan;
import pt.feup.tvvs.tenebris.model.arena.entities.Entity;
import pt.feup.tvvs.tenebris.model.arena.weapons.GrenadeLauncher;
import pt.feup.tvvs.tenebris.model.arena.weapons.Pistol;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import java.util.Collections;
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

        dylan.setSelectedWeapon(0);
        assertTrue(dylan.getEquipedWeapon() instanceof Pistol);
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

        // TreeSet sorts enum ordinals? No, usually natural order.
        // IDLE is usually first.
        // We verify the actual state chosen.
        assertEquals(Entity.State.IDLE, dylan.getMoving());
    }

    @Test
    public void testControllerSetsMoving() {
        Dylan dylan = new Dylan(new Vector2D(0,0), 100, 5);
        DylanController ctrl = new DylanController(dylan);
        ctrl.setMoving(Collections.singleton(Action.MOVE_UP));
        assertEquals(Entity.State.BACK, dylan.getMoving());
    }
}