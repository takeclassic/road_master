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


public class BuildingActivity extends ActionBarActivity {

    BuildingView Map;
    String name;
    int number;
    ArrayList<MapNode> path;

    // ActionBar 관련 View
    private ActionBar actionBar;
    TextView nameText;
    Spinner nameSpinner;
    ArrayList<Integer> maps;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Intent data = getIntent();
        data.setExtrasClassLoader(getClass().getClassLoader());
        number = data.getIntExtra("number", 0);
        name = data.getStringExtra("name");
        path = MainActivity.path;

        if(number != 0) {
            maps = new ArrayList<Integer>();
            Resources resources = getResources();

            int bid;
            int floornumber = 1;
            while((bid = resources.getIdentifier("b" + number + floornumber, "drawable", getPackageName())) != 0){
                floornumber++;
                maps.add(bid);
            }

            if(!maps.isEmpty()){
                Map = (BuildingView) findViewById(R.id.view_building);
                Map.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
                Map.setImage(ImageSource.resource(maps.get(0)));
                Map.setNumber(number * 10 + 1);
            }
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
        if(!maps.isEmpty()){
            String item[] = new String[maps.size()];
            for(int i = 0; i < maps.size(); i++){
                item[i] = (i + 1) + "층";
            }

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, item);
            spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
            nameSpinner.setAdapter(spinnerAdapter);
            nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Map.setImage(ImageSource.resource(maps.get(i)));
                    Map.setNumber(number * 10 + i + 1);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
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
            this.setResult(RESULT_GPS, intent);
            finish();
        }
    }


    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_building, menu);
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
