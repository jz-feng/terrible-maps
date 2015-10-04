package com.jerryfeng.terriblemaps.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.jerryfeng.terriblemaps.R;

/**
 * Created by Kevin on 04/10/2015.
 */
public class Compass extends ImageView {

    private float mCurrentDegree = 0f;

    public Compass(Context context) {
        super(context);
        init();
    }

    public Compass(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Compass(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.compass);
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 40, bitmap.getHeight() * 40, false);

        this.setImageBitmap(bitmap);
    }

    public void setHeading(float degree) {
        this.setRotation(-degree+45);
    }

}
