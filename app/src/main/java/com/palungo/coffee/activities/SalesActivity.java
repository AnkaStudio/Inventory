package com.palungo.coffee.activities;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
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
import com.palungo.coffee.adapters.SalesListAdapter;
import com.palungo.coffee.helpers.Constant;
import com.palungo.coffee.helpers.DatabaseHelper;
import com.palungo.coffee.helpers.SimpleItemTouchHelper;
import com.palungo.coffee.interfaces.ItemRemovedListener;
import com.palungo.coffee.pojo.SalesInfo;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SalesActivity extends AppCompatActivity implements OnDateSelectedListener, ItemRemovedListener {

    SalesActivity self;
    ArrayList<SalesInfo> mSalesInfoList = new ArrayList<>();
    SalesListAdapter mAdapter;
    DatabaseHelper mDBHelper;
    MaterialDialog mDialog;
    View mDialogView;
    EditText mInventoryQty, mInventoryName, mInventoryPrice;

    boolean flagOk;
    boolean isVisible = false;

    @Bind(R.id.activity_sales_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.activity_sales_background)
    ImageView mBackgroundImageView;

    @Bind(R.id.activity_sales_error)
    View mErrorView;

    @Bind(R.id.activity_sales_calendar)
    MaterialCalendarView mMaterialCalendarView;

    @Bind(R.id.activity_sales_recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.activity_sales_total_sum_view)
    TextView mTotalSumView;

    @Bind(R.id.activity_sales_date)
    TextView mSalesDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        self = this;
        ButterKnife.bind(this);

        mDBHelper = DatabaseHelper.getInstance(this);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_sales);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_left);
        }

        Glide.with(this.getApplicationContext())
                .load(R.drawable.coffee_background)
                .into(mBackgroundImageView);

        mAdapter = new SalesListAdapter(this, mSalesInfoList, this);
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
            mSalesDate.setText(GlobalApplication.singleton.formatDate(date));
        } catch (ParseException e) {
            mSalesDate.setText(date);
            e.printStackTrace();
        }

        mSalesInfoList.clear();
        mSalesInfoList.addAll(mDBHelper.getSales(date));

        if (mSalesInfoList.size() <= 0) {
            mErrorView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mTotalSumView.setText(Constant.RS + 0);
        } else {
            mErrorView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

            mAdapter.addData(mSalesInfoList);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ValueAnimator animator = new ValueAnimator();
                    animator.setObjectValues(0, mAdapter.getSum());
                    animator.setDuration(2500);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mTotalSumView.setText(Constant.RS + (int) animation.getAnimatedValue());
                        }
                    });
                    animator.start();
                }
            }, 500);

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
                    .duration(0)
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
                    .duration(0)
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

    }

    public void addSales(final SalesInfo info, final int pos) {
        flagOk = false;
        String title;
        if (info == null) {
            title = "Add Sales";
        } else {
            title = "Edit Sales";
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
                                // Not allowed to add new Sales
                            } else {
                                mDBHelper.updateSales(
                                        info.getId(),
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
            name.setFocusable(false);
            name.setTextColor(getResources().getColor(R.color.colorRed));
            name.setInputType(InputType.TYPE_NULL);

            EditText price = (EditText) mDialog.getCustomView().findViewById(R.id.layout_add_inventory_price);
            price.setText(info.getPrice() + "");
            price.setFocusable(false);
            price.setTextColor(getResources().getColor(R.color.colorRed));
            price.setInputType(InputType.TYPE_NULL);

            EditText qty = (EditText) mDialog.getCustomView().findViewById(R.id.layout_add_inventory_quantity);
            qty.setText(info.getQty() + "");
        }

        mDialog.show();
    }
}
