package com.lh.imbilibili.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.lh.imbilibili.R;

/**
 * Created by liuhui on 2016/7/9.
 */
public class ForegroundLinearLayout extends LinearLayout {

    private Drawable foreground;
    private boolean foregroundSizeChange = false;
    private Rect selfRect;
    private Rect overlayRect;

    public ForegroundLinearLayout(Context context) {
        super(context);
    }

    public ForegroundLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ForegroundLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            selfRect = new Rect();
            overlayRect = new Rect();
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ForegroundLayout);
            Drawable drawable = typedArray.getDrawable(R.styleable.ForegroundLayout_foreground);
            setForeground(drawable);
            typedArray.recycle();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        foregroundSizeChange = changed;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        foregroundSizeChange = true;
    }

    @Override
    public void setForeground(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.setForeground(drawable);
        } else {
            if (foreground != drawable) {
                if (foreground != null) {
                    foreground.setCallback(null);
                    unscheduleDrawable(foreground);
                }
                foreground = drawable;
                if (drawable != null) {
                    setWillNotDraw(false);
                    drawable.setCallback(this);
                    if (drawable.isStateful()) {
                        drawable.setState(getDrawableState());
                    }
                } else {
                    setWillNotDraw(true);
                }
                requestLayout();
                invalidate();
            }
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return super.verifyDrawable(who);
        } else {
            return super.verifyDrawable(who) || who == foreground;
        }
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (foreground != null && foreground.isStateful()) {
                foreground.setState(getDrawableState());
            }
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (foreground != null && foreground.isStateful()) {
                foreground.setState(getDrawableState());
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (foreground != null) {
                if (foregroundSizeChange) {
                    foregroundSizeChange = false;
                    int w = getRight() - getLeft();
                    int h = getBottom() - getTop();
                    selfRect.set(getPaddingLeft(), getPaddingTop(),
                            w - getPaddingRight(), h - getPaddingBottom());
                    Gravity.apply(Gravity.FILL, foreground.getIntrinsicWidth(),
                            foreground.getIntrinsicHeight(), selfRect, overlayRect);
                    foreground.setBounds(overlayRect);
                }
                foreground.draw(canvas);
            }
        }
    }

    @Override
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (foreground != null) {
                    foreground.setHotspot(x, y);
                }
            }
        }
    }
}
