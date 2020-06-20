package de.htw.ai.ema.persistence.dao;

import android.app.Activity;

import java.util.List;

import de.htw.ai.ema.model.Game;

public interface DAO {

    /**
     * This methods saves a game object to storage.
     * @param game
     */
    public long saveGame(Game game);

    /**
     * This method loads a game from storage.
     * @param id
     */
    public Game loadGame(long id);

    /**
     * This method loads all games from storage.
     * @return
     */
    public List<Game> getAllGames();

    /**
     * This method updates an existing game object.
     * @param id
     * @param game
     */
    public void updateGame(int id, Game game);

    /**
     * This method deletes a game from storage.
     * @param id
     */
    public void deleteGame(int id);

}
