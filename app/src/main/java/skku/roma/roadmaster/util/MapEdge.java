package skku.roma.roadmaster.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nyu531 on 2016-04-13.
 */
public class MapEdge implements Parcelable {
    MapNode other;
    float weight;

    @Deprecated
    public MapEdge(MapNode other, MapNode me){
        this.other = other;
        this.weight = (float) (4.01d * Math.sqrt(Math.pow(other.x - me.x, 2) + Math.pow(other.y - me.y, 2)));
    }

    public MapEdge(MapNode other, float weight) {
        this.other = other;
        this.weight = weight;
    }

    public MapEdge(Parcel parcel){
        readFromParcel(parcel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(other, i);
        parcel.writeFloat(weight);
    }

    public void readFromParcel(Parcel parcel) {
        other = parcel.readParcelable(getClass().getClassLoader());
        weight = parcel.readFloat();
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public MapEdge createFromParcel(Parcel parcel) {
            return new MapEdge(parcel);
        }

        @Override
        public Object[] newArray(int i) {
            return new MapEdge[i];
        }
    };
}
