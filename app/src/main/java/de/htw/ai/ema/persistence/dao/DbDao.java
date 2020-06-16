package de.htw.ai.ema.persistence.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.htw.ai.ema.model.Card;
import de.htw.ai.ema.model.Game;
import de.htw.ai.ema.model.Player;
import de.htw.ai.ema.model.Rank;
import de.htw.ai.ema.model.Suit;
import de.htw.ai.ema.persistence.database.Li5aContract;
import de.htw.ai.ema.persistence.database.Li5aDbHelper;

public class DbDao implements DAO {

    Li5aDbHelper helper;

    public DbDao(Context context){
        helper = new Li5aDbHelper(context);
    }

    @Override
    public long saveGame(Game game) {
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

        return db.insert(Li5aContract.GameEntry.TABLE_NAME, null, gameVals);
    }


    //TODO noch nicht fertig
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
            Player p = new Player(playerCursor.getString(gameCursor.getColumnIndex(Li5aContract.PlayerEntry.COLUMN_NAME_NAME)));

        }



        return null;
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
