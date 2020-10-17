package com.example.painter;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class ColorMask {
    private Point node;
    private int targetColor, replacementColor;

    public ColorMask(Point node, int targetColor, int replacementColor) {
        this.node = node;
        this.targetColor = targetColor;
        this.replacementColor = replacementColor;
    }

    public void fill(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int target = targetColor;
        int replacement = replacementColor;
        Point node = this.node;

        if (target != replacement) {
            Deque<Point> queue = new LinkedList<Point>();
            do {
                int x = node.x;
                int y = node.y;

                while (x > 0 && image.getPixel(x - 1, y) == target) {
                    x--;
                }

                boolean spanUp = false;
                boolean spanDown = false;

                while (x < width && image.getPixel(x, y) == target) {
                    image.setPixel(x, y, replacement);
                    if (!spanUp && y > 0 && image.getPixel(x, y - 1) == target) {
                        queue.add(new Point(x, y - 1));
                        spanUp = true;
                    } else if (spanUp && image.getPixel(x, y - 1) != target) {
                        spanUp = false;
                    }
                    if (!spanDown && y < height - 1 && image.getPixel(x, y + 1) == target) {
                        queue.add(new Point(x, y + 1));
                        spanDown = true;
                    } else if (spanDown && y < height - 1 && image.getPixel(x, y + 1) != target) {
                        spanDown = false;
                    }
                    x++;
                }
            } while ((node = queue.pollFirst()) != null);
        }
    }
}

