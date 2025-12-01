package org.yourcompany.yourproject.ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

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
    private int animationDelay = 70; // milliseconds
    private javax.swing.JLabel costLabel; // optional label to show total path cost
    private int gridSize;
    private Result[] allResults = new Result[4]; // 0: BFS, 1: DFS, 2: Dijkstra, 3: A*
    private int[] allCosts = new int[4];
    private String[] algorithmNames = {"BFS", "DFS", "Dijkstra", "A*"};
    private Color[] pathColors = {Color.BLUE, Color.GREEN, new Color(128,0,128), Color.ORANGE};
    private boolean showAllPaths = false;

    public MazePanel(int size) {
        this.gridSize = size;
        this.model = new MazeModel(size);
        PrimMazeGenerator.generate(model);
    }

    public void generate() {
        model = new MazeModel(gridSize);
        PrimMazeGenerator.generate(model);
        result = null;
        allResults = new Result[4];
        allCosts = new int[4];
        java.util.Arrays.fill(allCosts, -1);
        step = 0;
        pathStep = 0;
        showAllPaths = false;
        if (timer != null) timer.stop();
        if (costLabel != null) costLabel.setText("Cost: -");
        repaint();
    }

    public void setGridSize(int newSize) {
        if (newSize <= 1) return;
        this.gridSize = newSize;
    }

    public int getGridSize() {
        return this.gridSize;
    }

    public void solveBFS() {
        computeAllResults();
        result = allResults[0];
        startAnimation();
    }

    public void solveDFS() {
        computeAllResults();
        result = allResults[1];
        startAnimation();
    }

    public void solveDijkstra() {
        computeAllResults();
        result = allResults[2];
        startAnimation();
    }

    public void solveAStar() {
        computeAllResults();
        result = allResults[3];
        startAnimation();
    }

    private void computeAllResults() {
        allResults[0] = MazeSolver.solveBFS(model);
        allResults[1] = MazeSolver.solveDFS(model);
        allResults[2] = MazeSolver.dijkstra(model);
        allResults[3] = MazeSolver.aStar(model);
        for (int i = 0; i < 4; i++) {
            if (allResults[i] != null && allResults[i].path != null) {
                int total = 0;
                for (Point p : allResults[i].path) total += model.getCost(p);
                allCosts[i] = total;
            } else {
                allCosts[i] = -1;
            }
        }
    }

    private void startAnimation() {
        step = 0;
        pathStep = 0;
        showAllPaths = false;
        if (timer != null) timer.stop();
        timer = new javax.swing.Timer(animationDelay, e -> {
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
            if (!showAllPaths) {
                showAllPaths = true;
                repaint();
                ((javax.swing.Timer)e.getSource()).stop();
                // update cost label
                if (costLabel != null) {
                    StringBuilder sb = new StringBuilder("Costs: ");
                    for (int i = 0; i < 4; i++) {
                        if (i > 0) sb.append(", ");
                        sb.append(algorithmNames[i]).append(": ").append(allCosts[i] == -1 ? "-" : allCosts[i]);
                    }
                    costLabel.setText(sb.toString());
                }
                return;
            }
            ((javax.swing.Timer)e.getSource()).stop();
        });
        timer.start();
    }

    public void setCostLabel(javax.swing.JLabel label) {
        this.costLabel = label;
        if (label != null) label.setText("Cost: -");
    }

    /**
     * Set animation delay in milliseconds. If animation is running, updates timer delay.
     */
    public void setAnimationDelay(int ms) {
        if (ms <= 0) return;
        this.animationDelay = ms;
        if (timer != null) timer.setDelay(ms);
    }

    public int getAnimationDelay() {
        return animationDelay;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int N = model.getSize();
        int cellW = getWidth() / N;
        int cellH = getHeight() / N;
        // draw terrain background per cell
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                Terrain t = model.getTerrain(new Point(i, j));
                if (t == Terrain.GRASS) g2.setColor(new Color(220, 255, 200));
                else if (t == Terrain.MUD) g2.setColor(new Color(210, 180, 140));
                else g2.setColor(new Color(180, 210, 255));
                g2.fillRect(j*cellW+1, i*cellH+1, cellW-2, cellH-2);
            }
        }
        // draw walls (on top of terrain)
        g2.setColor(Color.BLACK);
        // vertical walls
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N - 1; col++) {
                if (model.vWalls[row][col]) {
                    int x = (col + 1) * cellW;
                    int y1 = row * cellH;
                    int y2 = (row + 1) * cellH;
                    g2.drawLine(x, y1, x, y2);
                }
            }
        }
        // horizontal walls
        for (int row = 0; row < N - 1; row++) {
            for (int col = 0; col < N; col++) {
                if (model.hWalls[row][col]) {
                    int y = (row + 1) * cellH;
                    int x1 = col * cellW;
                    int x2 = (col + 1) * cellW;
                    g2.drawLine(x1, y, x2, y);
                }
            }
        }
        // draw visited in order as translucent overlay so terrain remains visible
        if (result != null && result.visitOrder != null) {
            int limit = Math.min(step, result.visitOrder.size());
            Color visitCol = new Color(255, 230, 0, 120);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
            g2.setColor(visitCol);
            for (int i = 0; i < limit; i++) {
                Point p = result.visitOrder.get(i);
                g2.fillRect(p.y*cellW+1, p.x*cellH+1, cellW-2, cellH-2);
            }
            g2.setComposite(AlphaComposite.SrcOver);
        }
        // draw path progressively as red line
        if (result != null && result.path != null && pathStep > 0) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2));
            int limit = Math.min(pathStep, result.path.size());
            for (int i = 0; i < limit - 1; i++) {
                Point p1 = result.path.get(i), p2 = result.path.get(i + 1);
                int x1 = p1.y * cellW + cellW / 2;
                int y1 = p1.x * cellH + cellH / 2;
                int x2 = p2.y * cellW + cellW / 2;
                int y2 = p2.x * cellH + cellH / 2;
                g2.drawLine(x1, y1, x2, y2);
            }
            g2.setStroke(new BasicStroke(1)); // reset
        }
        // draw all paths as lines if showAllPaths
        if (showAllPaths) {
            g2.setStroke(new BasicStroke(2));
            for (int i = 0; i < 4; i++) {
                if (allResults[i] != null && allResults[i].path != null) {
                    g2.setColor(pathColors[i]);
                    java.util.List<Point> path = allResults[i].path;
                    for (int j = 0; j < path.size() - 1; j++) {
                        Point p1 = path.get(j), p2 = path.get(j + 1);
                        int x1 = p1.y * cellW + cellW / 2;
                        int y1 = p1.x * cellH + cellH / 2;
                        int x2 = p2.y * cellW + cellW / 2;
                        int y2 = p2.x * cellH + cellH / 2;
                        g2.drawLine(x1, y1, x2, y2);
                    }
                }
            }
            g2.setStroke(new BasicStroke(1)); // reset
        }
        // draw start and end as solid small boxes on top
        int insetW = Math.max(2, cellW/6);
        int insetH = Math.max(2, cellH/6);
        g2.setColor(Color.GREEN);
        g2.fillRect(model.start.y*cellW+insetW, model.start.x*cellH+insetH, cellW-2*insetW, cellH-2*insetH);
        g2.setColor(Color.BLUE);
        g2.fillRect(model.end.y*cellW+insetW, model.end.x*cellH+insetH, cellW-2*insetW, cellH-2*insetH);

        // legend (top-left)
        int lx = 8, ly = 8, sw = 14, sh = 12, gap = 6;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();
        // terrain legend
        g2.setColor(new Color(220, 255, 200)); g2.fillRect(lx, ly, sw, sh); g2.setColor(Color.BLACK); g2.drawRect(lx, ly, sw, sh); g2.drawString("Grass (1)", lx+sw+gap, ly+sh-3);
        g2.setColor(new Color(210, 180, 140)); g2.fillRect(lx, ly+sh+gap, sw, sh); g2.setColor(Color.BLACK); g2.drawRect(lx, ly+sh+gap, sw, sh); g2.drawString("Mud (5)", lx+sw+gap, ly+sh+gap+sh-3);
        g2.setColor(new Color(180, 210, 255)); g2.fillRect(lx, ly+2*(sh+gap), sw, sh); g2.setColor(Color.BLACK); g2.drawRect(lx, ly+2*(sh+gap), sw, sh); g2.drawString("Water (10)", lx+sw+gap, ly+2*(sh+gap)+sh-3);
        // overlays legend
        int oy = ly+3*(sh+gap)+4;
        g2.setColor(new Color(255,230,0,120)); g2.fillRect(lx, oy, sw, sh); g2.setColor(Color.BLACK); g2.drawRect(lx, oy, sw, sh); g2.drawString("Visited", lx+sw+gap, oy+sh-3);
        g2.setColor(new Color(220,20,60,200)); g2.fillRect(lx, oy+sh+gap, sw, sh); g2.setColor(Color.BLACK); g2.drawRect(lx, oy+sh+gap, sw, sh); g2.drawString("Path", lx+sw+gap, oy+sh+gap+sh-3);
    }
}
