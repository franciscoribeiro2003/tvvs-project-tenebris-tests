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

class TenebrisTests {

    @Test
    void testSingletonAndStateManagement() throws Exception {
        // 1. Reset Singleton via Reflection (Necessary because it might be initialized by other tests)
        Field instanceField = Tenebris.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        // 2. Mock static dependencies to prevent constructor crash
        try (MockedStatic<SaveDataManager> saveManager = Mockito.mockStatic(SaveDataManager.class);
             MockedStatic<SoundManager> soundManager = Mockito.mockStatic(SoundManager.class);
             MockedStatic<GUI> gui = Mockito.mockStatic(GUI.class)) {

            // Setup Mocks
            SaveDataManager mockSaveManager = mock(SaveDataManager.class);
            saveManager.when(SaveDataManager::getInstance).thenReturn(mockSaveManager);

            SoundManager mockSound = mock(SoundManager.class);
            soundManager.when(SoundManager::getInstance).thenReturn(mockSound);

            GUI mockGUI = mock(GUI.class);
            gui.when(GUI::getGUI).thenReturn(mockGUI);

            // 3. Act: Get Instance (Triggers constructor safely)
            Tenebris game = Tenebris.getInstance();
            assertNotNull(game);

            // 4. Test SaveData Provider methods
            SaveData save = mock(SaveData.class);
            game.setSaveData(save);
            assertEquals(save, game.getSaveData());

            // 5. Test State Changer methods
            State mockState = mock(State.class);
            game.setState(mockState);
            assertTrue(game.stateChanged());
        }
    }
}