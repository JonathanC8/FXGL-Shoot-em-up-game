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
import com.jonathanc8.fxglgame.components.PlayerControl;
import com.jonathanc8.fxglgame.components.Teams;
import com.jonathanc8.fxglgame.factories.ShmupFactory;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
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
    private boolean devMode = false;


    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1600);
        settings.setHeight(900);
        settings.setVersion("Early Development");
        settings.setTitle("A game without a name");
        settings.setScaleAffectedOnResize(true);
        //settings.setMainMenuEnabled(true);
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
        //testBedMode();
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

        onCollision(Entities.PLAYER, Entities.ENEMY, (player, enemy) -> {
            player.removeFromWorld();
            cleanUp();
        });

        onCollision(Entities.ENEMY, Entities.PROJECTILE, (enemy, bullet) -> {
            if(bullet.getComponent(Teams.class).side == 0){
                spawn("explosion", new SpawnData(enemy.getCenter().subtract(100, 100)).put("entity", "enemy"));
                play("explosion huge.wav");
                enemy.removeFromWorld();
                bullet.removeFromWorld();
                inc("score", 1);
                score++;
            }
        });

        onCollision(Entities.PLAYER, Entities.PROJECTILE, (player, bullet) -> {
            if(bullet.getComponent(Teams.class).side == 1){
                spawn("explosion", new SpawnData(player.getCenter().subtract(70, 80)).put("entity", "player"));
                spawn("explosion", new SpawnData(bullet.getCenter().subtract(8, 50)).put("entity", "missile"));
                play("explosion medium.wav");
                alive = false;
                player.removeFromWorld();
                bullet.removeFromWorld();
                getGameController().gotoGameMenu();

            }
        });

        onCollision(Entities.PROJECTILE, Entities.PROJECTILE, (projectile1, projectile2) -> {
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
        score.textProperty().bind(FXGL.getWorldProperties().intProperty("score").asString().concat(" Points"));

        getGameScene().addUINode(score);
    }


    @Override
    protected void onUpdate(double tpf){
        if(alive && !devMode){
            viewport.setY(increment);
            increment += camSpeed;
            player.translateY(camSpeed);
        }

    }

    protected void testBedMode(){
        viewport.setY(6000);
        spawn("missile", new SpawnData(800, 6000).put("side", Entities.ENEMY));
        player.setY(6500);

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
