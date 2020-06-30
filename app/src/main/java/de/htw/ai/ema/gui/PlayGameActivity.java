package de.htw.ai.ema.gui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import de.htw.ai.ema.R;
import de.htw.ai.ema.control.MultiplayerController;
import de.htw.ai.ema.model.Card;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PlayGameActivity extends AppCompatActivity {

    private MultiplayerController controller;
    private String playerName;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter imageAdapter;
    private StaggeredGridLayoutManager layoutManager;
    private LinkedList<Card> handCards;
    private Card selectedCard;
    private Map<Integer, Card> selectedCards;
    private final String TAG = "PlayGameActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);
        Intent i = getIntent();
        this.playerName = i.getStringExtra("playersName");
        this.controller = new MultiplayerController(this.playerName,
                i.getBooleanExtra("host", false));
        this.controller.addChangeListener(new ChangeListener() {
            @Override
            public void onCardAdd(Card c) {
                Log.println(Log.INFO, TAG, "Listener: Card added");
            }

            @Override
            public void onCardSetChange(List<Card> cards) {
                PlayGameActivity.this.addNewHand(cards);
            }
        });
        this.handCards = new LinkedList<>();
        this.recyclerView = (RecyclerView) findViewById(R.id.recycler_view_hand_cards);
        this.layoutManager = new StaggeredGridLayoutManager(13, StaggeredGridLayoutManager.VERTICAL);
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.imageAdapter = new ImageAdapter(handCards);
        this.recyclerView.setAdapter(this.imageAdapter);
        this.selectedCards = new HashMap<>();
        this.controller.startGame();
    }

    private void addCard(Card card){
        this.handCards.add(card);
        Log.println(Log.INFO, TAG, "added Card to handcard list");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageAdapter.notifyItemInserted(handCards.size()-1);
            }
        });
    }

    private void addNewHand(List<Card> cards){
        this.handCards.clear();
        this.handCards.addAll(cards);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageAdapter.notifyDataSetChanged();
            }
        });
    }

    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

        private List<Card> cards;
        public int currentCard;

        public class ImageViewHolder extends RecyclerView.ViewHolder {

            public ImageView imageView;
            public ImageViewHolder(ImageView v){
                super(v);
                imageView = v;
            }
        }

        public ImageAdapter(List<Card> cards){
            this.cards = cards;
        }

        @Override
        public ImageAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            ImageView v = (ImageView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.hand_card_image_view, parent, false);

            View.OnClickListener onClickListener = v1 -> {
                ImageView iv = (ImageView) v1;
                int yOffset = 100;
                if(!PlayGameActivity.this.selectedCards.containsKey(iv.getId())){
                    Drawable frame = getResources().getDrawable(R.drawable.highlight_card_frame);
                    iv.setBackground(frame);
                    //PlayGameActivity.this.selectedCard = cards.get(iv.getId());
                    PlayGameActivity.this.selectedCards.put(iv.getId(), cards.get(iv.getId()));
                   /* ViewGroup.LayoutParams params = PlayGameActivity.this.recyclerView.getLayoutParams();
                    params.height-=100;
                    PlayGameActivity.this.recyclerView.setLayoutParams(params);*/
                    iv.setY(iv.getY()-yOffset);
                } else {
                    Log.println(Log.INFO, TAG, "clicked on same card again");
                    iv.setBackground(null);
                    PlayGameActivity.this.selectedCards.remove(iv.getId());
                    iv.setY(iv.getY()+yOffset);
                    //PlayGameActivity.this.selectedCard = null;
                }
            };
            //TODO?
            v.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    return false;
                }
            });
            v.setOnClickListener(onClickListener);
            ImageViewHolder vh = new ImageViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position){
            holder.imageView.setId(position);
            Context c = getApplicationContext();
            int resId = c.getResources().getIdentifier(handCards.get(position).getName().toLowerCase(),
                    "drawable", c.getApplicationInfo().packageName);
            holder.imageView.setImageResource(resId);
        }

        @Override
        public int getItemCount(){
            return cards.size();
        }
    }
}
