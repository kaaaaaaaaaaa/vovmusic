package com.vovmusic.uit.animations;

import android.content.Context;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.cardview.widget.CardView;

import com.vovmusic.uit.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ScaleAnimation {
    private Animation scaleUpAnimation, scaleDownAnimation;
    private Context context;

    private Button button;
    private ImageView imageView;
    private CardView cardView;
    private CircleImageView circleImageView;
    private RelativeLayout relativeLayout;

    public ScaleAnimation() {
    }

    public ScaleAnimation(Context context, Button button) {
        this.context = context;
        this.button = button;
    }

    public ScaleAnimation(Context context, ImageView imageView) {
        this.context = context;
        this.imageView = imageView;
    }

    public ScaleAnimation(Context context, CardView cardView) {
        this.context = context;
        this.cardView = cardView;
    }

    public ScaleAnimation(Context context, CircleImageView circleImageView) {
        this.context = context;
        this.circleImageView = circleImageView;
    }

    public ScaleAnimation(Context context, RelativeLayout relativelayout) {
        this.context = context;
        this.relativeLayout = relativelayout;
    }

    public void Event_Button() {
        this.scaleUpAnimation = AnimationUtils.loadAnimation(this.context, R.anim.scale_up);
        this.scaleDownAnimation = AnimationUtils.loadAnimation(this.context, R.anim.scale_down);

        this.button.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                button.startAnimation(scaleDownAnimation);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                button.startAnimation(scaleUpAnimation);
            }
            return false;
        });
    }

    public void Event_ImageView() {
        this.scaleUpAnimation = AnimationUtils.loadAnimation(this.context, R.anim.scale_up);
        this.scaleDownAnimation = AnimationUtils.loadAnimation(this.context, R.anim.scale_down);

        this.imageView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                imageView.startAnimation(scaleDownAnimation);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                imageView.startAnimation(scaleUpAnimation);
            }
            return false;
        });
    }

    public void Event_CardView() {
        this.scaleUpAnimation = AnimationUtils.loadAnimation(this.context, R.anim.scale_up);
        this.scaleDownAnimation = AnimationUtils.loadAnimation(this.context, R.anim.scale_down);

        this.cardView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                cardView.startAnimation(scaleDownAnimation);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                cardView.startAnimation(scaleUpAnimation);
            }
            return false;
        });
    }

    public void Event_CircleImageView() {
        this.scaleUpAnimation = AnimationUtils.loadAnimation(this.context, R.anim.scale_up);
        this.scaleDownAnimation = AnimationUtils.loadAnimation(this.context, R.anim.scale_down);

        this.circleImageView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                circleImageView.startAnimation(scaleDownAnimation);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                circleImageView.startAnimation(scaleUpAnimation);
            }
            return false;
        });
    }

    public void Event_RelativeLayout() {
        this.scaleUpAnimation = AnimationUtils.loadAnimation(this.context, R.anim.scale_up);
        this.scaleDownAnimation = AnimationUtils.loadAnimation(this.context, R.anim.scale_down);

        this.relativeLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                relativeLayout.startAnimation(scaleDownAnimation);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                relativeLayout.startAnimation(scaleUpAnimation);
            }
            return false;
        });
    }
}
