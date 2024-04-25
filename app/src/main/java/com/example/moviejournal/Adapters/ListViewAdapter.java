package com.example.moviejournal.Adapters;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.moviejournal.R;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ListViewAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> titles;

    private static final String TAG = "MainActivity";
    private static final String SERVER_IP = "";
    private static final int SERVER_PORT = 6000;

    public ListViewAdapter(Activity context, ArrayList<String> title) {
        super(context, R.layout.list_item, title);
        this.context = context;
        this.titles = title;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_item, null, true);

        TextView titleText = rowView.findViewById(R.id.list_item_text);
        titleText.setText(titles.get(position));


        Button button = rowView.findViewById(R.id.item_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titles.get(position);
                String word = "Year: ";
                int index = title.indexOf(word);
                String s = title.substring(0, index).trim();

                new DeleteTask().execute(s);

                titles.remove(position);
                notifyDataSetChanged();
            }
        });
        return rowView;
    }
    public class DeleteTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... ids) {
            try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                String query = "DELETE FROM to_watch WHERE title='" + ids[0]+"'";
                objectOutputStream.writeObject(query);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

