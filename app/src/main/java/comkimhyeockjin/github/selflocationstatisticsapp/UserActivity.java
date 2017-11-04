package comkimhyeockjin.github.selflocationstatisticsapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tsengvn.typekit.TypekitContextWrapper;

/**
 * Created by SUDALKIM on 2017-10-23.
 */

public class UserActivity extends Activity{
    private final static int USER_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, USER_REQUEST);

        Button graphBtn = (Button) findViewById(R.id.graphBtn);
        graphBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent temp_intent = new Intent(getApplicationContext(), GraphActivity.class);
                startActivity(temp_intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            finish();
        } else {
            boolean isLogin = data.getExtras().getBoolean("isLogin");
            if (!isLogin) {
                finish();
            }
        }
    }


}
