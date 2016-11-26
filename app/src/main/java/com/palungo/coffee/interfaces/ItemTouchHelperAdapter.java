package com.palungo.coffee.interfaces;

/**
 * Created by Sanjay on 7/6/2016.
 */
public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemSwipe(int adapterPosition, int direction);
}
