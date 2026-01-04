package pt.feup.tvvs.tenebris;

import org.junit.jupiter.api.Test;
import pt.feup.tvvs.tenebris.model.arena._commands.*;
import pt.feup.tvvs.tenebris.model.arena.effects.Explosion;
import pt.feup.tvvs.tenebris.model.arena.entities.monster.TenebrisPeon;
import pt.feup.tvvs.tenebris.model.arena.particles.DamageBlood;
import pt.feup.tvvs.tenebris.model.arena.particles.ParticleType;
import pt.feup.tvvs.tenebris.model.arena.projectiles.Bullet;
import pt.feup.tvvs.tenebris.model.arena.static_elements.BreakableWall;
import pt.feup.tvvs.tenebris.utils.Vector2D;

import static org.junit.jupiter.api.Assertions.*;

public class CommandWhiteBoxTests {


    @Test
    public void testCreateProjectileCommand() {
        Vector2D pos = new Vector2D(10, 10);
        Bullet bullet = new Bullet(pos, Vector2D.Direction.RIGHT);
        CreateProjectile command = new CreateProjectile(bullet);

        assertEquals(bullet, command.projectile());
    }

    @Test
    public void testCreateProjectileEquality() {
        Vector2D pos = new Vector2D(10, 10);
        Bullet bullet1 = new Bullet(pos, Vector2D.Direction.RIGHT);
        Bullet bullet2 = new Bullet(pos, Vector2D.Direction.RIGHT);

        CreateProjectile cmd1 = new CreateProjectile(bullet1);
        CreateProjectile cmd2 = new CreateProjectile(bullet1);
        CreateProjectile cmd3 = new CreateProjectile(bullet2);

        assertEquals(cmd1, cmd2);
        assertNotEquals(cmd1, cmd3);
        assertNotEquals(cmd1, null);
        assertNotEquals(cmd1, "string");
    }

    @Test
    public void testDeleteProjectileCommand() {
        Vector2D pos = new Vector2D(10, 10);
        Bullet bullet = new Bullet(pos, Vector2D.Direction.RIGHT);
        DeleteProjectile command = new DeleteProjectile(bullet);

        assertEquals(bullet, command.projectile());
    }

    @Test
    public void testDeleteProjectileEquality() {
        Vector2D pos = new Vector2D(10, 10);
        Bullet bullet = new Bullet(pos, Vector2D.Direction.RIGHT);

        DeleteProjectile cmd1 = new DeleteProjectile(bullet);
        DeleteProjectile cmd2 = new DeleteProjectile(bullet);

        assertEquals(cmd1, cmd2);
    }

    @Test
    public void testCreateParticleCommand() {
        Vector2D pos = new Vector2D(10, 10);
        CreateParticle command = new CreateParticle(pos, ParticleType.DAMAGE_BLOOD);

        assertEquals(pos, command.position());
        assertEquals(ParticleType.DAMAGE_BLOOD, command.type());
    }

    @Test
    public void testCreateParticleEquality() {
        Vector2D pos = new Vector2D(10, 10);
        CreateParticle cmd1 = new CreateParticle(pos, ParticleType.DAMAGE_BLOOD);
        CreateParticle cmd2 = new CreateParticle(pos, ParticleType.DAMAGE_BLOOD);
        CreateParticle cmd3 = new CreateParticle(pos, ParticleType.DEATH_BLOOD);

        assertEquals(cmd1, cmd2);
        assertNotEquals(cmd1, cmd3);
    }

    @Test
    public void testDeleteParticleCommand() {
        Vector2D pos = new Vector2D(10, 10);
        DamageBlood particle = new DamageBlood(pos);
        DeleteParticle command = new DeleteParticle(particle);

        assertEquals(particle, command.particle());
    }

    @Test
    public void testCreateEffectCommand() {
        Vector2D pos = new Vector2D(10, 10);
        Explosion explosion = new Explosion(pos, 20);
        CreateEffect command = new CreateEffect(explosion);

        assertEquals(explosion, command.effect());
    }

    @Test
    public void testCreateEffectEquality() {
        Vector2D pos = new Vector2D(10, 10);
        Explosion explosion = new Explosion(pos, 20);

        CreateEffect cmd1 = new CreateEffect(explosion);
        CreateEffect cmd2 = new CreateEffect(explosion);

        assertEquals(cmd1, cmd2);
    }

    @Test
    public void testDeleteEffectCommand() {
        Vector2D pos = new Vector2D(10, 10);
        Explosion explosion = new Explosion(pos, 20);
        DeleteEffect command = new DeleteEffect(explosion);

        assertEquals(explosion, command.effect());
    }

    @Test
    public void testDeleteMonsterCommand() {
        Vector2D pos = new Vector2D(10, 10);
        TenebrisPeon monster = new TenebrisPeon(pos, 50, 2, 10, 100);
        DeleteMonster command = new DeleteMonster(monster);

        assertEquals(monster, command.monster());
    }

    @Test
    public void testDeleteMonsterEquality() {
        Vector2D pos = new Vector2D(10, 10);
        TenebrisPeon monster = new TenebrisPeon(pos, 50, 2, 10, 100);

        DeleteMonster cmd1 = new DeleteMonster(monster);
        DeleteMonster cmd2 = new DeleteMonster(monster);

        assertEquals(cmd1, cmd2);
    }

    @Test
    public void testDeleteBreakableWallCommand() {
        Vector2D pos = new Vector2D(10, 10);
        BreakableWall wall = new BreakableWall(pos, 50);
        DeleteBreakableWall command = new DeleteBreakableWall(wall);

        assertEquals(wall, command.breakableWall());
    }

    @Test
    public void testKillDylanCommand() {
        KillDylan command = new KillDylan();
        assertNotNull(command);
    }

    @Test
    public void testShakeCameraCommand() {
        ShakeCamera command = new ShakeCamera();
        assertNotNull(command);
    }
}