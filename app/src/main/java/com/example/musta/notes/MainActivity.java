package com.example.musta.notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> notes;
    private ArrayAdapter<String> adapter;
    private int currentIndex;
    private SharedPreferences sharedPreferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.addNote){
            //go to another activity
            Intent intent = new Intent(getApplicationContext(), NoteWriter.class);
            startActivityForResult(intent, 1);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notes = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, notes);
        sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        ArrayList<String> extra = new ArrayList<String>();
        try {
            extra = (ArrayList<String>) ObjectSerializer.deserialize(
                    sharedPreferences.getString("list", ObjectSerializer.serialize(
                            new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        notes.addAll(extra);

        final ListView notesView = (ListView) findViewById(R.id.notes);
        notesView.setAdapter(adapter);
        notesView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int index = i;
                new AlertDialog.Builder(MainActivity.this)//MainActivity.this needed
                        .setTitle("Are you sure?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Do you want to delete this note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                notes.remove(index);
                                adapter.notifyDataSetChanged();
                                saveList();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;//avoid triggering of onItemClick
            }
        });
        notesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentIndex = i;
                Intent intent = new Intent(getApplicationContext(), NoteWriter.class);
                intent.putExtra("startText", notes.get(i));
                startActivityForResult(intent, 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(1 == requestCode){
            if(resultCode == Activity.RESULT_OK){
                String note = data.getStringExtra("note");
                if(!note.equals("")){
                    notes.add(note);
                    adapter.notifyDataSetChanged();
                    saveList();
                }
            }
        } else if (2 == requestCode){
            if(resultCode == Activity.RESULT_OK){
                String note = data.getStringExtra("note");
                if(!note.equals("")) {
                    notes.remove(currentIndex);
                    notes.add(currentIndex, note);
                    adapter.notifyDataSetChanged();
                    saveList();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveList(){
        try {
            sharedPreferences.edit().putString("list", ObjectSerializer.serialize(notes))
                    .apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
