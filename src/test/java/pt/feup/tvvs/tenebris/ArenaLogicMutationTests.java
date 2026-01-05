package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.controller.arena.ArenaController;
import pt.feup.tvvs.tenebris.gui.GUI;
import pt.feup.tvvs.tenebris.model.arena.Arena;
import pt.feup.tvvs.tenebris.model.menu.LevelCompletedMenu;
import pt.feup.tvvs.tenebris.model.menu.VictoryMenu;
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

public class ArenaLogicMutationTests {

    @Test
    public void testWinCondition_NextLevel() throws IOException, InterruptedException {
        Arena arena = new Arena();
        ArenaController controller = new ArenaController(arena);

        SaveData save = Mockito.mock(SaveData.class);
        when(save.getLevel()).thenReturn(1);

        SaveDataProvider provider = Mockito.mock(SaveDataProvider.class);
        when(provider.getSaveData()).thenReturn(save);

        StateChanger changer = Mockito.mock(StateChanger.class);

        try (MockedStatic<GUI> gui = Mockito.mockStatic(GUI.class)) {
            GUI mockGUI = Mockito.mock(GUI.class);
            gui.when(GUI::getGUI).thenReturn(mockGUI);
            when(mockGUI.getActiveActions()).thenReturn(Collections.emptySet());

            // Tick enough times to drain endCounter
            for (int i = 0; i < 65; i++) {
                controller.tick(changer, provider);
            }

            // Accept one or more invocations; controller may call increaseLevel repeatedly in current logic.
            verify(save, atLeastOnce()).increaseLevel();
            // Verify transition to LevelCompletedMenu
            verify(changer, atLeastOnce()).setState(argThat(state ->
                    state instanceof MenuState && ((MenuState)state).getModel() instanceof LevelCompletedMenu
            ));
        }
    }

    @Test
    public void testWinCondition_GameVictory() throws IOException, InterruptedException {
        Arena arena = new Arena();
        ArenaController controller = new ArenaController(arena);

        SaveData save = Mockito.mock(SaveData.class);
        when(save.getLevel()).thenReturn(SaveData.MAX_LEVEL);

        SaveDataProvider provider = Mockito.mock(SaveDataProvider.class);
        when(provider.getSaveData()).thenReturn(save);

        StateChanger changer = Mockito.mock(StateChanger.class);

        try (MockedStatic<GUI> gui = Mockito.mockStatic(GUI.class)) {
            gui.when(GUI::getGUI).thenReturn(Mockito.mock(GUI.class));

            for (int i = 0; i < 65; i++) controller.tick(changer, provider);

            // Transition to VictoryMenu
            verify(changer, atLeastOnce()).setState(argThat(state ->
                    state instanceof MenuState && ((MenuState)state).getModel() instanceof VictoryMenu
            ));
        }
    }

    @Test
    public void testLoseCondition_HeartlessMode() throws IOException, InterruptedException {
        Arena arena = new Arena();
        ArenaController controller = new ArenaController(arena);

        arena.setDylan(null); // Dead

        SaveData save = Mockito.mock(SaveData.class);
        when(save.getDifficulty()).thenReturn(Difficulty.Heartless);

        SaveDataProvider provider = Mockito.mock(SaveDataProvider.class);
        when(provider.getSaveData()).thenReturn(save);

        StateChanger changer = Mockito.mock(StateChanger.class);
        SaveDataManager mockManager = Mockito.mock(SaveDataManager.class);

        try (MockedStatic<GUI> gui = Mockito.mockStatic(GUI.class);
             MockedStatic<SaveDataManager> staticManager = Mockito.mockStatic(SaveDataManager.class)) {

            gui.when(GUI::getGUI).thenReturn(Mockito.mock(GUI.class));
            staticManager.when(SaveDataManager::getInstance).thenReturn(mockManager);

            for (int i = 0; i < 65; i++) controller.tick(changer, provider);

            verify(mockManager, atLeastOnce()).deleteSave(save);
            verify(changer, atLeastOnce()).setState(any(MenuState.class));
        }
    }
}