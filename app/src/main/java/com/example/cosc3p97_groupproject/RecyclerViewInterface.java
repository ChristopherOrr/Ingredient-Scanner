package com.example.cosc3p97_groupproject;

/** Interface to implement item click and hold within recycler view
 *
 * @author Mason De Fazio and Chris Orr
 * @course      COSC 3P97
 * @version     1.0  */
public interface RecyclerViewInterface {
    void onItemClick(int position);

    void onItemHold(int position);
}
