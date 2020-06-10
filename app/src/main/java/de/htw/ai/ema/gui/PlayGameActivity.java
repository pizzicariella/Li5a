package de.htw.ai.ema.gui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.htw.ai.ema.R;
import de.htw.ai.ema.control.MultiplayerController;
import de.htw.ai.ema.model.Card;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class PlayGameActivity extends AppCompatActivity {

    private MultiplayerController controller;
    private String playerName;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter imageAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private LinkedList<Card> handCards;
    private Card selectedCard;
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
                //this.imageAdapter.notifyItemInserted(handCards.size()-1);
                //Log.println(Log.INFO, TAG, "adapter notified");
            }

            @Override
            public void onCardSetChange(List<Card> cards) {
                PlayGameActivity.this.addNewHand(cards);
                /*for(Card c: cards){
                    PlayGameActivity.this.addCard(c);
                }*/
                Log.println(Log.INFO, TAG, "Listener: new cards added");
            }
        });
        this.handCards = new LinkedList<>();
        this.recyclerView = (RecyclerView) findViewById(R.id.recycler_view_hand_cards);
        this.layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.imageAdapter = new ImageAdapter(handCards);
        this.recyclerView.setAdapter(this.imageAdapter);
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

        //this.imageAdapter.notifyDataSetChanged();
        Log.println(Log.INFO, TAG, "inserted item");
    }

    private void addNewHand(List<Card> cards){
        Log.println(Log.INFO, TAG, "adding new cards now");
        this.handCards.clear();
        this.handCards.addAll(cards);
        Log.println(Log.INFO, TAG, "added cards");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageAdapter.notifyDataSetChanged();
            }
        });
        Log.println(Log.INFO, TAG, "made notification");
    }

    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

        private List<Card> cards;
        public int currentCard;

        public class ImageViewHolder extends RecyclerView.ViewHolder {

            public ImageView imageView;
            public ImageViewHolder(ImageView v){
                super(v);
                imageView = v;
                Log.println(Log.INFO, TAG, "ImageViewHolder");
            }
            /*public TextView tv;
            public ImageViewHolder(TextView v){
                super(v);
                tv = v;
            }*/
        }

        public ImageAdapter(List<Card> cards){
            this.cards = cards;
            Log.println(Log.INFO, TAG,"ImageAdapter");
        }

        @Override
        public ImageAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            ImageView v = (ImageView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.hand_card_image_view, parent, false);
            /*TextView v = (TextView) LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.device_text_view, parent, false);*/
            /*View.OnClickListener onClickListener = v1 -> {
                ImageView iv = (ImageView) v1;
                Drawable frame = getResources().getDrawable(R.drawable.highlight_card_frame);
                iv.setBackground(frame);
                //PlayGameActivity.this.selectedCard = cards.get()
                //JoinGameActivity.this.selected = cards.get(tv.getId());
            };
            v.setOnClickListener(onClickListener);*/
            ImageViewHolder vh = new ImageViewHolder(v);
            Log.println(Log.INFO, TAG, "oncreateviewholder");
            return vh;
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position){
            holder.imageView.setId(position);

            //Log.println(Log.INFO, TAG, path);
            /*File imgFile = new File("res/drawable/"+handCards.get(position).getImgPath());
            if(imgFile.exists()){
                holder.imageView.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
            } else {
                Log.println(Log.INFO, TAG, "file doesn't exist");
            }*/
            Context c = getApplicationContext();
            Log.println(Log.INFO, TAG, c.getApplicationInfo().packageName);
            Log.println(Log.INFO, TAG, handCards.get(position).getName().toLowerCase());
            int resId = c.getResources().getIdentifier(handCards.get(position).getName().toLowerCase(),
                    "drawable", c.getApplicationInfo().packageName);
            Log.println(Log.INFO, TAG, "id: "+resId);
            Drawable drawable = getResources().getDrawable(resId);
            holder.imageView.setImageDrawable(drawable);
            //holder.tv.setText(this.cards.get(position).getName());
            //Log.println(Log.INFO, TAG, new String(String.valueOf(drawable)));

        }

        @Override
        public int getItemCount(){
            return cards.size();
        }
    }
}
