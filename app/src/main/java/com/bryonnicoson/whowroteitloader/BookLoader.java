package com.bryonnicoson.whowroteitloader;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by bryon on 3/8/18.
 */

public class BookLoader extends AsyncTaskLoader {

    private String mQueryString;

    public BookLoader(Context context, String mQueryString) {
        super(context);
        this.mQueryString = mQueryString;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        return NetWorkUtils.getBookInfo(mQueryString);
    }
}
