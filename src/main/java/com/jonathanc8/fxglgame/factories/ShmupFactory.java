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
import com.jonathanc8.fxglgame.Entities;
import com.jonathanc8.fxglgame.components.HomingMissileComponent;
import com.jonathanc8.fxglgame.components.PlayerControl;
import com.jonathanc8.fxglgame.components.ShootPlayer;
import com.jonathanc8.fxglgame.components.Teams;
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
        Entity e = entityBuilder(data).type(Entities.PLAYER).viewWithBBox(texture("ship/sparrow.png")).at(800, 6450).collidable().build();
        e.addComponent(new PlayerControl(e, 10));
        return e;
    }

    @Spawns("powerUp")
    public Entity newPowerUp(SpawnData data){
        Entity e = entityBuilder().type(Entities.POWER_UP).viewWithBBox(new Rectangle(20, 20, Color.YELLOW)).collidable().build();

        return e;
    }

    @Spawns("bullet")
    public Entity newBullet(SpawnData data){
        Point2D dir = data.get("dir");
        int side = data.get("side");
        int speed;
        Texture texture;
        if(side == 0){
            texture = texture("projectile/blaster+.png");
            speed = 1500;
        } else {
            texture = texture("projectile/javelin.png");
            speed = 300;
        }
        Entity e = entityBuilder(data).type(Entities.PROJECTILE)
                .with(new ProjectileComponent( dir, speed ))
                .rotate(90)
                .with(new TimeComponent())
                .with(new OffscreenCleanComponent())
                .with(new Teams(side))
                .view(texture)
                .bbox(BoundingShape.box(25, 50))
                .collidable()
                .build();
        return e;
    }

    @Spawns("missile")
    public Entity newMissile(SpawnData data){
        Entities side = data.get("side");
        int speed = 4;
        Entity missile = entityBuilder(data).type(Entities.PROJECTILE)
                .with(new TimeComponent())
                .with(new HomingMissileComponent(side, speed))
                .with(new OffscreenCleanComponent())
                .with(new Teams(side))
                .viewWithBBox(texture("projectile/javelin.png"))
                .collidable()
                .rotate(180)
                .build();

        return missile;
    }

    @Spawns("enemy")
    public Entity newEnemy(SpawnData data){
        Entity e = entityBuilder(data).type(Entities.ENEMY).viewWithBBox(texture("ship/hai water bug.png"))
                .collidable()
                .build();
        e.addComponent(new ShootPlayer(e));
        return e;
    }

    @Spawns("alienTank")
    public Entity newAlienTank(SpawnData data){
        Entity e = entityBuilder(data).type(Entities.ENEMY).viewWithBBox(new Rectangle(100, 100, Color.RED)).collidable().build();
        e.addComponent(new ShootPlayer(e));
        return e;
    }

    @Spawns("alienGrunt")
    public Entity newAlienGrunt(SpawnData data){
        return null;
    }

    @Spawns("alienArtillery")
    public Entity newAlienArtie(SpawnData data){
        return null;
    }

    @Spawns("alienAntiair")
    public Entity newAlienAA(SpawnData data){
        return null;
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
