package skku.roma.roadmaster.util;

/**
 * Created by nyu531 on 2016-04-13.
 */
public class MapEdge {
    MapNode other;
    float weight;

    public MapEdge(MapNode other, MapNode me){
        this.other = other;
        this.weight = (float) (4.01d * Math.sqrt(Math.pow(other.x - me.x, 2) + Math.pow(other.y - me.y, 2)));
    }

    public MapEdge(MapNode other, float weight) {
        this.other = other;
        this.weight = weight;
    }
}
