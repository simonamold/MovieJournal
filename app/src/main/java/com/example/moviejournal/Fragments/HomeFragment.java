package com.example.moviejournal.Fragments;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.moviejournal.Activities.DetailsActivity;
import com.example.moviejournal.R;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    ArrayList<String> allMovies = new ArrayList<>();
    private static final String TAG = "MainActivity";

    private static final String SERVER_IP = "";
    private static final int SERVER_PORT = 6000;

    private int id = 100;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        listView = view.findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_home, allMovies);

        if (listView != null) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    String title = (String) adapterView.getItemAtPosition(i);
                    String word = " Year: ";
                    int index = title.indexOf(word);
                    System.out.println(title.substring(0, index));
                    intent.putExtra("title", title.substring(0, index));
                    startActivity(intent);
                }
            });
        }

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (allMovies.contains(s)){
                    adapter.getFilter().filter(s);
                }
                else{
                    System.out.println("not found");
                }
                return  false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        Button btnAddMovie;
        btnAddMovie = view.findViewById(R.id.btnAddMovie);
        btnAddMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewShowDialog();

            }
        });

        new QueryTask(listView).execute("SELECT * FROM all_movies");
        return view;
    }


    private class QueryTask extends AsyncTask<String, Void, ArrayList<String>> {

        private ListView listView;

        public QueryTask(ListView listView) {
            this.listView = listView;
        }

        @Override
        protected ArrayList<String> doInBackground(String... queries) {
            ArrayList<String> results = null;
            try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(queries[0]);

                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                results = (ArrayList<String>) objectInputStream.readObject();
                Log.d(TAG, "Received " + results.size() + " results from server.");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return results;
        }


        @Override
        protected void onPostExecute(ArrayList<String> results) {

            if (results == null) {
                return;
            }

            StringBuilder stringBuilder = new StringBuilder();

            for (String result : results) {
                allMovies.add(result);
                stringBuilder.append(result).append("\n");
            }
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
        }
    }

    public void createNewShowDialog() {
        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        EditText popUpTitle, popUpGenre, popUpDescription, popUpReleaseDate;
        Button popUpSave, popUpCancel;
        dialogBuilder = new AlertDialog.Builder(getActivity());
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
                new QueryTask(listView).execute("INSERT INTO all_movies " +
                        "(id, title, genre, release_date, description) VALUES" + "(" + id +
                        ", " + "'" + popUpTitle.getText().toString() + "'" + ", " + "'" +
                        popUpGenre.getText().toString() +
                        "'" + ", " + "'" + popUpReleaseDate.getText().toString() + "'" + ", " + "'" +
                        popUpDescription.getText().toString() + "'" +
                        ")");
                System.out.println("Inserted successfully");

                id++;
                allMovies.add(popUpTitle.getText().toString() + popUpReleaseDate.getText().toString());

                adapter.notifyDataSetChanged();
                //refreshData();
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

   /* @Override
    public void onPause() {
        super.onPause();
        allMovies.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        new QueryTask(listView).execute("SELECT * FROM all_movies");
    }*/

}