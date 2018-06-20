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
import android.view.animation.LinearInterpolator;

import com.example.lib_view.R;


/**
 * @author chenjiawang
 * @package com.example.lib_view.tick
 * @fileName CTickView
 * @date on 2018/6/14
 * @describe TODO
 * @email chen-jw@crystal-optech.com
 */

public class CTickView extends View {
    private int mWidth;
    private int mHeight;

    private int mColor;//图案颜色
    private int mColorCircle;//背景颜色
    private float strokeWidth;//图案宽度
    private Path mPathCircle;//背景绘图
    private Path mPathStar;//图案绘图
    private Paint mPaintCircle;//背景画笔
    private Paint mPaintStar;//图案画笔
    private PathMeasure mStarPathMeasure;
    private ValueAnimator mStarAnimator;

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
    public CTickView(Context context){
        this(context, null);
    }

    public CTickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CTickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.lib_ui_view_CTickView);
        mColor = typedArray.getColor(R.styleable.lib_ui_view_CTickView_lib_ui_view_ctv_color, Color.parseColor("#000000"));
        mColorCircle = typedArray.getColor(R.styleable.lib_ui_view_CTickView_lib_ui_view_ctr_colorCircle,Color.parseColor("#47b018"));
        strokeWidth = typedArray.getDimension(R.styleable.lib_ui_view_CTickView_lib_ui_view_ctr_strokeWidth,3);
        typedArray.recycle();
        //初始化
        init();
    }

    public void init(){
        mPathCircle = new Path();
        mPathStar = new Path();
        mStarPathMeasure = new PathMeasure();

        mPaintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCircle.setColor(mColorCircle);

        mPaintStar = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintStar.setColor(mColor);
        mPaintStar.setStyle(Paint.Style.STROKE);
        mPaintStar.setStrokeWidth(strokeWidth);//设置线条宽度

        mStarAnimator = ValueAnimator.ofFloat(0f,1f);
        mStarAnimator.setDuration(3000);//持续时间
        mStarAnimator.setInterpolator(new LinearInterpolator());//线性均匀改变
        mStarAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                factor = (float)animation.getAnimatedValue();
                postInvalidate();//实现界面刷新
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mWidth,mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas){
        mPathStar.moveTo(mWidth * scaleAX,mHeight * scaleAY);
        mPathStar.lineTo(mWidth * scaleBX,mHeight * scaleBY);
        mPathStar.lineTo(mWidth * scaleCX,mHeight * scaleCY);
        mPathStar.lineTo(mWidth * scaleDX,mHeight * scaleDY);
        mPathStar.lineTo(mWidth * scaleEX,mHeight * scaleEY);
        mPathStar.close();
        mStarPathMeasure.setPath(mPathStar,false);
        mStarPathMeasure.getSegment(0, factor * mStarPathMeasure.getLength(), mPathCircle, true);
        mPathCircle.rLineTo(0,0);
        canvas.drawCircle(mWidth / 2f, mWidth / 2f, mWidth / 2f, mPaintCircle);
        canvas.drawPath(mPathCircle,mPaintStar);
    }

    public void start() {
        stop();
        mPathCircle = new Path();
        if (mStarAnimator != null) {
            mStarAnimator.start();
        }
    }

    /**
     * Stop animation
     */
    public void stop() {
        if (mStarAnimator != null) {
            mStarAnimator.end();
        }
    }
}
