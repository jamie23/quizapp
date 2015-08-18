package com.android.jamie.friendsquizapp;

import android.content.Context;
import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * Created by jamie on 21/07/15.
 */
public class DownloadJsonTask extends AsyncTask<String, Void, JSONObject>{
    private final Context activityContext;

    public DownloadJsonTask(Context passedContext){
        activityContext = passedContext;
    }

    public JSONObject doInBackground(String... url){
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url[0]);

        try{
            HttpResponse response = httpClient.execute(httpGet);

            //Should do error handling here if we cannot access the JSON data online.
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String json = reader.readLine();
            JSONArray questionsJSON = new JSONArray(json);

            for(int i = 0;i<questionsJSON.length();i++){
                JSONObject x = questionsJSON.getJSONObject(i);
                String author = x.get("author").toString().toLowerCase();
                String question = x.get("question_title").toString();
                String answer = x.get("answer").toString();
                String level = x.get("question_level").toString();
                AddQuestionToDB(author, question, answer, level);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void AddQuestionToDB(String author, String question, String answer, String level){
        final DatabaseHandler db = new DatabaseHandler(activityContext);
        db.addQuestion(author, question, answer, level);
    }
}
