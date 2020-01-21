package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.io.IOException;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.MoflusSQLiteHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DataResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.CSVHelper;

/**
 * Created by ranuwp on 3/17/2017.
 */

public class SQLiteAsyncTask extends AsyncTask {

    private Context context;
    private MoflusSQLiteHelper moflusSQLiteHelper;
    private DataResult dataResult;
    private AlertDialog alertDialog;

    public SQLiteAsyncTask(Context context, MoflusSQLiteHelper moflusSQLiteHelper, DataResult dataResult, AlertDialog alertDialog) {
        this.context = context;
        this.moflusSQLiteHelper = moflusSQLiteHelper;
        this.dataResult = dataResult;
        this.alertDialog = alertDialog;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        moflusSQLiteHelper.addDataResult(dataResult);
        try {
            CSVHelper.createFile(dataResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        alertDialog.dismiss();
        Toast.makeText(context,"Data Result Saved",Toast.LENGTH_LONG).show();
    }
}
