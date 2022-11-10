package com.jonathanc8.fxglgame;

public enum ItemTypes {
    MISSILE(5, 3),
    LASER(3, 0),
    PLASMA_CANNON(1, 1500);


    public int damage;
    public int speed;
    ItemTypes(int damage, int speed){
        this.damage = damage;
        this.speed = speed;
    }
}
