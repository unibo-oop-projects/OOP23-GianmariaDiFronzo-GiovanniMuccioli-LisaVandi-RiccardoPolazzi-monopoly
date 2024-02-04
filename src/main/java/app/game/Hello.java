package app.game;

import game.view.GameView;

/**
 * The main class for launching the game application.
 * This class contains the main method responsible for starting the game
 * by creating an istance of {@link GameView} class 
 * after an istance of {@link MenuView} class.
 * 
 * To start the application, simply run the {@code main} method of this class.
 */
public final class Hello {

    private Hello() { }
    /**
     * The main method to launch the game application.
     * 
     * @param args command-line arguments (not used in this application)
     */
    public static void main(final String[] args) {
        new GameView();
    }
}
