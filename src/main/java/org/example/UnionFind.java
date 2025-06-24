package org.example;

import java.util.Arrays;

public class UnionFind {

        private int[] parent;

        public UnionFind(int n) {
            parent = new int[n];
            Arrays.fill(parent, -1);
        }

        public int find(int x) {
            if (parent[x] < 0) return x;
            parent[x] = find(parent[x]);
            return parent[x];
        }

        public void union(int x, int y) {
            int xr = find(x);
            int yr = find(y);
            if (xr == yr) return;

            if (parent[xr] < parent[yr]) {
                parent[xr] += parent[yr];
                parent[yr] = xr;
            } else {
                parent[yr] += parent[xr];
                parent[xr] = yr;
            }
        }

}
