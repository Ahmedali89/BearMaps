import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;


/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    private TreeMap<Long, Vertex> intersectionDB = new TreeMap<>();
    public TreeSet<String> tries =new TreeSet<>();
    public Map<String,String> mappingAddresses = new LinkedHashMap<>();
    public Map<String,ArrayList<Vertex>> locations = new HashMap<>();
    public HashSet<String> deleted = new HashSet<>();

    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputFile, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */

    private void clean() {

        Iterable<Vertex> V = intersectionDB.values();
        TreeMap<Long, Vertex> newDB = new TreeMap<>();
        for (Vertex v : V) {
            if ((v.adjs).size() > 0) {
                 newDB.put(v.getId(),v);
            }
        }
        intersectionDB = newDB;
    }

    TreeMap<Long, Vertex> getIntersectionDB() {
        return intersectionDB;
    }

    /** Returns an iterable of all vertex IDs in the graph. */
    Iterable<Long> vertices() {
        //YOUR CODE HERE, this currently returns only an empty list.
        return intersectionDB.keySet();
    }

    void addVertex(String id, String lon, String lat) {
        Vertex v = new Vertex(id, lon, lat);
        intersectionDB.put(Long.parseLong(id), v);
    }

    Map.Entry<Long, Vertex> getLastEntry() {
        return intersectionDB.lastEntry();
    }

    public void connectVerteices(ArrayList<String> l, String name, String speed) {
        int i = 0;
        int j = 1;
        while (j < l.size()) {
            Vertex n1 = intersectionDB.get(Long.parseLong(l.get(i)));
            Vertex n2 = intersectionDB.get(Long.parseLong(l.get(j)));
            Edge road = new Edge(n1, n2);
            road.setName(name);
            if (speed.length() > 0) {
                road.setSpeed(speed);
            }
            road.setWeight(distance(n1.getId(), n2.getId()));
            n1.adjs.add(road);
            n2.adjs.add(road);
            i = j;
            j++;
        }
    }

    /** Returns ids of all vertices adjacent to v. */
    Iterable<Long> adjacent(long v) {

        ArrayList<Long> adjacent = new ArrayList<>();
        Vertex node = intersectionDB.get(v);
        for (Edge x : node.adjs) {
            adjacent.add(x.getotherEnd(node).getId());
        }
        return adjacent;
    }

    /** Returns the Euclidean distance between vertices v and w, where Euclidean distance
     *  is defined as sqrt( (lonV - lonV)^2 + (latV - latV)^2 ). */
    double distance(long v, long w) {
        Vertex v1 = intersectionDB.get(v);
        Vertex v2 = intersectionDB.get(w);
        double dis = Math.sqrt(Math.pow((v2.getLON() - v1.getLON()), 2)
                + Math.pow((v2.getLat() - v1.getLat()), 2));
        return dis;
    }

    double distance(Vertex v1, Vertex v2) {

        double dis = Math.sqrt(Math.pow((v2.getLON() - v1.getLON()), 2)
                + Math.pow((v2.getLat() - v1.getLat()), 2));
        return dis;
    }

    /** Returns the vertex id closest to the given longitude and latitude. */
    long closest(double lon, double lat) {
        Vertex temp = new Vertex("0", lon, lat);
        double min = 0.0;
        Object[] carrier = {0, null};
        for (long v : intersectionDB.keySet()) {
            Vertex ver = intersectionDB.get(v);
            double dis = distance(ver, temp);
            if (min == 0.0) {
                min = dis;
                carrier[0] = dis;
                carrier[1] = ver;
            } else {
                if (min > dis) {
                    carrier[0] = dis;
                    carrier[1] = ver;
                    min = dis;
                }
            }
        }
        return ((Vertex) carrier[1]).getId();
    }

    /** Longitude of vertex v. */
    double lon(long v) {
        return intersectionDB.get(v).getLON();
    }

    /** Latitude of vertex v. */
    double lat(long v) {
        return intersectionDB.get(v).getLat();
    }

    public static class Edge {
        private double weight;
        private String speed;
        private String name;
        private Vertex from;
        private Vertex to;

        public Edge(Vertex node1, Vertex node2) {
            from = node1;
            to = node2;
            weight = 0.0;
            name = "";
            speed = "";
        }

        public Vertex getotherEnd(Vertex v) {
            if (v.equals(from)) {
                return to;
            } else {
                return from;
            }
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double w) {
            weight = w;
        }

        public void setSpeed(String s) {
            speed = s;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public static class Vertex implements Comparable<Vertex> {

        @Override
        public int compareTo(Vertex other) {
            if (this.priority == other.priority) {
                return 0;
            } else if (this.priority > other.priority) {
                return 1;
            } else {
                return -1;
            }
        }
        String keyname;
        private double priority;
        private Set<Edge> adjs;
        private double lon;
        private double lat;
        private long id;
        private LinkedHashMap<String,Object> tag;

        public Vertex(String id, double lon, double lat) {
            this.lon = lon;
            this.lat = lat;
            this.id = 0;
            tag = new LinkedHashMap<>();
            adjs = new HashSet<>();
            priority = Double.POSITIVE_INFINITY;
            keyname = "";
        }

        public Vertex(String id, String lon, String lat) {

            this.lon = Double.parseDouble(lon);
            this.lat = Double.parseDouble(lat);
            this.id = Long.parseLong(id);
            tag = new LinkedHashMap<>();
            adjs = new HashSet<>();
            priority = Double.POSITIVE_INFINITY;
            keyname = "";
        }
        public void setPriority(double p) {
            priority = p;
        }

        public double getLON() {
            return lon;
        }

        public double getLat() {
            return lat;
        }

        public long getId() {
            return id;
        }

        public Map<String, Object> getTags() {
            return tag;
        }

        public Set<Edge> getAdjs() {
            return adjs;
        }

        public void setId(String id) {
            this.id = Long.parseLong(id);
        }

        public void setLon(String lon) {
            this.lon = Double.parseDouble(lon);
        }

        public void setLat(String lat) {
            this.lat = Double.parseDouble(lat);
        }

        public void setTag(LinkedHashMap<String, Object> name) {
            tag = name;
        }

        public void keyName(String n) {
            keyname = n;
        }

        public String getKeyname() {
            return keyname;
        }

    }

    public static void main(String[] args) {
        TreeSet<String> t = new TreeSet<>();
        Map<String,String> m = new HashMap<>();
        t.add("monadjashdkjhads");
        t.add("dajahdsjfhasjhuwe");
        t.add("mondafdjfhasdjkfha");
        t.add("mondsasdjhfuihewifhewihw");
        t.add("monfhquiefheuihfeuiwbaxsvbiewbasniewab");
        t.add("yndajndjasbviadnkwn");
        t.add("rdsnajdnsaksnksandkfnas");
        t.add("ndaadsfashfhwiwejfka");
        t.add("wdakdnajnkadsnn");
        t.add("sdsanviuanknkdsan");
        t.add("osdakjdahakjhaksja");
        System.out.println(t.subSet("mon",true,"monz",true));
    }
}
