package de.htw.ai.ema.control;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.htw.ai.ema.gui.PlayGameActivity;
import de.htw.ai.ema.logic.Li5aLogic;
import de.htw.ai.ema.logic.Li5aLogicImpl;
import de.htw.ai.ema.model.Card;
import de.htw.ai.ema.model.Game;
import de.htw.ai.ema.model.Hand;
import de.htw.ai.ema.model.Player;
import de.htw.ai.ema.network.ConnectionProperties;
import de.htw.ai.ema.network.service.handler.ConnectionHandler;
import de.htw.ai.ema.network.service.listener.ReceiveListener;

public class MultiplayerController implements Control {

    private ConnectionHandler handler;
    private Li5aLogic logic;
    private String playersName;
    private Map<String, Player> players;
    private Game game;
    private boolean host;
    private final String TAG = "MultiplayerController";

    public MultiplayerController(String playersName, boolean host){
        this.playersName = playersName;
        this.players = new HashMap<>();
        this.players.put(playersName, new Player(playersName));
        this.host = host;
        //this.handler = new NToMConnectionHandler(playersName);
        this.handler = ConnectionProperties.getInstance().getConHandler();
        this.handler.addReceiveListener(new ReceiveListenerImpl());
        this.logic = new Li5aLogicImpl();
    }

    public Game getGame() {
        return game;
    }

    public String getPlayersName() {
        return playersName;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public boolean isHost() {
        return host;
    }

    public Li5aLogic getLogic() {
        return logic;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public void startGame() {
        if(!this.host){
            /*TODO problem: if client is to fast, host doesn't receive name because listener hasn't been added yet.
                the sleep solution is ugly, but works. Think about something better.
             */
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.e(TAG, "Interrupted Exception", e);
                e.printStackTrace();
            }
            Log.println(Log.INFO, TAG, playersName+": sending my name to others");
            this.handler.sendMessage("name"+this.playersName);
        }
    }

    @Override
    public void startGameRound(){
        if(this.game != null){
            //oder ist es sinnvoller immer ein neues objekt zu erstellen?
            this.game.getCurrentRound().setRoundNumber(this.game.getCurrentRound().getRoundNumber()+1);
            this.game.getCurrentRound().getCurrentCycle().setCycleNumber(0);
            this.startCycle();
            PlayGameActivity.setHandCards(this.game.getPlayers().get(playersName).getHand().getCards());
            /*for (Card card : handCards) {
                PlayGameActivity.addImage(card);
                //PlayGameActivity.imageAdapter.notifyItemInserted(handCards.size()-1);
            }*/
        } else {
            Log.println(Log.INFO, TAG, "game wasn't initialized correctly");
        }
    }

    private void resetHandCards(){
        Hand[] hands = this.logic.dealCards(game.getDeck());
        int i = 0;
        for(Player p: this.game.getPlayers().values()){
            p.setHand(hands[i]);
            i++;
        }
    }

    @Override
    public Player startCycle() {
        if(game != null){
            this.game.getCurrentRound().getCurrentCycle().getStack().clear();
        } else {
            Log.println(Log.INFO, TAG, "game wasn't initialized correctly");
            //TODO what should happen here?
        }
        return logic.findStartPlayerCycle(this.game.getPlayers());
    }

    @Override
    public void passCards(List<Card> selected) {

    }

    @Override
    public void playCard(Card card) {

    }

    @Override
    public void cancel() {

    }

    private class ReceiveListenerImpl implements ReceiveListener{
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Object received) {
            Log.println(Log.INFO, TAG, "Listener received an object");
            if(received instanceof String){
                String receivedMessage = (String) received;
                Log.println(Log.INFO, TAG, "Listener: Received the following String: "+receivedMessage);
                if(MultiplayerController.this.host && receivedMessage.startsWith("name")) {
                    String receivedName = receivedMessage.substring(4);
                    if (MultiplayerController.this.players.containsKey(receivedName)) {
                        receivedName += " (2)";
                    }
                    MultiplayerController.this.players.put(receivedName, new Player(receivedName));
                    Log.println(Log.INFO, TAG, "Received name: " + receivedName);
                    //TODO sollte nur passieren wenn Spieler host ist, ansonsten erst nach verschicken des Spiel objekts
                    if (MultiplayerController.this.players.size() == 4) {
                        MultiplayerController.this.game = new Game(MultiplayerController.this.players);
                        Log.println(Log.INFO, TAG, "Game initialized");
                        MultiplayerController.this.resetHandCards();
                        //send initialized game object to others
                        Log.println(Log.INFO, TAG, "Sending game object to others");
                        MultiplayerController.this.handler.sendMessage(MultiplayerController.this.game);
                        startGameRound();
                    }
                }
            } else if(!MultiplayerController.this.host && received instanceof Game){
                MultiplayerController.this.game = (Game) received;
                Log.println(Log.INFO, TAG, "received game object.");
                startGameRound();
            }
        }
    }
}
