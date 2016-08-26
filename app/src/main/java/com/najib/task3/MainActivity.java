package com.najib.task3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity{
    EditText username, password;
    Button login;
    TextView link,lbl_http_connection;
    HttpURLConnection connection;
    BufferedReader reader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences get_shared_preference = getSharedPreferences("authentication", MODE_PRIVATE);
        if(get_shared_preference.getString("token_authentication", "") == null) {
            Intent intent_obj = new Intent(this, AdminActivity.class);
            startActivity(intent_obj);
        }

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            username = (EditText) findViewById(R.id.editText);
            password = (EditText) findViewById(R.id.editText2);
            link = (TextView) findViewById(R.id.textView);
            lbl_http_connection = (TextView) findViewById(R.id.lbl_http_connection);

            login = (Button) findViewById(R.id.button);

            login.setOnClickListener(new View.OnClickListener() {

                @Override

                public void onClick(View v) {
                    //localhost or 127.0.0.1 , is refer to emulator device it self
                    // use 10.0.2.2, for access local server
                    new ApiConnect().execute("http://private-eaf5da-users170.apiary-mock.com/users");
                    //new ApiConnect().execute("http://10.0.2.2:3000/");
                }

            });
    }

    //this method for handle http connection
    public String get_data(String url_target) {
        String line = "";
        try {
            URL url = new URL(url_target);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            //this will return to onPostExecute when doInBackground finished
            return buffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(connection != null) connection.disconnect();
            try {
                if(reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //this method for handel json parse
    public void process_json(String json_str) throws JSONException {
        try {
            JSONObject api_json = new JSONObject(json_str);
            JSONArray users = api_json.getJSONArray("users");
            boolean aya = false;
            for(int i=0; i<users.length(); i++) {
                JSONObject user = users.getJSONObject(i);
                if(user.getString("password").equals(password.getText().toString()) && user.getString("email").equals(username.getText().toString())){
                    Intent intent_obj = new Intent(this, AdminActivity.class);
                    startActivity(intent_obj);

                    SharedPreferences set_shared_preference = getSharedPreferences("authentication", MODE_PRIVATE);
                    SharedPreferences.Editor sp_editor = set_shared_preference.edit();
                    sp_editor.putString("token_authentication", user.getString("token_auth"));
                    sp_editor.commit();

                    Log.e("Log","Login Sukses");
                    aya = true;
                }
                Log.e("Log","Username1: "+username.getText().toString());
            }
            if(aya == false){
                Toast.makeText(getApplicationContext(),"Login gagal, sok cobaan deui..", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //asynctask method will process http connection in background
    //http connection will not working in UI thread

    class ApiConnect extends AsyncTask<String, String, String> {
        //overlay use for create unseen layer so user cannot click display during loading list
        //Dialog unseen_dialog = new Dialog(MainActivity.this, android.R.style.Theme_Panel);
        //handle loading progress with dialog
        ProgressDialog progress_dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute(){
            //unseen_dialog.setCancelable(false);
            //unseen_dialog.show();
            // this for init progress dialog
            // progress_dialog.setTitle("On Progress ....");
            //progress_dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress_dialog.setCancelable(true);
            progress_dialog.setMessage("Antosan...");
            progress_dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return get_data(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                process_json(s);
                //unseen_dialog.dismiss();
                progress_dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
