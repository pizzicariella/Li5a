package de.htw.ai.ema.persistence.dao;

import java.util.Map;

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
    public Game loadGame(long id) throws Exception;

    /**
     * This method loads characteristics of all games, including id, from storage.
     * @return
     */
    public Map<Long, String> getAllGameIds();

    /**
     * This method updates an existing game object.
     * @param id
     * @param game
     * @return
     */
    public boolean updateGame(long id, Game game);

    /**
     * This method deletes a game from storage.
     * @param id
     * @return
     */
    public boolean deleteGame(long id);

}
