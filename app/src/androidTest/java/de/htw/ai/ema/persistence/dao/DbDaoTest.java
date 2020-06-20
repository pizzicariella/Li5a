package de.htw.ai.ema.persistence.dao;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import de.htw.ai.ema.logic.Li5aLogic;
import de.htw.ai.ema.logic.Li5aLogicImpl;
import de.htw.ai.ema.model.Card;
import de.htw.ai.ema.model.Game;
import de.htw.ai.ema.model.GameRound;
import de.htw.ai.ema.model.Hand;
import de.htw.ai.ema.model.Player;
import de.htw.ai.ema.model.Rank;
import de.htw.ai.ema.model.Suit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DbDaoTest {

    private DAO dao;

    @Before
    public void setUp(){
        dao = new DbDao(InstrumentationRegistry.getInstrumentation().getTargetContext());
    }

    @Test
    public void testPreConditions(){
        assertNotNull(dao);
    }

    @Test
    public void testSaveAndLoadGame(){
        Map<String, Player> players = new HashMap<>();
        players.put("p1", new Player("p1"));
        players.put("p2", new Player("p2"));
        players.put("p3", new Player("p3"));
        players.put("p4", new Player("p4"));
        Game g = new Game(players);
        Li5aLogic ll = new Li5aLogicImpl();
        Hand[] hands = ll.dealCards(g.getDeck());
        int i = 0;
        for(Player p : g.getPlayers().values()){
            p.setHand(hands[i]);
            i++;
        }
        GameRound gr = new GameRound();
        gr.setRoundNumber(44);
        g.setCurrentRound(gr);
        g.getPlayers().get("p3").getAccount().addCard(new Card(Suit.CLUBS, Rank.ACE));
        g.getPlayers().get("p4").setTotalScore(66);

        long id = dao.saveGame(g);
        assertNotNull("there was an error saving the game", id);

        Game loadedGame = dao.loadGame(id);


        //Examples of game properties are being tested, not all of them
        assertEquals("player name not saved or loaded correctly",
                "p1", loadedGame.getPlayers().get("p1").getName());
        assertEquals("roundNumber not saved or loaded correctly",
                44, loadedGame.getCurrentRound().getRoundNumber());
        assertEquals("Cycle number not saved or loaded correctly",
                0, loadedGame.getCurrentRound().getCurrentCycle().getCycleNumber());
        assertEquals("Stack was not saved or loaded correctly",
                        g.getCurrentRound().getCurrentCycle().getStack().getCards().size(),
                        loadedGame.getCurrentRound().getCurrentCycle().getStack().getCards().size());
        assertEquals("HandCards not saved or loaded correctly",
                g.getPlayers().get("p2").getHand().getCards().size(),
                loadedGame.getPlayers().get("p2").getHand().getCards().size());
        assertEquals("HandCards or Cards not saved or loaded correctly",
                g.getPlayers().get("p2").getHand().getCards().get(4).getName(),
                loadedGame.getPlayers().get("p2").getHand().getCards().get(4).getName());
        assertEquals("Account or Cards not saved or loaded correctly",
                g.getPlayers().get("p3").getAccount().getCards().size(),
                loadedGame.getPlayers().get("p3").getAccount().getCards().size());
        assertEquals("Account or Cards not saved or loaded correctly",
                g.getPlayers().get("p3").getAccount().getCards().get(0).getName(),
                loadedGame.getPlayers().get("p3").getAccount().getCards().get(0).getName());
        assertEquals("Total score was not saved or loaded correctly",
                66, loadedGame.getPlayers().get("p4").getTotalScore());
        assertEquals("Last Collector property not saved or loaded correctly",
                false, loadedGame.getPlayers().get("p1").isLastCollector());
        assertEquals("Card not loaded or saved correctly",
                false, loadedGame.getPlayers().get("p3").getAccount().getCards().get(0).isLi5a());
        assertEquals("Card value not loaded or saved correctly",
                0, loadedGame.getPlayers().get("p3").getAccount().getCards().get(0).getValue());
    }
}
