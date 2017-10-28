package comkimhyeockjin.github.selflocationstatisticsapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    private final static int PERMISSION_INTERNET = 1;
    private final static int PERMISSION_FINE_LOCATION = 2;
    private final static int PERMISSION_COAST_LOCATION = 3;

    private static String OAUTH_CLIENT_ID = "Rj_aFrA9FICH0OWYVlfS";
    private static String OAUTH_CLIENT_SECRET = "pSX6XttGOk";
    private static String OAUTH_CLIENT_NAME="gurwls9628";
    private OAuthLoginButton loginButton;
    private OAuthLogin oAuthLogin;
    private String accessToken;
    Context mContext = LoginActivity.this;

    private OAuthLoginHandler loginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean b) {
            if(b){
                accessToken = oAuthLogin.getAccessToken(mContext);
                Toast.makeText(mContext, accessToken, Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, "Login Success", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.putExtra("isLogin", true);
                setResult(RESULT_OK, intent);
                finish();
            }
            else{
                String errorCode = oAuthLogin.getLastErrorCode(mContext).getCode();
                String errorDecs = oAuthLogin.getLastErrorDesc(mContext);
                Toast.makeText(mContext, "errorcode"+errorCode+", errordecs: "+errorDecs, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.putExtra("isLogin", false);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PERMISSION_INTERNET);
        }

        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
        }

        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_COAST_LOCATION);
        }

        oAuthLogin = OAuthLogin.getInstance();
        oAuthLogin.init(
                mContext,
                OAUTH_CLIENT_ID,
                OAUTH_CLIENT_SECRET,
                OAUTH_CLIENT_NAME
        );
        loginButton = (OAuthLoginButton)findViewById(R.id.loginBtn);
        loginButton.setOAuthLoginHandler(loginHandler);
        loginButton.setBgResourceId(R.drawable.white_naver_login);

//        Button button = (Button) findViewById(R.id.calendarButton);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new Thread() {
//                    @Override
//                    public void run() {
//                        String token = accessToken;
//                        String header = "Bearer " + token; // Bearer 다음에 공백 추가
//                        try {
//                            String apiURL = "https://openapi.naver.com/calendar/createSchedule.json";
//                            URL url = new URL(apiURL);
//                            HttpURLConnection con = (HttpURLConnection)url.openConnection();
//                            con.setRequestMethod("POST");
//                            con.setRequestProperty("Authorization", header);
//                            String calSum =  URLEncoder.encode("[제목] 캘린더API로 추가한 일정", "UTF-8");
//                            String calDes =  URLEncoder.encode("[상세] 회의 합니다", "UTF-8");
//                            String calLoc =  URLEncoder.encode("[장소] 그린팩토리", "UTF-8");
//                            String uid = UUID.randomUUID().toString();
//                            String scheduleIcalString = "BEGIN:VCALENDAR\n" +
//                                    "VERSION:2.0\n" +
//                                    "PRODID:Naver Calendar\n" +
//                                    "CALSCALE:GREGORIAN\n" +
//                                    "BEGIN:VTIMEZONE\n" +
//                                    "TZID:Asia/Seoul\n" +
//                                    "BEGIN:STANDARD\n" +
//                                    "DTSTART:19700101T000000\n" +
//                                    "TZNAME:GMT%2B09:00\n" +
//                                    "TZOFFSETFROM:%2B0900\n" +
//                                    "TZOFFSETTO:%2B0900\n" +
//                                    "END:STANDARD\n" +
//                                    "END:VTIMEZONE\n" +
//                                    "BEGIN:VEVENT\n" +
//                                    "SEQUENCE:0\n" +
//                                    "CLASS:PUBLIC\n" +
//                                    "TRANSP:OPAQUE\n" +
//                                    "UID:" + uid + "\n" +                          // 일정 고유 아이디
//                                    "DTSTART;TZID=Asia/Seoul:20171024T170000\n" +  // 시작 일시
//                                    "DTEND;TZID=Asia/Seoul:20171024T173000\n" +    // 종료 일시
//                                    "SUMMARY:"+ calSum +" \n" +                    // 일정 제목
//                                    "DESCRIPTION:"+ calDes +" \n" +                // 일정 상세 내용
//                                    "LOCATION:"+ calLoc +" \n" +                   // 장소
//                                    "RRULE:FREQ=YEARLY;BYDAY=FR;INTERVAL=1;UNTIL=20201231\n" +  // 일정 반복시 설정
//                                    "ORGANIZER;CN=관리자:mailto:admin@sample.com\n" + // 일정 만든 사람
//                                    "ATTENDEE;ROLE=REQ-PARTICIPANT;PARTSTAT=NEEDS-ACTION;CN=admin:mailto:user1@sample.com\n" + // 참석자
//                                    "CREATED:20171010T160000\n" +         // 일정 생성시각
//                                    "LAST-MODIFIED:20171010T160000\n" +   // 일정 수정시각
//                                    "DTSTAMP:20171010T160000\n" +         // 일정 타임스탬프
//                                    "END:VEVENT\n" +
//                                    "END:VCALENDAR";
//                            String postParams = "calendarId=defaultCalendarId&scheduleIcalString=" + scheduleIcalString;
//                            System.out.println(postParams);
//                            con.setDoOutput(true);
//                            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//                            wr.writeBytes(postParams);
//                            wr.flush();
//                            wr.close();
//                            int responseCode = con.getResponseCode();
//                            BufferedReader br;
//                            if (responseCode == 200) { // 정상 호출
//                                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
//                            } else {  // 에러 발생
//                                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
//                            }
//                            String inputLine;
//                            StringBuffer response = new StringBuffer();
//                            while ((inputLine = br.readLine()) != null) {
//                                response.append(inputLine);
//                            }
//                            System.out.println(response.toString());
//                            br.close();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }.start();
//            }
//        });


//        Button button1 = (Button) findViewById(R.id.pass);
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), MapExampleActivity.class);
//
//                startActivity(intent);
//
//            }
//        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String [] permission, int [] grantResults) {
        switch (requestCode) {
            case PERMISSION_INTERNET:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext, "INTERNET 권한 승인", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(mContext, "권한 거부.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            case PERMISSION_COAST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext, "Location 권한 승인", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(mContext, "권한 거부.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;

            case PERMISSION_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext, "Location 권한 승인", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(mContext, "권한 거부.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
        }
    }

//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        Button loginBt = (Button) findViewById(R.id.loginBtn);
//        loginBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LoginActivity.this, UserActivity.class);
//
//            }
//        });

//    }
}
