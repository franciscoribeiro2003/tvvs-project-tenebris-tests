package pt.feup.tvvs.tenebris;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.gui.LanternaGUI;
import pt.feup.tvvs.tenebris.model.arena.static_elements.BreakableWall;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BlackBoxTests {

    // --- Target 1: BreakableWall.takeDamage ---

    @Test
    void testWallTakeStandardDamage() {
        BreakableWall wall = new BreakableWall(new Vector2D(0, 0), 100);
        wall.takeDamage(20);
        assertEquals(80, wall.getHp());
    }

    @Test
    void testWallExactKill() {
        BreakableWall wall = new BreakableWall(new Vector2D(0, 0), 50);
        wall.takeDamage(50);
        assertEquals(0, wall.getHp());
    }

    @Test
    void testWallOverkill() {
        BreakableWall wall = new BreakableWall(new Vector2D(0, 0), 30);
        wall.takeDamage(100);
        assertEquals(0, wall.getHp(), "HP should clamp at 0");
    }

    @Test
    void testWallNegativeDamage() {
        // Based on logic: hp -= damage. If damage is negative, HP increases.
        BreakableWall wall = new BreakableWall(new Vector2D(0, 0), 50);
        wall.takeDamage(-10);
        assertEquals(60, wall.getHp());
    }

    // --- Target 2: LanternaGUI.drawArenaUI ---

    @Test
    void testDrawArenaUI_FullHealth() {
        // Arrange Mocking to prevent real window opening
        Screen mockScreen = Mockito.mock(Screen.class);
        TextGraphics mockGraphics = Mockito.mock(TextGraphics.class);
        when(mockScreen.newTextGraphics()).thenReturn(mockGraphics);

        // Inject Mock
        LanternaGUI gui = (LanternaGUI) LanternaGUI.getGUI();
        gui.setScreen(mockScreen);

        // Act: 140 MaxHP / 7 parts = 20 per part. 140 HP = 7 parts full.
        // Draws 1 background + 7 parts = 8 interactions.
        gui.drawArenaUI(140, 140);

        // Assert
        verify(mockScreen, times(8)).newTextGraphics();
    }

    @Test
    void testDrawArenaUI_ZeroHealth() {
        Screen mockScreen = Mockito.mock(Screen.class);
        TextGraphics mockGraphics = Mockito.mock(TextGraphics.class);
        when(mockScreen.newTextGraphics()).thenReturn(mockGraphics);

        LanternaGUI gui = (LanternaGUI) LanternaGUI.getGUI();
        gui.setScreen(mockScreen);

        // Draws 1 background + 0 parts = 1 interaction.
        gui.drawArenaUI(140, 0);

        verify(mockScreen, times(1)).newTextGraphics();
    }
}
