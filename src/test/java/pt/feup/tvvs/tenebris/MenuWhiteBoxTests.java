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

public class MenuWhiteBoxTests {

    private void verifyArenaTransition(StateChanger changer) throws IOException {
        verify(changer, atLeastOnce()).setState(any(ArenaState.class));
    }

    @Test
    public void testFullMenuNavigationAndExecution() throws IOException, InterruptedException {
        SaveDataProvider provider = Mockito.mock(SaveDataProvider.class);
        SaveData save = Mockito.mock(SaveData.class);
        when(provider.getSaveData()).thenReturn(save);

        MainMenu menu = new MainMenu(provider);
        MainMenuController controller = new MainMenuController(menu);
        StateChanger changer = Mockito.mock(StateChanger.class);

        // Open mocks
        try (MockedStatic<GUI> gui = Mockito.mockStatic(GUI.class);
             MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class);
             MockedStatic<SaveDataManager> manager = Mockito.mockStatic(SaveDataManager.class);
             MockedStatic<ArenaBuilder> builder = Mockito.mockStatic(ArenaBuilder.class)) {

            // GUI Mock
            GUI mockGUI = Mockito.mock(GUI.class);
            gui.when(GUI::getGUI).thenReturn(mockGUI);

            // Sound Mock
            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            // SaveData Mock
            SaveDataManager mockManager = Mockito.mock(SaveDataManager.class);
            manager.when(SaveDataManager::getInstance).thenReturn(mockManager);
            when(mockManager.getSaveCount()).thenReturn(1);
            when(mockManager.createNewSave(any())).thenReturn(save);

            // ArenaBuilder Mock
            Arena mockArena = Mockito.mock(Arena.class);
            builder.when(() -> ArenaBuilder.build(any(SaveData.class))).thenReturn(mockArena);

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

            // 2. Continue
            menu.setSelectedOption(1);
            controller.tick(changer, provider);
            verifyArenaTransition(changer);

            // 7. Exit
            menu.setSelectedOption(menu.getOptions().size() - 1);
            controller.tick(changer, provider);
            verify(changer, atLeastOnce()).setState(null);
        }
    }

    @Test
    public void testLevelsMenuLogic() throws IOException, InterruptedException {
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
            builder.when(() -> ArenaBuilder.build(any(SaveData.class))).thenReturn(mockArena);

            // Test Horizontal Nav
            when(mockGUI.getAction()).thenReturn(Action.LOOK_RIGHT);
            controller.tick(changer, provider);
            assertEquals(1, menu.getSelectedOption());

            // Test Vertical Nav (+3)
            when(mockGUI.getAction()).thenReturn(Action.LOOK_DOWN);
            controller.tick(changer, provider);
            assertEquals(4, menu.getSelectedOption());

            // Execute
            when(mockGUI.getAction()).thenReturn(Action.EXEC);
            controller.tick(changer, provider);
            verifyArenaTransition(changer);
        }
    }

    @Test
    public void testPauseMenuLogic() throws IOException, InterruptedException {
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
            builder.when(() -> ArenaBuilder.build(any(SaveData.class))).thenReturn(mockArena);

            // Continue
            menu.setSelectedOption(0);
            when(mockGUI.getAction()).thenReturn(Action.EXEC);
            controller.tick(changer, null);
            verifyArenaTransition(changer);

            // Restart
            menu.setSelectedOption(1);
            SaveDataProvider provider = Mockito.mock(SaveDataProvider.class);
            when(provider.getSaveData()).thenReturn(Mockito.mock(SaveData.class));

            when(mockGUI.getAction()).thenReturn(Action.EXEC);
            controller.tick(changer, provider);
            verifyArenaTransition(changer);

            // Quit
            when(mockGUI.getAction()).thenReturn(Action.QUIT);
            controller.tick(changer, provider);
            verify(changer).setState(null);
        }
    }

    @Test
    public void testNewGameMenuLogic() throws IOException, InterruptedException {
        NewGameMenu menu = new NewGameMenu();
        NewGameMenuController controller = new NewGameMenuController(menu);
        StateChanger changer = Mockito.mock(StateChanger.class);
        SaveDataProvider provider = Mockito.mock(SaveDataProvider.class);

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
            when(mockManager.createNewSave(any(Difficulty.class))).thenReturn(Mockito.mock(SaveData.class));

            Arena mockArena = Mockito.mock(Arena.class);
            builder.when(() -> ArenaBuilder.build(any(SaveData.class))).thenReturn(mockArena);

            // Select Difficulty
            menu.setSelectedOption(0);
            when(mockGUI.getAction()).thenReturn(Action.EXEC);
            controller.tick(changer, provider);

            verifyArenaTransition(changer);
        }
    }

    @Test
    public void testDeathMenuLogic() throws IOException, InterruptedException {
        DeathMenu menu = new DeathMenu();
        DeathMenuController controller = new DeathMenuController(menu);
        StateChanger changer = Mockito.mock(StateChanger.class);
        SaveDataProvider provider = Mockito.mock(SaveDataProvider.class);
        when(provider.getSaveData()).thenReturn(Mockito.mock(SaveData.class));

        try (MockedStatic<GUI> gui = Mockito.mockStatic(GUI.class);
             MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class);
             MockedStatic<ArenaBuilder> builder = Mockito.mockStatic(ArenaBuilder.class)) {

            GUI mockGUI = Mockito.mock(GUI.class);
            gui.when(GUI::getGUI).thenReturn(mockGUI);

            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            Arena mockArena = Mockito.mock(Arena.class);
            builder.when(() -> ArenaBuilder.build(any(SaveData.class))).thenReturn(mockArena);

            // Select Retry (Index 0)
            menu.setSelectedOption(0);
            when(mockGUI.getAction()).thenReturn(Action.EXEC);

            // Loop to bypass delay
            for(int i=0; i<20; i++) controller.tick(changer, provider);

            verifyArenaTransition(changer);
        }
    }

    @Test
    public void testCreditsAndQuit() throws IOException, InterruptedException {
        CreditsMenu menu = new CreditsMenu();
        CreditsMenuController controller = new CreditsMenuController(menu);
        StateChanger changer = Mockito.mock(StateChanger.class);

        try (MockedStatic<GUI> gui = Mockito.mockStatic(GUI.class);
             MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class);
             MockedStatic<ArenaBuilder> builder = Mockito.mockStatic(ArenaBuilder.class)) {

            GUI mockGUI = Mockito.mock(GUI.class);
            gui.when(GUI::getGUI).thenReturn(mockGUI);

            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            when(mockGUI.getAction()).thenReturn(Action.QUIT);
            controller.tick(changer, null);
            verify(changer).setState(null);
        }
    }

    @Test
    public void testLevelCompletedMenu() throws IOException, InterruptedException {
        LevelCompletedMenu menu = new LevelCompletedMenu();
        LevelCompletedMenuController controller = new LevelCompletedMenuController(menu);
        StateChanger changer = Mockito.mock(StateChanger.class);
        SaveDataProvider provider = Mockito.mock(SaveDataProvider.class);
        when(provider.getSaveData()).thenReturn(Mockito.mock(SaveData.class));

        try (MockedStatic<GUI> gui = Mockito.mockStatic(GUI.class);
             MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class);
             MockedStatic<ArenaBuilder> builder = Mockito.mockStatic(ArenaBuilder.class)) {

            GUI mockGUI = Mockito.mock(GUI.class);
            gui.when(GUI::getGUI).thenReturn(mockGUI);

            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            Arena mockArena = Mockito.mock(Arena.class);
            builder.when(() -> ArenaBuilder.build(any(SaveData.class))).thenReturn(mockArena);

            // Next Level (Option 0)
            menu.setSelectedOption(0);
            when(mockGUI.getAction()).thenReturn(Action.EXEC);

            for(int i=0; i<20; i++) controller.tick(changer, provider);

            verifyArenaTransition(changer);
        }
    }

    @Test
    public void testLoadGameMenu() throws IOException, InterruptedException {
        LoadGameMenu menu = new LoadGameMenu();
        LoadGameMenuController controller = new LoadGameMenuController(menu);
        StateChanger changer = Mockito.mock(StateChanger.class);
        SaveDataProvider provider = Mockito.mock(SaveDataProvider.class);

        try (MockedStatic<GUI> gui = Mockito.mockStatic(GUI.class);
             MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class);
             MockedStatic<SaveDataManager> manager = Mockito.mockStatic(SaveDataManager.class);
             MockedStatic<ArenaBuilder> builder = Mockito.mockStatic(ArenaBuilder.class)) {

            GUI mockGUI = Mockito.mock(GUI.class);
            gui.when(GUI::getGUI).thenReturn(mockGUI);

            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            Arena mockArena = Mockito.mock(Arena.class);
            builder.when(() -> ArenaBuilder.build(any(SaveData.class))).thenReturn(mockArena);

            SaveDataManager mockManager = Mockito.mock(SaveDataManager.class);
            manager.when(SaveDataManager::getInstance).thenReturn(mockManager);
            // Simulate 1 save
            when(mockManager.getSaveCount()).thenReturn(1);
            when(mockManager.getSave(anyInt())).thenReturn(Mockito.mock(SaveData.class));

            // Select Load (Option 0)
            when(mockGUI.getAction()).thenReturn(Action.EXEC);
            controller.tick(changer, provider);

            verifyArenaTransition(changer);
        }
    }
}