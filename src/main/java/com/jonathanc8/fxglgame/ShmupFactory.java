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
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ShmupFactory implements EntityFactory {
    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        return entityBuilder(data).type(Entities.PLAYER).viewWithBBox(new Rectangle(25, 25, Color.BLUE)).collidable().at(300,500).build();
    }

    @Spawns("bullet")
    public Entity newBullet(SpawnData data){
        Point2D dir = data.get("dir");
        Entities bullet = data.get("side");
        return entityBuilder(data).type(bullet).with(new ProjectileComponent( dir, 500 ))
                .with(new TimeComponent())
                .with(new OffscreenCleanComponent())
                .viewWithBBox(new Rectangle(5,7, Color.DARKGRAY))
                .collidable()
                .build();
    }

    @Spawns("enemy")
    public Entity newEnemy(SpawnData data){
        return entityBuilder(data).type(Entities.ENEMY).viewWithBBox(new Rectangle(60, 60, Color.RED)).collidable().build();
    }

    @Spawns("testBackground")
    public Entity testBackground(SpawnData data){
        return entityBuilder().view(new ScrollingBackgroundView(texture("forest.png").getImage(), getAppWidth(), getAppHeight())).build();
    }

    @Spawns("tank")
    public Entity newTank(SpawnData data){
        return null;
    }

    @Spawns("helicopter")
    public Entity newHeli(SpawnData data){
        return null;
    }

    @Spawns("artillery")
    public Entity newArtie(SpawnData data){
        return null;
    }

    @Spawns("antiair")
    public Entity newAA(SpawnData data){
        return null;
    }
}
