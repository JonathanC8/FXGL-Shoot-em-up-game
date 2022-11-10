package com.jonathanc8.fxglgame;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.TimerAction;
import com.almasb.fxgl.ui.FXGLButton;
import com.jonathanc8.fxglgame.components.PlayerControl;
import com.jonathanc8.fxglgame.components.Stats;
import com.jonathanc8.fxglgame.components.Teams;
import com.jonathanc8.fxglgame.factories.ShmupFactory;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Game extends GameApplication {
    private Entity player;
    private Entity player2;
    private Entity enemy;
    private Entity bullet;
    private Viewport viewport;
    private double increment = 6000;
    private int camSpeed = -2;
    private int moveSpeed = 8;
    private boolean isPlayer2 = false;
    private int score = 0;
    private boolean alive;


    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1600);
        settings.setHeight(900);
        settings.setVersion("Early Development");
        settings.setTitle("A game without a name");
        settings.setManualResizeEnabled(true);
        settings.setScaleAffectedOnResize(true);
        settings.setMainMenuEnabled(true);
        //settings.setGameMenuEnabled(true);

        settings.setSceneFactory(new MainSceneFactory());
    }

    @Override
    protected void initGame(){
        getGameWorld().addEntityFactory(new ShmupFactory());

        entityBuilder().at(0, 600).view(texture("Untitled.png")).rotate(-90).buildAndAttach();

        player = spawn("player", new SpawnData().put("color", Color.BLUE));
        alive = true;

        viewport = getGameScene().getViewport();

        startLevel();
    }

    @Override
    protected void initInput(){
        Input input = FXGL.getInput();

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction(){
                if(player.isActive())
                    player.getComponent(PlayerControl.class).onMove(new Vec2(moveSpeed, 0));
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction(){
                if(player.isActive())
                    player.getComponent(PlayerControl.class).onMove(new Vec2(-moveSpeed, 0));
            }
        }, KeyCode.A);


        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction(){
                if(player.isActive())
                    player.getComponent(PlayerControl.class).onMove(new Vec2(0, moveSpeed));
            }
        }, KeyCode.S);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction(){
                if(player.isActive())
                    player.getComponent(PlayerControl.class).onMove(new Vec2(0, -moveSpeed));
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
                player2.getComponent(PlayerControl.class).onMove(new Vec2(moveSpeed, 0));
        });

        onKey(KeyCode.LEFT, () -> {
            if(isPlayer2)
                player2.getComponent(PlayerControl.class).onMove(new Vec2(-moveSpeed, 0));
        });

        onKey(KeyCode.UP, () -> {
            if(isPlayer2)
                player2.getComponent(PlayerControl.class).onMove(new Vec2(0, -moveSpeed));
        });

        onKey(KeyCode.DOWN, () -> {
            if(isPlayer2)
                player2.getComponent(PlayerControl.class).onMove(new Vec2(0, moveSpeed));
        });

        onKeyDown(KeyCode.DELETE, () -> {
            if(isPlayer2 && player2.isActive())
                spawn("bullet", new SpawnData(player2.getCenter().getX(), player2.getCenter().getY()-25).put("dir",
                    new Point2D(0,-10)).put("side", 0));
        });

        input.addAction(new UserAction("Shoot") {
            @Override
            protected void onAction() {
                if(player.isActive()) {
                    player.getComponent(PlayerControl.class).shoot();
                }
            }
        }, KeyCode.SPACE);

        onKeyDown(KeyCode.E, () -> {
            viewport.bindToEntity(player, getAppCenter().getX() , getAppCenter().getY());
        });

    }

    @Override
    protected void initPhysics(){

        onCollision(EntityTypes.PLAYER, EntityTypes.ENEMY, (player, enemy) -> {
            player.getComponent(Stats.class).health -= enemy.getComponent(Stats.class).damage;
            enemy.getComponent(Stats.class).health -= player.getComponent(Stats.class).damage;
        });

        onCollision(EntityTypes.ENEMY, EntityTypes.PROJECTILE, (enemy, projectile) -> {
            if(projectile.getComponent(Teams.class).side == 0){
                enemy.getComponent(Stats.class).health -= projectile.getComponent(Stats.class).damage;
                projectile.removeFromWorld();
                score++;
            }
        });

        onCollision(EntityTypes.PLAYER, EntityTypes.PROJECTILE, (player, projectile) -> {
            if(projectile.getComponent(Teams.class).side == 1){
                spawn("explosion", new SpawnData(projectile.getCenter().subtract(8, 50)).put("entity", "missile"));
                player.getComponent(Stats.class).health -= projectile.getComponent(Stats.class).damage;
                projectile.removeFromWorld();

            }
        });

        onCollision(EntityTypes.PROJECTILE, EntityTypes.PROJECTILE, (projectile1, projectile2) -> {
            if(projectile1.getComponent(Teams.class).side != projectile2.getComponent(Teams.class).side){
                spawn("explosion", new SpawnData(projectile1.getCenter()).put("entity", "missile"));
                play("explosion small.wav");
                projectile1.removeFromWorld();
                projectile2.removeFromWorld();

            }
        });
    }

    @Override
    protected void initUI(){
        Text score = new Text();
        score.setTranslateX(50);
        score.setTranslateY(100);
        score.setStroke(Color.RED);
        score.setFont(Font.font(20));
        score.textProperty().bind(FXGL.getWorldProperties().intProperty("score").asString().concat(" Points"));

        finish.setTranslateX((getAppWidth()/3)+100);
        finish.setTranslateY(getAppHeight()/2);
        finish.setFont(Font.font(100));
        finish.setStroke(Color.BLUE);
        finish.setFill(Color.BLUE);
        finish.setText("FINISH!");
        finish.setVisible(false);

        getGameScene().addUINode(finish);
        getGameScene().addUINode(score);

    }
    @Override
    protected void onUpdate(double tpf){
        if(alive){
            viewport.setY(increment);
            increment += camSpeed;
            player.translateY(camSpeed);
        }
        if(viewport.getY() <= -500){
            increment = 8000;
            levelEnd();
        }
    }
    private Text finish = new Text();
    protected void levelEnd(){
        finish.setVisible(true);
        getGameTimer().runOnceAfter(() -> {
            finish.setVisible(false);
            startLevel();
        }, Duration.seconds(3));
    }

    protected void nextWave(){

    }

    protected void startLevel(){
        cleanUp();
        if(!player.isActive())
            player = spawn("player", new SpawnData().put("color", Color.BLUE));
        if(isPlayer2)
            if(!player2.isActive()){
                player2 = spawn("player", new SpawnData(player.getCenter().getX()+100, player.getCenter().getY()).put("color", Color.PINK));
            }
    }

    public void cleanUp(){
        ArrayList<Entity> entities = getGameWorld().getEntities();
        for(int i=0; entities.size() > i; i++) {
            entities.get(i).removeFromWorld();
        }
        resetLevel();
    }
    protected void resetLevel(){
        increment = 6000;
        setLevelFromMap("testLevelMap.tmx");

    }

    @Override
    protected void initGameVars(Map<String, Object> vars){
        vars.put("score", 0);
    }
    public static void main(String[] args){
        launch(args);
    }
}
