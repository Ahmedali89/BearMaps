/**
 * Created by ahmed on 4/12/17.
 */
public class Node {
    String name;
    private double ULON;
    private double ULAT;
    private double LRLON;
    private double LRLAT;
    Node[] childern = new Node[4];
    private int depth;
    private double LONDPP;

    public Node(String name, double rootULAT, double rootLRLAT, double rootULON, double rootLRLON, int depth) {
        this.name = name;
        ULON = rootULON;
        ULAT = rootULAT;
        LRLAT = rootLRLAT;
        LRLON = rootLRLON;
        LONDPP = (LRLON - ULON) / 256;
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }
    public Node getFirstChild() {
        return childern[0];
    }

    public Node getSecondChild() {
        return childern[1];
    }

    public Node getThirdChild() {
        return childern[2];
    }

    public Node getFourthChild() {
        return childern[3];
    }

    public double getULON() {
        return ULON;
    }

    public double getULAT() {
        return ULAT;
    }

    public double getLRLON() {
        return LRLON;
    }

    public double getLRLAT() {
        return LRLAT;
    }
    public double getLONDPP() {
        return LONDPP;
    }

    public String getName() {
        return name;
    }
    public Node[] getChildern() {
        return childern;
    }

    public String toString() {
        return "[ " + this.getULON() + ", " + this.getULAT() + ", " + this.getLRLON() + ", " + this.getLRLAT() + " ]";
    }

}
