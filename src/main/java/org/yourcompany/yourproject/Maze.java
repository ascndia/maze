/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package org.yourcompany.yourproject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import javax.imageio.ImageIO;
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
    private java.util.List<Point> visitOrder; // ordered exploration list
    private int pathStep = 0; // how many path cells to reveal during animation
    private BufferedImage ratImage;

    public Maze() {
        try {
            ratImage = ImageIO.read(new File("src/assets/image.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        generateMaze();
    }

    private void generateMaze() {
        // Prim's algorithm for maze generation
        // Horizontal walls between rows: (N-1) x N
        hWalls = new boolean[N - 1][N];
        // Vertical walls between columns: N x (N-1)
        vWalls = new boolean[N][N - 1];
        // Initialize all walls as present
        for (int i = 0; i < N - 1; i++) {
            for (int j = 0; j < N; j++) {
                hWalls[i][j] = true;
            }
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N - 1; j++) {
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
            // Remove the wall: if rows differ -> horizontal wall between rows;
            // if columns differ -> vertical wall between columns
            if (x1 == x2) { // same row -> vertical wall between columns y1 and y2
                vWalls[x1][Math.min(y1, y2)] = false;
            } else { // different rows -> horizontal wall between rows x1 and x2
                hWalls[Math.min(x1, x2)][y1] = false;
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
        // record exploration order
        visitOrder = new ArrayList<>();
        q.add(start);
        vis[start.x][start.y] = true;
        visitOrder.add(start);
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
                    visitOrder.add(nei);
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
        pathStep = 0;
        animate();
    }

    private void solveDFS() {
        Stack<Point> s = new Stack<>();
        Map<Point, Point> parent = new HashMap<>();
        boolean[][] vis = new boolean[N][N];
        // record exploration order
        visitOrder = new ArrayList<>();
        s.push(start);
        vis[start.x][start.y] = true;
        visitOrder.add(start);
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
                    visitOrder.add(nei);
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
        pathStep = 0;
        animate();
    }

    private java.util.List<Point> neighbors(Point p) {
        java.util.List<Point> res = new ArrayList<>();
        int x = p.x, y = p.y;
        // Up (row-1): check horizontal wall between row-1 and row at column y
        if (x > 0 && !hWalls[x - 1][y]) res.add(new Point(x - 1, y));
        // Down (row+1): check horizontal wall between row and row+1 at column y
        if (x < N - 1 && !hWalls[x][y]) res.add(new Point(x + 1, y));
        // Left (col-1): check vertical wall between col-1 and col at row x
        if (y > 0 && !vWalls[x][y - 1]) res.add(new Point(x, y - 1));
        // Right (col+1): check vertical wall between col and col+1 at row x
        if (y < N - 1 && !vWalls[x][y]) res.add(new Point(x, y + 1));
        return res;
    }

    private void animate() {
        step = 0;
        if (timer != null) timer.stop();
        timer = new javax.swing.Timer(80, e -> {
            // First reveal exploration in order, then reveal path gradually
            if (visitOrder != null && step < visitOrder.size()) {
                step++;
                repaint();
                return;
            }
            // exploration done; reveal path one cell at a time
            if (path != null && pathStep < path.size()) {
                pathStep++;
                repaint();
                return;
            }
            timer.stop();
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
        // Horizontal walls between rows: hWalls[rowBetween][col]
        for (int i = 0; i < N - 1; i++) {
            int y = (i + 1) * cellHeight;
            for (int j = 0; j < N; j++) {
                if (hWalls[i][j]) {
                    g.drawLine(j * cellWidth, y, (j + 1) * cellWidth, y);
                }
            }
        }
        // Vertical walls between columns: vWalls[row][colBetween]
        for (int i = 0; i < N; i++) {
            int row = i;
            for (int j = 0; j < N - 1; j++) {
                int x = (j + 1) * cellWidth;
                if (vWalls[row][j]) {
                    g.drawLine(x, row * cellHeight, x, (row + 1) * cellHeight);
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
        // Draw visited cells (yellow) in exploration order
        g.setColor(Color.YELLOW);
        if (visitOrder != null) {
            int limit = Math.min(step, visitOrder.size());
            for (int i = 0; i < limit; i++) {
                Point p = visitOrder.get(i);
                g.fillRect(p.y * cellWidth + 1, p.x * cellHeight + 1, cellWidth - 2, cellHeight - 2);
            }
            // Draw rat during exploration
            if (ratImage != null && step > 0 && step <= visitOrder.size() && (path == null || pathStep == 0)) {
                Point p = visitOrder.get(step - 1);
                g.drawImage(ratImage, p.y * cellWidth + 1, p.x * cellHeight + 1, cellWidth - 2, cellHeight - 2, null);
            }
        }
        // Draw path (red) progressively
        if (path != null && pathStep > 0) {
            g.setColor(Color.RED);
            int limit = Math.min(pathStep, path.size());
            for (int i = 0; i < limit; i++) {
                Point p = path.get(i);
                g.fillRect(p.y * cellWidth + 1, p.x * cellHeight + 1, cellWidth - 2, cellHeight - 2);
            }
            // Draw rat during path traversal
            if (ratImage != null && pathStep > 0 && pathStep <= path.size()) {
                Point p = path.get(pathStep - 1);
                g.drawImage(ratImage, p.y * cellWidth + 1, p.x * cellHeight + 1, cellWidth - 2, cellHeight - 2, null);
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
            maze.visitOrder = null;
            maze.step = 0;
            maze.pathStep = 0;
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
