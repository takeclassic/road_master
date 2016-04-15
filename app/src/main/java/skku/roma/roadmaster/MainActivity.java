package skku.roma.roadmaster;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.ListIterator;

import skku.roma.roadmaster.util.BuildingList;
import skku.roma.roadmaster.util.MapGraph;
import skku.roma.roadmaster.util.MapNode;
import skku.roma.roadmaster.util.MapView;

/**
 * Created by nyu531 on 2016-04-11.
 */
public class MainActivity extends ActionBarActivity {

    private ProgressBar loading;
    private RelativeLayout MapLayout;

    // ActionBar 관련 View
    private ActionBar actionBar;
    EditText departText;
    EditText destText;
    ImageButton departSearch;
    ImageButton destSearch;

    MapView Map;
    private float MapScale;
    private int MapWidth;
    private int MapHeight;
    BuildingList buildings;
    MapGraph Graph;

    ImageButton PlusButton;
    ImageButton MinusButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, SplashActivity.class));
        loading = (ProgressBar) findViewById(R.id.loading);
        MapLayout = (RelativeLayout) findViewById(R.id.maplayout);
        PlusButton = (ImageButton) findViewById(R.id.plusbutton);
        MinusButton = (ImageButton) findViewById(R.id.minusbutton);

        buildings = new BuildingList(MainActivity.this, MapLayout);
        Graph = new MapGraph();

        Map = (MapView) findViewById(R.id.view_map);
        Map.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
        Map.setImage(ImageSource.resource(R.drawable.map));

        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if(Map.isReady()){
                    PointF coordinate = Map.viewToSourceCoord(e.getX(), e.getY()); // 터치 시 좌표 정보를 알 수 있어요~~
                    Toast.makeText(getApplicationContext(), "X : " + Float.toString(coordinate.x) + " Y : " + Float.toString(coordinate.y), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        Map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(Map.isReady()) {
                    if (MapScale != Map.getScale()) {
                        MapScale = Map.getScale();
                    }
                    updateView();
                }

                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

        Map.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {
            @Override
            public void onReady() {
                MapScale = Map.getScale();
                MapHeight = Map.getHeight();
                MapWidth = Map.getWidth();

                Map.setMaxScale(1.5f);
            }

            @Override
            public void onImageLoaded() {
                updateView();
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onPreviewLoadError(Exception e) {

            }

            @Override
            public void onImageLoadError(Exception e) {
                Log.d("ROMA", "ERROR TnT");
            }

            @Override
            public void onTileLoadError(Exception e) {

            }
        });

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = LayoutInflater.from(this);

        View customView = inflater.inflate(R.layout.custom_actionbar, null);

        departText = (EditText) customView.findViewById(R.id.actionbar_depart);
        departSearch = (ImageButton) customView.findViewById(R.id.actionbar_depart_search);
        departSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 검색 기능 구현
            }
        });

        destText = (EditText) customView.findViewById(R.id.actionbar_dest);
        destSearch = (ImageButton) customView.findViewById(R.id.actionbar_dest_search);
        destSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 검색 기능 구현
            }
        });

        actionBar.setCustomView(customView);
        Toolbar toolbar = (Toolbar) customView.getParent();
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setPadding(0, 0, 0, 0);

        PlusButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                zoom(Map.getMaxScale());
                return false;
            }
        });
        MinusButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                zoom(- Map.getMaxScale());
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onGpsClick(View view) {
        //TODO GPS 구현
    }

    public void onPlusClick(View view) {
        zoom(0.2f);
    }

    public void onMinusClick(View view) {
        zoom(-0.2f);
    }

    public void onSearchClick(View view) {
        //TODO 검색 기능 구현
    }

    public void onFindWayClick(View view) {
        //TODO 길찾기 기능 구현, 예시
        ArrayList<MapNode> path = Graph.FindTheWay(Graph.FindTheNode(720, 460), Graph.FindTheNode(1000, 550));
        Map.setPath(path);
    }

    private void updateView(){
        float centerx = Map.getCenter().x;
        float centery = Map.getCenter().y;
        float minx = centerx - (MapWidth / 2) / MapScale;
        float miny = centery - (MapHeight / 2) / MapScale;
        float maxx = centerx + (MapWidth / 2) / MapScale;
        float maxy = centery + (MapHeight / 2) / MapScale;

        //Log.d("ROMA", MapWidth + " " + MapHeight + " " + MapScale);
        //Log.d("ROMA", centerx + " " + centery + " " + minx + " " + miny + " " + maxx + " " + maxy);

        buildings.setVisible(minx, maxx, miny, maxy, MapScale);
    }

    private void zoom(float scale){
        float newscale = Map.getScale() + scale;
        if(newscale > Map.getMaxScale()){
            newscale = Map.getMaxScale();
        }
        else if(newscale < Map.getMinScale()){
            newscale = Map.getMinScale();
        }

        SubsamplingScaleImageView.AnimationBuilder animationBuilder = Map.animateScale(newscale);
        animationBuilder.withDuration(500).start();
        MapScale = newscale;
        updateView();
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
}
