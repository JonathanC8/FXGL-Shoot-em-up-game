package com.jonathanc8.fxglgame.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.jonathanc8.fxglgame.Entities;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static com.almasb.fxgl.dsl.FXGL.*;
public class HomingMissileComponent extends Component {
    private Entities targetSide;
    private Entity target;
    private Entity missile;
    private int speed;
    private int turnSpeed = 1;
    private double timeBomb = 0;
    private double targetAngle = 0;
    public HomingMissileComponent(Entities side, int speed){
        if(side == Entities.PLAYER){
            targetSide = Entities.ENEMY;
        }

        if(side == Entities.ENEMY){
            targetSide = Entities.PLAYER;
        }
        missile = this.getEntity();
        this.speed = speed;

    }
    private double missileAngle;
    protected void followTarget(){
        if(getTarget()){
            Entity missile = this.getEntity();
            double relativeX = missile.getX() - target.getX();
            double relativeY = missile.getY() - target.getY();
            targetAngle = FXGLMath.atan2(relativeX, relativeY);
            targetAngle = -FXGLMath.toDegrees(targetAngle);
            if(targetAngle < 0 ){
                targetAngle+=360;
            }
            missileAngle = missile.getRotation();
            if(targetAngle < (missileAngle+10)){
                missile.rotateBy(-turnSpeed);
            }
            if(targetAngle > (missileAngle-10)){
                missile.rotateBy(+turnSpeed);
            }
            missile.translateX(Vec2.fromAngle(missile.getRotation()-90).mulLocal(speed).x);
            missile.translateY(Vec2.fromAngle(missile.getRotation()-90).mulLocal(speed).y);
        }
    }

    protected boolean getTarget(){
            if(target == null){
                List<Entity> targets = getGameWorld().getEntitiesByType(targetSide);
                if(targets.size() > 1){
                    int randomInt = (int)(Math.random()*targets.size());
                    target = targets.get(randomInt);
                    return true;
                }
                if(targets.size() == 1){
                    target = targets.get(0);
                    return true;
                }
            }
            if(target.isActive()){
                return true;
            }
        return false;
    }

    @Override
    public void onUpdate(double tpf){
        followTarget();
        timeBomb+= tpf;
        if(timeBomb >= 3){
            spawn("explosion", new SpawnData(this.getEntity().getCenter().subtract(8, 70)).put("entity", "missile"));
            play("explosion medium.wav");
            this.getEntity().removeFromWorld();
        }
    }

}
