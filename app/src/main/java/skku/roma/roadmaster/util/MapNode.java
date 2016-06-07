package skku.roma.roadmaster.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by nyu531 on 2016-04-13.
 */
public class MapNode {
    public int primary;
    public int x;
    public int y;
    String name;
    public int inBuilding;
    ArrayList<MapEdge> edgelist;

    // For Dijkstra
    float weight;
    MapNode pred;

    public MapNode(int primary, int x, int y, String name, int inBuilding) {
        this.primary = primary;
        this.x = x;
        this.y = y;
        this.name = name;
        this.inBuilding = inBuilding;

        edgelist = new ArrayList<MapEdge>();
    }

    public void addEdge(MapNode other, float weight){
        edgelist.add(new MapEdge(other, weight));
    }

    public String getName(){
        return name;
    }
}
