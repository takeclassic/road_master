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

    private int textSize = 16;

    //Test
    Map<Integer, MapNode> map;
    ArrayList<MapEdgeData> edges;

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
        animateScaleAndCenter(getMaxScale(), current).withDuration(1000).start();
        invalidate();
    }

    public void setPath(ArrayList<MapNode> path){
        this.path = path;
        invalidate();
    }

    @Deprecated
    public void setTest(MapGraph graph, ArrayList<MapEdgeData> edges){
        map = graph.Graph;
        this.edges = edges;
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
        paint.setStrokeWidth(3);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);

        if(current != null && Pin != null){
            PointF coord = sourceToViewCoord(current);
            float x = coord.x - (Pin.getWidth() / 2);
            float y = coord.y - Pin.getHeight();
            canvas.drawBitmap(Pin, x, y, paint);
        }

        if(path != null){
            if(departPin != null && path.get(path.size() - 1).inBuilding == 0){
                PointF coord = sourceToViewCoord(path.get(path.size() - 1).x, path.get(path.size() - 1).y);
                float x = coord.x - (departPin.getWidth() / 2);
                float y = coord.y - departPin.getHeight();
                canvas.drawBitmap(departPin, x, y, paint);
            }

            if(destPin != null && path.get(0).inBuilding == 0){
                PointF coord = sourceToViewCoord(path.get(0).x, path.get(0).y);
                float x = coord.x - (destPin.getWidth() / 2);
                float y = coord.y - destPin.getHeight();
                canvas.drawBitmap(destPin, x, y, paint);
            }

            Path line = new Path();
            PointF start;
            int index;
            for (index = 0;index < path.size(); index++){
                if(path.get(index).inBuilding == 0){
                    start = sourceToViewCoord(path.get(index).x, path.get(index).y);
                    line.moveTo(start.x, start.y);
                    break;
                }
            }

            boolean newline = false;

            for (index = index + 1; index < path.size(); index++){
                if(path.get(index).inBuilding == 0) {
                    PointF end = sourceToViewCoord(path.get(index).x, path.get(index).y);
                    if(newline){
                        line.moveTo(end.x, end.y);
                        newline = false;
                    }
                    else {
                        line.lineTo(end.x, end.y);
                    }
                }
                else{
                    canvas.drawPath(line, paint);
                    newline = true;
                }
            }

            canvas.drawPath(line, paint);
        }

        Paint textPaint = new TextPaint();
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.DKGRAY);

        if(buildings != null){
            for(Building building : buildings){
                PointF textPoint = sourceToViewCoord(building.x, building.y);
                // canvas.drawPoint(textPoint.x, textPoint.y, paint);
                canvas.drawText(building.text, textPoint.x - textPaint.measureText(building.text) / 2, textPoint.y + textSize / 2, textPaint);
            }
        }

        //TEST
        //paint.setStrokeWidth(3);
        if(map != null && edges != null){
            for(MapEdgeData edgeData : edges){
                MapNode a = map.get(edgeData.a);
                MapNode b = map.get(edgeData.b);

                if(a.inBuilding == 0 && b.inBuilding == 0) {
                    PointF start = sourceToViewCoord(a.x, a.y);
                    PointF end = sourceToViewCoord(b.x, b.y);

                    Path line = new Path();
                    line.moveTo(start.x, start.y);
                    line.lineTo(end.x, end.y);
                    canvas.drawPath(line, paint);
                }
            }
        }
    }
}
