package comkimhyeockjin.github.selflocationstatisticsapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapCalloutCustomOverlay;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MapExampleActivity extends NMapActivity {

    private static final String TAG = MapExampleActivity.class.getSimpleName();
    private static String lastPlace;
    private NMapView mMapView;
    private NMapController mMapController;
    private final String CLIENT_ID = "Rj_aFrA9FICH0OWYVlfS";
    private NMapOverlayManager mOverlayManager;
    private NMapMyLocationOverlay mMyLocationOverlay;
    private NMapLocationManager mMapLocationManager;
    private NMapCompassManager mMapCompassManager;
    private NMapViewerResourceProvider mMapViewerResourceProvider;
    private MapContainerView mMapContainerView;
    private PlaceDetectionClient mPlaceDetectionClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapView = new NMapView(this);
        setContentView(mMapView);
        mMapView.setClientId(CLIENT_ID);
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();

        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);
        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);
        mOverlayManager.setOnCalloutOverlayListener(onCalloutOverlayListener);
        mOverlayManager.setOnCalloutOverlayViewListener(onCalloutOverlayViewListener);
        mMapController = mMapView.getMapController();
        mMapLocationManager = new NMapLocationManager(this);
        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);
        mMapCompassManager = new NMapCompassManager(this);
        mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);

        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    showCurrentPlace();
                }

            }
        }.start();

//        DataLoadThread loadingThread = new DataLoadThread();
//        loadingThread.setDaemon(true);
//        loadingThread.start();
        startMyLocation();

    }

//    class DataLoadThread extends Thread{
//
//
//        @Override
//        public void run() {
//            while(true) {
//                try {
//                    sleep(20000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                loadMyLocationData();
//            }
//        }
//    }


    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

        @Override
        public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {

            if (mMapController != null) {
                mMapController.animateTo(myLocation);
            }

            return true;
        }

        @Override
        public void onLocationUpdateTimeout(NMapLocationManager locationManager) {

            // stop location updating
            //			Runnable runnable = new Runnable() {
            //				public void run() {
            //					stopMyLocation();
            //				}
            //			};
            //			runnable.run();

            Toast.makeText(MapExampleActivity.this, "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {

            Toast.makeText(MapExampleActivity.this, "Your current location is unavailable area.", Toast.LENGTH_LONG).show();

            stopMyLocation();
        }

    };

    /**
     * TODO 파일에서 저장된 장소의 정보 불러온다.
     */
    public void loadMyLocationData() {
        String fileName = "LocationData.bin";
        try {
            File saveFile = new File(this.getCacheDir(), fileName);
            BufferedReader inputStream = new BufferedReader(new FileReader(saveFile.getPath().toString()));
            String str;
            while ((str = inputStream.readLine()) != null) {
                String[] splitedStr = str.split("#");
                int markerId = NMapPOIflagType.PIN;
                NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
                poiData.beginPOIdata(2);
                poiData.addPOIitem(Double.parseDouble(splitedStr[0]), Double.parseDouble(splitedStr[1]), splitedStr[2], markerId, 0);
                poiData.endPOIdata();
                NMapPOIdataOverlay poIDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
                poIDataOverlay.showAllPOIdata(0);
            }
        } catch (IOException exception) {
            Log.d(TAG, "File Not Found ");
            return;
        }

    }

    /**
     * TODO 나의 위치를 파일로 저장한다.
     */
    public void saveMyLocation(PlaceLikelihood data) {
        String fileName = "LocationData.bin";
        try {
            if(!data.getPlace().getName().equals(lastPlace)) {
                Intent intent = new Intent(this, LastLocationInfo.class);
                File saveFile = new File(this.getCacheDir(), fileName);
                BufferedWriter outputStream = new BufferedWriter(new FileWriter(saveFile.getPath().toString(), true));
                Log.d(TAG, saveFile.getPath());
                outputStream.append(mMapLocationManager.getMyLocation().getLongitude() + "#");
                outputStream.append(mMapLocationManager.getMyLocation().getLatitude() + "#");
                outputStream.append(data.getPlace().getName() + "#");
                outputStream.append(data.getPlace().getPlaceTypes().get(0) + "#");
                long now = System.currentTimeMillis();
                Date date = new Date(now);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                outputStream.append(sdf.format(date));
                outputStream.newLine();
                outputStream.flush();
                outputStream.close();

                lastPlace = data.getPlace().getName().toString();
            }
        } catch (IOException exception) {
            Log.d("Error", "Write Error");
            return;
        }

    }


    private final NMapOverlayManager.OnCalloutOverlayViewListener onCalloutOverlayViewListener = new NMapOverlayManager.OnCalloutOverlayViewListener() {

        @Override
        public View onCreateCalloutOverlayView(NMapOverlay itemOverlay, NMapOverlayItem overlayItem, Rect itemBounds) {


            // null을 반환하면 말풍선 오버레이를 표시하지 않음
            return null;
        }

    };

    private final NMapOverlayManager.OnCalloutOverlayListener onCalloutOverlayListener = new NMapOverlayManager.OnCalloutOverlayListener() {

        @Override
        public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay itemOverlay, NMapOverlayItem overlayItem,
                                                         Rect itemBounds) {

            // handle overlapped items
            if (itemOverlay instanceof NMapPOIdataOverlay) {
                NMapPOIdataOverlay poiDataOverlay = (NMapPOIdataOverlay) itemOverlay;

                // check if it is selected by touch event
                if (!poiDataOverlay.isFocusedBySelectItem()) {
                    int countOfOverlappedItems = 1;

                    NMapPOIdata poiData = poiDataOverlay.getPOIdata();
                    for (int i = 0; i < poiData.count(); i++) {
                        NMapPOIitem poiItem = poiData.getPOIitem(i);

                        // skip selected item
                        if (poiItem == overlayItem) {
                            continue;
                        }

                        // check if overlapped or not
                        if (Rect.intersects(poiItem.getBoundsInScreen(), overlayItem.getBoundsInScreen())) {
                            countOfOverlappedItems++;
                        }
                    }

                    if (countOfOverlappedItems > 1) {
                        String text = countOfOverlappedItems + " overlapped items for " + overlayItem.getTitle();
                        Toast.makeText(MapExampleActivity.this, text, Toast.LENGTH_LONG).show();
                        return null;
                    }
                }
            }

            // use custom old callout overlay
            if (overlayItem instanceof NMapPOIitem) {
                NMapPOIitem poiItem = (NMapPOIitem) overlayItem;

                if (poiItem.showRightButton()) {
                    return new NMapCalloutCustomOldOverlay(itemOverlay, overlayItem, itemBounds,
                            mMapViewerResourceProvider);
                }
            }

            // use custom callout overlay
            return new NMapCalloutCustomOverlay(itemOverlay, overlayItem, itemBounds, mMapViewerResourceProvider);

            // set basic callout overlay
            //return new NMapCalloutBasicOverlay(itemOverlay, overlayItem, itemBounds);
        }

    };

    private void startMyLocation() {

        if (mMyLocationOverlay != null) {
            if (!mOverlayManager.hasOverlay(mMyLocationOverlay)) {
                mOverlayManager.addOverlay(mMyLocationOverlay);


            }
            if (mMapLocationManager.isMyLocationEnabled()) {
                if (!mMapView.isAutoRotateEnabled()) {
                    mMyLocationOverlay.setCompassHeadingVisible(true);

                    mMapCompassManager.enableCompass();

                    mMapView.setAutoRotateEnabled(true, false);

                    mMapContainerView.requestLayout();


                } else {
                    stopMyLocation();
                }

                mMapView.postInvalidate();
            } else {
                boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(true);
                if (!isMyLocationEnabled) {
                    Toast.makeText(MapExampleActivity.this, "Please enable a My Location source in system settings",
                            Toast.LENGTH_LONG).show();

                    Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(goToSettings);

                    return;
                }
                loadMyLocationData();
            }
        }
    }

    private void stopMyLocation() {
        if (mMyLocationOverlay != null) {
            mMapLocationManager.disableMyLocation();

            if (mMapView.isAutoRotateEnabled()) {
                mMyLocationOverlay.setCompassHeadingVisible(false);

                mMapCompassManager.disableCompass();

                mMapView.setAutoRotateEnabled(false, false);

                mMapContainerView.requestLayout();
            }
        }
    }


    private void showCurrentPlace() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        String locationProvider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        final double lng = lastKnownLocation.getLongitude();
        final double lat = lastKnownLocation.getLatitude();


        // Get the likely places - that is, the businesses and other points of interest that
        // are the best match for the device's current location.
        @SuppressWarnings("MissingPermission") final
        Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);

        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    double min = Double.MAX_VALUE;
                    PlaceLikelihood data = null;
                    PlaceLikelihoodBufferResponse places = task.getResult();

                    for (PlaceLikelihood placeLikelihood : places) {
                        // Build a list of likely places to show the user.

                        final double tempLating = placeLikelihood.getPlace().getLatLng().latitude;
                        final double tempLongitude = placeLikelihood.getPlace().getLatLng().longitude;
                        final double distance = Math.pow((lat - tempLating), 2) + Math.pow((lng - tempLongitude), 2);
                        if (min > distance) {
                            min = distance;
                            data = placeLikelihood;
                        }

                    }
                    if (mMapLocationManager.isMyLocationFixed() && data != null) {
                        saveMyLocation(data);
                    }
                } else {
                    Log.e(TAG, "Exception: %s", task.getException());
                }
            }
        });
    }


    private class MapContainerView extends ViewGroup {

        public MapContainerView(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            final int width = getWidth();
            final int height = getHeight();
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final View view = getChildAt(i);
                final int childWidth = view.getMeasuredWidth();
                final int childHeight = view.getMeasuredHeight();
                final int childLeft = (width - childWidth) / 2;
                final int childTop = (height - childHeight) / 2;
                view.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            }

            if (changed) {
                mOverlayManager.onSizeChanged(width, height);
            }
        }
    }


}
