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
    private static final int DATABASE_VERSION = 4;

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
            " UNION ALL SELECT 4, 988, 1536, '복지회관'" +
            " UNION ALL SELECT 3, 1336, 1486, '학생회관'" +
            " UNION ALL SELECT 4, 1338, 1484, '복지회관'" +
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

    private String INSERT_NODE_TABLE(){
        String INSERT_NODE_TABLE = "INSERT INTO " + TABLE_NODE;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(context.getAssets().open("node.csv"), "euc-kr");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = bufferedReader.readLine();
            String[] linearray = line.split(",");

            INSERT_NODE_TABLE = stringBuffer.append(INSERT_NODE_TABLE).append(" SELECT ").append(linearray[0]).append(" AS ").append(NODE_PRIMARY).append(", ")
                    .append(linearray[1]).append(" AS ").append(NODE_X).append(", ")
                    .append(linearray[2]).append(" AS ").append(NODE_Y).append(", '")
                    .append(linearray[3]).append("' AS ").append(NODE_NAME).append(", ")
                    .append(linearray[4]).append(" AS ").append(NODE_INBUILDING)
                    .toString();

            while((line = bufferedReader.readLine()) != null){
                linearray = line.split(",");
                INSERT_NODE_TABLE = stringBuffer.append(" UNION ALL SELECT ")
                        .append(linearray[0]).append(", ").append(linearray[1]).append(", ").append(linearray[2]).append(", '").append(linearray[3]).append("', ").append(linearray[4]).toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return INSERT_NODE_TABLE;
    }

    private String INSERT_EDGE_TABLE(){
        String INSERT_EDGE_TABLE = "INSERT INTO " + TABLE_EDGE;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(context.getAssets().open("edge.csv"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = bufferedReader.readLine();
            String[] linearray = line.split(",");

            INSERT_EDGE_TABLE = stringBuffer.append(INSERT_EDGE_TABLE).append(" SELECT ").append(linearray[0]).append(" AS ").append(EDGE_PRIMARY).append(", ")
                    .append(linearray[1]).append(" AS ").append(EDGE_A).append(", ")
                    .append(linearray[2]).append(" AS ").append(EDGE_B).append(", ")
                    .append(linearray[3]).append(" AS ").append(EDGE_WEIGHT)
                    .toString();

            while((line = bufferedReader.readLine()) != null){
                linearray = line.split(",");
                INSERT_EDGE_TABLE = stringBuffer.append(" UNION ALL SELECT ")
                        .append(linearray[0]).append(", ").append(linearray[1]).append(", ").append(linearray[2]).append(", ").append(linearray[3]).toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return INSERT_EDGE_TABLE;
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
        db.execSQL(INSERT_NODE_TABLE());

        db.execSQL(CREATE_EDGE_TABLE);
        db.execSQL(INSERT_EDGE_TABLE());

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



//
//    public Building getBuilding(String text) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        //건물명으로 정보 탐색
//        String selectQuery = "SELECT  * FROM " + TABLE_BUILDING + " WHERE "
//                + BUILDING_TEXT + " = " + text;
//
//        Cursor c = db.rawQuery(selectQuery, null);
//        Building build=new Building();
//
//
//        if (c != null)
//            c.moveToFirst();
//
//        build.setX(c.getInt(c.getColumnIndex(BUILDING_X)));
//        build.setY((c.getInt(c.getColumnIndex(BUILDING_Y))));
//        build.setText(c.getString(c.getColumnIndex(BUILDING_TEXT)));
//
//        return build;
//    }
//
//    /*PK로 튜플을 찾아 업데이트*/
//    public int updateBuilding(Building build) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(BUILDING_X, build.getX());
//        values.put(BUILDING_Y, build.getY());
//        // updating row
//        return db.update(TABLE_BUILDING, values, BUILDING_TEXT + " = " +build.getText(), null);
//    }
//
//    /*혹시몰라서 넣어놨음*/
//    public void deleteBuilding(String text) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_BUILDING, BUILDING_TEXT + "= " + text, null);
//    }
//
//    /*CRUDs relating MapEdge object*/
//    //Insert 하기 전에 전제는 MapNode에 MapEdge가 1대 다의 관계라는 것
//    public void insertMapEdge(MapEdge edge) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(MAPEDGE_WEIGHT, edge.getWeight());
//        values.put(MAPEDGE_ID, edge.getId());
//        /*edge가 속한 node의 x, y값을 받아 그대로 넣음*/
//        values.put(MAPEDGE_NODE_X, edge.getNodeX());
//        values.put(MAPEDGE_NODE_Y, edge.getNodeY());
//        // insert row
//        db.insert(TABLE_MAPEDGE, null, values);
//    }
////노드에 속한 엣지는 여러개일테니 arraylist를 리턴함
//    public List<MapEdge> getAllMapEdge(int x, int y) {
//        List<MapEdge> edges = new ArrayList<MapEdge>();
//
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        //MapEdge와 MapNode의 x, y키를 비교해 같은걸 뽑음...쿼리가 이게 맞는건지 모르겠음
//        String selectQuery = "SELECT * FROM "+ TABLE_MAPEDGE + " INNER JOIN " + TABLE_MAPNODE
//                            + "ON edge." + x + "="  + "node." +x + " AND " + "edge." + y + "=" + "node." +y;
//
//        Cursor c = db.rawQuery(selectQuery, null);
//
//        //looping through all rows and adding to the list
//        if (c.moveToFirst()) {
//            do {
//                MapEdge edge = new MapEdge();
//                edge.setId(c.getInt(c.getColumnIndex(MAPEDGE_ID)));
//                edge.setWeight((c.getInt(c.getColumnIndex(MAPEDGE_WEIGHT))));
//                // adding to edges arraylist
//                edges.add(edge);
//            } while (c.moveToNext());
//        }
//        return edges;
//    }
//    /*PK로 튜플을 찾아 업데이트*/
//    public int updateMapEdge(MapEdge edge) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(MAPEDGE_NODE_X, edge.getNodeX());
//        values.put(MAPEDGE_NODE_Y, edge.getNodeY());
//        values.put(MAPEDGE_WEIGHT, edge.getWeight());
//        // updating row
//        return db.update(TABLE_MAPEDGE, values, MAPEDGE_ID + " = " +edge.getId(), null);
//    }
//    public void deleteMapEdge(int id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_MAPEDGE, MAPEDGE_ID + "= " + id, null);
//    }
//
//    /*CRUDs relating MapNode*/
//    public void insertMapNode(MapNode node) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(MAPNODE_NAME, node.getName());
//        values.put(MAPNODE_PRIMARY, node.getPrimary());
//        values.put(MAPNODE_X, node.getX());
//        values.put(MAPNODE_Y, node.getY());
//        // insert row
//        db.insert(TABLE_MAPNODE, null, values);
//    }
//    /*같은 x,y값을 갖는 노드는 하나밖에 없으니 튜플 하나만 리턴*/
//    public MapNode getMapNode(int x, int y) {
//
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        String selectQuery = "SELECT * FROM "+ TABLE_MAPNODE + "WHERE " +TABLE_MAPNODE +".x " + "=" + x + "AND"
//                            + TABLE_MAPNODE +".y" + "=" + y;
//
//        Cursor c = db.rawQuery(selectQuery, null);
//        MapNode node=new MapNode();
//
//
//        if (c != null)
//            c.moveToFirst();
//
//        node.setX(c.getInt(c.getColumnIndex(MAPNODE_X)));
//        node.setY(c.getInt(c.getColumnIndex(MAPNODE_Y)));
//        node.setName(c.getString(c.getColumnIndex(MAPNODE_NAME)));
//        node.setPrimary(c.getInt(c.getColumnIndex(MAPNODE_PRIMARY)));
//
//        return node;
//    }
//    public int updateMapNode(MapNode node) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(MAPNODE_PRIMARY, node.getPrimary());
//        values.put(MAPNODE_NAME, node.getName());
//        // updating row
//        return db.update(TABLE_MAPNODE, values, MAPNODE_X + " = " +node.getX() + " AND " + MAPNODE_Y + "=" +node.getY(), null);
//    }
//    public void deleteMapNode(int x, int y) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_MAPNODE, MAPNODE_X + "= " + x + " AND " + MAPNODE_Y + "=" + y, null);
//    }
//
//    /*CRUDs concerning classlist*/
//    public void insertClassList(ClassList list) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(CLASSLIST_NUM, list.getNum());
//        values.put(CLASSLIST_NODE_X, list.getNodeX());
//        values.put(CLASSLIST_NODE_Y, list.getNodeY());
//        // insert row
//        db.insert(TABLE_MAPNODE, null, values);
//    }
//    /*MapNode에 속한 ClassList는 여러개일테니 여러 튜플을 다 리턴함*/
//    public List<ClassList> getAllClassList(int x, int y) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        List<ClassList> lists=new ArrayList<ClassList>();
//        //역시 쿼리가 확실히 이건지 잘 모르겠음....ㅠㅠ
//        String selectQuery = "SELECT * FROM "+ TABLE_CLASSLIST + " INNER JOIN " + TABLE_MAPNODE
//                + "ON classlist." + x + "="  + "node." +x  + " AND " + "classlist." + y + "=" + "node." +"y";
//
//        Cursor c = db.rawQuery(selectQuery, null);
//
//        //looping through all rows and adding to the list
//        if (c.moveToFirst()) {
//            do {
//                ClassList list = new ClassList();
//                list.setNum(c.getInt(c.getColumnIndex(CLASSLIST_NUM)));
//                // adding to edges arraylist
//                lists.add(list);
//            } while (c.moveToNext());
//        }
//        return lists;
//
//    }
//    /*ClassList는 primary key만 갖고 있으므로 update 안함*/
//    public void deleteClassList(int num) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_CLASSLIST, CLASSLIST_NUM + "= " + num , null);
//    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}