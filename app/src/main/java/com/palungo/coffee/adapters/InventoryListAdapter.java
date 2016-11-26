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
import com.palungo.coffee.GlobalApplication;
import com.palungo.coffee.R;
import com.palungo.coffee.activities.InventoryActivity;
import com.palungo.coffee.helpers.Constant;
import com.palungo.coffee.helpers.DatabaseHelper;
import com.palungo.coffee.interfaces.ItemRemovedListener;
import com.palungo.coffee.interfaces.ItemTouchHelperAdapter;
import com.palungo.coffee.pojo.InventoryInfo;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Sanjay on 6/23/2016.
 */
public class InventoryListAdapter extends RecyclerView.Adapter<InventoryListAdapter.InventoryListViewHolder> implements ItemTouchHelperAdapter {

    ItemRemovedListener mItemRemoved;
    MaterialDialog mMaterialDialog;
    Context mContext;
    ArrayList<InventoryInfo> mInventoryInfoList = new ArrayList<>();
    int sum;

    public InventoryListAdapter(Context mContext, ArrayList<InventoryInfo> mInventoryInfoList, ItemRemovedListener mItemRemoved) {
        this.mContext = mContext;
        this.mInventoryInfoList = mInventoryInfoList;
        this.mItemRemoved = mItemRemoved;
    }

    public void addData(final ArrayList<InventoryInfo> mItemList) {
        this.mInventoryInfoList = mItemList;
        notifyDataSetChanged();
    }

    @Override
    public InventoryListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InventoryListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_inventory, parent, false));
    }

    @Override
    public void onBindViewHolder(InventoryListViewHolder holder, int position) {
        InventoryInfo item = mInventoryInfoList.get(position);
        holder.mItemName.setText(item.getName());
        holder.mItemQty.setText("(" + item.getQty() + Constant.UNITS + " - " + Constant.RS + item.getPrice() + ")");
        holder.mItemPrice.setText(Constant.RS + (item.getQty() * item.getPrice()));
    }


    @Override
    public int getItemCount() {
        return mInventoryInfoList.size();
    }


    public int getSum() {
        sum = 0;
        for (int i = 0; i < mInventoryInfoList.size(); i++) {
            sum += (mInventoryInfoList.get(i).getPrice() * mInventoryInfoList.get(i).getQty());
        }
        return sum;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

    }

    @Override
    public void onItemSwipe(final int adapterPosition, int direction) {
        // Can Only edit today's entry
        if (mInventoryInfoList.get(adapterPosition).getDate().equals(GlobalApplication.singleton.getDate(null, null, null))) {
            if (direction == ItemTouchHelper.END) {
                ((InventoryActivity) mContext).addInventory(mInventoryInfoList.get(adapterPosition), adapterPosition);
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
                                mItemRemoved.itemRemoved();
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
        } else {
            mMaterialDialog = new MaterialDialog.Builder(mContext)
                    .title("Sorry")
                    .content("You cannot change items of previous dates")
                    .cancelable(false)
                    .positiveColor(mContext.getResources().getColor(R.color.colorTurquoise))
                    .negativeColor(mContext.getResources().getColor(R.color.colorPrimaryDark))
                    .negativeText("Ok")
                    .positiveText("Cancel")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            updateItem(adapterPosition);
                            dialog.dismiss();
                        }
                    })
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            updateItem(adapterPosition);
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    private void removeItem(int position) {
        // Remove from Database
        DatabaseHelper.getInstance(mContext).deleteInventory(mInventoryInfoList.get(position).getId());
        // Remove from List
        mInventoryInfoList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mInventoryInfoList.size());
    }

    public void updateItem(int position) {
        notifyItemChanged(position);
    }

    public class InventoryListViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.layout_inventory_view)
        View mItemView;

        @Bind(R.id.layout_inventory_name)
        TextView mItemName;

        @Bind(R.id.layout_inventory_qty)
        TextView mItemQty;

        @Bind(R.id.layout_inventory_price)
        TextView mItemPrice;

        public InventoryListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
