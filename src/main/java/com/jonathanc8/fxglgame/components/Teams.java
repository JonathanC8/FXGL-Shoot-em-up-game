package com.jonathanc8.fxglgame.components;

import com.almasb.fxgl.entity.component.Component;
import com.jonathanc8.fxglgame.EntityTypes;

public class Teams extends Component {
    public int side;
    public Teams(int side){
        this.side = side;
    }

    public Teams(EntityTypes side){
        if(side == EntityTypes.ENEMY){
            this.side = 1;
        }
    }
}
