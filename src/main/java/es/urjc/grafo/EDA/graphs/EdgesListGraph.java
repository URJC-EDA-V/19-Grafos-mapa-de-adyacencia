package es.urjc.grafo.EDA.graphs;

import es.urjc.grafo.EDA.utils.Pair;

import java.util.*;

/**
 * Implementación de un grafo utilizando lista de aristas.
 *
 * @param <V> tipo de los vértices
 * @param <E> tipo de las aristas
 */
public class EdgesListGraph<V, E> implements DirectedGraph<V, E>, UndirectedGraph<V, E> {

    private final boolean isDirected;
    private final List<InnerVertex<V>> vertices;
    private final List<InnerEdge<E>> edges;

    /**
     * Crea una instancia de grafo basada en listas de aristas.
     *
     * @param directed true si el grafo debe comportarse como dirigido, false si no dirigido
     */
    public EdgesListGraph(boolean directed) {
        isDirected = directed;
        this.vertices = new LinkedList<>();
        this.edges = new LinkedList<>();
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
    public static EdgesListGraph<String, Integer> graphFromEdgelist(String[][] receivedEdges, boolean directed) {
        EdgesListGraph<String, Integer> g = new EdgesListGraph<>(directed);

        // primera pasada para obtener el conjunto de etiquetas de vértice en orden
        TreeSet<String> labels = new TreeSet<>();
        for (String[] receivedEdge : receivedEdges) {
            labels.add(receivedEdge[0]);
            labels.add(receivedEdge[1]);
        }

        // ahora crear vértices (en orden alfabético)
        HashMap<String, Vertex<String>> verts = new HashMap<>();
        for (String label : labels)
            verts.put(label, g.insertVertex(label));

        // ahora añadir aristas al grafo
        for (String[] receivedEdge : receivedEdges) {
            Integer cost = (receivedEdge.length == 2 ? 1 : Integer.parseInt(receivedEdge[2]));
            g.insertEdge(verts.get(receivedEdge[0]), verts.get(receivedEdge[1]), cost);
        }
        return g;
    }

    //---------------- nested Vertex class ----------------
    private class InnerVertex<U> implements Vertex<U> {
        private final U element;

        /**
         * Construye un vértice interno que almacena el elemento proporcionado.
         *
         * @param elem elemento a almacenar en el vértice
         */
        public InnerVertex(U elem) {
            element = elem;
        }

        /**
         * Devuelve el elemento almacenado en el vértice.
         *
         * @return el elemento asociado a este vértice
         */
        public U getElement() {
            return element;
        }

        /**
         * Comprueba si este vértice pertenece al grafo indicado.
         *
         * @param graph el grafo contra el que validar la pertenencia
         * @return true si el vértice pertenece a graph, false en caso contrario
         */
        public boolean validate(EdgesListGraph<U, E> graph) {
            return (EdgesListGraph.this == graph && graph.vertices().contains(this));
        }

        /**
         * Igualdad basada en el elemento almacenado (la etiqueta del vértice).
         *
         * @param obj otro objeto
         * @return true si obj es un InnerVertex con igual elemento
         */
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            final InnerVertex other = (InnerVertex) obj;
            return this.element.equals(other.element);
        }

        /**
         * HashCode consistente con equals, basado en el elemento.
         *
         * @return código hash del elemento
         */
        public int hashCode() {
            return element.hashCode();
        }

        /**
         * Representación en String del vértice.
         *
         * @return cadena descriptiva del vértice
         */
        public String toString() {
            return "Vertex: " + element;
        }

    } //------------ end of InnerVertex class ------------

    //---------------- nested InnerEdge class ----------------

    /**
     * Representa una arista entre dos vértices. Puede ser dirigida o no según el grafo.
     */
    private class InnerEdge<R> implements Edge<R> {
        private final R element;
        private final Vertex<V>[] endpoints;

        /**
         * Crea una arista que une los vértices u (origen) y v (destino) y almacena el elemento dado.
         *
         * @param u     vértice origen
         * @param v     vértice destino
         * @param elem  elemento asociado a la arista
         */
        public InnerEdge(Vertex<V> u, Vertex<V> v, R elem) {
            element = elem;
            endpoints = (Vertex<V>[]) new Vertex[]{u, v};  // array of length 2
        }

        /**
         * Devuelve el elemento almacenado en la arista.
         *
         * @return el elemento asociado a esta arista
         */
        public R getElement() {
            return element;
        }

        /**
         * Devuelve los dos extremos de la arista como un Pair. En grafos dirigidos
         * el primer elemento es el origen y el segundo el destino.
         *
         * @return un Pair con los vértices extremos (origen, destino)
         */
        public Pair<Vertex<V>, Vertex<V>> getEndpoints() {
            return new Pair<>(endpoints[0], endpoints[1]);
        }

        /**
         * Comprueba si esta arista pertenece al grafo indicado.
         *
         * @param graph el grafo contra el que validar la pertenencia
         * @return true si la arista pertenece a graph, false en caso contrario
         */
        public boolean validate(EdgesListGraph<V, R> graph) {
            return EdgesListGraph.this == graph && graph.edges().contains(this);
        }

        /**
         * Igualdad de aristas: en grafos dirigidos el orden de extremos importa; en
         * grafos no dirigidos el par de extremos se considera sin orden.
         *
         * @param obj otro objeto
         * @return true si representan la misma arista según la semántica del grafo
         */
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            final InnerEdge other = (InnerEdge) obj;
            if (isDirected) {
                // El orden importa si el grafo es dirigido
                return this.endpoints[0].equals(other.endpoints[0]) && this.endpoints[1].equals(other.endpoints[1]);
            } else {
                // El orden no importa si el grafo no es dirigido
                return (this.endpoints[0].equals(other.endpoints[0]) && this.endpoints[1].equals(other.endpoints[1]))
                        || (this.endpoints[0].equals(other.endpoints[1]) && this.endpoints[1].equals(other.endpoints[0]));
            }
        }

        /**
         * Código hash consistente con equals. En no dirigido se suma el hash de los extremos
         * para no depender del orden.
         *
         * @return código hash de la arista
         */
        public int hashCode() {
            if (isDirected) {
                // El orden importa si el grafo es dirigido
                return Arrays.hashCode(endpoints);
            } else {
                // El orden no importa si el grafo no es dirigido
                return endpoints[0].hashCode() + endpoints[1].hashCode();
            }
        }

        /**
         * Representación en String de la arista.
         *
         * @return cadena descriptiva de la arista y sus extremos
         */
        public String toString() {
            return "Edge: " + element + " between (" + endpoints[0] + ") and (" + endpoints[1] + ")";
        }
    } //------------ end of InnerEdge class ------------

    /**
     * Valida que el objeto Vertex dado es una instancia válida de InnerVertex perteneciente a este grafo.
     *
     * @param vertex vértice a validar
     * @return la referencia a InnerVertex si es válida
     * @throws IllegalArgumentException si vertex no pertenece a este grafo o no es del tipo interno correcto
     */
    private InnerVertex<V> validate(Vertex<V> vertex) {
        if (!(vertex instanceof InnerVertex)) {
            throw new IllegalArgumentException("Invalid vertex");
        }
        InnerVertex<V> casted = (InnerVertex<V>) vertex;
        if (!vertices.contains(casted)) {
            throw new IllegalArgumentException("Invalid vertex");
        }
        return casted;
    }

    /**
     * Valida que el objeto Edge dado es una instancia válida de InnerEdge perteneciente a este grafo.
     *
     * @param edge arista a validar
     * @return la referencia a InnerEdge si es válida
     * @throws IllegalArgumentException si edge no pertenece a este grafo o no es del tipo interno correcto
     */
    private InnerEdge<E> validate(Edge<E> edge) {
        if (!(edge instanceof InnerEdge)) {
            throw new IllegalArgumentException("Invalid edge");
        }
        InnerEdge<E> casted = (InnerEdge<E>) edge;
        if (!edges.contains(casted)) {
            throw new IllegalArgumentException("Invalid edge");
        }
        return casted;
    }

    /**
     * Busca una arista que conecte v1 con v2. En grafos dirigidos se busca la arista (v1->v2).
     * En grafos no dirigidos se considera indistinto el orden de los extremos.
     *
     * @param v1 vértice origen
     * @param v2 vértice destino
     * @return la arista encontrada o null si no existe
     */
    private InnerEdge<E> findEdge(Vertex<V> v1, Vertex<V> v2) {
        for (InnerEdge<E> edge : edges) {
            Pair<Vertex<V>, Vertex<V>> endpoints = edge.getEndpoints();
            if (isDirected) {
                if (endpoints.getFirst().equals(v1) && endpoints.getSecond().equals(v2)) {
                    return edge;
                }
            } else {
                if ((endpoints.getFirst().equals(v1) && endpoints.getSecond().equals(v2))
                        || (endpoints.getFirst().equals(v2) && endpoints.getSecond().equals(v1))) {
                    return edge;
                }
            }
        }
        return null;
    }

    /**
     * Devuelve una colección con todos los vértices del grafo.
     * La colección devuelta es de sólo lectura (vista inmutable).
     *
     * @return colección de vértices
     */
    @Override
    public Collection<Vertex<V>> vertices() {
        return Collections.unmodifiableList(vertices);
    }

    /**
     * Devuelve una colección con todas las aristas del grafo.
     * La colección devuelta es de sólo lectura (vista inmutable).
     *
     * @return colección de aristas
     */
    @Override
    public Collection<Edge<E>> edges() {
        return Collections.unmodifiableList(edges);
    }

    /**
     * Devuelve las aristas incidentes en el vértice v. En grafos dirigidos se
     * consideran las aristas cuya destino (end) es v (es decir, aristas con v como extremo
     * final). En grafos no dirigidos se devuelven todas las aristas que tengan a v como
     * cualquiera de sus extremos.
     *
     * @param v vértice del que solicitar las aristas incidentes
     * @return colección inmutable de aristas incidentes en v
     * @throws IllegalArgumentException si v no pertenece al grafo
     */
    @Override
    public Collection<Edge<E>> incidentEdges(Vertex<V> v) {
        InnerVertex<V> vertex = validate(v);
        List<Edge<E>> incident = new LinkedList<>();
        for (InnerEdge<E> edge : edges) {
            Pair<Vertex<V>, Vertex<V>> endpoints = edge.getEndpoints();
            if (isDirected) {
                if (endpoints.getSecond().equals(vertex)) {
                    incident.add(edge);
                }
            } else if (endpoints.getFirst().equals(vertex) || endpoints.getSecond().equals(vertex)) {
                incident.add(edge);
            }
        }
        return Collections.unmodifiableList(incident);
    }

    /**
     * Dado un vértice v y una arista e incidente a v, devuelve el vértice opuesto
     * (el otro extremo de e). Lanza IllegalArgumentException si v no es extremo de e.
     *
     * @param v vértice conocido
     * @param e arista incidente a v
     * @return el vértice opuesto a v en la arista e
     * @throws IllegalArgumentException si e no pertenece al grafo o v no es extremo de e
     */
    @Override
    public Vertex<V> opposite(Vertex<V> v, Edge<E> e) {
        InnerVertex<V> vertex = validate(v);
        InnerEdge<E> edge = validate(e);
        Pair<Vertex<V>, Vertex<V>> endpoints = edge.getEndpoints();
        if (endpoints.getFirst().equals(vertex)) {
            return endpoints.getSecond();
        } else if (endpoints.getSecond().equals(vertex)) {
            return endpoints.getFirst();
        }
        throw new IllegalArgumentException("Vertex is not incident to edge");
    }

    /**
     * Devuelve el vértice final (end) de la arista. En grafos no dirigidos esto carece de sentido.
     *
     * @param edge arista a inspeccionar
     * @return el vértice final de la arista
     * @throws IllegalArgumentException si edge no pertenece al grafo
     */
    @Override
    public Vertex<V> endVertex(Edge<E> edge) {
        if (!isDirected) {
            throw new UnsupportedOperationException("This operation is only supported for directed graphs");
        }
        return validate(edge).getEndpoints().getSecond();
    }

    /**
     * Devuelve los dos extremos de la arista como un Pair. En grafos dirigidos el
     * primer elemento del par es el origen y el segundo el destino.
     *
     * @param edge arista a inspeccionar
     * @return un Pair con los vértices extremos de la arista
     * @throws IllegalArgumentException si edge no pertenece al grafo
     */
    @Override
    public Pair<Vertex<V>, Vertex<V>> endVertices(Edge<E> edge) {
        return validate(edge).getEndpoints();
    }

    /**
     * Devuelve el vértice inicial (start) de la arista. En grafos no dirigidos carece de sentido.
     *
     * @param edge arista a inspeccionar
     * @return el vértice inicial de la arista
     * @throws IllegalArgumentException si edge no pertenece al grafo
     */
    @Override
    public Vertex<V> startVertex(Edge<E> edge) {
        if (!isDirected) {
            throw new UnsupportedOperationException("This operation is only supported for directed graphs");
        }
        return validate(edge).getEndpoints().getFirst();
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
        validate(v1);
        validate(v2);
        return findEdge(v1, v2) != null;
    }

    /**
     * Devuelve la arista que conecta v1 y v2 según la semántica del grafo
     * (v1->v2 en dirigido; indistinto en no dirigido). Si no existe devuelve null.
     *
     * @param v1 vértice origen
     * @param v2 vértice destino
     * @return la arista que conecta v1 y v2, o null si no existe
     * @throws IllegalArgumentException si alguno de los vértices no pertenece al grafo
     */
    @Override
    public Edge<E> getEdge(Vertex<V> v1, Vertex<V> v2) {
        validate(v1);
        validate(v2);
        return findEdge(v1, v2);
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
        if (!isDirected) {
            throw new UnsupportedOperationException("This operation is only supported for directed graphs");
        }
        validate(v1);
        validate(v2);
        Edge<E> forward = findEdge(v1, v2);
        Edge<E> backward = findEdge(v2, v1);
        return new Pair<>(forward, backward);
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
        for (InnerVertex<V> vertex : vertices) {
            if (vertex.getElement().equals(value)) {
                return vertex;
            }
        }
        InnerVertex<V> newVertex = new InnerVertex<>(value);
        vertices.add(newVertex);
        return newVertex;
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
        InnerVertex<V> vertex1 = validate(v1);
        InnerVertex<V> vertex2 = validate(v2);
        Edge<E> existing = findEdge(vertex1, vertex2);
        if (existing != null) {
            this.removeEdge(existing);
        }
        InnerEdge<E> newEdge = new InnerEdge<>(vertex1, vertex2, edgeValue);
        edges.add(newEdge);
        return newEdge;
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
        InnerVertex<V> vertex = validate(v);
        List<Edge<E>> output = new LinkedList<>();
        for (InnerEdge<E> edge : edges) {
            Pair<Vertex<V>, Vertex<V>> endpoints = edge.getEndpoints();
            if (endpoints.getFirst().equals(vertex)) {
                output.add(edge);
            } else if (!isDirected && endpoints.getSecond().equals(vertex)) {
                output.add(edge);
            }
        }
        return Collections.unmodifiableList(output);
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
        InnerVertex<V> v = validate(vertex);
        Iterator<InnerEdge<E>> iterator = edges.iterator();
        while (iterator.hasNext()) {
            InnerEdge<E> edge = iterator.next();
            Pair<Vertex<V>, Vertex<V>> endpoints = edge.getEndpoints();
            if (endpoints.getFirst().equals(v) || endpoints.getSecond().equals(v)) {
                iterator.remove();
            }
        }
        vertices.remove(v);
        return v.getElement();
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
        InnerEdge<E> e = validate(edge);
        edges.remove(e);
        return e.getElement();
    }

    /**
     * Devuelve los vértices adyacentes al vértice proporcionado (todos los que comparten
     * una arista con él). En grafos dirigidos se consideran los vértices destino de las aristas.
     *
     * @param vertex vértice de referencia
     * @return colección inmutable de vértices adyacentes
     * @throws IllegalArgumentException si vertex no pertenece al grafo
     */
    @Override
    public Collection<Vertex<V>> adjacentVertices(Vertex<V> vertex) {
        InnerVertex<V> v = validate(vertex);
        Set<Vertex<V>> adjacent = new LinkedHashSet<>();
        for (InnerEdge<E> edge : edges) {
            Pair<Vertex<V>, Vertex<V>> endpoints = edge.getEndpoints();
            if (endpoints.getFirst().equals(v)) {
                adjacent.add(endpoints.getSecond());
            }
            if (!this.isDirected && endpoints.getSecond().equals(v)) {
                adjacent.add(endpoints.getFirst());
            }
        }
        return Collections.unmodifiableList(new ArrayList<>(adjacent));
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
    @Override
    public Collection<Vertex<V>> reachableVertices(Vertex<V> vertex) {
        InnerVertex<V> v = validate(vertex);
        List<Vertex<V>> reachable = new LinkedList<>();
        for (InnerEdge<E> edge : edges) {
            Pair<Vertex<V>, Vertex<V>> endpoints = edge.getEndpoints();
            if (endpoints.getFirst().equals(v)) {
                reachable.add(endpoints.getSecond());
            } else if (!isDirected && endpoints.getSecond().equals(v)) {
                reachable.add(endpoints.getFirst());
            }
        }
        return Collections.unmodifiableList(reachable);
    }
}
