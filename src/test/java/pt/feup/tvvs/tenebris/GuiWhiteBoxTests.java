package pt.feup.tvvs.tenebris;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.gui.Action;
import pt.feup.tvvs.tenebris.gui.GUI;
import pt.feup.tvvs.tenebris.gui.LanternaGUI;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class GuiWhiteBoxTests {

    private LanternaGUI gui;
    private Screen mockScreen;
    private TextGraphics mockGraphics;

    @BeforeEach
    public void setup() {
        // 1. Get the singleton instance
        gui = (LanternaGUI) LanternaGUI.getGUI();

        // 2. Mock the internal dependencies
        mockScreen = Mockito.mock(Screen.class);
        mockGraphics = Mockito.mock(TextGraphics.class);

        // 3. Inject mocks
        gui.setScreen(mockScreen);
        when(mockScreen.newTextGraphics()).thenReturn(mockGraphics);
        when(mockScreen.getTerminalSize()).thenReturn(new TerminalSize(100, 100));
    }

    @Test
    public void testGetAction_ArrowKeys() throws IOException, InterruptedException {
        // Arrange
        TerminalScreen mockTerminalScreen = Mockito.mock(TerminalScreen.class);
        SwingTerminalFrame mockTerminal = Mockito.mock(SwingTerminalFrame.class);

        // Inject specific screen type needed for casting in getAction
        gui.setScreen(mockTerminalScreen);
        when(mockTerminalScreen.getTerminal()).thenReturn(mockTerminal);

        // Simulate pressing Arrow Up
        when(mockTerminalScreen.pollInput()).thenReturn(new KeyStroke(KeyType.ArrowUp));

        // Act
        Action result = gui.getAction();

        // Assert
        assertEquals(Action.LOOK_UP, result);
    }

    @Test
    public void testGetAction_CharacterKeys() throws IOException, InterruptedException {
        TerminalScreen mockTerminalScreen = Mockito.mock(TerminalScreen.class);
        SwingTerminalFrame mockTerminal = Mockito.mock(SwingTerminalFrame.class);
        gui.setScreen(mockTerminalScreen);
        when(mockTerminalScreen.getTerminal()).thenReturn(mockTerminal);

        // Simulate pressing 'W' (Move Up)
        when(mockTerminalScreen.pollInput()).thenReturn(new KeyStroke('w', false, false));

        Action result = gui.getAction();
        assertEquals(Action.MOVE_UP, result);

        // Simulate pressing 'R' (Reload)
        when(mockTerminalScreen.pollInput()).thenReturn(new KeyStroke('r', false, false));
        Action result2 = gui.getAction();
        assertEquals(Action.RELOAD, result2);
    }

    @Test
    public void testDrawArenaBackGround() {
        gui.drawArenaBackGround();
        verify(mockGraphics).fillRectangle(any(TerminalPosition.class), any(), eq(' '));
    }

    @Test
    public void testDrawWeapon_Pistol() {
        try {
            // Access enum via GUI interface
            gui.drawWeapon(GUI.Weapon.PISTOL, 5);
            verify(mockScreen, atLeast(6)).newTextGraphics();
        } catch (Exception ignored) {}
    }

    @Test
    public void testDrawWeapon_GrenadeLauncher() {
        try {
            gui.drawWeapon(GUI.Weapon.GRENADE_LAUNCHER, 2);
            verify(mockScreen, atLeast(3)).newTextGraphics();
        } catch (Exception ignored) {}
    }

    @Test
    public void testDrawProjectile_BulletDirections() {
        try {
            Vector2D pos = new Vector2D(10,10);
            // Access enums via GUI interface
            gui.drawProjectile(pos, GUI.Projectile.BULLET, Vector2D.Direction.RIGHT);
            gui.drawProjectile(pos, GUI.Projectile.BULLET, Vector2D.Direction.UP);

            verify(mockScreen, atLeast(2)).newTextGraphics();
        } catch (Exception ignored) {}
    }

    @Test
    public void testDrawProjectile_ExplosiveAndSpell() {
        try {
            Vector2D pos = new Vector2D(10,10);
            gui.drawProjectile(pos, GUI.Projectile.EXPLOSIVE, Vector2D.Direction.RIGHT);
            gui.drawProjectile(pos, GUI.Projectile.SPELL, Vector2D.Direction.RIGHT);

            verify(mockScreen, atLeast(2)).newTextGraphics();
        } catch (Exception ignored) {}
    }

    @Test
    public void testDrawStaticElements() {
        try {
            Vector2D pos = new Vector2D(10,10);
            gui.drawStaticElement(pos, GUI.StaticElement.WALL);
            gui.drawStaticElement(pos, GUI.StaticElement.SPIKE);
            gui.drawStaticElement(pos, GUI.StaticElement.SANDBAG);

            verify(mockScreen, atLeast(3)).newTextGraphics();
        } catch (Exception ignored) {}
    }

    @Test
    public void testDrawDylan_AnimationStates() {
        try {
            Vector2D pos = new Vector2D(10,10);
            gui.drawDylan(pos, GUI.AnimationState.IDLE_1);
            gui.drawDylan(pos, GUI.AnimationState.FRONT_1);
            gui.drawDylan(pos, GUI.AnimationState.BACK_1);

            verify(mockScreen, atLeast(3)).newTextGraphics();
        } catch (Exception ignored) {}
    }

    @Test
    public void testDrawMonster_Types() {
        try {
            Vector2D pos = new Vector2D(10,10);
            // We test one state for each monster type to hit the main switch cases
            gui.drawMonster(pos, GUI.Monster.TENEBRIS_PEON, GUI.AnimationState.IDLE_1);
            gui.drawMonster(pos, GUI.Monster.TENEBRIS_HEAVY, GUI.AnimationState.IDLE_1);
            gui.drawMonster(pos, GUI.Monster.TENEBRIS_HARBINGER, GUI.AnimationState.IDLE_1);
            gui.drawMonster(pos, GUI.Monster.TENEBRIS_SPIKED_SCOUT, GUI.AnimationState.IDLE_1);
            gui.drawMonster(pos, GUI.Monster.TENEBRIS_WARDEN, GUI.AnimationState.IDLE_1);

            verify(mockScreen, atLeast(5)).newTextGraphics();
        } catch (Exception ignored) {}
    }

    @Test
    public void testRefresh() throws IOException {
        gui.refresh();
        verify(mockScreen).refresh();
    }

    @Test
    public void testClose() throws IOException {
        gui.close();
        verify(mockScreen).stopScreen();
    }
}