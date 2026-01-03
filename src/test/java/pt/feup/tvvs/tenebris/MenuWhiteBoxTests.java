package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.controller.menu.*;
import pt.feup.tvvs.tenebris.gui.Action;
import pt.feup.tvvs.tenebris.gui.GUI;
import pt.feup.tvvs.tenebris.model.arena.Arena;
import pt.feup.tvvs.tenebris.model.arena.ArenaBuilder;
import pt.feup.tvvs.tenebris.model.menu.*;
import pt.feup.tvvs.tenebris.savedata.SaveData;
import pt.feup.tvvs.tenebris.savedata.SaveDataManager;
import pt.feup.tvvs.tenebris.savedata.SaveDataProvider;
import pt.feup.tvvs.tenebris.sound.SoundManager;
import pt.feup.tvvs.tenebris.state.ArenaState;
import pt.feup.tvvs.tenebris.state.MenuState;
import pt.feup.tvvs.tenebris.state.StateChanger;
import pt.feup.tvvs.tenebris.utils.Difficulty;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class MenuWhiteBoxTests {

    @Test
    void testFullMenuNavigationAndExecution() throws IOException, InterruptedException {
        SaveDataProvider provider = Mockito.mock(SaveDataProvider.class);
        SaveData save = Mockito.mock(SaveData.class);
        when(provider.getSaveData()).thenReturn(save);

        MainMenu menu = new MainMenu(provider);
        MainMenuController controller = new MainMenuController(menu);
        StateChanger changer = Mockito.mock(StateChanger.class);

        // Open mocks in try-with-resources
        try (MockedStatic<GUI> gui = Mockito.mockStatic(GUI.class);
             MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class);
             MockedStatic<SaveDataManager> manager = Mockito.mockStatic(SaveDataManager.class);
             MockedStatic<ArenaBuilder> builder = Mockito.mockStatic(ArenaBuilder.class)) { // Added ArenaBuilder mock

            // Setup GUI Mock
            GUI mockGUI = Mockito.mock(GUI.class);
            gui.when(GUI::getGUI).thenReturn(mockGUI);

            // Setup Sound Mock
            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            // Setup SaveData Mock
            SaveDataManager mockManager = Mockito.mock(SaveDataManager.class);
            manager.when(SaveDataManager::getInstance).thenReturn(mockManager);
            when(mockManager.getSaveCount()).thenReturn(1);

            // Setup ArenaBuilder Mock (Prevent file loading)
            Arena mockArena = Mockito.mock(Arena.class);
            builder.when(() -> ArenaBuilder.build(any())).thenReturn(mockArena);

            // --- Navigation ---
            when(mockGUI.getAction()).thenReturn(Action.LOOK_DOWN);
            controller.tick(changer, provider);
            assertEquals(1, menu.getSelectedOption());

            when(mockGUI.getAction()).thenReturn(Action.LOOK_UP);
            controller.tick(changer, provider);
            assertEquals(0, menu.getSelectedOption());

            // --- Execution ---
            // 1. New Game
            menu.setSelectedOption(0);
            when(mockGUI.getAction()).thenReturn(Action.EXEC);
            controller.tick(changer, provider);
            verify(changer, atLeastOnce()).setState(any(MenuState.class));

            // 2. Continue (Uses ArenaBuilder)
            menu.setSelectedOption(1);
            controller.tick(changer, provider);
            verify(changer, atLeastOnce()).setState(any(ArenaState.class));

            // 7. Exit (Force index to last)
            menu.setSelectedOption(menu.getOptions().size() - 1);
            when(mockGUI.getAction()).thenReturn(Action.EXEC);
            controller.tick(changer, provider);
            verify(changer, atLeastOnce()).setState(null);
        }
    }

    @Test
    void testLevelsMenuLogic() throws IOException, InterruptedException {
        SaveDataProvider provider = Mockito.mock(SaveDataProvider.class);
        SaveData save = Mockito.mock(SaveData.class);
        when(provider.getSaveData()).thenReturn(save);
        when(save.getLevel()).thenReturn(6);

        LevelsMenu menu = new LevelsMenu(provider);
        LevelsMenuController controller = new LevelsMenuController(menu);
        StateChanger changer = Mockito.mock(StateChanger.class);

        try (MockedStatic<GUI> gui = Mockito.mockStatic(GUI.class);
             MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class);
             MockedStatic<SaveDataManager> manager = Mockito.mockStatic(SaveDataManager.class);
             MockedStatic<ArenaBuilder> builder = Mockito.mockStatic(ArenaBuilder.class)) {

            GUI mockGUI = Mockito.mock(GUI.class);
            gui.when(GUI::getGUI).thenReturn(mockGUI);

            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            SaveDataManager mockManager = Mockito.mock(SaveDataManager.class);
            manager.when(SaveDataManager::getInstance).thenReturn(mockManager);
            when(mockManager.getLastOpen()).thenReturn(save);
            when(mockManager.createNewSave(any(), anyInt())).thenReturn(save);

            Arena mockArena = Mockito.mock(Arena.class);
            builder.when(() -> ArenaBuilder.build(any())).thenReturn(mockArena);

            // Test Horizontal Nav
            when(mockGUI.getAction()).thenReturn(Action.LOOK_RIGHT);
            controller.tick(changer, provider);
            assertEquals(1, menu.getSelectedOption());

            // Test Vertical Nav (+3)
            when(mockGUI.getAction()).thenReturn(Action.LOOK_DOWN);
            controller.tick(changer, provider); // 1 -> 4
            assertEquals(4, menu.getSelectedOption());

            // Execute (Uses ArenaBuilder)
            when(mockGUI.getAction()).thenReturn(Action.EXEC);
            controller.tick(changer, provider);
            verify(changer).setState(any(ArenaState.class));
        }
    }

    @Test
    void testPauseMenuLogic() throws IOException, InterruptedException {
        Arena arena = Mockito.mock(Arena.class);
        PauseMenu menu = new PauseMenu(arena);
        PauseMenuController controller = new PauseMenuController(menu);
        StateChanger changer = Mockito.mock(StateChanger.class);

        try (MockedStatic<GUI> gui = Mockito.mockStatic(GUI.class);
             MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class);
             MockedStatic<ArenaBuilder> builder = Mockito.mockStatic(ArenaBuilder.class)) {

            GUI mockGUI = Mockito.mock(GUI.class);
            gui.when(GUI::getGUI).thenReturn(mockGUI);

            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            Arena mockArena = Mockito.mock(Arena.class);
            builder.when(() -> ArenaBuilder.build(any())).thenReturn(mockArena);

            // Continue
            menu.setSelectedOption(0);
            when(mockGUI.getAction()).thenReturn(Action.EXEC);
            controller.tick(changer, null);
            verify(changer).setState(any(ArenaState.class));

            // Restart (requires save provider & ArenaBuilder)
            menu.setSelectedOption(1);
            SaveDataProvider provider = Mockito.mock(SaveDataProvider.class);
            when(provider.getSaveData()).thenReturn(Mockito.mock(SaveData.class));
            controller.tick(changer, provider);
            verify(changer, atLeastOnce()).setState(any(ArenaState.class));

            // Quit
            when(mockGUI.getAction()).thenReturn(Action.QUIT);
            controller.tick(changer, provider);
            verify(changer).setState(null);
        }
    }
}