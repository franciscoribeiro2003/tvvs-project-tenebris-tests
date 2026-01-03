package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.tenebris.model.arena.Camera;
import pt.feup.tvvs.tenebris.model.arena.entities.monster.*;
import pt.feup.tvvs.tenebris.model.arena.static_elements.*;
import pt.feup.tvvs.tenebris.model.arena.weapons.*;
import pt.feup.tvvs.tenebris.utils.HitBox;
import pt.feup.tvvs.tenebris.utils.Pair;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import static org.junit.jupiter.api.Assertions.*;

class ModelCoverageTests {

    @Test
    void testMonsterConstructorsAndGetters() {
        Vector2D p = new Vector2D(0,0);

        TenebrisPeon peon = new TenebrisPeon(p, 10, 1, 5, 50);
        assertEquals(50, peon.getVisionRange());
        assertEquals(5, peon.getPlayerDamage());

        TenebrisHarbinger harb = new TenebrisHarbinger(p, 10, 1, 5, 50, 100);
        assertEquals(100, harb.getShootingRange());
        assertEquals(30, harb.getShootingCoolDown()); // default const
    }

    @Test
    void testUtils() {
        // HitBox
        HitBox hb = new HitBox(new Vector2D(0,0), new Vector2D(10,10));
        assertNotNull(hb.first);
        assertNotNull(hb.second);

        // Pair
        Pair<Integer> pair = new Pair<>(1, 2);
        assertEquals(1, pair.first);
        assertEquals(2, pair.second);
    }

    @Test
    void testCamera() {
        Camera cam = new Camera(new Vector2D(10,10));
        assertEquals(10, cam.getPosition().x());
        // Interaction
        assertTrue(cam.interact(new Wall(new Vector2D(0,0))).isEmpty());
    }

    @Test
    void testWeaponReloadLogic() {
        Pistol pistol = new Pistol();
        assertTrue(pistol.isLoaded());

        // Ensure weapon can shoot before shooting
        // Weapons might have a cooldown on start or require a tick
        // We simulate ticks until ready
        for(int i=0; i<100; i++) {
            if(pistol.canShoot()) break;
            pistol.tickWeaponTimer();
        }

        if (pistol.canShoot()) {
            pistol.shot();
        }

        pistol.startReload();
        assertTrue(pistol.isReloading());

        // Simulate timer tick
        for(int i=0; i<50; i++) pistol.tickWeaponTimer();
        pistol.reload();
        assertFalse(pistol.isReloading());
    }
}