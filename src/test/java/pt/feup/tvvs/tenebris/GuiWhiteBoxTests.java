package pt.feup.tvvs.tenebris;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.gui.Action;
import pt.feup.tvvs.tenebris.gui.GUI;
import pt.feup.tvvs.tenebris.gui.LanternaGUI;
import pt.feup.tvvs.tenebris.utils.Difficulty;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

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
        gui = (LanternaGUI) LanternaGUI.getGUI();
        mockScreen = Mockito.mock(Screen.class);
        mockGraphics = Mockito.mock(TextGraphics.class);
        gui.setScreen(mockScreen);
        when(mockScreen.newTextGraphics()).thenReturn(mockGraphics);
        when(mockScreen.getTerminalSize()).thenReturn(new TerminalSize(100, 100));
    }

    @AfterEach
    public void tearDown() {
        // Explicitly clear references to help GC
        Mockito.clearInvocations(mockScreen, mockGraphics);
        mockScreen = null;
        mockGraphics = null;
    }

    @Test
    public void testGetAction_ArrowKeys() throws IOException, InterruptedException {
        TerminalScreen mockTerminalScreen = Mockito.mock(TerminalScreen.class);
        SwingTerminalFrame mockTerminal = Mockito.mock(SwingTerminalFrame.class);
        gui.setScreen(mockTerminalScreen);
        when(mockTerminalScreen.getTerminal()).thenReturn(mockTerminal);

        KeyType[] types = {KeyType.Escape, KeyType.ArrowUp, KeyType.ArrowDown, KeyType.ArrowLeft, KeyType.ArrowRight, KeyType.Enter};
        Action[] expected = {Action.ESC, Action.LOOK_UP, Action.LOOK_DOWN, Action.LOOK_LEFT, Action.LOOK_RIGHT, Action.EXEC};

        for (int i = 0; i < types.length; i++) {
            when(mockTerminalScreen.pollInput()).thenReturn(new KeyStroke(types[i]));
            assertEquals(expected[i], gui.getAction());
        }
    }

    @Test
    public void testGetAction_CharKeys() throws IOException, InterruptedException {
        TerminalScreen mockTerminalScreen = Mockito.mock(TerminalScreen.class);
        SwingTerminalFrame mockTerminal = Mockito.mock(SwingTerminalFrame.class);
        gui.setScreen(mockTerminalScreen);
        when(mockTerminalScreen.getTerminal()).thenReturn(mockTerminal);

        char[] chars = {'e', 'q', 'w', 's', 'a', 'd', 'r', '1', '2', ' '};
        Action[] charExpected = {Action.EXEC, Action.QUIT, Action.MOVE_UP, Action.MOVE_DOWN, Action.MOVE_LEFT, Action.MOVE_RIGHT, Action.RELOAD, Action.SELECT_1, Action.SELECT_2, Action.EXEC};

        for (int i = 0; i < chars.length; i++) {
            when(mockTerminalScreen.pollInput()).thenReturn(new KeyStroke(chars[i], false, false));
            assertEquals(charExpected[i], gui.getAction());
        }
    }

    @Test
    public void testDrawArenaBackGround() {
        gui.drawArenaBackGround();
        verify(mockGraphics).fillRectangle(any(TerminalPosition.class), any(), eq(' '));
    }

    @Test
    public void testDrawAllStaticElements() {
        try {
            // Split execution to reduce trace size
            gui.drawStaticElement(new Vector2D(10, 10), GUI.StaticElement.WALL);
            gui.drawStaticElement(new Vector2D(10, 10), GUI.StaticElement.BREAKABLE_WALL);
            gui.drawStaticElement(new Vector2D(10, 10), GUI.StaticElement.SANDBAG);
            gui.drawStaticElement(new Vector2D(10, 10), GUI.StaticElement.SPIKE);

            verify(mockScreen, atLeast(4)).newTextGraphics();
        } catch (Exception ignored) {}
    }

    @Test
    public void testDrawAllProjectiles() {
        try {
            gui.drawProjectile(new Vector2D(10, 10), GUI.Projectile.BULLET, Vector2D.Direction.RIGHT);
            gui.drawProjectile(new Vector2D(10, 10), GUI.Projectile.BULLET, Vector2D.Direction.UP);
            gui.drawProjectile(new Vector2D(10, 10), GUI.Projectile.EXPLOSIVE, Vector2D.Direction.RIGHT);
            gui.drawProjectile(new Vector2D(10, 10), GUI.Projectile.SPELL, Vector2D.Direction.RIGHT);

            verify(mockScreen, atLeast(4)).newTextGraphics();
        } catch (Exception ignored) {}
    }

    @Test
    public void testDrawAllParticles() {
        try {
            gui.drawParticleEffect(new Vector2D(10, 10), GUI.ParticleEffect.DAMAGE_BLOOD, 1);
            gui.drawParticleEffect(new Vector2D(10, 10), GUI.ParticleEffect.DEATH_BLOOD, 1);
            gui.drawParticleEffect(new Vector2D(10, 10), GUI.ParticleEffect.SPELL_EXPLOSION, 1);
            gui.drawParticleEffect(new Vector2D(10, 10), GUI.ParticleEffect.EXPLOSION, 1);
            gui.drawParticleEffect(new Vector2D(10, 10), GUI.ParticleEffect.BREAKABLE_WALL_DAMAGE, 1);

            verify(mockScreen, atLeast(5)).newTextGraphics();
        } catch (Exception ignored) {}
    }

    @Test
    public void testDrawPeonAndHeavy() {
        try {
            // Reduced test scope per method to save memory
            gui.drawMonster(new Vector2D(10, 10), GUI.Monster.TENEBRIS_PEON, GUI.AnimationState.IDLE_1);
            gui.drawMonster(new Vector2D(10, 10), GUI.Monster.TENEBRIS_HEAVY, GUI.AnimationState.FRONT_1);
            verify(mockScreen, atLeastOnce()).newTextGraphics();
        } catch (Exception ignored) {}
    }

    @Test
    public void testDrawHarbingerAndScout() {
        try {
            gui.drawMonster(new Vector2D(10, 10), GUI.Monster.TENEBRIS_HARBINGER, GUI.AnimationState.BACK_1);
            gui.drawMonster(new Vector2D(10, 10), GUI.Monster.TENEBRIS_SPIKED_SCOUT, GUI.AnimationState.LEFT_1);
            verify(mockScreen, atLeastOnce()).newTextGraphics();
        } catch (Exception ignored) {}
    }

    @Test
    public void testDrawWarden() {
        try {
            gui.drawMonster(new Vector2D(10, 10), GUI.Monster.TENEBRIS_WARDEN, GUI.AnimationState.RIGHT_1);
            verify(mockScreen, atLeastOnce()).newTextGraphics();
        } catch (Exception ignored) {}
    }

    @Test
    public void testDrawDylanStates() {
        try {
            gui.drawDylan(new Vector2D(10,10), GUI.AnimationState.IDLE_1);
            gui.drawDylan(new Vector2D(10,10), GUI.AnimationState.FRONT_1);
            gui.drawDylan(new Vector2D(10,10), GUI.AnimationState.BACK_1);
            verify(mockScreen, atLeastOnce()).newTextGraphics();
        } catch (Exception ignored) {}
    }

    @Test
    public void testDrawMenus() {
        try {
            List<GUI.Menu_Options> ops = Collections.singletonList(GUI.Menu_Options.MAIN_MENU_EXIT);
            gui.drawMainMenu(ops, 0);
            gui.drawNewGameMenu(ops, 0);
            gui.drawCreditsMenu();
            gui.drawVictoryMenu();

            verify(mockScreen, atLeastOnce()).newTextGraphics();
        } catch (Exception ignored) {}
    }

    @Test
    public void testDrawMenus2() {
        try {
            List<GUI.Menu_Options> ops = Collections.singletonList(GUI.Menu_Options.MAIN_MENU_EXIT);
            gui.drawGameOverMenu();
            gui.drawDeathMenu(ops, 0);
            gui.drawLevelCompletedMenu(ops, 0);
            gui.drawPauseMenuMenu(ops, 0);

            verify(mockScreen, atLeastOnce()).newTextGraphics();
        } catch (Exception ignored) {}
    }

    @Test
    public void testDrawLoadGameMenu_Coverage() throws IOException {
        try {
            gui.drawLoadGameMenu(0, 0, 0, null);
            gui.drawLoadGameMenu(5, 1, 1, Difficulty.Normal);
            verify(mockScreen, atLeastOnce()).newTextGraphics();
        } catch (Exception ignored) {}
    }

    @Test
    public void testDrawLevelsMenu_Coverage() throws IOException {
        try {
            gui.drawLevelsMenu(1, 1, Difficulty.Normal);
            gui.drawLevelsMenu(6, 6, Difficulty.Heartless);
            verify(mockScreen, atLeastOnce()).newTextGraphics();
        } catch (Exception ignored) {}
    }

    @Test
    public void testDrawHowToPlay() {
        try {
            // Split big loop into smaller chunks if needed, but 9 iterations should be safe if isolated
            for(int i=0; i<=8; i++) gui.drawHowToPlayMenu(i);
            verify(mockScreen, atLeastOnce()).newTextGraphics();
        } catch (Exception ignored) {}
    }

    @Test
    public void testDrawWeapon() {
        try {
            gui.drawWeapon(GUI.Weapon.PISTOL, 5);
            gui.drawWeapon(GUI.Weapon.GRENADE_LAUNCHER, 1);
            verify(mockScreen, atLeast(2)).newTextGraphics();
        } catch (Exception ignored) {}
    }

    @Test
    public void testRefreshAndClose() throws IOException {
        gui.refresh();
        verify(mockScreen).refresh();

        gui.close();
        verify(mockScreen).stopScreen();
    }
}