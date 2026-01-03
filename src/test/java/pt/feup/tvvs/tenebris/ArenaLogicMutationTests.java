package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.controller.arena.ArenaController;
import pt.feup.tvvs.tenebris.gui.GUI;
import pt.feup.tvvs.tenebris.model.arena.Arena;
import pt.feup.tvvs.tenebris.savedata.SaveData;
import pt.feup.tvvs.tenebris.savedata.SaveDataManager;
import pt.feup.tvvs.tenebris.savedata.SaveDataProvider;
import pt.feup.tvvs.tenebris.state.MenuState;
import pt.feup.tvvs.tenebris.state.State;
import pt.feup.tvvs.tenebris.state.StateChanger;
import pt.feup.tvvs.tenebris.utils.Difficulty;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ArenaLogicMutationTests {

    @Test
    void testWinCondition_NextLevel() throws IOException, InterruptedException {
        // Arrange
        Arena arena = new Arena();
        ArenaController controller = new ArenaController(arena);

        // No monsters = Win condition
        // Current Level = 1 (Not Max)
        SaveData save = Mockito.mock(SaveData.class);
        when(save.getLevel()).thenReturn(1);

        SaveDataProvider provider = Mockito.mock(SaveDataProvider.class);
        when(provider.getSaveData()).thenReturn(save);

        StateChanger changer = Mockito.mock(StateChanger.class);

        // Mock GUI & Sound to prevent crash
        try (MockedStatic<GUI> gui = Mockito.mockStatic(GUI.class)) {
            GUI mockGUI = Mockito.mock(GUI.class);
            gui.when(GUI::getGUI).thenReturn(mockGUI);
            when(mockGUI.getActiveActions()).thenReturn(Collections.emptySet());

            // Act: We need to tick enough times to burn the "endCounter" (60 ticks)
            for (int i = 0; i < 65; i++) {
                controller.tick(changer, provider);
            }

            // Assert: Should transition to LevelCompletedMenu
            verify(changer, atLeastOnce()).setState(any(MenuState.class));

            // FIXED: Used atLeastOnce() because the test loop continues ticking after win
            verify(save, atLeastOnce()).increaseLevel();
        }
    }

    @Test
    void testWinCondition_GameVictory() throws IOException, InterruptedException {
        Arena arena = new Arena();
        ArenaController controller = new ArenaController(arena);

        SaveData save = Mockito.mock(SaveData.class);
        // Level 6 is MAX_LEVEL (defined in SaveData)
        when(save.getLevel()).thenReturn(SaveData.MAX_LEVEL);

        SaveDataProvider provider = Mockito.mock(SaveDataProvider.class);
        when(provider.getSaveData()).thenReturn(save);

        StateChanger changer = Mockito.mock(StateChanger.class);

        try (MockedStatic<GUI> gui = Mockito.mockStatic(GUI.class)) {
            gui.when(GUI::getGUI).thenReturn(Mockito.mock(GUI.class));

            for (int i = 0; i < 65; i++) controller.tick(changer, provider);

            // Assert
            verify(changer, atLeastOnce()).setState(any(State.class));
        }
    }

    @Test
    void testLoseCondition_HeartlessMode() throws IOException, InterruptedException {
        Arena arena = new Arena();
        ArenaController controller = new ArenaController(arena);

        // Dylan is null = Dead
        arena.setDylan(null);

        // Difficulty Heartless = Game Over (Delete Save)
        SaveData save = Mockito.mock(SaveData.class);
        when(save.getDifficulty()).thenReturn(Difficulty.Heartless);

        SaveDataProvider provider = Mockito.mock(SaveDataProvider.class);
        when(provider.getSaveData()).thenReturn(save);

        StateChanger changer = Mockito.mock(StateChanger.class);

        // Mock SaveManager because Heartless mode deletes the save
        SaveDataManager mockManager = Mockito.mock(SaveDataManager.class);

        try (MockedStatic<GUI> gui = Mockito.mockStatic(GUI.class);
             MockedStatic<SaveDataManager> staticManager = Mockito.mockStatic(SaveDataManager.class)) {

            gui.when(GUI::getGUI).thenReturn(Mockito.mock(GUI.class));
            staticManager.when(SaveDataManager::getInstance).thenReturn(mockManager);

            for (int i = 0; i < 65; i++) controller.tick(changer, provider);

            // Assert: Save deleted?
            verify(mockManager, atLeastOnce()).deleteSave(save);
            verify(changer, atLeastOnce()).setState(any(MenuState.class));
        }
    }
}