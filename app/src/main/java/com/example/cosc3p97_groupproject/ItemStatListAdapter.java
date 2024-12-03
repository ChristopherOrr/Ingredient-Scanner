package com.example.cosc3p97_groupproject;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/** Recyclerview list adapter for lists of recorded items in stats activity
 *
 * @author Mason De Fazio and Chris Orr
 * @course      COSC 3P97
 * @version     1.0  */
public class ItemStatListAdapter extends RecyclerView.Adapter<ItemStatListAdapter.MyViewHolder>{

    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<ItemStat> itemStats;
    public ItemStatListAdapter(Context context, ArrayList<ItemStat> itemStats,
                               RecyclerViewInterface recyclerViewInterface){
        this.context = context;
        this.itemStats = itemStats;
        this.recyclerViewInterface = recyclerViewInterface;

    }


    @NonNull
    @Override
    public ItemStatListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_stat_list_row, parent, false);
        return new ItemStatListAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemStatListAdapter.MyViewHolder holder, int position) {
        //assign values to each row

        holder.score.setText(String.valueOf(itemStats.get(position).getScore()));
        //set colour of score
        if(itemStats.get(position).getScore()>70){
            holder.score.setTextColor(Color.GREEN);
        }
        else if(itemStats.get(position).getScore()>=50){
            holder.score.setTextColor(Color.rgb(255,215,0)); //orange
        }
        else{
            holder.score.setTextColor(Color.RED);
        }

        holder.date.setText(itemStats.get(position).getDate().toString());
        holder.label.setText(itemStats.get(position).getLabel());


    }

    @Override
    public int getItemCount() {
        return itemStats.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView score;
        TextView date;
        TextView label;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface ) {
            super(itemView);

            score = itemView.findViewById(R.id.itemScoreTextView);
            date = itemView.findViewById(R.id.itemDateTextView);
            label = itemView.findViewById(R.id.labelTextView);


            //one press
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null){
                        int pos = getAdapterPosition();

                        //if position is valid
                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });

            //long press
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(recyclerViewInterface != null){
                        int pos = getAdapterPosition();

                        //if position is valid
                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemHold(pos);
                        }
                    }
                    return true;
                }
            });

        }
    }
}

