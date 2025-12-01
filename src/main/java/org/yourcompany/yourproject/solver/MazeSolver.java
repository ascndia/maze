package org.yourcompany.yourproject.solver;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
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

    public static Result dijkstra(MazeModel model) {
        int N = model.getSize();
        java.util.List<java.awt.Point> visitOrder = new ArrayList<>();
        Map<java.awt.Point, Integer> dist = new HashMap<>();
        Map<java.awt.Point, java.awt.Point> parent = new HashMap<>();
        PriorityQueue<java.awt.Point> pq = new PriorityQueue<>(Comparator.comparingInt(dist::get));
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++) dist.put(new java.awt.Point(i,j), Integer.MAX_VALUE);
        java.awt.Point start = model.start, end = model.end;
        dist.put(start, model.getCost(start));
        pq.add(start);
        boolean found = false;
        while (!pq.isEmpty()) {
            java.awt.Point u = pq.poll();
            if (!visitOrder.contains(u)) visitOrder.add(u);
            if (u.equals(end)) { found = true; break; }
            int du = dist.get(u);
            for (java.awt.Point v : model.neighbors(u)) {
                int alt = du + model.getCost(v);
                if (alt < dist.get(v)) {
                    dist.put(v, alt);
                    parent.put(v, u);
                    pq.remove(v);
                    pq.add(v);
                }
            }
        }
        Result r = new Result();
        r.visitOrder = visitOrder;
        r.visited = new HashSet<>();
        for (Map.Entry<java.awt.Point,Integer> e : dist.entrySet()) if (e.getValue() < Integer.MAX_VALUE) r.visited.add(new java.awt.Point(e.getKey()));
        if (found) {
            r.path = new ArrayList<>();
            java.awt.Point cur = end;
            while (cur != null) { r.path.add(0, cur); cur = parent.get(cur); }
        }
        return r;
    }

    public static Result aStar(MazeModel model) {
        int N = model.getSize();
        java.util.List<java.awt.Point> visitOrder = new ArrayList<>();
        Map<java.awt.Point, Integer> g = new HashMap<>();
        Map<java.awt.Point, Integer> f = new HashMap<>();
        Map<java.awt.Point, java.awt.Point> parent = new HashMap<>();
        java.awt.Point start = model.start, end = model.end;
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++) { java.awt.Point p = new java.awt.Point(i,j); g.put(p, Integer.MAX_VALUE); f.put(p, Integer.MAX_VALUE); }
        g.put(start, model.getCost(start));
        f.put(start, g.get(start) + heuristic(start, end));
        PriorityQueue<java.awt.Point> open = new PriorityQueue<>(Comparator.comparingInt(f::get));
        open.add(start);
        boolean found = false;
        while (!open.isEmpty()) {
            java.awt.Point u = open.poll();
            if (!visitOrder.contains(u)) visitOrder.add(u);
            if (u.equals(end)) { found = true; break; }
            for (java.awt.Point v : model.neighbors(u)) {
                int tentative = g.get(u) + model.getCost(v);
                if (tentative < g.get(v)) {
                    parent.put(v, u);
                    g.put(v, tentative);
                    f.put(v, tentative + heuristic(v, end));
                    open.remove(v);
                    open.add(v);
                }
            }
        }
        Result r = new Result();
        r.visitOrder = visitOrder;
        r.visited = new HashSet<>();
        for (Map.Entry<java.awt.Point,Integer> e : g.entrySet()) if (e.getValue() < Integer.MAX_VALUE) r.visited.add(new java.awt.Point(e.getKey()));
        if (found) {
            r.path = new ArrayList<>();
            java.awt.Point cur = end;
            while (cur != null) { r.path.add(0, cur); cur = parent.get(cur); }
        }
        return r;
    }

    private static int heuristic(java.awt.Point a, java.awt.Point b) {
        // Manhattan distance multiplied by minimum terrain cost (1)
        return (Math.abs(a.x - b.x) + Math.abs(a.y - b.y));
    }
}
