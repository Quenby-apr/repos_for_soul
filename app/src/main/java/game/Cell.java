package game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class Cell {
    private Point point;
    private final Paint paint;
    private int size;
    private int color;
    private int number;

    public Point getPoint() {
        return point;
    }

    public int getSize() {
        return size;
    }

    public int getNumber() {
        return number;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Cell(int number,Point startPoint, int color) {
        this.number = number;
        this.paint = new Paint();
        this.point = startPoint;
        this.size = 120;
        this.color = color;
    }

    public void draw(Canvas canvas) {
        paint.setColor(color);
        canvas.drawRect(point.x, point.y, point.x+size,point.y+size, paint);
        paint.setColor(Color.BLACK);
        paint.setTextSize(60);
        canvas.drawText(String.valueOf(number), point.x+size/4, point.y+size*2/3,paint);
    }
}
