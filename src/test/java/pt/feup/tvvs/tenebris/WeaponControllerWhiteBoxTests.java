package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.controller.arena.weapon.GrenadeLauncherController;
import pt.feup.tvvs.tenebris.controller.arena.weapon.PistolController;
import pt.feup.tvvs.tenebris.model.arena._commands.CommandHandler;
import pt.feup.tvvs.tenebris.model.arena._commands.CreateProjectile;
import pt.feup.tvvs.tenebris.model.arena.weapons.GrenadeLauncher;
import pt.feup.tvvs.tenebris.model.arena.weapons.Pistol;
import pt.feup.tvvs.tenebris.sound.SoundManager;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WeaponControllerWhiteBoxTests {

    @Test
    public void testPistolShootCreatesProjectile() {
        try (MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class)) {
            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            Pistol pistol = new Pistol();
            PistolController controller = (PistolController) pistol.getController();
            CommandHandler handler = Mockito.mock(CommandHandler.class);

            // Wait for cooldown
            for (int i = 0; i < 10; i++) {
                controller.update();
            }

            controller.shoot(handler, new Vector2D(50, 50), Vector2D.Direction.RIGHT);

            verify(handler).handleCommand(any(CreateProjectile.class));
        }
    }

    @Test
    public void testPistolCannotShootDuringCooldown() {
        try (MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class)) {
            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            Pistol pistol = new Pistol();
            PistolController controller = (PistolController) pistol.getController();
            CommandHandler handler = Mockito.mock(CommandHandler.class);

            // Shoot once with proper cooldown
            for (int i = 0; i < 10; i++) controller.update();
            controller.shoot(handler, new Vector2D(50, 50), Vector2D.Direction.RIGHT);

            // Try immediate second shot
            controller.shoot(handler, new Vector2D(50, 50), Vector2D.Direction.RIGHT);

            // Should only have one projectile created
            verify(handler, times(1)).handleCommand(any(CreateProjectile.class));
        }
    }

    @Test
    public void testPistolReloadsWhenEmpty() {
        try (MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class)) {
            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            Pistol pistol = new Pistol();
            PistolController controller = (PistolController) pistol.getController();
            CommandHandler handler = Mockito.mock(CommandHandler.class);

            // Empty the magazine
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) controller.update();
                if (pistol.canShoot()) {
                    controller.shoot(handler, new Vector2D(50, 50), Vector2D.Direction.RIGHT);
                }
            }

            assertFalse(pistol.isLoaded() && ! pistol.isReloading());
        }
    }

    @Test
    public void testGrenadeLauncherShootCreatesProjectile() {
        try (MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class)) {
            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            GrenadeLauncher gl = new GrenadeLauncher();
            GrenadeLauncherController controller = (GrenadeLauncherController) gl.getController();
            CommandHandler handler = Mockito.mock(CommandHandler.class);

            controller.shoot(handler, new Vector2D(50, 50), Vector2D.Direction.RIGHT);

            verify(handler).handleCommand(any(CreateProjectile.class));
        }
    }

    @Test
    public void testGrenadeLauncherReloadsAfterShot() {
        try (MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class)) {
            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            GrenadeLauncher gl = new GrenadeLauncher();
            GrenadeLauncherController controller = (GrenadeLauncherController) gl.getController();
            CommandHandler handler = Mockito.mock(CommandHandler.class);

            controller.shoot(handler, new Vector2D(50, 50), Vector2D.Direction.RIGHT);

            assertFalse(gl.isLoaded());
        }
    }

    @Test
    public void testWeaponReloadTriggersSound() {
        try (MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class)) {
            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            Pistol pistol = new Pistol();
            PistolController controller = (PistolController) pistol.getController();

            controller.reload();

            verify(mockSound).playSFX(SoundManager.SFX.PISTOL_RELOAD);
        }
    }

    @Test
    public void testPistolShootDirections() {
        try (MockedStatic<SoundManager> sound = Mockito.mockStatic(SoundManager.class)) {
            SoundManager mockSound = Mockito.mock(SoundManager.class);
            sound.when(SoundManager::getInstance).thenReturn(mockSound);

            Pistol pistol = new Pistol();
            PistolController controller = (PistolController) pistol.getController();
            CommandHandler handler = Mockito.mock(CommandHandler.class);

            Vector2D.Direction[] directions = {
                    Vector2D.Direction.UP, Vector2D.Direction.DOWN,
                    Vector2D.Direction.LEFT, Vector2D.Direction.RIGHT
            };

            for (Vector2D.Direction dir : directions) {
                for (int i = 0; i < 10; i++) controller.update();
                if (pistol.canShoot()) {
                    controller.shoot(handler, new Vector2D(50, 50), dir);
                }
            }

            verify(handler, atLeast(1)).handleCommand(any(CreateProjectile.class));
        }
    }
}