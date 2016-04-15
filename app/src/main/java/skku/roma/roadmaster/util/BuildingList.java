package skku.roma.roadmaster.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nyu531 on 2016-04-13.
 */
public class BuildingList {
    Context context;
    RelativeLayout MapLayout;
    ArrayList<Building> buildings;
    int fontSize = 15;

    public BuildingList(Context context, RelativeLayout MapLayout) {
        this.context = context;
        this.MapLayout = MapLayout;
        buildings = new ArrayList<Building>();

        //TODO 기존 Map에서 글자를 삭제하고 정보를 추가해야 합니다.
        buildings.add(new Building(1634, 1819, " 성균관대학교\n자연과학캠퍼스"));
        buildings.add(new Building(2487, 991, "제2공학관"));
    }

    public void setFontSize(int fontSize){
        this.fontSize = fontSize;
    }

    public void setVisible(float minx, float maxx, float miny, float maxy, float MapScale){
        for(Building building : buildings) {
            if (minx < building.x && building.x < maxx && miny < building.y && building.y < maxy) {
                float locationx = (building.x - minx) * MapScale;
                float locationy = (building.y - miny) * MapScale;
                int marginLeft = (int) locationx;
                int marginTop = (int) locationy;

                if(building.textView == null) {
                    TextView newTextView = new TextView(context);
                    newTextView.setText(building.text);
                    newTextView.setTextSize(fontSize);
                    building.textView = newTextView;
                    MapLayout.addView(building.textView);
                }

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(marginLeft, marginTop, 0, 0);

                building.textView.setLayoutParams(layoutParams);
                building.textView.setVisibility(View.VISIBLE);
            } else {
                building.textView.setVisibility(View.INVISIBLE);
            }
        }
    }
}
