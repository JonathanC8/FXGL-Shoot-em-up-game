package com.jonathanc8.fxglgame.components;

import com.almasb.fxgl.entity.component.Component;
import com.jonathanc8.fxglgame.Entities;

public class Teams extends Component {
    public int side;
    public Teams(int side){
        this.side = side;
    }

    public Teams(Entities side){
        if(side == Entities.ENEMY){
            this.side = 1;
        }
    }
}
