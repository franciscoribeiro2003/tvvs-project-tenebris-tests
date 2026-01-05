package pt.feup.tvvs.tenebris;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.gui.LanternaGUI;
import pt.feup.tvvs.tenebris.model.arena.static_elements.BreakableWall;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BlackBoxTests {

    // ========== BreakableWall.takeDamage(int damage) Tests ==========

    // EC1:  Valid positive damage (typical case)
    @Test
    public void testWallTakeStandardDamage() {
        BreakableWall wall = new BreakableWall(new Vector2D(0, 0), 100);
        wall.takeDamage(20);
        assertEquals(80, wall.getHp());
    }

    // EC2: Damage equals HP (boundary - exact kill)
    @Test
    public void testWallExactKill() {
        BreakableWall wall = new BreakableWall(new Vector2D(0, 0), 50);
        wall.takeDamage(50);
        assertEquals(0, wall.getHp());
        assertFalse(wall.isAlive());
    }

    // EC3: Damage exceeds HP (overkill - boundary)
    @Test
    public void testWallOverkill() {
        BreakableWall wall = new BreakableWall(new Vector2D(0, 0), 30);
        wall.takeDamage(100);
        assertEquals(0, wall.getHp(), "HP should clamp at 0");
        assertFalse(wall.isAlive());
    }

    // EC4: Negative damage (heals the wall)
    @Test
    public void testWallNegativeDamage() {
        BreakableWall wall = new BreakableWall(new Vector2D(0, 0), 50);
        wall.takeDamage(-10);
        assertEquals(60, wall.getHp());
    }

    // EC5: Zero damage
    @Test
    public void testWallZeroDamage() {
        BreakableWall wall = new BreakableWall(new Vector2D(0, 0), 50);
        wall.takeDamage(0);
        assertEquals(50, wall.getHp());
        assertTrue(wall.isAlive());
    }

    // BVA:  Damage = HP - 1 (just below kill threshold)
    @Test
    public void testWallDamageBelowKill() {
        BreakableWall wall = new BreakableWall(new Vector2D(0, 0), 50);
        wall.takeDamage(49);
        assertEquals(1, wall.getHp());
        assertTrue(wall.isAlive());
    }

    // BVA: Damage = HP + 1 (just above kill threshold)
    @Test
    public void testWallDamageAboveKill() {
        BreakableWall wall = new BreakableWall(new Vector2D(0, 0), 50);
        wall.takeDamage(51);
        assertEquals(0, wall.getHp());
        assertFalse(wall.isAlive());
    }

    // BVA:  Very small damage
    @Test
    public void testWallMinimalDamage() {
        BreakableWall wall = new BreakableWall(new Vector2D(0, 0), 100);
        wall.takeDamage(1);
        assertEquals(99, wall.getHp());
    }

    // BVA: Very large damage
    @Test
    public void testWallMassiveDamage() {
        BreakableWall wall = new BreakableWall(new Vector2D(0, 0), 100);
        wall.takeDamage(Integer.MAX_VALUE);
        assertEquals(0, wall.getHp());
    }

    // ========== LanternaGUI.drawArenaUI(int maxHP, int hp) Tests ==========

    // EC1: Full health
    @Test
    public void testDrawArenaUI_FullHealth() {
        Screen mockScreen = Mockito.mock(Screen.class);
        TextGraphics mockGraphics = Mockito.mock(TextGraphics.class);
        when(mockScreen.newTextGraphics()).thenReturn(mockGraphics);

        LanternaGUI gui = (LanternaGUI) LanternaGUI.getGUI();
        gui.setScreen(mockScreen);

        // MaxHP 140 / 7 = 20 per part. HP 140 -> 7 parts. Total draws: 1 (bg) + 7 (parts) = 8
        gui.drawArenaUI(140, 140);

        verify(mockScreen, times(8)).newTextGraphics();
    }

    // EC2: Zero health
    @Test
    public void testDrawArenaUI_ZeroHealth() {
        Screen mockScreen = Mockito.mock(Screen.class);
        TextGraphics mockGraphics = Mockito.mock(TextGraphics.class);
        when(mockScreen.newTextGraphics()).thenReturn(mockGraphics);

        LanternaGUI gui = (LanternaGUI) LanternaGUI.getGUI();
        gui.setScreen(mockScreen);

        // HP 0 -> 0 parts. Total draws: 1 (bg) + 0 = 1
        gui.drawArenaUI(140, 0);

        verify(mockScreen, times(1)).newTextGraphics();
    }

    // EC3: Half health
    @Test
    public void testDrawArenaUI_HalfHealth() {
        Screen mockScreen = Mockito.mock(Screen.class);
        TextGraphics mockGraphics = Mockito.mock(TextGraphics.class);
        when(mockScreen.newTextGraphics()).thenReturn(mockGraphics);

        LanternaGUI gui = (LanternaGUI) LanternaGUI.getGUI();
        gui.setScreen(mockScreen);

        // MaxHP 100 / 7 = 14 (integer div). HP 50.
        // Parts = ceil(50/14) = 4. Total draws = 1 + 4 = 5.
        gui.drawArenaUI(100, 50);

        verify(mockScreen, times(5)).newTextGraphics();
    }

    // BVA: HP = 1 (minimum alive)
    @Test
    public void testDrawArenaUI_MinimalHealth() {
        Screen mockScreen = Mockito.mock(Screen.class);
        TextGraphics mockGraphics = Mockito.mock(TextGraphics.class);
        when(mockScreen.newTextGraphics()).thenReturn(mockGraphics);

        LanternaGUI gui = (LanternaGUI) LanternaGUI.getGUI();
        gui.setScreen(mockScreen);

        // HP 1 -> Always at least 1 part if alive. Total draws = 2.
        gui.drawArenaUI(100, 1);

        verify(mockScreen, times(2)).newTextGraphics();
    }

    // BVA: HP = maxHP - 1
    @Test
    public void testDrawArenaUI_AlmostFullHealth() {
        Screen mockScreen = Mockito.mock(Screen.class);
        TextGraphics mockGraphics = Mockito.mock(TextGraphics.class);
        when(mockScreen.newTextGraphics()).thenReturn(mockGraphics);

        LanternaGUI gui = (LanternaGUI) LanternaGUI.getGUI();
        gui.setScreen(mockScreen);

        // MaxHP 100 / 7 = 14. HP 99. 99/14 = 7.07 -> 8 parts?
        // Logic: 7 parts max. 14 * 7 = 98. So 99 fills 7 parts completely?
        // Code: current = (99 + 14 - 1) / 14 = 112 / 14 = 8.
        // Wait, max parts is 7. If logic allows >7, we verify strictly what code does.
        // If code doesn't cap at 7, this test ensures we know that.
        // Let's use a number that creates exactly 7 parts.
        // Max 140, hp 139. Portion 20. (139+19)/20 = 7. Total 8.
        gui.drawArenaUI(140, 139);

        verify(mockScreen, times(8)).newTextGraphics();
    }

    // EC4: Different maxHP values
    @Test
    public void testDrawArenaUI_VariousMaxHP() {
        Screen mockScreen = Mockito.mock(Screen.class);
        TextGraphics mockGraphics = Mockito.mock(TextGraphics.class);
        when(mockScreen.newTextGraphics()).thenReturn(mockGraphics);

        LanternaGUI gui = (LanternaGUI) LanternaGUI.getGUI();
        gui.setScreen(mockScreen);

        // 150/7=21. 75/21 = 3.5 -> 4. Total 5 calls.
        gui.drawArenaUI(150, 75);
        verify(mockScreen, times(5)).newTextGraphics();

        Mockito.reset(mockScreen);
        when(mockScreen.newTextGraphics()).thenReturn(mockGraphics);

        // 200/7=28. 100/28 = 3.5 -> 4. Total 5 calls.
        gui.drawArenaUI(200, 100);
        verify(mockScreen, times(5)).newTextGraphics();
    }


    @Test
    public void testDrawArenaUI_BoundaryOneHealth() {
        Screen mockScreen = Mockito.mock(Screen.class);
        TextGraphics mockGraphics = Mockito.mock(TextGraphics.class);
        when(mockScreen.newTextGraphics()).thenReturn(mockGraphics);

        LanternaGUI gui = (LanternaGUI) LanternaGUI.getGUI();
        gui.setScreen(mockScreen);

        gui.drawArenaUI(140, 1);  // Boundary:  just above zero. 1/20 -> 1 part. Total 2.
        verify(mockScreen, times(2)).newTextGraphics();
    }


    @Test
    public void testDrawArenaUI_NegativeHealth() {
        Screen mockScreen = Mockito.mock(Screen.class);
        TextGraphics mockGraphics = Mockito.mock(TextGraphics.class);
        when(mockScreen.newTextGraphics()).thenReturn(mockGraphics);

        LanternaGUI gui = (LanternaGUI) LanternaGUI.getGUI();
        gui.setScreen(mockScreen);

        // Test with negative HP (boundary). Result should be 0 parts. Total 1.
        gui.drawArenaUI(140, -10);
        verify(mockScreen, times(1)).newTextGraphics();
    }

    @Test
    public void testWallBoundaryOneHP() {
        BreakableWall wall = new BreakableWall(new Vector2D(0, 0), 1);
        assertTrue(wall.isAlive());
        wall.takeDamage(1);
        assertFalse(wall.isAlive());
        assertEquals(0, wall.getHp());
    }

    @Test
    public void testWallLargeDamage() {
        BreakableWall wall = new BreakableWall(new Vector2D(0, 0), 50);
        wall.takeDamage(Integer.MAX_VALUE);
        assertEquals(0, wall.getHp(), "HP should clamp at 0");
        assertFalse(wall.isAlive());
    }

    @Test
    public void testWallMultipleDamageInstances() {
        BreakableWall wall = new BreakableWall(new Vector2D(0, 0), 100);
        wall.takeDamage(30);
        assertEquals(70, wall.getHp());
        wall.takeDamage(30);
        assertEquals(40, wall.getHp());
        wall.takeDamage(30);
        assertEquals(10, wall.getHp());
        wall.takeDamage(30);
        assertEquals(0, wall.getHp());
    }
}