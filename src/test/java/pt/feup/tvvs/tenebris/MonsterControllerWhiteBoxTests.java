package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.controller.arena.monster.*;
import pt.feup.tvvs.tenebris.model.arena.Arena;
import pt.feup.tvvs.tenebris.model.arena._commands.CommandHandler;
import pt.feup.tvvs.tenebris.model.arena.entities.Dylan;
import pt.feup.tvvs.tenebris.model.arena.entities.Entity;
import pt.feup.tvvs.tenebris.model.arena.entities.monster.*;
import pt.feup.tvvs.tenebris.model.arena.static_elements.Wall;
import pt.feup.tvvs.tenebris.model.arena.static_elements.VisionBlocker;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class MonsterControllerWhiteBoxTests {

    @Test
    public void testPeonMovesTowardsDylan() throws IOException {
        Vector2D monsterPos = new Vector2D(100, 100);
        Vector2D dylanPos = new Vector2D(120, 100);

        TenebrisPeon peon = new TenebrisPeon(monsterPos, 50, 2, 10, 200);
        TenebrisPeonController controller = (TenebrisPeonController) peon.getController();

        Arena arena = new Arena();
        CommandHandler handler = Mockito.mock(CommandHandler.class);

        controller.update(dylanPos, arena, handler);

        assertEquals(Entity.State.RIGHT, peon.getMoving());
    }

    @Test
    public void testPeonStopsWhenDylanOutOfRange() throws IOException {
        Vector2D monsterPos = new Vector2D(100, 100);
        Vector2D dylanPos = new Vector2D(500, 500);

        TenebrisPeon peon = new TenebrisPeon(monsterPos, 50, 2, 10, 50);
        TenebrisPeonController controller = (TenebrisPeonController) peon.getController();

        Arena arena = new Arena();
        CommandHandler handler = Mockito.mock(CommandHandler.class);

        controller.update(dylanPos, arena, handler);

        assertEquals(Entity.State.IDLE, peon.getMoving());
    }

    @Test
    public void testPeonBlockedByWall() throws IOException {
        Vector2D monsterPos = new Vector2D(100, 100);
        Vector2D dylanPos = new Vector2D(120, 100);
        Vector2D wallPos = new Vector2D(110, 100);

        TenebrisPeon peon = new TenebrisPeon(monsterPos, 50, 2, 10, 200);
        TenebrisPeonController controller = (TenebrisPeonController) peon.getController();

        Arena arena = new Arena();
        arena.addElement(new Wall(wallPos));

        CommandHandler handler = Mockito.mock(CommandHandler.class);

        controller.update(dylanPos, arena, handler);

        assertEquals(Entity.State.IDLE, peon.getMoving());
    }

    @Test
    public void testHeavyMovesTowardsDylan() throws IOException {
        Vector2D monsterPos = new Vector2D(100, 100);
        Vector2D dylanPos = new Vector2D(100, 120);

        TenebrisHeavy heavy = new TenebrisHeavy(monsterPos, 100, 1, 20, 200);
        TenebrisHeavyController controller = (TenebrisHeavyController) heavy.getController();

        Arena arena = new Arena();
        CommandHandler handler = Mockito.mock(CommandHandler.class);

        controller.update(dylanPos, arena, handler);

        assertEquals(Entity.State.FRONT, heavy.getMoving());
    }

    @Test
    public void testHarbingerMovesWhenInVisionRange() throws IOException {
        // Arrange:  Harbinger has visionRange=250, shootingRange=150
        // Place Dylan at distance > shootingRange but < visionRange so monster moves toward Dylan
        Vector2D monsterPos = new Vector2D(50, 50);
        Vector2D dylanPos = new Vector2D(230, 50); // Distance = 180, which is > 150 (shootingRange) but < 250 (visionRange)

        TenebrisHarbinger harbinger = new TenebrisHarbinger(monsterPos, 40, 2, 35, 250, 150);
        TenebrisHarbingerController controller = (TenebrisHarbingerController) harbinger.getController();

        Arena arena = new Arena();
        arena.setDylan(new Dylan(dylanPos, 100, 3));
        CommandHandler mockHandler = Mockito.mock(CommandHandler.class);

        // Act
        controller.update(dylanPos, arena, mockHandler);

        // Assert:  Monster should move RIGHT toward Dylan (since Dylan is to the right)
        assertEquals(Entity.State.RIGHT, harbinger.getMoving());
    }

    @Test
    public void testSpikedScoutMovesTowardsDylan() throws IOException {
        Vector2D monsterPos = new Vector2D(100, 100);
        Vector2D dylanPos = new Vector2D(80, 100);

        TenebrisSpikedScout scout = new TenebrisSpikedScout(monsterPos, 30, 5, 15, 200);
        TenebrisSpikedScoutController controller = (TenebrisSpikedScoutController) scout.getController();

        Arena arena = new Arena();
        CommandHandler handler = Mockito.mock(CommandHandler.class);

        controller.update(dylanPos, arena, handler);

        assertEquals(Entity.State.LEFT, scout.getMoving());
    }

    @Test
    public void testWardenMovesTowardsDylan() throws IOException {
        Vector2D monsterPos = new Vector2D(100, 100);
        Vector2D dylanPos = new Vector2D(100, 80);

        TenebrisWarden warden = new TenebrisWarden(monsterPos, 100, 2, 45, 200);
        TenebrisWardenController controller = (TenebrisWardenController) warden.getController();

        Arena arena = new Arena();
        CommandHandler handler = Mockito.mock(CommandHandler.class);

        controller.update(dylanPos, arena, handler);

        assertEquals(Entity.State.BACK, warden.getMoving());
    }

    @Test
    public void testMonsterDiagonalMovement() throws IOException {
        Vector2D monsterPos = new Vector2D(100, 100);
        Vector2D dylanPos = new Vector2D(150, 150);

        TenebrisPeon peon = new TenebrisPeon(monsterPos, 50, 2, 10, 200);
        TenebrisPeonController controller = (TenebrisPeonController) peon.getController();

        Arena arena = new Arena();
        CommandHandler handler = Mockito.mock(CommandHandler.class);

        controller.update(dylanPos, arena, handler);

        assertNotEquals(Entity.State.IDLE, peon.getMoving());
    }

    @Test
    public void testVisionBlockerBlocksMonster() throws IOException {
        Vector2D monsterPos = new Vector2D(100, 100);
        Vector2D dylanPos = new Vector2D(150, 100);
        Vector2D blockerPos = new Vector2D(125, 100);

        TenebrisPeon peon = new TenebrisPeon(monsterPos, 50, 2, 10, 200);
        TenebrisPeonController controller = (TenebrisPeonController) peon.getController();

        Arena arena = new Arena();
        arena.addElement(new VisionBlocker(blockerPos));

        CommandHandler handler = Mockito.mock(CommandHandler.class);

        controller.update(dylanPos, arena, handler);

        assertEquals(Entity.State.IDLE, peon.getMoving());
    }

    @Test
    public void testMonsterMovesBackward() throws IOException {
        Vector2D monsterPos = new Vector2D(100, 100);
        Vector2D dylanPos = new Vector2D(100, 50);

        TenebrisPeon peon = new TenebrisPeon(monsterPos, 50, 2, 10, 200);
        TenebrisPeonController controller = (TenebrisPeonController) peon.getController();

        Arena arena = new Arena();
        CommandHandler handler = Mockito.mock(CommandHandler.class);

        controller.update(dylanPos, arena, handler);

        assertEquals(Entity.State.BACK, peon.getMoving());
    }

    @Test
    public void testMonsterMovesFront() throws IOException {
        Vector2D monsterPos = new Vector2D(100, 100);
        Vector2D dylanPos = new Vector2D(100, 150);

        TenebrisPeon peon = new TenebrisPeon(monsterPos, 50, 2, 10, 200);
        TenebrisPeonController controller = (TenebrisPeonController) peon.getController();

        Arena arena = new Arena();
        CommandHandler handler = Mockito.mock(CommandHandler.class);

        controller.update(dylanPos, arena, handler);

        assertEquals(Entity.State.FRONT, peon.getMoving());
    }
}