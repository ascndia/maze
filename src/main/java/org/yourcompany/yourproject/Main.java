package org.yourcompany.yourproject;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

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
            buttons.add(gen); buttons.add(bfs); buttons.add(dfs);
            frame.add(buttons, BorderLayout.SOUTH);
            frame.setSize(560,560);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
