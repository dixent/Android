package com.example.painter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.FileFilter;
import java.io.InputStream;
import java.util.ArrayList;

public class PaintView extends View {
    public static int BRUSH_SIZE = 20;
    public static final int DEFAULT_COLOR = Color.RED;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    public static final int GREEN = Color.GREEN;
    private static final float TOUCH_TOLERANCE = 4;
    private boolean blur;
    private boolean straight = false;
    public boolean fillFigure = false;
    private float x, y;
    private MaskFilter maskEmboss;
    private MaskFilter maskBlur;
    private Paint paint;
    private Paint fillPaint;
    private Path path;
    private Bitmap bitmap;
    private Canvas canvas;
    private ArrayList<Object> pathsAndColors = new ArrayList<>();
    private int currentColor;
    private int strokeWidth;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private Paint bitmapPaint = new Paint(Paint.DITHER_FLAG);
    private String currentDrawer = "line";


    public PaintView(Context context) {
        super(context, null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(DEFAULT_COLOR);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setXfermode(null);
        paint.setAlpha(0xff);

        fillPaint = new Paint();
        fillPaint.setColor(GREEN);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setXfermode(null);
        fillPaint.setAlpha(0xff);

        maskEmboss = new EmbossMaskFilter(new float[] {1, 1, 1}, 0.4f, 6, 3.5f);
        maskBlur = new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);
    }

    public void init(DisplayMetrics metrics) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);

        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;
    }

    public void setBitmapBackground(InputStream imageStream) {
        bitmap = BitmapFactory.decodeStream(imageStream);
        invalidate();
    }

    public void setBitmapBackground(Bitmap photoBitmap) {
        bitmap = photoBitmap;
        invalidate();
    }


    public void normal() {
        blur = false;
    }

    public void blur() {
        blur = true;
    }

    public void clear() {
        pathsAndColors.clear();
        normal();
        invalidate();
    }

    public void setDrawer(String figure) {
        currentDrawer = figure;
        straight = false;
    }

    public void setStraight() {
        straight = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.drawColor(backgroundColor);
        canvas.drawBitmap(bitmap, null, new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), null);

        for (Object object : pathsAndColors) {
            if (object.getClass().getSimpleName().equals("PointerPath")) {
                PointerPath path = (PointerPath) object;
                paint.setColor(path.color);
                paint.setStrokeWidth(path.strokeWidth);
                paint.setMaskFilter(null);

                if (path.blur)
                    paint.setMaskFilter(maskBlur);

                canvas.drawPath(path.path, paint);

                if (path.fill) {
                    canvas.drawPath(path.path, fillPaint);
                }
            } else {
                //ColorMask mask = (ColorMask) object;
                //mask.fill(getDrawingCache(true));
                //this.get
            }
        }

        canvas.restore();
    }

    private void touchStart(float x, float y) {
        if (currentDrawer.equals("fill")) {
            addColorMask(x, y);
        } else {
            path = new Path();

            PointerPath pointerPath = new PointerPath(currentColor, fillFigure, blur, strokeWidth, path);
            pathsAndColors.add(pointerPath);

            path.reset();
            path.moveTo(x, y);
            this.x = x;
            this.y = y;
        }
    }

    private void touchMove(float x, float y) {
        switch(currentDrawer) {
            case "line":
                drawLine(x, y);
                break;
            case "rectangle":
                drawRectangle(x, y);
                break;
            case "oval":
                drawOval(x, y);
                break;
        }
    }

    private void addColorMask(float x, float y) {
        Point point = new Point((int) x, (int) y);
        int currentPixelColor = bitmap.getPixel(point.x, point.y);
        pathsAndColors.add(new ColorMask(point, currentPixelColor, GREEN));
    }

    private void drawLine(float x, float y) {
        if (straight) {
            resetPath();
            path.lineTo(x, y);
        } else {
            float dx = Math.abs(x - this.x);
            float dy = Math.abs(y - this.y);

            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                path.quadTo(this.x, this.y, (x + this.x) / 2, (y + this.y) / 2);
                this.x = x;
                this.y = y;
            }
        }

    }

    private void drawRectangle(float x, float y) {
        resetPath();

        float[] coordinates = prepareCoordinates(x, y);

        path.addRect(this.x, this.y, coordinates[0], coordinates[1], Path.Direction.CW);
    }

    private void drawOval(float x, float y) {
        resetPath();

        float[] coordinates = prepareCoordinates(x, y);

        path.addOval(this.x, this.y, coordinates[0], coordinates[1], Path.Direction.CW);
    }

    private float[] prepareCoordinates(float x, float y) {
        if (straight) {
            float widthSquare = Math.abs(x - this.x) > Math.abs(y - this.y) ? y - this.y : x - this.x;
            x = this.x + widthSquare;
            y = this.y + widthSquare;
        }
        return new float[] { x, y };
    }

    private void resetPath() {
        path.reset();
        path.moveTo(this.x, this.y);
    }

    private void touchUp(float x, float y) {
      // path.lineTo(x, y);
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                touchStart(x, y);
                invalidate();
                Log.i("state", "click down");
                break;
            case MotionEvent.ACTION_MOVE :
                touchMove(x, y);
                invalidate();
                Log.i("state", "click move");
                break;
            case MotionEvent.ACTION_UP :
                touchUp(x, y);
                invalidate();
                Log.i("state", "click up");
                break;
        }

        return true;
    }
}
