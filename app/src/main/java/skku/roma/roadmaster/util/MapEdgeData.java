package skku.roma.roadmaster.util;

/**
 * Created by nyu531 on 2016-05-15.
 */
public class MapEdgeData {
    int primary;
    int a;
    int b;
    float weight;

    public MapEdgeData(int primary, int a, int b, float weight) {
        this.primary = primary;
        this.a = a;
        this.b = b;
        this.weight = weight;
    }
}
