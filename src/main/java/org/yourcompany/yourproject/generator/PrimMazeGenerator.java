package org.yourcompany.yourproject.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.yourcompany.yourproject.model.MazeModel;

public class PrimMazeGenerator {
    private static class Wall {
        int x1, y1, x2, y2;
        Wall(int x1, int y1, int x2, int y2) { this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2; }
    }

    public static void generate(MazeModel model) {
        int N = model.getSize();
        model.initWalls();
        boolean[][] vis = new boolean[N][N];
        List<Wall> walls = new ArrayList<>();
        vis[0][0] = true;
        addWalls(0, 0, N, walls);
        Random rand = new Random();
        while (!walls.isEmpty()) {
            Wall w = walls.remove(rand.nextInt(walls.size()));
            int x1 = w.x1, y1 = w.y1, x2 = w.x2, y2 = w.y2;
            if (vis[x1][y1] && vis[x2][y2]) continue;
            if (vis[x1][y1]) {
                vis[x2][y2] = true;
                addWalls(x2, y2, N, walls);
            } else {
                vis[x1][y1] = true;
                addWalls(x1, y1, N, walls);
            }
            // remove wall between adjacent cells
            if (x1 == x2) { // same row -> cells are left/right -> remove vertical wall at vWalls[row][minCol]
                int row = x1;
                int col = Math.min(y1, y2);
                model.vWalls[row][col] = false;
            } else { // same column -> cells are up/down -> remove horizontal wall at hWalls[minRow][col]
                int row = Math.min(x1, x2);
                int col = y1;
                model.hWalls[row][col] = false;
            }
        }
    }

    private static void addWalls(int x, int y, int N, List<Wall> walls) {
        if (x > 0) walls.add(new Wall(x - 1, y, x, y));
        if (x < N - 1) walls.add(new Wall(x, y, x + 1, y));
        if (y > 0) walls.add(new Wall(x, y - 1, x, y));
        if (y < N - 1) walls.add(new Wall(x, y, x, y + 1));
    }
}
