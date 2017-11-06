package comkimhyeockjin.github.selflocationstatisticsapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by SUDALKIM on 2017-10-23.
 */

public class UserActivity extends Activity{
    private final static int USER_REQUEST = 1;
    Context mContext = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);



        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, USER_REQUEST);

        Button mapBtn = (Button) findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(mContext, MapExampleActivity.class);
                startActivity(intent1);
            }
        });
                                  
        Button graphBtn = (Button) findViewById(R.id.graphBtn);
        graphBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent temp_intent = new Intent(getApplicationContext(), GraphActivity.class);
                startActivity(temp_intent);

            }
        });
        Map<String, Integer> timeMap = new HashMap<>();
        String fileName = "LocationData.bin";
        try {
            File saveFile = new File(this.getCacheDir(), fileName);
            BufferedReader inputStream = new BufferedReader(new FileReader(saveFile.getPath().toString()));
            String str, lastName = "";
            int lastHour = 0, lastMin = 0;

            while ((str = inputStream.readLine()) != null) {
                String[] splitedStr = str.split("#");
                String name = splitedStr[2];

                String[] time = splitedStr[4].split(" ")[1].split(":");
                int hour = Integer.parseInt(time[0]);
                int min = Integer.parseInt(time[1]);
                if (hour < lastHour) {
                    hour += 24;
                }
                if (!lastName.equals("")) {
                    int totalMin = (hour * 60 + min) - (lastHour * 60 + lastMin);

                    if (timeMap.containsKey(lastName)) {
                        totalMin += timeMap.get(lastName);
                    }
                    timeMap.put(lastName, totalMin);
                }
                lastName = name;
                lastHour = hour;
                lastMin = min;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        int max = 0;
        String maxName ="";

        for (String name : timeMap.keySet()) {
            if (timeMap.get(name) > max) {
                max = timeMap.get(name);
                maxName = name;
            }
        }

        TextView timeText = (TextView) findViewById(R.id.timeText);
        TextView bestPlaceText2 = (TextView) findViewById(R.id.bestPlaceText2);

        timeText.setText("이번 달 " + (int)(max/60)+"시간 "+ max%60 +"분을 "+ maxName +"에서 보냈어요!");
        bestPlaceText2.setText(maxName);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            finish();
        } else {
            boolean isLogin = data.getExtras().getBoolean("isLogin");
            String name = data.getExtras().getString("name");
            TextView userNameText = (TextView) findViewById(R.id.userNameText);
            userNameText.setText(name + "님");
            if (!isLogin) {
                finish();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));

    }

}
