package com.palungo.coffee.activities;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.palungo.coffee.GlobalApplication;
import com.palungo.coffee.R;
import com.palungo.coffee.adapters.InventoryListAdapter;
import com.palungo.coffee.helpers.Constant;
import com.palungo.coffee.helpers.DatabaseHelper;
import com.palungo.coffee.helpers.SimpleItemTouchHelper;
import com.palungo.coffee.interfaces.ItemRemovedListener;
import com.palungo.coffee.pojo.InventoryInfo;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InventoryActivity extends AppCompatActivity implements OnDateSelectedListener, ItemRemovedListener {

    InventoryActivity self;
    ArrayList<InventoryInfo> mInventoryList = new ArrayList<>();
    InventoryListAdapter mAdapter;
    DatabaseHelper mDBHelper;
    MaterialDialog mDialog;
    View mDialogView;
    EditText mInventoryQty, mInventoryName, mInventoryPrice;

    boolean flagOk;
    boolean isVisible = false;

    @Bind(R.id.activity_inventory_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.activity_inventory_background)
    ImageView mBackgroundImageView;

    @Bind(R.id.activity_inventory_error)
    View mErrorView;

    @Bind(R.id.activity_inventory_recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.activity_inventory_total_sum_view)
    TextView mTotalSumView;

    @Bind(R.id.activity_inventory_date)
    TextView mInventoryDate;

    @Bind(R.id.activity_inventory_fab)
    FloatingActionButton fabButton;

    @Bind(R.id.activity_inventory_calendar)
    MaterialCalendarView mMaterialCalendarView;

    public void addInventory(final InventoryInfo info, final int pos) {
        flagOk = false;
        String title;
        if (info == null) {
            title = "Add an Inventory";
        } else {
            title = "Edit an Inventory";
        }

        mDialog = new MaterialDialog.Builder(self)
                .title(title)
                .customView(R.layout.layout_add_inventory, true)
                .autoDismiss(false)
                .positiveColor(getResources().getColor(R.color.colorTurquoise))
                .negativeColor(getResources().getColor(R.color.colorPrimaryDark))
                .positiveText("Save")
                .negativeText("Cancel")
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mDialogView = dialog.getView();

                        mInventoryQty = (EditText) mDialogView.findViewById(R.id.layout_add_inventory_quantity);
                        mInventoryName = (EditText) mDialogView.findViewById(R.id.layout_add_inventory_name);
                        mInventoryPrice = (EditText) mDialogView.findViewById(R.id.layout_add_inventory_price);

                        if (mInventoryQty.getText().length() > 1) {
                            flagOk = true;
                        } else {
                            flagOk = false;
                            mInventoryQty.setError("Required");
                        }

                        if (mInventoryName.getText().length() > 1) {
                            flagOk = true;
                        } else {
                            flagOk = false;
                            mInventoryName.setError("Required");
                        }

                        if (mInventoryPrice.getText().length() > 1) {
                            flagOk = true;
                        } else {
                            flagOk = false;
                            mInventoryPrice.setError("Required");
                        }

                        if (flagOk) {

                            if (info == null) {
                                mDBHelper.addInventory(
                                        mInventoryName.getText().toString(),
                                        Integer.parseInt(mInventoryPrice.getText().toString()),
                                        Integer.parseInt(mInventoryQty.getText().toString()));
                            } else {
                                mDBHelper.updateInventory(
                                        info.getId(),
                                        mInventoryName.getText().toString(),
                                        Integer.parseInt(mInventoryPrice.getText().toString()),
                                        Integer.parseInt(mInventoryQty.getText().toString())
                                );
                            }

                            requestData(GlobalApplication.singleton.getDate(null, null, null));

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
            EditText name = (EditText) mDialog.getCustomView().findViewById(R.id.layout_add_inventory_name);
            name.setText(info.getName() + "");

            EditText price = (EditText) mDialog.getCustomView().findViewById(R.id.layout_add_inventory_price);
            price.setText(info.getPrice() + "");

            EditText qty = (EditText) mDialog.getCustomView().findViewById(R.id.layout_add_inventory_quantity);
            qty.setText(info.getQty() + "");
        }

        mDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        self = this;
        ButterKnife.bind(this);

        mDBHelper = DatabaseHelper.getInstance(this);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_inventory);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_left);
        }

        Glide.with(this.getApplicationContext())
                .load(R.drawable.coffee_background)
                .into(mBackgroundImageView);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.setAdapter(mAdapter);
                addInventory(null, -1);
            }
        });

        mAdapter = new InventoryListAdapter(this, mInventoryList, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelper(this, mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);

        mMaterialCalendarView.setOnDateChangedListener(this);
        mMaterialCalendarView.setDateSelected(new GregorianCalendar(), true);

        requestData(GlobalApplication.singleton.getDate(null, null, null));
    }

    private void requestData(String date) {

        try {
            mInventoryDate.setText(GlobalApplication.singleton.formatDate(date));
        } catch (ParseException e) {
            mInventoryDate.setText(date);
            e.printStackTrace();
        }

        mInventoryList.clear();
        mInventoryList.addAll(mDBHelper.getInventory(date));

        if (mInventoryList.size() <= 0) {
            mErrorView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);

            updateSum();
        } else {
            mErrorView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

            mAdapter.addData(mInventoryList);
            updateSum();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_options_date:
                toggleCalendar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void toggleCalendar() {
        if (isVisible) {
            ViewAnimator.animate(mMaterialCalendarView)
                    .alpha(1, 0)
                    .fadeOut()
                    .duration(50)
                    .onStop(new AnimationListener.Stop() {
                        @Override
                        public void onStop() {
                            isVisible = false;
                            mMaterialCalendarView.setVisibility(View.GONE);
                        }
                    })
                    .start();
        } else {
            mMaterialCalendarView.setVisibility(View.VISIBLE);
            ViewAnimator.animate(mMaterialCalendarView)
                    .alpha(0, 1)
                    .fadeIn()
                    .duration(50)
                    .onStop(new AnimationListener.Stop() {
                        @Override
                        public void onStop() {
                            isVisible = true;
                        }
                    })
                    .start();
        }
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        requestData(GlobalApplication.singleton.getDate(date.getYear(), date.getMonth(), date.getDay()));
        toggleCalendar();
    }

    @Override
    public void itemRemoved() {
        updateSum();
    }

    private void updateSum() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAdapter.getSum() != 0) {
                    ValueAnimator animator = new ValueAnimator();
                    animator.setObjectValues(0, mAdapter.getSum());
                    animator.setDuration(2500);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mTotalSumView.setText(Constant.RS + (int) animation.getAnimatedValue());
                        }
                    });
                    animator.start();
                } else {
                    mTotalSumView.setText(Constant.RS + 0);
                }
            }
        }, 500);
    }
}
