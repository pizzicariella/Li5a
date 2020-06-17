package de.htw.ai.ema.persistence.dao;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.htw.ai.ema.model.Account;
import de.htw.ai.ema.model.Card;
import de.htw.ai.ema.model.Cycle;
import de.htw.ai.ema.model.Game;
import de.htw.ai.ema.model.GameRound;
import de.htw.ai.ema.model.Hand;
import de.htw.ai.ema.model.Player;
import de.htw.ai.ema.model.Rank;
import de.htw.ai.ema.model.Stack;
import de.htw.ai.ema.model.Suit;
import de.htw.ai.ema.persistence.database.Li5aContract;
import de.htw.ai.ema.persistence.database.Li5aDbHelper;

public class DbDao implements DAO {

    Li5aDbHelper helper;
    //private final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;


    public DbDao(Context context){
        helper = new Li5aDbHelper(context);
    }

    @Override
    public long saveGame(Game game) {
        /*ActivityCompat.requestPermissions(activity, new String[]
                        {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);*/
        SQLiteDatabase db = helper.getWritableDatabase();

        HashMap<String, Long> cardIds = new HashMap<>();
        Card[] allCards = game.getDeck().getCards();
        for (Card c: allCards){
            ContentValues cardVals = new ContentValues();
            cardVals.put(Li5aContract.CardEntry.COLUMN_NAME_SUIT, c.getSuit().name());
            cardVals.put(Li5aContract.CardEntry.COLUMN_NAME_RANK, c.getRank().name());
            cardIds.put(c.getName(), db.insert(Li5aContract.CardEntry.TABLE_NAME, null, cardVals));
        }

        Map<String, Player> players = game.getPlayers();
        ArrayList<Long> playerIds = new ArrayList<>();
        for(Player p: players.values()){

            List<Card> handCards = p.getHand().getCards();
            ContentValues handVals = new ContentValues();
            int cardIndex = 1;
            for(Card c: handCards){
                handVals.put("card"+cardIndex, cardIds.get(c.getName()));
                cardIndex++;
            }
            Long handId = db.insert(Li5aContract.HandEntry.TABLE_NAME, null, handVals);

            List<Card> accountCards = p.getAccount().getCards();
            ContentValues accountVals = new ContentValues();
            int accountCardIndex = 0;
            for (Card c: accountCards){
                accountVals.put("card"+accountCardIndex, cardIds.get(c.getName()));
                accountCardIndex++;
            }
            Long accountId = db.insert(Li5aContract.AccountEntry.TABLE_NAME, null, accountVals);

            ContentValues playerVals = new ContentValues();
            playerVals.put(Li5aContract.PlayerEntry.COLUMN_NAME_NAME, p.getName());
            playerVals.put(Li5aContract.PlayerEntry.COLUMN_NAME_HAND, handId);
            playerVals.put(Li5aContract.PlayerEntry.COLUMN_NAME_ACCOUNT, accountId);
            playerVals.put(Li5aContract.PlayerEntry.COLUMN_NAME_TOTALSCORE, p.getTotalScore());
            playerVals.put(Li5aContract.PlayerEntry.COLUMN_NAME_LAST_COLLECTOR, p.isLastCollector());
            playerIds.add(db.insert(Li5aContract.PlayerEntry.TABLE_NAME, null, playerVals));
        }

        List<Card> stackCards = game.getCurrentRound().getCurrentCycle().getStack().getCards();
        ContentValues stackVals = new ContentValues();
        int stackCardIndex = 1;
        for (Card c: stackCards){
            stackVals.put("card"+stackCardIndex, cardIds.get(c.getName()));
            stackCardIndex++;
        }
        long stackId = db.insert(Li5aContract.StackEntry.TABLE_NAME, null, stackVals);

        ContentValues cycleVals = new ContentValues();
        cycleVals.put(Li5aContract.CycleEntry.COLUMN_NAME_STACK, stackId);
        cycleVals.put(Li5aContract.CycleEntry.COLUMN_NAME_CYCLE_NUMBER,
                game.getCurrentRound().getCurrentCycle().getCycleNumber());
        long cycleId = db.insert(Li5aContract.CycleEntry.TABLE_NAME, null, cycleVals);

        ContentValues gameRoundVals = new ContentValues();
        gameRoundVals.put(Li5aContract.GameRoundEntry.COLUMN_NAME_CYCLE, cycleId);
        gameRoundVals.put(Li5aContract.GameRoundEntry.COLUMN_NAME_ROUND_NUMBER, game.getCurrentRound().getRoundNumber());
        long roundId = db.insert(Li5aContract.GameRoundEntry.TABLE_NAME, null, gameRoundVals);

        ContentValues gameVals = new ContentValues();
        gameVals.put(Li5aContract.GameEntry.COLUMN_NAME_DATE, System.currentTimeMillis());
        gameVals.put(Li5aContract.GameEntry.COLUMN_NAME_PLAYER0, playerIds.get(0));
        gameVals.put(Li5aContract.GameEntry.COLUMN_NAME_PLAYER1, playerIds.get(1));
        gameVals.put(Li5aContract.GameEntry.COLUMN_NAME_PLAYER2, playerIds.get(2));
        gameVals.put(Li5aContract.GameEntry.COLUMN_NAME_PLAYER3, playerIds.get(3));
        gameVals.put(Li5aContract.GameEntry.COLUMN_NAME_OVER, game.isOver());
        gameVals.put(Li5aContract.GameEntry.COLUMN_NAME_GAMEROUND, roundId);

        long id = db.insert(Li5aContract.GameEntry.TABLE_NAME, null, gameVals);
        db.close();
        return id;
    }


    @Override
    public Game loadGame(int id) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cardCursor = db.query(Li5aContract.CardEntry.TABLE_NAME, null, null,
                null, null, null, null);

        HashMap<Long, Card> allCards = new HashMap<>();
        while (cardCursor.moveToNext()){
            long cardId = cardCursor.getLong(cardCursor.getColumnIndex(BaseColumns._ID));
            Suit suit = Suit.valueOf(cardCursor.getString(cardCursor.getColumnIndex(Li5aContract.CardEntry.COLUMN_NAME_SUIT)));
            Rank rank = Rank.valueOf(cardCursor.getString(cardCursor.getColumnIndex(Li5aContract.CardEntry.COLUMN_NAME_RANK)));
            allCards.put(cardId, new Card(suit, rank));
        }
        cardCursor.close();

        String selection = BaseColumns._ID + " = ?";
        String[] gameSelectionArgs = {String.valueOf(id)};

        Cursor gameCursor = db.query(Li5aContract.GameEntry.TABLE_NAME, null, selection,
                gameSelectionArgs, null, null, null);

        ArrayList<Long> playerIds = new ArrayList<>();
        gameCursor.moveToNext();
        playerIds.add(gameCursor.getLong(gameCursor.getColumnIndex(Li5aContract.GameEntry.COLUMN_NAME_PLAYER0)));
        playerIds.add(gameCursor.getLong(gameCursor.getColumnIndex(Li5aContract.GameEntry.COLUMN_NAME_PLAYER1)));
        playerIds.add(gameCursor.getLong(gameCursor.getColumnIndex(Li5aContract.GameEntry.COLUMN_NAME_PLAYER2)));
        playerIds.add(gameCursor.getLong(gameCursor.getColumnIndex(Li5aContract.GameEntry.COLUMN_NAME_PLAYER3)));

        HashMap<String, Player> players = new HashMap<>();
        for(Long pId: playerIds){
            String[] playerSelectionArgs = {String.valueOf(pId)};
            Cursor playerCursor = db.query(Li5aContract.PlayerEntry.TABLE_NAME, null, selection,
                    playerSelectionArgs, null, null, null);
            playerCursor.moveToNext();
            Player p = new Player(playerCursor.getString(playerCursor.getColumnIndex(Li5aContract.PlayerEntry.COLUMN_NAME_NAME)));
            long handId = playerCursor.getInt(playerCursor.getColumnIndex(Li5aContract.PlayerEntry.COLUMN_NAME_HAND));
            String[] handSelectionArgs = {String.valueOf(handId)};
            Cursor handCursor = db.query(Li5aContract.HandEntry.TABLE_NAME, null, selection,
                    handSelectionArgs, null, null, null);
            List<Card> handCards = new ArrayList<>();
            handCursor.moveToNext();
            for(int i = 0; i<13; i++){
                if(!handCursor.isNull(i)){
                    handCards.add(allCards.get(handCursor.getInt(i)));
                }
            }
            handCursor.close();
            Hand h = new Hand();
            h.setCards(handCards);
            p.setHand(h);

            long accountId = playerCursor.getInt(playerCursor.getColumnIndex(Li5aContract.PlayerEntry.COLUMN_NAME_ACCOUNT));
            String[] accountSelectionArgs = {String.valueOf(accountId)};
            Cursor accountCursor = db.query(Li5aContract.AccountEntry.TABLE_NAME, null, selection,
                    accountSelectionArgs, null, null, null);
            List<Card> accountCards = new ArrayList<>();
            accountCursor.moveToNext();
            for(int i = 0; i<4; i++){
                if(!accountCursor.isNull(i)){
                    accountCards.add(allCards.get(accountCursor.getInt(i)));
                }
            }
            accountCursor.close();
            Account a = new Account();
            for(Card c: accountCards){
                a.addCard(c);
            }
            p.setAccount(a);

            p.setTotalScore(playerCursor.getInt(playerCursor.getColumnIndex(Li5aContract.PlayerEntry.COLUMN_NAME_TOTALSCORE)));
            int lastCollector = playerCursor.getInt(playerCursor.getColumnIndex(Li5aContract.PlayerEntry.COLUMN_NAME_LAST_COLLECTOR));
            playerCursor.close();
            p.setLastCollector(lastCollector > 0 ? true : false);

            players.put(p.getName(), p);
        }

        Game g = new Game(players);
        int over = gameCursor.getInt(gameCursor.getColumnIndex(Li5aContract.GameEntry.COLUMN_NAME_OVER));
        g.setOver(over > 0 ? true : false);

        long roundId = gameCursor.getLong(gameCursor.getColumnIndex(Li5aContract.GameEntry.COLUMN_NAME_GAMEROUND));
        gameCursor.close();
        String[] roundSelectionArgs = {String.valueOf(roundId)};
        Cursor roundCursor = db.query(Li5aContract.GameRoundEntry.TABLE_NAME, null, selection,
                roundSelectionArgs, null, null, null);
        roundCursor.moveToNext();
        GameRound gr = new GameRound();
        gr.setRoundNumber(roundCursor.getInt(roundCursor.getColumnIndex(Li5aContract.GameRoundEntry.COLUMN_NAME_ROUND_NUMBER)));

        long cycleId = roundCursor.getLong(roundCursor.getColumnIndex(Li5aContract.GameRoundEntry.COLUMN_NAME_CYCLE));
        roundCursor.close();
        String[] cycleSelectionArgs = {String.valueOf(cycleId)};
        Cursor cycleCursor = db.query(Li5aContract.CycleEntry.TABLE_NAME, null, selection,
                cycleSelectionArgs, null, null, null);
        cycleCursor.moveToNext();
        Cycle c = new Cycle();
        c.setCycleNumber(cycleCursor.getInt(cycleCursor.getColumnIndex(Li5aContract.CycleEntry.COLUMN_NAME_CYCLE_NUMBER)));

        long stackId = cycleCursor.getLong(cycleCursor.getColumnIndex(Li5aContract.CycleEntry.COLUMN_NAME_STACK));
        cycleCursor.close();

        String[] stackSelectionArgs = {String.valueOf(stackId)};
        Cursor stackCursor = db.query(Li5aContract.StackEntry.TABLE_NAME, null, selection,
                stackSelectionArgs, null, null, null);
        stackCursor.moveToNext();
        List<Card> stackCards = new ArrayList<>();
        for(int i = 0; i<4; i++){
            if(!stackCursor.isNull(i)){
                stackCards.add(allCards.get(stackCursor.getLong(i)));
            }
        }
        stackCursor.close();
        Stack s = new Stack();
        for (Card card: stackCards){
            s.addCard(card);
        }

        c.setStack(s);
        gr.setCurrentCycle(c);
        g.setCurrentRound(gr);

        db.close();

        return g;
    }

    @Override
    public List<Game> getAllGames() {
        return null;
    }

    @Override
    public void updateGame(int id, Game game) {

    }

    @Override
    public void deleteGame(int id) {

    }
}
