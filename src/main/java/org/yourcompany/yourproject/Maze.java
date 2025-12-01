/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package org.yourcompany.yourproject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author darlis
 */
public class Maze extends JPanel {

    private int N = 20; // maze size
    private boolean[][] hWalls, vWalls; // horizontal and vertical walls
    private Point start = new Point(0, 0);
    private Point end = new Point(N - 1, N - 1);
    private java.util.List<Point> path;
    private Set<Point> visited;
    private javax.swing.Timer timer;
    private int step = 0;

    public Maze() {
        generateMaze();
    }

    private void generateMaze() {
        // Prim's algorithm for maze generation
        hWalls = new boolean[N][N - 1];
        vWalls = new boolean[N - 1][N];
        // Initialize all walls as present
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N - 1; j++) {
                hWalls[i][j] = true;
            }
        }
        for (int i = 0; i < N - 1; i++) {
            for (int j = 0; j < N; j++) {
                vWalls[i][j] = true;
            }
        }
        // Visited cells
        boolean[][] vis = new boolean[N][N];
        java.util.List<Wall> walls = new ArrayList<>();
        // Start from (0,0)
        vis[0][0] = true;
        addWalls(0, 0, walls);
        Random rand = new Random();
        while (!walls.isEmpty()) {
            Wall w = walls.remove(rand.nextInt(walls.size()));
            int x1 = w.x1, y1 = w.y1, x2 = w.x2, y2 = w.y2;
            if (vis[x1][y1] && vis[x2][y2]) continue;
            if (vis[x1][y1]) {
                vis[x2][y2] = true;
                addWalls(x2, y2, walls);
            } else {
                vis[x1][y1] = true;
                addWalls(x1, y1, walls);
            }
            // Remove the wall
            if (x1 == x2) { // horizontal wall
                hWalls[Math.min(x1, x2)][y1] = false;
            } else { // vertical wall
                vWalls[x1][Math.min(y1, y2)] = false;
            }
        }
    }

    private void addWalls(int x, int y, java.util.List<Wall> walls) {
        if (x > 0) walls.add(new Wall(x - 1, y, x, y));
        if (x < N - 1) walls.add(new Wall(x, y, x + 1, y));
        if (y > 0) walls.add(new Wall(x, y - 1, x, y));
        if (y < N - 1) walls.add(new Wall(x, y, x, y + 1));
    }

    private static class Wall {
        int x1, y1, x2, y2;
        Wall(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }

    private void solveBFS() {
        Queue<Point> q = new LinkedList<>();
        Map<Point, Point> parent = new HashMap<>();
        boolean[][] vis = new boolean[N][N];
        q.add(start);
        vis[start.x][start.y] = true;
        boolean found = false;
        while (!q.isEmpty()) {
            Point p = q.poll();
            if (p.equals(end)) {
                found = true;
                break;
            }
            for (Point nei : neighbors(p)) {
                if (!vis[nei.x][nei.y]) {
                    vis[nei.x][nei.y] = true;
                    q.add(nei);
                    parent.put(nei, p);
                }
            }
        }
        if (found) {
            path = new ArrayList<>();
            Point cur = end;
            while (cur != null) {
                path.add(0, cur);
                cur = parent.get(cur);
            }
        }
        visited = new HashSet<>();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (vis[i][j]) visited.add(new Point(i, j));
            }
        }
        animate();
    }

    private void solveDFS() {
        Stack<Point> s = new Stack<>();
        Map<Point, Point> parent = new HashMap<>();
        boolean[][] vis = new boolean[N][N];
        s.push(start);
        vis[start.x][start.y] = true;
        boolean found = false;
        while (!s.isEmpty()) {
            Point p = s.pop();
            if (p.equals(end)) {
                found = true;
                break;
            }
            for (Point nei : neighbors(p)) {
                if (!vis[nei.x][nei.y]) {
                    vis[nei.x][nei.y] = true;
                    s.push(nei);
                    parent.put(nei, p);
                }
            }
        }
        if (found) {
            path = new ArrayList<>();
            Point cur = end;
            while (cur != null) {
                path.add(0, cur);
                cur = parent.get(cur);
            }
        }
        visited = new HashSet<>();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (vis[i][j]) visited.add(new Point(i, j));
            }
        }
        animate();
    }

    private java.util.List<Point> neighbors(Point p) {
        java.util.List<Point> res = new ArrayList<>();
        int x = p.x, y = p.y;
        if (x > 0 && !vWalls[x - 1][y]) res.add(new Point(x - 1, y));
        if (x < N - 1 && !vWalls[x][y]) res.add(new Point(x + 1, y));
        if (y > 0 && !hWalls[x][y - 1]) res.add(new Point(x, y - 1));
        if (y < N - 1 && !hWalls[x][y]) res.add(new Point(x, y + 1));
        return res;
    }

    private void animate() {
        step = 0;
        if (timer != null) timer.stop();
        timer = new javax.swing.Timer(100, e -> {
            step++;
            if (step >= visited.size()) {
                timer.stop();
                repaint();
            } else {
                repaint();
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int cellWidth = getWidth() / N;
        int cellHeight = getHeight() / N;
        g.setColor(Color.BLACK);
        // Draw walls
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N - 1; j++) {
                if (hWalls[i][j]) {
                    g.drawLine(j * cellWidth, i * cellHeight, (j + 1) * cellWidth, i * cellHeight);
                }
            }
        }
        for (int i = 0; i < N - 1; i++) {
            for (int j = 0; j < N; j++) {
                if (vWalls[i][j]) {
                    g.drawLine(j * cellWidth, i * cellHeight, j * cellWidth, (i + 1) * cellHeight);
                }
            }
        }
        // Draw cells
        g.setColor(Color.WHITE);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                g.fillRect(j * cellWidth + 1, i * cellHeight + 1, cellWidth - 2, cellHeight - 2);
            }
        }
        // Draw visited cells (yellow)
        g.setColor(Color.YELLOW);
        int count = 0;
        if (visited != null) {
            for (Point p : visited) {
                if (count++ < step) {
                    g.fillRect(p.y * cellWidth + 1, p.x * cellHeight + 1, cellWidth - 2, cellHeight - 2);
                }
            }
        }
        // Draw path (red)
        if (path != null) {
            g.setColor(Color.RED);
            for (Point p : path) {
                g.fillRect(p.y * cellWidth + 1, p.x * cellHeight + 1, cellWidth - 2, cellHeight - 2);
            }
        }
        // Draw start (green) and end (blue)
        g.setColor(Color.GREEN);
        g.fillRect(start.y * cellWidth + 1, start.x * cellHeight + 1, cellWidth - 2, cellHeight - 2);
        g.setColor(Color.BLUE);
        g.fillRect(end.y * cellWidth + 1, end.x * cellHeight + 1, cellWidth - 2, cellHeight - 2);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Maze");
        Maze maze = new Maze();
        frame.add(maze, BorderLayout.CENTER);
        JPanel panel = new JPanel();
        JButton gen = new JButton("Generate");
        gen.addActionListener(e -> {
            maze.generateMaze();
            maze.path = null;
            maze.visited = null;
            maze.repaint();
        });
        JButton bfs = new JButton("Solve BFS");
        bfs.addActionListener(e -> maze.solveBFS());
        JButton dfs = new JButton("Solve DFS");
        dfs.addActionListener(e -> maze.solveDFS());
        panel.add(gen);
        panel.add(bfs);
        panel.add(dfs);
        frame.add(panel, BorderLayout.SOUTH);
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
