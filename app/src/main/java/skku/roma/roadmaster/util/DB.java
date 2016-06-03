package skku.roma.roadmaster.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by takeclassic on 2016-05-05.
 */

public class DB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "RoadMasterDB.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_BUILDING = "building";
    private static final String BUILDING_NUMBER="number";
    private static final String BUILDING_X="x";
    private static final String BUILDING_Y="y";
    private static final String BUILDING_TEXT = "text";

    private static final String TABLE_NODE = "node";
    private static final String NODE_PRIMARY = "id";
    private static final String NODE_X="x";
    private static final String NODE_Y="y";
    private static final String NODE_NAME = "name";
    private static final String NODE_INBUILDING = "inbuilding";

    private static final String TABLE_EDGE= "edge";
    private static final String EDGE_PRIMARY="id";
    private static final String EDGE_A="a";
    private static final String EDGE_B="b";
    private static final String EDGE_WEIGHT="weight";

    private static final String TABLE_CLASS= "class";
    private static final String CLASS_PRIMARY="id";
    private static final String CLASS_NAME="name";
    private static final String CLASS_X="x";
    private static final String CLASS_Y="y";
    private static final String CLASS_A="a";
    private static final String CLASS_AWEIGHT="aweight";
    private static final String CLASS_B="b";
    private static final String CLASS_BWEIGHT="bweight";

    private static final String TABLE_CACHE="cache";

    private static final String CREATE_BUILDING_TABLE = "CREATE TABLE " + TABLE_BUILDING + "("
            + BUILDING_NUMBER + " INTEGER, " + BUILDING_X + " INTEGER, " + BUILDING_Y + " INTEGER, " + BUILDING_TEXT + " TEXT"
            + ")";

    private static final String INSERT_BUILDING_TABLE = "INSERT INTO " + TABLE_BUILDING +
            " SELECT 21 AS " + BUILDING_NUMBER + ", 2447 AS " + BUILDING_X + ", 1552 AS " + BUILDING_Y + ", '제1공학관' AS " + BUILDING_TEXT +
            " UNION ALL SELECT 25, 2534, 1023, '제2공학관'" +
            " UNION ALL SELECT 61, 1482, 686, '생명공학관'" +
            " UNION ALL SELECT 62, 1719, 587, '생명공학대학'" +
            " UNION ALL SELECT 51, 1626, 883, '기초학문관'" +
            " UNION ALL SELECT 32, 1789, 1096, '제2과학관'" +
            " UNION ALL SELECT 31, 1885, 1309, '제1과학관'" +
            " UNION ALL SELECT 85, 2065, 614, '산학협력관'" +
            " UNION ALL SELECT 3, 1336, 1486, '학생회관'" +
            " UNION ALL SELECT 4, 988, 1536, '복지회관'" +
            " UNION ALL SELECT 5, 808, 1844, '수성관'" +
            " UNION ALL SELECT 71, 1235, 2322, '의학관'" +
            " UNION ALL SELECT 53, 2440, 2449, '약학관'" +
            " UNION ALL SELECT 33, 2593, 2614, '화학관'" +
            " UNION ALL SELECT 40, 2889, 2528, '반도체관'" +
            " UNION ALL SELECT 81, 3017, 2287, '제1종합연구동'" +
            " UNION ALL SELECT 83, 3121, 1831, '제2종합연구동'" +
            " UNION ALL SELECT 86, 2108, 2465, 'N센터'" +
            " UNION ALL SELECT 24, 2850, 1800, '공학실습동'" + //0
            " UNION ALL SELECT 20, 2957, 1423, '공학실습동'" + //0
            " UNION ALL SELECT 0, 2943, 1174, '파워플랜트'" +
            " UNION ALL SELECT 0, 2943, 1014, '생명공학실습동'" +
            " UNION ALL SELECT 0, 2039, 368, '예관'" +
            " UNION ALL SELECT 0, 1684, 196, '의관'" +
            " UNION ALL SELECT 0, 1461, 209, '인관'" +
            " UNION ALL SELECT 0, 1825, 1532, '삼성학술정보관'" +
            " UNION ALL SELECT 0, 918, 2245, '대강당'" +
            " UNION ALL SELECT 0, 265, 2202, '체육관'" +
            " UNION ALL SELECT 0, 918, 2245, '대강당'" +
            " UNION ALL SELECT 0, 2328, 2625, '제약기술관'" +
            " UNION ALL SELECT 0, 1954, 2841, '환경플랜트'" +
            " UNION ALL SELECT 0, 834, 373, '신관 A동'" +
            " UNION ALL SELECT 0, 1072, 501, '신관 B동'";

    private static final String CREATE_NODE_TABLE = "CREATE TABLE " + TABLE_NODE + "("
            + NODE_PRIMARY + " INTEGER PRIMARY KEY, " + NODE_X + " INTEGER, "+ NODE_Y + " INTEGER, " + NODE_NAME + " TEXT, " + NODE_INBUILDING + " INTEGER"
            + ")";

    /*안드로이드는 프로요 2.2부터 FK를 지원함*/
    private static final String CREATE_EDGE_TABLE = "CREATE TABLE " + TABLE_EDGE + "("
            + EDGE_PRIMARY + " INTEGER PRIMARY KEY, " + EDGE_A + " INTEGER, " + EDGE_B + " INTEGER, " + EDGE_WEIGHT + " REAL,"
            + " FOREIGN KEY("+EDGE_A+", "+EDGE_B+") REFERENCES "+TABLE_NODE+"("+NODE_PRIMARY+", "+NODE_PRIMARY+")" + ")";

    private static final String CREATE_CLASS_TABLE = "CREATE TABLE " + TABLE_CLASS + "("
            + CLASS_PRIMARY + " STRING PRIMARY KEY, " + CLASS_NAME + " STRING, " + CLASS_X + " INTEGER, " + CLASS_Y + " INTEGER, " + CLASS_A + " INTEGER, " + CLASS_AWEIGHT + " REAL, " + CLASS_B + " INTEGER, " + CLASS_BWEIGHT + " REAL,"
            + " FOREIGN KEY("+CLASS_A+", "+CLASS_B+") REFERENCES "+TABLE_NODE+"("+NODE_PRIMARY+", "+NODE_PRIMARY+")" + ")";

    private static final String CREATE_CACHE_TABLE = "CREATE TABLE " + TABLE_CACHE + "("
            + CLASS_PRIMARY + " STRING PRIMARY KEY, " + CLASS_NAME + " STRING, " + CLASS_X + " INTEGER, " + CLASS_Y + " INTEGER, " + CLASS_A + " INTEGER, " + CLASS_AWEIGHT + " REAL, " + CLASS_B + " INTEGER, " + CLASS_BWEIGHT + " REAL,"
            + " FOREIGN KEY("+CLASS_A+", "+CLASS_B+") REFERENCES "+TABLE_NODE+"("+NODE_PRIMARY+", "+NODE_PRIMARY+")" + ")";

    private Context context;

    public DB(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    private void INSERT_NODE_TABLE(SQLiteDatabase db){
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(context.getAssets().open("node.csv"), "euc-kr");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;

            while((line = bufferedReader.readLine()) != null){
                String[] linearray = line.split(",");
                ContentValues values = new ContentValues();
                values.put(NODE_PRIMARY, linearray[0]);
                values.put(NODE_X, linearray[1]);
                values.put(NODE_Y, linearray[2]);
                values.put(NODE_NAME, linearray[3]);
                values.put(NODE_INBUILDING, linearray[4]);
                db.insert(TABLE_NODE, null, values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void INSERT_EDGE_TABLE(SQLiteDatabase db){
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(context.getAssets().open("edge.csv"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;

            while((line = bufferedReader.readLine()) != null){
                String[] linearray = line.split(",");
                ContentValues values = new ContentValues();
                values.put(EDGE_PRIMARY, linearray[0]);
                values.put(EDGE_A, linearray[1]);
                values.put(EDGE_B, linearray[2]);
                values.put(EDGE_WEIGHT, linearray[3]);
                db.insert(TABLE_EDGE, null, values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void INSERT_CLASS_TABLE(SQLiteDatabase db){
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(context.getAssets().open("class.csv"), "euc-kr");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;

            while((line = bufferedReader.readLine()) != null){
                String[] linearray = line.split(",");
                if(linearray.length == 6){
                    ContentValues values = new ContentValues();
                    values.put(CLASS_PRIMARY, linearray[0]);
                    values.put(CLASS_NAME, linearray[1]);
                    values.put(CLASS_X, linearray[2]);
                    values.put(CLASS_Y, linearray[3]);
                    values.put(CLASS_A, linearray[4]);
                    values.put(CLASS_AWEIGHT, linearray[5]);
                    db.insert(TABLE_CLASS, null, values);
                }
                else {
                    ContentValues values = new ContentValues();
                    values.put(CLASS_PRIMARY, linearray[0]);
                    values.put(CLASS_NAME, linearray[1]);
                    values.put(CLASS_X, linearray[2]);
                    values.put(CLASS_Y, linearray[3]);
                    values.put(CLASS_A, linearray[4]);
                    values.put(CLASS_AWEIGHT, linearray[5]);
                    values.put(CLASS_B, linearray[6]);
                    values.put(CLASS_BWEIGHT, linearray[7]);
                    db.insert(TABLE_CLASS, null, values);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            //db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BUILDING_TABLE);
        db.execSQL(INSERT_BUILDING_TABLE);

        db.execSQL(CREATE_NODE_TABLE);
        INSERT_NODE_TABLE(db);

        db.execSQL(CREATE_EDGE_TABLE);
        INSERT_EDGE_TABLE(db);

        db.execSQL(CREATE_CLASS_TABLE);
        INSERT_CLASS_TABLE(db);

        db.execSQL(CREATE_CACHE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUILDING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EDGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NODE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CACHE);
        // create new tables
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public ArrayList<Building> getBuildings(){
        ArrayList<Building> buildings = new ArrayList<Building>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_BUILDING, null);
        if(c.moveToFirst()){
            while(!c.isAfterLast()){
                buildings.add(new Building(c.getInt(c.getColumnIndex(BUILDING_NUMBER)), c.getInt(c.getColumnIndex(BUILDING_X)), c.getInt(c.getColumnIndex(BUILDING_Y)), c.getString(c.getColumnIndex(BUILDING_TEXT))));
                c.moveToNext();
            }
        }

        return  buildings;
    }

    public ArrayList<MapNode> getNodes(){
        ArrayList<MapNode> nodes = new ArrayList<MapNode>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NODE, null);
        if(c.moveToFirst()){
            while(!c.isAfterLast()){
                nodes.add(new MapNode(c.getInt(c.getColumnIndex(NODE_PRIMARY)), c.getInt(c.getColumnIndex(NODE_X)), c.getInt(c.getColumnIndex(NODE_Y)), c.getString(c.getColumnIndex(NODE_NAME)), c.getInt(c.getColumnIndex(NODE_INBUILDING))));
                c.moveToNext();
            }
        }

        return nodes;
    }

    public ArrayList<MapEdgeData> getEdges(){
        ArrayList<MapEdgeData> edges = new ArrayList<MapEdgeData>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_EDGE, null);
        if(c.moveToFirst()){
            while(!c.isAfterLast()){
                edges.add(new MapEdgeData(c.getInt(c.getColumnIndex(EDGE_PRIMARY)), c.getInt(c.getColumnIndex(EDGE_A)), c.getInt(c.getColumnIndex(EDGE_B)), c.getFloat(c.getColumnIndex(EDGE_WEIGHT))));
                c.moveToNext();
            }
        }

        return edges;
    }

    public Classroom getClass(String primary){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CLASS + " WHERE " + CLASS_PRIMARY + " = '" + primary + "'", null);
        c.moveToFirst();
        if(!c.isNull(c.getColumnIndex(CLASS_B))){
            return new Classroom(c.getString(c.getColumnIndex(CLASS_PRIMARY)), c.getString(c.getColumnIndex(CLASS_NAME)), c.getInt(c.getColumnIndex(CLASS_X)), c.getInt(c.getColumnIndex(CLASS_Y)), c.getInt(c.getColumnIndex(CLASS_A)), c.getFloat(c.getColumnIndex(CLASS_AWEIGHT)), c.getInt(c.getColumnIndex(CLASS_B)), c.getFloat(c.getColumnIndex(CLASS_BWEIGHT)));
        }
        else{
            return new Classroom(c.getString(c.getColumnIndex(CLASS_PRIMARY)), c.getString(c.getColumnIndex(CLASS_NAME)), c.getInt(c.getColumnIndex(CLASS_X)), c.getInt(c.getColumnIndex(CLASS_Y)), c.getInt(c.getColumnIndex(CLASS_A)), c.getFloat(c.getColumnIndex(CLASS_AWEIGHT)));
        }
    }

    public ArrayList<Classroom> searchClass(String text){
        ArrayList<Classroom> classes = new ArrayList<Classroom>();

        SQLiteDatabase db = this.getReadableDatabase();
        if(Character.isDigit(text.charAt(0))){
            Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CLASS + " WHERE " + CLASS_PRIMARY + " LIKE '" + text + "%'", null);
            if(c.moveToFirst()){
                while(!c.isAfterLast()){
                    if(!c.isNull(c.getColumnIndex(CLASS_B))){
                        classes.add(new Classroom(c.getString(c.getColumnIndex(CLASS_PRIMARY)), c.getString(c.getColumnIndex(CLASS_NAME)), c.getInt(c.getColumnIndex(CLASS_X)), c.getInt(c.getColumnIndex(CLASS_Y)), c.getInt(c.getColumnIndex(CLASS_A)), c.getFloat(c.getColumnIndex(CLASS_AWEIGHT)), c.getInt(c.getColumnIndex(CLASS_B)), c.getFloat(c.getColumnIndex(CLASS_BWEIGHT))));
                    }
                    else{
                        classes.add(new Classroom(c.getString(c.getColumnIndex(CLASS_PRIMARY)), c.getString(c.getColumnIndex(CLASS_NAME)), c.getInt(c.getColumnIndex(CLASS_X)), c.getInt(c.getColumnIndex(CLASS_Y)), c.getInt(c.getColumnIndex(CLASS_A)), c.getFloat(c.getColumnIndex(CLASS_AWEIGHT))));
                    }
                    c.moveToNext();
                }
            }
        }
        else{
            Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CLASS + " WHERE " + CLASS_NAME + " LIKE '%" + text + "%'", null);
            if(c.moveToFirst()){
                while(!c.isAfterLast()){
                    if(!c.isNull(c.getColumnIndex(CLASS_B))){
                        classes.add(new Classroom(c.getString(c.getColumnIndex(CLASS_PRIMARY)), c.getString(c.getColumnIndex(CLASS_NAME)), c.getInt(c.getColumnIndex(CLASS_X)), c.getInt(c.getColumnIndex(CLASS_Y)), c.getInt(c.getColumnIndex(CLASS_A)), c.getFloat(c.getColumnIndex(CLASS_AWEIGHT)), c.getInt(c.getColumnIndex(CLASS_B)), c.getFloat(c.getColumnIndex(CLASS_BWEIGHT))));
                    }
                    else{
                        classes.add(new Classroom(c.getString(c.getColumnIndex(CLASS_PRIMARY)), c.getString(c.getColumnIndex(CLASS_NAME)), c.getInt(c.getColumnIndex(CLASS_X)), c.getInt(c.getColumnIndex(CLASS_Y)), c.getInt(c.getColumnIndex(CLASS_A)), c.getFloat(c.getColumnIndex(CLASS_AWEIGHT))));
                    }
                    c.moveToNext();
                }
            }
        }

        return classes;
    }

    public void insertCache(String primary){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_CACHE, CLASS_PRIMARY + " = '" + primary + "'", null);
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CLASS + " WHERE " + CLASS_PRIMARY + " = '" + primary + "'", null);
        c.moveToFirst();

        ContentValues values = new ContentValues();
        if(!c.isNull(c.getColumnIndex(CLASS_B))){
            values.put(CLASS_PRIMARY, c.getString(c.getColumnIndex(CLASS_PRIMARY)));
            values.put(CLASS_NAME, c.getString(c.getColumnIndex(CLASS_NAME)));
            values.put(CLASS_X, c.getInt(c.getColumnIndex(CLASS_X)));
            values.put(CLASS_Y, c.getInt(c.getColumnIndex(CLASS_Y)));
            values.put(CLASS_A, c.getInt(c.getColumnIndex(CLASS_A)));
            values.put(CLASS_AWEIGHT, c.getFloat(c.getColumnIndex(CLASS_AWEIGHT)));
            values.put(CLASS_B, c.getInt(c.getColumnIndex(CLASS_B)));
            values.put(CLASS_BWEIGHT, c.getFloat(c.getColumnIndex(CLASS_BWEIGHT)));
        }
        else{
            values.put(CLASS_PRIMARY, c.getString(c.getColumnIndex(CLASS_PRIMARY)));
            values.put(CLASS_NAME, c.getString(c.getColumnIndex(CLASS_NAME)));
            values.put(CLASS_X, c.getInt(c.getColumnIndex(CLASS_X)));
            values.put(CLASS_Y, c.getInt(c.getColumnIndex(CLASS_Y)));
            values.put(CLASS_A, c.getInt(c.getColumnIndex(CLASS_A)));
            values.put(CLASS_AWEIGHT, c.getFloat(c.getColumnIndex(CLASS_AWEIGHT)));
        }
        db.insert(TABLE_CACHE, null, values);
    }

    public ArrayList<Classroom> getCache(){
        ArrayList<Classroom> classes = new ArrayList<Classroom>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CACHE, null);
        if(c.moveToFirst()){
            while(!c.isAfterLast()){
                if(!c.isNull(c.getColumnIndex(CLASS_B))){
                    classes.add(new Classroom(c.getString(c.getColumnIndex(CLASS_PRIMARY)), c.getString(c.getColumnIndex(CLASS_NAME)), c.getInt(c.getColumnIndex(CLASS_X)), c.getInt(c.getColumnIndex(CLASS_Y)), c.getInt(c.getColumnIndex(CLASS_A)), c.getFloat(c.getColumnIndex(CLASS_AWEIGHT)), c.getInt(c.getColumnIndex(CLASS_B)), c.getFloat(c.getColumnIndex(CLASS_BWEIGHT))));
                }
                else{
                    classes.add(new Classroom(c.getString(c.getColumnIndex(CLASS_PRIMARY)), c.getString(c.getColumnIndex(CLASS_NAME)), c.getInt(c.getColumnIndex(CLASS_X)), c.getInt(c.getColumnIndex(CLASS_Y)), c.getInt(c.getColumnIndex(CLASS_A)), c.getFloat(c.getColumnIndex(CLASS_AWEIGHT))));
                }
                c.moveToNext();
            }
        }

        return classes;
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}