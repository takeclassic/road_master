package skku.roma.roadmaster.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by nyu531 on 2016-04-13.
 */
public class Building {
    public int x;
    public int y;
    public TextView textView = null;
    public String text;

    public Building(int x, int y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
    }

    @Deprecated
    public void SetVisible(float minx, float maxx, float miny, float maxy, float MapScale){
        if (minx < x && x < maxx && miny < y && y < maxy) {
            float locationx = (x - minx) * MapScale;
            float locationy = (y - miny) * MapScale;
            int marginLeft = (int) locationx;
            int marginTop = (int) locationy;

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(marginLeft, marginTop, 0, 0);
            textView.setLayoutParams(layoutParams);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
        }
    }
}
