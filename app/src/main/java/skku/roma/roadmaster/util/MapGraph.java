package skku.roma.roadmaster.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nyu531 on 2016-04-13.
 */
public class MapGraph {
    Map<Integer, MapNode> Graph;

    public MapGraph() {
        Graph = new HashMap<Integer, MapNode>();

        addNode(727, 468, 1, "신관 A동 정문");
        addNode(692, 520, 2, "신관 A동 앞");
        addEdge(1, 2, 1f);
        addNode(1029, 562, 3, "신관 B동 정문");
        addEdge(2, 3, 2f);
        sortNode();
    }

    public void addNode(int x, int y, int primary, String name) {
        Graph.put(primary, new MapNode(x, y, primary, name));
    }

    public void addEdge(int a, int b, float weight){
        MapNode node1 = Graph.get(a);
        MapNode node2 = Graph.get(b);

        node1.addEdge(node2, weight);
        node2.addEdge(node1, weight);
    }

    static class AscCompare implements Comparator<MapEdge> {
        @Override
        public int compare(MapEdge mapEdge, MapEdge mapEdge2) {
            return mapEdge.weight < mapEdge2.weight ? -1 : mapEdge.weight > mapEdge2.weight ? 1 : 0;
        }
    }

    public void sortNode(){
        AscCompare compare = new AscCompare();
        for(Map.Entry<Integer, MapNode> elem : Graph.entrySet()){
            elem.getValue().sortEdge(compare);
        }
    }

    public ArrayList<MapNode> FindTheWay(MapNode departure, MapNode destination){
        ArrayList<MapNode> path = new ArrayList<MapNode>();
        path.add(departure);
        path.add(departure.edgelist.get(0).other);
        path.add(destination);
        //TODO 다익스트라 알고리즘을 구현합니다 : 전혀 구현되어 있지 않은 상태
        return path;
    }

    public MapNode FindTheNode(int x, int y){
        double difference;
        double min = 4000;
        MapNode node = null;
        for(Map.Entry<Integer, MapNode> elem : Graph.entrySet()){
            difference = Math.pow(elem.getValue().x - x, 2) + Math.pow(elem.getValue().y - y, 2);
            if(difference < min){
                min = difference;
                node = elem.getValue();
            }
        }

        return node;
    }

}
