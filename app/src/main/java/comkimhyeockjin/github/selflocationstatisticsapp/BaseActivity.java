package comkimhyeockjin.github.selflocationstatisticsapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.tsengvn.typekit.TypekitContextWrapper;

/**
 * Created by SUDALKIM on 2017-11-04.
 */

public class BaseActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

}
