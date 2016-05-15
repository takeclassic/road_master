package skku.roma.roadmaster.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 * Created by nyu531 on 2016-04-13.
 */
public class MapGraph {
    Map<Integer, MapNode> Graph;
    DB db;

    public MapGraph(DB db) {
        this.db = db;
        Graph = new HashMap<Integer, MapNode>();

        ArrayList<MapNode> nodes = db.getNodes();
        ArrayList<MapEdgeData> edges = db.getEdges();
        /*
        Edge를 테이블 단위로 하고, Dijkstra 알고리즘을 사용할 때 Edge 테이블에서 검색하는 구조는 어떨까요?
        -> 객체가 적어져서 하드웨어적 Cost는 적어지지만, 많은 Edge가 있는 전체 테이블에서 소수의 Edge만을 검색해야하는 소프트웨어적 Cost가 늘어납니다.
        -> 만약 자료구조를 전부 제작했을 때 하드웨어적 Cost가 매우 증가하여 사용할 수 없다면 자료구조를 바꾸는 것도 좋을 것 같습니다.
         */

        for(MapNode node : nodes){
            Graph.put(node.primary, node);
        }

        for(MapEdgeData edgeData : edges){
            if(edgeData.weight != 0){
                addEdge(edgeData.a, edgeData.b, edgeData.weight);
            }
            else{
                addEdge(edgeData.a, edgeData.b);
            }
        }

        // sortNode();
    }

    @Deprecated // 이를 Deprecated한 이유는 weight는 한번 계산하면 고정값이 되기에 그를 데이터베이스에 저장해두는 것이 좋기 때문입니다. weight는 전부 계산되어 데이터베이스에 저장해두어야 합니다.
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

    @Deprecated
    public void sortNode(){
        Comparator<MapEdge> compare = new Comparator<MapEdge>() {
            @Override
            public int compare(MapEdge mapEdge, MapEdge mapEdge2) {
                return mapEdge.weight < mapEdge2.weight ? -1 : mapEdge.weight > mapEdge2.weight ? 1 : 0;
            }
        };

        for(Map.Entry<Integer, MapNode> elem : Graph.entrySet()){
            elem.getValue().sortEdge(compare);
        }
    }

    public ArrayList<MapNode> FindTheWay(MapNode departure, MapNode destination){
        for(Map.Entry<Integer, MapNode> elem : Graph.entrySet()){
            elem.getValue().weight = Float.MAX_VALUE;
            elem.getValue().pred = null;
        }

        ArrayList<MapNode> path = new ArrayList<MapNode>();
        PriorityQueue<MapNode> queue = new PriorityQueue<>(11, new Comparator<MapNode>() {
            @Override
            public int compare(MapNode mapNode, MapNode mapNode2) {
                return mapNode.weight < mapNode2.weight ? -1 : mapNode.weight > mapNode2.weight ? 1 : 0;
            }
        });

        departure.weight = 0;
        queue.add(departure);
        while(!queue.isEmpty()){
            MapNode node = queue.poll();
            if(node.equals(destination)){
                MapNode pred = destination;
                while(pred != null){
                    path.add(pred);
                    pred = pred.pred;
                }
                return path;
            }

            for(MapEdge edge : node.edgelist){
                if(edge.other.weight > node.weight + edge.weight) {
                    edge.other.pred = node;
                    edge.other.weight = node.weight + edge.weight;
                    queue.add(edge.other);
                }
            }
        }

        return path;
    }

    public MapNode getNodeByLocation(int x, int y){
        double difference;
        double min = Double.MAX_VALUE;
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
