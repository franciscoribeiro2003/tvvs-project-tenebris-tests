package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.tenebris.savedata.SaveData;
import pt.feup.tvvs.tenebris.savedata.SaveDataManager;
import pt.feup.tvvs.tenebris.utils.Difficulty;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SaveDataTests {

    @Test
    public void testSaveDataFlow() throws IOException {
        File tempFile = File.createTempFile("test_save", ".csv");
        tempFile.deleteOnExit();

        SaveDataManager manager = SaveDataManager.getInstance();

        SaveData save = manager.createNewSave(Difficulty.Champion);
        assertNotNull(save);
        assertEquals(Difficulty.Champion, save.getDifficulty());
        assertEquals(1, save.getLevel());

        save.increaseLevel();
        assertEquals(2, save.getLevel());

        int countBefore = manager.getSaveCount();
        manager.deleteSave(save);
        assertEquals(countBefore - 1, manager.getSaveCount());
    }
}