
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */

public class Router {
    /**
     * Return a LinkedList of <code>Long</code>s
     * representing the shortest path from st to dest,
     * where the longs are node IDs.
     */
    public static LinkedList<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                                double destlon, double destlat) {
        long source = g.closest(stlon, stlat);
        long distination = g.closest(destlon, destlat);

        GraphDB.Vertex start = g.getIntersectionDB().get(source);
        GraphDB.Vertex target = g.getIntersectionDB().get(distination);

        Map<Long, Double> distanceSoFar = new HashMap<>();
        Map<GraphDB.Vertex, GraphDB.Vertex> pathToGoal = new HashMap<>();

        LinkedList<Long> route = new LinkedList<>();
        PriorityQueue<GraphDB.Vertex> pq = new PriorityQueue<>(GraphDB.Vertex:: compareTo);
        Set<GraphDB.Edge> visited = new HashSet<>();
        double dis = 0.0;
        double estimatedDisToTarget = 0.0;
        double priority = 0.0;
        start.setPriority(priority);
        pq.add(start);
        GraphDB.Vertex neighbor;

        distanceSoFar.put(start.getId(), 0.0);
        pathToGoal.put(start, null);

        while (pq.size() > 0) {

            GraphDB.Vertex src = pq.remove();

            if (src.equals(target)) {
                GraphDB.Vertex goal = src;
                while (pathToGoal.get(goal) != null) {
                    route.addFirst(goal.getId());
                    goal = pathToGoal.get(goal);
                }
                route.addFirst(goal.getId());
                break;
            }
            for (GraphDB.Edge e : src.getAdjs()) {
                neighbor = e.getotherEnd(src);
                if (!visited.contains(e)) {
                    visited.add(e);
                    dis = distanceSoFar.get(src.getId()) + e.getWeight();
                    if (!distanceSoFar.containsKey(neighbor.getId())) {
                        distanceSoFar.put(neighbor.getId(), Double.POSITIVE_INFINITY);
                    }
                    if (distanceSoFar.get(neighbor.getId()) > dis) {
                        distanceSoFar.put(neighbor.getId(), dis);
                        pathToGoal.put(neighbor, src);
                        estimatedDisToTarget = g.distance(neighbor, target);
                        priority = distanceSoFar.get(neighbor.getId()) + estimatedDisToTarget;
                        neighbor.setPriority(priority);
                    }
                    if (pq.contains(neighbor)) {
                        pq.remove(neighbor);
                    }
                    pq.add(neighbor);
                }
            }
        }
        return route;
    }
}
