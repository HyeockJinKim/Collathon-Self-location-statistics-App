package comkimhyeockjin.github.selflocationstatisticsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

public class LoginExampleActivity extends AppCompatActivity {

    private static String OAUTH_CLIENT_ID = "Rj_aFrA9FICH0OWYVlfS";
    private static String OAUTH_CLIENT_SECRET = "pSX6XttGOk";
    private static String OAUTH_CLIENT_NAME="gurwls9628";
    private OAuthLoginButton oAuthLoginButton;
    private OAuthLogin oAuthLogin;
    private String accessToken;
    Context mContext = LoginExampleActivity.this;

    private OAuthLoginHandler oAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean b) {
            if(b){
                accessToken = oAuthLogin.getAccessToken(mContext);
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

        Button button = (Button) findViewById(R.id.calendarButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callCalendarApi();
            }
        });

    }

    private void callCalendarApi(){
        String token = accessToken;
        String header = "Bearer " + token; // Bearer 다음에 공백 추가
        try {
            String apiURL = "https://openapi.naver.com/calendar/createSchedule.json";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", header);
            String calSum =  URLEncoder.encode("[제목] 캘린더API로 추가한 일정", "UTF-8");
            String calDes =  URLEncoder.encode("[상세] 회의 합니다", "UTF-8");
            String calLoc =  URLEncoder.encode("[장소] 그린팩토리", "UTF-8");
            String uid = UUID.randomUUID().toString();
            String scheduleIcalString = "BEGIN:VCALENDAR\n" +
                    "VERSION:2.0\n" +
                    "PRODID:Naver Calendar\n" +
                    "CALSCALE:GREGORIAN\n" +
                    "BEGIN:VTIMEZONE\n" +
                    "TZID:Asia/Seoul\n" +
                    "BEGIN:STANDARD\n" +
                    "DTSTART:19700101T000000\n" +
                    "TZNAME:GMT%2B09:00\n" +
                    "TZOFFSETFROM:%2B0900\n" +
                    "TZOFFSETTO:%2B0900\n" +
                    "END:STANDARD\n" +
                    "END:VTIMEZONE\n" +
                    "BEGIN:VEVENT\n" +
                    "SEQUENCE:0\n" +
                    "CLASS:PUBLIC\n" +
                    "TRANSP:OPAQUE\n" +
                    "UID:" + uid + "\n" +                          // 일정 고유 아이디
                    "DTSTART;TZID=Asia/Seoul:20171024T170000\n" +  // 시작 일시
                    "DTEND;TZID=Asia/Seoul:20171024T173000\n" +    // 종료 일시
                    "SUMMARY:"+ calSum +" \n" +                    // 일정 제목
                    "DESCRIPTION:"+ calDes +" \n" +                // 일정 상세 내용
                    "LOCATION:"+ calLoc +" \n" +                   // 장소
                    "RRULE:FREQ=YEARLY;BYDAY=FR;INTERVAL=1;UNTIL=20201231\n" +  // 일정 반복시 설정
                    "ORGANIZER;CN=관리자:mailto:admin@sample.com\n" + // 일정 만든 사람
                    "ATTENDEE;ROLE=REQ-PARTICIPANT;PARTSTAT=NEEDS-ACTION;CN=admin:mailto:user1@sample.com\n" + // 참석자
                    "CREATED:20171010T160000\n" +         // 일정 생성시각
                    "LAST-MODIFIED:20171010T160000\n" +   // 일정 수정시각
                    "DTSTAMP:20171010T160000\n" +         // 일정 타임스탬프
                    "END:VEVENT\n" +
                    "END:VCALENDAR";
            String postParams = "calendarId=defaultCalendarId&scheduleIcalString=" + scheduleIcalString;
            System.out.println(postParams);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
