package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.controller.arena.ArenaController;
import pt.feup.tvvs.tenebris.gui.GUI;
import pt.feup.tvvs.tenebris.model.arena.Arena;
import pt.feup.tvvs.tenebris.model.arena._commands.*;
import pt.feup.tvvs.tenebris.model.arena.entities.Dylan;
import pt.feup.tvvs.tenebris.model.arena.entities.monster.TenebrisPeon;
import pt.feup.tvvs.tenebris.model.arena.particles.ParticleType;
import pt.feup.tvvs.tenebris.model.arena.projectiles.Bullet;
import pt.feup.tvvs.tenebris.savedata.SaveData;
import pt.feup.tvvs.tenebris.savedata.SaveDataProvider;
import pt.feup.tvvs.tenebris.sound.SoundManager;
import pt.feup.tvvs.tenebris.state.StateChanger;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ArenaControllerDeepTests {

    private Arena arena;
    private ArenaController controller;

    @BeforeEach
    public void setup() throws IOException {
        arena = new Arena();
        controller = new ArenaController(arena);
    }

    @Test
    public void testCommandProcessing_CreateAndDeleteAllTypes() {
        // We create a dummy loop to simulate processing these commands inside the controller
        // Since triggerCommands is private, we invoke it via tick() or by spying checkCollisions logic
        // Best approach: Add commands manually then trigger a tick

        // Mock GUI to prevent nulls during tick
        try (MockedStatic<GUI> gui = Mockito.mockStatic(GUI.class)) {
            GUI mockGUI = Mockito.mock(GUI.class);
            gui.when(GUI::getGUI).thenReturn(mockGUI);
            when(mockGUI.getActiveActions()).thenReturn(Collections.emptySet());

            // Inject Commands via handleCommand
            Bullet bullet = new Bullet(new Vector2D(0,0), Vector2D.Direction.RIGHT);
            controller.handleCommand(new CreateProjectile(bullet));
            controller.handleCommand(new CreateParticle(new Vector2D(0,0), ParticleType.DAMAGE_BLOOD));

            // Trigger update to process commands
            controller.tick(Mockito.mock(StateChanger.class), Mockito.mock(SaveDataProvider.class));

            // Verify items added
            assertFalse(arena.getProjectiles().isEmpty());
            assertFalse(arena.getParticles().isEmpty());

            // Now Delete them
            controller.handleCommand(new DeleteProjectile(bullet));
            controller.handleCommand(new DeleteParticle(arena.getParticles().get(0)));

            controller.tick(Mockito.mock(StateChanger.class), Mockito.mock(SaveDataProvider.class));

            assertTrue(arena.getProjectiles().isEmpty());
            assertTrue(arena.getParticles().isEmpty());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCollision_MonsterVsDylan() throws IOException {
        Vector2D pos = new Vector2D(100, 100);
        Dylan dylan = new Dylan(pos, 100, 1);
        TenebrisPeon monster = new TenebrisPeon(pos, 100, 1, 10, 100);

        arena.setDylan(dylan);
        arena.addElement(monster);

        // Mock Sound to avoid NPE
        try (MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class)) {
            sound.when(SoundManager::getInstance).thenReturn(Mockito.mock(SoundManager.class));

            controller.checkCollisions();

            // Dylan should have taken damage (100 - 10 = 90)
            assertEquals(90, dylan.getHp());
        }
    }

    @Test
    public void testCollision_MonsterVsMonster() throws IOException {
        Vector2D pos = new Vector2D(100, 100);
        TenebrisPeon m1 = new TenebrisPeon(pos, 100, 1, 10, 100);
        TenebrisPeon m2 = new TenebrisPeon(pos, 100, 1, 10, 100);

        arena.addElement(m1);
        arena.addElement(m2);

        // They should push each other but no damage logic is usually defined for monster-monster
        // We verify that collision logic ran without error
        controller.checkCollisions();
    }

    @Test
    public void testLevelCompletion() throws Exception {
        // Setup empty arena (no monsters)
        // Mock GUI
        try (MockedStatic<GUI> gui = Mockito.mockStatic(GUI.class);
             MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class)) {

            GUI mockGUI = Mockito.mock(GUI.class);
            gui.when(GUI::getGUI).thenReturn(mockGUI);
            sound.when(SoundManager::getInstance).thenReturn(Mockito.mock(SoundManager.class));

            SaveDataProvider provider = Mockito.mock(SaveDataProvider.class);
            SaveData data = Mockito.mock(SaveData.class);
            when(provider.getSaveData()).thenReturn(data);
            when(data.getLevel()).thenReturn(1); // Not max level

            StateChanger changer = Mockito.mock(StateChanger.class);

            // Tick 60 times to trigger end counter
            for(int i=0; i<65; i++) controller.tick(changer, provider);

            verify(data).increaseLevel();
        }
    }
}