package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.controller.menu.*;
import pt.feup.tvvs.tenebris.gui.Action;
import pt.feup.tvvs.tenebris.gui.GUI;
import pt.feup.tvvs.tenebris.model.menu.*;
import pt.feup.tvvs.tenebris.savedata.SaveDataProvider;
import pt.feup.tvvs.tenebris.sound.SoundManager;
import pt.feup.tvvs.tenebris.state.MenuState;
import pt.feup.tvvs.tenebris.state.StateChanger;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MenuControllerWhiteBoxTests {

    @Test
    public void testAllMenuNavigations() throws IOException, InterruptedException {
        SaveDataProvider provider = Mockito.mock(SaveDataProvider.class);
        StateChanger changer = Mockito.mock(StateChanger.class);
        GUI mockGUI = Mockito.mock(GUI.class);
        SoundManager mockSound = Mockito.mock(SoundManager.class);

        try (MockedStatic<GUI> guiStatic = Mockito.mockStatic(GUI.class);
             MockedStatic<SoundManager> soundStatic = Mockito.mockStatic(SoundManager.class)) {

            guiStatic.when(GUI::getGUI).thenReturn(mockGUI);
            soundStatic.when(SoundManager::getInstance).thenReturn(mockSound);

            // Test Main Menu Navigation - New Game
            MainMenu menu = new MainMenu(provider);
            MainMenuController mainController = new MainMenuController(menu);

            // 1. Boundary: Wrap UP (First -> Last)
            menu.setSelectedOption(0);
            when(mockGUI.getAction()).thenReturn(Action.LOOK_UP);
            mainController.tick(changer, provider);
            // FIX: Changed getNumberOptions() to getOptions().size()
            assertEquals(menu.getOptions().size() - 1, menu.getSelectedOption(), "Should wrap UP to last");

            // 2. Boundary: Wrap DOWN (Last -> First)
            when(mockGUI.getAction()).thenReturn(Action.LOOK_DOWN);
            mainController.tick(changer, provider);
            assertEquals(0, menu.getSelectedOption(), "Should wrap DOWN to first");

            // 3. Execution
            menu.setSelectedOption(0); // Ensure New Game selected
            when(mockGUI.getAction()).thenReturn(Action.EXEC);
            mainController.tick(changer, provider);

            verify(changer).setState(argThat(state ->
                    state instanceof MenuState && ((MenuState)state).getModel() instanceof NewGameMenu
            ));

            // Test HowToPlay - Exit logic
            HowToPlayMenu htp = new HowToPlayMenu();
            HowToPlayMenuController htpController = new HowToPlayMenuController(htp);
            when(mockGUI.getAction()).thenReturn(Action.ESC);
            htpController.tick(changer, provider);

            verify(changer).setState(argThat(state ->
                    state instanceof MenuState && ((MenuState)state).getModel() instanceof MainMenu
            ));

            // Test GameOver
            GameOverMenu gom = new GameOverMenu();
            GameOverMenuController gomController = new GameOverMenuController(gom);
            // Needs delay skip
            for(int i=0; i<20; i++) gomController.tick(changer, provider);

            verify(mockSound, org.mockito.Mockito.atLeastOnce()).playSFX(any());
        }
    }
}