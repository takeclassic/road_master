package skku.roma.roadmaster.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
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
    private Bitmap departPin;
    private Bitmap destPin;

    public MapView(Context context){
        this(context, null);
    }

    public MapView(Context context, AttributeSet attr){
        super(context, attr);
        initialise();
    }

    private void initialise() {
        /*
        float density = getResources().getDisplayMetrics().densityDpi;
        pin = BitmapFactory.decodeResource(this.getResources(), R.drawable.pin);
        float w = (density/900f) * pin.getWidth();
        float h = (density/900f) * pin.getHeight();
        pin = Bitmap.createScaledBitmap(pin, (int) w, (int) h, true);
        */

        float density = getResources().getDisplayMetrics().densityDpi;
        departPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.departpin);
        destPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.destpin);
        float w = (density/1500f) * departPin.getWidth();
        float h = (density/1500f) * departPin.getHeight();
        departPin = Bitmap.createScaledBitmap(departPin, (int) w, (int) h, true);
        destPin = Bitmap.createScaledBitmap(destPin, (int) w, (int) h, true);
    }

    public void setPath(ArrayList<MapNode> path){
        this.path = path;
        initialise();
        invalidate();
    }
/*
    public void setStartPin(float x, float y){
        start = new PointF(x * 2, y * 2);
        //initialise();
        //invalidate();
    }

    public void setEndPin(float x, float y){
        end = new PointF(x * 2, y * 2);
        initialise();
        invalidate();
    }
    */

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
            /*
            for (int index = 0; index < path.size() - 1; index++){
                Path line = new Path();
                PointF start = sourceToViewCoord(path.get(index).x * 2, path.get(index).y * 2);
                PointF end = sourceToViewCoord(path.get(index + 1).x * 2, path.get(index + 1).y * 2);
                line.moveTo(start.x, start.y);
                line.lineTo(end.x, end.y);
                canvas.drawPath(line, paint);
            }
            */
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
        final int textSize = 40;
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.DKGRAY);

        PointF textPoint = sourceToViewCoord(1634, 1819);
        canvas.drawText("성균관대학교", textPoint.x, textPoint.y, textPaint);
        canvas.drawText("자연과학캠퍼스", textPoint.x, textPoint.y+textSize, textPaint);
        textPoint = sourceToViewCoord(2487, 991);
        canvas.drawText("제2공학관", textPoint.x, textPoint.y, textPaint);

		

/*
        if(start != null && pin != null){
            PointF startPin = sourceToViewCoord(start);
            float x = startPin.x - (pin.getWidth() / 2);
            float y = startPin.y - pin.getHeight();
            canvas.drawBitmap(pin, x, y, paint);
        }

        if(end != null && pin != null){
            PointF endPin = sourceToViewCoord(end);
            float x = endPin.x - (pin.getWidth() / 2);
            float y = endPin.y - pin.getHeight();
            canvas.drawBitmap(pin, x, y, paint);
        }

        if(start != null && end != null && pin != null){
            Log.d("ROMA", "PATH");
            Path path = new Path();
            path.reset();

            PointF startPin = sourceToViewCoord(start);
            path.moveTo(startPin.x, startPin.y);
            PointF endPin = sourceToViewCoord(end);
            path.lineTo(endPin.x, endPin.y);
            paint.setStrokeWidth(5);
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(path, paint);
        }
        */


    }
}
