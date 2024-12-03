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

/** Recyclerview list adapter for lists of ingredients in report activity and food item stats activity
 *
 * @author Mason De Fazio and Chris Orr
 * @course      COSC 3P97
 * @version     1.0  */
public class FoodIngredientListAdapter extends RecyclerView.Adapter<FoodIngredientListAdapter.MyViewHolder>{

    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<FoodIngredient> foodIngredients;
    public FoodIngredientListAdapter(Context context, ArrayList<FoodIngredient> foodIngredients,
                                     RecyclerViewInterface recyclerViewInterface){
        this.context = context;
        this.foodIngredients = foodIngredients;
        this.recyclerViewInterface = recyclerViewInterface;

    }


    @NonNull
    @Override
    public FoodIngredientListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.food_ingredient_list_row, parent, false);
        return new FoodIngredientListAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodIngredientListAdapter.MyViewHolder holder, int position) {
        //assign values to each row

        holder.name.setText(String.valueOf(foodIngredients.get(position).getName()));

        //change colour based on rating
        int rating = foodIngredients.get(position).getRating();
        switch (rating){
            case 3:
                holder.name.setTextColor(Color.RED);
                break;
            case 2:
                holder.name.setTextColor(Color.rgb(255,215,0));
                break;
            case 1:
                holder.name.setTextColor(Color.GREEN);
                break;

        }




    }

    @Override
    public int getItemCount() {
        return foodIngredients.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name;


        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            name = itemView.findViewById(R.id.foodIngredientNameTextView);

            //single click
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

            //long click
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

