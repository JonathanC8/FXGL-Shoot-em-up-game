package com.jonathanc8.fxglgame.components;

import com.almasb.fxgl.entity.component.Component;
import com.jonathanc8.fxglgame.EntityTypes;

public class Stats extends Component {
    public int health;
    public int damage;
    public int speed;
    public int turnSpeed;
    public EntityTypes team;
    public Stats(int health, int damage, int speed, int turnSpeed, EntityTypes team){
        this.health = health;
        this.damage = damage;
        this.speed = speed;
        this.turnSpeed = turnSpeed;
        this.team = team;
    }

    public Stats(int health, int damage, int speed, EntityTypes team){
        this.health = health;
        this.damage = damage;
        this.speed = speed;
        this.team = team;
    }

    public int getHealth(){
        return health;
    }

}
