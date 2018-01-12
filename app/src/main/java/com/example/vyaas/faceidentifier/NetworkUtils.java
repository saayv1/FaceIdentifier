package com.example.vyaas.faceidentifier;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
/**
 * Created by vyaas on 11/1/17.
 */

public class NetworkUtils  {
    public String[] getFaceIds(String response)
    {
        String[] faceId;
        try {
            JSONArray jsonArray = new JSONArray(response);
            faceId = new String[jsonArray.length()];
            for(int i=0; i<jsonArray.length();i++)
            {
               JSONObject jsonObject = jsonArray.getJSONObject(i);
                faceId[i]= jsonObject.getString("faceId");
            }
            return  faceId;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public String getNumberOfFaces(String response)
    {
        int faceCount;
        try {
            JSONArray jsonArray = new JSONArray(response);
            faceCount=jsonArray.length();
            return String.valueOf(faceCount);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public String getAges(String response)
    {
           try {
            JSONArray jsonArray = new JSONArray(response);
            String ageText="";

            for(int i=0; i<jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ageText=ageText+(String.valueOf(jsonObject.getJSONObject("faceAttributes").getLong("age")))+" ";


            }

            return  ageText;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public String getGender(String response)
    {
        try {
            JSONArray jsonArray = new JSONArray(response);
            String ageText="";

            for(int i=0; i<jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ageText=ageText+(String.valueOf(jsonObject.getJSONObject("faceAttributes").getString("gender")))+" ";


            }

            return  ageText;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public String[] getPersistedIds(String response)
    {
        try {
            JSONArray jsonArray = new JSONArray(response);
            String[] persistedId=new String[jsonArray.length()];

            for(int i=0; i<jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONArray candidate= jsonObject.getJSONArray("candidates");
                if(candidate.length()!=0) {
                    JSONObject personId = candidate.getJSONObject(0);
                    persistedId[i] = personId.getString("personId");
                }
                else{
                    persistedId[i]="";
                }

            }

            return  persistedId;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

}
