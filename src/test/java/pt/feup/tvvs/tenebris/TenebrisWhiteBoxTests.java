package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.gui.GUI;
import pt.feup.tvvs.tenebris.savedata.SaveData;
import pt.feup.tvvs.tenebris.savedata.SaveDataManager;
import pt.feup.tvvs.tenebris.sound.SoundManager;
import pt.feup.tvvs.tenebris.state.State;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class TenebrisWhiteBoxTests {

    @Test
    public void testSingletonAndStateManagement() throws Exception {
        Field instanceField = Tenebris.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        try (MockedStatic<SaveDataManager> saveManager = Mockito.mockStatic(SaveDataManager.class);
             MockedStatic<SoundManager> soundManager = Mockito.mockStatic(SoundManager.class);
             MockedStatic<GUI> gui = Mockito.mockStatic(GUI.class)) {

            SaveDataManager mockSaveManager = mock(SaveDataManager.class);
            saveManager.when(SaveDataManager::getInstance).thenReturn(mockSaveManager);

            SoundManager mockSound = mock(SoundManager.class);
            soundManager.when(SoundManager::getInstance).thenReturn(mockSound);

            GUI mockGUI = mock(GUI.class);
            gui.when(GUI::getGUI).thenReturn(mockGUI);

            Tenebris game = Tenebris.getInstance();
            assertNotNull(game);

            SaveData save = mock(SaveData.class);
            game.setSaveData(save);
            assertEquals(save, game.getSaveData());

            State mockState = mock(State.class);
            game.setState(mockState);
            assertTrue(game.stateChanged());
        }
    }
}