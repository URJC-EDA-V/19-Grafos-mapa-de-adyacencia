package es.urjc.grafo.EDA.graphs;

import es.urjc.grafo.EDA.utils.Pair;

import java.util.*;

public class AdjacencyMapGraph<V, E> implements UndirectedGraph<V, E>, DirectedGraph<V, E> {

    private final boolean isDirected;
    private final Map<Vertex<V>, Vertex<V>> vertices = new HashMap<>();
    private final Map<Edge<E>, Edge<E>> edges = new HashMap<>();

    public AdjacencyMapGraph(boolean directed) {
        isDirected = directed;
    }

    /**
     * Construye un grafo a partir de una lista de pares (o tripletas) de cadenas.
     * Cada entrada receivedEdges[i] contiene al menos dos elementos: etiqueta origen y etiqueta destino.
     * Si se proporciona un tercer elemento, se interpreta como el valor (Integer) de la arista.
     * Las etiquetas de vértice se insertan en orden alfabético.
     *
     * @param receivedEdges matriz de parejas (o tripletas) que describen las aristas
     * @param directed true si el grafo resultante debe ser dirigido
     * @return un grafo EdgesListGraph con los vértices y aristas indicados
     */
    public static AdjacencyMapGraph<String, Integer> graphFromEdgelist(String[][] receivedEdges, boolean directed) {
        AdjacencyMapGraph<String, Integer> g = new AdjacencyMapGraph<>(directed);

        // first pass to get sorted set of vertex labels
        TreeSet<String> labels = new TreeSet<>();
        for (String[] receivedEdge : receivedEdges) {
            labels.add(receivedEdge[0]);
            labels.add(receivedEdge[1]);
        }

        // now create vertices (in alphabetical order)
        HashMap<String, Vertex<String>> verts = new HashMap<>();
        for (String label : labels)
            verts.put(label, g.insertVertex(label));

        // now add edges to the graph
        for (String[] receivedEdge : receivedEdges) {
            Integer cost = (receivedEdge.length == 2 ? 1 : Integer.parseInt(receivedEdge[2]));
            g.insertEdge(verts.get(receivedEdge[0]), verts.get(receivedEdge[1]), cost);
        }
        return g;
    }

    //---------------- nested Vertex class ----------------

    /**
     * A vertex of an adjacency map graph representation.
     */
    private class InnerVertex<U> implements Vertex<U> {
        private final U element;
        private final Map<Vertex<U>, Edge<E>> outgoing;
        private final Map<Vertex<U>, Edge<E>> incoming;

        /**
         * Constructs a new InnerVertex instance storing the given element.
         */
        public InnerVertex(U elem) {
            element = elem;
            outgoing = new HashMap<>();
            if (isDirected) incoming = new HashMap<>();
            else
                // if undirected, alias outgoing map as incoming
                // De esta manera, solo tenemos un mapa de adyacencia donde se encuentran las aristas de las que
                // este vertice forma parte, independientemente de la direccion de la arista
                incoming = outgoing;
        }

        /**
         * Returns the element associated with the vertex.
         */
        public U getElement() {
            return element;
        }

        /**
         * Returns reference to the underlying map of outgoing edges.
         */
        public Map<Vertex<U>, Edge<E>> getOutgoing() {
            return outgoing;
        }

        /**
         * Returns reference to the underlying map of incoming edges.
         */
        public Map<Vertex<U>, Edge<E>> getIncoming() {
            return incoming;
        }

        /**
         * Validates that this vertex instance belongs to the given graph.
         */
        public boolean validate(AdjacencyMapGraph<U, E> graph) {
            return (AdjacencyMapGraph.this == graph && graph.vertices().contains(this));
        }

        public String toString() {
            return "Vertex: " + element;
        }

        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            final InnerVertex<U> other = (InnerVertex<U>) obj;
            return this.element.equals(other.element);
        }

        public int hashCode() {
            return element.hashCode();
        }
    } //------------ end of InnerVertex class ------------

    //---------------- nested InnerEdge class ----------------

    /**
     * An edge between two vertices.
     */
    private class InnerEdge<R> implements Edge<R> {
        private final R element;
        private final Vertex<V>[] endpoints;

        /**
         * Constructs InnerEdge instance from u to v, storing the given element.
         */
        public InnerEdge(Vertex<V> u, Vertex<V> v, R elem) {
            element = elem;
            endpoints = (Vertex<V>[]) new Vertex[]{u, v};  // array of length 2
        }

        /**
         * Returns the element associated with the edge.
         */
        public R getElement() {
            return element;
        }

        /**
         * Returns reference to the endpoint array.
         */
        public Pair<Vertex<V>, Vertex<V>> getEndpoints() {
            return new Pair<>(endpoints[0], endpoints[1]);
        }

        /**
         * Validates that this edge instance belongs to the given graph.
         */
        public boolean validate(UndirectedGraph<V, R> graph) {
            return AdjacencyMapGraph.this == graph && graph.edges().contains(this);
        }

        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            final InnerEdge<R> other = (InnerEdge<R>) obj;
            if (isDirected) {
                // Order matters
                return this.endpoints[0].equals(other.endpoints[0]) && this.endpoints[1].equals(other.endpoints[1]);
            } else {
                // Order does not matter
                return (this.endpoints[0].equals(other.endpoints[0]) && this.endpoints[1].equals(other.endpoints[1])) || (this.endpoints[0].equals(other.endpoints[1]) && this.endpoints[1].equals(other.endpoints[0]));
            }
        }

        public int hashCode() {
            if (isDirected) {
                // Order matters
                return Arrays.hashCode(endpoints);
            } else {
                // Order does not matter
                return endpoints[0].hashCode() + endpoints[1].hashCode();
            }
        }

        public String toString() {
            return "Edge: " + element + " between (" + endpoints[0] + ") and (" + endpoints[1] + ")";
        }
    } //------------ end of InnerEdge class ------------

    private InnerVertex<V> validate(Vertex<V> v) {
        if (!(v instanceof AdjacencyMapGraph.InnerVertex vertex)) throw new IllegalArgumentException("Invalid vertex");
        if (!vertex.validate(this)) throw new IllegalArgumentException("Invalid vertex");
        return vertex;
    }

    private InnerEdge<E> validate(Edge<E> e) {
        if (!(e instanceof AdjacencyMapGraph.InnerEdge edge)) throw new IllegalArgumentException("Invalid edge");
        if (!edge.validate(this)) throw new IllegalArgumentException("Invalid edge");
        return edge;
    }

    /**
     * Devuelve el vértice inicial (start) de la arista. En grafos no dirigidos carece de sentido.
     *
     * @param edge arista a inspeccionar
     * @return el vértice inicial de la arista
     * @throws IllegalArgumentException si edge no pertenece al grafo
     */
    public Vertex<V> startVertex(Edge<E> edge) {
        InnerEdge<E> innerEdge = validate(edge);
        return innerEdge.getEndpoints().getFirst();
    }

    public Vertex<V> getVertex(V value) {
        // Dado que hashCode de Vertex solo tiene en cuenta el elemento almacenado, al igual que equals(),
        // podemos buscar directamente el valor, que va a tener el mismo hashCode que el Vertex correspondiente
        // y, además, equals() devolverá true.
        return this.vertices.get(value);
    }

    /**
     * Devuelve la arista que conecta value1 y value2 según la semántica del grafo
     * (value1->value2 en dirigido; indistinto en no dirigido). Si no existe, devuelve null.
     *
     * @param value1 vértice origen
     * @param value2 vértice destino
     * @return la arista que conecta value1 y value2, o null si no existe
     * @throws IllegalArgumentException si alguno de los vértices no pertenece al grafo
     */
    public Edge<E> getEdge(V value1, V value2) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Devuelve un par con la arista desde v1 hasta v2 (primera componente) y la
     * arista desde v2 hasta v1 (segunda componente). Sólo tiene sentido en grafos
     * dirigidos; en grafos no dirigidos esta operación lanza UnsupportedOperationException.
     *
     * @param v1 vértice 1
     * @param v2 vértice 2
     * @return Pair con las aristas forward y backward (cualquiera puede ser null)
     * @throws UnsupportedOperationException si el grafo no es dirigido
     * @throws IllegalArgumentException si alguno de los vértices no pertenece al grafo
     */
    @Override
    public Pair<Edge<E>, Edge<E>> getEdges(Vertex<V> v1, Vertex<V> v2) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Devuelve las aristas que salen (output) del vértice dado. En grafos no dirigidos
     * se incluyen también las aristas en las que el vértice aparece como segundo extremo.
     *
     * @param v vértice origen
     * @return colección inmutable de aristas de salida desde v
     * @throws IllegalArgumentException si v no pertenece al grafo
     */
    @Override
    public Collection<Edge<E>> outputEdges(Vertex<V> v) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Devuelve una colección con todos los vértices del grafo.
     * La colección devuelta es de sólo lectura (vista inmutable).
     *
     * @return colección de vértices
     */
    @Override
    public Collection<Vertex<V>> vertices() {
        return this.vertices.keySet();
    }

    /**
     * Devuelve una colección con todas las aristas del grafo.
     * La colección devuelta es de sólo lectura (vista inmutable).
     *
     * @return colección de aristas
     */
    @Override
    public Collection<Edge<E>> edges() {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Devuelve las aristas incidentes en el vértice v. En grafos dirigidos se
     * consideran las aristas cuyo destino (end) es v (es decir, aristas con v como extremo
     * final). En grafos no dirigidos se devuelven todas las aristas que tengan a v como
     * cualquiera de sus extremos.
     *
     * @param v vértice del que solicitar las aristas incidentes
     * @return colección inmutable de aristas incidentes en v
     * @throws IllegalArgumentException si v no pertenece al grafo
     */
    @Override
    public Collection<Edge<E>> incidentEdges(Vertex<V> v) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Dado un vértice v y una arista e de la que v forma parte, devuelve el vértice opuesto
     * (el otro extremo de e). Lanza IllegalArgumentException si v no es extremo de e.
     *
     * @param v vértice conocido
     * @param e arista incidente a v
     * @return el vértice opuesto a v en la arista e
     * @throws IllegalArgumentException si e no pertenece al grafo o v no es extremo de e
     */
    @Override
    public Vertex<V> opposite(Vertex<V> v, Edge<E> e) throws IllegalArgumentException {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Devuelve el vértice final (end) de la arista. En grafos no dirigidos esto carece de sentido.
     *
     * @param e arista a inspeccionar
     * @return el vértice final de la arista
     * @throws IllegalArgumentException si e no pertenece al grafo
     */
    @Override
    public Vertex<V> endVertex(Edge<E> e) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Devuelve los dos extremos de la arista como un Pair. En grafos dirigidos el
     * primer elemento del par es el origen y el segundo el destino. En grafos no dirigidos, el orden no importa.
     *
     * @param edge arista a inspeccionar
     * @return un Pair con los vértices extremos de la arista
     * @throws IllegalArgumentException si edge no pertenece al grafo
     */
    @Override
    public Pair<Vertex<V>, Vertex<V>> endVertices(Edge<E> edge) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Comprueba si existe una arista que conecte v1 con v2. En grafos dirigidos se
     * comprueba la arista v1->v2; en grafos no dirigidos se comprueba la existencia de
     * una arista que tenga a v1 y v2 como extremos en cualquier orden.
     *
     * @param v1 vértice origen
     * @param v2 vértice destino
     * @return true si los vértices son adyacentes según la semántica del grafo
     * @throws IllegalArgumentException si alguno de los vértices no pertenece al grafo
     */
    @Override
    public boolean areAdjacent(Vertex<V> v1, Vertex<V> v2) {
        InnerVertex<V> vertex1 = validate(v1);
        InnerVertex<V> vertex2 = validate(v2);
        return vertex1.getOutgoing().containsKey(vertex2) ;
    }

    /**
     * Devuelve la arista que conecta v1 y v2 según la semántica del grafo
     * (v1->v2 en dirigido; indistinto en no dirigido). Si no existe, devuelve null.
     *
     * @param v1 vértice origen
     * @param v2 vértice destino
     * @return la arista que conecta v1 y v2, o null si no existe
     * @throws IllegalArgumentException si alguno de los vértices no pertenece al grafo
     */
    @Override
    public Edge<E> getEdge(Vertex<V> v1, Vertex<V> v2) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Inserta un vértice con el valor dado si no existe ya otro vértice con el mismo
     * elemento. Si ya existe, devuelve la instancia previa.
     *
     * @param value valor a almacenar en el vértice
     * @return el vértice existente o el nuevo vértice insertado
     */
    @Override
    public Vertex<V> insertVertex(V value) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Inserta una arista entre v1 y v2 con el valor proporcionado. Si ya existe una
     * arista entre esos vértices, en este diseño se reemplaza (se elimina la anterior)
     * y se inserta la nueva con el nuevo valor.
     *
     * @param v1        vértice origen
     * @param v2        vértice destino
     * @param edgeValue valor a almacenar en la arista
     * @return la arista insertada (nueva) o la existente si no se reemplaza
     * @throws IllegalArgumentException si alguno de los vértices no pertenece al grafo
     */
    @Override
    public Edge<E> insertEdge(Vertex<V> v1, Vertex<V> v2, E edgeValue) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Elimina el vértice indicado y todas las aristas de las que forma parte.
     *
     * @param vertex vértice a eliminar
     * @return el elemento almacenado en el vértice eliminado
     * @throws IllegalArgumentException si vertex no pertenece al grafo
     */
    @Override
    public V removeVertex(Vertex<V> vertex) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Elimina la arista indicada del grafo.
     *
     * @param edge arista a eliminar
     * @return el elemento almacenado en la arista eliminada
     * @throws IllegalArgumentException si edge no pertenece al grafo
     */
    @Override
    public E removeEdge(Edge<E> edge) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }


    /**
     * Devuelve los vértices adyacentes al vértice proporcionado (todos los que comparten
     * una arista con él).
     *
     * @param vertex vértice de referencia
     * @return colección inmutable de vértices adyacentes
     * @throws IllegalArgumentException si vertex no pertenece al grafo
     */
    public Collection<Vertex<V>> adjacentVertices(Vertex<V> vertex) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Devuelve los vértices alcanzables desde el vértice dado siguiendo una sola arista.
     * En grafos dirigidos devuelve los destinos de las aristas salientes; en no dirigidos
     * coincide con adjacentVertices.
     *
     * @param vertex vértice de partida
     * @return colección inmutable de vértices alcanzables con un paso
     * @throws IllegalArgumentException si vertex no pertenece al grafo
     */
    public Collection<Vertex<V>> reachableVertices(Vertex<V> vertex) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
