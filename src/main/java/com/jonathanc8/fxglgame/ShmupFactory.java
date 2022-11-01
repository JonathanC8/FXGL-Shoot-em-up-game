package com.jonathanc8.fxglgame;

import static com.almasb.fxgl.dsl.FXGL.*;

import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.dsl.views.ScrollingBackgroundView;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.TimeComponent;
import com.almasb.fxgl.entity.state.EntityState;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class ShmupFactory implements EntityFactory {
    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        Color color = data.get("color");
        return entityBuilder(data).type(Entities.PLAYER).viewWithBBox(new Rectangle(25, 25, color)).collidable().at(800,800).build();
    }

    @Spawns("bullet")
    public Entity newBullet(SpawnData data){
        Point2D dir = data.get("dir");
        int side = data.get("side");
        int speed;
        Color color;
        if(side == 0){
            color = Color.DARKGRAY;
            speed = 500;
        } else {
            color = Color.RED;
            speed = 300;
        }
        return entityBuilder(data).type(Entities.BULLET).with(new ProjectileComponent( dir, speed ))
                .with(new TimeComponent())
                .with(new OffscreenCleanComponent())
                .with(new Teams(side))
                .viewWithBBox(new Rectangle(5,7, color))
                .collidable()
                .build();
    }

    @Spawns("enemy")
    public Entity newEnemy(SpawnData data){
        Entity e = entityBuilder(data).type(Entities.ENEMY).viewWithBBox(new Rectangle(60, 60, Color.RED)).collidable()
                .build();
        e.addComponent(new ShootPlayer(e));
        return e;
    }

    @Spawns("testBackground")
    public Entity testBackground(SpawnData data){
        return entityBuilder().view(new ScrollingBackgroundView(texture("forest.png").getImage(), getAppWidth(), getAppHeight())).build();
    }

    @Spawns("alienTank")
    public Entity newAlienTank(SpawnData data){
        Entity e = entityBuilder(data).type(Entities.ENEMY).viewWithBBox(new Rectangle(60, 60, Color.RED)).collidable().build();
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
}
