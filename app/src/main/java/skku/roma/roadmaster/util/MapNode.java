package skku.roma.roadmaster.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by nyu531 on 2016-04-13.
 */
public class MapNode {
    public int x;
    public int y;
    int primary;
    String name;
    ArrayList<Integer> classlist;
    ArrayList<MapEdge> edgelist;

    public MapNode(int x, int y, int primary, String name) {
        this.x = x;
        this.y = y;
        this.primary = primary;
        this.name = name;
        classlist = new ArrayList<Integer>();
        edgelist = new ArrayList<MapEdge>();
    }

    public void addEdge(MapNode other, float weight){
        edgelist.add(new MapEdge(other, weight));
    }

    public void sortEdge(MapGraph.AscCompare compare){
        Collections.sort(edgelist, compare);
    }

    public void addClass(int classnum){
        classlist.add(classnum);
    }
}
