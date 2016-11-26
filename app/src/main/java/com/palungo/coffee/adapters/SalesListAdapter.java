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
import com.palungo.coffee.activities.SalesActivity;
import com.palungo.coffee.helpers.Constant;
import com.palungo.coffee.helpers.DatabaseHelper;
import com.palungo.coffee.interfaces.ItemRemovedListener;
import com.palungo.coffee.interfaces.ItemTouchHelperAdapter;
import com.palungo.coffee.pojo.SalesInfo;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Sanjay on 7/1/2016.
 */
public class SalesListAdapter extends RecyclerView.Adapter<SalesListAdapter.SalesViewHolder> implements ItemTouchHelperAdapter {

    ItemRemovedListener mItemRemoved;
    MaterialDialog mMaterialDialog;
    Context mContext;
    ArrayList<SalesInfo> mSalesInfoList = new ArrayList<>();
    int sum;

    public SalesListAdapter(Context mContext, ArrayList<SalesInfo> mSalesInfoList, ItemRemovedListener mItemRemoved) {
        this.mContext = mContext;
        this.mSalesInfoList = mSalesInfoList;
        this.mItemRemoved = mItemRemoved;
    }

    public void addData(final ArrayList<SalesInfo> mItemList) {
        this.mSalesInfoList = mItemList;
        notifyDataSetChanged();
    }

    @Override
    public SalesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SalesViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_sales, parent, false));
    }

    @Override
    public void onBindViewHolder(SalesViewHolder holder, int position) {
        SalesInfo item = mSalesInfoList.get(position);
        holder.mItemName.setText(item.getName());
        holder.mItemPrice.setText(Constant.RS + (item.getQty() * item.getPrice()));
        holder.mItemQty.setText("(" + item.getQty() + Constant.UNITS + " - " + Constant.RS + item.getPrice() + ")");
    }

    @Override
    public int getItemCount() {
        return mSalesInfoList.size();
    }

    public int getSum() {
        sum = 0;
        for (int i = 0; i < mSalesInfoList.size(); i++) {
            sum += (mSalesInfoList.get(i).getPrice() * mSalesInfoList.get(i).getQty());
        }
        return sum;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

    }

    @Override
    public void onItemSwipe(final int adapterPosition, int direction) {
        // Can Only edit today's entry
        if (mSalesInfoList.get(adapterPosition).getDate().equals(GlobalApplication.singleton.getDate(null, null, null))) {
            if (direction == ItemTouchHelper.END) {
                ((SalesActivity) mContext).addSales(mSalesInfoList.get(adapterPosition), adapterPosition);
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
        DatabaseHelper.getInstance(mContext).deleteSales(mSalesInfoList.get(position).getId());
        // Remove from List
        mSalesInfoList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mSalesInfoList.size());
    }

    public void updateItem(int position) {
        notifyItemChanged(position);
    }

    public class SalesViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.layout_sales_view)
        View mItemView;

        @Bind(R.id.layout_sales_name)
        TextView mItemName;

        @Bind(R.id.layout_sales_qty)
        TextView mItemQty;

        @Bind(R.id.layout_sales_price)
        TextView mItemPrice;

        public SalesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
