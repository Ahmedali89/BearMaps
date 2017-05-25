

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Queue;
import java.util.ArrayDeque;
/**
 * Created by ahmed on 4/10/17.
 */
public class QuadTree {

    private Node root;
    private static int size = 0;

    public QuadTree(double rootULAT, double rootLRLAT, double rootULON, double rootLRLON) {
        size =+ 1;
        root = new Node("img/.png", rootULAT, rootLRLAT, rootULON, rootLRLON, 0);
        createTree(root, 0);
    }

    public void createTree(Node root,int depth) {
        if (depth == 7) {
            return;
        } else {
            double midpointx = (root.getULON() + root.getLRLON()) / 2;
            double midpointy = (root.getULAT() + root.getLRLAT()) / 2;

            String name = root.name.substring(0,root.name.indexOf(".png"));

            root.childern[0] = new Node( name + "1" + ".png",
                    root.getULAT(), midpointy,root.getULON(),midpointx, depth + 1);
            root.childern[1] = new Node(name + "2" + ".png",
                    root.getULAT(), midpointy, midpointx,root.getLRLON(), depth + 1);
            root.childern[2] = new Node(name + "3" + ".png",
                    midpointy,root.getLRLAT(), root.getULON(), midpointx, depth + 1);
            root.childern[3] = new Node(name + "4" + ".png",
                    midpointy,root.getLRLAT(), midpointx, root.getLRLON(), depth + 1);
            size += 4;
            for (Node ch : root.childern) {
                createTree(ch, depth + 1);
            }
            return;
        }
    }
    public int getSize() {
        return size;
    }
    public Node getRoot() {
        return root;
    }
    public Map<String, Object> getImages(Map<String, Double> parmas) {

        /** @return A map of results for the front end as specified:
         * "render_grid"   -> String[][], the files to display
         * "raster_ul_lon" -> Number, the bounding upper left longitude of the rastered image <br>
         * "raster_ul_lat" -> Number, the bounding upper left latitude of the rastered image <br>
         * "raster_lr_lon" -> Number, the bounding lower right longitude of the rastered image <br>
         * "raster_lr_lat" -> Number, the bounding lower right latitude of the rastered image <br>
         * "depth"         -> Number, the 1-indexed quadtree depth of the nodes of the rastered image.
         *                    Can also be interpreted as the length of the numbers in the image
         *                    string. <br>
         * "query_success" -> Boolean, whether the query was able to successfully complete. Don't
         *                    forget to set this to true! <br>*/

        Node queryBox = new Node("required", parmas.get("ullat"), parmas.get("lrlat"),
                parmas.get("ullon"), parmas.get("lrlon"), 0);
        double lONDPP = (parmas.get("lrlon") - (parmas.get("ullon")))/parmas.get("w");
        ArrayList<Node> allRequiredNodes = search(queryBox,lONDPP);
        int depth = allRequiredNodes.get(0).getDepth();
        String[][] images = arrangeImages(allRequiredNodes);
        Map<String, Object> imgs = new HashMap<>();
        int size = allRequiredNodes.size();
        imgs.put("render_grid",images);
        imgs.put("raster_ul_lon",(allRequiredNodes.get(0)).getULON());
        imgs.put("raster_ul_lat",(allRequiredNodes.get(0)).getULAT());
        imgs.put("raster_lr_lon",(allRequiredNodes.get(size - 1)).getLRLON());
        imgs.put("raster_lr_lat",(allRequiredNodes.get(size - 1)).getLRLAT());
        imgs.put("depth", depth);
        imgs.put("query_success", true);
        return imgs;
    }

    public String[][] arrangeImages(ArrayList<Node> list) {

        Set<Double> keys = new HashSet<>();
        for (Node x : list) {
            if (!keys.contains(x.getULAT())) {
                keys.add(x.getULAT());
            }
        }

        Map<Double,ArrayList<String>> mapping = new LinkedHashMap<>();
        for (Node x : list) {
            if(mapping.containsKey(x.getULAT())) {
               mapping.get(x.getULAT()).add(x.getName());
            } else {
                ArrayList<String> l = new ArrayList<>();
                l.add(x.getName());
                mapping.put(x.getULAT(),l);
            }
        }

        String[][] images = new String[keys.size()][];
        int i = 0;
        for (double y : mapping.keySet()) {
            images[i] = new String[(mapping.get(y)).size()];
            for (int j = 0; j < mapping.get(y).size(); j++) {
                images[i][j] = mapping.get(y).get(j);
            }
            i += 1;
        }

        return images;
    }
    public ArrayList<Node> search(Node queryBox,double londpp) {
        ArrayList<Node> list = new ArrayList<>();
        Node tile = this.root;
        Queue<Node> overlapps = new ArrayDeque<>();
        overlapps.add(tile);
        while(!overlapps.isEmpty()) {
            tile = overlapps.remove();
            if (checkIntersection(tile,queryBox)) {
                if (tile.getLONDPP() <= londpp || tile.getDepth() > 6) {
                    list.add(tile);
                } else {
                    for (Node ch : tile.childern) {
                        overlapps.add(ch);
                    }
                }
            }
        }
        return list;
    }

    public boolean checkIntersection(Node tile, Node queryBox) {
        if (tile.getLRLON() <= queryBox.getULON() || queryBox.getLRLON() <= tile.getULON()
                || tile.getLRLAT() >= queryBox.getULAT() || tile.getULAT() <= queryBox.getLRLAT()) {
            return false;
        }
        return true;
    }

    public static void print(String[][] images) {
        for (int s = 0; s < images.length; s++) {
            for (int j = 0; j < images[s].length; j++) {
                System.out.print(images[s][j] + " ");
            }
            System.out.println();
        }
    }

    public void print(Node root, int level) {
        if (root == null) {
            return;
        }
        if (level == 1) {
            System.out.print(root.getName()+ " ");
        } else {
            for (Node x : root.getChildern()) {
                print(x, level - 1);
            }
        }
    }
}
