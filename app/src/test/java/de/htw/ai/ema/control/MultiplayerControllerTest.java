package de.htw.ai.ema.control;

import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import de.htw.ai.ema.model.Game;
import de.htw.ai.ema.model.Player;

import static org.junit.Assert.*;


public class MultiplayerControllerTest {

    private final String TAG = "MultiplayerController";
    MultiplayerController mpConH = new MultiplayerController("host", true);
    MultiplayerController mpConC = new MultiplayerController("client", false);
    Map<String, Player> players = new HashMap<>();

    @Test
    public void testConstruct(){
        assertEquals("players name not set", "host", mpConH.getPlayersName());
        assertNotNull("Player Map not initialized", mpConH.getPlayers());
        assertTrue("Own player not added to player map", mpConH.getPlayers().containsKey("host"));
        assertTrue("host variable not set correctly", mpConH.isHost());
        assertNotNull("logic object was not initialized", mpConH.getLogic());
    }

    @Test
    public void testStartGame(){
        fail("not yet implemented");
    }

    @Test
    public void testStartGameRound(){
        //TODO test behaviour with null game object
        players.put("p1", new Player("p1"));
        players.put("p2", new Player("p2"));
        players.put("p3", new Player("p3"));
        players.put("p4", new Player("p4"));
        Game g = new Game(players);
        mpConH.setGame(g);
        int roundNumberBefore = mpConH.getGame().getCurrentRound().getRoundNumber();
        mpConH.startGameRound();
        int roundNumberAfter = mpConH.getGame().getCurrentRound().getRoundNumber();
        int numHandCards = mpConH.getGame().getPlayers().get("p3").getHand().getCards().size();
        assertEquals("Round Number was not increased", roundNumberBefore+1, roundNumberAfter);
        assertEquals("Cycle number not set to 0", 0,
                mpConH.getGame().getCurrentRound().getCurrentCycle().getCycleNumber());
        assertEquals("players didn't receive hand cards", 13, numHandCards);
    }

    @Test
    public void testStartCycle(){
        //TODO test behaviour with null game object
        players.put("p1", new Player("p1"));
        players.put("p2", new Player("p2"));
        players.put("p3", new Player("p3"));
        players.put("p4", new Player("p4"));
        Game g = new Game(players);
        mpConH.setGame(g);
        Player p = mpConH.startCycle();
        assertEquals("Stack was not cleared for next round", 0,
                mpConH.getGame().getCurrentRound().getCurrentCycle().getStack().getCards().size());
    }

    @Test
    public void testPassCards(){
        fail("Not yet implemented");
    }

    @Test
    public void testPlayCard(){
        fail("Not yet implemented");
    }

    @Test
    public void testCancel(){
        fail("Not yet implemented");
    }

}
