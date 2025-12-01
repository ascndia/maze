package org.yourcompany.yourproject.solver;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.yourcompany.yourproject.model.MazeModel;

public class MazeSolver {
    public static class Result {
        public java.util.List<Point> path;
        public java.util.List<Point> visitOrder;
        public Set<Point> visited;
    }

    public static Result solveBFS(MazeModel model) {
        int N = model.getSize();
        Queue<Point> q = new LinkedList<>();
        Map<Point, Point> parent = new HashMap<>();
        boolean[][] vis = new boolean[N][N];
        java.util.List<Point> visitOrder = new ArrayList<>();
        q.add(model.start);
        vis[model.start.x][model.start.y] = true;
        visitOrder.add(model.start);
        boolean found = false;
        while (!q.isEmpty()) {
            Point p = q.poll();
            if (p.equals(model.end)) { found = true; break; }
            for (Point nei : model.neighbors(p)) {
                if (!vis[nei.x][nei.y]) {
                    vis[nei.x][nei.y] = true;
                    q.add(nei);
                    visitOrder.add(nei);
                    parent.put(nei, p);
                }
            }
        }
        Result r = new Result();
        r.visitOrder = visitOrder;
        r.visited = new HashSet<>();
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++) if (vis[i][j]) r.visited.add(new Point(i,j));
        if (found) {
            r.path = new ArrayList<>();
            Point cur = model.end;
            while (cur != null) { r.path.add(0, cur); cur = parent.get(cur); }
        }
        return r;
    }

    public static Result solveDFS(MazeModel model) {
        int N = model.getSize();
        Stack<Point> s = new Stack<>();
        Map<Point, Point> parent = new HashMap<>();
        boolean[][] vis = new boolean[N][N];
        java.util.List<Point> visitOrder = new ArrayList<>();
        s.push(model.start);
        vis[model.start.x][model.start.y] = true;
        visitOrder.add(model.start);
        boolean found = false;
        while (!s.isEmpty()) {
            Point p = s.pop();
            if (p.equals(model.end)) { found = true; break; }
            for (Point nei : model.neighbors(p)) {
                if (!vis[nei.x][nei.y]) {
                    vis[nei.x][nei.y] = true;
                    s.push(nei);
                    visitOrder.add(nei);
                    parent.put(nei, p);
                }
            }
        }
        Result r = new Result();
        r.visitOrder = visitOrder;
        r.visited = new HashSet<>();
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++) if (vis[i][j]) r.visited.add(new Point(i,j));
        if (found) {
            r.path = new ArrayList<>();
            Point cur = model.end;
            while (cur != null) { r.path.add(0, cur); cur = parent.get(cur); }
        }
        return r;
    }
}
