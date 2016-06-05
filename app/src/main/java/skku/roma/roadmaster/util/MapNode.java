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
public class MapNode implements Parcelable {
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

    public MapNode(Parcel parcel){
        readFromParcel(parcel);
    }

    public void addEdge(MapNode other){
        edgelist.add(new MapEdge(other, this));
    }

    public void addEdge(MapNode other, float weight){
        edgelist.add(new MapEdge(other, weight));
    }

    public void sortEdge(Comparator<MapEdge> compare){
        Collections.sort(edgelist, compare);
    }

    public String getName(){
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(primary);
        parcel.writeInt(x);
        parcel.writeInt(y);
        parcel.writeString(name);
        parcel.writeInt(inBuilding);
        parcel.writeTypedList(edgelist);
    }

    public void readFromParcel(Parcel parcel) {
        primary = parcel.readInt();
        x = parcel.readInt();
        y = parcel.readInt();
        name = parcel.readString();
        inBuilding = parcel.readInt();
        parcel.readTypedList(edgelist, MapEdge.CREATOR);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public MapNode createFromParcel(Parcel parcel) {
            return new MapNode(parcel);
        }

        @Override
        public MapNode[] newArray(int i) {
            return new MapNode[i];
        }
    };
}
