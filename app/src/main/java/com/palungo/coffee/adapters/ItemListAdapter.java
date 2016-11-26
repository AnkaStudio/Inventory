package com.palungo.coffee.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.palungo.coffee.R;
import com.palungo.coffee.activities.MainActivity;
import com.palungo.coffee.helpers.Constant;
import com.palungo.coffee.helpers.DatabaseHelper;
import com.palungo.coffee.interfaces.ItemListClickListener;
import com.palungo.coffee.interfaces.ItemTouchHelperAdapter;
import com.palungo.coffee.pojo.ItemInfo;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Sanjay on 6/23/2016.
 */
public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemListViewHolder> implements ItemTouchHelperAdapter {

    MaterialDialog mMaterialDialog;
    ArrayList<ItemInfo> mItemInfoList = new ArrayList<>();
    ItemListClickListener mListener;
    Context mContext;
    ItemListViewHolder holder;
    int currentPos;

    public ItemListAdapter(Context mContext, ArrayList<ItemInfo> mItemInfoList, ItemListClickListener mListener) {
        this.mContext = mContext;
        this.mItemInfoList = mItemInfoList;
        this.mListener = mListener;
    }

    public void addData(final ArrayList<ItemInfo> mItemList) {
        this.mItemInfoList = mItemList;
        notifyDataSetChanged();
    }

    @Override
    public ItemListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemListViewHolder holder, int position) {
        final ItemInfo item = mItemInfoList.get(position);
        this.holder = holder;
        this.currentPos = position;

        holder.mItemName.setText(item.getItemName());
        holder.mItemPrice.setText(Constant.RS + item.getItemPrice());
        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onListClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemInfoList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

    }

    @Override
    public void onItemSwipe(final int adapterPosition, int direction) {
        if (direction == ItemTouchHelper.END) {
            ((MainActivity) mContext).addMenuItem(mItemInfoList.get(adapterPosition), adapterPosition);
        } else if (direction == ItemTouchHelper.START) {
            // Delete Popup
            mMaterialDialog = new MaterialDialog.Builder(mContext)
                    .title("Delete ?")
                    .content("Do you want to delete this item ?")
                    .positiveColor(mContext.getResources().getColor(R.color.colorRed))
                    .negativeColor(mContext.getResources().getColor(R.color.colorPrimaryDark))
                    .negativeText("Cancel")
                    .positiveText("Delete")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            removeItem(adapterPosition);
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            updateItem(adapterPosition);
                            dialog.dismiss();
                        }
                    })
                    .cancelable(false)
                    .show();
        }
    }

    private void removeItem(int position) {
        // Remove from Database
        DatabaseHelper.getInstance(mContext).deleteItems(mItemInfoList.get(position).getItemId());
        // Remove from List
        mItemInfoList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mItemInfoList.size());
    }

    public void updateItem(int position) {
        notifyItemChanged(position);
    }

    public class ItemListViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.layout_item_view)
        View mItemView;

        @Bind(R.id.layout_item_content)
        View mItemContent;

        @Bind(R.id.layout_item_name)
        TextView mItemName;

        @Bind(R.id.layout_item_price)
        TextView mItemPrice;

        public ItemListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
