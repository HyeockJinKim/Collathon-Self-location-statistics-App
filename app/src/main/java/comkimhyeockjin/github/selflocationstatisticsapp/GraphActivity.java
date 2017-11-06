package comkimhyeockjin.github.selflocationstatisticsapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.PieChart;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SUDALKIM on 2017-10-30.
 */

public class GraphActivity extends Activity {
    PieChart mChart;
    private static final String TAG = GraphActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        mChart = (PieChart) findViewById(R.id.chart1);
        String fileName = "LocationData.bin";
        try {
            File saveFile = new File(this.getCacheDir(), fileName);
            BufferedReader inputStream = new BufferedReader(new FileReader(saveFile.getPath().toString()));
            String str, lastName="";
            int lastHour=0, lastMin = 0;
            Map<String, Integer> timeMap = new HashMap<>();
            while ((str = inputStream.readLine()) != null) {
                String[] splitedStr = str.split("#");
                String name = splitedStr[2];
                if (!lastName.equals("")) {
                    String[] time = splitedStr[4].split(" ")[1].split(":");
                    int hour = Integer.parseInt(time[0]);
                    int min = Integer.parseInt(time[1]);
                    if (hour < lastHour) {
                        hour += 24;
                    }

                    int totalMin = (hour*60+min) - (lastHour*60+lastMin);

                    if (timeMap.containsKey(lastName)) {
                        totalMin += timeMap.get(lastName);
                    }
                    timeMap.put(lastName, totalMin);
                }
                lastName = name;

            }
        } catch (IOException exception) {
            Log.d(TAG, "File Not Found ");
            return;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));

    }

}
