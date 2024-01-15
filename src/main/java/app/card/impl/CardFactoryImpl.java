package app.card.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import app.card.apii.Buildable;
import app.card.apii.Buyable;
import app.card.apii.Card;
import app.card.apii.CardFactory;
import app.card.apii.Unbuyable;
import app.card.utils.JsonReader;
import app.card.utils.StaticActions;
import app.player.apii.Player;

public class CardFactoryImpl implements CardFactory {
    @Override
    public List<Card> cardsInitializer() throws IOException {
        final var allCards = new ArrayList<Card>();
        final String SEP = File.separator;
        final String FILE_NAME = System.getProperty("user.dir") + SEP + "src" + SEP + "main" 
            + SEP + "java" + SEP + "app" + SEP + "card" + SEP + "utils" + SEP + "cardList.json";
        final var jsonList = JsonReader.readJson(FILE_NAME);
        jsonList.forEach(i -> {
            var type = i.getString("tipology");
            switch (type) {
                case "static":
                    allCards.add(createStaticCard(
                        Integer.valueOf(i.getString("id")),
                        i.getString("name"),
                        i.getString("action")));
                    break;
                case "property":
                    allCards.add(createProperty(
                        Integer.valueOf(i.getString("id")),
                        i.getString("name"),
                        Integer.valueOf(i.getString("price")),
                        Integer.valueOf(i.getString("housePrice")),
                        Integer.valueOf(i.getString("fees"))
                    ));
                    break;
                case "station":
                    allCards.add(createStation(
                        Integer.valueOf(i.getString("id")),
                        i.getString("name"),
                        Integer.valueOf(i.getString("price")),
                        Integer.valueOf(i.getString("fees"))
                    ));
                    break;
            
                default:
                    break;
            }
        });
        return allCards;
    }

    @Override
    public Buildable createProperty(final int id, final String name, final int price, final int housePrice, final int fees) {
        return new Buildable() {

            private Optional<Player> owner = Optional.empty();

            @Override
            public int getPrice() {
                return price;
            }

            @Override
            public Boolean isOwned() {
                return this.owner.isPresent();
            }

            @Override
            public Boolean isOwnedByPlayer(final Player player) {
                return isOwned() && owner.get().equals(player);
            }

            @Override
            public Player getOwner() {
                return this.owner.get();
            }

            @Override
            public int getTransitFees() {
                return fees;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getId() {
                return id;
            }

            @Override
            public int getHousePrice() {
                return price;
            }

            @Override
            public void setOwner(Player player) {
                this.owner = Optional.of(player);
            }
            
        };
    }

    @Override
    public Buyable createStation(final int id, final String name, final int price, final int fees) {
        return createProperty(id, name, price, 0, fees);
    }

    @Override
    public Unbuyable createStaticCard(final int id, final String name, final String func) {
        return new Unbuyable() {

            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getId() {
                return id;
            }

            @Override
            public void makeAction(final Player player) {
                try {
                    final String methodName = func;
                    final Class<?> clazz = StaticActions.class;
                    System.out.println(clazz);
                    final Method method = clazz.getMethod(methodName, Player.class);
                    method.invoke(null,player);  // Passa null come oggetto in quanto il metodo è statico
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
        };
    }

}
