package com.thilojaeggi.tchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SetName extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_start);
        TextView done = findViewById(R.id.done);
        final EditText name = findViewById(R.id.name_et);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!name.getText().toString().isEmpty()){
                    SharedPreferences sharedPreferences= getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("firstart", false);
                    editor.putString("name", name.getText().toString());
                    editor.apply();
                    finish();

                }
            }
        });
    }
}