package com.android.jamie.friendsquizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * Created by jamie on 30/07/15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "QuestionsDB";
    private static final String TABLE_QUESTIONS = "questions";

    private static final String KEY_ID = "id";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_QUESTION = "question";
    private static final String KEY_ANSWER = "answer";
    private static final String KEY_USED = "used";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_QUESTIONS_TABLE = "CREATE TABLE " + TABLE_QUESTIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_LEVEL + " INTEGER," +
                KEY_AUTHOR + " TEXT," + KEY_QUESTION + " TEXT," + KEY_ANSWER + " TEXT," + KEY_USED + " INTEGER" + ")";

        db.execSQL(CREATE_QUESTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);

        // Create tables again
        onCreate(db);
    }

    // Adding new contact
    public void addQuestion(String author, String question, String answer, String level) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Add image file path
        values.put(KEY_AUTHOR, author);
        values.put(KEY_QUESTION, question);
        values.put(KEY_ANSWER, answer);
        values.put(KEY_LEVEL, level);
        values.put(KEY_USED, "0");

        Log.d("Inserting", author);
        Log.d("Inserting", question);
        Log.d("Inserting", answer);
        Log.d("Inserting", level);

        // Inserting Row
        db.insert(TABLE_QUESTIONS, null, values);
        db.close(); // Closing database connection
    }


    public List<JSONObject> getAllQuestions() {
        List<JSONObject> questionList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_QUESTIONS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                JSONObject jObject = new JSONObject();
                try{
                    jObject.put("id", (cursor.getString(0)));
                    jObject.put("level", (cursor.getString(1)));
                    jObject.put("author", (cursor.getString(2)));
                    jObject.put("question", (cursor.getString(3)));
                    jObject.put("answer", (cursor.getString(4)));
                    jObject.put("used", (cursor.getString(5)));
                }catch (Exception e){
                    e.printStackTrace();
                }
                questionList.add(jObject);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return questionList;
    }

    //Get random question for a passed level that is not written by the current user
    public JSONObject getRandomQuestion(int level, String currUser) {
        List<JSONObject> questionList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_QUESTIONS + " WHERE " + KEY_USED + " = 0 " + "AND " + KEY_LEVEL + " = " + level +
                " AND " + KEY_AUTHOR + " !=  '" + currUser + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                JSONObject jObject = new JSONObject();
                try{
                    jObject.put("id", (cursor.getString(0)));
                    jObject.put("level", (cursor.getString(1)));
                    jObject.put("author", (cursor.getString(2)));
                    jObject.put("question", (cursor.getString(3)));
                    jObject.put("answer", (cursor.getString(4)));
                }catch (Exception e){
                    e.printStackTrace();
                }
                questionList.add(jObject);
            } while (cursor.moveToNext());
        }

        cursor.close();

        if(questionList.size() == 0){
            return null;
        }else{
            Random rand = new Random();
            int ranNum = rand.nextInt(questionList.size());
            return questionList.get(ranNum);
        }
    }

    //Get question based on ID
    public JSONObject getQuestion(String ID) {
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_QUESTIONS + " WHERE " + KEY_ID + " = " + ID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        JSONObject question = new JSONObject();

        try {
            question.put("id", (cursor.getString(0)));
            question.put("level", (cursor.getString(1)));
            question.put("author", (cursor.getString(2)));
            question.put("question", (cursor.getString(3)));
            question.put("answer", (cursor.getString(4)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try{
            updateQuestionUsed(question.get("id").toString(), question.get("level").toString(),
                    question.get("author").toString(), question.get("question").toString(),
                    question.get("answer").toString());

        }catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();

        return question;

    }

    private void updateQuestionUsed(String id, String level, String author, String question , String answer){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues con = new ContentValues();

        con.put("id", id);
        con.put("level", level);
        con.put("author", author);
        con.put("question", question);
        con.put("answer", answer);
        con.put("used", "1");
        db.update(TABLE_QUESTIONS, con, "id ='" + id + "'",null);
    }
}
