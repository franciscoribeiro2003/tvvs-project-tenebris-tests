package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.controller.arena.ArenaController;
import pt.feup.tvvs.tenebris.gui.Action;
import pt.feup.tvvs.tenebris.gui.GUI;
import pt.feup.tvvs.tenebris.model.arena.Arena;
import pt.feup.tvvs.tenebris.savedata.SaveData;
import pt.feup.tvvs.tenebris.savedata.SaveDataProvider;
import pt.feup.tvvs.tenebris.state.StateChanger;
import pt.feup.tvvs.tenebris.utils.Difficulty;

import java.io.IOException;
import java.util.HashSet;

class ArenaControllerWhiteBoxTests {

    @Test
    void testQuitActionTriggersStateChange() throws IOException, InterruptedException {
        // Arrange
        Arena arena = new Arena();
        ArenaController controller = new ArenaController(arena);

        StateChanger stateChanger = Mockito.mock(StateChanger.class);
        SaveDataProvider saveProvider = Mockito.mock(SaveDataProvider.class);
        GUI mockGUI = Mockito.mock(GUI.class);

        // PREVENT NULL POINTER: Create a dummy SaveData
        // The constructor is protected, so we mock the class instead
        SaveData dummySave = Mockito.mock(SaveData.class);
        Mockito.when(saveProvider.getSaveData()).thenReturn(dummySave);
        // Stub level to avoid null issues if accessed
        Mockito.when(dummySave.getLevel()).thenReturn(1);

        // Mock Static GUI
        try (MockedStatic<GUI> staticGUI = Mockito.mockStatic(GUI.class)) {
            staticGUI.when(GUI::getGUI).thenReturn(mockGUI);

            // Mock Input
            Mockito.when(mockGUI.getAction()).thenReturn(Action.QUIT);
            Mockito.when(mockGUI.getActiveActions()).thenReturn(new HashSet<>());

            // Act
            controller.tick(stateChanger, saveProvider);

            // Assert
            Mockito.verify(stateChanger).setState(null);
        }
    }
}