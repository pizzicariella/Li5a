package de.htw.ai.ema.persistence.dao;

import android.app.Instrumentation;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.ArrayList;
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
import de.htw.ai.ema.persistence.database.Li5aContract;
import de.htw.ai.ema.persistence.database.Li5aDbHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DbDaoTest {

    private DAO dao;
    private long testId;
    private Game testGame;
    private static final String DATABASE_NAME = "Li5aTest.db";
    //private static final String DATABASE_NAME = "Li5a.db";

    @Before
    public void setUp(){

        InstrumentationRegistry.getInstrumentation().getContext().deleteDatabase(DATABASE_NAME);
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        Li5aDbHelper helper = Li5aDbHelper.getInstance(context, DATABASE_NAME);
        SQLiteDatabase db = helper.getWritableDatabase();

        dao = new DbDao(context, DATABASE_NAME);
        Map<String, Player> players = new HashMap<>();
        players.put("p1", new Player("p1"));
        players.put("p2", new Player("p2"));
        players.put("p3", new Player("p3"));
        players.put("p4", new Player("p4"));
        testGame = new Game(players);
        testGame.setName("testGameName");
        Li5aLogic ll = new Li5aLogicImpl();
        Hand[] hands = ll.dealCards(testGame.getDeck());
        int i = 0;
        for(Player p : testGame.getPlayers().values()){
            p.setHand(hands[i]);
            i++;
        }
        GameRound gr = new GameRound();
        gr.setRoundNumber(99);
        testGame.setCurrentRound(gr);
        testGame.getPlayers().get("p3").getAccount().addCard(new Card(Suit.CLUBS, Rank.ACE));
        testGame.getPlayers().get("p4").setTotalScore(88);

        testId = dao.saveGame(testGame);
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
        g.setName("testGameSaveAndLoad");
        Li5aLogic ll = new Li5aLogicImpl();
        Hand[] hands = ll.dealCards(g.getDeck());
        int i = 0;
        for(Player p : g.getPlayers().values()){
            p.setHand(hands[i]);
            i++;
        }
        GameRound gr = new GameRound();
        gr.setRoundNumber(44);
        gr.getCurrentCycle().getStack().addCard(new Card(Suit.SPADES, Rank.KING));
        g.setCurrentRound(gr);
        g.getPlayers().get("p3").getAccount().addCard(new Card(Suit.CLUBS, Rank.ACE));
        g.getPlayers().get("p4").setTotalScore(66);

        long id = dao.saveGame(g);
        assertNotNull("there was an error saving the game", id);

        Game loadedGame = null;
        try {
            loadedGame = dao.loadGame(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        assertEquals("Stack was not saved or loaded correctly",
                g.getCurrentRound().getCurrentCycle().getStack().getCards().get(0).getName(),
                loadedGame.getCurrentRound().getCurrentCycle().getStack().getCards().get(0).getName());
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
        assertEquals("Name was not loaded or saved correctly",
                g.getName(), loadedGame.getName());
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void loadGameWithWrongIdThrowsException() throws Exception {
        expectedException.expect(Exception.class);
        expectedException.expectMessage("Id not found.");
        Game wrongIdGame = dao.loadGame(7777);
    }

    @Test
    public void testGetAllGameIds(){
        Map<Long, String> allGames = dao.getAllGameIds();
        System.out.println("there are "+allGames.size()+" games");
        assertTrue("Did not return all games: "+allGames.size()+" games", allGames.size()>0);
        assertTrue("Map does not contain testId", allGames.containsKey(testId));
        String gameInfos = allGames.get(testId);
        assertTrue("Value not created correctly", gameInfos.contains("$"));
        String[] vals = gameInfos.split("\\$");
        assertEquals("value not created correctly", testGame.getName(), vals[0] );
        assertTrue("timestamp not a timestamp", Long.valueOf(vals[1]) instanceof Long);
    }

    @Test
    public void testUpdateGame() throws Exception {
        Map<String, Player> players = new HashMap<>();
        players.put("p1", new Player("p1"));
        players.put("p2", new Player("p2"));
        players.put("p3", new Player("p3"));
        players.put("p4", new Player("p4"));
        Game g = new Game(players);
        g.setName("testGameUpdate");
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

        g.getCurrentRound().setRoundNumber(444);
        g.getCurrentRound().getCurrentCycle().getStack().addCard(new Card(Suit.HEARTS, Rank.FOUR));
        g.getCurrentRound().getCurrentCycle().setCycleNumber(333);
        /*gr.setRoundNumber(444);
        Stack stack = new Stack();
        stack.addCard(new Card(Suit.HEARTS, Rank.FOUR));
        Cycle cycle = new Cycle();
        cycle.setStack(stack);
        cycle.setCycleNumber(333);
        gr.setCurrentCycle(cycle);
        g.setCurrentRound(gr);*/
        g.getPlayers().get("p1").setTotalScore(50);
        ArrayList<Card> p2Hand = new ArrayList<>();
        p2Hand.add(new Card(Suit.SPADES, Rank.EIGHT));
        p2Hand.add(new Card(Suit.CLUBS, Rank.QUEEN));
        g.getPlayers().get("p2").getHand().setCards(p2Hand);
        g.getPlayers().get("p3").getAccount().addCard(new Card(Suit.HEARTS, Rank.TEN));

        boolean unsuccessfulUpdate = dao.updateGame(7777, g);
        assertFalse(unsuccessfulUpdate);

        boolean successfulUpdate = dao.updateGame(id, g);
        assertTrue("update not successful", successfulUpdate);

        Game updatedGame = dao.loadGame(id);
        assertEquals("round number was not updated", 444,
                updatedGame.getCurrentRound().getRoundNumber());
        assertEquals("stack size was not updated",
                g.getCurrentRound().getCurrentCycle().getStack().getCards().size(),
                updatedGame.getCurrentRound().getCurrentCycle().getStack().getCards().size());
        assertEquals("stack was updated with wrong card",
                g.getCurrentRound().getCurrentCycle().getStack().getCards().get(0).getName(),
                updatedGame.getCurrentRound().getCurrentCycle().getStack().getCards().get(0).getName());
        assertEquals("cycle number was not updated", 333,
                updatedGame.getCurrentRound().getCurrentCycle().getCycleNumber());
        assertEquals("total score not updated", 50,
                updatedGame.getPlayers().get("p1").getTotalScore());
        assertEquals("totalscore updated without change", 66,
                updatedGame.getPlayers().get("p4").getTotalScore());
        assertEquals("handcards not updated correctly", 2,
                updatedGame.getPlayers().get("p2").getHand().getCards().size());
        assertEquals("handcards not updated correctly",
                g.getPlayers().get("p2").getHand().getCards().get(0).getName(),
                updatedGame.getPlayers().get("p2").getHand().getCards().get(0).getName());
        assertEquals("handcards updated without change",
                g.getPlayers().get("p1").getHand().getCards().size(),
                updatedGame.getPlayers().get("p1").getHand().getCards().size());
        assertEquals("handcards updated without change",
                g.getPlayers().get("p1").getHand().getCards().get(0).getName(),
                updatedGame.getPlayers().get("p1").getHand().getCards().get(0).getName());
        assertEquals("account not updated correctly",
                g.getPlayers().get("p3").getAccount().getCards().size(),
                updatedGame.getPlayers().get("p3").getAccount().getCards().size());
        assertEquals("account not updated correctly",
                g.getPlayers().get("p3").getAccount().getCards().get(0).getName(),
                updatedGame.getPlayers().get("p3").getAccount().getCards().get(0).getName());
        assertEquals("account not updated correctly",
                g.getPlayers().get("p3").getAccount().getCards().get(1).getName(),
                updatedGame.getPlayers().get("p3").getAccount().getCards().get(1).getName());
    }

    @Test
    public void testDeleteGame() throws Exception {
        Map<String, Player> players = new HashMap<>();
        players.put("p1", new Player("p1"));
        players.put("p2", new Player("p2"));
        players.put("p3", new Player("p3"));
        players.put("p4", new Player("p4"));
        Game deleteTestGame = new Game(players);
        deleteTestGame.setName("deleteTestGameName");
        Li5aLogic ll = new Li5aLogicImpl();
        Hand[] hands = ll.dealCards(deleteTestGame.getDeck());
        int i = 0;
        for(Player p : deleteTestGame.getPlayers().values()){
            p.setHand(hands[i]);
            i++;
        }
        GameRound gr = new GameRound();
        gr.setRoundNumber(99);
        deleteTestGame.setCurrentRound(gr);
        deleteTestGame.getPlayers().get("p3").getAccount().addCard(new Card(Suit.CLUBS, Rank.ACE));
        deleteTestGame.getPlayers().get("p4").setTotalScore(88);

        long deleteTestId = dao.saveGame(deleteTestGame);

        boolean successfulDelete = dao.deleteGame(deleteTestId);
        assertTrue("delete method didn't return true on existing id", successfulDelete);

        expectedException.expect(Exception.class);
        expectedException.expectMessage("Id not found.");
        Game deletedGame = dao.loadGame(deleteTestId);

        boolean unsuccessfulDelete = dao.deleteGame(7777);
        assertFalse("delete method didn't return false on not existing id", unsuccessfulDelete);
    }


}
