package com.example.lib_view.tick;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import com.example.lib_view.R;

/**
 * @author chenjiawang
 * @package com.example.lib_view.tick
 * @fileName STickView
 * @date on 2018/6/19
 * @describe TODO
 * @email chen-jw@crystal-optech.com
 */

public class STickView extends View {

    private int mWidth;
    private int mHeight;

    private float factor;
    private float scaleAX = 0.1f;
    private float scaleAY = 0.4f;
    private float scaleBX = 0.8f;
    private float scaleBY = 0.4f;
    private float scaleCX = 0.3f;
    private float scaleCY = 0.8f;
    private float scaleDX = 0.5f;
    private float scaleDY = 0.2f;
    private float scaleEX = 0.7f;
    private float scaleEY = 0.8f;

    private int mColorStar;
    private int mColorCircle;
    private float mStrokeWidth;
    private Path mPathStar;
    private Path mPathCircle;
    private Paint mPaintStar;
    private Paint mPaintCircle;
    private PathMeasure mTickPathMeasure;
    private ValueAnimator mValueAnimator;

    public STickView(Context context) {
        this(context, null);
    }

    public STickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public STickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.lib_ui_view_CTickView);
        mColorStar = typedArray.getColor(R.styleable.lib_ui_view_CTickView_lib_ui_view_ctv_color, Color.parseColor("#ffffff"));
        mColorCircle = typedArray.getColor(R.styleable.lib_ui_view_CTickView_lib_ui_view_ctr_colorCircle, Color.parseColor("#47b018"));
        mStrokeWidth = typedArray.getDimension(R.styleable.lib_ui_view_CTickView_lib_ui_view_ctr_strokeWidth, 3);
        typedArray.recycle();
        init();
    }

    public void init() {
        mPathCircle = new Path();
        mPathStar = new Path();
        mTickPathMeasure = new PathMeasure();

        mPaintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCircle.setColor(mColorCircle);

        mPaintStar = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintStar.setColor(mColorStar);
        mPaintStar.setStyle(Paint.Style.STROKE);
        mPaintStar.setStrokeWidth(mStrokeWidth);

        mValueAnimator = ValueAnimator.ofFloat(0f, 1f);
        mValueAnimator.setDuration(3000);
        mValueAnimator.setInterpolator(new SpringScaleInterpolator(0.4f));
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mPaintCircle != null && mPaintStar != null) {
                    float alpha = 1f * animation.getDuration() / 3000;
                    alpha = Math.min(alpha, 1f);
                    alpha = Math.max(alpha, 0f);
                    mPaintCircle.setAlpha((int) (255 * alpha));
                    mPaintStar.setAlpha((int) (255 * alpha));
                }
                factor = (float) animation.getAnimatedValue();
                factor = factor / 1.27f;
                factor = Math.min(factor, 1f);
                factor = Math.max(factor, 0f);
                postInvalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float width = this.mWidth * 0.86f * factor;
        float height = this.mHeight * 0.86f * factor;
        float startX = (this.mWidth - width) / 2f;
        float startY = (this.mHeight - height) / 2f;
        //初始化path
        mPathStar.reset();
        mPathCircle.reset();
        mPathStar.moveTo(startX + width * scaleAX, startY + height * scaleAY);
        mPathStar.lineTo(startX + width * scaleBX, startY + height * scaleBY);
        mPathStar.lineTo(startX + width * scaleCX, startY + height * scaleCY);
        mPathStar.lineTo(startX + width * scaleDX, startY + height * scaleDY);
        mPathStar.lineTo(startX + width * scaleEX, startY + height * scaleEY);
        mPathStar.close();
        mTickPathMeasure.setPath(mPathStar, false);
        mTickPathMeasure.getSegment(0, mTickPathMeasure.getLength(), mPathStar, true);
        width = this.mWidth * factor;
        height = this.mHeight * factor;
        startX = (this.mWidth - width) / 2f;
        startY = (this.mHeight - height) / 2f;
        canvas.drawCircle(startX + width / 2f, startY + width / 2f, width / 2f, mPaintCircle);
        mPaintCircle.setStrokeWidth(mStrokeWidth * factor);
        canvas.drawPath(mPathStar, mPaintStar);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    /**
     * Start animation
     */
    public void start() {
        stop();
        mPathStar = new Path();
        if (mValueAnimator != null) {
            mValueAnimator.start();
        }
    }

    /**
     * Stop animation
     */
    public void stop() {
        if (mValueAnimator != null) {
            mValueAnimator.end();
        }
    }

    class SpringScaleInterpolator implements Interpolator {
        private float factor;

        SpringScaleInterpolator(float factor) {
            this.factor = factor;
        }

        @Override
        public float getInterpolation(float input) {
            return (float) (Math.pow(2, -10 * input) * Math.sin((input - factor / 4) * (2 * Math.PI) / factor) + 1);
        }
    }
}
