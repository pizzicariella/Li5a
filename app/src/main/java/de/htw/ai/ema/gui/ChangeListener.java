package de.htw.ai.ema.gui;

import java.util.List;

import de.htw.ai.ema.model.Card;

public interface ChangeListener {

    /**
     * This method can be implemented to define actions in case a card is added.
     * @param c
     */
    public void onCardAdd(Card c);

    /**
     * This method can be implemented to define actions in case a List of Card is added.
     * @param cards
     */
    public void onCardSetChange(List<Card> cards);
}
