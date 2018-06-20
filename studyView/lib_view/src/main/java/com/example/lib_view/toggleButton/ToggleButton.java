package com.example.lib_view.toggleButton;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;

import com.example.lib_view.R;

/**
 * @author chenjiawang
 * @package com.example.lib_view.toggleButton
 * @fileName ToggleButton
 * @date on 2018/6/20
 * @describe TODO
 * @email chen-jw@crystal-optech.com
 */

public class ToggleButton extends View {

    private int mHeight;
    private int mWidth;
    private int mColorNormal;//关闭颜色
    private int mColorLight;//打开颜色
    private int mColorPadding;//背景颜色
    private float mPaddingWidth;//背景宽度
    private int mDuration;//动画持续时间

    private Rect mRect;
    private RectF mRectF;
    private int mTouchSlop;//最小滑动距离

    private Paint mPaintNormal;
    private Paint mPaintLight;
    private Paint mPaintPadding;
    private Paint mPaintShader;

    private ValueAnimator mValueAnimator;
    private float factor = 1;//进度因子:0-1

    private OnToggleListener mListener;
    private boolean isOpen;//当前的位置
    private float dX, dY;
    private boolean isClickValid;//点击是否有效


    public ToggleButton(Context context) {
        this(context, null);
    }

    public ToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTypeArray(context, attrs);
        init(context);
    }

    private void initTypeArray(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.lib_ui_view_ToggleButton);
        mColorNormal = typedArray.getColor(R.styleable.lib_ui_view_ToggleButton_lib_ui_view_tbtn_colorNormal, Color.parseColor("#ffffff"));
        mColorLight = typedArray.getColor(R.styleable.lib_ui_view_ToggleButton_lib_ui_view_tbtn_colorLight, Color.parseColor("#FF4081"));
        mColorPadding = typedArray.getColor(R.styleable.lib_ui_view_ToggleButton_lib_ui_view_tbtn_colorPadding, Color.parseColor("#e3e4e5"));
        mPaddingWidth = typedArray.getDimension(R.styleable.lib_ui_view_ToggleButton_lib_ui_view_tbtn_padding, 3);
        mDuration = typedArray.getInteger(R.styleable.lib_ui_view_ToggleButton_lib_ui_view_tbtn_duration, 0);
        typedArray.recycle();
    }

    private void init(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);//禁用硬件加速，否则阴影的扩散setShadowLayer无效了
        }
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mRect = new Rect();
        mRectF = new RectF();
        mPaintNormal = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintNormal.setColor(mColorNormal);

        mPaintLight = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLight.setColor(mColorLight);

        mPaintPadding = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintPadding.setColor(mColorPadding);

        mPaintShader = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintShader.setColor(mColorNormal);
        mPaintShader.setShadowLayer(mPaddingWidth * 2, 0, 0, mColorPadding);
        mValueAnimator = ValueAnimator.ofFloat(0f, 1f);
        mValueAnimator.setDuration(mDuration);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                factor = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null && factor == 1) {
                    mListener.onToggle(isOpen);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean toggle = (factor == 1) == isOpen;
        float rectRadius = mHeight / 2f;
        //先画背景阴影图，再画内部颜色，将其覆盖
        mRect.set(0, 0, mWidth, mHeight);
        mRectF.set(mRect);
        canvas.drawRoundRect(mRectF, rectRadius, rectRadius, toggle ? mPaintLight : mPaintPadding);
        mRect.set((int) mPaddingWidth, (int) mPaddingWidth, (int) (mWidth - mPaddingWidth), (int) (mHeight - mPaddingWidth));
        mRectF.set(mRect);
        canvas.drawRoundRect(mRectF, rectRadius, rectRadius, toggle ? mPaintLight : mPaintNormal);
        float c0 = mHeight / 2;
        float c1 = mWidth - mHeight / 2;
        float start = !isOpen ? c1 : c0;
        float end = isOpen ? c1 : c0;
        float offsetX = start + (end - start) * factor;
        canvas.drawCircle(offsetX, mHeight / 2, mHeight / 2 - mPaddingWidth, mPaintShader);
    }

    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mWidth,mHeight);
    }

    public boolean onTouchEvent(MotionEvent event){
        if(!(factor ==0 || factor == 1)){
            return false;
        }
        float eX = event.getX();
        float eY = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                dX = eX;
                dY = eY;
                isClickValid = true;
            case MotionEvent.ACTION_MOVE:
                if (isClickValid && (Math.abs(eX - dX) > mTouchSlop || Math.abs(eY - dY) > mTouchSlop)) {
                    isClickValid = false;
                }
                return isClickValid;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (!isClickValid || eX < 0 || eX > mWidth || eY < 0 || eY > mHeight) {
                    return false;
                }
                isOpen = !isOpen;
                if (mDuration <= 0) {
                    invalidate();
                } else {
                    start();
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 开始动画
     */
    public void start() {
        stop();
        if (mValueAnimator != null) {
            mValueAnimator.start();
        }
    }

    /**
     * 停止动画
     */
    public void stop() {
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
        }
    }


    public interface OnToggleListener {
        /**
         * @param isOpen: isOpen
         */
        void onToggle(boolean isOpen);
    }
}
