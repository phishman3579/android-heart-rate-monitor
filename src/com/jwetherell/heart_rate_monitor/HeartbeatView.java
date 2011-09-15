package com.jwetherell.heart_rate_monitor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;


/**
 * This class extends the View class and is designed draw the heartbeat image.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class HeartbeatView extends View {
	private static Bitmap greenBitmap = null;
	private static Bitmap redBitmap = null;

    public HeartbeatView(Context context, AttributeSet attr) {
        super(context,attr);

        greenBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.green_icon);
        redBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_icon);
    }

    public HeartbeatView(Context context) {
        super(context);

        greenBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.green_icon);
        redBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_icon);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(parentWidth, parentHeight);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
    	if (canvas==null) return;

        if (HeartRateMonitor.getCurrent()==HeartRateMonitor.TYPE.GREEN) canvas.drawBitmap(greenBitmap, 0, 0, null);
        else canvas.drawBitmap(redBitmap, 0, 0, null);
    }
}
