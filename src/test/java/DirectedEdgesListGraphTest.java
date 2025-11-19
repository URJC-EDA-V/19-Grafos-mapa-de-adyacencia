import es.urjc.grafo.EDA.graphs.DirectedGraph;
import es.urjc.grafo.EDA.graphs.Edge;
import es.urjc.grafo.EDA.graphs.EdgesListGraph;
import es.urjc.grafo.EDA.graphs.Vertex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DirectedEdgesListGraphTest {

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

    private DirectedGraph<String, Integer> graphFirstExample;

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
        graphFirstExample = EdgesListGraph.graphFromEdgelist(edgesFirstExampleGraph, true);
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
        Set<String> foundVertices = new HashSet<>();
        for (Vertex<String> v : vertices) {
            assertTrue(availableVerticesFirstExample.contains(v.getElement()));
            foundVertices.add(v.getElement());
        }
        assertEquals(availableVerticesFirstExample, foundVertices);
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
                            edge -> (edge[0].equals(graphFirstExample.startVertex(e).getElement())
                                    && edge[1].equals(graphFirstExample.endVertex(e).getElement()))
                    )
            );
            assertFalse(
                    Arrays.stream(edgesFirstExampleGraph).anyMatch(
                            edge -> (edge[0].equals(graphFirstExample.endVertex(e).getElement())
                                    && edge[1].equals(graphFirstExample.startVertex(e).getElement()))
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
        assertNull(edgeBA);

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

        edgeAB = graphFirstExample.insertEdge(vertexA, vertexB, 1);
        assertEquals(1, edgeAB.getElement());
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
    public void testOutputEdges() {
        Collection<Edge<Integer>> outputFromA = graphFirstExample.outputEdges(vertexA);
        assertEquals(3, outputFromA.size());
        assertTrue(outputFromA.stream().anyMatch(e -> graphFirstExample.endVertex(e).equals(vertexB)));
        assertTrue(outputFromA.stream().anyMatch(e -> graphFirstExample.endVertex(e).equals(vertexE)));
        assertTrue(outputFromA.stream().anyMatch(e -> graphFirstExample.endVertex(e).equals(vertexF)));

        Collection<Edge<Integer>> outputFromF = graphFirstExample.outputEdges(vertexF);
        assertEquals(1, outputFromF.size());
        assertTrue(outputFromF.stream().anyMatch(e -> graphFirstExample.endVertex(e).equals(vertexI)));

        Collection<Edge<Integer>> outputFromN = graphFirstExample.outputEdges(vertexN);
        assertEquals(0, outputFromN.size());
    }

    @Test
    public void testReachableVertices() {
        Collection<Vertex<String>> reachableFromI = graphFirstExample.reachableVertices(vertexI);
        assertEquals(3, reachableFromI.size());
        assertTrue(reachableFromI.stream().anyMatch(v -> v.getElement().equals("J")));
        assertTrue(reachableFromI.stream().anyMatch(v -> v.getElement().equals("M")));
        assertTrue(reachableFromI.stream().anyMatch(v -> v.getElement().equals("N")));

        Collection<Vertex<String>> reachableFromC = graphFirstExample.reachableVertices(vertexC);
        assertEquals(2, reachableFromC.size());
        assertTrue(reachableFromC.stream().anyMatch(v -> v.getElement().equals("D")));
        assertTrue(reachableFromC.stream().anyMatch(v -> v.getElement().equals("G")));

        Collection<Vertex<String>> reachableFromP = graphFirstExample.reachableVertices(vertexP);
        assertTrue(reachableFromP.isEmpty());
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

