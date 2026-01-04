package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.controller.menu.*;
import pt.feup.tvvs.tenebris.gui.Action;
import pt.feup.tvvs.tenebris.gui.GUI;
import pt.feup.tvvs.tenebris.model.menu.*;
import pt.feup.tvvs.tenebris.savedata.SaveData;
import pt.feup.tvvs.tenebris.savedata.SaveDataProvider;
import pt.feup.tvvs.tenebris.sound.SoundManager;
import pt.feup.tvvs.tenebris.state.StateChanger;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MenuControllerTests {

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

            // Test Main Menu Navigation
            MainMenu menu = new MainMenu(provider);
            MainMenuController mainController = new MainMenuController(menu);

            when(mockGUI.getAction()).thenReturn(Action.LOOK_DOWN);
            mainController.tick(changer, provider);

            when(mockGUI.getAction()).thenReturn(Action.LOOK_UP);
            mainController.tick(changer, provider);

            // Test HowToPlay
            HowToPlayMenu htp = new HowToPlayMenu();
            HowToPlayMenuController htpController = new HowToPlayMenuController(htp);
            when(mockGUI.getAction()).thenReturn(Action.ESC);
            htpController.tick(changer, provider);
            // Should go back to Main Menu (checked by state change, not mocked here deeply but code runs)

            // Test GameOver
            GameOverMenu gom = new GameOverMenu();
            GameOverMenuController gomController = new GameOverMenuController(gom);
            // Needs delay skip
            for(int i=0; i<20; i++) gomController.tick(changer, provider);

            verify(mockSound, org.mockito.Mockito.atLeastOnce()).playSFX(any());
        }
    }
}