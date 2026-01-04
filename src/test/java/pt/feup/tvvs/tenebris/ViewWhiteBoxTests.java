package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pt.feup.tvvs.tenebris.gui.GUI;
import pt.feup.tvvs.tenebris.model.arena.Arena;
import pt.feup.tvvs.tenebris.model.arena.effects.Explosion;
import pt.feup.tvvs.tenebris.model.arena.entities.Dylan;
import pt.feup.tvvs.tenebris.model.arena.entities.monster.*;
import pt.feup.tvvs.tenebris.model.arena.particles.*;
import pt.feup.tvvs.tenebris.model.arena.projectiles.*;
import pt.feup.tvvs.tenebris.model.arena.static_elements.*;
import pt.feup.tvvs.tenebris.model.arena.weapons.*;
import pt.feup.tvvs.tenebris.model.menu.*;
import pt.feup.tvvs.tenebris.savedata.SaveDataProvider;
import pt.feup.tvvs.tenebris.utils.Vector2D;
import pt.feup.tvvs.tenebris.view.arena.ArenaView;
import pt.feup.tvvs.tenebris.view.arena.effects.ExplosionView;
import pt.feup.tvvs.tenebris.view.arena.entity.DylanView;
import pt.feup.tvvs.tenebris.view.arena.entity.monster.*;
import pt.feup.tvvs.tenebris.view.arena.particles.*;
import pt.feup.tvvs.tenebris.view.arena.projectiles.*;
import pt.feup.tvvs.tenebris.view.arena.staticelement.*;
import pt.feup.tvvs.tenebris.view.arena.weapon.*;
import pt.feup.tvvs.tenebris.view.menu.*;

import java.io.IOException;

public class ViewWhiteBoxTests {

    @Test
    public void testAllViewsDraw() throws IOException {
        GUI mockGUI = Mockito.mock(GUI.class);
        Vector2D dummyPos = new Vector2D(0,0);

        try (MockedStatic<GUI> staticGUI = Mockito.mockStatic(GUI.class)) {
            staticGUI.when(GUI::getGUI).thenReturn(mockGUI);

            // --- Menu Views ---
            new MainMenuView(new MainMenu(Mockito.mock(SaveDataProvider.class))).draw();
            new NewGameMenuView(new NewGameMenu()).draw();
            new CreditsMenuView(new CreditsMenu()).draw();
            new DeathMenuView(new DeathMenu()).draw();
            new GameOverMenuView(new GameOverMenu()).draw();
            new HowToPlayMenuView(new HowToPlayMenu()).draw();
            new LevelCompletedMenuView(new LevelCompletedMenu()).draw();
            new PauseMenuView(new PauseMenu(Mockito.mock(Arena.class))).draw();
            new VictoryMenuView(new VictoryMenu()).draw();

            // --- Entity Views ---
            new DylanView(new Dylan(dummyPos, 100, 1)).draw(dummyPos);

            // Monsters
            new TenebrisPeonView(new TenebrisPeon(dummyPos, 10, 1, 1, 10)).draw(dummyPos);
            new TenebrisHeavyView(new TenebrisHeavy(dummyPos, 10, 1, 1, 10)).draw(dummyPos);
            new TenebrisHarbingerView(new TenebrisHarbinger(dummyPos, 10, 1, 1, 10, 10)).draw(dummyPos);
            new TenebrisSpikedScoutView(new TenebrisSpikedScout(dummyPos, 10, 1, 1, 10)).draw(dummyPos);
            new TenebrisWardenView(new TenebrisWarden(dummyPos, 10, 1, 1, 10)).draw(dummyPos);

            // --- Static Elements ---
            new WallView(new Wall(dummyPos)).draw(dummyPos);
            new BreakableWallView(new BreakableWall(dummyPos, 10)).draw(dummyPos);
            new SandbagView(new SandBag(dummyPos)).draw(dummyPos);
            new SpikeView(new Spike(dummyPos, 10)).draw(dummyPos);
            new VisionBlockerView(new VisionBlocker(dummyPos)).draw(dummyPos);

            // --- Projectiles ---
            new BulletView(new Bullet(dummyPos, Vector2D.Direction.RIGHT)).draw(dummyPos);
            new SpellView(new Spell(dummyPos, Vector2D.Direction.RIGHT, 10)).draw(dummyPos);
            new ExplosiveBulletView(new ExplosiveBullet(dummyPos, Vector2D.Direction.RIGHT)).draw(dummyPos);

            // --- Particles ---
            new DamageBloodView(new DamageBlood(dummyPos)).draw(dummyPos);
            new DeathBloodView(new DeathBlood(dummyPos)).draw(dummyPos);
            new SpellExplosionView(new SpellExplosion(dummyPos)).draw(dummyPos);
            new BreakableWallDamageView(new BreakableWallDamage(dummyPos)).draw(dummyPos);

            // --- Effects ---
            new ExplosionView(new Explosion(dummyPos, 10)).draw(dummyPos);

            // --- Weapons ---
            new PistolView(new Pistol()).draw();
            new GrenadeLauncherView(new GrenadeLauncher()).draw();

            // --- Arena View ---
            Arena arena = new Arena();
            arena.setDylan(new Dylan(dummyPos, 100, 1));
            new ArenaView(arena).draw();
        }
    }
}