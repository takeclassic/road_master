package skku.roma.roadmaster.util;

import java.util.ArrayList;
import java.util.Arrays;
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
            addEdge(edgeData.a, edgeData.b, edgeData.weight);
        }

        // sortNode();
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

    public Way findWay(Classroom depart, Classroom dest){
        if(depart.b == 0){
            if(dest.b != 0){
                Way waya = findWayWithNodes(Graph.get(depart.a), Graph.get(dest.a));
                Way wayb = findWayWithNodes(Graph.get(depart.a), Graph.get(dest.b));

                if(waya.getWeight() + depart.aweight + dest.aweight < wayb.getWeight() + depart.aweight + dest.bweight){
                    waya.setDepart(depart);
                    waya.setDest(dest);
                    return waya;
                }
                else{
                    wayb.setDepart(depart);
                    wayb.setDest(dest);
                    return wayb;
                }
            }
            else{
                Way way = findWayWithNodes(Graph.get(depart.a), Graph.get(dest.a));
                way.setDepart(depart);
                way.setDest(dest);
                return way;
            }
        }
        else {
            if(dest.b != 0) {
                Way waya = findWayWithNodes(Graph.get(depart.a), Graph.get(dest.a));
                Way wayb = findWayWithNodes(Graph.get(depart.a), Graph.get(dest.b));
                Way wayc = findWayWithNodes(Graph.get(depart.b), Graph.get(dest.a));
                Way wayd = findWayWithNodes(Graph.get(depart.b), Graph.get(dest.b));

                float aweight = waya.getWeight() + depart.aweight + dest.aweight;
                float bweight = wayb.getWeight() + depart.aweight + dest.bweight;
                float cweight = wayc.getWeight() + depart.bweight + dest.aweight;
                float dweight = wayd.getWeight() + depart.bweight + dest.bweight;

                float[] weights = {aweight, bweight, cweight, dweight};
                Arrays.sort(weights);

                if(weights[0] == aweight) {
                    waya.setDest(dest);
                    waya.setDepart(depart);
                    return waya;
                }
                else if(weights[0] == bweight){
                    wayb.setDest(dest);
                    wayb.setDepart(depart);
                    return wayb;
                }
                else if(weights[0] == cweight){
                    wayc.setDest(dest);
                    wayc.setDepart(depart);
                    return wayc;
                }
                else if(weights[0] == dweight){
                    wayd.setDest(dest);
                    wayd.setDepart(depart);
                    return wayd;
                }
            }
            else {
                Way waya = findWayWithNodes(Graph.get(depart.a), Graph.get(dest.a));
                Way wayb = findWayWithNodes(Graph.get(depart.b), Graph.get(dest.a));

                if(waya.getWeight() + depart.aweight + dest.aweight < wayb.getWeight() + depart.bweight + dest.aweight){
                    waya.setDepart(depart);
                    waya.setDest(dest);
                    return waya;
                }
                else{
                    wayb.setDepart(depart);
                    wayb.setDest(dest);
                    return wayb;
                }
            }
        }

        return null;
    }

    public Way findWay(MapNode depart, Classroom dest){
        if(dest.b != 0){
            Way waya = findWayWithNodes(depart, Graph.get(dest.a));
            Way wayb = findWayWithNodes(depart, Graph.get(dest.b));

            if(waya.getWeight() + dest.aweight < wayb.getWeight() + dest.bweight){
                waya.setDest(dest);
                return waya;
            }
            else{
                wayb.setDest(dest);
                return wayb;
            }
        }
        else{
            Way way = findWayWithNodes(depart, Graph.get(dest.a));
            way.setDest(dest);
            return way;
        }
    }

    private Way findWayWithNodes(MapNode departure, MapNode destination){
        Way way = new Way();

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
                way.setPath(path);
                way.setWeight(node.weight);
                return way;
            }

            for(MapEdge edge : node.edgelist){
                if(edge.other.weight > node.weight + edge.weight) {
                    edge.other.pred = node;
                    edge.other.weight = node.weight + edge.weight;
                    queue.add(edge.other);
                }
            }
        }

        return way;
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

    public MapNode getNodeByClassroom(Classroom classroom){
        return Graph.get(classroom.a);
    }
}
