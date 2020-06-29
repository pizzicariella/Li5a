package de.htw.ai.ema.gui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import de.htw.ai.ema.R;
import de.htw.ai.ema.model.Game;
import de.htw.ai.ema.model.Player;
import de.htw.ai.ema.persistence.dao.DAO;
import de.htw.ai.ema.persistence.dao.DbDao;

public class LoadGameActivity extends AppCompatActivity {

    private DAO dao;
    private static final String DATABASE_NAME = "Li5a.db";
    private ArrayList<String> allGames;
    private RecyclerView recyclerViewGames, recyclerViewNames;
    private RecyclerView.Adapter gameAdapter, nameAdapter;
    private LinearLayoutManager layoutManagerGames, layoutManagerNames;
    private final String TAG = "Load Game Activity";
    private String selectedGame;
    private Player selectedPlayer;
    private ArrayList<Player> players;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);
        this.dao = new DbDao(getApplicationContext(), DATABASE_NAME);
        this.allGames = convertGameMapToList(dao.getAllGameIds());
        this.players = new ArrayList<>();
        Log.println(Log.INFO, TAG, "num of games: "+allGames.size());
        this.recyclerViewGames = (RecyclerView) findViewById(R.id.recycler_view_load_game);
        this.recyclerViewNames = (RecyclerView) findViewById(R.id.player_names_recycler_view);
        this.layoutManagerGames = new LinearLayoutManager(this);
        this.layoutManagerNames = new LinearLayoutManager(this);
        this.recyclerViewGames.setLayoutManager(this.layoutManagerGames);
        this.recyclerViewNames.setLayoutManager(this.layoutManagerNames);
        this.gameAdapter = new GameAdapter(this, this.allGames);
        this.nameAdapter = new NameAdapter(this.players);
        this.recyclerViewGames.setAdapter(this.gameAdapter);
        this.recyclerViewNames.setAdapter(this.nameAdapter);
    }

    private ArrayList<String> convertGameMapToList(Map<Long, String> games){
        ArrayList<String> gameInfos = new ArrayList<>();
        for(Map.Entry e: games.entrySet()){
            String allInfos = e.getKey()+"$"+e.getValue();
            gameInfos.add(allInfos);
        }
        return gameInfos;
    }

    public void loadSelectedGame(View view){
        if(this.selectedGame != null) {
            String[] gameInfos = this.selectedGame.split("\\$");
            Game game;
            try {
                game = dao.loadGame(Long.valueOf(gameInfos[0]));
                //TODO connection
                recyclerViewGames.setVisibility(View.GONE);
                recyclerViewNames.setVisibility(View.VISIBLE);
                for(Player p: game.getPlayers().values()){
                    this.players.add(p);
                    this.nameAdapter.notifyItemInserted(this.players.size()-1);
                    Log.println(Log.INFO, TAG, "added player "+p.getName());
                }

                TextView heading = (TextView) findViewById(R.id.text_view_select_game);
                heading.setText(R.string.text_select_name);
                Button loadGameButton = (Button) findViewById(R.id.button_load_selected_game);
                loadGameButton.setVisibility(View.GONE);
                /*Intent intent = new Intent(this, PlayGameActivity.class);
                intent.putExtra("loadedGame", game);
                startActivity(intent);*/
            } catch (Exception e) {
                Log.e(TAG, "The requested game id was not found in database", e);
            }
        } else{
            Log.println(Log.INFO, TAG, "selected game was null");
        }
    }

    public class NameAdapter extends RecyclerView.Adapter<NameAdapter.NameViewHolder> {

        private ArrayList<Player> playerList;

        public NameAdapter(ArrayList<Player> playerList){
            this.playerList = playerList;
        }

        @NonNull
        @Override
        public NameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView v = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.player_name_text_view, parent, false);
            Log.println(Log.INFO, TAG, "oncreateviewholder");
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv = (TextView) v;
                    int color = Color.TRANSPARENT;
                    Drawable background = v.getBackground();
                    if(background instanceof ColorDrawable){
                        color = ((ColorDrawable) background).getColor();
                    }
                    if(!(color == Color.GREEN)) {
                        tv.setBackgroundColor(Color.GREEN);
                    } else {
                        tv.setBackgroundColor(Color.TRANSPARENT);
                    }
                    LoadGameActivity.this.selectedPlayer = playerList.get(tv.getId());
                }
            };
            v.setOnClickListener(onClickListener);
            NameViewHolder vh = new NameViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull NameViewHolder holder, int position) {
            holder.nameTextView.setId(position);
            holder.nameTextView.setText(playerList.get(position).getName());
            Log.println(Log.INFO, TAG, "onbindviewholder");
        }

        @Override
        public int getItemCount() {
            return playerList.size();
        }

        public class NameViewHolder extends RecyclerView.ViewHolder {
            public TextView nameTextView;
            public NameViewHolder(View v){
                super(v);
                this.nameTextView = (TextView) v;
            }
        }
    }

    public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

        LayoutInflater inflater;
        private ArrayList<String> games;

        public GameAdapter(Context context, ArrayList<String> games){
            this.inflater = LayoutInflater.from(context);
            this.games = games;
        }

        public class GameViewHolder extends RecyclerView.ViewHolder {
            public TextView gameNameTextView, gameDateTextView;
            public GameViewHolder(View v){
                super(v);
                gameNameTextView = (TextView) v.findViewById(R.id.text_view_game_name);
                gameDateTextView = (TextView) v.findViewById(R.id.text_view_game_date);
            }
        }

        @NonNull
        @Override
        public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = inflater.inflate(R.layout.game_recycler_view_row, parent, false);

            View.OnClickListener onClickListener = v1 -> {
                View v = v1;
                int color = Color.TRANSPARENT;
                Drawable background = view.getBackground();
                if(background instanceof ColorDrawable){
                    color = ((ColorDrawable) background).getColor();
                }
                if(!(color == Color.GREEN)) {
                    v.setBackgroundColor(Color.GREEN);
                    LoadGameActivity.this.selectedGame = this.games.get(v.getId());
                } else {
                    v.setBackgroundColor(Color.TRANSPARENT);
                    if(LoadGameActivity.this.selectedGame.equals(this.games.get(v.getId()))){
                        LoadGameActivity.this.selectedGame = null;
                    }
                }
            };
            view.setOnClickListener(onClickListener);
            GameViewHolder gh = new GameViewHolder(view);
            return gh;
        }

        @Override
        public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
            holder.itemView.setId(position);
            String infoString = games.get(position);
            String[] IdNameDate = infoString.split("\\$");
            holder.gameNameTextView.setText(IdNameDate[1]);
            Date date = new Date(Long.valueOf(IdNameDate[2]));
            DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            holder.gameDateTextView.setText(df.format(date));
            //holder.textView.setText();
        }

        @Override
        public int getItemCount() {
            return games.size();
        }
    }
}
