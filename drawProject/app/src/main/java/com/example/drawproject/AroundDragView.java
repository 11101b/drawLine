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
        //????????????????????????(200, 200)??????
        mpath.lineTo(centX + 50, centY + 50);
        //???mpath????????????????????? mpath.lineTo(100, 100);??????
        mpath.lineTo(centX - 50, centY + 50);
        mpath.close();
        //??????path??????
//        canvas.drawPath(mpath, painty);

        degrees = (float) Math.toDegrees(Math.atan((double) (lastX - fristX) / (lastY - fristY)));
        if (degrees==0)
            degrees = 180;
        Log.d("-----??????", (lastX - fristX) + "/" + (lastY - fristY) + "===" + Math.atan((double) (lastX - fristX) / (lastY - fristY)) + "//" + (float) Math.toDegrees(Math.atan((double) (lastX - fristX) / (lastY - fristY))));
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
        matrix.postTranslate(-mBitmap.getWidth() / 2, -mBitmap.getHeight() / 2);//??????1
        matrix.postRotate(degrees + 90 - 45);//??????2
        matrix.postTranslate(centX, centY);//??????3  ??????????????????
        canvas.drawBitmap(mBitmap, matrix, null);//??????4
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
     * ??????
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
     * ????????????
     * @param       x????????????x??????
     * @param       y????????????y??????
     * @param       w???????????????????????????
     * @param       h???????????????????????????
     * @param       bx????????????x??????
     * @param       by????????????y??????
     *
     * @return      null
     ------------------------------------*/

    public static void drawImage(Canvas canvas, Bitmap blt, int x, int y,
                                 int w, int h, int bx, int by) {
        Rect src = new Rect();// ?????? >>?????????
        Rect dst = new Rect();// ?????? >>????????????

        src.left = bx;
        src.top = by;
        src.right = bx + w;
        src.bottom = by + h;

        dst.left = x;
        dst.top = y;
        dst.right = x + w;
        dst.bottom = y + h;
        // ???????????????????????????????????????--?????????/????????????????????????????????????
        // ??????????????????????????? ?????????????????????????????????????????????????????????????????????????????????
        canvas.drawBitmap(blt, null, dst, null);
        src = null;
        dst = null;
    }

    /**
     * ????????????Bitmap
     *
     * @param canvas ??????
     * @param bitmap ??????
     * @param x      ????????????x??????
     * @param y      ????????????y??????
     */

    public static void drawImage(Canvas canvas, Bitmap bitmap, int x, int y) {
        // ???????????? ???bitmap????????????????????? x,y???
        canvas.drawBitmap(bitmap, x, y, null);
    }
}