package org.yourcompany.yourproject;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JLabel;
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
            JButton gen = new JButton("Generate");
            gen.addActionListener(e -> panel.generate());
            JButton bfs = new JButton("Solve BFS");
            bfs.addActionListener(e -> panel.solveBFS());
            JButton dfs = new JButton("Solve DFS");
            dfs.addActionListener(e -> panel.solveDFS());
            JButton dijk = new JButton("Dijkstra");
            dijk.addActionListener(e -> panel.solveDijkstra());
            JButton astar = new JButton("A*");
            astar.addActionListener(e -> panel.solveAStar());
            buttons.add(gen); buttons.add(bfs); buttons.add(dfs); buttons.add(dijk); buttons.add(astar);

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
            frame.add(buttons, BorderLayout.SOUTH);
            frame.setSize(560,560);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
