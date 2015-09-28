package com.example.tetali_chaitanya.evhunt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    InputStream inputStream;
    StringBuffer buffer;
    URL url_1;
    String status_login = "";
    EditText rgno,pass,phone;
    Button register;
    ProgressDialog pd;
    String rgno_1,pass_1,phone_1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rgno = (EditText) findViewById(R.id.rgno);
        pass = (EditText) findViewById(R.id.pass);
        phone = (EditText) findViewById(R.id.phone);
        register = (Button)findViewById(R.id.rbutton);
        pd = new ProgressDialog(this);
        pd.setMessage("Fetching Data! ");
        pd.setProgress(0);
        pd.setMax(100);
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);


        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                rgno_1 = rgno.getText().toString();
                pass_1 = pass.getText().toString();
                phone_1 = phone.getText().toString();

                fetchStatus f = new fetchStatus();

                f.onPreExecute();
                f.execute(rgno_1, pass_1, phone_1);
            }
        });
    }

    public class fetchStatus extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected void onPostExecute(String s) {
            pd.dismiss();
            // super.onPostExecute(s);
            Log.d("FINALLLLLL", s);
            if (s.contains("Success"))
            {
                Intent send_details_1 = new Intent(getApplicationContext(), Home.class);

                Bundle stud_details = new Bundle();

                stud_details.putString("rgno", rgno_1);
                stud_details.putString("pass", pass_1);
                stud_details.putString("phone", phone_1);

                send_details_1.putExtras(stud_details);

                startActivity(send_details_1);
            }
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            pd.setMessage(progress[0]);
        }
        @Override
        protected String doInBackground(String... params) {
            try {

                publishProgress("Loading.....20%");

                Log.d("ENTERED","DO IN BACKGROUND");
                final String baseurl = "http://student-check.herokuapp.com/attendance";
                Uri uri = Uri.parse(baseurl).buildUpon()
                        .appendQueryParameter("reg_no", params[0])
                        .appendQueryParameter("dob", params[1])
                        .appendQueryParameter("mob_num", params[2]).build();

                URL url_1 = new URL(uri.toString());

                publishProgress("Loading.....40%");

                Log.d("URL", uri.toString());



                urlConnection = (HttpURLConnection) url_1.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                publishProgress("Loading.....60%");

                Log.d("MAKECONNECTION ", "Im in Make connection");

                inputStream = urlConnection.getInputStream();
                buffer = new StringBuffer();
                if (inputStream == null)
                {
                    // Nothing to do.
                    status_login = null;
                }

                publishProgress("Loading.....80%");

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while ((line = reader.readLine()) != null)
                {
                    buffer.append(line + "\n");
                }
                if (line != "")
                    Log.d("NULLLL", "NULLLLL");

                status_login = buffer.toString();
                Log.d("RESULTTT", status_login);

                publishProgress("Loading.....100%");
            }
            catch (MalformedURLException e)
            {
                Log.d("ERROR", e.toString());
            }
            catch (IOException e)
            {
                Log.d("ERROR", e.toString());
            }
            finally
            {

                if (urlConnection != null)
                {
                    urlConnection.disconnect();
                }
                if (reader != null)
                {
                    try
                    {
                        reader.close();
                    }
                    catch (final IOException e)
                    {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            return status_login;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
