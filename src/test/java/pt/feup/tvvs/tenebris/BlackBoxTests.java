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

public class BlackBoxTests {

    @Test
    public void testWallTakeStandardDamage() {
        BreakableWall wall = new BreakableWall(new Vector2D(0, 0), 100);
        wall.takeDamage(20);
        assertEquals(80, wall.getHp());
    }

    @Test
    public void testWallExactKill() {
        BreakableWall wall = new BreakableWall(new Vector2D(0, 0), 50);
        wall.takeDamage(50);
        assertEquals(0, wall.getHp());
    }

    @Test
    public void testWallOverkill() {
        BreakableWall wall = new BreakableWall(new Vector2D(0, 0), 30);
        wall.takeDamage(100);
        assertEquals(0, wall.getHp(), "HP should clamp at 0");
    }

    @Test
    public void testWallNegativeDamage() {
        BreakableWall wall = new BreakableWall(new Vector2D(0, 0), 50);
        wall.takeDamage(-10);
        assertEquals(60, wall.getHp());
    }

    @Test
    public void testDrawArenaUI_FullHealth() {
        Screen mockScreen = Mockito.mock(Screen.class);
        TextGraphics mockGraphics = Mockito.mock(TextGraphics.class);
        when(mockScreen.newTextGraphics()).thenReturn(mockGraphics);

        LanternaGUI gui = (LanternaGUI) LanternaGUI.getGUI();
        gui.setScreen(mockScreen);

        gui.drawArenaUI(140, 140);

        verify(mockScreen, times(8)).newTextGraphics();
    }

    @Test
    public void testDrawArenaUI_ZeroHealth() {
        Screen mockScreen = Mockito.mock(Screen.class);
        TextGraphics mockGraphics = Mockito.mock(TextGraphics.class);
        when(mockScreen.newTextGraphics()).thenReturn(mockGraphics);

        LanternaGUI gui = (LanternaGUI) LanternaGUI.getGUI();
        gui.setScreen(mockScreen);

        gui.drawArenaUI(140, 0);

        verify(mockScreen, times(1)).newTextGraphics();
    }
}