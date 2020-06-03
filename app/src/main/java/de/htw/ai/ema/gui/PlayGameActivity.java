package de.htw.ai.ema.gui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.htw.ai.ema.R;
import de.htw.ai.ema.control.MultiplayerController;
import de.htw.ai.ema.model.Card;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;

public class PlayGameActivity extends AppCompatActivity {

    private MultiplayerController controller;
    private String playerName;
    private RecyclerView recyclerView;
    private static RecyclerView.Adapter imageAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static ArrayList<Card> handCards;
    private Card selectedCard;
    private final String TAG = "PlayGameActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);
        handCards = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_hand_cards);
        layoutManager = new LinearLayoutManager(PlayGameActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        imageAdapter = new ImageAdapter(handCards);
        recyclerView.setAdapter(imageAdapter);
        Intent i = getIntent();
        this.playerName = i.getStringExtra("playersName");
        this.controller = new MultiplayerController(this.playerName,
                i.getBooleanExtra("host", false));
    }

    public static void addImage(Card card){
        handCards.add(card);
        imageAdapter.notifyItemInserted(handCards.size()-1);
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
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView iv = (ImageView)v;
                    Drawable frame = getResources().getDrawable(R.drawable.highlight_card_frame);
                    iv.setBackground(frame);
                    //PlayGameActivity.this.selectedCard = cards.get()
                    //JoinGameActivity.this.selected = cards.get(tv.getId());
                }
            };
            v.setOnClickListener(onClickListener);
            ImageViewHolder vh = new ImageViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position){
            holder.imageView.setId(position);
            holder.imageView.setImageBitmap(BitmapFactory.decodeFile("resources/"+cards.get(position).getImgPath()));
        }

        @Override
        public int getItemCount(){
            return cards.size();
        }
    }
}
