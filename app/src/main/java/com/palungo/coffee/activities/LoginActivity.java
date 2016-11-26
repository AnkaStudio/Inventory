package com.palungo.coffee.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.palungo.coffee.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    LoginActivity self;

    @Bind(R.id.activity_login_logo)
    ImageView mLoginView;

    @Bind(R.id.activity_login_background)
    ImageView mLoginBackground;

    @Bind(R.id.activity_login_password_layout)
    View mPasswordView;

    @Bind(R.id.activity_login_username_layout)
    View mUsernameView;

    @Bind(R.id.activity_login_submit)
    Button mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        self = this;
        ButterKnife.bind(this);

        // Set Login
        Glide.with(this).load(R.drawable.coffee_background).into(mLoginBackground);
        Glide.with(this).load(R.drawable.logo).into(mLoginView);

        // Animate View

        ViewAnimator.animate(mUsernameView)
                .dp().translationY(400, 0)
                .alpha(0, 1)
                .fadeIn()
                .duration(500)
                .andAnimate(mPasswordView)
                .dp().translationY(600, 0)
                .alpha(0, 1)
                .fadeIn()
                .duration(700)
                .andAnimate(mSubmitButton)
                .dp().translationY(800, 0)
                .alpha(0, 1)
                .fadeIn()
                .duration(900)
                .start();

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Animate View
                ViewAnimator.animate(mLoginView)
                        .dp().translationY(0, -200)
                        .alpha(0, 1)
                        .fadeOut()
                        .duration(400)
                        .andAnimate(mSubmitButton)
                        .dp().translationY(0, 800)
                        .alpha(1, 0)
                        .fadeOut()
                        .duration(400)
                        .andAnimate(mPasswordView)
                        .dp().translationY(0, 600)
                        .alpha(1, 0)
                        .fadeOut()
                        .duration(500)
                        .andAnimate(mUsernameView)
                        .dp().translationY(0, 400)
                        .alpha(1, 0)
                        .fadeOut()
                        .duration(600)
                        .onStop(new AnimationListener.Stop() {
                            @Override
                            public void onStop() {
                                startActivity(new Intent(self, MainActivity.class));
                                finish();
                            }
                        })
                        .start();
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
