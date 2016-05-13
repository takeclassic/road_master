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

        addNode(734, 490, 1, "신관 A동 정문 앞");
        addNode(692, 520, 2, "신관 A동 앞");
        addEdge(1, 2);
        addNode(893, 486, 3, "신관 B동 후문 앞");
        addEdge(1, 3);
        addNode(890, 559, 4, "신관 B동 앞");
        addEdge(2, 4);
        addEdge(3, 4);
        addNode(1037, 585, 5, "신관 B동 정문 앞");
        addEdge(4, 5);
        addNode(1321, 532, 6, "생명공학관 근처 샛길 앞");
        addEdge(5, 6);
        addNode(1254, 656, 7, "생명공학관 근처 샛길 중간");
        addEdge(6, 7);
        addNode(1386, 676, 8, "생명공학관 근처 샛길");
        addEdge(7, 8);
        addNode(1535, 581, 9, "생명공학관 입구");
        addEdge(8, 9);
        addNode(1483, 504, 10, "생명공학관 앞");
        addEdge(6, 10);
        addEdge(9, 10);
        addNode(1537, 296, 11, "밍기뉴 앞");
        addEdge(10, 11);
        addNode(1700, 321, 12, "의관 앞");
        addEdge(11, 12);
        addNode(1790, 438, 13, "의관 앞 갈림길");
        addEdge(12, 13);
        addNode(1644, 469, 14, "의관 앞 갈림길");
        addEdge(9, 14);
        addEdge(10, 14);
        addEdge(13, 14);
        addNode(1867, 441, 15, "예관 앞 갈림길");
        addEdge(13, 15);
        addNode(1855, 527, 16, "생명공학관 앞 갈림길");
        addEdge(14, 16);
        addEdge(15, 16);
        addNode(1899, 653, 17, "생명공학대학 앞 갈림길");
        addEdge(16, 17);
        addNode(1672, 638, 18, "생명공학대학 앞 샛길");
        addEdge(17, 18);
        addNode(1606, 682, 19, "생명공학관 앞");
        addEdge(9, 19);
        addEdge(18, 19);
        addNode(1556, 733, 20, "생명공학관 앞");
        addEdge(19, 20);

        sortNode();
    }

    public void addNode(int x, int y, int primary, String name) {
        Graph.put(primary, new MapNode(x, y, primary, name));
    }

    public void addEdge(int a, int b){
        MapNode node1 = Graph.get(a);
        MapNode node2 = Graph.get(b);

        node1.addEdge(node2);
        node2.addEdge(node1);
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
