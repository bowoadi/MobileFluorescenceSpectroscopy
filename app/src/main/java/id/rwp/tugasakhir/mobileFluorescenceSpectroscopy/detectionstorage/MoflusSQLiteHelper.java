package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detectionstorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.ClassificationMethod;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.CloudComputingResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DotLocation;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.Dots;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DataResult;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.DetectionMethodColor;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.GraphData;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.GraphDataValue;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.detetionmodel.MicroRNA;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.BitmapHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.helper.DateHelper;
import id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server.model.testing.TestingResult;

/**
 * Created by ranuwp on 3/10/2017.
 */

/**
 * Kelas yang mengatur pengelolaan database pada moflus
 */
public class MoflusSQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME =  "MOFLUS_DB";

    /**
     * Konstruktor MoflusSQLiteHelper
     * @param context context dari activity yang sedang berjalan
     */
    public MoflusSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create Table
    private String CREATE_MICRORNA = "CREATE TABLE `microrna` ( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, `name` TEXT NOT NULL DEFAULT 'No Name', `is_used` NUMERIC NOT NULL DEFAULT 0 )";
    private String CREATE_CLASSIFICATION_METHOD = "CREATE TABLE `classification_method` ( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, `name` TEXT NOT NULL DEFAULT 'No Name' )";
    private String CREATE_DETECTION_METHOD_COLOR = "CREATE TABLE `detection_method_color` ( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, `color_type` TEXT NOT NULL DEFAULT 'No Name' )";
    private String CREATE_DATA_RESULT = "CREATE TABLE \"data_result\" ( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, `name` TEXT NOT NULL, `preview_picture` BLOB, `preview_picture_url` TEXT, `datetime` TEXT NOT NULL, `is_clouded` NUMERIC NOT NULL DEFAULT 0, `detection_method_color_id` INTEGER NOT NULL)";
    private String CREATE_GRAPH_DATA = "CREATE TABLE \"graph_data\" ( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, `microrna_id` INTEGER NOT NULL, `data_result_id` INTEGER NOT NULL, FOREIGN KEY(`microrna_id`) REFERENCES `microrna`(`id`) ON DELETE CASCADE, FOREIGN KEY(`data_result_id`) REFERENCES data_result(id) ON DELETE CASCADE )";
    private String CREATE_GRAPH_DATA_VALUE = "CREATE TABLE `graph_data_value` ( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, `graph_data_id` INTEGER NOT NULL, `time` REAL NOT NULL DEFAULT 0, `value` REAL NOT NULL DEFAULT 0, FOREIGN KEY(`graph_data_id`) REFERENCES graph_data(id) ON DELETE CASCADE )";
    private String CREATE_CLOUD_COMPUTING_RESULT = "CREATE TABLE \"cloud_computing_result\" ( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, `data_result_id` INTEGER NOT NULL, `classification_method_id` INTEGER NOT NULL, `datetime` TEXT NOT NULL, `result` TEXT NOT NULL, `process` INTEGER NOT NULL, FOREIGN KEY(`data_result_id`) REFERENCES `data_result`(`id`) ON DELETE CASCADE, FOREIGN KEY(`classification_method_id`) REFERENCES `classification_method`(`id`) ON DELETE CASCADE )";
    //New
    private String CREATE_DOTS = "CREATE TABLE \"dots\" ( `id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` TEXT NOT NULL DEFAULT 'No Name', `scale` INTEGER DEFAULT 0 )";
    private String CREATE_DOT_LOCATION =  "CREATE TABLE \"dot_location\" ( `id` INTEGER, `dots_id` INTEGER NOT NULL, `x` INTEGER NOT NULL, `y` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`dots_id`) REFERENCES `dots`(`id`) )";

    //Testing Result
    private String CREATE_TESTING_RESULT = "CREATE TABLE `testing_result` ( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, `path` TEXT NOT NULL DEFAULT '', 'result' TEXT NOT NULL DEFAULT '' )";

    /**
     * Fungsi yang pertama kali berjalan saat membuat database sqlite
     * @param sqLiteDatabase database yang dibuat
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //SQL Exec
        sqLiteDatabase.execSQL(CREATE_MICRORNA);
        sqLiteDatabase.execSQL(CREATE_CLASSIFICATION_METHOD);
        sqLiteDatabase.execSQL(CREATE_DETECTION_METHOD_COLOR);
        sqLiteDatabase.execSQL(CREATE_DATA_RESULT);
        sqLiteDatabase.execSQL(CREATE_GRAPH_DATA);
        sqLiteDatabase.execSQL(CREATE_GRAPH_DATA_VALUE);
        sqLiteDatabase.execSQL(CREATE_CLOUD_COMPUTING_RESULT);
        sqLiteDatabase.execSQL(CREATE_DOTS);
        sqLiteDatabase.execSQL(CREATE_DOT_LOCATION);
        sqLiteDatabase.execSQL(CREATE_TESTING_RESULT);
    }

    /**
     * Fungsi yang akan berjalan saat terdapat perubahan versi pada database yang digunakan
     * @param sqLiteDatabase database yang digunakan
     * @param oldVersion versi yang lama
     * @param newVersion versi yang baru
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        switch (oldVersion){
            case 1 :
                sqLiteDatabase.execSQL(CREATE_DOTS);
                sqLiteDatabase.execSQL(CREATE_DOT_LOCATION);
            case 2 :
                sqLiteDatabase.execSQL(CREATE_TESTING_RESULT);
        }
    }

    /**
     * Fungsi yang akan selalu berjalan saat database dibuka
     * @param db
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys = ON");
    }

    //MicroRNA

    /**
     * Menambahkan microRNA ke database
     * @param microRNA microRNA yang akan ditambahkan
     * @return id dari microrna pada database
     */
    public Long addMicroRNA(MicroRNA microRNA){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(microRNA.getId() != 0){
            values.put("id", microRNA.getId());
        }
        values.put("name", microRNA.getName());
        values.put("is_used",microRNA.isUsed());
        Long id = db.insertWithOnConflict("microrna",
                null,
                values,SQLiteDatabase.CONFLICT_IGNORE);
        if(id == -1){
            updateMicroRNA(microRNA);
        }
        db.close();
        return id;
    }

    /**
     * Menghapus microRNA pada database
     * @param microRNA microRNA yang akan dihapus
     * @return id dari microrna yang dihapus
     */
    public int deleteMicroRNA(MicroRNA microRNA){
        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.delete("microrna",
                "id=?",
                new String[]{microRNA.getId()+""});
        db.close();
        return i;
    }

    /**
     * Mendapatkan semua microrna yang ada pada database
     * @return semua microrna yang ada pada database
     */
    public List<MicroRNA> getAllMicroRNA(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<MicroRNA> allMicroRNA = new ArrayList<>();
        String query = "SELECT * FROM microrna";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                MicroRNA microRNA = new MicroRNA();
                microRNA.setId(cursor.getInt(0));
                microRNA.setName(cursor.getString(1));
                microRNA.setUsed(cursor.getInt(2) == 1);
                allMicroRNA.add(microRNA);
            }while(cursor.moveToNext());
        }
        db.close();
        return allMicroRNA;
    }

    /**
     * Mendapatkan microrna berdasarkan id
     * @param id id microrna
     * @return microrna berdasarkan id
     */
    public MicroRNA getMicroRNA(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        MicroRNA microRNA = new MicroRNA();
        String query = "SELECT * FROM microrna WHERE id="+id;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                microRNA.setId(cursor.getInt(0));
                microRNA.setName(cursor.getString(1));
                microRNA.setUsed(cursor.getInt(2) == 1);
            }while(cursor.moveToNext());
        }
        return microRNA;
    }

    /**
     * Mendapatkan microrna berdasarkan database dan id
     * @param db database yang digunakan
     * @param id id microrna
     * @return microrna
     */
    private MicroRNA getMicroRNA(SQLiteDatabase db,int id){
        MicroRNA microRNA = new MicroRNA();
        String query = "SELECT * FROM microrna WHERE id="+id;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                microRNA.setId(cursor.getInt(0));
                microRNA.setName(cursor.getString(1));
                microRNA.setUsed(cursor.getInt(2) == 1);
            }while(cursor.moveToNext());
        }
        return microRNA;
    }

    /**
     * Mendapatkan semua microrna yang digunakan pada database
     * @return semua microrna yang digunakan
     */
    public List<MicroRNA> getAllUsedMicroRNA(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<MicroRNA> allMicroRNA = new ArrayList<>();
        String query = "SELECT * FROM microrna WHERE is_used = 1";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                MicroRNA microRNA = new MicroRNA();
                microRNA.setId(cursor.getInt(0));
                microRNA.setName(cursor.getString(1));
                microRNA.setUsed(cursor.getInt(2) == 1);
                allMicroRNA.add(microRNA);
            }while(cursor.moveToNext());
        }
        db.close();
        return allMicroRNA;
    }

    /**
     * Mengubah microrna pada database yang digunakan
     * @param microRNA microrna yang diubah
     * @return
     */
    public int updateMicroRNA(MicroRNA microRNA){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", microRNA.getId());
        values.put("name", microRNA.getName());
        values.put("is_used", microRNA.isUsed()?1:0);
        int id = db.update("microrna",
                values,
                "id = ?",
                new String[]{""+microRNA.getId()});
        db.close();
        return id;
    }

    /**
     * Menghapus semua microrna yang ada
     */
    private void deleteMicroRNATable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM microrna");
        db.close();
    }

    //Classification Method

    /**
     * Menambahkan classificationmethod ke database
     * @param classificationMethod classificationmethod yang ditambahkan
     * @return id classificationmethod yang ditambahkan pada database
     */
    public Long addClassificationMethod(ClassificationMethod classificationMethod){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", classificationMethod.getId());
        values.put("name", classificationMethod.getName());
        Long id = db.insertWithOnConflict("classification_method",
                null,
                values,SQLiteDatabase.CONFLICT_IGNORE);
        if(id == -1){
            updateClassificationMethod(db,classificationMethod);
        }
        db.close();
        return id;
    }

    /**
     * Mendapatkan semua classificationmethod yang ada pada database
     * @return semua classificationmethod yang ada pada database
     */
    public List<ClassificationMethod> getAllClassificationMethod(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<ClassificationMethod> allClassificationMethod = new ArrayList<>();
        String query = "SELECT * FROM classification_method";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                ClassificationMethod classificationMethod = new ClassificationMethod();
                classificationMethod.setId(cursor.getInt(0));
                classificationMethod.setName(cursor.getString(1));
                allClassificationMethod.add(classificationMethod);
            }while(cursor.moveToNext());
        }
        db.close();
        return allClassificationMethod;
    }

    /**
     * Mendapatkan classificationmethod berdasarkan id pada database
     * @param id id classificationmethod
     * @return classificationmethod berdasarkan id
     */
    public ClassificationMethod getClassificationMethod(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ClassificationMethod classificationMethod = new ClassificationMethod();
        String query = "SELECT * FROM classification_method WHERE id="+id;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                classificationMethod.setId(cursor.getInt(0));
                classificationMethod.setName(cursor.getString(1));
            }while(cursor.moveToNext());
        }
        db.close();
        return classificationMethod;
    }

    /**
     * Mengubah classificationmethod pada database
     * @param db database yang digunakan
     * @param classificationMethod classificationmethod yang diubah
     * @return id classificationmethod yang diubah
     */
    private int updateClassificationMethod(SQLiteDatabase db,ClassificationMethod classificationMethod){
        ContentValues values = new ContentValues();
        values.put("id", classificationMethod.getId());
        values.put("name", classificationMethod.getName());
        int id = db.update("classification_method",
                values,
                "id = ?",
                new String[]{""+classificationMethod.getId()});
        return id;
    }

    /**
     * Menghapus semua classificationmethod pada database
     */
    private void deleteClassificationMethodTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM classification_method");
        db.close();
    }

    //Detection Method Color

    /**
     * Menambahkan detectionmethodcolor ke database
     * @param detectionMethodColor detectionmethodcolor yang ditambahkan
     * @return id detectionmethodcolor yang ditambahkan pada database
     */
    public Long addDetectionMethodColor(DetectionMethodColor detectionMethodColor){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", detectionMethodColor.getId());
        values.put("color_type", detectionMethodColor.getColor_type());
        Long id = db.insertWithOnConflict("detection_method_color",
                null,
                values,SQLiteDatabase.CONFLICT_IGNORE);
        if(id == -1){
            updateDetectionMethodColor(detectionMethodColor);
        }
        db.close();
        return id;
    }

    /**
     * Mendapatkan semua detectionmethodcolor yang ada pada database
     * @return semua detectionmethodcolor yang ada pada database
     */
    public List<DetectionMethodColor> getAllDetectionMethodColor(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<DetectionMethodColor> allDetectionMethodColor = new ArrayList<>();
        String query = "SELECT * FROM detection_method_color";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                DetectionMethodColor detectionMethodColor = new DetectionMethodColor();
                detectionMethodColor.setId(cursor.getInt(0));
                detectionMethodColor.setColor_type(cursor.getString(1));
                allDetectionMethodColor.add(detectionMethodColor);
            }while(cursor.moveToNext());
        }
        db.close();
        return allDetectionMethodColor;
    }

    /**
     * Mendapatkan detectionmtehodcolor berdasarkan id pada database
     * @param id id detectionmethodcolor
     * @return detectionmethodcolor berdasarkan id
     */
    public DetectionMethodColor getDetectionMethodColor(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        DetectionMethodColor detectionMethodColor = new DetectionMethodColor();
        String query = "SELECT * FROM detection_method_color WHERE id="+id;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                detectionMethodColor.setId(cursor.getInt(0));
                detectionMethodColor.setColor_type(cursor.getString(1));
            }while(cursor.moveToNext());
        }
        db.close();
        return detectionMethodColor;
    }

    /**
     * Mendapatkan detectionmethodcolor berdasarkan database dan id
     * @param db database yang digunakan
     * @param id id detectionmethodcolor
     * @return detectionmethodcolor
     */
    private DetectionMethodColor getDetectionMethodColor(SQLiteDatabase db,int id){
        DetectionMethodColor detectionMethodColor = new DetectionMethodColor();
        String query = "SELECT * FROM detection_method_color WHERE id="+id;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                detectionMethodColor.setId(cursor.getInt(0));
                detectionMethodColor.setColor_type(cursor.getString(1));
            }while(cursor.moveToNext());
        }
        return detectionMethodColor;
    }

    /**
     * Mengubah detectionmethodcolor pada database
     * @param detectionMethodColor detectionmethodcolor yang diubah
     * @return id detectionmethodcolor yang diubah
     */
    private int updateDetectionMethodColor(DetectionMethodColor detectionMethodColor){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", detectionMethodColor.getId());
        values.put("color_type", detectionMethodColor.getColor_type());
        int id = db.update("detection_method_color",
                values,
                "id = ?",
                new String[]{""+detectionMethodColor.getId()});
        db.close();
        return id;
    }

    /**
     * Menghapus semua detectionmethod color yang ada pada database
     */
    private void deleteDetectionMethodColorTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM detection_method_color");
        db.close();
    }

    //Data Result

    /**
     * Menambah dataresult ke database
     * @param dataResult dataresult yang ditambah
     * @return id dataresult
     */
    public Long addDataResult(DataResult dataResult){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues data_result = new ContentValues();
        if(dataResult.getId() != 0){
            data_result.put("id", dataResult.getId());
        }
        data_result.put("name", dataResult.getName());
        if(dataResult.getPreview_picture() != null){
            data_result.put("preview_picture", BitmapHelper.getBytes(dataResult.getPreview_picture()));
        }
        if(dataResult.getPreview_picture_url() != null){
            data_result.put("preview_picture_url", dataResult.getPreview_picture_url());
        }
        data_result.put("datetime", DateHelper.SQLITE_FORMAT_ALL.format(dataResult.getDatetime()));
        data_result.put("is_clouded", (dataResult.isIs_clouded())?"1":"0");
        data_result.put("detection_method_color_id", dataResult.getDetectionMethodColorId());
        Log.d("RWPDB","RWP");
        Long id = db.insertWithOnConflict("data_result",
                null,
                data_result,SQLiteDatabase.CONFLICT_IGNORE);
        if(id == -1){
            //updateDataResult(dataResult);
        }else {
            dataResult.setId(id.intValue());
            for(GraphData graphData : dataResult.getGraphDatas()){
                addGraphData(db,graphData,dataResult.getId());
            }
        }
        db.close();
        return id;
    }

    /**
     * Mengubah dataresult pada database
     * @param dataResult dataresult yang diubah
     * @return id dataresult
     */
    public int updateDataResult(DataResult dataResult){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", dataResult.getId());
        values.put("name", dataResult.getName());
        values.put("preview_picture", BitmapHelper.getBytes(dataResult.getPreview_picture()));
        values.put("preview_picture_url", dataResult.getPreview_picture_url());
        values.put("datetime", DateHelper.SQLITE_FORMAT_ALL.format(dataResult.getDatetime()));
        values.put("is_clouded", (dataResult.isIs_clouded())?1:0);
        values.put("detection_method_color_id", dataResult.getDetectionMethodColorId());
        int id = db.update("data_result",
                values,
                "id = ?",
                new String[]{""+dataResult.getId()});
        db.close();
        return id;
    }

    /**
     * Menghapus dataresult pada database
     * @param dataResult dataresult yang dihapus
     * @return id dataresult
     */
    public int deleteDataResult(DataResult dataResult){
        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.delete("data_result",
                "id=?",
                new String[]{dataResult.getId()+""});
        db.close();
        return i;
    }

    /**
     * Mendapatkan full dataresult dari database
     * @return semua dataresult lengkap dengan isi-isinya
     */
    public ArrayList<DataResult> getFullAllDataResult() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<DataResult> allDataResult = new ArrayList<>();
        String query = "SELECT * FROM data_result ORDER BY id DESC";
        Cursor cursor = db.rawQuery(query,null);
        DatabaseUtils.dumpCursor(cursor);
        if(cursor != null && cursor.getCount() > 0){
            if(cursor.moveToFirst()){
                do{
                    DataResult dataResult = new DataResult();
                    dataResult.setId(cursor.getInt(0));
                    dataResult.setName(cursor.getString(1));
                    dataResult.setPreview_picture_url(cursor.getString(3));
                    try {
                        dataResult.setDatetime(DateHelper.SQLITE_FORMAT_ALL.parse(cursor.getString(4)));
                    } catch (ParseException e) {
                        dataResult.setDatetime(new Date());
                    }
                    dataResult.setIs_clouded((cursor.getInt(5) == 1));
                    if(cursor.getBlob(2) != null){
                        dataResult.setPreview_picture(BitmapHelper.getBitmap(cursor.getBlob(2)));
                    }
                    int idDetectionColor = cursor.getInt(6);
                    ArrayList<DetectionMethodColor> detectionMethodColors= new ArrayList<>();
                    if(idDetectionColor >= 0){
                        String[] stringIds = String.valueOf(idDetectionColor).split("");
                        for(String val : stringIds){
                            if(!val.equals("")){
                                DetectionMethodColor detectionMethodColor = getDetectionMethodColor(db,Integer.parseInt(val));
                                detectionMethodColors.add(detectionMethodColor);
                            }
                        }
                    }
                    dataResult.setDetectionMethodColors(detectionMethodColors);
                    ArrayList<GraphData> graphDatas = getAllGraphData(db,dataResult.getId());
                    dataResult.setGraphDatas(graphDatas);
                    allDataResult.add(dataResult);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return allDataResult;
    }

    /**
     * Mendapatkan jumlah dataresult yang didsimpan diserver
     * @return jumlah dataresult yang disimpan di server
     */
    public int getDataResultCloudedCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = 0;
        String query = "SELECT COUNT(id) FROM data_result WHERE is_clouded=1";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                result = cursor.getInt(0);
            }while(cursor.moveToNext());
        }
        db.close();
        return result;
    }

    /**
     * Mendapatkan dataresult berdasarkan id pada database
     * @param id id dataresult
     * @return dataresult
     */
    public DataResult getDataResult(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        DataResult dataResult = new DataResult();
        String query = "SELECT * FROM data_result WHERE id="+id;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                DatabaseUtils.dumpCursor(cursor);
                dataResult.setId(cursor.getInt(0));
                dataResult.setName(cursor.getString(1));
                if(cursor.getBlob(2) != null) {
                    dataResult.setPreview_picture(BitmapHelper.getBitmap(cursor.getBlob(2)));
                }
                dataResult.setPreview_picture_url(cursor.getString(3));
                try {
                    dataResult.setDatetime(DateHelper.SQLITE_FORMAT_ALL.parse(cursor.getString(4)));
                } catch (ParseException e) {
                    dataResult.setDatetime(new Date());
                }
                dataResult.setIs_clouded((cursor.getInt(5) == 1));
                int idDetectionColor = cursor.getInt(6);
                String[] stringIds = String.valueOf(idDetectionColor).split("");
                ArrayList<DetectionMethodColor> detectionMethodColors= new ArrayList<>();
                for(String val : stringIds){
                    if(!val.equals("") && !val.equals("-")){
                        DetectionMethodColor detectionMethodColor = getDetectionMethodColor(db,Integer.parseInt(val));
                        detectionMethodColors.add(detectionMethodColor);

                    }
                }
                dataResult.setDetectionMethodColors(detectionMethodColors);

                ArrayList<GraphData> graphDatas = getAllGraphData(db,dataResult.getId());
                dataResult.setGraphDatas(graphDatas);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return dataResult;
    }

    /**
     * Menghapus semua dataresult pada database
     */
    private void deleteDataResultTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM data_result");
        db.close();
    }

    //Graph Data

    /**
     * Menambah graphdata ke database
     * @param db database yang digunakan
     * @param graphData graphdata yang ditambah
     * @param data_result_id id dari dataresult
     * @return id dari graphdata pada database
     */
    private Long addGraphData(SQLiteDatabase db,GraphData graphData, int data_result_id){
        ContentValues values = new ContentValues();
        values.put("microrna_id", graphData.getMicroRNA().getId());
        values.put("data_result_id", data_result_id);
        Long id = db.insertWithOnConflict("graph_data",
                null,
                values,SQLiteDatabase.CONFLICT_IGNORE);
        graphData.setId(id.intValue());
        for(GraphDataValue graphDataValue : graphData.getGraphDataValues()){
            addGraphDataValue(db,graphDataValue,graphData.getId());
        }
        return id;
    }

    /**
     * Mendapatkan semua graphdata berdasarkan id dataresult pada database
     * @param db database yang digunakan
     * @param id id dataresult
     * @return semua graphdata pada dataresult berdasarkan id
     */
    private ArrayList<GraphData> getAllGraphData(SQLiteDatabase db,int id){
        ArrayList<GraphData> allGraphData = new ArrayList<>();
        String query = "SELECT * FROM graph_data WHERE data_result_id="+id;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                GraphData graphData = new GraphData();
                graphData.setId(cursor.getInt(0));
                MicroRNA microRNA = getMicroRNA(db,cursor.getInt(1));
                graphData.setMicroRNA(microRNA);
                ArrayList<GraphDataValue> graphDataValues = getAllGraphDataValue(db,graphData.getId());
                graphData.setGraphDataValues(graphDataValues);
                allGraphData.add(graphData);
            }while(cursor.moveToNext());
        }
        return allGraphData;
    }

    /**
     * Menghapus semua graphdata pada table
     */
    private void deleteGraphDataTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM graph_data");
        db.close();
    }

    //Graph Data Value

    /**
     * Menambahkan graphdatavalue pada database berdasarkan graphdata id
     * @param db database yang digunakan
     * @param graphDataValue graphdatavalue yang ditambahkan
     * @param graph_data_id id graphdata
     * @return id graphdatavalue
     */
    private Long addGraphDataValue(SQLiteDatabase db,GraphDataValue graphDataValue, int graph_data_id){
        ContentValues values = new ContentValues();
        values.put("graph_data_id", graph_data_id);
        values.put("time", graphDataValue.getTime());
        values.put("value", graphDataValue.getValue());
        Long id = db.insertWithOnConflict("graph_data_value",
                null,
                values,SQLiteDatabase.CONFLICT_IGNORE);
        graphDataValue.setId(id.intValue());
        return id;
    }

    /**
     * Mendapatkan semua graphdatavalue berdasarkan id graphdata pada database
     * @param db database yang digunakan
     * @param id id graphdata
     * @return semua graphdatavalue pada graphdata berdasarkan id pada database
     */
    private ArrayList<GraphDataValue> getAllGraphDataValue(SQLiteDatabase db,int id){
        ArrayList<GraphDataValue> allGraphDataValue = new ArrayList<>();
        String query = "SELECT * FROM graph_data_value WHERE graph_data_id="+id;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                GraphDataValue graphDataValue = new GraphDataValue(cursor.getFloat(2),cursor.getFloat(3));
                graphDataValue.setId(cursor.getInt(0));
                allGraphDataValue.add(graphDataValue);
            }while(cursor.moveToNext());
        }
        return allGraphDataValue;
    }

    /**
     * Menghapus semua graphdatavalue pada database
     */
    private void deleteGraphDataValueTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM graph_data_value");
        db.close();
    }

    //Cloud Computing Result

    /**
     * Menambahkan cloudcomputingresult pada database
     * @param cloudComputingResult cloudcomputing yang ditambahkan
     * @param dataresult_id id dari dataresult
     * @return id dari cloudcomputing
     */
    public Long addCloudComputingResult(CloudComputingResult cloudComputingResult, int dataresult_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("data_result_id", dataresult_id);
        values.put("classification_method_id", cloudComputingResult.getClassificationMethod().getId());
        values.put("datetime", DateHelper.SQLITE_FORMAT_ALL.format(cloudComputingResult.getDatetime()));
        values.put("result",cloudComputingResult.getResult());
        values.put("process",cloudComputingResult.getProcess());
        Long id = db.insertWithOnConflict("cloud_computing_result",
                null,
                values,SQLiteDatabase.CONFLICT_IGNORE);
        cloudComputingResult.setId(id.intValue());
        db.close();
        return id;
    }

    /**
     * Mendapatkan semua cloudcomputingresult berdasarkan id dataresult
     * @param data_result_id
     * @return semua cloudcomputingresult berdasarkan dataresult id
     */
    public ArrayList<CloudComputingResult> getAllCloudComputingResult(int data_result_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<CloudComputingResult> allCloudComputingResult = new ArrayList<>();
        String query = "SELECT * FROM cloud_computing_result WHERE data_result_id="+data_result_id;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                CloudComputingResult cloudComputingResult = new CloudComputingResult();
                cloudComputingResult.setId(cursor.getInt(0));
                cloudComputingResult.setClassificationMethod(getClassificationMethod(cursor.getInt(2)));
                try {
                    cloudComputingResult.setDatetime(DateHelper.SQLITE_FORMAT_ALL.parse(cursor.getString(3)));
                } catch (ParseException e) {
                    cloudComputingResult.setDatetime(new Date());
                }
                cloudComputingResult.setResult(cursor.getString(4));
                cloudComputingResult.setProcess(cursor.getInt(5));
                allCloudComputingResult.add(cloudComputingResult);
            }while(cursor.moveToNext());
        }
        return allCloudComputingResult;
    }

    /**
     * Mengubah cloudcomputingresult pada database
     * @param cloudComputingResult cloudcomputingresult yang diubah
     * @return id cloudcomputingresult pada database
     */
    public int updateCloudComputingResult(CloudComputingResult cloudComputingResult){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", cloudComputingResult.getId());
        values.put("datetime",DateHelper.SQLITE_FORMAT_ALL.format(cloudComputingResult.getDatetime()));
        values.put("result",cloudComputingResult.getResult());
        values.put("process",cloudComputingResult.getProcess());
        int id = db.update("cloud_computing_result",
                values,
                "id = ?",
                new String[]{""+cloudComputingResult.getId()});
        db.close();
        return id;
    }

    /**
     * Menghapus cloudcomputingresult pada database
     * @param cloudComputingResult cloudcomputingresult yang dihapus
     * @return id cloudcomputingresult pada database
     */
    public int deleteCloudComputing(CloudComputingResult cloudComputingResult){
        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.delete("cloud_computing_result",
                "id=?",
                new String[]{cloudComputingResult.getId()+""});
        db.close();
        return i;
    }

    /**
     * Menghapus semua cloudcomputingresult pada database
     */
    public void deleteCloudComputingResult(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM cloud_computing_result");
        db.close();
    }

    //Dots

    /**
     * Menambahkan dots pada database
     * @param dots dots yang ditambah
     * @return id dots pada database
     */
    public Long addDots(Dots dots){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", dots.getName());
        contentValues.put("scale", dots.getScale());
        Long id = db.insertWithOnConflict("dots",
                null,
                contentValues,SQLiteDatabase.CONFLICT_IGNORE);
        if(id == -1){
            //updateDataResult(dataResult);
        }else {
            dots.setId(id.intValue());
            for(DotLocation dotLocation : dots.getDotLocations()){
                addDotLocation(dotLocation,dots.getId());
            }
        }
        db.close();
        return id;
    }

    /**
     * Mengubah dots pada database
     * @param dots dots yang diubah
     * @return id dots pada database
     */
    public int updateDots(Dots dots){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", dots.getId());
        values.put("name", dots.getName());
        values.put("scale", dots.getScale());
        int id = db.update("dots",
                values,
                "id = ?",
                new String[]{""+dots.getId()});
        db.close();
        return id;
    }

    /**
     * Menghapus dots pada database
     * @param dots dots yang dihapus
     * @return id dots pada database
     */
    public int deleteDots(Dots dots){
        SQLiteDatabase db = this.getWritableDatabase();
        for(DotLocation dotLocation : dots.getDotLocations()){
            deleteDotLocation(dotLocation);
        }
        int i = db.delete("dots",
                "id=?",
                new String[]{dots.getId()+""});
        db.close();
        return i;
    }

    /**
     * Menghapus semua dots pada database
     */
    public void deleteDots(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM dots");
        db.close();
    }

    /**
     * Mendapatkan semuadots lengkap beserta isi-isinya
     * @return semua dots lengkap
     */
    public ArrayList<Dots> getFullDots() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Dots> allDots = new ArrayList<>();
        String query = "SELECT * FROM dots ORDER BY id DESC";
        Cursor cursor = db.rawQuery(query,null);
        DatabaseUtils.dumpCursor(cursor);
        if(cursor != null && cursor.getCount() > 0){
            if(cursor.moveToFirst()){
                do{
                    Dots dots = new Dots();
                    dots.setId(cursor.getInt(0));
                    dots.setName(cursor.getString(1));
                    dots.setScale(cursor.getInt(2));
                    ArrayList<DotLocation> dotLocations = getAllDotLocation(dots.getId());
                    dots.setDotLocations(dotLocations);
                    allDots.add(dots);
                }while(cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return allDots;
    }

    //DotLocation

    /**
     * Mendapatkan dotlocation pada database berdasarkan dots id
     * @param dotLocation dotlocation yang ditambahkan
     * @param dots_id id dots
     * @return id dari dotlocation pada database
     */
    public Long addDotLocation(DotLocation dotLocation, int dots_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("dots_id", dots_id);
        values.put("x", dotLocation.getX());
        values.put("y", dotLocation.getY());
        Long id = db.insertWithOnConflict("dot_location",
                null,
                values,SQLiteDatabase.CONFLICT_IGNORE);
        dotLocation.setId(id.intValue());
        db.close();
        return id;
    }

    /**
     * Mendapatkan semua dotlocation berdasarkan id dots
     * @param dots_id id dari dots
     * @return Semua dotlocation dari dots berdasarkan id pada database
     */
    public ArrayList<DotLocation> getAllDotLocation(int dots_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<DotLocation> allDotLocations = new ArrayList<>();
        String query = "SELECT * FROM dot_location WHERE dots_id="+dots_id;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                DotLocation dotLocation = new DotLocation();
                dotLocation.setId(cursor.getInt(0));
                dotLocation.setX(cursor.getInt(2));
                dotLocation.setY(cursor.getInt(3));
                allDotLocations.add(dotLocation);
            }while(cursor.moveToNext());
        }
        return allDotLocations;
    }

    /**
     * Mengubah dotlocation
     * @param dotLocation dotlocation yang diubah
     * @return id dotlocation pada database
     */
    public int updateDotLocation(DotLocation dotLocation){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", dotLocation.getId());
        values.put("x",dotLocation.getX());
        values.put("y",dotLocation.getY());
        int id = db.update("dot_location",
                values,
                "id = ?",
                new String[]{""+dotLocation.getId()});
        db.close();
        return id;
    }

    /**
     * Menghapus dotlocation pada database
     * @param dotLocation dotlocation yang dihapus
     * @return id dotlocation
     */
    public int deleteDotLocation(DotLocation dotLocation){
        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.delete("dot_location",
                "id=?",
                new String[]{dotLocation.getId()+""});
        return i;
    }

    /**
     * Menghapus semua dotlocation
     */
    public void deleteDotLocation(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM dot_location");
        db.close();
    }

    //Testing Result

    /**
     * Menambah testingresult pada database
     * @param testingResult testingresult yang ditambahkan
     * @param db database yang digunakan
     * @return id testingresult pada database
     */
    public Long addTestingResult(TestingResult testingResult, SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put("path", testingResult.getPath());
        values.put("result", testingResult.getResult());
        Long id = db.insertWithOnConflict("testing_result",
                null,
                values,SQLiteDatabase.CONFLICT_IGNORE);
        return id;
    }

    /**
     * Mendapatkan semua testingresult pada database
     * @return testingresult pada database
     */
    public List<TestingResult> getAllTestingResult(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<TestingResult> allTestingResult = new ArrayList<>();
        String query = "SELECT * FROM testing_result";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                TestingResult testingResult = new TestingResult();
                testingResult.setPath(cursor.getString(1));
                testingResult.setResult(cursor.getString(2));
                allTestingResult.add(testingResult);
            }while(cursor.moveToNext());
        }
        return allTestingResult;
    }

    /**
     * Menghapus semua testingresult pada database
     */
    public void deleteTestingResult(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM testing_result");
        db.close();
    }

    /**
     * Mereplace testingresult pada database
     * @param testingResults testingresult yang mereplace
     */
    public void replaceTestingResult(List<TestingResult> testingResults){
        deleteTestingResult();
        SQLiteDatabase db = this.getWritableDatabase();
        for(TestingResult testingResult : testingResults){
            addTestingResult(testingResult, db);
        }
        db.close();
    }


    //All Table

    /**
     * Menghapus semua table pada database secara bersamaan
     */
    public void deleteAllTableRecord(){
        deleteMicroRNATable();
        deleteClassificationMethodTable();
        deleteDetectionMethodColorTable();
        deleteDataResultTable();
        deleteGraphDataTable();
        deleteGraphDataValueTable();
        deleteCloudComputingResult();
        deleteDotLocation();
        deleteDots();
        deleteTestingResult();
    }

}
