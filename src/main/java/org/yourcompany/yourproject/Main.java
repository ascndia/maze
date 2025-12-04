package org.yourcompany.yourproject;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.yourcompany.yourproject.ui.MazePanel;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Maze");
            MazePanel panel = new MazePanel(16);
            frame.add(panel, BorderLayout.CENTER);
            JPanel buttons = new JPanel();
            // grid size control (applies when Generate pressed)
            JSpinner gridSizeSpinner = new JSpinner(new SpinnerNumberModel(panel.getGridSize(), 5, 80, 1));
            buttons.add(new JLabel("Grid:"));
            buttons.add(gridSizeSpinner);
            // extra openings spinner (0 = perfect maze)
            JSpinner extraOpenings = new JSpinner(new SpinnerNumberModel(0, 0, 200, 1));
            buttons.add(new JLabel("Extra openings:"));
            buttons.add(extraOpenings);

            JButton gen = new JButton("Generate");
            JButton runRace = new JButton("Run Race");
            JButton replay = new JButton("Replay");
            JButton bfs = new JButton("Solve BFS");
            JButton dfs = new JButton("Solve DFS");
            JButton dijk = new JButton("Dijkstra");
            JButton astar = new JButton("A*");

            // Configure initial states
            replay.setEnabled(false);
            bfs.setEnabled(false);
            dfs.setEnabled(false);
            dijk.setEnabled(false);
            astar.setEnabled(false);

            // Add listeners
            gen.addActionListener(e -> {
                try {
                    int newSize = (Integer) gridSizeSpinner.getValue();
                    panel.setGridSize(newSize);
                } catch (Exception ex) {}
                try {
                    int k = (Integer) extraOpenings.getValue();
                    panel.generate(k);
                } catch (Exception ex) {
                    panel.generate();
                }
                bfs.setEnabled(false);
                dfs.setEnabled(false);
                dijk.setEnabled(false);
                astar.setEnabled(false);
                replay.setEnabled(false);
            });

            runRace.addActionListener(e -> {
                panel.startRace();
                bfs.setEnabled(true);
                dfs.setEnabled(true);
                dijk.setEnabled(true);
                astar.setEnabled(true);
                replay.setEnabled(true);
            });

            replay.addActionListener(e -> panel.replayRace());
            bfs.addActionListener(e -> panel.solveBFS());
            dfs.addActionListener(e -> panel.solveDFS());
            dijk.addActionListener(e -> panel.solveDijkstra());
            astar.addActionListener(e -> panel.solveAStar());

            // Add buttons in logical order
            buttons.add(gen); 
            buttons.add(runRace);
            buttons.add(replay);
            buttons.add(bfs); 
            buttons.add(dfs); 
            buttons.add(dijk); 
            buttons.add(astar);

            // Slider for animation speed (ms delay) with Fast / Slow labels
            JLabel speedLabel = new JLabel("70 ms");
            JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 10, 500, panel.getAnimationDelay());
            speedSlider.setPaintTicks(false);
            speedSlider.setPaintLabels(false);
            speedSlider.setPreferredSize(new java.awt.Dimension(180, 24));
            speedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
                @Override
                public void stateChanged(javax.swing.event.ChangeEvent e) {
                    int v = speedSlider.getValue();
                    panel.setAnimationDelay(v);
                    speedLabel.setText(v + " ms");
                }
            });
            JPanel speedPanel = new JPanel(new java.awt.BorderLayout(6, 0));
            speedPanel.add(new JLabel("Fast"), java.awt.BorderLayout.WEST);
            speedPanel.add(speedSlider, java.awt.BorderLayout.CENTER);
            speedPanel.add(new JLabel("Slow"), java.awt.BorderLayout.EAST);
            buttons.add(speedPanel);
            buttons.add(speedLabel);

            // Cost label (updated when path animation finishes)
            JLabel costLabel = new JLabel("Cost: -");
            panel.setCostLabel(costLabel);
            buttons.add(costLabel);
            frame.add(buttons, BorderLayout.SOUTH);
            frame.setSize(560,560);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
