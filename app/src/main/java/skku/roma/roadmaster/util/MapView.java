package skku.roma.roadmaster.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.Map;

import skku.roma.roadmaster.R;

/**
 * Created by nyu531 on 2016-04-14.
 */
public class MapView extends SubsamplingScaleImageView {
    private ArrayList<MapNode> path;
    private PointF current;
    private Bitmap departPin;
    private Bitmap destPin;
    private Bitmap Pin;
    private ArrayList<Building> buildings;

    private int textSize = 20;

    //Test
    Map<Integer, MapNode> map;

    public MapView(Context context){
        this(context, null);
    }

    public MapView(Context context, AttributeSet attr){
        super(context, attr);
        initialise();
    }

    private void initialise() {
        float density = getResources().getDisplayMetrics().densityDpi;
        departPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.departpin);
        destPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.destpin);
        Pin = BitmapFactory.decodeResource(this.getResources(), R.drawable.pin);

        float w = (density/1500f) * departPin.getWidth();
        float h = (density/1500f) * departPin.getHeight();
        departPin = Bitmap.createScaledBitmap(departPin, (int) w, (int) h, true);
        destPin = Bitmap.createScaledBitmap(destPin, (int) w, (int) h, true);

        w = (density/1500f) * Pin.getWidth();
        h = (density/1500f) * Pin.getHeight();
        Pin = Bitmap.createScaledBitmap(Pin, (int) w, (int) h, true);
    }

    public void setTextSize(int textSize){
        this.textSize = textSize;
    }

    public void setBuildings(ArrayList<Building> buildings){
        this.buildings = buildings;
        invalidate();
    }

    public void setPin(PointF current){
        this.current = current;
        invalidate();
        Log.d("ROMA", "x : "+current.x+", y : "+current.y);
    }

    public void setPath(ArrayList<MapNode> path){
        this.path = path;
        invalidate();
    }

    @Deprecated
    public void setTest(MapGraph graph){
        map = graph.Graph;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(!isReady()){
            return;
        }

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);

        if(current != null && Pin != null){
            PointF coord = sourceToViewCoord(current);
            float x = coord.x - (Pin.getWidth() / 2);
            float y = coord.y - Pin.getHeight();
            canvas.drawBitmap(Pin, x, y, paint);
        }

        if(path != null){
            if(departPin != null){
                PointF coord = sourceToViewCoord(path.get(0).x, path.get(0).y);
                float x = coord.x - (departPin.getWidth() / 2);
                float y = coord.y - departPin.getHeight();
                canvas.drawBitmap(departPin, x, y, paint);
            }

            if(destPin != null){
                PointF coord = sourceToViewCoord(path.get(path.size() - 1).x, path.get(path.size() - 1).y);
                float x = coord.x - (destPin.getWidth() / 2);
                float y = coord.y - destPin.getHeight();
                canvas.drawBitmap(destPin, x, y, paint);
            }

            Path line = new Path();
            PointF start = sourceToViewCoord(path.get(0).x, path.get(0).y);
            line.moveTo(start.x, start.y);

            for (int index = 1; index < path.size(); index++){
                PointF end = sourceToViewCoord(path.get(index).x, path.get(index).y);
                line.lineTo(end.x, end.y);
            }

            canvas.drawPath(line, paint);
        }

        //TODO 글자 추적되는 기능 테스트버전. 엔터처리, buildingList 객체 참조하기 구현해야합니다. 가능하다면 좌표기준설정과 정렬기능또한.
        Paint textPaint = new TextPaint();
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.DKGRAY);

        if(buildings != null){
            for(Building building : buildings){
                PointF textPoint = sourceToViewCoord(building.x, building.y);
                canvas.drawText(building.text, textPoint.x, textPoint.y, textPaint);
            }
        }

        //TEST
        paint.setStrokeWidth(3);
        if(map != null){
            for(Map.Entry<Integer, MapNode> elem : map.entrySet()){
                MapNode node = elem.getValue();

                PointF start = sourceToViewCoord(node.x, node.y);

                ArrayList<MapEdge> list = node.edgelist;
                for(MapEdge edge : list){
                    Path line = new Path();
                    line.moveTo(start.x, start.y);
                    PointF end = sourceToViewCoord(edge.other.x, edge.other.y);
                    line.lineTo(end.x, end.y);
                    canvas.drawPath(line, paint);
                }
            }
        }
    }
}
