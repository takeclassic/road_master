package skku.roma.roadmaster.util;

/**
 * Created by takec on 2016-05-08.
 * MapNode의 Classlist를 DB로 표현하기 위한 객체
 */
public class ClassList {
    int num;
    MapNode node;

    public int getNum() {
        return num;
    }
    public void setNum(int num) {
        this.num = num;
    }
    public int getNodeX() {
        return this.node.x;
    }
    public int getNodeY() {
        return this.node.y;
    }

    public ClassList(int num,  MapNode node) {
        this.num = num;
        this.node=node;
    }

    public ClassList() {

    }
}
