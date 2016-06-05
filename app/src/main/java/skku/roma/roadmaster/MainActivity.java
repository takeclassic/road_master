package skku.roma.roadmaster;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import skku.roma.roadmaster.util.Building;
import skku.roma.roadmaster.util.Classroom;
import skku.roma.roadmaster.util.DB;
import skku.roma.roadmaster.util.MapGraph;
import skku.roma.roadmaster.util.MapNode;
import skku.roma.roadmaster.util.MapView;
import skku.roma.roadmaster.util.Way;

/**
 * Created by nyu531 on 2016-04-11.
 */
public class MainActivity extends ActionBarActivity implements BeaconConsumer {

    private ProgressBar loading;

    // ActionBar 관련 View
    private ActionBar actionBar;
    EditText departText;
    EditText destText;
    ImageButton departSearch;
    ImageButton destSearch;
    ImageButton departDelete;
    ImageButton destDelete;
    InputMethodManager inputMethodManager;

    MapView Map;
    ArrayList<Building> buildings;
    MapGraph Graph;

    int departType = 0;
    MapNode departNode;
    Classroom departClass;
    Classroom destClass;
    public static Way way;

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
                Toast.makeText(getApplicationContext(), "GPS 정보 수신에 실패했습니다.", Toast.LENGTH_SHORT).show();
                departText.setText("내위치: 찾을 수 없음");
                departType = 0;
            }
        }
    };
    double currentx;
    double currenty;

    //Database
    DB db;

    //Beacon
    private BeaconManager beaconManager;
    private Region region;
    boolean beaconSuccess;
    Collection<Beacon> beacon;
    Thread waitingBeacon;

    //Building Click
    private float BUILDINGX = 200;
    private float BUILDINGY = 100;
    private static int BUILDING = 1;
    private static int SEARCH = 2;
    private static int SETTING = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        db = new DB(this);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        region = new Region("ROMA", null, null, null);
        beaconManager.bind(this);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        loading = (ProgressBar) findViewById(R.id.loading);
        PlusButton = (ImageButton) findViewById(R.id.plusbutton);
        MinusButton = (ImageButton) findViewById(R.id.minusbutton);

        Graph = new MapGraph(db);

        Map = (MapView) findViewById(R.id.view_map);
        Map.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);

        if(sharedPreferences.getBoolean("pref_high_quality_map", false)){
            Map.setImage(ImageSource.resource(R.drawable.maphigh));
        }
        else{
            Map.setImage(ImageSource.resource(R.drawable.map));
        }

        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if(Map.isReady()){
                    PointF coordinate = Map.viewToSourceCoord(e.getX(), e.getY());
                    float x = coordinate.x;
                    float y = coordinate.y;
                    for(Building building : buildings)
                        if (building.number != 0 && building.x - BUILDINGX < x && x < building.x + BUILDINGX && building.y - BUILDINGY < y && y < building.y + BUILDINGY) {
                            Intent buildingActivity = new Intent(MainActivity.this, BuildingActivity.class);
                            buildingActivity.putExtra("number", building.number);
                            buildingActivity.putExtra("name", building.text);
                            startActivityForResult(buildingActivity, BUILDING);
                            break;
                        }
                }
                return true;
            }
        });

        Map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

        Map.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {
            @Override
            public void onReady() {
                Map.setMaxScale(Float.parseFloat(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("pref_max_scale", "1.5")));
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
        Map.setTextSize((Integer.parseInt(sharedPreferences.getString("pref_text_size", "16"))));

        startActivity(new Intent(this, SplashActivity.class)); // Map을 로드하고 로딩 액티비티 실행

        //actionBar 관련 코드
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = LayoutInflater.from(this);

        View customView = inflater.inflate(R.layout.custom_actionbar, null);

        departText = (EditText) customView.findViewById(R.id.actionbar_depart);
        departText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if(charSequence.length() == 0){
                    setDepartDelete(false);
                }
                else{
                    setDepartDelete(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        departText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                final boolean isEnter = keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN;
                if(isEnter){
                    inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    departText.clearFocus();
                    departSearch.callOnClick();
                    return true;
                }
                return false;
            }
        });

        departSearch = (ImageButton) customView.findViewById(R.id.actionbar_depart_search);
        departSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!departText.getText().toString().isEmpty()){
                    Intent search = new Intent(MainActivity.this, SearchActivity.class);
                    search.putExtra("type", "depart");
                    search.putExtra("string", departText.getText().toString());
                    startActivityForResult(search, SEARCH);
                }
                else{
                    Toast.makeText(getApplicationContext(), "검색어를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        departDelete = (ImageButton) customView.findViewById(R.id.actionbar_depart_delete);
        departDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                departText.setText("");
                departText.requestFocus();
            }
        });

        destText = (EditText) customView.findViewById(R.id.actionbar_dest);
        destText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if(charSequence.length() == 0){
                    setDestDelete(false);
                }
                else{
                    setDestDelete(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        destText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                final boolean isEnter = keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN;
                if(isEnter){
                    inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    destText.clearFocus();
                    destSearch.callOnClick();
                    return true;
                }
                return false;
            }
        });

        destSearch = (ImageButton) customView.findViewById(R.id.actionbar_dest_search);
        destSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!destText.getText().toString().isEmpty()) {
                    Intent search = new Intent(MainActivity.this, SearchActivity.class);
                    search.putExtra("type", "dest");
                    search.putExtra("string", destText.getText().toString());
                    startActivityForResult(search, SEARCH);
                }
                else{
                    Toast.makeText(getApplicationContext(), "검색어를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        destDelete = (ImageButton) customView.findViewById(R.id.actionbar_dest_delete);
        destDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destText.setText("");
                destText.requestFocus();
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
        beaconSuccess = false;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean useBeacon = sharedPreferences.getBoolean("pref_use_beacon", true);
        if(useBeacon){
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(!bluetoothAdapter.isEnabled()){
                bluetoothAdapter.enable();
            }

            try {
                beaconManager.startRangingBeaconsInRegion(region);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        waitingBeacon = new Thread(){
            @Override
            public void run() {
                synchronized (this){
                    try {
                        wait(5000);
                        if(beaconSuccess){
                            MapNode node = Graph.getNodeByBeacon(beacon);
                            if(node != null){
                                Intent buildingActivity = new Intent(MainActivity.this, BuildingActivity.class);
                                int number = node.inBuilding / 10;
                                buildingActivity.putExtra("number", number);

                                String name = null;
                                for(Building building : buildings){
                                    if(building.number == number){
                                        name = building.text;
                                    }
                                }
                                buildingActivity.putExtra("name", name);
                                buildingActivity.putExtra("floor", node.inBuilding);
                                buildingActivity.putExtra("x", node.x);
                                buildingActivity.putExtra("y", node.y);
                                buildingActivity.putExtra("type", "beacon");
                                buildingActivity.putExtra("primary", node.primary);
                                startActivityForResult(buildingActivity, BUILDING);
                                locationSuccess = true;
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if(useBeacon) {
                    Toast.makeText(getApplicationContext(), "비콘 정보 수신에 시도합니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected Void doInBackground(Void... voids) {
                if(useBeacon){
                    waitingBeacon.start();
                    try {
                        waitingBeacon.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if(locationSuccess){
                    return;
                }

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
                    showToast("GPS 정보 수신에 시도합니다.");
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
        }.execute(new Void[] {});
    }

    public void onPlusClick(View view) {
        zoom(0.3f);
    }

    public void onMinusClick(View view) {
        zoom(-0.3f);
    }

    public void onSearchClick(View view) {
        Intent search = new Intent(MainActivity.this, SearchActivity.class);
        search.putExtra("type", "search");
        startActivityForResult(search, SEARCH);
        //Map.setTest(Graph, db.getEdges()); // TEST
    }

    public void onFindWayClick(View view) {
        if(departType == 0){
            Toast.makeText(this, "출발지를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if(destClass == null){
            Toast.makeText(this, "도착지를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if(departType == 1 && departNode.inBuilding == 0){
            way = Graph.findWay(departNode, destClass);
            Map.animateScaleAndCenter(Map.getMaxScale(), new PointF(departNode.x, departNode.y)).withDuration(1000).start();
        }
        else if(departType == 1 && departNode.inBuilding != 0 ){
            way = Graph.findWay(departNode, destClass);
            Intent buildingActivity = new Intent(MainActivity.this, BuildingActivity.class);

            int number = departNode.inBuilding / 10;
            buildingActivity.putExtra("number", number);

            String name = null;
            for(Building building : buildings){
                if(building.number == number){
                    name = building.text;
                }
            }
            buildingActivity.putExtra("name", name);
            buildingActivity.putExtra("floor", departNode.inBuilding);
            buildingActivity.putExtra("x", departNode.x);
            buildingActivity.putExtra("y", departNode.y);
            buildingActivity.putExtra("type", "main");
            startActivityForResult(buildingActivity, BUILDING);
        }
        else if(departType == 2){
            way = Graph.findWay(departClass, destClass);
            Intent buildingActivity = new Intent(MainActivity.this, BuildingActivity.class);

            MapNode classnode = Graph.getNodeByClassroom(departClass);
            int number = classnode.inBuilding / 10;
            buildingActivity.putExtra("number", number);

            String name = null;
            for(Building building : buildings){
                if(building.number == number){
                    name = building.text;
                }
            }
            buildingActivity.putExtra("name", name);

            buildingActivity.putExtra("floor", classnode.inBuilding);
            buildingActivity.putExtra("x", departClass.x);
            buildingActivity.putExtra("y", departClass.y);
            buildingActivity.putExtra("type", "main");
            startActivityForResult(buildingActivity, BUILDING);
        }

        Map.setWay(way);
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
        currentx = (longitude - longitudeBase) * 1000000d / 2.8185;
        currenty = (latitudeBase - latitude) * 1000000d / 2.2529;

        if(currentx <= 0 || 3366 <= currentx || currenty <= 0 || 3106 <= currenty){
            departNode = null;
            departType = 0;
            departText.setText("내위치: 학교 내부가 아닙니다.");
        }
        else{
            departNode = Graph.getNodeByLocation((int) currentx, (int) currenty);
            departType = 1;
            departText.setText("내위치: " + departNode.getName());
            Map.setPin(new PointF((float) currentx, (float) currenty));
        }
    }

    private void setDepartDelete(boolean activate){
        if(activate){
            departDelete.setVisibility(View.VISIBLE);
            departDelete.setClickable(true);
        }
        else{
            departDelete.setVisibility(View.INVISIBLE);
            departDelete.setClickable(false);
            departType = 0;
        }
    }

    private void setDestDelete(boolean activate){
        if(activate){
            destDelete.setVisibility(View.VISIBLE);
            destDelete.setClickable(true);
        }
        else{
            destDelete.setVisibility(View.INVISIBLE);
            destDelete.setClickable(false);
            destClass = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
        if(requestCode == BUILDING && resultCode == BuildingActivity.RESULT_GPS){
            currentx = data.getDoubleExtra("x", currentx);
            currenty = data.getDoubleExtra("y", currenty);
            departNode = Graph.getNodeByLocation((int) currentx, (int) currenty);
            departType = 1;
            departText.setText("내위치: " + departNode.getName());
            Map.setPin(new PointF((float) currentx, (float) currenty));
        }
        */

        if(requestCode == BUILDING && resultCode == BuildingActivity.RESULT_SET_DEPART){
            String primary = data.getStringExtra("primary");
            Classroom classroom = db.getClass(primary);
            departText.setText(classroom.primary + " : " + classroom.name);
            departClass = classroom;
            departType = 2;
        }

        if(requestCode == BUILDING && resultCode == BuildingActivity.RESULT_SET_DEST){
            String primary = data.getStringExtra("primary");
            Classroom classroom = db.getClass(primary);
            destText.setText(classroom.primary + " : " + classroom.name);
            destClass = classroom;
        }

        if(requestCode == BUILDING && resultCode == BuildingActivity.RESULT_SET_BEACON){
            int primary = data.getIntExtra("primary", 0);
            departNode = Graph.getNodeByPrimary(primary);
            departType = 1;
            departText.setText("내위치: " + departNode.getName());
        }

        if(requestCode == SEARCH && resultCode == SearchActivity.RESULT_SELECT){
            String type = data.getStringExtra("type");
            String primary = data.getStringExtra("primary");
            Classroom classroom = db.getClass(primary);
            if(type.equals("depart")){
                departText.setText(classroom.primary + " : " + classroom.name);
                departClass = classroom;
                departType = 2;
            }
            else if(type.equals("dest")){
                destText.setText(classroom.primary + " : " + classroom.name);
                destClass = classroom;
            }
            else if(type.equals("search")){
                Intent buildingActivity = new Intent(MainActivity.this, BuildingActivity.class);

                MapNode classnode = Graph.getNodeByClassroom(classroom);
                int number = classnode.inBuilding / 10;
                buildingActivity.putExtra("number", number);

                String name = null;
                for(Building building : buildings){
                    if(building.number == number){
                        name = building.text;
                    }
                }
                buildingActivity.putExtra("name", name);
                buildingActivity.putExtra("floor", classnode.inBuilding);
                buildingActivity.putExtra("x", classroom.x);
                buildingActivity.putExtra("y", classroom.y);
                buildingActivity.putExtra("type", "search");
                buildingActivity.putExtra("primary", classroom.primary);
                startActivityForResult(buildingActivity, BUILDING);
            }
        }

        if(requestCode == SETTING){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            Map.setTextSize((Integer.parseInt(sharedPreferences.getString("pref_text_size", "16"))));
            Map.setMaxScale(Float.parseFloat(sharedPreferences.getString("pref_max_scale", "1.5")));

            if(sharedPreferences.getBoolean("pref_high_quality_map", false)){
                Map.setImage(ImageSource.resource(R.drawable.maphigh));
            }
            else{
                Map.setImage(ImageSource.resource(R.drawable.map));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if(beacons.size() > 0){
                    beaconSuccess = true;
                    beacon = beacons;
                    try {
                        beaconManager.stopRangingBeaconsInRegion(region);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    synchronized (waitingBeacon){
                        waitingBeacon.notifyAll();
                    }
                }
            }
        });
    }

    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onSettingClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, SETTING);
    }
}
