package comkimhyeockjin.github.selflocationstatisticsapp;

import android.app.Application;
import android.icu.text.DisplayContext;

import com.tsengvn.typekit.Typekit;


/**
 * Created by SUDALKIM on 2017-11-04.
 */

public class FontActivity extends Application {
    @Override
    public void onCreate(){
        super.onCreate();

        Typekit.getInstance().addNormal(Typekit.createFromAsset(this, "font/NanumBarunGothic.otf")).addBold(Typekit.createFromAsset(this, "font/NanumBarunGothicBold.otf"));

    }

}
