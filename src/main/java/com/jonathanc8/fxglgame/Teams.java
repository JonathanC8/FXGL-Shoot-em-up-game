package com.jonathanc8.fxglgame;

import com.almasb.fxgl.entity.component.Component;

public class Teams extends Component {
    public int side;
    public Teams(int side){
        this.side = side;
    }
}
