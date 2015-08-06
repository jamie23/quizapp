package com.android.jamie.friendsquizapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONObject;

public class QuestionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Button btnFinished = (Button) findViewById(R.id.btnFinished);
        TextView txtQuestion = (TextView) findViewById(R.id.txtQuestion);
        TextView txtAns = (TextView) findViewById(R.id.txtAns);

        Bundle extras = getIntent().getExtras();
        final String id = extras.getString("questionID");
        final String level = extras.getString("questionLevel");

        DatabaseHandler db = new DatabaseHandler(this);
        JSONObject question = db.getQuestion(id);

        //Set this to DB question
        try{
            txtQuestion.setText(question.get("question").toString());
            txtAns.setText(question.get("answer").toString());
        }catch(Exception e){
            e.printStackTrace();
        }

        btnFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent drinkIntent = new Intent(getApplicationContext(), ForfeitSelector.class);
                drinkIntent.putExtra("questionLevel", level);
                startActivity(drinkIntent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question, menu);
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
}
