package com.palungo.coffee.activities;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.palungo.coffee.GlobalApplication;
import com.palungo.coffee.R;
import com.palungo.coffee.helpers.Constant;
import com.palungo.coffee.helpers.DatabaseHelper;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReportActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    ReportActivity self;
    DatabaseHelper mDBHelper;

    @Bind(R.id.activity_report_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.activity_report_background)
    ImageView mBackgroundImageView;

    @Bind(R.id.activity_report_from_date)
    EditText mFromDateEditText;

    @Bind(R.id.activity_report_to_date)
    EditText mToDateEditText;

    @Bind(R.id.activity_report_income)
    TextView mIncome;

    @Bind(R.id.activity_report_expenses)
    TextView mExpenses;

    @Bind(R.id.activity_report_error_view)
    View mErrorView;

    @Bind(R.id.activity_report_main_view)
    View mMainView;

    @Bind(R.id.activity_report_profit_loss)
    TextView mProfitLoss;

    DatePickerDialog mDatePicker;

    private int income, expenses, profitLoss;
    private String fromDate, toDate;
    private String focus = null;

    @OnClick(R.id.activity_report_generate_button)
    void generateReport() {
        mMainView.setVisibility(View.VISIBLE);

        income = mDBHelper.getTotalSales(fromDate, toDate);
        expenses = mDBHelper.getTotalInventory(fromDate, toDate);
        profitLoss = income - expenses;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ValueAnimator iAnimator = new ValueAnimator();
                iAnimator.setObjectValues(0, income);
                iAnimator.setDuration(2500);
                iAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mIncome.setText(Constant.RS + (int) animation.getAnimatedValue());
                    }
                });
                iAnimator.start();

                ValueAnimator eAnimator = new ValueAnimator();
                eAnimator.setObjectValues(0, expenses);
                eAnimator.setDuration(2500);
                eAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mExpenses.setText(Constant.RS + (int) animation.getAnimatedValue());
                    }
                });
                eAnimator.start();


                ValueAnimator plAnimator = new ValueAnimator();
                plAnimator.setObjectValues(0, profitLoss);
                plAnimator.setDuration(2500);
                plAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mProfitLoss.setText(Constant.RS + (int) animation.getAnimatedValue());
                    }
                });
                plAnimator.start();
            }
        }, 500);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        self = this;
        ButterKnife.bind(this);

        mDBHelper = DatabaseHelper.getInstance(this);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_report);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_left);
        }

        Glide.with(this.getApplicationContext())
                .load(R.drawable.coffee_background)
                .into(mBackgroundImageView);


        // Initialize DatePicker dialog;

        Calendar now = Calendar.getInstance();
        mDatePicker = DatePickerDialog.newInstance(
                self,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        // Disabling Selecting and editing an EditText
        fromDate = toDate = GlobalApplication.singleton.getDate(null, null, null);

        mFromDateEditText.setText(fromDate);
        mFromDateEditText.setFocusable(false);
        mFromDateEditText.setFocusableInTouchMode(false);
        mFromDateEditText.setLongClickable(false);
        mFromDateEditText.setInputType(0);

        mToDateEditText.setText(toDate);
        mToDateEditText.setFocusable(false);
        mToDateEditText.setFocusableInTouchMode(false);
        mToDateEditText.setLongClickable(false);
        mToDateEditText.setInputType(0);


        mFromDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focus = "fromDate";
                mDatePicker.show(getFragmentManager(), "DatePicker");
            }
        });

        mToDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focus = "toDate";
                mDatePicker.show(getFragmentManager(), "DatePicker");
            }
        });




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if (focus.equals("fromDate")) {
            fromDate = GlobalApplication.singleton.getDate(year, monthOfYear, dayOfMonth);
        } else if (focus.equals("toDate")) {
            toDate = GlobalApplication.singleton.getDate(year, monthOfYear, dayOfMonth);
        }

        mFromDateEditText.setText(fromDate);
        mToDateEditText.setText(toDate);
    }
}
