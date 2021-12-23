package org.thermo.test;


import org.thermo.core.EngineManager;
import org.thermo.core.WindowManager;
import org.thermo.core.utils.Constants;

public class Launcher {
    private static WindowManager window;
    private static TestGame game;

    public static void main(String[] args) {
        window = new WindowManager(Constants.TITLE, 1600, 900);
        game = new TestGame();
        EngineManager engine = new EngineManager();

        try {
            engine.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WindowManager getWindow() {
        return window;
    }

    public static TestGame getGame() {
        return game;
    }
}
