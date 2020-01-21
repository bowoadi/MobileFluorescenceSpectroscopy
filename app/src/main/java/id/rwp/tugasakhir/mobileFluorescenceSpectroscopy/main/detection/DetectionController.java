package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.detection;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectioncamera.DetectionSurfaceView;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectioncamera.CameraSettings;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectioncamera.DetectionHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectioncamera.DetectionListener;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DotLocation;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.Dots;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.MoflusSQLiteHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.SQLiteAsyncTask;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DataResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DetectionMethodColor;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.GraphData;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.MicroRNA;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.BitmapHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.MoFluSPermission;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.detection.adapter.DetectionMethodColorAdapter;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.detection.adapter.DotsAdapter;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.detection.adapter.GraphDataAdapter;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.utils.MoflusSharedPreferenceHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.R;

/**
 * Kelas yang mengontrol tampilan untuk detection
 */
public class DetectionController extends AppCompatActivity implements View.OnClickListener, RangeSeekBar.OnRangeSeekBarChangeListener, DetectionListener, GraphDataAdapter.Listener, DetectionMethodColorAdapter.OnColorDataListener, View.OnLongClickListener {

    //View
    private RecyclerView recyclerview;
    private RangeSeekBar pixel_size_seekbar;
    private Spinner detection_method_color_spinner;
    private EditText time_interval_edittext;
    private EditText time_duration_edittext;
    private ImageButton detection_process_button;
    private ImageButton cancel_button;
    private FrameLayout camera_framelayout;
    private TextView countdown_textview;
    private RelativeLayout detection_progress_process_button_parentview;
    private ImageView pixel_location_imageview;
    //Var
    private GraphDataAdapter graphDataAdapter;
    private MoflusSQLiteHelper moflusSQLiteHelper;
    private DetectionHelper detectionHelper;
    private List<DetectionMethodColor> detectionMethodColorList;
    private DataResult dataResult;
    private DetectionSurfaceView detectionSurfaceView;

    /**
     * Fungsi yang pertama kali dijalankan saat detection view ditampilkan
     * @param savedInstanceState berisi data-data yang disimpan dalam bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!MoFluSPermission.requestCamera(this)){
            return;
        }
        moflusSQLiteHelper = new MoflusSQLiteHelper(this);
        setContentView(R.layout.detection_activity);
        viewSetup();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * Fungsi yang dijalankan untuk membentuk view pada detection
     */
    private void viewSetup() {
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Pixel Location ImageView
        pixel_location_imageview = (ImageView) findViewById(R.id.pixel_location_imageview);
//        pixel_location_imageview.setOnClickListener(this);
//        pixel_location_imageview.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                Matrix invertMatrix = new Matrix();
//                DotLocation dotLocation = new DotLocation();
//                ((ImageView) view).getImageMatrix().invert(invertMatrix);
//                float[] eventXY = new float[]{motionEvent.getX(), motionEvent.getY()};
//                invertMatrix.mapPoints(eventXY);
//                int x = (int) eventXY[0];
//                int y = (int) eventXY[1];
//                if (x < 0) {
//                    dotLocation.setX(0);
//                } else if (x > CameraSettings.PREVIEW_WIDTH - 1) {
//                    dotLocation.setX(CameraSettings.PREVIEW_WIDTH - 1);
//                } else if (y < 0) {
//                    dotLocation.setY(0);
//                } else if (y > CameraSettings.PREVIEW_HEIGHT - 1) {
//                    dotLocation.setY(CameraSettings.PREVIEW_HEIGHT - 1);
//                } else {
//                    dotLocation.setX(x);
//                    dotLocation.setY(y);
//                }
//                view.setTag(dotLocation);
//                return false;
//            }
//        });
        //Process Progress
        detection_progress_process_button_parentview = (RelativeLayout) findViewById(R.id.detection_progress_process_button_parentview);
        cancel_button = (ImageButton) findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(this);
        //Countdown TextView
        countdown_textview = (TextView) findViewById(R.id.countdown_textview);
        //Detection Method Color Spinner
        detection_method_color_spinner = (Spinner) findViewById(R.id.detection_method_color_spinner);
        detectionMethodColorList = moflusSQLiteHelper.getAllDetectionMethodColor();
        int color_type_id = getSharedPreferences(MoflusSharedPreferenceHelper.NAME,MODE_PRIVATE).getInt(MoflusSharedPreferenceHelper.COLOR_TYPE,-1);
        DetectionMethodColor header = new DetectionMethodColor();
        header.setColor_type("Color Type");
        detectionMethodColorList.add(0,header);
        if(color_type_id != -1){
            String[] color_type_id_string = String.valueOf(color_type_id).split("");
            for(String color_type : color_type_id_string){
                if(!color_type.equals("")){
                    detectionMethodColorList.get(Integer.parseInt(color_type)).setSelected(true);
                }
            }
        }
        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, detectionMethodColorStringList);
        DetectionMethodColorAdapter arrayAdapter = new DetectionMethodColorAdapter(this, R.layout.detection_method_color_item, detectionMethodColorList,this);
        detection_method_color_spinner.setAdapter(arrayAdapter);
        //Pixel Size Seekbar
        pixel_size_seekbar = (RangeSeekBar) findViewById(R.id.pixel_size_seekbar);
        pixel_size_seekbar.setSelectedMaxValue(getSharedPreferences(MoflusSharedPreferenceHelper.NAME, MODE_PRIVATE)
                .getInt(MoflusSharedPreferenceHelper.PIXEL_SIZE, 5));
        pixel_size_seekbar.setOnRangeSeekBarChangeListener(this);
        //Time Form
        time_interval_edittext = (EditText) findViewById(R.id.time_interval_edittext);
        time_interval_edittext.setText(getSharedPreferences(MoflusSharedPreferenceHelper.NAME, MODE_PRIVATE)
                .getString(MoflusSharedPreferenceHelper.TIME_INTERVAL, "5000"));
        time_duration_edittext = (EditText) findViewById(R.id.time_duration_edittext);
        time_duration_edittext.setText(getSharedPreferences(MoflusSharedPreferenceHelper.NAME, MODE_PRIVATE)
                .getString(MoflusSharedPreferenceHelper.TIME_DURATION, "360000"));
        //Detection Process Button
        detection_process_button = (ImageButton) findViewById(R.id.detection_process_button);
        detection_process_button.setOnClickListener(this);
        detection_process_button.setOnLongClickListener(this);
        //RecyclerView
        recyclerview = (RecyclerView) findViewById(R.id.graph_data_recyclerview);
        recyclerview.getRecycledViewPool().setMaxRecycledViews(0,100);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(linearLayoutManager);
        //Detection Helper
        detectionHelper = new DetectionHelper(this);
        //Camera Setting
        if (checkCameraHardware()) {
            camera_framelayout = (FrameLayout) findViewById(R.id.camera_framelayout);
            detectionSurfaceView = new DetectionSurfaceView(this, detectionHelper);
//            cameraProcessController.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View view, MotionEvent motionEvent) {
//                    view.invalidate();
//                    return true;
//                }
//            });
            detectionSurfaceView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.invalidate();
                }
            });
            camera_framelayout.addView(detectionSurfaceView);
        }
    }

    /**
     * Fungsi yang menangani reaksi user saat user memberikan Permission terhadap aplikasi
     * @param requestCode code request yang diminta
     * @param permissions permission yang diminta
     * @param grantResults hasil dari permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MoFluSPermission.CAMERA_REQUEST_CODE :
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewSetup();
                } else {
                    Toast.makeText(this, "Please Allow Camera Permission", Toast.LENGTH_SHORT).show();
                    finish();

                }
                break;
        }
    }

    /**
     * Fungsi agar activity laind dapat pindah ke halaman detection
     * @param activity berisi activity dari tampilan yang sedang muncul
     * @param request_code kode request yang diminta
     */
    public static void toDetectionActivity(Activity activity, int request_code) {
        activity.startActivityForResult(new Intent(activity, DetectionController.class), request_code);
    }

    /**
     * Fungsi untuk membuat tampilan menu
     * @param menu menu pada activity detection
     * @return true jika menu dapat ditampilkan, false jika tidak
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detection, menu);
        return true;
    }

    /**
     * Fungsi yang menangani saat menu dipilih
     * @param item menu yang dipilih
     * @return true jika terdapat menu, false jika tidak
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_data_result:
                saveDataResult();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Fungsi yang menangani klik terhadap aplikasi
     * @param view view yang ditekan
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.detection_process_button:
                detectionProcess();
                break;
            case R.id.cancel_button:
                cancelProcess();
                break;
            case R.id.pixel_location_imageview :
                changePixelLocation((DotLocation)view.getTag());
        }
    }

    /**
     * Fungsi yang dipanggil saat user merubah ukuran piksel
     * @param bar view yang dipilih
     * @param minValue nilai terendah
     * @param maxValue nilai tertinggi
     */
    @Override
    public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
        switch (bar.getId()) {
            case R.id.pixel_size_seekbar:
                detectionHelper.setPixel_size((int)maxValue);
                getSharedPreferences(MoflusSharedPreferenceHelper.NAME, MODE_PRIVATE)
                        .edit()
                        .putInt(MoflusSharedPreferenceHelper.PIXEL_SIZE, (int) maxValue)
                        .apply();
                break;
        }
    }

    /**
     * Fungsi yang mengecek tipe warna ada yang dipilih
     * @return true jika terdapat warna yang dipilih, false jika tidak ada
     */
    private boolean checkColorType(){
        for(DetectionMethodColor detectionMethodColor : detectionMethodColorList){
            if(detectionMethodColor.isSelected()) return true;
        }
        return false;
    }

    /**
     * Fungsi untuk memulai proses deteksi
     */
    private void detectionProcess() {
        if(!detectionFormCheck()){
            return;
        }
        final int start_delay = getSharedPreferences(MoflusSharedPreferenceHelper.NAME, MODE_PRIVATE).getInt(MoflusSharedPreferenceHelper.START_DELAY, 5);
        long interval = Long.parseLong(time_interval_edittext.getText().toString());
        long duration = Long.parseLong(time_duration_edittext.getText().toString());
        int pixel_size = Integer.parseInt(pixel_size_seekbar.getSelectedMaxValue().toString());
        dataResult = new DataResult();
        graphDataAdapter = new GraphDataAdapter(this, dataResult.getGraphDatas(),duration, this);
        recyclerview.setAdapter(graphDataAdapter);
        getSharedPreferences(MoflusSharedPreferenceHelper.NAME,MODE_PRIVATE)
                .edit()
                .putString(MoflusSharedPreferenceHelper.TIME_DURATION,String.valueOf(duration))
                .putString(MoflusSharedPreferenceHelper.TIME_INTERVAL,String.valueOf(interval))
                .apply();
        detectionHelper.setInterval(interval);
        detectionHelper.setDuration(duration);
        detectionHelper.setPixel_size(pixel_size);
        getSharedPreferences(MoflusSharedPreferenceHelper.NAME, MODE_PRIVATE)
                .edit()
                .putString(MoflusSharedPreferenceHelper.TIME_INTERVAL, "" + interval)
                .apply();
        getSharedPreferences(MoflusSharedPreferenceHelper.NAME, MODE_PRIVATE)
                .edit()
                .putString(MoflusSharedPreferenceHelper.TIME_DURATION, "" + duration)
                .apply();
        final ArrayList<GraphData> graphDatas = new ArrayList<>();
        int p = 0;
        for (MicroRNA microRNA : moflusSQLiteHelper.getAllUsedMicroRNA()) {
            GraphData graphData = new GraphData();
            graphData.setId(microRNA.getId());
            graphData.setMicroRNA(microRNA);
            if(p++ == 0){
                graphData.setSelected_graph_data(true);
            }
            graphDatas.add(graphData);
        }
        ArrayList<DetectionMethodColor> detectionMethodColors = new ArrayList<>();
        for(DetectionMethodColor detectionMethodColor : detectionMethodColorList){
            if(detectionMethodColor.isSelected()){
                detectionMethodColors.add(detectionMethodColor);
            }
        }
        dataResult.setDetectionMethodColors(detectionMethodColors);
        dataResult.setDatetime(new Date());
        dataResult.setPreview_picture(BitmapHelper.getBitmap(detectionHelper.getPreview()));
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setPositiveButton("Start", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        View view = LayoutInflater.from(this).inflate(R.layout.preview_dialog, null);
        final TextView textView = (TextView) view.findViewById(R.id.microrna_name);
        textView.setText(graphDatas.get(0).getMicroRNA().getName());
        final ArrayList<DotLocation> dotLocations = new ArrayList<>();
        final DotLocation dotLocation = new DotLocation();
        final ImageView imageView = (ImageView) view.findViewById(R.id.image_preview);
        imageView.setTag(0);
        imageView.setImageBitmap(BitmapHelper.getBitmapFromNV21(detectionHelper.getPreview()));
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Matrix invertMatrix = new Matrix();
                ((ImageView) view).getImageMatrix().invert(invertMatrix);
                float[] eventXY = new float[]{motionEvent.getX(), motionEvent.getY()};
                invertMatrix.mapPoints(eventXY);
                int x = (int) eventXY[0];
                int y = (int) eventXY[1];
                dotLocation.setX(x);
                dotLocation.setY(y);
                if (x < 0) {
                    dotLocation.setX(0);
                } else if (x > CameraSettings.PREVIEW_WIDTH - 1) {
                    dotLocation.setX(CameraSettings.PREVIEW_WIDTH - 1);
                }
                if (y < 0) {
                    dotLocation.setY(0);
                } else if (y > CameraSettings.PREVIEW_HEIGHT - 1) {
                    dotLocation.setY(CameraSettings.PREVIEW_HEIGHT - 1);
                }
                return false;
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int j = ((int) view.getTag()) + 1;
                if (j <= graphDatas.size()) {
                    view.setTag(j);
                    //paint
                    ImageView preview = (ImageView) view;
                    Bitmap newBitmap = ((BitmapDrawable) preview.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
                    Canvas canvas = new Canvas(newBitmap);
                    Paint paint = new Paint();
                    paint.setColor(Color.RED);
                    canvas.drawBitmap(newBitmap, new Matrix(), null);
                    int radius = CameraSettings.PREVIEW_HEIGHT/100;
                    canvas.drawCircle(dotLocation.getX(), dotLocation.getY(), radius, paint);
                    imageView.setImageBitmap(newBitmap);
                    MicroRNA microRNA = graphDatas.get(j-1).getMicroRNA();
                    DotLocation newDotLocation = new DotLocation();
                    newDotLocation.setMicroRNA(microRNA);
                    newDotLocation.setX(dotLocation.getX());
                    newDotLocation.setY(dotLocation.getY());
                    dotLocations.add(newDotLocation);
                    if (j == graphDatas.size()) {
                        textView.setText("Finish");
                        detectionHelper.setDotLocations(dotLocations);
                    }else{
                        textView.setText(graphDatas.get(j).getMicroRNA().getName());
                    }
                }
            }
        });
        alertDialog.setView(view);
        //Create Save Detection Location
        alertDialog.setNeutralButton("Browse Dots", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.setNegativeButton("Save Dots", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        //
        final AlertDialog dialog = alertDialog.create();
        dialog.show();
        final Context context = this;
        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialogDots = new AlertDialog.Builder(context);
                alertDialogDots.setTitle("Select Dots");
                View dotsView = LayoutInflater.from(context).inflate(R.layout.dots_list_dialog,null,false);
                RecyclerView recyclerView = (RecyclerView) dotsView.findViewById(R.id.dots_recyclerview);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(linearLayoutManager);
                ArrayList<Dots> dotses = moflusSQLiteHelper.getFullDots();
                alertDialogDots.setView(dotsView);
                final AlertDialog newDialog = alertDialogDots.create();
                DotsAdapter dotsAdapter = new DotsAdapter(context, dotses, new DotsAdapter.OnDotsClickListener() {
                    @Override
                    public void onClick(Dots dots) {
                        int i = ((int)imageView.getTag());
                        int j = i+dots.getDotLocations().size();
                        if (j <= graphDatas.size()) {
                            imageView.setTag(j);
                            //paint
                            Bitmap newBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
                            Canvas canvas = new Canvas(newBitmap);
                            Paint paint = new Paint();
                            paint.setColor(Color.RED);
                            canvas.drawBitmap(newBitmap, new Matrix(), null);
                            int radius = CameraSettings.PREVIEW_HEIGHT/100;
                            for(DotLocation dotLocation1 : dots.getDotLocations()){
                                canvas.drawCircle(dotLocation1.getX(), dotLocation1.getY(), radius, paint);
                                MicroRNA microRNA = graphDatas.get(i++).getMicroRNA();
                                DotLocation newDotLocation = new DotLocation();
                                newDotLocation.setMicroRNA(microRNA);
                                newDotLocation.setX(dotLocation1.getX());
                                newDotLocation.setY(dotLocation1.getY());
                                dotLocations.add(newDotLocation);
                            }
                            imageView.setImageBitmap(newBitmap);
                            if (j == graphDatas.size()) {
                                textView.setText("Finish");
                                detectionHelper.setDotLocations(dotLocations);
                                newDialog.dismiss();
                            }else{
                                textView.setText(graphDatas.get(j).getMicroRNA().getName());
                            }
                        }else{
                            Toast.makeText(context, "Too Much Dots", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                recyclerView.setAdapter(dotsAdapter);
                newDialog.show();
            }
        });
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((int)imageView.getTag()) == graphDatas.size() ){
                    AlertDialog.Builder alertDialogDotLocation = new AlertDialog.Builder(context);
                    View nameView = LayoutInflater.from(context).inflate(R.layout.dots_name_dialog,null,false);
                    final EditText editText = (EditText) nameView.findViewById(R.id.dots_name);
                    alertDialogDotLocation.setView(nameView);
                    alertDialogDotLocation.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String name = editText.getText().toString();
                            Dots dots = new Dots();
                            dots.setName(name);
                            dots.setDotLocations(dotLocations);
                            dots.setScale(0);
                            moflusSQLiteHelper.addDots(dots);
                            Toast.makeText(context, "Dots Saved", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialogDotLocation.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialogDotLocation.show();
                }else{
                    Toast.makeText(DetectionController.this, "Please Dot All Samples", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((int) imageView.getTag()) == graphDatas.size()) {
                    dataResult.getGraphDatas().clear();
                    dataResult.getGraphDatas().addAll(graphDatas);
                    detectionHelper.setDataResult(dataResult,getApplicationContext());
                    detectionSurfaceView.startDetection(countdown_textview, start_delay);
                    enableProcessProgress();
                    pixel_location_imageview.setImageBitmap(BitmapHelper.getBitmapPixelLocation(dotLocations));
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * Fungsi untuk mengecek apakah minimum kebutuhan hardware terpenuhi
     * @return true jika ya, false jika tidak
     */
    private boolean checkCameraHardware() {
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * Fungsi untuk menyimpan DataResult
     */
    private void saveDataResult(){
        if(dataResult == null ){
            Toast.makeText(this,"Please Start Detection",Toast.LENGTH_SHORT).show();
            return;
        }else if(dataResult.getGraphDatas().get(0).getGraphDataValues().size() == 0){
            Toast.makeText(this, "No Value Detected", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.data_result_name_dialog,null,false);
        final EditText data_result_name = (EditText) view.findViewById(R.id.data_result_name);
        final Context context = this;
        alertDialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = data_result_name.getText().toString();
                if(name.equals("")){
                    dataResult.setName("No Name");
                }else{
                    dataResult.setName(name);
                }
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.dataresult_save_loading_view,null,false);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setView(view);
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
                dataResult.setPreview_picture(Bitmap.createScaledBitmap(dataResult.getPreview_picture(),400,225,true));
                new SQLiteAsyncTask(context,moflusSQLiteHelper,dataResult,alertDialog).execute();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.setView(view);
        alertDialogBuilder.show();
    }

    /**
     * Fungsi untuk membatalkan proses deteksi
     */
    private void cancelProcess() {
        disableProcessProgress();
        detectionHelper.stopDelayText();
        if (detectionHelper.isProcess()) {
            detectionHelper.finishProcess();
        }
    }

    /**
     * Fungsi untuk merubah tampilan saat process dilakukan
     */
    private void enableProcessProgress() {
        time_duration_edittext.setEnabled(false);
        time_interval_edittext.setEnabled(false);
        detection_method_color_spinner.setEnabled(false);
        detection_progress_process_button_parentview.setVisibility(View.VISIBLE);
        detection_process_button.setVisibility(View.GONE);
    }

    /**
     * Fungsi untuk merubah tampilan saat process tidak dilakukan
     */
    private void disableProcessProgress() {
        time_duration_edittext.setEnabled(true);
        time_interval_edittext.setEnabled(true);
        detection_method_color_spinner.setEnabled(true);
        detection_progress_process_button_parentview.setVisibility(View.GONE);
        detection_process_button.setVisibility(View.VISIBLE);
    }

    /**
     * Fungsi yang dilakukan saat lokasi pixel yang dideteksi dipindah
     * @param newDotLocation
     */
    private void changePixelLocation(DotLocation newDotLocation) {
        try{
            DotLocation dotLocation = detectionHelper.getDotLocations().get(graphDataAdapter.getSelected_graphview());
            dotLocation.setX(newDotLocation.getX());
            dotLocation.setY(newDotLocation.getY());
            pixel_location_imageview.setImageBitmap(BitmapHelper.getBitmapPixelLocation(detectionHelper.getDotLocations()));
        }catch (NullPointerException e){

        }
    }

    /**
     * Fungsi yang dipanggil saat data yang terdapat pada dataresult berubah
     * @param dataResult
     */
    @Override
    public void dataChange(DataResult dataResult) {
        graphDataAdapter.notifyDataSetChanged();
    }

    /**
     * Fungsi yang dipanggil saat proses telah selesai
     * @param dataResult dataResult yang dihasilkan
     */
    @Override
    public void finishProcess(DataResult dataResult) {
        disableProcessProgress();
    }

    /**
     * Mengecek apakah form deteksi telah terpenuhi
     * @return true jika ya, false jika tidak
     */
    private boolean detectionFormCheck(){
        if(moflusSQLiteHelper.getAllUsedMicroRNA().size() < 1){
            Toast.makeText(this,"Please Select Samples in Setting Menu",Toast.LENGTH_SHORT).show();
            return false;
        }else if(detection_method_color_spinner.getSelectedItemPosition()<0){
            Toast.makeText(this,"Please Update Your Data At Setting Menu",Toast.LENGTH_SHORT).show();
            return false;
        }else if(Integer.parseInt(time_interval_edittext.getText().toString()) > Integer.parseInt(time_duration_edittext.getText().toString()) ){
            Toast.makeText(this,"Interval More Tha Duration",Toast.LENGTH_SHORT).show();
            return false;
        }else if(!checkColorType()){
            Toast.makeText(this, "Please Select Select At Least 1 Color Type", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Fungsi yang dipanggil saat microRNA atau Sample yang dipilih berubah
     * @param position posisi sample ata microrna yang dipilih
     */
    @Override
    public void onChangeSelected(int position) {
        detectionSurfaceView.setSelectedMicroRNAPosition(position);
    }

    /**
     * Fungsi saat tipe warna yang digunakan berubah
     * @param id id tipe warna
     */
    @Override
    public void onColorDataChange(int id) {
        getSharedPreferences(MoflusSharedPreferenceHelper.NAME,MODE_PRIVATE).edit()
                .putInt(MoflusSharedPreferenceHelper.COLOR_TYPE,id)
                .apply();
    }

    /**
     * Fungsi yang dipanggil saat tampilan ditekan lama
     * @param view tampilan yang ditekan lama
     * @return true jika terdapat tampilan yang ditekan, false jika tidak
     */
    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()){
            case R.id.detection_process_button :
                detectionSurfaceView.startAutoFocus();
                break;
        }
        return true;
    }
}
