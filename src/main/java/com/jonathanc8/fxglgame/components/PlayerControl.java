package com.jonathanc8.fxglgame.components;

import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.jonathanc8.fxglgame.Entities;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.*;
public class PlayerControl extends Component {
    private Entity player;
    private Viewport viewport;
    public double shootSpeed = 20;
    public double timer = shootSpeed;
    public double shootDamage;
    public int specialAmmo;
    public Entities specialType;
    public  boolean keepShoot = false;
    public PlayerControl(Entity player, double shootSpeed){
        this.player = player;
        this.viewport = getGameScene().getViewport();
        shootDamage = 1;
        specialAmmo = 0;
    }

    public void onMove(Vec2 dir){
        if(player.getRightX() >= getAppWidth()){
            player.translateY(dir.y);
            if(dir.x < 0){
                player.translateX(dir.x);
            }
        } else if(player.getRightX()- 25 <= 0){
            player.translateY(dir.y);
            if(dir.x > 0){
                player.translateX(dir.x);
            }
        } else{
            player.translateX(dir.x);
        }

        if(player.getBottomY() >= viewport.getY()+900){
            player.translateX(dir.x);
            if(dir.y < 0){
                player.translateY(dir.y);
            }
        } else if(player.getBottomY() - 25 <= viewport.getY()){
            player.translateX(dir.x);
            if(dir.y > 0){
                player.translateY(dir.y);
            }
        } else{
            player.translateY(dir.y);
        }

    }

    public void shoot(){
        if(timer >= shootSpeed){
            play("blaster.wav");
            spawn("bullet", new SpawnData(player.getCenter().getX(), player.getCenter().getY()-25).put("dir",
                    new Point2D(0,-10)).put("side", 0));
        timer = 0;
        }
    }

    @Override
    public void onUpdate(double tpf){
        if(timer <= shootSpeed)
        timer++;
    }
}
