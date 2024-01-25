package app.game.model;

import app.game.api.MenuController;
import app.player.api.Player;
import app.player.impl.PlayerImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Implementation of MenuController with its logic.
 */
public class MenuControllerImpl implements MenuController {
    private List<Player> players;
    private static final String FILE_NAME = "saved_games.txt";
    private static final int MIN_NUM_PLAYER = 2;
    private static final int MAX_NUM_PLAYER = 5;

    /**
     * Constructs a new MenuControllerImpl.
     * This constructor is responsible for initializing the list of players for the game.
     */
    public MenuControllerImpl() {
        this.players = new ArrayList<>();
    }

    /**
     * Start a new game with the provided list of players.
     * 
     * @param players the list of players participating in the game
     * @return true if the game is successfully started, false otherwhise
     */
    @Override
    public boolean startGame(final List<Player> players) {
        if (players.size() < MIN_NUM_PLAYER || players.size() > MAX_NUM_PLAYER) {
            return false;
        }

        this.players = players;
        System.out.println("Start game...");
        return true;
    }

    /**
     * Quits the current game.
     */
    @Override
    public void quitGame() {
        System.out.println("...Exit game");
        System.exit(0);
    }

    /**
     * Inserts new players.
     * 
     * @param currentPlayerNames the list of the player names to be inserted
     * @return a list of player objects representing the inserted players for the current game
     */
    @Override
    public List<Player> insertPlayers(final List<String> currentPlayerNames) {
        final List<Player> currentPlayers = new ArrayList<>();

        for (int i = 0; i < currentPlayerNames.size(); i++) {
            final String playerName = currentPlayerNames.get(i);
            if (playerName == null || playerName.isEmpty()) {
                return new ArrayList<>();
            }

            final Player player = new PlayerImpl(i + 1, playerName);
            currentPlayers.add(player);
        }

        return currentPlayers;
    }

    /**
     * Saves the current state of the game to a file.
     */
    @Override
    public void saveGame() {
        try {
            if (players != null && !players.isEmpty()) {
                saveGameToFile(players);
                System.out.println("Partita salvata con successo!");
            } else {
                throw new Exception("Impossibile salvare la partita. Inizia una partita prima di salvare.");
            }
        } catch (Exception e) {
            System.out.println("Errore durante il salvataggio del gioco: " + e.getMessage());
        }
    }

    private void saveGameToFile(final List<Player> players) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter("saved_games.txt", true))) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy  HH:mm:ss", Locale.ITALY);
            final String timestamp = dateFormat.format(new Date());
            writer.println("*** Partita del: " + timestamp + " ***");

            for (final Player currentPlayer : players) {
                writer.println("Player: " + currentPlayer.getName()
                               + ", Id: " + currentPlayer.getId()
                               + ", Posizione: " + currentPlayer.getCurrentPosition()
                               + ", Denaro: " + currentPlayer.getMoney());
            }
            writer.println("\n");
        } catch (IOException e) {
            System.out.println("Errore durante il salvataggio del gioco: " + e.getMessage());
        }
    }

    /**
     * Displays the data of the saved games.
     * 
     * @return a list of strings representing the data of the saved games
     */
    @Override
    public List<String> viewSavedGames() {
        final List<String> savedGames = new ArrayList<>();

        final File file = new File(FILE_NAME);
        if (!file.exists()) {
            return savedGames;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                savedGames.add(line);
            }
            System.out.println("Caricamento partite salvate...");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore durante il caricamento delle partite salvate.");
        }

        return savedGames;
    }
}
