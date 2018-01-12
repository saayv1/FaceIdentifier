package com.example.vyaas.faceidentifier;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    TextView plainText,twitterText,bioText;
    ImageView iv1;
    private AppDatabase sampleDatabase;
    List<Celebrity> celebList;
    private int PICK_IMAGE_REQUEST = 1;
    private int CLICK_IMAGE_REQUEST = 2;
    byte[] byteArray;
    NetworkUtils n;
    IdentificationConstraints identificationConstraints;

    final static String EASTUS_BASE_URL = "https://eastus.api.cognitive.microsoft.com/face/v1.0/detect?returnFaceId=true&returnFaceLandmarks=false&returnFaceAttributes=age,gender";
    final static String EASTUS_IDENTIFICATION_URL = "https://eastus.api.cognitive.microsoft.com/face/v1.0/identify";
    Button cambutton, galbutton, identifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        plainText = (TextView) findViewById(R.id.tv_1);
        twitterText=findViewById(R.id.twitter);
        twitterText.setMovementMethod(LinkMovementMethod.getInstance());
        bioText=findViewById(R.id.bio);
        iv1 = (ImageView) findViewById(R.id.iv_1);
        n = new NetworkUtils();
        cambutton = (Button) findViewById(R.id.cambutton);
        galbutton = (Button) findViewById(R.id.galbutton);
        identifyButton = (Button) findViewById(R.id.identify_button);

        sampleDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "CELEBRITY").build();

        new DatabaseAsync().execute();

        galbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            }
        });

        cambutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CLICK_IMAGE_REQUEST);

            }
        });

        identifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(identificationConstraints!=null) {
                    new OkHttpHandler().execute(EASTUS_IDENTIFICATION_URL);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                identificationConstraints=null;
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                iv1.setImageBitmap(bitmap);
                new OkHttpHandler().execute(EASTUS_BASE_URL, byteArrayOutputStream.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (requestCode == CLICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            identificationConstraints=null;
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byteArray = byteArrayOutputStream.toByteArray();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            iv1.setImageBitmap(photo);
            new OkHttpHandler().execute(EASTUS_BASE_URL);

        }

    }

    public class IdentificationConstraints{
        int numberOfFaces;
        String[] faceIds;

        public int getNumberOfFaces() {
            return numberOfFaces;
        }

        public void setNumberOfFaces(int numberOfFaces) {
            this.numberOfFaces = numberOfFaces;
        }

        public String[] getFaceIds() {
            return faceIds;
        }

        public void setFaceIds(String[] faceIds) {
            this.faceIds = faceIds;
        }

        IdentificationConstraints(int numberOfFaces,String[] faceIds)
        {
         this.numberOfFaces=numberOfFaces;
         this.faceIds=faceIds;
        }
    }



    public class OkHttpHandler extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;

        OkHttpClient client = new OkHttpClient();

        public final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public final MediaType octet_stream = MediaType.parse("application/octet-stream");


        @Override
        protected String doInBackground(String... params) {
            Request.Builder builder = new Request.Builder();
            builder.url(params[0]);
            try {

                Request request;
                if (EASTUS_BASE_URL.equals(params[0])) {
                    RequestBody imageBody = RequestBody.create(octet_stream, byteArray);

                    request = new Request.Builder().url(params[0])
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Ocp-Apim-Subscription-Key", "9f6348e73af841a199131b4332f299a0")
                            .post(imageBody)
                            .build();
                } else {

                    String[] faceIdArray = identificationConstraints.getFaceIds();
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < faceIdArray.length; i++) {
                        array.put(faceIdArray[i]);
                    }
                    JSONObject jll=  new JSONObject();
                    jll.put("personGroupId","1");
                    jll.put("faceIds",array);
                    RequestBody personGro = RequestBody.create(JSON,jll.toString());
                    request = new Request.Builder().url(EASTUS_IDENTIFICATION_URL)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Ocp-Apim-Subscription-Key", "9f6348e73af841a199131b4332f299a0")
                            .post(personGro)
                            .build();
                }

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            byteArray = null;
            super.onPostExecute(s);
            //dialog.dismiss();

            try {
                if(identificationConstraints==null) {
                    twitterText.setText("");
                    bioText.setText("");
                    if (!s.equals("[]") || !s.contains("error")) {
                        String[] faceId = n.getFaceIds(s);
                        String numberoffaces = n.getNumberOfFaces(s);
                        String age = n.getAges(s);
                        String gender = n.getGender(s);
                        plainText.setText("Number of Faces = " + numberoffaces + " " + "\nAge: " + age + "\nGender: " + gender + "\n");
                        identificationConstraints = new IdentificationConstraints(Integer.valueOf(numberoffaces), faceId);
                    } else {
                        plainText.setText("0 Faces Detected");
                    }
                }
                else
                {
                   // plainText.setText(s);
                    String[] persistedId = n.getPersistedIds(s);
                    if(persistedId.length==0)
                    {
                        plainText.setText("Could not identify the face");
                    }
                    else {
                        List<String> peopleNames= new ArrayList<String>();
                        List<String> twitterIds = new ArrayList<>();
                        List<String> bios = new ArrayList<>();
                        String name="";
                        String twitterString="";
                        String bioString="";
                        for(int i=0;i<persistedId.length;i++)
                        {
                            for(int j=0;j<celebList.size();j++)
                            {
                                if(persistedId[i].equals(celebList.get(j).getPersistedId()))
                                {
                                    peopleNames.add(celebList.get(j).getName());
                                    twitterIds.add(celebList.get(j).getTwitter());
                                    bios.add(celebList.get(j).getBio());
                                }
                            }
                        }


                        for(int i =0 ; i<peopleNames.size();i++)
                        {
                            if(i==peopleNames.size()-1) {
                                name=name+(peopleNames.get(i));
                                twitterString=twitterString+(twitterIds.get(i))+"\n";
                                bioString=bioString+(bios.get(i))+"\n";
                                plainText.setText(name);
                                twitterText.setText(twitterString);
                                bioText.setText(bioString);
                            }
                            else
                                name=name+(peopleNames.get(i)+" ,");
                                twitterString=twitterString+(twitterIds.get(i))+"\n";
                                bioString=bioString+(bios.get(i))+"\n";
                        }

                        /*
                        if(persistedId[0].equals(celebList.get(1).getPersistedId()))
                        plainText.setText(celebList.get(1).getName());

                        if(persistedId[0].equals(celebList.get(0).getPersistedId()))
                            plainText.setText(celebList.get(0).getName());
                    */
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                plainText.setText("");
                Toast.makeText(getApplicationContext(), "Could not detect and/or identify", Toast.LENGTH_LONG).show();
            }
        }
    }


    private class DatabaseAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Perform pre-adding operation here.
        }

        @Override
        protected Void doInBackground(Void... voids) {
            /*
            List<Celebrity> c = sampleDatabase.userDao().getAll();
            sampleDatabase.userDao().delete(c.get(0));
            sampleDatabase.userDao().delete(c.get(1));
            */


            //Let's add some dummy data to the database.
/*
            Celebrity celebrity = new Celebrity();
            celebrity.setPersistedId("acd97c9e-5f3e-43a9-a150-6ed029bb91ba");
            celebrity.setBio("Jeffrey Preston Bezos is an American technology and retail entrepreneur, investor, electrical engineer, computer scientist, and philanthropist, best known as the founder, chairman, and chief executive officer of Amazon.com, the world's largest online shopping retailer");
            celebrity.setName("Jeff Bezos");
            celebrity.setTwitter("https://twitter.com/jeffbezos?lang=en");
            celebrity.setWikipedia("https://en.wikipedia.org/wiki/Jeff_Bezos");



            //Now access all the methods defined in DaoAccess with sampleDatabase object
            sampleDatabase.userDao().insertAll(celebrity);
*/

             celebList = sampleDatabase.userDao().getAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //To after addition operation here.
        }
    }


}
