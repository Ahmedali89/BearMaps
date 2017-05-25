import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class AGRouterTest extends AGMapTest {
    /**
     * Test the route-finding functionality by comparing the node id list item by item.
     * @throws Exception
     */
    /*end_lat=37.83796678748061, start_lon=-122.26143030962002, start_lat=37.831051606162895, end_lon=-122.2650498760774*/
    @Test
    public void testShortestPath() throws Exception {
        for (TestParameters p : params) {
            LinkedList<Long> studentRouteResult = Router.shortestPath(graph,
                    -122.26143030962002, 37.831051606162895,
                    -122.2650498760774, 37.83796678748061);

            assertEquals("Found route differs for input: " + p.routeParams + ".\n",
                    p.routeResult, studentRouteResult);
        }
    }
}
