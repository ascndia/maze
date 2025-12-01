package org.yourcompany.yourproject.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class MazeModel {
    private final int N;
    // vWalls[row][col] is a vertical wall between (row,col) and (row,col+1) -> size N x (N-1)
    public boolean[][] vWalls;
    // hWalls[row][col] is a horizontal wall between (row,col) and (row+1,col) -> size (N-1) x N
    public boolean[][] hWalls;
    // terrain per cell
    private Terrain[][] terrain;
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
        // init terrain to GRASS by default
        terrain = new Terrain[N][N];
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++) terrain[i][j] = Terrain.GRASS;
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

    public Terrain getTerrain(Point p) { return terrain[p.x][p.y]; }
    public int getCost(Point p) { return terrain[p.x][p.y].cost; }
    public void setTerrain(int x, int y, Terrain t) { terrain[x][y] = t; }
    public void randomizeTerrain(double pMud, double pWater) {
        Random rnd = new Random();
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++) {
            double r = rnd.nextDouble();
            if (r < pWater) terrain[i][j] = Terrain.WATER;
            else if (r < pWater + pMud) terrain[i][j] = Terrain.MUD;
            else terrain[i][j] = Terrain.GRASS;
        }
    }

    /**
     * Remove up to k random internal walls to create loops/shortcuts (make maze imperfect).
     */
    public void removeRandomWalls(int k) {
        java.util.List<int[]> candidates = new java.util.ArrayList<>();
        // encode as {type, row, col} where type: 0 = horizontal, 1 = vertical
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N - 1; j++) {
                if (vWalls[i][j]) candidates.add(new int[] {1, i, j});
            }
        }
        for (int i = 0; i < N - 1; i++) {
            for (int j = 0; j < N; j++) {
                if (hWalls[i][j]) candidates.add(new int[] {0, i, j});
            }
        }
        java.util.Random rnd = new java.util.Random();
        for (int removed = 0; removed < k && !candidates.isEmpty(); removed++) {
            int idx = rnd.nextInt(candidates.size());
            int[] c = candidates.remove(idx);
            if (c[0] == 1) {
                vWalls[c[1]][c[2]] = false;
            } else {
                hWalls[c[1]][c[2]] = false;
            }
        }
    }
}
