package com.palungo.coffee.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.palungo.coffee.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashActivity extends Activity {

    SplashActivity self;

    @Bind(R.id.activity_splash_logo)
    ImageView mSplashLogo;

    @Bind(R.id.activity_splash_background)
    ImageView mSplashBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        self = this;

        ButterKnife.bind(this);

        Glide.with(this).load(R.drawable.coffee_background).into(mSplashBackground);

        // Animate View
        ViewAnimator.animate(mSplashLogo)
                .dp().translationY(200, 0)
                .alpha(0, 1)
                .fadeIn()
                .duration(800)
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(self, LoginActivity.class));
                                self.finish();
                            }
                        }, 1000);
                    }
                })
                .start();
    }
}
