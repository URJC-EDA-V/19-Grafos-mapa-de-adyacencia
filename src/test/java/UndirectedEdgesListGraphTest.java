import es.urjc.grafo.EDA.graphs.Edge;
import es.urjc.grafo.EDA.graphs.EdgesListGraph;
import es.urjc.grafo.EDA.graphs.UndirectedGraph;
import es.urjc.grafo.EDA.graphs.Vertex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UndirectedEdgesListGraphTest {

    private static final String[][] edgesFirstExampleGraph = {
            {"A","B"}, {"A","E"}, {"A","F"}, {"B","C"}, {"B","F"},
            {"C","D"}, {"C","G"}, {"D","G"}, {"D","H"}, {"E","F"},
            {"E","I"}, {"F","I"}, {"G","J"}, {"G","K"}, {"G","L"},
            {"H","L"}, {"I","J"}, {"I","M"}, {"I","N"}, {"J","K"},
            {"K","N"}, {"K","O"}, {"L","P"}, {"M","N"},
    };
    private static final Set<String> availableVerticesFirstExample =
            new HashSet<>(
                    Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P")
            );

    private UndirectedGraph<String, Integer> graphFirstExample;

    private Vertex<String> vertexA;
    private Vertex<String> vertexB;
    private Vertex<String> vertexC;
    private Vertex<String> vertexD;
    private Vertex<String> vertexE;
    private Vertex<String> vertexF;
    private Vertex<String> vertexG;
    private Vertex<String> vertexH;
    private Vertex<String> vertexI;
    private Vertex<String> vertexJ;
    private Vertex<String> vertexK;
    private Vertex<String> vertexL;
    private Vertex<String> vertexM;
    private Vertex<String> vertexN;
    private Vertex<String> vertexO;
    private Vertex<String> vertexP;

    @BeforeEach
    public void setUpGraph() {
        graphFirstExample = EdgesListGraph.graphFromEdgelist(edgesFirstExampleGraph, false);
        vertexA = findVertex("A");
        vertexB = findVertex("B");
        vertexC = findVertex("C");
        vertexD = findVertex("D");
        vertexE = findVertex("E");
        vertexF = findVertex("F");
        vertexG = findVertex("G");
        vertexH = findVertex("H");
        vertexI = findVertex("I");
        vertexJ = findVertex("J");
        vertexK = findVertex("K");
        vertexL = findVertex("L");
        vertexM = findVertex("M");
        vertexN = findVertex("N");
        vertexO = findVertex("O");
        vertexP = findVertex("P");
    }

    private Vertex<String> findVertex(String label) {
        return graphFirstExample.vertices().stream()
                .filter(v -> v.getElement().equals(label))
                .findFirst()
                .orElseThrow();
    }

    @Test
    public void testVertices() {
        Collection<Vertex<String>> vertices = graphFirstExample.vertices();

        assertEquals(availableVerticesFirstExample.size(), vertices.size());
        for (Vertex<String> v : vertices) {
            assertTrue(availableVerticesFirstExample.contains(v.getElement()));
        }
        graphFirstExample.insertVertex("Q");
        assertEquals(availableVerticesFirstExample.size() + 1, graphFirstExample.vertices().size());
        assertTrue(graphFirstExample.vertices().stream().anyMatch(v -> v.getElement().equals("Q")));
    }

    @Test
    public void testEdges() {
        Collection<Edge<Integer>> edges = graphFirstExample.edges();

        assertEquals(edgesFirstExampleGraph.length, edges.size());
        for (Edge<Integer> e : edges) {
            assertTrue(
                    Arrays.stream(edgesFirstExampleGraph).anyMatch(
                            edge -> (edge[0].equals(graphFirstExample.endVertices(e).getFirst().getElement())
                                    && edge[1].equals(graphFirstExample.endVertices(e).getSecond().getElement()))
                                    ||
                                    (edge[1].equals(graphFirstExample.endVertices(e).getFirst().getElement())
                                            && edge[0].equals(graphFirstExample.endVertices(e).getSecond().getElement()))
                    )
            );
        }
    }

    @Test
    public void testOpposite() {
        Edge<Integer> edgeAB = graphFirstExample.getEdge(vertexA, vertexB);
        assertEquals(vertexB, graphFirstExample.opposite(vertexA, edgeAB));
        assertEquals(vertexA, graphFirstExample.opposite(vertexB, edgeAB));

        Edge<Integer> edgeBA = graphFirstExample.getEdge(vertexB, vertexA);
        assertEquals(vertexA, graphFirstExample.opposite(vertexB, edgeBA));
        assertEquals(vertexB, graphFirstExample.opposite(vertexA, edgeBA));

        Edge<Integer> edgeAE = graphFirstExample.getEdge(vertexE, vertexA);
        assertEquals(vertexA, graphFirstExample.opposite(vertexE, edgeAE));
        assertEquals(vertexE, graphFirstExample.opposite(vertexA, edgeAE));
    }

    @Test
    public void testReplaceEdge() {
        Edge<Integer> edgeAB = graphFirstExample.getEdge(vertexA, vertexB);
        assertEquals(1, edgeAB.getElement());
        assertEquals(edgesFirstExampleGraph.length, graphFirstExample.edges().size());

        graphFirstExample.removeEdge(graphFirstExample.getEdge(vertexA, vertexB));
        edgeAB = graphFirstExample.insertEdge(vertexA, vertexB, 2);
        assertEquals(2, edgeAB.getElement());
        assertEquals(edgesFirstExampleGraph.length, graphFirstExample.edges().size());

        edgeAB = graphFirstExample.insertEdge(vertexA, vertexB, 3);
        assertEquals(3, edgeAB.getElement());
        assertEquals(edgesFirstExampleGraph.length, graphFirstExample.edges().size());

        edgeAB = graphFirstExample.insertEdge(vertexB, vertexA, 4);
        assertEquals(4, edgeAB.getElement());
        assertEquals(edgesFirstExampleGraph.length, graphFirstExample.edges().size());
    }

    @Test
    public void testIncidentEdges() {
        Collection<Edge<Integer>> incidentEdgesA = graphFirstExample.incidentEdges(vertexA);
        assertEquals(3, incidentEdgesA.size());
        assertTrue(incidentEdgesA.stream().anyMatch(e -> graphFirstExample.opposite(vertexA, e).getElement().equals("B")));
        assertTrue(incidentEdgesA.stream().anyMatch(e -> graphFirstExample.opposite(vertexA, e).getElement().equals("E")));
        assertTrue(incidentEdgesA.stream().anyMatch(e -> graphFirstExample.opposite(vertexA, e).getElement().equals("F")));

        Collection<Edge<Integer>> incidentEdgesN = graphFirstExample.incidentEdges(vertexN);
        assertEquals(3, incidentEdgesN.size());
        assertTrue(incidentEdgesN.stream().anyMatch(e -> graphFirstExample.opposite(vertexN, e).getElement().equals("I")));
        assertTrue(incidentEdgesN.stream().anyMatch(e -> graphFirstExample.opposite(vertexN, e).getElement().equals("M")));
        assertTrue(incidentEdgesN.stream().anyMatch(e -> graphFirstExample.opposite(vertexN, e).getElement().equals("K")));

        Collection<Edge<Integer>> incidentEdgesE = graphFirstExample.incidentEdges(vertexE);
        assertEquals(3, incidentEdgesE.size());
        assertTrue(incidentEdgesE.stream().anyMatch(e -> graphFirstExample.opposite(vertexE, e).getElement().equals("A")));
        assertTrue(incidentEdgesE.stream().anyMatch(e -> graphFirstExample.opposite(vertexE, e).getElement().equals("F")));
        assertTrue(incidentEdgesE.stream().anyMatch(e -> graphFirstExample.opposite(vertexE, e).getElement().equals("I")));
    }

    @Test
    public void testAreAdjacent() {
        assertTrue(graphFirstExample.areAdjacent(vertexA, vertexB));
        assertTrue(graphFirstExample.areAdjacent(vertexB, vertexA));

        assertTrue(graphFirstExample.areAdjacent(vertexA, vertexE));
        assertTrue(graphFirstExample.areAdjacent(vertexE, vertexA));

        assertTrue(graphFirstExample.areAdjacent(vertexA, vertexF));
        assertTrue(graphFirstExample.areAdjacent(vertexF, vertexA));

        assertTrue(graphFirstExample.areAdjacent(vertexB, vertexC));
        assertTrue(graphFirstExample.areAdjacent(vertexC, vertexB));

        assertTrue(graphFirstExample.areAdjacent(vertexB, vertexF));
        assertTrue(graphFirstExample.areAdjacent(vertexF, vertexB));

        assertTrue(graphFirstExample.areAdjacent(vertexC, vertexD));
        assertTrue(graphFirstExample.areAdjacent(vertexD, vertexC));

        assertTrue(graphFirstExample.areAdjacent(vertexC, vertexG));
        assertTrue(graphFirstExample.areAdjacent(vertexG, vertexC));

        assertTrue(graphFirstExample.areAdjacent(vertexD, vertexG));
        assertTrue(graphFirstExample.areAdjacent(vertexG, vertexD));

        assertTrue(graphFirstExample.areAdjacent(vertexD, vertexH));
        assertTrue(graphFirstExample.areAdjacent(vertexH, vertexD));

        assertTrue(graphFirstExample.areAdjacent(vertexE, vertexF));
        assertTrue(graphFirstExample.areAdjacent(vertexF, vertexE));

        assertTrue(graphFirstExample.areAdjacent(vertexE, vertexI));
        assertTrue(graphFirstExample.areAdjacent(vertexI, vertexE));

        assertTrue(graphFirstExample.areAdjacent(vertexF, vertexI));
        assertTrue(graphFirstExample.areAdjacent(vertexI, vertexF));

        assertTrue(graphFirstExample.areAdjacent(vertexG, vertexJ));
        assertTrue(graphFirstExample.areAdjacent(vertexJ, vertexG));

        assertTrue(graphFirstExample.areAdjacent(vertexG, vertexK));
        assertTrue(graphFirstExample.areAdjacent(vertexK, vertexG));

        assertTrue(graphFirstExample.areAdjacent(vertexG, vertexL));
        assertTrue(graphFirstExample.areAdjacent(vertexL, vertexG));

        assertTrue(graphFirstExample.areAdjacent(vertexH, vertexL));
        assertTrue(graphFirstExample.areAdjacent(vertexL, vertexH));

        assertTrue(graphFirstExample.areAdjacent(vertexI, vertexJ));
        assertTrue(graphFirstExample.areAdjacent(vertexJ, vertexI));

        assertTrue(graphFirstExample.areAdjacent(vertexI, vertexM));
        assertTrue(graphFirstExample.areAdjacent(vertexM, vertexI));

        assertTrue(graphFirstExample.areAdjacent(vertexI, vertexN));
        assertTrue(graphFirstExample.areAdjacent(vertexN, vertexI));
    }

    @Test
    public void testRemoveVertex() {
        int originalSize = graphFirstExample.vertices().size();
        assertEquals("A", graphFirstExample.removeVertex(vertexA));
        assertFalse(graphFirstExample.vertices().contains(vertexA));
        assertEquals(originalSize - 1, graphFirstExample.vertices().size());
        assertEquals(edgesFirstExampleGraph.length - 3, graphFirstExample.edges().size());
        assertFalse(graphFirstExample.edges().stream().anyMatch(e ->
                graphFirstExample.endVertices(e).getFirst().getElement().equals("A")
                        || graphFirstExample.endVertices(e).getSecond().getElement().equals("A")));
    }

    @Test
    public void testRemoveEdge() {
        int originalSize = graphFirstExample.edges().size();
        Edge<Integer> edgeAB = graphFirstExample.getEdge(vertexA, vertexB);
        assertEquals(1, graphFirstExample.removeEdge(edgeAB));
        assertFalse(graphFirstExample.edges().contains(edgeAB));
        assertEquals(originalSize - 1, graphFirstExample.edges().size());
        assertFalse(graphFirstExample.areAdjacent(vertexA, vertexB));
    }

}

