package skku.roma.roadmaster;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
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
import java.util.Calendar;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

import skku.roma.roadmaster.util.Building;
import skku.roma.roadmaster.util.BuildingList;
import skku.roma.roadmaster.util.DB;
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
    ArrayList<Building> buildings;
    MapGraph Graph;

    ImageButton PlusButton;
    ImageButton MinusButton;

    //GPS
    LocationManager locationManager;
    boolean locationSuccess;
    private static final double latitudeBase = 37.297452;
    private static final double longitudeBase = 126.969801;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(!locationSuccess){
                Toast.makeText(getApplicationContext(), "위치 정보 수신에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        };
    };
    double currentx;
    double currenty;

    //Database
    DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        db = new DB(this);

        loading = (ProgressBar) findViewById(R.id.loading);
        MapLayout = (RelativeLayout) findViewById(R.id.maplayout);
        PlusButton = (ImageButton) findViewById(R.id.plusbutton);
        MinusButton = (ImageButton) findViewById(R.id.minusbutton);

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
                    //updateView();
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
                //updateView();
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

        buildings = db.getBuildings();
        Map.setBuildings(buildings);

        startActivity(new Intent(this, SplashActivity.class)); // Map을 로드하고 로딩 액티비티 실행

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
        locationSuccess = false;

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            pleaseTurnOnGps();
            return;
        }

        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}

            @Override
            public void onLocationChanged(Location location) {
				if(location.getProvider().equals(LocationManager.NETWORK_PROVIDER)){
					Log.d("ROMA", "위치를 네트워크로 구했습니다");
				}

                locationSuccess = true;
                setPin(location.getLatitude(), location.getLongitude());
                locationManager.removeUpdates(this);
            }
        };

        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(lastLocation != null && lastLocation.getTime() > Calendar.getInstance().getTimeInMillis() - 30000){ // 30초가 안 지났으면
            setPin(lastLocation.getLatitude(), lastLocation.getLongitude());
        }

        lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(lastLocation != null && lastLocation.getTime() > Calendar.getInstance().getTimeInMillis() - 30000){
            setPin(lastLocation.getLatitude(), lastLocation.getLongitude());
        }
        else{
            Toast.makeText(this, "위치 정보 수신에 시도합니다.", Toast.LENGTH_SHORT).show();
            Timer timer = new Timer();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    locationManager.removeUpdates(locationListener);
                    handler.sendEmptyMessage(0);
                }
            }, 20000);
        }
    }

    public void onPlusClick(View view) {
        zoom(0.3f);
    }

    public void onMinusClick(View view) {
        zoom(-0.3f);
    }

    public void onSearchClick(View view) {
        //TODO 검색 기능 구현
        Map.setTest(Graph); // TEST
    }

    public void onFindWayClick(View view) {
        //TODO 길찾기 기능 구현, 예시
        ArrayList<MapNode> path = Graph.FindTheWay(Graph.FindTheNode(720, 460), Graph.FindTheNode(1000, 550));
        Map.setPath(path);
    }

    /*
	@Deprecated
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
    */

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
        //updateView();
    }

    private void pleaseTurnOnGps() {
        AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
        gsDialog.setTitle("현재 위치 서비스");
        gsDialog.setMessage("위치 서비스를 실행해주세요.");
        gsDialog.setPositiveButton("실행",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create().show();
    }

    private void setPin(double latitude, double longitude){
        Log.d("ROMA", "latitude : " +latitude+ ", longitude : " +longitude);
        currentx = (longitude - longitudeBase) * 1000000d / 2.8185;
        currenty = (latitudeBase - latitude) * 1000000d / 2.2529;

        Map.setPin(new PointF((float) currentx, (float) currenty));
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
