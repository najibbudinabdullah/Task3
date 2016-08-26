package com.najib.task3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by w174rd on 8/25/2016.
 */
public class AdminActivity extends AppCompatActivity {
    TextView admin,logout;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        final SharedPreferences shared_preference = getSharedPreferences("authentication", MODE_PRIVATE);

        if(shared_preference.getString("token_authentication", "").equals("")) {
            Intent intent_obj = new Intent(this, MainActivity.class);
            startActivity(intent_obj);
            this.finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        admin = (TextView) findViewById(R.id.textView2);
        logout = (TextView) findViewById(R.id.Logout);

        admin.setText("Hallo, "+shared_preference.getString("nama", ""));

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor sp_editor = shared_preference.edit();
                sp_editor.clear();
                sp_editor.commit();

                intent();
            }

        });
    }

    public void intent(){
        Intent intent_obj = new Intent(this, MainActivity.class);
        startActivity(intent_obj);

        Toast.makeText(getApplicationContext(),"Logout Berhasil..", Toast.LENGTH_SHORT).show();
        this.finish();
    }
}
