package comkimhyeockjin.github.selflocationstatisticsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginDefine;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

public class LoginExampleActivity extends AppCompatActivity {

    private static String OAUTH_CLIENT_ID = "Rj_aFrA9FICH0OWYVlfS";
    private static String OAUTH_CLIENT_SECRET = "pSX6XttGOk";
    private static String OAUTH_CLIENT_NAME="gurwls9628";
    private OAuthLoginButton oAuthLoginButton;
    private OAuthLogin oAuthLogin;
    Context mContext = LoginExampleActivity.this;

    private OAuthLoginHandler oAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean b) {
            if(b){
                String accessToken = oAuthLogin.getAccessToken(mContext);
                Toast.makeText(mContext, accessToken, Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, "Login Success", Toast.LENGTH_SHORT).show();
            }
            else{
                String errorCode = oAuthLogin.getLastErrorCode(mContext).getCode();
                String errorDecs = oAuthLogin.getLastErrorDesc(mContext);
                Toast.makeText(mContext, "errorcode"+errorCode+", errordecs: "+errorDecs, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login_example);

        oAuthLogin = OAuthLogin.getInstance();
        oAuthLogin.init(
                mContext,
                OAUTH_CLIENT_ID,
                OAUTH_CLIENT_SECRET,
                OAUTH_CLIENT_NAME
        );
        oAuthLoginButton = (OAuthLoginButton)findViewById(R.id.btOAuthLoginImg);
        oAuthLoginButton.setOAuthLoginHandler(oAuthLoginHandler);
        oAuthLoginButton.setBgResourceId(R.drawable.library_btn_navermap_bg);

    }
}
