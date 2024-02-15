package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.card.apii.Card;
import app.card.apii.CardAdapter;
import app.game.apii.GameController;
import app.game.controller.GameControllerImpl;
import app.game.view.BtnCodeEnum;
import app.player.apii.Player;
import app.player.impl.PlayerImpl;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

class TestGameController {

    private GameController logic;
    private Map<BtnCodeEnum, Boolean> btnList;

    @BeforeEach
    void start() throws IOException {
        this.logic = new GameControllerImpl(List.of("mario", "luigi"));
    }

    @Test

    void testPlayer() {

        logic.newTurn();
        assertEquals("mario", logic.getCurrentPlayer().getName());
        logic.newTurn();
        assertEquals("luigi", logic.getCurrentPlayer().getName());
        logic.newTurn();
        assertEquals("mario", logic.getCurrentPlayer().getName());

    }

    @Test

    void testDice() {

        logic.newTurn();

        logic.setDiceValue(4);
        logic.startTurn();

        assertEquals(4, logic.getCurrentPlayer().getCurrentPosition());
        assertEquals("Viserba", logic.getCurrentCard().getName());

        logic.setDiceValue(20);
        logic.startTurn();

        assertEquals(0, logic.getCurrentPlayer().getCurrentPosition());
        assertEquals("GO", logic.getCurrentCard().getName());

        logic.newTurn();

        logic.setDiceValue(30);
        logic.startTurn();

        assertEquals(6, logic.getCurrentPlayer().getCurrentPosition());
        assertEquals("Transito", logic.getCurrentCard().getName());

    }

    @Test

    void testJail() {

        logic.newTurn();

        logic.setDiceValue(18);
        logic.startTurn();

        assertEquals(true, logic.isCurrentPlayerInJail());

    }

    @Test

    void testEnabledButtons() {

        logic.newTurn();

        this.btnList = new HashMap<>();
        btnList.put(BtnCodeEnum.buyHouse, false);
        btnList.put(BtnCodeEnum.buyPropriety, false);
        btnList.put(BtnCodeEnum.endTurn, false);
        btnList.put(BtnCodeEnum.rollDice, false);
        btnList.put(BtnCodeEnum.sellPropriety, false);

        assertEquals(btnList, logic.getBtnStatus());

        logic.setDiceValue(4);
        logic.startTurn();

        btnList.put(BtnCodeEnum.buyHouse, false);
        btnList.put(BtnCodeEnum.buyPropriety, true);
        btnList.put(BtnCodeEnum.endTurn, true);
        btnList.put(BtnCodeEnum.rollDice, false);
        btnList.put(BtnCodeEnum.sellPropriety, false);

        assertEquals(btnList, logic.getBtnStatus());

        logic.buyPropriety();

        logic.newTurn();
        logic.newTurn();
        logic.setDiceValue(0);
        logic.startTurn();

        btnList.put(BtnCodeEnum.buyHouse, true);
        btnList.put(BtnCodeEnum.buyPropriety, false);
        btnList.put(BtnCodeEnum.endTurn, true);
        btnList.put(BtnCodeEnum.rollDice, false);
        btnList.put(BtnCodeEnum.sellPropriety, true);

        assertEquals(btnList, logic.getBtnStatus());
        assertEquals(4, logic.getCurrentPlayer().getCurrentPosition());
    }

    @Test

    void buyProprietyTest() {

        logic.newTurn();

        assertEquals(500, logic.getCurrentPlayer().getBankAccount().getBalance());

        logic.setDiceValue(4);
        logic.startTurn();
        logic.buyPropriety();

        Player owner = CardAdapter.buildableAdapter(logic.getCurrentCard()).getOwner();

        assertEquals(owner, logic.getCurrentPlayer());

        assertEquals(400, logic.getCurrentPlayer().getBankAccount().getBalance());

    }

    @Test
    void buildHouseTest() {

        logic.newTurn();

        logic.setDiceValue(4);
        logic.startTurn();
        logic.buyPropriety();

        logic.newTurn();
        logic.newTurn();

        logic.buildHouse();

        assertEquals(logic, btnList);

    }

    @Test

    void payFeesTest() {
        logic.newTurn();
        logic.setDiceValue(4);
        logic.startTurn();
        logic.buyPropriety();

        Player owner = CardAdapter.buyableAdapter(logic.getCurrentCard()).getOwner();

        logic.newTurn();
        assertEquals(500, logic.getCurrentPlayer().getBankAccount().getBalance());

        logic.setDiceValue(4);
        logic.startTurn();

        int fees = CardAdapter.buyableAdapter(logic.getCurrentCard()).getTransitFees();

        logic.payFees(owner);

        assertEquals(500 - fees, logic.getCurrentPlayer().getBankAccount().getBalance());
        assertEquals(400 + fees, logic.getCurrentPlayer().getBankAccount().getBalance());

    }

    @Test

    void defeatTest() {
        logic.newTurn();
        logic.setDiceValue(4);
        logic.startTurn();
        logic.buyPropriety();

        logic.newTurn();
        logic.getCurrentPlayer().getBankAccount().setBalance(5);
        logic.setDiceValue(4);
        logic.startTurn();

        assertEquals(1, logic.getPlayerList().size());
        assertEquals(1, logic.getDefeatedList().size());
    }

}