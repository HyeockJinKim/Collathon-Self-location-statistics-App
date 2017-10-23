package comkimhyeockjin.github.selflocationstatisticsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;

public class MapExampleActivity extends NMapActivity {

    private NMapView mMapView;
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
        setContentView(mMapView);

        mMapView.setClientId(CLIENT_ID);
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();

    }




}
