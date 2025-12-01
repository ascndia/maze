package org.yourcompany.yourproject.ui;

import java.awt.AlphaComposite;
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
        if (costLabel != null) costLabel.setText("Cost: -");
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
            ((javax.swing.Timer)e.getSource()).stop();
            // update cost label when animation finished
            if (costLabel != null) {
                if (result != null && result.path != null) {
                    int total = 0;
                    for (java.awt.Point p : result.path) total += model.getCost(p);
                    costLabel.setText("Cost: " + total);
                } else {
                    costLabel.setText("Cost: -");
                }
            }
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
        // draw path progressively as a more opaque overlay but inset so terrain still shows
        if (result != null && result.path != null && pathStep > 0) {
            int limit = Math.min(pathStep, result.path.size());
            Color pathCol = new Color(220, 20, 60, 200);
            g2.setColor(pathCol);
            for (int i = 0; i < limit; i++) {
                Point p = result.path.get(i);
                int insetW = Math.max(2, cellW/6);
                int insetH = Math.max(2, cellH/6);
                g2.fillRect(p.y*cellW+insetW, p.x*cellH+insetH, cellW-2*insetW, cellH-2*insetH);
            }
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
