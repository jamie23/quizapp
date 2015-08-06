package com.android.jamie.friendsquizapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class PlayerEntry extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_entry);

        //Clear shared preferences
        clearPreferences();

        //Getting the questions from the server
        new com.android.jamie.friendsquizapp.DownloadJsonTask(this).execute("http://jamie23.pythonanywhere.com/api/v1/questions/?format=json");

        Button btnNextPlayer = (Button) findViewById(R.id.btnNextPlayer);
        Button btnFinished = (Button) findViewById(R.id.btnFinished);

        final EditText playerName = (EditText) findViewById(R.id.inputName);

        btnNextPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences playerPref = getSharedPreferences("playerPref",0);

                String serialized = playerPref.getString("players", "");

                List<String> list = new LinkedList<>();

                if(serialized.isEmpty()){
                    list.add(0,playerName.getText().toString().toLowerCase());
                }else{
                    list.addAll(Arrays.asList(TextUtils.split(serialized, ",")));
                    list.add(playerName.getText().toString().toLowerCase());
                }

                SharedPreferences.Editor editor = playerPref.edit();

                try{
                    editor.putString("players", TextUtils.join(",", list));
                }catch(Exception e){
                    e.printStackTrace();
                }
                editor.commit();
                playerName.setText("");
            }
        });

        btnFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check that at least one person has been added
                SharedPreferences playerPref = getSharedPreferences("playerPref",0);
                String serialized = playerPref.getString("players", "");
                List<String> list = new LinkedList<>();
                list.addAll(Arrays.asList(TextUtils.split(serialized, ",")));

                if(list.size()==0){
                    Toast.makeText(PlayerEntry.this, "Please input players before you begin", Toast.LENGTH_SHORT).show();
                }else {
                    int playerCount = list.size();
                    SharedPreferences.Editor editor = playerPref.edit();
                    editor.putInt("playerCount", playerCount);
                    editor.putInt("currPlayer", 0);

                    editor.commit();

                    Intent questionSelect = new Intent(getApplicationContext(), QuestionSelector.class);
                    startActivity(questionSelect);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the  menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearPreferences(){
        SharedPreferences preferences = getSharedPreferences("playerPref", 0);
        preferences.edit().remove("players").commit();

        String DB_PATH = "data/data/com.android.jamie.friendsquizapp/databases/QuestionsDB";

        try{
            File file = new File(DB_PATH);
            boolean fileDeleted = file.delete();
            Log.d("File deleted safely: ", String.valueOf(fileDeleted));
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}


