package ru.spbu.videosegmentation;

import org.jgrapht.*;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.PrimMinimumSpanningTree;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;


public class MSTClusterMerging {
    private final int threshold;
    private final double pos_weight;

    public MSTClusterMerging(int threshold, double pos_weight) {
        this.threshold = threshold;
        this.pos_weight = pos_weight;
    }

    int[] merge(double[][] segments, int[] labels) {
        int[] result = labels.clone();
        int graph_size = segments.length;

        final WeightedGraph<Integer, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        for(int i = 0; i < graph_size; i++) {
            graph.addVertex(i);
        }
        for(int i = 0; i < graph_size; i++) {
            for(int j = 0; j < graph_size; j++) {
                if(i == j) {
                    continue;
                }
                DefaultWeightedEdge edge = new DefaultWeightedEdge();
                graph.addEdge(i, j, edge);
                graph.setEdgeWeight(edge, distance(segments[i], segments[j]));
            }
        }

        PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> prim = new PrimMinimumSpanningTree<>(graph);
        Set<DefaultWeightedEdge> mst = prim.getMinimumSpanningTreeEdgeSet();
        mst = mst.stream().filter(e -> graph.getEdgeWeight(e) < threshold).collect(Collectors.toSet());

        final UndirectedGraph<Integer, DefaultEdge> forest = new SimpleGraph<>(DefaultEdge.class);
        for(int i = 0; i < graph_size; i++) {
            forest.addVertex(i);
        }
        for(DefaultWeightedEdge e : mst) {
            forest.addEdge(graph.getEdgeSource(e), graph.getEdgeTarget(e));
        }

        ConnectivityInspector<Integer, DefaultEdge> connectivityInspector = new ConnectivityInspector<>(forest);
        List<Set<Integer>> connectedSets = connectivityInspector.connectedSets();

        int[] labels_remapping = new int[graph_size];
        Arrays.fill(labels_remapping, -1);

        int componentId = 0;
        for(Set<Integer> component : connectedSets) {
            for(Integer vertex : component) {
                labels_remapping[vertex] = componentId;
            }
            componentId++;
        }

        for(int i = 0; i < result.length; i++) {
            result[i] = labels_remapping[result[i]];
        }

        return result;
    }

    private double distance(double[] a, double[] b) {
        double result = 0;
        for(int i = 0; i < 3; i++) {
            result += Math.pow(a[i] - b[i], 2);
        }
        for(int i = 3; i < 5; i++) {
            result += pos_weight * Math.abs(a[i] - b[i]);
        }
        return result;
    }
}
