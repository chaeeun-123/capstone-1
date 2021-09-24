package com.example.capstone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;



public class MypageActivity extends AppCompatActivity {

    private TextView tv_name, tv_id;
    public static String user_name, login_id, session, user_phone, login_pwd, profile_img;
    private ImageView img;

    final private String url = "http://myapp.dothome.co.kr/UserInfo.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        final SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        final Button logout = (Button) findViewById(R.id.logoutButton);
        final Button edit_profile = (Button) findViewById((R.id.edit_profile));


        tv_name = findViewById(R.id.tv_name);
        tv_id = findViewById(R.id.tv_id);

        login_id = sharedPreferences.getString("inputId","");

        // values 값 세팅
        ContentValues values = new ContentValues();
        values.put("login_id", sharedPreferences.getString("inputId",""));

        // httpurlconnection 실행. login_id 값 넘겨 줌
        HttpUtil httpUtil = new HttpUtil(url, values);
        httpUtil.execute();



        // 로그아웃 버튼 클릭 시 수행
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // sharedprefernce에 저장된 값 모두 삭제
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("inputId");
                editor.remove("inputPwd");
                editor.putString(getResources().getString(R.string.prefAutoLoginState),"non-autoLogin");
                editor.putString(getResources().getString(R.string.prefLoginState),"loggedout");
                editor.apply();
                editor.commit();

                startActivity(new Intent(MypageActivity.this, LoginActivity.class));
                finish();

            }
        });

        // 프로필 편집 버튼 클릭 시 수행
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // editprofile activity로 값 넘겨주기
                Intent intent = new Intent(MypageActivity.this, EditProfile.class);
                intent.putExtra("login_id", sharedPreferences.getString("inputId",""));
                intent.putExtra("user_name", user_name);
                intent.putExtra("login_pwd", login_pwd);
                intent.putExtra("profile_img", profile_img);

                startActivity(intent);
            }
        });

    }


    public class HttpUtil extends AsyncTask<Void, Void, String>{
        private String url;
        private ContentValues values;

        public HttpUtil(String url, ContentValues values){
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params){
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            String result = requestHttpURLConnection.postRequest(url, values);
            return result;
        }

        @Override
        protected  void onPostExecute(String result){
            super.onPostExecute(result);

            try {
                // jsonobject value 파싱
                JSONObject jsonObject = new JSONObject(result);
                user_name = jsonObject.getString("user_name");
                user_phone = jsonObject.getString("user_phone");
                login_pwd = jsonObject.getString("login_pwd");
                profile_img = jsonObject.getString("profile_img");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // php에서 받아 온 값 세팅
            tv_name.setText(user_name);
            tv_id.setText(login_id);

            // 프로필 이미지
            img=findViewById(R.id.user_profile);
            Glide.with(getApplicationContext())
                    .load(profile_img)
                    .into(img);


        }
    }



}