package com.jonathanc8.fxglgame;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.jonathanc8.fxglgame.menus.MainMenu;

public class MainSceneFactory extends SceneFactory {
    @Override
    public FXGLMenu newMainMenu(){
        return new MainMenu(MenuType.MAIN_MENU);
    }

    @Override
    public FXGLMenu newGameMenu(){
        return new MainMenu(MenuType.GAME_MENU);
    }


}
