package skku.roma.roadmaster.util;

import java.util.ArrayList;

/**
 * Created by nyu53 on 2016-06-03.
 */
public class Way {
    ArrayList<MapNode> path;
    float weight;
    Classroom depart;
    Classroom dest;

    public Way() {
    }

    public void setPath(ArrayList<MapNode> path) {
        this.path = path;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getWeight() {
        return weight;
    }

    public void setDepart(Classroom depart) {
        this.depart = depart;
    }

    public void setDest(Classroom dest) {
        this.dest = dest;
    }
}
