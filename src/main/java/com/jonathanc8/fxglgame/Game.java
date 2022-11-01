package com.jonathanc8.fxglgame;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Game extends GameApplication {
    private Entity player;
    private Entity player2;
    private Entity enemy;
    private Entity bullet;
    private Viewport viewport;
    private double increment = 500;
    private double speed = -2;
    private int moveSpeed = 5;
    private boolean isPlayer2 = false;


    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1600);
        settings.setHeight(900);
        settings.setVersion("0.01");
        settings.setTitle("Some Shmup game");
    }

    @Override
    protected void initGame(){
        getGameWorld().addEntityFactory(new ShmupFactory());
        entityBuilder().at(500, 600).view("Untitled.png").rotate(-90).buildAndAttach();

        player = spawn("player", new SpawnData().put("color", Color.BLUE));

        viewport = getGameScene().getViewport();

        startLevel();

    }

    protected void startLevel(){
        if(!player.isActive())
            player = spawn("player", new SpawnData().put("color", Color.BLUE));
        if(isPlayer2)
            if(!player2.isActive()){
                player2 = spawn("player", new SpawnData(player.getCenter().getX()+100, player.getCenter().getY()).put("color", Color.PINK));
            }

        spawn("enemy", new SpawnData(600, 200));
        spawn("enemy", new SpawnData(800, 150));
        spawn("enemy", new SpawnData(1000, 200));

        spawn("enemy", new SpawnData(950, -50));
        spawn("enemy", new SpawnData(900, -200));
        spawn("enemy", new SpawnData(950, -350));
        spawn("enemy", new SpawnData(900, -500));

        spawn("enemy", new SpawnData(600, -550));
        spawn("enemy", new SpawnData(800, -300));

        spawn("enemy", new SpawnData(600, -900));
        spawn("enemy", new SpawnData(700, -950));
        spawn("enemy", new SpawnData(1000, -900));

        spawn("enemy", new SpawnData(700, -1100));
        spawn("enemy", new SpawnData(900, -1100));

        spawn("enemy", new SpawnData(600, -1200));
        spawn("enemy", new SpawnData(800, -1250));
        spawn("enemy", new SpawnData(1000, -1200));

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
            }
        }, KeyCode.W);

        onKeyDown(KeyCode.ENTER, () -> {
            if(!isPlayer2){
                player2 = spawn("player", new SpawnData(player.getCenter().getX()+200, player.getCenter().getY()).put("color", Color.PINK));
                isPlayer2 = true;
            }
        });

        onKey(KeyCode.RIGHT, () -> {
            if(isPlayer2)
                player2.translateX(moveSpeed);
        });

        onKey(KeyCode.LEFT, () -> {
            if(isPlayer2)
                player2.translateX(-moveSpeed);
        });

        onKey(KeyCode.UP, () -> {
            if(isPlayer2)
                player2.translateY(-moveSpeed);
        });

        onKey(KeyCode.DOWN, () -> {
            if(isPlayer2)
                player2.translateY(moveSpeed);
        });

        onKeyDown(KeyCode.DELETE, () -> {
            if(isPlayer2 && player2.isActive())
                spawn("bullet", new SpawnData(player2.getCenter().getX(), player2.getCenter().getY()-25).put("dir",
                    new Point2D(0,-10)).put("side", 0));
        });


        onKeyDown(KeyCode.SPACE, () -> {
            if(player.isActive())
                spawn("bullet", new SpawnData(player.getCenter().getX(), player.getCenter().getY()-25).put("dir",
                        new Point2D(0,-10)).put("side", 0));
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


    @Override
    protected void onUpdate(double tpf){
        increment += speed;

        player.translateY(speed);
        if(isPlayer2)
            player2.translateY(speed);
        viewport.setY(increment);
        if(viewport.getY() < -1600){
            player.setY(800);
            increment = 500;
            getGameWorld().removeEntities(getGameWorld().getEntitiesByType(Entities.ENEMY));
            startLevel();
        }

    }

    private int score = 0;
    @Override
    protected void initPhysics(){

        onCollision(Entities.PLAYER, Entities.ENEMY, (player, enemy) -> {
            player.removeFromWorld();
            resetLevel();
        });

        onCollision(Entities.ENEMY, Entities.BULLET, (enemy, bullet) -> {
            if(bullet.getComponent(Teams.class).side == 0){
                enemy.removeFromWorld();
                bullet.removeFromWorld();
                inc("score", 1);
                score++;
            }
        });

        onCollision(Entities.PLAYER, Entities.BULLET, (player, bullet) -> {
            if(bullet.getComponent(Teams.class).side == 1){
                player.removeFromWorld();
                resetLevel();
            }
        });


    }

    protected void resetLevel(){
        if(getGameWorld().getEntitiesByType(Entities.PLAYER).size() <1){
            if(isPlayer2){
                player2.setY(800);
                player2.setX(1000);
            }
            player.setY(800);
            player.setX(800);
            increment = 500;
            getGameWorld().removeEntities(getGameWorld().getEntitiesByType(Entities.ENEMY));
            startLevel();
            inc("score", -score);
            score = 0;
        }
    }

    @Override
    protected void initGameVars(Map<String, Object> vars){
        vars.put("score", 0);
    }

    public static void main(String[] args){
        launch(args);
    }
}
