package de.htw.ai.ema.persistence.dao;

import android.Manifest;

import androidx.core.app.ActivityCompat;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import de.htw.ai.ema.gui.MainActivity;
import de.htw.ai.ema.logic.Li5aLogic;
import de.htw.ai.ema.logic.Li5aLogicImpl;
import de.htw.ai.ema.model.Game;
import de.htw.ai.ema.model.GameRound;
import de.htw.ai.ema.model.Hand;
import de.htw.ai.ema.model.Player;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DbDaoTest {

    /*@Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);*/

    private DAO dao;
    //private final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    @Before
    public void setUp(){
        dao = new DbDao(InstrumentationRegistry.getInstrumentation().getContext());
    }

    @Test
    public void testPreConditions(){
        assertNotNull(dao);
    }

    @Test
    public void testSaveGame(){
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

        long id = dao.saveGame(g);
        System.out.println(id);
        assertNotNull(id);
    }
}
