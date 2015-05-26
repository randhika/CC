package com.ly.cc.custview.imagePainting;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.ly.cc.R;

/**
 * Created by LeeeYou on 2015/5/26.
 */
public class RoundImageView extends ImageView {

    /**
     * ͼƬ�����ͣ�Բ��orԲ��
     */
    private int type;
    public static final int TYPE_CIRCLE = 0;
    public static final int TYPE_ROUND = 1;

    private static final int BORDER_RADIUS_DEFAULT = 10;// Բ�Ǵ�С��Ĭ��ֵ

    private int mBorderRadius;//Բ�ǵĴ�С

    private Paint mBitmapPaint;//��ͼ��Paint

    private int mRadius;// Բ�ǵİ뾶

    private Matrix matrix;//3x3 ������Ҫ������С�Ŵ�

    private BitmapShader mBitmapShader;//��Ⱦͼ��ʹ��ͼ��Ϊ����ͼ����ɫ

    private int mWidth;

    private RectF mRoundRect;

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        matrix = new Matrix();
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);

        ta.getDimensionPixelSize(R.styleable.RoundImageView_borderRadius,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        BORDER_RADIUS_DEFAULT,
                        getResources().getDisplayMetrics()));

        type = ta.getInt(R.styleable.RoundImageView_type, TYPE_CIRCLE);

        ta.recycle();
    }

    public RoundImageView(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /**
         *  ���������Բ�Σ���ǿ�Ƹı�view�Ŀ���һ�£���СֵΪ׼
         */
        if (type == TYPE_CIRCLE) {
            mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
            mRadius = mWidth / 2;
            setMeasuredDimension(mWidth, mWidth);
        }
    }

    private void setUpShader() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        Bitmap bmp = drawableToBitmap(drawable);

        mBitmapShader = new BitmapShader(bmp, TileMode.CLAMP, TileMode.CLAMP);
        float scale = 1.0f;

        if (type == TYPE_CIRCLE) {
            int bSize = Math.min(bmp.getWidth(), bmp.getHeight());
            scale = mWidth * 1.0f / bSize;
        } else if (type == TYPE_ROUND) {
            // ���ͼƬ�Ŀ����߸���view�Ŀ��߲�ƥ�䣬�������Ҫ���ŵı��������ź��ͼƬ�Ŀ��ߣ�һ��Ҫ��������view�Ŀ��ߣ�������������ȡ��ֵ
            if (!(bmp.getWidth() == getWidth()
                    && bmp.getHeight() == getHeight())) {
                scale = Math.max(getWidth() * 1.0f / bmp.getWidth(), getHeight() * 1.0f / bmp.getHeight());
            }
        }

        matrix.setScale(scale, scale); // shader�ı任��������������Ҫ���ڷŴ������С

        mBitmapShader.setLocalMatrix(matrix); // ���ñ任����

        mBitmapPaint.setShader(mBitmapShader); // ����shader
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }

        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getDrawable() == null) {
            return;
        }

        setUpShader();

        if (type == TYPE_ROUND) {
            canvas.drawRoundRect(mRoundRect, mBorderRadius, mBorderRadius, mBitmapPaint);
        } else if (type == TYPE_CIRCLE) {
            canvas.drawCircle(mRadius, mRadius, mRadius, mBitmapPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (type == TYPE_ROUND) {
            mRoundRect = new RectF(0, 0, w, h);
        }
    }

    public void setType(int type) {
        if (this.type != type) {
            this.type = type;
            if (this.type != TYPE_ROUND && this.type != TYPE_CIRCLE) {
                this.type = TYPE_CIRCLE;
            }
            requestLayout();
        }
    }

    public void setBorderRadius(int borderRadius) {
        final int pxVal = dp2px(borderRadius);
        if (this.mBorderRadius != pxVal) {
            this.mBorderRadius = pxVal;
            invalidate();
        }
    }

    private int dp2px(int borderRadius) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderRadius, getResources().getDisplayMetrics());
    }


    private static final String STATE_INSTANCE = "state_instance";
    private static final String STATE_TYPE = "state_type";
    private static final String STATE_BORDER_RADIUS = "state_border_radius";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_INSTANCE, super.onSaveInstanceState());
        bundle.putInt(STATE_TYPE, type);
        bundle.putInt(STATE_BORDER_RADIUS, mBorderRadius);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(((Bundle) state)
                    .getParcelable(STATE_INSTANCE));
            this.type = bundle.getInt(STATE_TYPE);
            this.mBorderRadius = bundle.getInt(STATE_BORDER_RADIUS);
        } else {
            super.onRestoreInstanceState(state);
        }

    }

}