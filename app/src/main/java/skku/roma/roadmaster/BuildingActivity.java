package skku.roma.roadmaster;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import skku.roma.roadmaster.util.BuildingView;
import skku.roma.roadmaster.util.MapNode;
import skku.roma.roadmaster.util.MapView;
import skku.roma.roadmaster.util.Way;


public class BuildingActivity extends ActionBarActivity {

    BuildingView Map;
    String name;
    int number;
    Way way;

    // ActionBar 관련 View
    private ActionBar actionBar;
    TextView nameText;
    Spinner nameSpinner;
    int[] maps;

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
        }
    };
    double currentx;
    double currenty;

    public static int RESULT_GPS = 1;
    public static int RESULT_SET_DEPART = 2;
    public static int RESULT_SET_DEST = 3;

    boolean once = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        final Intent data = getIntent();
        data.setExtrasClassLoader(getClass().getClassLoader());
        number = data.getIntExtra("number", 0);
        name = data.getStringExtra("name");
        way = MainActivity.way;

        if(number != 0) {
            maps = new int[10];
            Resources resources = getResources();

            int bid;
            for(int i = 0; i <= 9; i++){
                maps[i] = resources.getIdentifier("b" + number + i, "drawable", getPackageName());
            }

            Map = (BuildingView) findViewById(R.id.view_building);
            Map.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE);
            Map.setWay(way);
        }

        //actionBar 관련 코드
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = LayoutInflater.from(this);

        View customView = inflater.inflate(R.layout.custom_actionbar2, null);

        nameText = (TextView) customView.findViewById(R.id.actionbar2_text);
        nameText.setText(name);

        nameSpinner = (Spinner) customView.findViewById(R.id.actionbar2_spinner);
        if(!isMapEmpty()){
            ArrayList<String> item = new ArrayList<String>();
            if(maps[9] != 0){
                item.add("B2층");
            }
            if(maps[0] != 0){
                item.add("B1층");
            }

            for(int i = 1; i <= 8; i++){
                if(maps[i] != 0){
                    item.add(i + "층");
                }
            }

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, item);
            spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
            nameSpinner.setAdapter(spinnerAdapter);
            nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String select = (String) nameSpinner.getSelectedItem();
                    if(select.equals("B2층")){
                        Map.setImage(ImageSource.resource(maps[9]));
                        Map.setNumber(number * 10 + i + 9);
                    }
                    else if(select.equals("B1층")){
                        Map.setImage(ImageSource.resource(maps[0]));
                        Map.setNumber(number * 10 + i);
                    }
                    for(int j = 1; j <= 8 ;j++){
                        if(select.equals(j + "층")){
                            Map.setImage(ImageSource.resource(maps[j]));
                            Map.setNumber(number * 10 + j);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            int floor = data.getIntExtra("floor", 0);
            int spinnerposition =  spinnerAdapter.getPosition("1층");
            if(floor != 0){
                floor %= 10;
                if(floor == 0){
                    spinnerposition = spinnerAdapter.getPosition("B1층");
                }
                else if(floor == 9){
                    spinnerposition = spinnerAdapter.getPosition("B2층");
                }
                else{
                    spinnerposition = spinnerAdapter.getPosition(floor + "층");
                }
                if(data.getStringExtra("type").equals("search")) {
                    Map.setPin(new PointF(data.getIntExtra("x", 0), data.getIntExtra("y", 0)));
                    Map.setEnabled(false);
                    nameSpinner.setEnabled(false);
                    LinearLayout setButtons = (LinearLayout) findViewById(R.id.setButtons);
                    setButtons.setVisibility(View.VISIBLE);
                }
                Map.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {
                    @Override
                    public void onReady() {
                        if(once) {
                            Map.animateScaleAndCenter(Map.getMaxScale(), new PointF(data.getIntExtra("x", 0), data.getIntExtra("y", 0))).withDuration(1000).start();
                            once = false;
                        }
                    }

                    @Override
                    public void onImageLoaded() {

                    }

                    @Override
                    public void onPreviewLoadError(Exception e) {

                    }

                    @Override
                    public void onImageLoadError(Exception e) {

                    }

                    @Override
                    public void onTileLoadError(Exception e) {

                    }
                });
            }
            nameSpinner.setSelection(spinnerposition);
        }

        actionBar.setCustomView(customView);
        Toolbar toolbar = (Toolbar) customView.getParent();
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setPadding(0, 0, 0, 0);
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
            Toast.makeText(this, "학교 내부가 아닙니다.",  Toast.LENGTH_SHORT).show();
        }
        else{
            Intent intent = new Intent();
            intent.putExtra("x", currentx);
            intent.putExtra("y", currenty);
            setResult(RESULT_GPS, intent);
            finish();
        }
    }

    boolean isMapEmpty(){
        for(int map : maps){
            if(map != 0){
                return false;
            }
        }

        return true;
    }

    public void onSetDepart(View view) {
        Intent data = getIntent();
        Intent intent = new Intent();
        intent.putExtra("primary", data.getStringExtra("primary"));
        setResult(RESULT_SET_DEPART, intent);
        finish();
    }

    public void onSetDest(View view) {
        Intent data = getIntent();
        Intent intent = new Intent();
        intent.putExtra("primary", data.getStringExtra("primary"));
        setResult(RESULT_SET_DEST, intent);
        finish();
    }
}
