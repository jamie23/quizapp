package com.android.jamie.friendsquizapp;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Random;

public class ForfeitSelector extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forfeit_selector);
        Button btnNext = (Button) findViewById(R.id.btnNext);

        setForfeit();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getApplicationContext(), QuestionSelector.class);
                startActivity(mainIntent);
                finish();
            }
        });
    }

    //Work out the forfeit and set it
    private void setForfeit(){
        Bundle extras = getIntent().getExtras();
        String level = extras.getString("questionLevel");
        final int questionValue = Integer.parseInt(level);
        Random rand = new Random();
        int randomNum = 0;

        switch(questionValue){
            case 1: randomNum = rand.nextInt(13);
                break;
            case 2: randomNum = rand.nextInt(15)+2;
                break;
            case 3: randomNum = rand.nextInt(15)+6;
                break;
            case 4: randomNum = rand.nextInt(20);

        }

        TextView txtRanNum = (TextView) findViewById(R.id.txtRandomNum);
        txtRanNum.setText("Your random number is :  " + Integer.toString(randomNum));
        TextView txtForfeit = (TextView) findViewById(R.id.txtForfeit);

         if(randomNum<5){
            txtForfeit.setText("1 finger of your drink");
        }else if(randomNum>=5&&randomNum<10) {
            txtForfeit.setText("2 fingers of your drink");
        }else if(randomNum>=10&&randomNum<15){
            txtForfeit.setText("3 fingers of your drink");
        }else if(randomNum>=15&&randomNum<21){
            txtForfeit.setText("4 fingers of your drink");
        }else{
            txtForfeit.setText("Down your drink.");
        }

        //Setting titles to custom fonts
        TextView txtForfeitTitle = (TextView) findViewById(R.id.txtForfeitTitle);
        Typeface lobsterFont = Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-Regular.ttf");
        txtForfeitTitle.setTypeface(lobsterFont);
    }
}
