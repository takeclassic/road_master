package skku.roma.roadmaster.util;

/**
 * Created by nyu531 on 2016-04-13.
 */
public class MapEdge {
    MapNode other;
    float weight;

    public MapEdge(MapNode other, float weight) {
        this.other = other;
        this.weight = weight;
    }
}
