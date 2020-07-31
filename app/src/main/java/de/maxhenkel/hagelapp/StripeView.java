package de.maxhenkel.hagelapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class StripeView extends View {

    private Paint thickPaint;
    private Paint thinPaint;

    private int stripeCount;
    private boolean vertical;
    private int backgroundColor;

    public StripeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        thickPaint = new Paint();
        thickPaint.setAntiAlias(true);
        thickPaint.setColor(Color.BLACK);
        thickPaint.setStyle(Paint.Style.STROKE);
        thickPaint.setStrokeWidth(16F);

        thinPaint = new Paint();
        thinPaint.setAntiAlias(true);
        thinPaint.setColor(Color.BLACK);
        thinPaint.setStyle(Paint.Style.STROKE);
        thinPaint.setStrokeWidth(5F);

        backgroundColor = Color.WHITE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(backgroundColor);

        if (vertical) {
            float stripeSpacing = (float) getWidth() / stripeCount;

            for (int i = 0; i <= stripeCount; i++) {
                if (i % 3 == 0) {
                    canvas.drawLine(i * stripeSpacing, 0F, i * stripeSpacing, getHeight(), thickPaint);
                } else {
                    canvas.drawLine(i * stripeSpacing, 0F, i * stripeSpacing, getHeight(), thinPaint);
                }
            }
        } else {
            float stripeSpacing = (float) getHeight() / stripeCount;

            for (int i = 0; i <= stripeCount; i++) {
                if (i % 3 == 0) {
                    canvas.drawLine(0F, i * stripeSpacing, getHeight(), i * stripeSpacing, thickPaint);
                } else {
                    canvas.drawLine(0F, i * stripeSpacing, getWidth(), i * stripeSpacing, thinPaint);
                }
            }
        }
    }

    public void setBigStripeThickness(int thickness) {
        thickPaint.setStrokeWidth(thickness);
        invalidate();
    }

    public void setThinStripeThickness(int thickness) {
        thinPaint.setStrokeWidth(thickness);
        invalidate();
    }

    public void setStripeCount(int stripeCount) {
        this.stripeCount = stripeCount;
        invalidate();
    }

    public void setStripeColor(int stripeColor) {
        thinPaint.setColor(stripeColor);
        thickPaint.setColor(stripeColor);
        invalidate();
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
        invalidate();
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        invalidate();
    }
}
