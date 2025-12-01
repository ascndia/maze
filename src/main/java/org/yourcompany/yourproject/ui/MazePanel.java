package org.yourcompany.yourproject.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;

import org.yourcompany.yourproject.generator.PrimMazeGenerator;
import org.yourcompany.yourproject.model.MazeModel;
import org.yourcompany.yourproject.model.Terrain;
import org.yourcompany.yourproject.solver.MazeSolver;
import org.yourcompany.yourproject.solver.MazeSolver.Result;

public class MazePanel extends JPanel {
    private MazeModel model;
    private Result result;
    private javax.swing.Timer timer;
    private int step = 0;
    private int pathStep = 0;

    public MazePanel(int size) {
        this.model = new MazeModel(size);
        PrimMazeGenerator.generate(model);
    }

    public void generate() {
        model = new MazeModel(model.getSize());
        PrimMazeGenerator.generate(model);
        result = null;
        step = 0;
        pathStep = 0;
        if (timer != null) timer.stop();
        repaint();
    }

    public void solveBFS() {
        result = MazeSolver.solveBFS(model);
        startAnimation();
    }

    public void solveDFS() {
        result = MazeSolver.solveDFS(model);
        startAnimation();
    }

    public void solveDijkstra() {
        result = MazeSolver.dijkstra(model);
        startAnimation();
    }

    public void solveAStar() {
        result = MazeSolver.aStar(model);
        startAnimation();
    }

    private void startAnimation() {
        step = 0;
        pathStep = 0;
        if (timer != null) timer.stop();
        timer = new javax.swing.Timer(70, e -> {
            if (result != null && step < result.visitOrder.size()) {
                step++;
                repaint();
                return;
            }
            if (result != null && result.path != null && pathStep < result.path.size()) {
                pathStep++;
                repaint();
                return;
            }
            ((javax.swing.Timer)e.getSource()).stop();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int N = model.getSize();
        int cellW = getWidth() / N;
        int cellH = getHeight() / N;
        // draw terrain background per cell
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                Terrain t = model.getTerrain(new Point(i, j));
                if (t == Terrain.GRASS) g.setColor(new Color(220, 255, 200));
                else if (t == Terrain.MUD) g.setColor(new Color(210, 180, 140));
                else g.setColor(new Color(180, 210, 255));
                g.fillRect(j*cellW+1, i*cellH+1, cellW-2, cellH-2);
            }
        }
        g.setColor(Color.BLACK);
        // draw vertical walls (between left/right cells): vWalls[row][col] -> line at x = (col+1)*cellW
        g.setColor(Color.BLACK);
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N - 1; col++) {
                if (model.vWalls[row][col]) {
                    int x = (col + 1) * cellW;
                    int y1 = row * cellH;
                    int y2 = (row + 1) * cellH;
                    g.drawLine(x, y1, x, y2);
                }
            }
        }
        // draw horizontal walls (between up/down cells): hWalls[row][col] -> line at y = (row+1)*cellH
        for (int row = 0; row < N - 1; row++) {
            for (int col = 0; col < N; col++) {
                if (model.hWalls[row][col]) {
                    int y = (row + 1) * cellH;
                    int x1 = col * cellW;
                    int x2 = (col + 1) * cellW;
                    g.drawLine(x1, y, x2, y);
                }
            }
        }
        // draw visited in order
        if (result != null && result.visitOrder != null) {
            g.setColor(Color.YELLOW);
            int limit = Math.min(step, result.visitOrder.size());
            for (int i = 0; i < limit; i++) {
                Point p = result.visitOrder.get(i);
                g.fillRect(p.y*cellW+1, p.x*cellH+1, cellW-2, cellH-2);
            }
        }
        // draw path progressively
        if (result != null && result.path != null && pathStep > 0) {
            g.setColor(Color.RED);
            int limit = Math.min(pathStep, result.path.size());
            for (int i = 0; i < limit; i++) {
                Point p = result.path.get(i);
                g.fillRect(p.y*cellW+1, p.x*cellH+1, cellW-2, cellH-2);
            }
        }
        // start and end
        g.setColor(Color.GREEN);
        g.fillRect(model.start.y*cellW+1, model.start.x*cellH+1, cellW-2, cellH-2);
        g.setColor(Color.BLUE);
        g.fillRect(model.end.y*cellW+1, model.end.x*cellH+1, cellW-2, cellH-2);
    }
}
