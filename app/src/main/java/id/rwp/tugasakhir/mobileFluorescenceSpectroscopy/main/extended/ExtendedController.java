package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.extended;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.ClassificationMethod;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DotLocation;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.Dots;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.MoflusSQLiteHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage.utils.MoflusSharedPreferenceHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DetectionMethodColor;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.GraphData;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.MicroRNA;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.detection.adapter.DetectionMethodColorAdapter;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.detection.adapter.DotsAdapter;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.home.adapter.FolderAdapter;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.Folder;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.FolderUtil;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.main.extendeddetail.ExtendedDetailController;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.R;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.MoFluSService;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.ImageUploader;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.Status;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.User;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.dataset.DataSet;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.dataset.FolderDataSet;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.testing.TestingResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.databinding.DatasetAdditionalLayoutBinding;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.databinding.ExtendedActivityBinding;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.databinding.ExtendedContentBinding;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.databinding.FolderNameDialogBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Kelas Cadangan Yang Tidak Digunakan untuk tampilan extended
 */
public class ExtendedController extends AppCompatActivity implements FolderAdapter.Listener, View.OnClickListener, DetectionMethodColorAdapter.OnColorDataListener, RangeSeekBar.OnRangeSeekBarChangeListener {

    private ExtendedActivityBinding extendedActivityBinding;
    private ExtendedContentBinding extendedContentBinding;
    private FolderAdapter adapter;
    private List<Folder> folders;
    //Training
    private MoflusSQLiteHelper moflusSQLiteHelper;
    private List<DetectionMethodColor> detectionMethodColorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        extendedActivityBinding = DataBindingUtil.setContentView(this,R.layout.extended_activity);
        extendedContentBinding = extendedActivityBinding.extendedContent;
        moflusSQLiteHelper = new MoflusSQLiteHelper(this);
        setSupportActionBar(extendedActivityBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        folders = new ArrayList<>(0);
        adapter = new FolderAdapter(this,this, folders);
        adapter.swapFolder(FolderUtil.getAllFolder());
        ((LinearLayoutManager)extendedContentBinding.folderRecyclerview.getLayoutManager()).setStackFromEnd(true);
        ((LinearLayoutManager)extendedContentBinding.folderRecyclerview.getLayoutManager()).setReverseLayout(true);
        extendedContentBinding.folderRecyclerview.setAdapter(adapter);
        extendedContentBinding.folderRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy>0){
                    extendedActivityBinding.addFolder.hide();
                }else {
                    extendedActivityBinding.addFolder.show();
                }
            }
        });
        extendedActivityBinding.addFolder.setOnClickListener(this);
        extendedContentBinding.uploadButton.setOnClickListener(this);
        extendedContentBinding.trainingButton.setOnClickListener(this);
        //Color Spinner
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
        extendedContentBinding.detectionMethodColorSpinner.setAdapter(arrayAdapter);
        //
        //Seekbar Pixel Size
        extendedContentBinding.pixelSizeSeekbar.setSelectedMaxValue(getSharedPreferences(MoflusSharedPreferenceHelper.NAME, MODE_PRIVATE)
                .getInt(MoflusSharedPreferenceHelper.PIXEL_SIZE, 5));
        extendedContentBinding.pixelSizeSeekbar.setOnRangeSeekBarChangeListener(this);
        //Check Status Training
        checkStatus();
        extendedContentBinding.statusImageView.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static void toActivity(Context context){
        context.startActivity(new Intent(context,ExtendedController.class));
    }

    @Override
    public void onClick(Folder folder) {
        ExtendedDetailController.toActivity(this,folder);
    }

    @Override
    public void onPopup(View view, Folder folder) {
        adapter.showPopup(view,folder);
    }

    @Override
    public void onSelected(Folder folder) {
        adapter.getFolders().get(adapter.getFolders().indexOf(folder)).setSelected(folder.isSelected());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.statusImageView :
                Toast.makeText(this, "Checking Status...", Toast.LENGTH_SHORT).show();
                checkStatus();
                break;
            case R.id.add_folder :
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                final FolderNameDialogBinding folderView =  DataBindingUtil.inflate(LayoutInflater.from(this),R.layout.folder_name_dialog,null,false);
                dialog.setView(folderView.getRoot());
                dialog.setTitle("Create Folder");
                dialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                final AlertDialog alertDialog = dialog.create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(folderView.folderName.getText().toString().equals("")){
                            folderView.folderName.setError("Empty Name");
                        }else if(adapter.isExistFolder(folderView.folderName.getText().toString())){
                            folderView.folderName.setError("Folder Exists");
                        }else{
                            adapter.addFolder(FolderUtil.addFolder(folderView.folderName.getText().toString()));
                            alertDialog.dismiss();
                            extendedContentBinding.folderRecyclerview.smoothScrollToPosition(adapter.getItemCount()-1);
                        }
                    }
                });
                break;
            case R.id.uploadButton :
                ArrayList<File> uploadFile = new ArrayList<>();
                for(Folder folder : adapter.getFolders()){
                    if(folder.isSelected()){
                        for(File file : folder.getFile().listFiles()){
                            uploadFile.add(file);
                        }
                    }
                }
                if(uploadFile.size()==0){
                    Toast.makeText(this, "No Folder Selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                ImageUploader imageUploader = new ImageUploader(this, MoflusSharedPreferenceHelper.getUser(this));
                imageUploader.uploadDatasetImage(uploadFile);
                Toast.makeText(this, "Image Uploading...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.trainingButton :
                trainingProcess();
                break;
        }
    }

    private void checkStatus(){
        User user = MoflusSharedPreferenceHelper.getUser(this);
        Gson gson = new Gson();
        Log.d("RWPJSON",gson.toJson(user));
        MoFluSService.getService().checkTraining(user).enqueue(new Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, Response<Status> response) {
                Status res = response.body();
                if(res != null && res.isSuccess()){
                    extendedContentBinding.infoTextView.setText(res.getInfo());
                    Toast.makeText(ExtendedController.this, "Checking Success", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ExtendedController.this, "Checking Problem", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Status> call, Throwable t) {
                extendedContentBinding.infoTextView.setText("No Internet Access");
                Toast.makeText(ExtendedController.this, "No Internet Access", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void trainingProcess() {
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
        if(graphDatas.size() == 0){
            Toast.makeText(this, "Select at least 1 Sample", Toast.LENGTH_SHORT).show();
            return;
        }
        final ArrayList<DetectionMethodColor> detectionMethodColors = new ArrayList<>();
        for(DetectionMethodColor detectionMethodColor : detectionMethodColorList){
            if(detectionMethodColor.isSelected()){
                detectionMethodColors.add(detectionMethodColor);
            }
        }
        if(detectionMethodColors.size() == 0){
            Toast.makeText(this, "Select at least 1 Color Type", Toast.LENGTH_SHORT).show();
            return;
        }
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setPositiveButton("Next", new DialogInterface.OnClickListener() {
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
        //Image Cast
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        String imagePath = "";
        for(Folder folder : adapter.getSelectedFolders()){
            if(folder.getFile().listFiles().length<1){
                continue;
            }else{
                imagePath = folder.getFile().listFiles()[0].getPath();
                break;
            }
        }
        if(imagePath.equals("")){
            Toast.makeText(this, "No Image Preview", Toast.LENGTH_SHORT).show();
            return;
        }
        //Pixel Size
        final int pixel_size = getSharedPreferences(MoflusSharedPreferenceHelper.NAME, MODE_PRIVATE)
                .getInt(MoflusSharedPreferenceHelper.PIXEL_SIZE, 5);
        final Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        imageView.setImageBitmap(bitmap);
        //
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Matrix invertMatrix = new Matrix();
                ImageView prev = ((ImageView) view);
                prev.getImageMatrix().invert(invertMatrix);
                float[] eventXY = new float[]{motionEvent.getX(), motionEvent.getY()};
                invertMatrix.mapPoints(eventXY);
                int x = (int) eventXY[0];
                int y = (int) eventXY[1];
                dotLocation.setX(x);
                dotLocation.setY(y);
                if (x < 0) {
                    dotLocation.setX(0);
                } else if (x > bitmap.getWidth() - 1) {
                    dotLocation.setX(bitmap.getWidth() - 1);
                }
                if (y < 0) {
                    dotLocation.setY(0);
                } else if (y > bitmap.getHeight() - 1) {
                    dotLocation.setY(bitmap.getHeight() - 1);
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
                    int radius = newBitmap.getWidth()/100;
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
                            int radius = bitmap.getWidth()/100;
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
                    Toast.makeText(ExtendedController.this, "Please Dot All Samples", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((int) imageView.getTag()) == graphDatas.size()) {
                    imageView.setImageBitmap(null);
                    AlertDialog.Builder datasetDataAdditionalDialog = new AlertDialog.Builder(context);
                    final DatasetAdditionalLayoutBinding datasetAdditionalLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.dataset_additional_layout,null,false);
                    datasetAdditionalLayoutBinding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                            switch (i){
                                case R.id.testing_button :
                                    datasetAdditionalLayoutBinding.trainingForm.setVisibility(View.GONE);
                                    datasetAdditionalLayoutBinding.testingForm.setVisibility(View.VISIBLE);
                                    break;
                                case R.id.training_button :
                                    datasetAdditionalLayoutBinding.trainingForm.setVisibility(View.VISIBLE);
                                    datasetAdditionalLayoutBinding.testingForm.setVisibility(View.GONE);
                                    break;
                            }
                        }

                    });
                    //Spinner
                    List<ClassificationMethod> classificationMethodList = moflusSQLiteHelper.getAllClassificationMethod();
                    List<String> classificationMethodStringList = new ArrayList<>();
                    for (ClassificationMethod classificationMethod : classificationMethodList) {
                        classificationMethodStringList.add(classificationMethod.getName());
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ExtendedController.this, android.R.layout.simple_spinner_item, classificationMethodStringList);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    datasetAdditionalLayoutBinding.classificationMethodSpinner.setAdapter(arrayAdapter);

                    //
                    datasetDataAdditionalDialog.setTitle("Additional");
                    datasetDataAdditionalDialog.setView(datasetAdditionalLayoutBinding.getRoot());
                    datasetDataAdditionalDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DataSet dataSet = new DataSet();
                            dataSet.setUid(MoflusSharedPreferenceHelper.getUser(ExtendedController.this).getUid());
                            ArrayList<FolderDataSet> folderDataSets = new ArrayList<>();
                            for(Folder folder : adapter.getSelectedFolders()){
                                FolderDataSet folderDataSet = new FolderDataSet(folder,dotLocations,detectionMethodColors,pixel_size);
                                folderDataSets.add(folderDataSet);
                            }
                            dataSet.setFolders(folderDataSets);
                            Gson gson = new Gson();
                            String json = gson.toJson(dataSet);
                            Log.d("RWPJSON", json);
                            switch (datasetAdditionalLayoutBinding.radioGroup.getCheckedRadioButtonId()){
                                case R.id.training_button :
                                    dataSet.setLrate(Double.parseDouble(datasetAdditionalLayoutBinding.lrateEdittext.getText().toString()));
                                    dataSet.setEpoch(Integer.parseInt(datasetAdditionalLayoutBinding.epochEdittext.getText().toString()));
                                    dataSet.setMerror(Double.parseDouble(datasetAdditionalLayoutBinding.merrorEdittext.getText().toString()));
                                    dataSet.setHidden(Integer.parseInt(datasetAdditionalLayoutBinding.hiddenEdittext.getText().toString()));
                                    dataSet.setK(Integer.parseInt(datasetAdditionalLayoutBinding.kEdittext.getText().toString()));
                                    MoFluSService.getService().dataSetSend(dataSet).enqueue(new Callback<Status>() {
                                        @Override
                                        public void onResponse(Call<Status> call, Response<Status> response) {
                                            if(response.body() != null && response.body().isSuccess()){
                                                //Check Status Training
                                                MoFluSService.getService().checkTraining(MoflusSharedPreferenceHelper.getUser(ExtendedController.this)).enqueue(new Callback<Status>() {
                                                    @Override
                                                    public void onResponse(Call<Status> call, Response<Status> response) {
                                                        extendedContentBinding.infoTextView.setText(response.body().getInfo());
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Status> call, Throwable t) {
                                                        extendedContentBinding.infoTextView.setText("No Internet Access");
                                                    }
                                                });
                                                Toast.makeText(context, "Training in Process...", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(context, "Training Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Status> call, Throwable t) {
                                            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                                case R.id.testing_button :
                                    dataSet.setMethod(datasetAdditionalLayoutBinding.classificationMethodSpinner.getSelectedItem().toString());
                                    MoFluSService.getService().testingDataSet(dataSet).enqueue(new Callback<List<TestingResult>>() {
                                        @Override
                                        public void onResponse(Call<List<TestingResult>> call, Response<List<TestingResult>> response) {
                                            Toast.makeText(context, "Testing Success", Toast.LENGTH_SHORT).show();
                                            moflusSQLiteHelper.replaceTestingResult(response.body());
                                        }

                                        @Override
                                        public void onFailure(Call<List<TestingResult>> call, Throwable t) {
                                            Toast.makeText(context, "No Internet Access", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                            }
                        }
                    });
                    datasetDataAdditionalDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                        }
                    });
                    datasetDataAdditionalDialog.show();
                    dialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onColorDataChange(int id) {
        getSharedPreferences(MoflusSharedPreferenceHelper.NAME,MODE_PRIVATE).edit()
                .putInt(MoflusSharedPreferenceHelper.COLOR_TYPE,id)
                .apply();
    }

    @Override
    public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
        switch (bar.getId()) {
            case R.id.pixel_size_seekbar:
                getSharedPreferences(MoflusSharedPreferenceHelper.NAME, MODE_PRIVATE)
                        .edit()
                        .putInt(MoflusSharedPreferenceHelper.PIXEL_SIZE, (int) maxValue)
                        .apply();
                break;
        }
    }
}
