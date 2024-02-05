package app.player.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

import app.card.api.Buildable;
import app.card.api.Buyable;
import app.card.api.Card;
import app.player.api.BankAccount;
import app.player.api.Player;

/**
 * Class which implements a Player.
 */
public class PlayerImpl implements Player {
    /**
     * Maximum number of houses a player can build on a box.
     */
    private static final int MAX_NUMBER_HOUSES = 3; 
    /**
     * Map which associates each Card box with an Optional that indicates
     * if that box is already owned by the current player 
     * and the number of houses built on that box.
     */
    private Map<Card, Optional<Integer>> map;
    private int currentPosition;
    private String name;
    private int id;
    private BankAccount account; 

    /**
     * @param name
     * @param Id
     * @param cards
     * @param initialAmount
     * In order to avoid errors with the method map.get(), in case the map isn't initialized,
     * this constructor sets the values of the map with Optional.empty().
     */
    public PlayerImpl(final String name, final int Id, final List<Card> cards, final int initialAmount) {
        this.name = name; 
        this.id = Id;
        this.map = new HashMap<>();
        for (Card box : cards) {
            this.map.put(box, Optional.empty());
        }
        this.account = new BankAccountImpl(initialAmount);
    }

    @Override
    public int getCurrentPosition() {
        return this.currentPosition;
    }

    @Override
    public void setPosition(final int position) {
       this.currentPosition = position; 
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getNumberStationOwned() {
        int cont = 0;
        for (Card box : map.keySet()) {
            if (map.get(box).isPresent() && (box.isBuyable() == true && box.isBuildable() == false)) {
                cont += 1; 
            }
        }
        return cont;
    }
    
    @Override
    public BankAccount getBankAccount() {
        return this.account;
    }

    @Override
    public List<Buyable> getBuyableOwned() {
        List<Buyable> buyableOwned = new LinkedList<>(); 
        for (Card box : map.keySet()) {
            if (map.get(box).isPresent() && (box.isBuyable() == true)) {
                // cast eseguibile perché ho appena controllato che la Card sia di tipo Buyable
                buyableOwned.add((Buyable) box); 
            }
        }
        return buyableOwned;
    }

    @Override
    public List<Buildable> getBuildableOwned() {
        List<Buildable> buildableOwned = new LinkedList<>(); 
        for (Card box : map.keySet()) {
            if (map.get(box).isPresent() && (box.isBuildable() == true)) {
                // cast eseguibile perché ho appena controllato che la Card sia di tipo Buildable
                buildableOwned.add((Buildable) box); 
            }
        }
        return buildableOwned;
    }

    @Override
    public Optional<Integer> getHouseBuilt(final Buildable built) {
        // chi usa questo metodo, si dovrà preoccupare di verificare che il player possieda la casella 
        return map.get(built); 
    }

    @Override
    public void buyBox(final Buyable box) {
        if (map.get(box).isPresent()) {
            return; 
        } 
        // pago la banca del costo della casella
        account.payPlayer(null, box.getPrice());
        map.put(box, Optional.of(0)); // 0 è diverso da Empty, quindi possiedo la casella con 0 case costruite
    }

    @Override
    public void buildHouse(final Buildable box) throws IllegalArgumentException {
        // box = (Buyable) box
        if (map.get(box).isEmpty()) {
            throw new IllegalArgumentException("You're trying to build a house on a box you don't own"); 
        }
        if (map.get(box).get() >= MAX_NUMBER_HOUSES) {
            throw new IllegalArgumentException("Already built maximum number of houses on this box");
        }
        // Aggiungo alla casella acquistata il numero di case incrementato di 1
        map.put(box, Optional.of(map.get(box).get() + 1));
    }

    /**
     * @param box current box
     * @return value of the current box, obtained by the sum of box's price
     *          with the number of houses built by the player on that box
     * This method's visibility is private, because it must not be used by anyone else.
     */
    private int boxValue(final Buyable box) {
        if (!(box instanceof Buildable)) {
            return box.getPrice();
        }
        Buildable newBox = (Buildable) box;
        Optional<Integer> numberHouses = getHouseBuilt(newBox);
        if (numberHouses.isEmpty()) {
            // lancia eccezione se il player non possiede la casella. 
            // Infatti nella mappa si associa la casella a un opzionale che, 
            // se è empty, indica che non la si possiede. 
            throw new IllegalArgumentException("Player doesn't own the current box.");
        }
        return newBox.getPrice() + numberHouses.get() * newBox.getHousePrice();
    }

    @Override
    public void sellBuyable(final Buyable box) {
        this.account.receivePayment(boxValue(box));    
        // lo eseguo dopo perché altrimenti rimuovo prima di contare quante case ho sopra
        map.put(box, Optional.empty());
    }

    @Override
    public boolean isInJail(final Card box) {
        return false;     
    }
}
