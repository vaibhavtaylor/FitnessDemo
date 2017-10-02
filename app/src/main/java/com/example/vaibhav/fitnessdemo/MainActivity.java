package com.example.vaibhav.fitnessdemo;

import android.animation.Animator;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skyfishjy.library.RippleBackground;

import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;

public class MainActivity extends FragmentActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 4;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    /**
     * instance variables
     */
    private FloatingActionButton goButton;
    private TextView goButtonText, timerText;
    private RevealFrameLayout revealView;
    private ImageView profileIcon;
    private int btnStatus = 0;
    private Animation animationFadeOut, circleFadeOut;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    private ImageView goBack;
    private LinearLayout profileView;
    private int taskCounter = 1;
    private RippleBackground rippleBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setClipToPadding(false);

        //initialising instance variables
        goButton = (FloatingActionButton) findViewById(R.id.goButton);
        goButtonText = (TextView) findViewById(R.id.goButtonText);
        profileIcon = (ImageView) findViewById(R.id.profileIcon);
        profileView = (LinearLayout) findViewById(R.id.profileView);
        revealView = (RevealFrameLayout) findViewById(R.id.revealView);
        animationFadeOut = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fadeout);
        circleFadeOut = AnimationUtils.loadAnimation(MainActivity.this, R.anim.circle_fade);
        timerText = (TextView) findViewById(R.id.timeText);
        goBack = (ImageView) findViewById(R.id.goBack);
        rippleBackground=(RippleBackground)findViewById(R.id.fading);

        //adding click listener on Go Button to start the timer
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //depending on which task is selected the timer is incremented by that amount
                if (mPager.getCurrentItem() == 0)
                    taskCounter = 1;
                else if (mPager.getCurrentItem() == 1)
                    taskCounter = 2;
                else if (mPager.getCurrentItem() == 2)
                    taskCounter = 5;
                else if(mPager.getCurrentItem() == 3)
                    taskCounter = 10;

                if(goButtonText.getText().toString().equals("GO")) {
                    timerText.setText("00:00:00");
                    startTime = SystemClock.uptimeMillis();
                    customHandler.postDelayed(updateTimerThread, 1000);
                    rippleBackground.startRippleAnimation();
                }
                else {
                    //when the timer is stopped we remove the time counter callback and stop animation
                    customHandler.removeCallbacks(updateTimerThread);
                    rippleBackground.stopRippleAnimation();
                }

                //adding text fading animation on Go Button
                goButtonText.startAnimation(animationFadeOut);

            }
        });

        //showing profile view on clicking the profile icon
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileActivity();
            }
        });

        //going back to main view when clicking the above arrow button on profile view
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //setting the radius and center points for the animation
                int centerX = revealView.getLeft();
                int centerY = revealView.getTop();
                float radius = Math.max(revealView.getWidth(), revealView.getHeight()) * 2.0f;

                //check if the profile view is visible or no
                if(revealView.getVisibility() == View.GONE) {
                    revealView.setVisibility(View.VISIBLE);
                }
                else {
                    revealView.setVisibility(View.GONE);
                }

                // Android native animator
                Animator animator =
                        ViewAnimationUtils.createCircularReveal(profileView, centerX, centerY, radius, 0);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(1000);
                animator.start();

            }
        });

        //setting animation listener
        animationFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(goButtonText.getText().toString().equals("GO")) {
                    goButtonText.setText("STOP");
                }
                else {
                    goButtonText.setText("GO");
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    //timer update counter thread
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = (SystemClock.uptimeMillis() - startTime) * taskCounter;
            updatedTime = timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = (secs % 60);
            int milliseconds = (int) (updatedTime % 100);
            timerText.setText("" + mins + ":"
                    + String.format("%02d", secs) + ":"
                    + String.format("%02d", milliseconds));
            customHandler.postDelayed(this, 1000);
        }
    };

    //function to open profile view
    public void openProfileActivity() {

        //setting the center and radius point of the animation
        int centerX = revealView.getRight();
        int centerY = revealView.getBottom();
        float radius = Math.max(profileIcon.getWidth(), profileIcon.getHeight()) * 10.0f;

        //revealing the profile view
        revealView.setVisibility(View.VISIBLE);

        // Android native animator
        Animator animator =
                ViewAnimationUtils.createCircularReveal(profileView, centerX, centerY, 0, radius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(1000);
        animator.start();

    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ScreenSlidePageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
