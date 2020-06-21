package de.htw.ai.ema.persistence.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.widget.ArrayAdapter;

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

//TODO catch, handle and test exceptions in this class, aufräumen!!!!s
public class DbDao implements DAO {

    private Li5aDbHelper helper;
    private final String TAG = "DbDao";

    public DbDao(Context context){
        //helper = new Li5aDbHelper(context);
        this.helper = Li5aDbHelper.getInstance(context);
    }

    @Override
    public long saveGame(Game game) {

        SQLiteDatabase db = helper.getWritableDatabase();

        HashMap<String, Long> cardIds = getCardDict(db, game);

        Map<String, Player> players = game.getPlayers();
        ArrayList<Long> playerIds = savePlayers(db, players, cardIds);

        long stackId = saveStack(db, game.getCurrentRound().getCurrentCycle().getStack(), cardIds);

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
    public Game loadGame(long id) {
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
        gameCursor.moveToNext();

        ArrayList<Long> playerIds = getPlayerIds(gameCursor);

        HashMap<String, Player> players = new HashMap<>();
        for(Long pId: playerIds){
            String[] playerSelectionArgs = {String.valueOf(pId)};
            Cursor playerCursor = db.query(Li5aContract.PlayerEntry.TABLE_NAME, null, selection,
                    playerSelectionArgs, null, null, null);
            playerCursor.moveToNext();
            Player p = new Player(playerCursor.getString(playerCursor.getColumnIndex(Li5aContract.PlayerEntry.COLUMN_NAME_NAME)));
            long handId = playerCursor.getLong(playerCursor.getColumnIndex(Li5aContract.PlayerEntry.COLUMN_NAME_HAND));
            String[] handSelectionArgs = {String.valueOf(handId)};
            Cursor handCursor = db.query(Li5aContract.HandEntry.TABLE_NAME, null, selection,
                    handSelectionArgs, null, null, null);
            List<Card> handCards = new ArrayList<>();
            //handCursor.moveToNext();
            if(handCursor.moveToFirst()){
                int cardIndex = 0;
                for(int i = 0; i<13; i++){
                    if(!handCursor.isNull(handCursor.getColumnIndex("card"+i))){
                        long cardId = handCursor.getLong(handCursor.getColumnIndex("card"+i));
                        handCards.add(allCards.get(cardId));
                    }
                }
            }
            handCursor.close();
            Hand h = new Hand();
            h.setCards(handCards);
            p.setHand(h);

            long accountId = playerCursor.getLong(playerCursor.getColumnIndex(Li5aContract.PlayerEntry.COLUMN_NAME_ACCOUNT));
            String[] accountSelectionArgs = {String.valueOf(accountId)};
            Cursor accountCursor = db.query(Li5aContract.AccountEntry.TABLE_NAME, null, selection,
                    accountSelectionArgs, null, null, null);
            List<Card> accountCards = new ArrayList<>();
            //accountCursor.moveToNext();
            if(accountCursor.moveToFirst()){
                for(int i = 0; i<4; i++){
                    if(!accountCursor.isNull(accountCursor.getColumnIndex("card"+i))){
                        accountCards.add(
                                allCards.get(accountCursor.getLong(
                                        accountCursor.getColumnIndex("card"+i))));
                    }
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
        List<Card> stackCards = new ArrayList<>();
        if(stackCursor.moveToFirst()){
            for(int i = 0; i<4; i++){
                if(!stackCursor.isNull(i)){
                    stackCards.add(allCards.get(stackCursor.getLong(i)));
                }
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
    public Map<String, Long> getAllGameIds() {
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] columns = {BaseColumns._ID, Li5aContract.GameEntry.COLUMN_NAME_NAME,
                Li5aContract.GameEntry.COLUMN_NAME_DATE};
        Cursor gameCursor = db.query(Li5aContract.GameEntry.TABLE_NAME, columns, null,
                null, null, null, null);
        Map<String, Long> allGames = new HashMap<>();
        while (gameCursor.moveToNext()){
            String name = gameCursor.getColumnName(
                    gameCursor.getColumnIndex(Li5aContract.GameEntry.COLUMN_NAME_NAME));
            long timestamp = gameCursor.getLong(
                    gameCursor.getColumnIndex(Li5aContract.GameEntry.COLUMN_NAME_DATE));
            long id = gameCursor.getLong(gameCursor.getColumnIndex(BaseColumns._ID));
            String nameAndTs = name+"$"+timestamp;
            allGames.put(nameAndTs, id);
        }
        db.close();
        return allGames;
    }

    //TODO write test
    @Override
    public void updateGame(int id, Game game) {
        SQLiteDatabase db = helper.getWritableDatabase();

        Map<String, Long> cardIds = getCardDict(db, game);
        Map<String, Player> players = game.getPlayers();

        String selection = BaseColumns._ID + " = ?";
        String[] gameSelectionArgs = {String.valueOf(id)};
        String[] gameColumns = {Li5aContract.GameEntry.COLUMN_NAME_PLAYER0,
                Li5aContract.GameEntry.COLUMN_NAME_PLAYER1,
                Li5aContract.GameEntry.COLUMN_NAME_PLAYER2,
                Li5aContract.GameEntry.COLUMN_NAME_PLAYER3,
                Li5aContract.GameEntry.COLUMN_NAME_GAMEROUND};

        Cursor gameCursor = db.query(Li5aContract.GameEntry.TABLE_NAME, gameColumns, selection,
                gameSelectionArgs, null, null, null);
        gameCursor.moveToNext();

        ArrayList<Long> playerIds = getPlayerIds(gameCursor);
        long roundId = gameCursor.getLong(gameCursor.getColumnIndex(Li5aContract.GameEntry.COLUMN_NAME_GAMEROUND));
        gameCursor.close();
        for (int i = 0; i<4; i++){
            String[] playerSelectionArgs = {String.valueOf(playerIds.get(i))};
            String[] playerColumns = {Li5aContract.PlayerEntry.COLUMN_NAME_HAND,
                    Li5aContract.PlayerEntry.COLUMN_NAME_ACCOUNT};
            Cursor playerCursor = db.query(Li5aContract.PlayerEntry.TABLE_NAME, playerColumns, selection,
                    playerSelectionArgs, null, null, null);
            playerCursor.moveToNext();
            long handId = playerCursor.getLong(playerCursor.getColumnIndex(Li5aContract.PlayerEntry.COLUMN_NAME_HAND));
            long accountId = playerCursor.getLong(playerCursor.getColumnIndex(Li5aContract.PlayerEntry.COLUMN_NAME_ACCOUNT));
            playerCursor.close();
            String[] handSelectionArgs = {String.valueOf(handId)};

            List<Card> handCards = players.get(i).getHand().getCards();
            ContentValues handVals = new ContentValues();

            for(int j=0; j<13; j++){
                if(j<handCards.size()){
                    handVals.put("card"+j, cardIds.get(handCards.get(j).getName()));
                } else {
                    handVals.put("card"+j, (String) null);
                }
            }
            selection = BaseColumns._ID + " LIKE ?";
            db.update(Li5aContract.HandEntry.TABLE_NAME, handVals, selection, handSelectionArgs);

            List<Card> accountCards = players.get(i).getAccount().getCards();
            ContentValues accountVals = new ContentValues();
            for(int j = 0; j<4; j++){
                if(j<accountCards.size()){
                    accountVals.put("card"+j, cardIds.get(accountCards.get(j).getName()));
                } else {
                    handVals.put("card"+j, (String) null);
                }
            }
            String[] accountSelectionArgs = {String.valueOf(accountId)};
            db.update(Li5aContract.AccountEntry.TABLE_NAME, accountVals, selection, accountSelectionArgs);

            ContentValues playerVals = new ContentValues();
            playerVals.put(Li5aContract.PlayerEntry.COLUMN_NAME_TOTALSCORE, players.get(i).getTotalScore());
            db.update(Li5aContract.PlayerEntry.TABLE_NAME, playerVals, selection, playerSelectionArgs);
        }

        String[] roundSelectionArgs = {String.valueOf(roundId)};
        String[] roundColumns = {Li5aContract.GameRoundEntry.COLUMN_NAME_CYCLE};
        Cursor roundCursor = db.query(Li5aContract.GameRoundEntry.TABLE_NAME, roundColumns, selection,
                roundSelectionArgs, null, null, null);
        roundCursor.moveToNext();

        long cycleId = roundCursor.getLong(roundCursor.getColumnIndex(Li5aContract.GameRoundEntry.COLUMN_NAME_CYCLE));
        roundCursor.close();

        String[] cycleSelectionArgs = {String.valueOf(cycleId)};
        String[] cycleColumns = {Li5aContract.CycleEntry.COLUMN_NAME_STACK};
        Cursor cycleCursor = db.query(Li5aContract.CycleEntry.TABLE_NAME, cycleColumns, selection,
                cycleSelectionArgs, null, null, null);
        cycleCursor.moveToNext();

        long stackId = cycleCursor.getLong(cycleCursor.getColumnIndex(Li5aContract.CycleEntry.COLUMN_NAME_STACK));
        cycleCursor.close();

        List<Card> stackCards = game.getCurrentRound().getCurrentCycle().getStack().getCards();
        ContentValues stackVals = new ContentValues();
        for(int j = 1; j<=4; j++){
            if(j<stackCards.size()){
                stackVals.put("card"+j, cardIds.get(stackCards.get(j).getName()));
            } else {
                stackVals.put("card"+j, (String) null);
            }
        }

        String[] stackSelectionArgs = {String.valueOf(stackId)};
        db.update(Li5aContract.StackEntry.TABLE_NAME, stackVals, selection, stackSelectionArgs);

        ContentValues cycleVals = new ContentValues();
        cycleVals.put(Li5aContract.CycleEntry.COLUMN_NAME_CYCLE_NUMBER,
                game.getCurrentRound().getCurrentCycle().getCycleNumber());

        db.update(Li5aContract.CycleEntry.TABLE_NAME, cycleVals, selection, cycleSelectionArgs);

        ContentValues roundVals = new ContentValues();
        roundVals.put(Li5aContract.GameRoundEntry.COLUMN_NAME_ROUND_NUMBER,
                game.getCurrentRound().getRoundNumber());
        db.update(Li5aContract.GameRoundEntry.TABLE_NAME, roundVals, selection, roundSelectionArgs);

        ContentValues gameVals = new ContentValues();
        gameVals.put(Li5aContract.GameEntry.COLUMN_NAME_DATE, System.currentTimeMillis());
        gameVals.put(Li5aContract.GameEntry.COLUMN_NAME_OVER, game.isOver());
        db.update(Li5aContract.GameEntry.TABLE_NAME, gameVals, selection, gameSelectionArgs);
    }

    @Override
    public void deleteGame(int id) {

    }

    private HashMap<String, Long> getCardDict(SQLiteDatabase writableDb, Game game){
        Cursor cardCursor = writableDb.query(Li5aContract.CardEntry.TABLE_NAME, null, null,
                null, null, null, null);
        HashMap<String, Long> cardIds = new HashMap<>();
        if(cardCursor.getColumnCount() != 52){
            writableDb.execSQL("DELETE FROM "+Li5aContract.CardEntry.TABLE_NAME);
            Card[] allCards = game.getDeck().getCards();
            for (Card c: allCards){
                ContentValues cardVals = new ContentValues();
                cardVals.put(Li5aContract.CardEntry.COLUMN_NAME_SUIT, c.getSuit().name());
                cardVals.put(Li5aContract.CardEntry.COLUMN_NAME_RANK, c.getRank().name());
                cardIds.put(c.getName(), writableDb.insert(Li5aContract.CardEntry.TABLE_NAME, null, cardVals));
            }
        } else {
            while (cardCursor.moveToNext()){
                String suit = cardCursor.getString(cardCursor.getColumnIndex(Li5aContract.CardEntry.COLUMN_NAME_SUIT));
                String rank = cardCursor.getString(cardCursor.getColumnIndex(Li5aContract.CardEntry.COLUMN_NAME_RANK));
                long id = cardCursor.getLong(cardCursor.getColumnIndex(BaseColumns._ID));
                cardIds.put((suit+rank).toUpperCase(), id);
            }
        }
        cardCursor.close();
        return cardIds;
    }

    private ArrayList<Long> savePlayers(SQLiteDatabase writableDb, Map<String, Player> players, Map<String, Long> cardDict){
        ArrayList<Long> playerIds = new ArrayList<>();
        for(Player p: players.values()){

            List<Card> handCards = p.getHand().getCards();
            ContentValues handVals = new ContentValues();
            int cardIndex = 0;
            for(Card c: handCards){
                handVals.put("card"+cardIndex, cardDict.get(c.getName()));
                cardIndex++;
            }
            /*Long handId = db.insert(Li5aContract.HandEntry.TABLE_NAME,
                    Li5aContract.HandEntry.COLUMN_NAME_CARD1, handVals);*/
            long handId = writableDb.insert(Li5aContract.HandEntry.TABLE_NAME, null, handVals);

            List<Card> accountCards = p.getAccount().getCards();
            ContentValues accountVals = new ContentValues();
            int accountCardIndex = 0;
            for (Card c: accountCards){
                accountVals.put("card"+accountCardIndex, cardDict.get(c.getName()));
                accountCardIndex++;
            }
            Long accountId = writableDb.insert(Li5aContract.AccountEntry.TABLE_NAME,
                    Li5aContract.AccountEntry.COLUMN_NAME_CARD0, accountVals);
            //long accountId = db.insert(Li5aContract.AccountEntry.TABLE_NAME, null, accountVals);

            ContentValues playerVals = new ContentValues();
            playerVals.put(Li5aContract.PlayerEntry.COLUMN_NAME_NAME, p.getName());
            playerVals.put(Li5aContract.PlayerEntry.COLUMN_NAME_HAND, handId);
            playerVals.put(Li5aContract.PlayerEntry.COLUMN_NAME_ACCOUNT, accountId);
            playerVals.put(Li5aContract.PlayerEntry.COLUMN_NAME_TOTALSCORE, p.getTotalScore());
            playerVals.put(Li5aContract.PlayerEntry.COLUMN_NAME_LAST_COLLECTOR, p.isLastCollector());
            playerIds.add(writableDb.insert(Li5aContract.PlayerEntry.TABLE_NAME, null, playerVals));
        }
        return playerIds;
    }

    private long saveStack(SQLiteDatabase db, Stack stack, Map<String, Long> cardDict){
        List<Card> stackCards = stack.getCards();

        ContentValues stackVals = new ContentValues();
        int stackCardIndex = 1;
        for (Card c : stackCards) {
            stackVals.put("card" + stackCardIndex, cardDict.get(c.getName()));
            stackCardIndex++;
        }
        /*long stackId = db.insert(Li5aContract.StackEntry.TABLE_NAME,
                Li5aContract.StackEntry.COLUMN_NAME_CARD1, stackVals);*/
        return db.insert(Li5aContract.StackEntry.TABLE_NAME, null, stackVals);
    }

    private ArrayList<Long> getPlayerIds(Cursor gameCursor){
        ArrayList<Long> playerIds = new ArrayList<>();
        //gameCursor.moveToNext();
        playerIds.add(gameCursor.getLong(gameCursor.getColumnIndex(Li5aContract.GameEntry.COLUMN_NAME_PLAYER0)));
        playerIds.add(gameCursor.getLong(gameCursor.getColumnIndex(Li5aContract.GameEntry.COLUMN_NAME_PLAYER1)));
        playerIds.add(gameCursor.getLong(gameCursor.getColumnIndex(Li5aContract.GameEntry.COLUMN_NAME_PLAYER2)));
        playerIds.add(gameCursor.getLong(gameCursor.getColumnIndex(Li5aContract.GameEntry.COLUMN_NAME_PLAYER3)));
        return playerIds;
    }

}
