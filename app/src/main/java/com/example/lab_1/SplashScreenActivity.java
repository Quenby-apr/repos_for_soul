package com.example.lab_1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

import game.Cell;

public class SplashScreenActivity extends Activity implements View.OnTouchListener {
    int width_activity;
    int height_activity;
    int cellCount = 12;
    int survivors = cellCount;
    Point[] points = new Point[cellCount];
    int[] arrayColors = {Color.WHITE, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.GREEN};
    ArrayList<Cell> cells;

    private void createCells() {
        boolean layering= false;
        Random rnd = new Random();
        for (int i = cellCount-survivors; i < cellCount; i++) {
            int colorIndex = rnd.nextInt(arrayColors.length);
            int point_x = rnd.nextInt(width_activity * 3 / 4) + 60;
            int point_y = rnd.nextInt(height_activity * 3 / 4) + 60;
            for (int k = 0; k < cells.size();k++) {
                if (checkLayering(point_x, point_y, cells.get(0).getSize(), cells.get(k).getPoint().x,cells.get(k).getPoint().y,cells.get(k).getSize())) {
                    layering = true;
                    break;
                }
            }
            if (layering) {
                layering=false;
                i--;
                continue;
            }
            cells.add(new Cell(i+1, new Point(point_x, point_y), arrayColors[colorIndex]));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cells = new ArrayList<>();
        DrawView dv = new DrawView(this);
        dv.setOnTouchListener(this);
        setContentView(dv);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width_activity = size.x;
        height_activity = size.y;
        createCells();
    }

    private boolean checkLayering(int x, int y,int size, int x2, int y2, int size2) {
        if ((x>=x2 && x<=x2+size2) && (y>=y2 && y<=y2+size2))
            return true;
        if ((x+size>=x2 && x+size<=x2+size2) && (y>=y2 && y<=y2+size2))
            return true;
        if ((x>=x2 && x<=x2+size2) && (y+size>=y2 && y+size<=y2+size2))
            return true;
        if ((x+size>=x2 && x+size<=x2+size2) && (y+size>=y2 && y+size<=y2+size2))
            return true;
        return false;
    }

    private int checkCoordinates(int number, float cur_x, float cur_y, Point cellPoint, int length) {
        int top_x = cellPoint.x;
        int top_y = cellPoint.y;
        float relative_x = cur_x - top_x;
        float relative_y = cur_y - top_y;
        if (relative_x <= 0|| relative_x >= length) return 0;
        if (relative_y <= 0|| relative_y >= length) return 0;
        if (number != cellCount-survivors+1) return 1;
        return 2;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (cells == null || cells.size() == 0) return true;
        float touch_x = motionEvent.getX();
        float touch_y = motionEvent.getY();
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            for (Cell cell : cells) {
                if (checkCoordinates(cell.getNumber(), touch_x, touch_y, cell.getPoint(), cell.getSize())==2) {
                    cells.clear();
                    survivors--;
                    createCells();
                    return true;
                }
                if (checkCoordinates(cell.getNumber(), touch_x, touch_y, cell.getPoint(), cell.getSize())==1) {
                    cells.clear();
                    survivors =cellCount;
                    createCells();
                    return true;
                }
            }
        }

        return true;
    }

    private void changeActivity() {
        new Handler(getMainLooper()).post(() -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    class DrawView extends SurfaceView implements SurfaceHolder.Callback {
        private DrawThread drawThread;

        public DrawView(Context context) {
            super(context);
            getHolder().addCallback(this);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            drawThread = new DrawThread(getHolder());
            drawThread.setRunning(true);
            drawThread.start();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            boolean retry = true;
            drawThread.setRunning(false);
            while (retry) {
                try {
                    drawThread.join();
                    retry = false;
                } catch (InterruptedException e) {
                }
            }
        }

        class DrawThread extends Thread {

            private boolean running = false;
            private final SurfaceHolder surfaceHolder;
            private final Bitmap bitmap;
            private final Paint paint;

            public DrawThread(SurfaceHolder surfaceHolder) {
                this.surfaceHolder = surfaceHolder;
                Bitmap bitmapSource = BitmapFactory.decodeResource(getResources(), R.drawable.background);
                bitmap = Bitmap.createBitmap(bitmapSource, 0, 0, width_activity, height_activity);
                paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            }

            public void setRunning(boolean running) {
                this.running = running;
            }

            @Override
            public void run() {
                Canvas canvas;
                while (running) {
                    canvas = null;
                    try {
                        canvas = surfaceHolder.lockCanvas(null);
                        if (canvas == null)
                            continue;
                        canvas.drawBitmap(bitmap, 0, 0, paint);
                        if (cells == null) continue;
                        if (cells.size() == 0) {
                            setRunning(false);
                            changeActivity();
                        } else {
                            for (Cell cell : cells) {
                                cell.draw(canvas);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (canvas != null) {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }
        }
    }
}

