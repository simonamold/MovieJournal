package com.example.moviejournal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviejournal.Fragments.HomeFragment;
import com.example.moviejournal.R;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";
    private static final String SERVER_IP = "";
   
    private static final int SERVER_PORT = 6000;

    String title;
    TextView titleTxt, genreTxt, descriptionTxt, releaseDateTxt;

    String selectedMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        titleTxt = findViewById(R.id.txtTitleDetail);
        genreTxt = findViewById(R.id.txtGenreDetail);
        descriptionTxt = findViewById(R.id.txtDescriptionDetail);
        releaseDateTxt = findViewById(R.id.txtReleaseYearDetail);

        title = getIntent().getStringExtra("title");
        title = title.trim();

        Toolbar toolbar = findViewById(R.id.toolBar1);
        setSupportActionBar(toolbar);

        new QueryTask().execute("SELECT title, genre, release_date, description FROM all_movies " +
                "WHERE title = '" + title + "'");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.detail_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.backBtn)
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        if(item.getItemId() == R.id.toWatchBtn)
        {
            new QueryTask().execute("INSERT INTO to_watch " +
                    "(id, title, genre, release_date, description) " +
                    "SELECT id, title, genre, release_date, description FROM all_movies " +
                    "WHERE title = '" + title + "'");
            System.out.println("Inserted successfully");
            Toast.makeText(getApplicationContext(), "Inserted Successfully", Toast.LENGTH_LONG).show();
        }
        if(item.getItemId() == R.id.watchedBtn)
        {
            new QueryTask().execute("INSERT INTO watched " +
                    "(id, title, genre, release_date, description) " +
                    "SELECT id, title, genre, release_date, description FROM all_movies " +
                    "WHERE title = '" + title + "'");
            System.out.println("Inserted successfully");
            Toast.makeText(getApplicationContext(), "Inserted Successfully", Toast.LENGTH_LONG).show();
        }
        if(item.getItemId() == R.id.editBtn)
        {
            createNewShowDialog();
        }
        return true;
    }
    private class QueryTask extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... queries) {
            ArrayList<String> results = null;
            try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(queries[0]);

                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                results
                        = (ArrayList<String>) objectInputStream.readObject();
                Log.d(TAG, "Received " + results.size() + " results from server.");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<String> results) {

            Log.d(TAG, "Results: " + results.toString());
            if (results == null) {
                return;
            }

            StringBuilder stringBuilder = new StringBuilder();

            for (String result : results) {
                    selectedMovie = result;
                    stringBuilder.append(result).append("\n");
            }
            String[] substrings = selectedMovie.split("/");

            titleTxt.setText(substrings[0]);
            genreTxt.setText(substrings[1]);
            releaseDateTxt.setText(substrings[2]);
            descriptionTxt.setText(substrings[3]);

        }
    }

    public void createNewShowDialog() {
        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        EditText popUpTitle, popUpGenre, popUpDescription, popUpReleaseDate;
        Button popUpSave, popUpCancel;
        dialogBuilder = new AlertDialog.Builder(this);
        final View showPopUp = getLayoutInflater().inflate(R.layout.add_movie_popup, null);

        popUpTitle = (EditText) showPopUp.findViewById(R.id.txtTitlePopUp);
        popUpGenre = (EditText) showPopUp.findViewById(R.id.txtGenrePopUp);
        popUpDescription = (EditText) showPopUp.findViewById(R.id.txtDescriptionPopUp);
        popUpReleaseDate = (EditText) showPopUp.findViewById(R.id.txtReleaseDatePopUp);
        popUpSave = (Button) showPopUp.findViewById(R.id.btnPopUpSave);
        popUpCancel = (Button) showPopUp.findViewById(R.id.btnPopUpCancel);

        dialogBuilder.setView(showPopUp);
        dialog = dialogBuilder.create();
        dialog.show();


        popUpSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new QueryTask().execute("UPDATE all_movies " +
                        "SET title =" +"'" + popUpTitle.getText().toString() + "'" +
                        ", genre = " + "'" + popUpGenre.getText().toString() + "'" + ", release_date = "
                        + "'" + popUpReleaseDate.getText().toString() + "'" +  ", description = '" +
                        popUpDescription.getText().toString() + "' "
                        + "WHERE title =" + "'" + title + "'");
                System.out.println("Updated successfully");

                dialog.dismiss();
            }
        });

        popUpCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }


}