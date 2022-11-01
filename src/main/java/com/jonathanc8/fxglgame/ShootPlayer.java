package com.jonathanc8.fxglgame;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.*;
public class ShootPlayer extends Component {

    private Entity player;
    private Entity thisEnemy;
    private Point2D dir;
    private int time;
    private int randPlayer;
    protected ShootPlayer(Entity thisEnemy){
        this.thisEnemy = thisEnemy;
    }
    @Override
    public void onUpdate(double tpf){
        time++;
        if(getGameWorld().getEntitiesByType(Entities.PLAYER).size() > 1){
            randPlayer = (int)Math.round(Math.random());
            player = getGameWorld().getEntitiesByType(Entities.PLAYER).get(randPlayer);
        } else{
            player = getGameWorld().getEntitiesByType(Entities.PLAYER).get(0);
        }
        if(time == 35){
            dir = new Point2D(player.getCenter().getX()-thisEnemy.getCenter().getX(), player.getCenter().getY()-thisEnemy.getCenter().getY());
        }
        if((player.getCenter().getY() > thisEnemy.getY()+50 || player.getCenter().getY() < thisEnemy.getY()) && time > 50){
            time = 0;
            spawn("bullet", new SpawnData(thisEnemy.getCenter().getX(), thisEnemy.getCenter().getY())
                    .put("dir", dir).put("side", 1));
        }
    }
}
