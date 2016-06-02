package skku.roma.roadmaster.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nyu531 on 2016-05-06.
 */
public class Classroom {
    public String primary;
    public String name;
    public int x;
    public int y;
    int a;
    float aweight;
    int b = 0;
    float bweight = 0;

    public Classroom(String primary, String name, int x, int y, int a, float aweight) {
        this.primary = primary;
        this.name = name;
        this.y = y;
        this.x = x;
        this.aweight = aweight;
        this.a = a;
    }

    public Classroom(String primary, String name, int x, int y, int a, float aweight, int b, float bweight) {
        this.primary = primary;
        this.name = name;
        this.x = x;
        this.y = y;
        this.a = a;
        this.b = b;
        this.aweight = aweight;
        this.bweight = bweight;
    }
}
