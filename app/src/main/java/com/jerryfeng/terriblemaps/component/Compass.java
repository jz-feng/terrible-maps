package com.jerryfeng.terriblemaps.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.jerryfeng.terriblemaps.R;

/**
 * Created by Kevin on 04/10/2015.
 */
public class Compass extends ImageView {

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
        BitmapDrawable bd = new BitmapDrawable(getResources(), bitmap);
        bd.setAntiAlias(false);

        this.setImageBitmap(bd.getBitmap());
    }

    private void setDirection(int degree) {
        this.setRotation(degree);
    }

}
