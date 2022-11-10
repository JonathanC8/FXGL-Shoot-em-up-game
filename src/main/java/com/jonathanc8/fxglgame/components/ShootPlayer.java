package com.jonathanc8.fxglgame.components;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.jonathanc8.fxglgame.EntityTypes;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.*;
public class ShootPlayer extends Component {

    private Entity player;
    private Entity thisEnemy;
    private Point2D dir;
    private int time;
    private int randPlayer;
    private int health;
    public ShootPlayer(Entity thisEnemy){
        this.thisEnemy = thisEnemy;

    }
    @Override
    public void onUpdate(double tpf){
        time++;
        health = this.entity.getComponent(Stats.class).getHealth();
        if(health <= 0){
            spawn("explosion", new SpawnData(this.entity.getCenter().subtract(100, 100)).put("entity", "enemy"));
            play("explosion huge.wav");
            inc("score", 5);
            this.entity.removeFromWorld();
        }

        if(getGameWorld().getEntitiesByType(EntityTypes.PLAYER).size() > 1){
            randPlayer = (int)Math.round(Math.random());
            player = getGameWorld().getEntitiesByType(EntityTypes.PLAYER).get(randPlayer);
        } else if(getGameWorld().getEntitiesByType(EntityTypes.PLAYER).size() == 1){
            player = getGameWorld().getEntitiesByType(EntityTypes.PLAYER).get(0);
        }
        if(player != null){
            if(player.isActive()) {
                if (time == 50) {
                    dir = new Point2D(player.getCenter().getX() - thisEnemy.getCenter().getX(), player.getCenter().getY() - thisEnemy.getCenter().getY());
                }
                if ((player.getCenter().getY() > thisEnemy.getY() + 50 || player.getCenter().getY() < thisEnemy.getY()) && time > 100) {
                    time = 0;
                    thisEnemy.rotateToVector(dir);
                    thisEnemy.rotateBy(90);
                    spawn("homingMissile", new SpawnData(thisEnemy.getCenter().getX(), thisEnemy.getCenter().getY())
                            .put("side", EntityTypes.ENEMY));
                }
            }
        }
    }
}
