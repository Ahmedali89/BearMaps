import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ahmed on 4/12/17.
 */

public class TestQuadTree {
    @Test
    public void testConstructor() {
        QuadTree t = new QuadTree(MapServer.ROOT_ULLAT,MapServer.ROOT_LRLAT,MapServer.ROOT_ULLON,MapServer.ROOT_LRLON);
        System.out.println(t.getSize());
        System.out.println(t.getRoot().getLONDPP());
        Map<String,Double> params = new HashMap<>();
        params.put("ullat", 37.870213571328854);
        params.put("lrlat",37.8318576119893);
        params.put("ullon",-122.30410170759153);
        params.put("lrlon",-122.2104604264636);
        params.put("w",1085.0);
        params.put("h",566.0);
        Node par = new Node("query",37.870213571328854,37.8318576119893,-122.30410170759153,-122.2104604264636,0);
//        t.print();
//        for(Node x : t.getRoot().getChildern()) {
//            System.out.println(x.getName() + " " + x.toString());
//        }
        t.getImages(params);
    }

    @Test
    public void testOverLapping() {
        QuadTree t = new QuadTree(MapServer.ROOT_ULLAT,MapServer.ROOT_LRLAT,MapServer.ROOT_ULLON,MapServer.ROOT_LRLON);
        assertEquals(t.checkIntersection(t.getRoot(),t.getRoot().getFirstChild()),true);
        assertEquals(t.checkIntersection(t.getRoot().getFourthChild(),t.getRoot().getFirstChild()),false);
        assertEquals(t.checkIntersection(t.getRoot().getFirstChild(),t.getRoot().getFirstChild().getFirstChild()),true);
        assertEquals(t.checkIntersection(t.getRoot().getFirstChild().getFourthChild(),t.getRoot().getFirstChild().getFirstChild()),false);
        assertEquals(t.checkIntersection(t.getRoot().getFirstChild().getFirstChild().getFirstChild(),t.getRoot().getFirstChild().getFirstChild()),true);
    }

}
