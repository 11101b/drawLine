package com.example.drawproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class AroundDragView extends View implements View.OnTouchListener {
    private static final String TAG = "ImageLight";
    protected int screenWidth;
    protected int screenHeight;
    protected int lastX;
    protected int lastY;
    protected int fristX;
    protected int fristY;
    protected int centX = 200;
    protected int centY = 200;

    private Bitmap mBitmap;

    private Context mContext;
    float degrees = 180f;


    boolean isFrist;

    protected Paint paint = new Paint();
    protected Paint painty = new Paint();
//    protected Paint mpath = new Paint();

    private Path mpath = new Path();
    int offset = 30;

    public AroundDragView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(this);
        this.mContext = context;

    }

    public AroundDragView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        this.mContext = context;
    }

    public AroundDragView(Context context) {
        super(context);
        setOnTouchListener(this);
        this.mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(4.0f);
        paint.setStyle(Paint.Style.STROKE);


        PathEffect effects = new DashPathEffect(new float[]{0, 10, 35, 2}, 1);
        paint.setPathEffect(effects);




        if (fristX == 0)
            fristX= this.getWidth()/2;
        if (lastX == 0)
            lastX= this.getWidth()/2;
        if (lastY == 0)
            lastY= this.getHeight();
        centX = (fristX + lastX) / 2;
        centY = (fristY + lastY) / 2;

        canvas.drawLine(fristX, fristY, centX, centY, paint);
        canvas.drawLine(lastX, lastY, centX, centY, paint);

        painty.setColor(Color.YELLOW);
        painty.setStrokeWidth(4.0f);
        painty.setStyle(Paint.Style.STROKE);




        canvas.drawCircle(fristX, fristY, 10, painty);
            canvas.drawCircle(lastX, lastY, 10, painty);


        mpath.reset();
        mpath.moveTo(centX - 50, centY - 50);
        //从起始位置划线到(200, 200)坐标
        mpath.lineTo(centX + 50, centY + 50);
        //将mpath封闭，也可以写 mpath.lineTo(100, 100);代替
        mpath.lineTo(centX - 50, centY + 50);
        mpath.close();
        //绘制path路径
//        canvas.drawPath(mpath, painty);

        degrees = (float) Math.toDegrees(Math.atan((double) (lastX - fristX) / (lastY - fristY)));
        if (degrees==0)
            degrees = 180;
        Log.d("-----角度", (lastX - fristX) + "/" + (lastY - fristY) + "===" + Math.atan((double) (lastX - fristX) / (lastY - fristY)) + "//" + (float) Math.toDegrees(Math.atan((double) (lastX - fristX) / (lastY - fristY))));
        if ((lastX - fristX) < 0 && (lastY - fristY) < 0) {
            degrees = 270 + (90 - degrees);
        } else if ((lastY - fristY) < 0) {
            degrees = Math.abs(degrees);
        } else if ((lastX - fristX) < 0) {
            degrees = Math.abs(degrees) + 180;

        } else if ((lastX - fristX) > 0 && (lastY - fristY) > 0) {
            degrees = 90 + (90 - degrees);

        }


        if (null == mBitmap)
            mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test);
//        canvas.drawBitmap(mBitmap, centX-(mBitmap.getWidth()/2), centY-(mBitmap.getHeight()/2), null);
        Matrix matrix = new Matrix();
        matrix.postTranslate(-mBitmap.getWidth() / 2, -mBitmap.getHeight() / 2);//步骤1
        matrix.postRotate(degrees + 90 - 45);//步骤2
        matrix.postTranslate(centX, centY);//步骤3  屏幕的中心点
        canvas.drawBitmap(mBitmap, matrix, null);//步骤4
        matrix.reset();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (Math.abs(event.getX()-lastX)<=100&&Math.abs(event.getY()-lastY)<=100){
                    lastX = (int) event.getX();
                    lastY = (int) event.getY();
                    isFrist =true;
                }
                if (Math.abs(event.getX()-fristX)<=100&&Math.abs(event.getY()-fristY)<=100){
                    fristX = (int) event.getX();
                    fristY = (int) event.getY();
                    isFrist =false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isFrist) {
                    lastX = (int) event.getX();
                    lastY = (int) event.getY();
                } else {
                    fristX = (int) event.getX();
                    fristY = (int) event.getY();
                }

                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        invalidate();
        return false;
    }

    /**
     * 移动
     *
     * @param v
     * @param dx
     */
    private void move(View v, int dx) {
        int left = v.getLeft() + dx;
        int right = v.getRight() + dx;
        if (left < 100 + offset) {
            left = 100 + offset;
            right = left + v.getWidth();
        }
        if (right > screenWidth - 100) {
            right = screenWidth - 100;
            left = right - v.getWidth();
        }
        v.layout(left, 0, right, getHeight());
    }
/*---------------------------------
     * 绘制图片
     * @param       x屏幕上的x坐标
     * @param       y屏幕上的y坐标
     * @param       w要绘制的图片的宽度
     * @param       h要绘制的图片的高度
     * @param       bx图片上的x坐标
     * @param       by图片上的y坐标
     *
     * @return      null
     ------------------------------------*/

    public static void drawImage(Canvas canvas, Bitmap blt, int x, int y,
                                 int w, int h, int bx, int by) {
        Rect src = new Rect();// 图片 >>原矩形
        Rect dst = new Rect();// 屏幕 >>目标矩形

        src.left = bx;
        src.top = by;
        src.right = bx + w;
        src.bottom = by + h;

        dst.left = x;
        dst.top = y;
        dst.right = x + w;
        dst.bottom = y + h;
        // 画出指定的位图，位图将自动--》缩放/自动转换，以填补目标矩形
        // 这个方法的意思就像 将一个位图按照需求重画一遍，画后的位图就是我们需要的了
        canvas.drawBitmap(blt, null, dst, null);
        src = null;
        dst = null;
    }

    /**
     * 绘制一个Bitmap
     *
     * @param canvas 画布
     * @param bitmap 图片
     * @param x      屏幕上的x坐标
     * @param y      屏幕上的y坐标
     */

    public static void drawImage(Canvas canvas, Bitmap bitmap, int x, int y) {
        // 绘制图像 将bitmap对象显示在坐标 x,y上
        canvas.drawBitmap(bitmap, x, y, null);
    }
}