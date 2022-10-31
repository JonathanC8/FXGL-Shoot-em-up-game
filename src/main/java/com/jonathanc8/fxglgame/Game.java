package com.jonathanc8.fxglgame;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.views.ScrollingBackgroundView;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Game extends GameApplication {

    private enum Type{
        PLAYER, ENEMY
    }
    private Entity player;
    private Entity enemy;
    private Entity bullet;
    private Viewport viewport;


    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(600);
        settings.setHeight(600);
        settings.setVersion("0.01");
        settings.setTitle("Some Shmup game");


    }

    @Override
    protected void initGame(){
        getGameWorld().addEntityFactory(new ShmupFactory());
        entityBuilder().at(0, 600).view("Untitled.png").rotate(-90).buildAndAttach();

        player = spawn("player");

        viewport = getGameScene().getViewport();

        startLevel();

    }

    protected void startLevel(){
        spawn("enemy", new SpawnData(100, 200));
        spawn("enemy", new SpawnData(300, 150));
        spawn("enemy", new SpawnData(500, 200));

        spawn("enemy", new SpawnData(450, -50));
        spawn("enemy", new SpawnData(400, -200));
        spawn("enemy", new SpawnData(450, -350));
        spawn("enemy", new SpawnData(400, -500));

        spawn("enemy", new SpawnData(100, -550));
        spawn("enemy", new SpawnData(200, -300));

        spawn("enemy", new SpawnData(100, -900));
        spawn("enemy", new SpawnData(300, -950));
        spawn("enemy", new SpawnData(500, -900));

        spawn("enemy", new SpawnData(200, -1100));
        spawn("enemy", new SpawnData(400, -1100));

        spawn("enemy", new SpawnData(100, -1200));
        spawn("enemy", new SpawnData(300, -1250));
        spawn("enemy", new SpawnData(500, -1200));

    }

    @Override
    protected void initInput(){
        Input input = FXGL.getInput();

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction(){
                player.translateX(5);
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction(){
                player.translateX(-5);
            }
        }, KeyCode.A);


        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction(){
                player.translateY(5);
            }
        }, KeyCode.S);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction(){
                player.translateY(-5);
                /*
                if(tVelocity > 50 || tVelocity < -50){

                }
                dir.x += Vec2.fromAngle(player.getRotation() - 90).mulLocal(mul).x;
                dir.y += Vec2.fromAngle(player.getRotation() - 90).mulLocal(mul).y;
                tVelocity = Math.sqrt(dir.x*dir.x +dir.y*dir.y);
                inc("velocityX", (int)dir.x);
                inc("velocityY", (int)dir.y);
                 */
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Respawn") {
            @Override
            protected void onAction(){
                if(!player.isActive())
                    player = spawn("player");
            }
        }, KeyCode.ENTER);


        onKeyDown(KeyCode.SPACE, () -> {
            spawn("bullet", new SpawnData(player.getCenter().getX(), player.getCenter().getY()-25).put("dir",
                    new Point2D(0,-10)).put("side", Entities.BULLET));
        });
        //Easy way to add movement.
        /*
        FXGL.onKey(KeyCode.RIGHT, () -> {
            Enemy.translateX(5); // move right 5 pixels
         });

        FXGL.onKey(KeyCode.LEFT, () -> {
            Enemy.translateX(-5); // move left 5 pixels
        });
        */

        onKey(KeyCode.UP, () -> {

        });

        onKey(KeyCode.DOWN, () -> {

        });

        onKeyDown(KeyCode.E, () -> {
            viewport.bindToEntity(player, 300 , 300);
        } );

    }

    @Override
    protected void initUI(){
        Text score = new Text();
        score.setTranslateX(50);
        score.setTranslateY(100);
        score.textProperty().bind(FXGL.getWorldProperties().intProperty("score").asString().concat(" Points"));

        getGameScene().addUINode(score);
    }

    private Vec2 dir = new Vec2(0 ,0);
    private double increment = 0;
    private double speed = -2;
    @Override
    protected void onUpdate(double tpf){
        increment += speed;
        //player.translate(dir);
        if(getGameWorld().getEntitiesByType(Entities.ENEMY).size() < 3){

        }
        player.translateY(speed);
        viewport.setY(increment);
        if(viewport.getY() < -1600){
            player.setY(500);
            increment = 0;
            getGameWorld().removeEntities(getGameWorld().getEntitiesByType(Entities.ENEMY));
            startLevel();
        }

    }

    private int score = 0;
    @Override
    protected void initPhysics(){

        onCollision(Entities.PLAYER, Entities.BULLET, (player, bullet) -> {
            System.out.println("PLAYER HAS BEEN SHOT");
            player.removeFromWorld();
            getGameWorld().removeEntities(getGameWorld().getEntitiesByType(Entities.ENEMY));
        });

        onCollision(Entities.PLAYER, Entities.ENEMY, (player, enemy) -> {
            player.setY(500);
            increment = 0;
            getGameWorld().removeEntities(getGameWorld().getEntitiesByType(Entities.ENEMY));
            startLevel();
            inc("score", -score);
            score = 0;
        });

        onCollision(Entities.ENEMY, Entities.BULLET, (enemy, bullet) -> {
            System.out.println("ENEMY HAS BEEN SHOT");
            enemy.removeFromWorld();
            bullet.removeFromWorld();
            inc("score", 1);
            score++;
        });


    }

    @Override
    protected void initGameVars(Map<String, Object> vars){
        vars.put("score", 0);
    }

    public static void main(String[] args){
        launch(args);
    }
}
