package com.example.musta.notes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NoteWriter extends AppCompatActivity {
    private EditText note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_writer);
        note = (EditText) findViewById(R.id.noteWriter);
        Intent receiver = getIntent();
        String receivedText = receiver.getStringExtra("startText");
        if(receivedText != null){
            note.setText(receivedText);
        }


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("note", note.getText().toString());
        setResult(Activity.RESULT_OK, intent);

        super.onBackPressed();
    }

    private void toaster(String string, boolean longToast){
        if (longToast){
            Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
        }
        //getApplicationContext() gets context of app
    }
}
