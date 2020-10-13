package com.thilojaeggi.tchat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class SettingsActivity extends Activity {
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    ImageButton close;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        AppCompatButton changename = findViewById(R.id.changenamebutton);
        close = findViewById(R.id.close);
        sharedPreferences = getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        changename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(SettingsActivity.this);
                AlertDialog dialog = new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("Name")
                        .setMessage("Gib hier einen neuen Namen ein")
                        .setView(editText)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String editTextInput = editText.getText().toString();
                                editor.putString("name", editTextInput).apply();
                            }
                        })
                        .setNegativeButton("Abbrechen", null)
                        .create();
                dialog.show();
            }
        });
    }
}