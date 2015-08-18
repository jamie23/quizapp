package com.android.jamie.friendsquizapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class QuestionSelector extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_selector);

        Button btnQuestion1 = (Button) findViewById(R.id.btnOne);
        Button btnQuestion2 = (Button) findViewById(R.id.btnTwo);
        Button btnQuestion3 = (Button) findViewById(R.id.btnThree);
        Button btnSkip = (Button) findViewById(R.id.btnSkip);

        final String currentPlayer = updateCurrentPlayer();

        final DatabaseHandler db = new DatabaseHandler(this);

        btnQuestion1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject question = db.getRandomQuestion(1, currentPlayer);
                checkQuesStartActivity(question);

            }
        });

        btnQuestion2.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                JSONObject question = db.getRandomQuestion(2, currentPlayer);
                                                checkQuesStartActivity(question);
                                            }
                                        }

        );

        btnQuestion3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject question = db.getRandomQuestion(3, currentPlayer);
                checkQuesStartActivity(question);
            }
        });


        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent drinkIntent = new Intent(getApplicationContext(), ForfeitSelector.class);
                drinkIntent.putExtra("questionLevel", "4");
                startActivity(drinkIntent);
                finish();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question_selector, menu);
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

    public String updateCurrentPlayer() {
        SharedPreferences playerPref = getSharedPreferences("playerPref", 0);

        String serialized = playerPref.getString("players", "");
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(TextUtils.split(serialized, ",")));

        int totalPlayers = playerPref.getInt("playerCount", 0);
        int currPlayer = playerPref.getInt("currPlayer", 0);


        TextView editCurrPlayer = (TextView) findViewById(R.id.currPlayer);
        Typeface lobsterFont = Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-Regular.ttf");

        editCurrPlayer.setTypeface(lobsterFont);


        String currPlayerName = list.get(currPlayer % totalPlayers);
        editCurrPlayer.setText(currPlayerName);


        SharedPreferences.Editor editor = playerPref.edit();
        editor.putInt("currPlayer", currPlayer + 1);
        editor.commit();

        return currPlayerName;
    }

    //Check that there is an available question (not asked and not written by current user
    private void checkQuesStartActivity(JSONObject question) {

        if (question == null) {
            Toast.makeText(QuestionSelector.this, "There are no questions left at this level for you.", Toast.LENGTH_SHORT).show();
        } else {
            Intent askQuestion = new Intent(getApplicationContext(), QuestionActivity.class);

            try {
                String questionID = question.get("id").toString();
                String questionLevel = question.get("level").toString();

                //Allow access to question ID in the next activity
                askQuestion.putExtra("questionID", questionID);
                askQuestion.putExtra("questionLevel", questionLevel);

                startActivity(askQuestion);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}