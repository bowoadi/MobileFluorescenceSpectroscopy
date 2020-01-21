package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.detaileddataresult;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.classificationserver.ClassificationListener;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.ClassificationMethod;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.classificationserver.ClassificationRequest;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.CloudComputingResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.MoflusSQLiteHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.utils.MoflusSharedPreferenceHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DataResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.GraphData;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.GraphDataValue;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.detaileddataresult.adapter.CloudComputingResultAdapter;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.R;

/**
 * Kelas yang mengontrol tampilan untuk detaildataresult
 */
public class DetailedDataResultController extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerview;
    private ImageView preview_picture;
    private TextView detection_method_color_textview;
    private Button process_button;
    private GraphView graphview;
    private TextView clear_all_textview;
    private LinearLayout linear_layout;
    private Spinner classification_method_spinner;
    private CloudComputingResultAdapter cloudComputingResultAdapter;
    private MoflusSQLiteHelper moflusSQLiteHelper;
    private DataResult dataResult;
    private RequestQueue requestQueue;
    private ArrayList<CloudComputingResult> listCloudComputingResult;

    /**
     * Fungsi yang pertama kali dijalankan saat detaileddataresult view ditampilkan
     * @param savedInstanceState berisi data-data yang disimpan dalam bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestQueue = Volley.newRequestQueue(this);
        moflusSQLiteHelper = new MoflusSQLiteHelper(this);
        setContentView(R.layout.detailed_data_result_activity);
        viewSetup();
    }

    /**
     * Fungsi yang dijalankan untuk membentuk view pada detaileddataresult
     */
    private void viewSetup() {
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int id = getIntent().getExtras().getInt("data_result_id");
        dataResult = moflusSQLiteHelper.getDataResult(id);
        getSupportActionBar().setTitle(dataResult.getName());
        //Linear Layout
        linear_layout = (LinearLayout) findViewById(R.id.linear_layout);
        //GraphView
        for (GraphData graphData : dataResult.getGraphDatas()) {
            View view = LayoutInflater.from(this).inflate(R.layout.graphview, null);
            GraphView graphView = (GraphView) view.findViewById(R.id.graphview);
            graphView.setTitle(graphData.getMicroRNA().getName());
            LineGraphSeries<GraphDataValue> lineGraphSeries = new LineGraphSeries<>();
            lineGraphSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Toast.makeText(getApplicationContext(), "Time = " + dataPoint.getX() + "\nValue = " + dataPoint.getY(), Toast.LENGTH_SHORT).show();
                }
            });
            for (GraphDataValue graphDataValue : graphData.getGraphDataValues()) {
                lineGraphSeries.appendData(graphDataValue, false, graphData.getGraphDataValues().size());
            }
            graphData.setLineGraphSeries(lineGraphSeries);
            graphData.setGraphView(graphView, (int) lineGraphSeries.getHighestValueX());
            linear_layout.addView(view);
        }
        //Detection Method Color TextView
        detection_method_color_textview = (TextView) findViewById(R.id.detection_method_color_textview);
        detection_method_color_textview.setText(dataResult.getDetectionColorMethodText());
        //Preview Picture
        preview_picture = (ImageView) findViewById(R.id.preview_picture);
        if(dataResult.getPreview_picture() == null){
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            requestOptions.dontAnimate();
            requestOptions.encodeQuality(50);
            requestOptions.error(R.drawable.error_placeholder);
            requestOptions.placeholder(R.drawable.loading_placeholder);
            Glide.with(this).load(dataResult.getPreview_picture_url()).apply(requestOptions).into(preview_picture);
        }else{
            preview_picture.setImageBitmap(dataResult.getPreview_picture());
        }
        //Clear All TextView
        clear_all_textview = (TextView) findViewById(R.id.clear_all_textview);
        clear_all_textview.setOnClickListener(this);
        //Process Button
        process_button = (Button) findViewById(R.id.process_button);
        process_button.setOnClickListener(this);
        //RecyclerView
        recyclerview = (RecyclerView) findViewById(R.id.result_recycleview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerview.setLayoutManager(linearLayoutManager);
        //Classification Method Spinner
        classification_method_spinner = (Spinner) findViewById(R.id.classification_method_spinner);
        List<ClassificationMethod> classificationMethodList = moflusSQLiteHelper.getAllClassificationMethod();
        List<String> classificationMethodStringList = new ArrayList<>();
        for (ClassificationMethod classificationMethod : classificationMethodList) {
            classificationMethodStringList.add(classificationMethod.getName());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, classificationMethodStringList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classification_method_spinner.setAdapter(arrayAdapter);

        //Dummy
        listCloudComputingResult = moflusSQLiteHelper.getAllCloudComputingResult(dataResult.getId());
        cloudComputingResultAdapter = new CloudComputingResultAdapter(this, listCloudComputingResult);
        recyclerview.setAdapter(cloudComputingResultAdapter);
    }

    /**
     * Fungsi agar activity lain dapat pindah ke halaman detaileddataresult
     * @param context berisi context dari tampilan yang sedang muncul
     * @param dataResult berisi dataresult yang akan ditampilkan detailnya
     */
    public static void toDetailedDataResultsActivity(Context context, DataResult dataResult) {
        Intent intent = new Intent(context, DetailedDataResultController.class);
        intent.putExtra("data_result_id", dataResult.getId());
        context.startActivity(intent);
    }

    /**
     * Fungsi yang menangani klik terhadap aplikasi
     * @param view view yang ditekan
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.process_button:
                cloudComputingProcess();
                break;
            case R.id.clear_all_textview:
                clearAllCloudComputing();
                break;
        }
    }

    /**
     * Fungsi untuk menghapus semua hasil cloud computing
     */
    private void clearAllCloudComputing() {
        moflusSQLiteHelper.deleteCloudComputingResult();
        listCloudComputingResult.clear();
        cloudComputingResultAdapter.notifyDataSetChanged();
    }

    /**
     * Fungsi untuk memulai proses cloud computing
     */
    private void cloudComputingProcess() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.cloud_computing_dataset_preview, null);
        final EditText start_time_edittext = (EditText) view.findViewById(R.id.start_time_edittext);
        start_time_edittext.setInputType(InputType.TYPE_NULL);
        final EditText to_time_edittext = (EditText) view.findViewById(R.id.to_time_edittext);
        to_time_edittext.setInputType(InputType.TYPE_NULL);
        //OnClickListener
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.start_time_edittext:
                        to_time_edittext.clearFocus();
                        break;
                    case R.id.to_time_edittext:
                        to_time_edittext.setTypeface(Typeface.DEFAULT_BOLD);
                        start_time_edittext.clearFocus();
                        start_time_edittext.setTypeface(Typeface.DEFAULT);
                        break;
                }
            }
        };
        start_time_edittext.setText("0");
        ArrayList<GraphData> graphDatas = dataResult.getGraphDatas();
        ArrayList<GraphDataValue> graphDataValues = graphDatas.get(graphDatas.size() - 1).getGraphDataValues();
        to_time_edittext.setText(String.valueOf(graphDataValues.get(graphDataValues.size() - 1).getX()));
        start_time_edittext.setOnClickListener(onClickListener);
        start_time_edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    start_time_edittext.setTypeface(Typeface.DEFAULT_BOLD);
                    to_time_edittext.setTypeface(Typeface.DEFAULT);
                }
            }
        });
        to_time_edittext.setOnClickListener(onClickListener);
        to_time_edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    to_time_edittext.setTypeface(Typeface.DEFAULT_BOLD);
                    start_time_edittext.setTypeface(Typeface.DEFAULT);
                }
            }
        });
        final double time[] = {0, graphDataValues.get(graphDataValues.size() - 1).getX()};
        //GraphView
        GraphView graphview = (GraphView) view.findViewById(R.id.graphview);
        GraphData graphData = dataResult.getGraphDatas().get(0);
        LineGraphSeries<GraphDataValue> lineGraphSeries = new LineGraphSeries<>();
        lineGraphSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                if (start_time_edittext.hasFocus()) {
                    time[0] = dataPoint.getX();
                    start_time_edittext.setText(String.valueOf(time[0]));
                } else if (to_time_edittext.hasFocus()) {
                    time[1] = dataPoint.getX();
                    to_time_edittext.setText(String.valueOf(time[1]));
                }
            }
        });
        for (GraphDataValue graphDataValue : graphData.getGraphDataValues()) {
            lineGraphSeries.appendData(graphDataValue, false, graphData.getGraphDataValues().size());
        }
        graphData.setLineGraphSeries(lineGraphSeries);
        graphData.setGraphView(graphview, (int) lineGraphSeries.getHighestValueX());

        alertDialog.setView(view);
        alertDialog.setPositiveButton("Process", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (time[0] < 0) {
                    Toast.makeText(getApplicationContext(), "Please Select Start Time", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (time[1] < 0) {
                    Toast.makeText(getApplicationContext(), "Please Select To Time", Toast.LENGTH_SHORT).show();
                    return;
                }
                sentCloudComputingData(dataResult, time);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.show();
    }

    /**
     * Proses untuk mengirim dataresult ke server untuk dilakukan klasifikasi
     * @param dataResult dataresult yang akan dikirim
     * @param time waktu yang digunakan
     */
    private void sentCloudComputingData(final DataResult dataResult, final double[] time) {
        String url = "http://172.104.188.209/classify/"+ MoflusSharedPreferenceHelper.getUser(this).getUid();
        final CloudComputingResult cloudComputingResult = new CloudComputingResult();
        final ClassificationMethod classificationMethod = moflusSQLiteHelper.getClassificationMethod(classification_method_spinner.getSelectedItemPosition() + 1);
        ClassificationRequest classificationRequest = new ClassificationRequest(this, url, dataResult, classificationMethod, new ClassificationListener() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    if(jsonObject.isNull("error")){
                        String result_text = jsonObject.getJSONObject("hasil_klasifikasi").getString("hasil");
                        cloudComputingResult.setResult(result_text);
                        cloudComputingResult.setProcess(1);
                        cloudComputingResult.setDatetime(new Date());
                        moflusSQLiteHelper.updateCloudComputingResult(cloudComputingResult);
                        cloudComputingResultAdapter.notifyDataSetChanged();
                    }else{
                        String result_text = jsonObject.getString("error");
                        cloudComputingResult.setResult(result_text);
                        cloudComputingResult.setProcess(-1);
                        cloudComputingResult.setDatetime(new Date());
                        moflusSQLiteHelper.updateCloudComputingResult(cloudComputingResult);
                        cloudComputingResultAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    cloudComputingResult.setResult("Error");
                    cloudComputingResult.setProcess(-1);
                    cloudComputingResult.setDatetime(new Date());
                    moflusSQLiteHelper.updateCloudComputingResult(cloudComputingResult);
                    cloudComputingResultAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailed(String error) {
                cloudComputingResult.setResult("Can't Connect to Server");
                cloudComputingResult.setProcess(-1);
                moflusSQLiteHelper.updateCloudComputingResult(cloudComputingResult);
                cloudComputingResultAdapter.notifyDataSetChanged();
            }
        });
        cloudComputingResult.setClassificationMethod(classificationMethod);
        cloudComputingResult.setResult("Waiting...");
        cloudComputingResult.setDatetime(new Date());
        cloudComputingResult.setProcess(0);
        moflusSQLiteHelper.addCloudComputingResult(cloudComputingResult, dataResult.getId());
        listCloudComputingResult.add(cloudComputingResult);
        cloudComputingResultAdapter.notifyDataSetChanged();
        recyclerview.scrollToPosition(listCloudComputingResult.size() - 1);
        classificationRequest.setDataResultFromToTime(time[0],time[1]);
        classificationRequest.startClassification();
    }
}
