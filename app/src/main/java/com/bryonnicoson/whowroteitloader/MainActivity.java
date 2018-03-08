package com.bryonnicoson.whowroteitloader;

import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String>{

    private EditText mBookInput;
    private TextView mTitleText;
    private TextView mAuthorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBookInput = findViewById(R.id.bookInput);
        mTitleText = findViewById(R.id.titleText);
        mAuthorText = findViewById(R.id.authorText);

        // if loader exists, reconnect it
        if(getSupportLoaderManager().getLoader(0)!=null)
            getSupportLoaderManager().initLoader(0,null,this);
    }

    public void searchBooks(View view) {
        // hide the keyboard
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        // get network state
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        String queryString = mBookInput.getText().toString();
        if (queryString.length() == 0) {
            mTitleText.setText(R.string.empty_query_prompt);
            mAuthorText.setText("");
        } else {
            if (networkInfo != null && networkInfo.isConnected()) {
                Bundle queryBundle = new Bundle();
                queryBundle.putString("queryString", queryString);
                getSupportLoaderManager().restartLoader(0, queryBundle, this);
                //new FetchBook(mTitleText, mAuthorText).execute(queryString);
                mTitleText.setText(R.string.loading);
                mAuthorText.setText("");
            } else {
                mTitleText.setText(R.string.network_error_prompt);
                mAuthorText.setText("");
            }
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new BookLoader(this, args.getString("queryString"));
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray itemsArray = jsonObject.getJSONArray("items");
            //iterate through the results
            for (int i = 0; i < itemsArray.length(); i++){
                JSONObject book = itemsArray.getJSONObject(i);
                String title = null;
                String authors = null;
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                try {
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (title != null && authors != null) {
                    mTitleText.setText(title);
                    mAuthorText.setText(authors);
                    return;
                }
            }
        } catch (JSONException e){
            mTitleText.setText("No Results Found");
            mAuthorText.setText("");
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
