package app.player.view;

import javax.swing.JOptionPane;

import app.player.api.BailLogic;
import app.player.api.Player;
import app.player.impl.BailLogicImpl;

/**
 * Graphical implementation of the bail for prison.
 */ 
public final class BailView {

    private BailLogic logic = new BailLogicImpl();

    /**
     * Method which gives a player the possibility to choose 
     * between paying an amount of money to go out of jail or not.
     * @param player
     */
    public void showMenuBail(final Player player) {
        String message = "Would you like to pay " + BailLogicImpl.DEFAULT_PAYMENT 
            + "$ to go out of prison? You have " + player.getBankAccount().getBalance() + "$ on your BankAccount.";
        int choice = JOptionPane.showConfirmDialog(null, message, "YOU ARE IN PRISON!", 
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            if (logic.hasPayed(player)) {
                JOptionPane.showMessageDialog(null, "You have payed the bail, OUT OF PRISON!",
                "FREE TO GO", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Not enough money for paying the bail. STAYING IN PRISON!",
                "STILL IN PRISON", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            logic.notPayed(player);
            JOptionPane.showMessageDialog(null, "You have decided not to pay the bail. STAYING IN PRISON FOR ONE SHIFT!",
            "STILL IN PRISON", JOptionPane.ERROR_MESSAGE);
        }
    }
}