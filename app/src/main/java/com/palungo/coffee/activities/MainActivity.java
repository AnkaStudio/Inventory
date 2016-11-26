package com.palungo.coffee.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.palungo.coffee.R;
import com.palungo.coffee.adapters.ItemListAdapter;
import com.palungo.coffee.helpers.Constant;
import com.palungo.coffee.helpers.DatabaseHelper;
import com.palungo.coffee.helpers.SimpleItemTouchHelper;
import com.palungo.coffee.interfaces.ItemListClickListener;
import com.palungo.coffee.pojo.ItemInfo;

import java.util.ArrayList;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ItemListClickListener {

    MainActivity self;
    ArrayList<ItemInfo> mItemInfoList = new ArrayList<>();
    ItemListAdapter mAdapter;
    MaterialNumberPicker numberPicker;
    DatabaseHelper mDBHelper;
    MaterialDialog mDialog;
    View mDialogView;
    EditText mItemName, mItemPrice;
    boolean flagOk;

    @Bind(R.id.activity_main_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.activity_main_scroll_view)
    ScrollView mScrollView;

    @Bind(R.id.activity_main_background)
    ImageView mBackgroundImageView;

    @Bind(R.id.activity_main_error)
    View mErrorView;

    @Bind(R.id.activity_main_recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.activity_main_fab)
    FloatingActionButton fabButton;

    public void addMenuItem(final ItemInfo info, final int pos) {
        flagOk = false;
        String title;
        if (info == null) {
            title = "Add an Item";
        } else {
            title = "Edit an Item";
        }
        mDialog = new MaterialDialog.Builder(self)
                .title(title)
                .customView(R.layout.layout_add_menu, true)
                .positiveColor(getResources().getColor(R.color.colorTurquoise))
                .negativeColor(getResources().getColor(R.color.colorPrimaryDark))
                .positiveText("Save")
                .negativeText("Cancel")
                .cancelable(false)
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mDialogView = dialog.getView();
                        mItemName = (EditText) mDialogView.findViewById(R.id.layout_add_menu_name);
                        mItemPrice = (EditText) mDialogView.findViewById(R.id.layout_add_menu_price);

                        if (mItemName.getText().length() > 1) {
                            flagOk = true;
                        } else {
                            flagOk = false;
                            mItemName.setError("Required");
                        }

                        if (mItemPrice.length() > 1) {
                            flagOk = true;
                        } else {
                            flagOk = false;
                            mItemPrice.setError("Required");
                        }
                        if (flagOk) {
                            if (info == null) {
                                mDBHelper.addItems(
                                        mItemName.getText().toString(),
                                        Integer.parseInt(mItemPrice.getText().toString())
                                );
                            } else {
                                mDBHelper.updateItems(
                                        info.getItemId(),
                                        mItemName.getText().toString(),
                                        Integer.parseInt(mItemPrice.getText().toString())
                                );
                            }
                            requestData();
                            dialog.dismiss();
                        }

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (info != null) {
                            mAdapter.updateItem(pos);
                        }
                        dialog.dismiss();
                    }
                }).build();


        if (info != null) {
            EditText name = (EditText) mDialog.getCustomView().findViewById(R.id.layout_add_menu_name);
            name.setText(info.getItemName() + "");

            EditText price = (EditText) mDialog.getCustomView().findViewById(R.id.layout_add_menu_price);
            price.setText(info.getItemPrice() + "");
        }

        mDialog.show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        self = this;
        ButterKnife.bind(this);

        mDBHelper = DatabaseHelper.getInstance(this);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_menu);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_coffee);
        }

        Glide.with(this.getApplicationContext())
                .load(R.drawable.coffee_background)
                .into(mBackgroundImageView);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMenuItem(null, -1);
            }
        });

        mAdapter = new ItemListAdapter(this, mItemInfoList, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mScrollView.setEnabled(false);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mScrollView.smoothScrollBy(0, (int) (dy * 0.2));
            }
        });
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelper(this, mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);

        requestData();
    }


    private void requestData() {
        if (mDBHelper.getItems().size() <= 0) {
            mDBHelper.addItems("Espresso", 160);
            mDBHelper.addItems("Regular Coffee", 160);
            mDBHelper.addItems("Americano", 160);
            mDBHelper.addItems("Cappuccino", 160);
            mDBHelper.addItems("Cafe Latte", 270);
            mDBHelper.addItems("Frozen Coffee", 395);
            mDBHelper.addItems("Iced Coffee", 225);
            mDBHelper.addItems("Cremice", 355);
            mDBHelper.addItems("Hot Tea", 155);
            mDBHelper.addItems("Hot Chocolate", 260);
            mDBHelper.addItems("Milk", 165);
            mDBHelper.addItems("Chocolate Milk", 240);
            mDBHelper.addItems("Hot Hazelnut", 200);
            mDBHelper.addItems("Hot Vanilla Nut", 300);
            mDBHelper.addItems("Hot Caramel Machiatto", 255);
            mDBHelper.addItems("Mocha Berry Latte", 200);
            mDBHelper.addItems("Choco Macademia", 180);
            mDBHelper.addItems("Croissants", 95);
            mDBHelper.addItems("Bagels", 55);
            mDBHelper.addItems("Cookies", 150);
        }

        if (mDBHelper.getItems().size() <= 0) {
            mErrorView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mErrorView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        mItemInfoList.clear();
        mItemInfoList.addAll(mDBHelper.getItems());
        mAdapter.addData(mItemInfoList);
    }


    @Override
    public void onListClick(final ItemInfo item) {

        numberPicker = new MaterialNumberPicker.Builder(self)
                .minValue(1)
                .maxValue(10)
                .defaultValue(1)
                .backgroundColor(Color.TRANSPARENT)
                .separatorColor(Color.TRANSPARENT)
                .textColor(Color.BLACK)
                .textSize(20)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .build();

        new MaterialDialog.Builder(self)
                .title(item.getItemName())
                .customView(numberPicker, true)
                .positiveColor(getResources().getColor(R.color.colorTurquoise))
                .negativeColor(getResources().getColor(R.color.colorPrimaryDark))
                .positiveText("Ok")
                .negativeText("Cancel")
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        MaterialDialog mMaterialDialog = new MaterialDialog.Builder(self)
                                .title("Total")
                                .customView(R.layout.layout_popup_sales, true)
                                .autoDismiss(false)
                                .positiveColor(getResources().getColor(R.color.colorTurquoise))
                                .negativeColor(getResources().getColor(R.color.colorPrimaryDark))
                                .positiveText("Sold")
                                .negativeText("Cancel")
                                .cancelable(false)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        DatabaseHelper.getInstance(self).addSales(item.getItemName(), numberPicker.getValue(), item.getItemPrice());
                                        dialog.dismiss();
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .build();


                        TextView mPopupName = (TextView) mMaterialDialog.getCustomView().findViewById(R.id.layout_popup_sales_name);
                        mPopupName.setText(item.getItemName());
                        TextView mPopupQuantity = (TextView) mMaterialDialog.getCustomView().findViewById(R.id.layout_popup_sales_quantity);
                        mPopupQuantity.setText("(" + numberPicker.getValue() + Constant.UNITS + " - " + Constant.RS + item.getItemPrice() + ")");
                        TextView mPopupPrice = (TextView) mMaterialDialog.getCustomView().findViewById(R.id.layout_popup_sales_price);
                        mPopupPrice.setText(Constant.RS + (numberPicker.getValue() * item.getItemPrice()));
                        mMaterialDialog.show();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_inventory:
                startActivity(new Intent(self, InventoryActivity.class));
                return true;
            case R.id.menu_main_sales:
                startActivity(new Intent(self, SalesActivity.class));
                return true;
            case R.id.menu_main_report:
                startActivity(new Intent(self, ReportActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


}
