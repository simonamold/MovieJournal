package com.example.moviejournal.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.moviejournal.Activities.DetailsActivity;
import com.example.moviejournal.Adapters.ListViewAdapter;
import com.example.moviejournal.R;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ToWatchFragment extends Fragment {

    private ListView listView;
    private ListViewAdapter adapter;
    ArrayList<String> toWatch = new ArrayList<>();
    private static final String TAG = "MainActivity";

    private static final String SERVER_IP = "";
    private static final int SERVER_PORT = 6000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_to_watch, container, false);

        listView = view.findViewById(R.id.listView);
        adapter = new ListViewAdapter(getActivity(), toWatch);

        if (listView != null) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    String title = (String) adapterView.getItemAtPosition(i);
                    String word = "Year: ";
                    int index = title.indexOf(word);
                    System.out.println(title.substring(0, index));
                    intent.putExtra("title", title);
                    startActivity(intent);
                }
            });
        }
        List<String> queriesList = new ArrayList<>();
        queriesList.add("Select * from to_watch");
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (toWatch.contains(s)){
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
        new QueryTask(listView).execute(queriesList);

        return view;
    }

    private class QueryTask extends AsyncTask<List<String>, Void, List<ArrayList<String>>> {

        private ListView listView;

        public QueryTask(ListView listView){
            this.listView=listView;
        }

        @Override
        protected List<ArrayList<String>> doInBackground(List<String>... queriesList) {
            List<ArrayList<String>> resultsList = new ArrayList<>();

            try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                for (String query : queriesList[0]) {
                    objectOutputStream.writeObject(query);

                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                    ArrayList<String> results = (ArrayList<String>) objectInputStream.readObject();
                    Log.d(TAG, "Received " + results.size() + " results from server.");
                    resultsList.add(results);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return resultsList;
        }


        @Override
        protected void onPostExecute(List<ArrayList<String>> resultsList) {

            if (resultsList == null) {
                return;
            }

            StringBuilder stringBuilder = new StringBuilder();

            for (ArrayList<String> results : resultsList) {
                for (String result : results) {
                    toWatch.add(result);
                    stringBuilder.append(result).append("\n");
                }
            }
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
        }
    }

}