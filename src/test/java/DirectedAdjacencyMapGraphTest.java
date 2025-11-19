import es.urjc.grafo.EDA.graphs.AdjacencyMapGraph;
import es.urjc.grafo.EDA.graphs.DirectedGraph;
import es.urjc.grafo.EDA.graphs.Edge;
import es.urjc.grafo.EDA.graphs.Vertex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class DirectedAdjacencyMapGraphTest {

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

    private static DirectedGraph<String, Integer> graphFirstExample;

    private static Vertex<String> vertexA;
    private static Vertex<String> vertexB;
    private static Vertex<String> vertexC;
    private static Vertex<String> vertexD;
    private static Vertex<String> vertexE;
    private static Vertex<String> vertexF;
    private static Vertex<String> vertexG;
    private static Vertex<String> vertexH;
    private static Vertex<String> vertexI;
    private static Vertex<String> vertexJ;
    private static Vertex<String> vertexK;
    private static Vertex<String> vertexL;
    private static Vertex<String> vertexM;
    private static Vertex<String> vertexN;
    private static Vertex<String> vertexO;
    private static Vertex<String> vertexP;

    @BeforeEach
    public void setUpGraph() {
        graphFirstExample = AdjacencyMapGraph.graphFromEdgelist(edgesFirstExampleGraph, true);
        vertexA = graphFirstExample.vertices().stream().filter(v -> v.getElement().equals("A")).findFirst().get();
        vertexB = graphFirstExample.vertices().stream().filter(v -> v.getElement().equals("B")).findFirst().get();
        vertexC = graphFirstExample.vertices().stream().filter(v -> v.getElement().equals("C")).findFirst().get();
        vertexD = graphFirstExample.vertices().stream().filter(v -> v.getElement().equals("D")).findFirst().get();
        vertexE = graphFirstExample.vertices().stream().filter(v -> v.getElement().equals("E")).findFirst().get();
        vertexF = graphFirstExample.vertices().stream().filter(v -> v.getElement().equals("F")).findFirst().get();
        vertexG = graphFirstExample.vertices().stream().filter(v -> v.getElement().equals("G")).findFirst().get();
        vertexH = graphFirstExample.vertices().stream().filter(v -> v.getElement().equals("H")).findFirst().get();
        vertexI = graphFirstExample.vertices().stream().filter(v -> v.getElement().equals("I")).findFirst().get();
        vertexJ = graphFirstExample.vertices().stream().filter(v -> v.getElement().equals("J")).findFirst().get();
        vertexK = graphFirstExample.vertices().stream().filter(v -> v.getElement().equals("K")).findFirst().get();
        vertexL = graphFirstExample.vertices().stream().filter(v -> v.getElement().equals("L")).findFirst().get();
        vertexM = graphFirstExample.vertices().stream().filter(v -> v.getElement().equals("M")).findFirst().get();
        vertexN = graphFirstExample.vertices().stream().filter(v -> v.getElement().equals("N")).findFirst().get();
        vertexO = graphFirstExample.vertices().stream().filter(v -> v.getElement().equals("O")).findFirst().get();
        vertexP = graphFirstExample.vertices().stream().filter(v -> v.getElement().equals("P")).findFirst().get();
    }

    @Test
    public void testVertices() {
        Collection<Vertex<String>> vertices = graphFirstExample.vertices();

        assertEquals(availableVerticesFirstExample.size(), graphFirstExample.vertices().size());
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
                    // Checks that the edge is in the list of edges
                    Arrays.stream(edgesFirstExampleGraph).anyMatch(
                            edge -> (edge[0].equals(graphFirstExample.startVertex(e).getElement())
                                        && edge[1].equals(graphFirstExample.endVertex(e).getElement())
                            )
                    )
            );
            assertFalse(
                    // Checks that the reversed edge is not in the list of edges, since direction matters here
                    Arrays.stream(edgesFirstExampleGraph).anyMatch(
                            edge -> (edge[0].equals(graphFirstExample.endVertex(e).getElement())
                                    && edge[1].equals(graphFirstExample.startVertex(e).getElement())
                            )
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
        assertNull(edgeBA); // There is no edge from B to A, only from A to B

        Edge<Integer> edgeAE = graphFirstExample.getEdge(vertexE, vertexA);
        assertNull(edgeAE);
        edgeAE = graphFirstExample.getEdge(vertexA, vertexE);
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
    }

    @Test
    public void testIncidentEdges() {
        Collection<Edge<Integer>> incidentEdgesA = graphFirstExample.incidentEdges(vertexA);
        assertEquals(0, incidentEdgesA.size());

        Collection<Edge<Integer>> incidentEdgesN = graphFirstExample.incidentEdges(vertexN);
        assertEquals(3, incidentEdgesN.size());
        assertTrue(incidentEdgesN.stream().anyMatch(e -> graphFirstExample.opposite(vertexN, e).getElement().equals("I")));
        assertTrue(incidentEdgesN.stream().anyMatch(e -> graphFirstExample.opposite(vertexN, e).getElement().equals("M")));
        assertTrue(incidentEdgesN.stream().anyMatch(e -> graphFirstExample.opposite(vertexN, e).getElement().equals("K")));

        Collection<Edge<Integer>> incidentEdgesE = graphFirstExample.incidentEdges(vertexE);
        assertEquals(1, incidentEdgesE.size());
        assertTrue(incidentEdgesE.stream().anyMatch(e -> graphFirstExample.opposite(vertexE, e).getElement().equals("A")));
    }

    @Test
    public void testAreAdjacent() {
        assertTrue(graphFirstExample.areAdjacent(vertexA, vertexB));
        assertFalse(graphFirstExample.areAdjacent(vertexB, vertexA));

        assertTrue(graphFirstExample.areAdjacent(vertexA, vertexE));
        assertFalse(graphFirstExample.areAdjacent(vertexE, vertexA));

        assertTrue(graphFirstExample.areAdjacent(vertexA, vertexF));
        assertFalse(graphFirstExample.areAdjacent(vertexF, vertexA));

        assertTrue(graphFirstExample.areAdjacent(vertexB, vertexC));
        assertFalse(graphFirstExample.areAdjacent(vertexC, vertexB));

        assertTrue(graphFirstExample.areAdjacent(vertexB, vertexF));
        assertFalse(graphFirstExample.areAdjacent(vertexF, vertexB));

        assertTrue(graphFirstExample.areAdjacent(vertexC, vertexD));
        assertFalse(graphFirstExample.areAdjacent(vertexD, vertexC));

        assertTrue(graphFirstExample.areAdjacent(vertexC, vertexG));
        assertFalse(graphFirstExample.areAdjacent(vertexG, vertexC));

        assertTrue(graphFirstExample.areAdjacent(vertexD, vertexG));
        assertFalse(graphFirstExample.areAdjacent(vertexG, vertexD));

        assertTrue(graphFirstExample.areAdjacent(vertexD, vertexH));
        assertFalse(graphFirstExample.areAdjacent(vertexH, vertexD));

        assertTrue(graphFirstExample.areAdjacent(vertexE, vertexF));
        assertFalse(graphFirstExample.areAdjacent(vertexF, vertexE));

        assertTrue(graphFirstExample.areAdjacent(vertexE, vertexI));
        assertFalse(graphFirstExample.areAdjacent(vertexI, vertexE));

        assertTrue(graphFirstExample.areAdjacent(vertexF, vertexI));
        assertFalse(graphFirstExample.areAdjacent(vertexI, vertexF));

        assertTrue(graphFirstExample.areAdjacent(vertexG, vertexJ));
        assertFalse(graphFirstExample.areAdjacent(vertexJ, vertexG));

        assertTrue(graphFirstExample.areAdjacent(vertexG, vertexK));
        assertFalse(graphFirstExample.areAdjacent(vertexK, vertexG));

        assertTrue(graphFirstExample.areAdjacent(vertexG, vertexL));
        assertFalse(graphFirstExample.areAdjacent(vertexL, vertexG));

        assertTrue(graphFirstExample.areAdjacent(vertexH, vertexL));
        assertFalse(graphFirstExample.areAdjacent(vertexL, vertexH));

        assertTrue(graphFirstExample.areAdjacent(vertexI, vertexJ));
        assertFalse(graphFirstExample.areAdjacent(vertexJ, vertexI));

        assertTrue(graphFirstExample.areAdjacent(vertexI, vertexM));
        assertFalse(graphFirstExample.areAdjacent(vertexM, vertexI));

        assertTrue(graphFirstExample.areAdjacent(vertexI, vertexN));
        assertFalse(graphFirstExample.areAdjacent(vertexN, vertexI));
    }

    @Test
    public void testRemoveVertex() {
        int originalSize = graphFirstExample.vertices().size();
        assertEquals("A", graphFirstExample.removeVertex(vertexA));
        assertFalse(graphFirstExample.vertices().contains(vertexA));
        assertEquals(originalSize - 1, graphFirstExample.vertices().size());
        assertEquals(edgesFirstExampleGraph.length - 3, graphFirstExample.edges().size());
        assertFalse(graphFirstExample.edges().stream().anyMatch(e ->
                graphFirstExample.startVertex(e).getElement().equals("A")
                        || graphFirstExample.endVertex(e).getElement().equals("A")));
    }

    @Test
    public void testRemoveEdge() {
        int originalSize = graphFirstExample.edges().size();
        Edge<Integer> edgeAB = graphFirstExample.getEdge(vertexA, vertexB);
        assertEquals(1, graphFirstExample.removeEdge(edgeAB));
        assertFalse(graphFirstExample.edges().contains(edgeAB));
        assertEquals(originalSize - 1, graphFirstExample.edges().size());
        assertFalse(graphFirstExample.areAdjacent(vertexA, vertexB));

        graphFirstExample.insertEdge(vertexA, vertexB, 1);
        graphFirstExample.insertEdge(vertexB, vertexA, 2);
        edgeAB = graphFirstExample.getEdge(vertexA, vertexB);
        assertEquals(1, graphFirstExample.removeEdge(edgeAB));
        assertFalse(graphFirstExample.areAdjacent(vertexA, vertexB));
        assertTrue(graphFirstExample.areAdjacent(vertexB, vertexA));
    }
}
