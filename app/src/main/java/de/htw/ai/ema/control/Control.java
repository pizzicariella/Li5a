package de.htw.ai.ema.control;

import java.util.List;

import de.htw.ai.ema.model.Card;
import de.htw.ai.ema.model.Player;

public interface Control {

    /**
     * This method starts the game after the game object has been initialized.
     */
    public void startGame();

    /**
     * This method calls all processes that are necessary to start a new Game Round, for example
     * assigning cards to players.
     */
    public void startGameRound();

    /**
     * This method starts a new Cycle in a GameRound.
     */
    public Player startCycle();

    /**
     * This method takes a List of cards selected by the user in the view and passes them to the
     * next player by triggering corresponding changes in Model.
     * @param selected
     */
    public void passCards(List<Card> selected);

    /**
     * This method takes the Card the User selected and puts it on the stack object of the current
     * cycle.
     * @param card
     */
    public void playCard(Card card);

    /**
     * This method cancels the running game.
     */
    public void cancel();
}
