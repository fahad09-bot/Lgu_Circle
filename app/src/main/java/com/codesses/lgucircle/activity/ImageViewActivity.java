package com.codesses.lgucircle.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.databinding.ActivityImageViewBinding;


public class ImageViewActivity extends AppCompatActivity {

    //    TODO: Context
    private AppCompatActivity mContext;

    //    TODO: View binding
    private ActivityImageViewBinding binding;

    //    TODO: Variables
    private String imageUri = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
        binding = DataBindingUtil.setContentView(mContext, R.layout.activity_image_view);

//        TODO: Getting intent & show full screen image
        imageUri = getIntent().getStringExtra(getString(R.string.intent_open_full_screen_image));
        Glide.with(mContext).load(imageUri).into(binding.imageView);


//        TODO: Click listener
        binding.close.setOnClickListener(v ->
                finish());

    }
}