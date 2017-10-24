package comkimhyeockjin.github.selflocationstatisticsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;

public class MapExampleActivity extends NMapActivity {

    private NMapView mMapView;
    private NMapController mMapController;
    private final String CLIENT_ID = "Rj_aFrA9FICH0OWYVlfS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setMapDataProviderListener(new OnDataProviderListener() {
            @Override
            public void onReverseGeocoderResponse(NMapPlacemark nMapPlacemark, NMapError nMapError) {
                Toast.makeText(getApplicationContext(), nMapPlacemark.toString(), Toast.LENGTH_LONG).show();
            }
        });
        mMapView = new NMapView(this);
        mMapController = mMapView.getMapController();

        setContentView(mMapView);
        mMapView.setClientId(CLIENT_ID);

        final NMapLocationManager mMapLocationManager = new NMapLocationManager(this);
        NMapLocationManager.OnLocationChangeListener onMyLocationChangeListner = new NMapLocationManager.OnLocationChangeListener(){
            @Override
            public boolean onLocationChanged(com.nhn.android.maps.NMapLocationManager nMapLocationManager, com.nhn.android.maps.maplib.NGeoPoint nGeoPoint){

                if (mMapController != null) {
                    mMapController.animateTo(nGeoPoint);
                }


                return true;
            }

            @Override
            public void onLocationUpdateTimeout(com.nhn.android.maps.NMapLocationManager nMapLocationManager){

            }

            @Override
            public void onLocationUnavailableArea(com.nhn.android.maps.NMapLocationManager nMapLocationManager, com.nhn.android.maps.maplib.NGeoPoint nGeoPoint){

            }

        };


        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListner);

        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();


    }

}
