package com.example.painter;

import android.graphics.Path;

public class PointerPath {
    public boolean fill;
    public boolean blur;
    public int strokeWidth;
    public Path path;
    public int color;

    public PointerPath(int color, boolean fill, boolean blur, int strokeWidth, Path path) {
        this.color = color;
        this.fill = fill;
        this.blur = blur;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }
}