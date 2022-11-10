package com.jonathanc8.fxglgame.factories;

import static com.almasb.fxgl.dsl.FXGL.*;

import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.TimeComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.jonathanc8.fxglgame.EntityTypes;
import com.jonathanc8.fxglgame.ItemTypes;
import com.jonathanc8.fxglgame.components.*;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class ShmupFactory implements EntityFactory {
    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        Color color = data.get("color");
        Entity e = entityBuilder(data)
                .type(EntityTypes.PLAYER)
                .viewWithBBox(texture("ship/sparrow.png"))
                .with(new Stats(10, 10, 8, EntityTypes.PLAYER))
                .at(800, 6450)
                .collidable()
                .build();
        e.addComponent(new PlayerControl(10));
        return e;
    }

    @Spawns("powerUp")
    public Entity newPowerUp(SpawnData data){
        Entity e = entityBuilder(data)
                .type(EntityTypes.POWER_UP)
                .viewWithBBox(new Rectangle(20, 20, Color.YELLOW))
                .collidable()
                .build();

        return e;
    }

    @Spawns("randomPowerUp")
    public Entity newRandPowerUp(SpawnData data){
        Entity e = newPowerUp(data);
        e.addComponent(new PowerUpController());
        return e;
    }

    @Spawns("missilePowerUp")
    public Entity newMissilePowerUp(SpawnData data){
        Entity e = newPowerUp(data);
        e.addComponent(new PowerUpController("missile"));
        return e;
    }

    @Spawns("bullet")
    public Entity newBullet(SpawnData data){
        Point2D dir = data.get("dir");
        EntityTypes side = data.get("side");
        int speed = ItemTypes.PLASMA_CANNON.speed;
        Texture texture= texture("projectile/blaster+.png");;
        Entity e = entityBuilder(data).type(EntityTypes.PROJECTILE)
                .with(new ProjectileComponent( dir, speed ))
                .rotate(90)
                .with(new TimeComponent())
                .with(new OffscreenCleanComponent())
                .with(new Teams(side))
                .with(new Stats(1, 1, speed, side))
                .view(texture)
                .bbox(BoundingShape.box(25, 50))
                .collidable()
                .build();
        return e;
    }

    @Spawns("homingMissile")
    public Entity newHomingMissile(SpawnData data){
        EntityTypes side = data.get("side");
        int speed = ItemTypes.MISSILE.speed;
        Entity missile = entityBuilder(data).type(EntityTypes.PROJECTILE)
                .with(new TimeComponent())
                .with(new HomingMissileComponent(side, speed))
                .with(new Stats(2, ItemTypes.MISSILE.damage, speed, side))
                .with(new OffscreenCleanComponent())
                .with(new Teams(side))
                .viewWithBBox(texture("projectile/javelin.png"))
                .collidable()
                .rotate(180)
                .build();

        return missile;
    }

    @Spawns("alienTank")
    public Entity newAlienTank(SpawnData data){
        Entity e = entityBuilder(data).type(EntityTypes.ENEMY).viewWithBBox(new Rectangle(100, 100, Color.RED)).collidable().build();
        e.addComponent(new ShootPlayer(e));
        return e;
    }

    @Spawns("alienGrunt")
    public Entity newAlienGrunt(SpawnData data){
        Entity grunt = entityBuilder(data)
                .type(EntityTypes.ENEMY)
                .viewWithBBox(texture("ship/wasp.png"))
                .with(new GruntController(4))
                .with(new Stats(2, 3, 4,0, EntityTypes.ENEMY))
                .collidable()
                .build();

        return grunt;
    }

    @Spawns("alienArtillery")
    public Entity newAlienArtie(SpawnData data){
        return null;
    }

    @Spawns("alienAntiAir")
    public Entity newAlienAA(SpawnData data){
        Entity e = entityBuilder(data).type(EntityTypes.ENEMY)
                .with(new Stats(5, 3, 0, EntityTypes.ENEMY))
                .viewWithBBox(texture("ship/hai water bug.png"))
                .collidable()
                .build();
        e.addComponent(new ShootPlayer(e));
        return e;
    }

    @Spawns("explosion")
    public Entity newExplosion(SpawnData data){
        List<Image> animeSprites = new ArrayList<>();
        AnimationChannel animation;
        if(data.get("entity") == "player"){
            animeSprites.add(image("effect/explosion/medium~1.png"));
            animeSprites.add(image("effect/explosion/medium~2.png"));
            animeSprites.add(image("effect/explosion/medium~3.png"));
            animeSprites.add(image("effect/explosion/medium~4.png"));
            animeSprites.add(image("effect/explosion/medium~5.png"));
            animeSprites.add(image("effect/explosion/medium~6.png"));
            animeSprites.add(image("effect/explosion/medium~7.png"));
        }

        if(data.get("entity") == "missile"){
            animeSprites.add(image("effect/explosion/small~1.png"));
            animeSprites.add(image("effect/explosion/small~2.png"));
            animeSprites.add(image("effect/explosion/small~3.png"));
            animeSprites.add(image("effect/explosion/small~4.png"));
            animeSprites.add(image("effect/explosion/small~5.png"));
            animeSprites.add(image("effect/explosion/small~6.png"));
        }
        if(data.get("entity") == "enemy"){
            animeSprites.add(image("effect/explosion/huge~1.png"));
            animeSprites.add(image("effect/explosion/huge~2.png"));
            animeSprites.add(image("effect/explosion/huge~3.png"));
            animeSprites.add(image("effect/explosion/huge~4.png"));
            animeSprites.add(image("effect/explosion/huge~5.png"));
            animeSprites.add(image("effect/explosion/huge~6.png"));
            animeSprites.add(image("effect/explosion/huge~7.png"));
            animeSprites.add(image("effect/explosion/huge~8.png"));
        }
        animation = new AnimationChannel(animeSprites, Duration.seconds(0.5));
        return entityBuilder(data)
                .view(texture("effect/explosion/huge~0.png").toAnimatedTexture(animation).play())
                .with(new ExpireCleanComponent(Duration.seconds(0.5)))
                .build();
    }
}
