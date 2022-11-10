package com.jonathanc8.fxglgame.components;

import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.core.View;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.jonathanc8.fxglgame.EntityTypes;
import javafx.geometry.Point2D;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GruntController extends Component {
    private int moveSpeed;
    private boolean visible = false;
    private int health;
    public GruntController(int moveSpeed){
        this.moveSpeed = moveSpeed;
    }

    public void gruntAI(){
        if(visible){
            this.entity.translateY(2);
        }
    }

    @Override
    public void onUpdate(double tpf){
        gruntAI();
        health = this.entity.getComponent(Stats.class).getHealth();
        Viewport cam = getGameScene().getViewport();
        if(this.entity.getY()+100 >= cam.getY()  && this.entity.getY() <= cam.getY()-20){
            visible = true;
        }
        if(health <= 0){
            spawn("explosion", new SpawnData(this.entity.getCenter().subtract(100, 100)).put("entity", "enemy"));
            play("explosion huge.wav");
            inc("score", 2);
            this.entity.removeFromWorld();
        }
    }
}
