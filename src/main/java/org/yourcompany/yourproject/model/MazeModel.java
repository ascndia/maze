package org.yourcompany.yourproject.model;

import java.awt.Point;
import java.util.ArrayList;

public class MazeModel {
    private final int N;
    // vWalls[row][col] is a vertical wall between (row,col) and (row,col+1) -> size N x (N-1)
    public boolean[][] vWalls;
    // hWalls[row][col] is a horizontal wall between (row,col) and (row+1,col) -> size (N-1) x N
    public boolean[][] hWalls;
    public Point start;
    public Point end;

    public MazeModel(int N) {
        this.N = N;
        this.start = new Point(0, 0);
        this.end = new Point(N - 1, N - 1);
        initWalls();
    }

    public int getSize() {
        return N;
    }

    public void initWalls() {
        vWalls = new boolean[N][N - 1];
        hWalls = new boolean[N - 1][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N - 1; j++) vWalls[i][j] = true;
        }
        for (int i = 0; i < N - 1; i++) {
            for (int j = 0; j < N; j++) hWalls[i][j] = true;
        }
    }

    public java.util.List<Point> neighbors(Point p) {
        java.util.List<Point> res = new ArrayList<>();
        int x = p.x, y = p.y;
        // left: check vertical wall to the left at vWalls[x][y-1]
        if (y > 0 && !vWalls[x][y - 1]) res.add(new Point(x, y - 1));
        // right: check vertical wall at vWalls[x][y]
        if (y < N - 1 && !vWalls[x][y]) res.add(new Point(x, y + 1));
        // up: check horizontal wall above at hWalls[x-1][y]
        if (x > 0 && !hWalls[x - 1][y]) res.add(new Point(x - 1, y));
        // down: check horizontal wall at hWalls[x][y]
        if (x < N - 1 && !hWalls[x][y]) res.add(new Point(x + 1, y));
        return res;
    }
}
