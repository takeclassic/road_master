package skku.roma.roadmaster.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by nyu531 on 2016-04-13.
 */
public class Building {
    public int number;
    public int x;
    public int y;
    public String text;

    public Building(int number, int x, int y, String text) {
        this.number = number;
        this.x = x;
        this.y = y;
        this.text = text;
    }
}
