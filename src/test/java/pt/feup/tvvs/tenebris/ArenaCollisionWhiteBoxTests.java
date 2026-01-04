package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.controller.arena.ArenaController;
import pt.feup.tvvs.tenebris.model.arena.Arena;
import pt.feup.tvvs.tenebris.model.arena._commands.DeleteMonster;
import pt.feup.tvvs.tenebris.model.arena._commands.DeleteProjectile;
import pt.feup.tvvs.tenebris.model.arena.entities.Dylan;
import pt.feup.tvvs.tenebris.model.arena.entities.monster.TenebrisPeon;
import pt.feup.tvvs.tenebris.model.arena.projectiles.Bullet;
import pt.feup.tvvs.tenebris.model.arena.static_elements.Wall;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ArenaCollisionWhiteBoxTests {

    @Test
    public void testBulletKillsMonster() throws IOException {
        // Arrange
        Arena arena = new Arena();
        ArenaController controller = spy(new ArenaController(arena));

        Vector2D pos = new Vector2D(50, 50);
        TenebrisPeon monster = new TenebrisPeon(pos, 10, 1, 10, 100);
        Bullet bullet = new Bullet(pos, Vector2D.Direction.RIGHT);

        arena.addElement(monster);
        arena.getProjectiles().add(bullet);

        // Act
        controller.checkCollisions();

        // Assert
        verify(controller).handleCommand(any(DeleteMonster.class));
    }

    @Test
    public void testDylanTakesDamageFromMonster() throws IOException {
        Arena arena = new Arena();
        ArenaController controller = new ArenaController(arena);

        Vector2D pos = new Vector2D(50, 50);
        Dylan dylan = new Dylan(pos, 100, 5);
        TenebrisPeon monster = new TenebrisPeon(pos, 100, 1, 20, 100);

        arena.setDylan(dylan);
        arena.addElement(monster);

        assertEquals(100, dylan.getHp());

        // Act
        controller.checkCollisions();

        // Assert
        assertEquals(80, dylan.getHp(), "Dylan should take 20 damage from collision");
    }

    @Test
    public void testProjectileHitsWall() throws IOException {
        Arena arena = new Arena();
        ArenaController controller = spy(new ArenaController(arena));

        Vector2D pos = new Vector2D(10, 10);
        Wall wall = new Wall(pos);
        Bullet bullet = new Bullet(pos, Vector2D.Direction.RIGHT);

        arena.addElement(wall);
        arena.getProjectiles().add(bullet);

        // Act
        controller.checkCollisions();

        // Assert
        verify(controller).handleCommand(any(DeleteProjectile.class));
    }
}