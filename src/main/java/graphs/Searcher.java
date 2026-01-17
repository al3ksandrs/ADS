package graphs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;

public class Searcher {

    /**
     * represents a path of connected vertices and edges in the graph
     */
    public static class DGPath<V extends Identifiable> {

        private final SinglyLinkedList<V> vertices = new SinglyLinkedList<>();
        private final Set<V> visited = new HashSet<>();
        private double totalWeight = 0.0;

        /**
         * representation invariants: 1. vertices contains a sequence of
         * vertices that are connected in the graph by a directed edge, i.e. FOR
         * ALL i: 0 < i < vertices.length:
         * this.getEdge(vertices[i-1],vertices[i]) will provide edge information
         * of the connection 2. a path with one vertex has no edges 3. a path
         * without vertices is empty totalWeight is a helper attribute to
         * capture additional info from searches, not a fundamental property of
         * a path visited is a helper set to be able to track visited vertices
         * in searches, not a fundamental property of a path
         *
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(
                    String.format("Weight=%f Length=%d visited=%d (",
                            totalWeight, vertices.size(), visited.size()));
            String separator = "";
            for (V v : vertices) {
                sb.append(separator).append(v.getId());
                separator = ", ";
            }
            sb.append(")");
            return sb.toString();
        }

        public SinglyLinkedList<V> getVertices() {
            return vertices;
        }

        public double getTotalWeight() {
            return totalWeight;
        }

        public void setTotalWeight(double totalWeight) {
            this.totalWeight = totalWeight;
        }

        public Set<V> getVisited() {
            return visited;
        }
    }

    /**
     * Uses a depth-first search algorithm to find a path from the start vertex
     * to the target vertex in the graph All vertices that are being visited by
     * the search should also be registered in path.visited
     *
     * @param startId
     * @param targetId
     * @return the path from start to target returns null if either start or
     * target cannot be matched with a vertex in the graph or no path can be
     * found from start to target
     */
    public static <V extends Identifiable, E> DGPath<V> depthFirstSearch(DirectedGraph<V, E> graph, String startId, String targetId) {

        V start = graph.getVertexById(startId);
        V target = graph.getVertexById(targetId);
        if (start == null || target == null) {
            return null;
        }

        DGPath<V> path = new DGPath<>();

        if (dfsRecursive(graph, start, target, path)) {
            return path;
        }

        return null;
    }

    // Helper method for recursive DFS because we need to keep state through each step
    private static <V extends Identifiable, E> boolean dfsRecursive(DirectedGraph<V, E> graph, V current, V target, DGPath<V> path) {
        // Marks current node as visited so we don't revisit it
        path.getVisited().add(current);

        // Arrived at target, so stop and build path back
        if (current.equals(target)) {
            path.getVertices().addFirst(current);
            return true;
        }

        // Explores each neighbour recursively
        for (V neighbour : graph.getNeighbours(current)) {
            if (!path.getVisited().contains(neighbour) && dfsRecursive(graph, neighbour, target, path)) {
                // If path found through neighbour, add current to path
                path.getVertices().addFirst(current);
                return true;
            }
        }

        return false;
    }

    /**
     * Uses a breadth-first search algorithm to find a path from the start
     * vertex to the target vertex in the graph All vertices that are being
     * visited by the search should also be registered in path.
     *
     * @param startId
     * @param targetId
     * @return the path from start to target returns null if either start or
     * target cannot be matched with a vertex in the graph or no path can be
     * found from start to target
     */
    public static <V extends Identifiable, E> DGPath<V> breadthFirstSearch(DirectedGraph<V, E> graph, String startId, String targetId) {

        V start = graph.getVertexById(startId);
        V target = graph.getVertexById(targetId);
        if (start == null || target == null) {
            return null;
        }

        // initialise the result path of the search
        DGPath<V> path = new DGPath<>();
        path.getVisited().add(start);

        // easy target
        if (start.equals(target)) {
            path.getVertices().add(target);
            return path;
        }

        // Queue to explore nodes fairly (level by level)
        Queue<V> queue = new LinkedList<>();
        // BFS doesn't use recursion and we need to remember where we came from, so we use a map for visited nodes
        Map<V, V> predecessors = new HashMap<>();

        queue.add(start);
        boolean found = false;

        while (!queue.isEmpty()) {
            V current = queue.poll();

            // Target node found
            if (current.equals(target)) {
                found = true;
                break;
            }

            // Explore each neighbour node
            for (V neighbour : graph.getNeighbours(current)) {
                if (!path.getVisited().contains(neighbour)) {
                    path.getVisited().add(neighbour);
                    predecessors.put(neighbour, current);

                    // Add to queue for further exploration
                    queue.add(neighbour);
                }
            }
        }

        if (found) {
            // Reconstructs path backwards from target to start
            V curr = target;
            while (curr != null) {
                path.getVertices().addFirst(curr);
                curr = predecessors.get(curr);
            }
            return path;
        }

        return null;
    }

    // helper class to build the spanning tree of visited vertices in dijkstra's shortest path algorithm
    // your may change this class or delete it altogether follow a different approach in your implementation
    private static class DSPNode<V> implements Comparable<DSPNode<V>> {

        protected V vertex;                // the graph vertex that is concerned with this DSPNode
        protected V fromVertex = null;     // the parent's node vertex that has an edge towards this node's vertex
        protected boolean marked = false;  // indicates DSP processing has been marked complete for this vertex
        protected double weightSumTo = Double.MAX_VALUE;   // sum of weights of current shortest path to this node's vertex

        private DSPNode(V vertex) {
            this.vertex = vertex;
        }

        // comparable interface helps to find a node with the shortest current path, sofar
        @Override
        public int compareTo(DSPNode dspv) {
            return Double.compare(weightSumTo, dspv.weightSumTo);
        }
    }

    /**
     * Calculates the edge-weighted shortest path from start to target according
     * to Dijkstra's algorithm of a minimum spanning tree
     *
     * @param startId id of the start vertex of the search
     * @param targetId id of the target vertex of the search
     * @param weightMapper provides a function, by which the weight of an edge
     * can be retrieved or calculated
     * @return the shortest path from start to target returns null if either
     * start or target cannot be matched with a vertex in the graph or no path
     * can be found from start to target
     */
    public static <V extends Identifiable, E> DGPath<V> dijkstraShortestPath(
            DirectedGraph<V, E> graph, String startId, String targetId,
            Function<E, Double> weightMapper) {

        V start = graph.getVertexById(startId);
        V target = graph.getVertexById(targetId);
        if (start == null || target == null) {
            return null;
        }

        // initialise the result path of the search
        DGPath<V> path = new DGPath<>();

        // easy target
        if (start.equals(target)) {
            path.getVertices().add(start);
            path.getVisited().add(start);
            return path;
        }

        Map<V, DSPNode<V>> progressData = new HashMap<>();
        // Priority queue to always expand the least cost node next
        PriorityQueue<DSPNode<V>> pq = new PriorityQueue<>();

        // initialise the progress of the start node
        DSPNode<V> startNode = new DSPNode<>(start);
        startNode.weightSumTo = 0.0;
        progressData.put(start, startNode);
        pq.add(startNode);

        while (!pq.isEmpty()) {
            DSPNode<V> currentDSP = pq.poll();
            V currentVertex = currentDSP.vertex;

            if (currentDSP.marked) {
                continue;
            }

            // Mark as visited/processed
            currentDSP.marked = true;
            path.getVisited().add(currentVertex);

            // Target found, build path back
            if (currentVertex.equals(target)) {
                return buildPathFromDSP(currentDSP, path, progressData);
            }

            // Explore each neighbour
            for (V neighbour : graph.getNeighbours(currentVertex)) {
                processNeighbour(graph, currentVertex, neighbour, currentDSP, progressData, pq, weightMapper);
            }
        }

        return null;
    }

    // Reconstructs the path backwards (Reduces nesting depth)
    private static <V extends Identifiable> DGPath<V> buildPathFromDSP(
            DSPNode<V> endNode, DGPath<V> path, Map<V, DSPNode<V>> progressData) {

        path.setTotalWeight(endNode.weightSumTo);

        DSPNode<V> node = endNode;

        while (node != null) {
            path.getVertices().addFirst(node.vertex);
            node = (node.fromVertex == null) ? null : progressData.get(node.fromVertex);
        }

        return path;
    }

    // Processes a neighbour node during Dijkstra's algorithm
    private static <V extends Identifiable, E> void processNeighbour(
            DirectedGraph<V, E> graph, V currentVertex, V neighbour,
            DSPNode<V> currentDSP, Map<V, DSPNode<V>> progressData,
            PriorityQueue<DSPNode<V>> pq, Function<E, Double> weightMapper) {

        // Calculate new distance to neighbour through current vertex
        E edge = graph.getEdge(currentVertex, neighbour);
        double weight = weightMapper.apply(edge);
        double newDist = currentDSP.weightSumTo + weight;

        // Get or create neighbour DSP node
        DSPNode<V> neighbourNode = progressData.get(neighbour);

        if (neighbourNode == null) {
            neighbourNode = new DSPNode<>(neighbour);
            progressData.put(neighbour, neighbourNode);
        }

        // Update neighbour if not yet processed and new distance is better
        if (!neighbourNode.marked && newDist < neighbourNode.weightSumTo) {
            neighbourNode.weightSumTo = newDist;
            neighbourNode.fromVertex = currentVertex;
            pq.add(neighbourNode);
        }
    }
}
