package skku.roma.roadmaster.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by takeclassic on 2016-05-05.
 */

public class DB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "RoadMasterDB.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_BUILDING = "building";
    private static final String BUILDING_X="x";
    private static final String BUILDING_Y="y";
    private static final String BUILDING_TEXT = "text";

    private static final String TABLE_MAPNODE = "node";
    private static final String MAPNODE_X="x";
    private static final String MAPNODE_Y="y";
    private static final String MAPNODE_PRIMARY = "primary";
    private static final String MAPNODE_NAME = "name";

    /*MapNode의 classlist를 하나의 table로 뺐음 MapNode와 Classlist가 1:다의 관계로 파악*/
    private static final String TABLE_CLASSLIST = "classlist";
    //CLASSLIST에 들어있는 하나의 번호를 표현
    private static final String CLASSLIST_NUM="num";
    //MapNode의 PK를 FK로 가질 변수들
    private static final String CLASSLIST_NODE_X="node_x";
    private static final String CLASSLIST_NODE_Y="node_y";

    /*"MapNode:MapEdge = 1:다" 로 파악함*/
    private static final String TABLE_MAPEDGE= "edge";
    private static final String MAPEDGE_WEIGHT="weight";
    //MapNode를 realation으로 매핑하기 위해 만든 PK
    private static final String MAPEDGE_ID="id";
    //MapNode의 PK를 FK로 가질 변수들
    private static final String MAPEDGE_NODE_X="node_x";
    private static final String MAPEDGE_NODE_Y="node_y";

    /*똑같은 건물명은 없고, 사용자는 건물명으로 검색할테니 건물명을 primary key로 함*/
    private static final String CREATE_BUILDING_TABLE = "CREATE TABLE " + TABLE_BUILDING + "("
            + BUILDING_X + " INTEGER," + BUILDING_Y + " INTEGER," + BUILDING_TEXT + " TEXT PRIMARY KEY"
            + ")";

    private static final String INSERT_BUILDING_TABLE = "INSERT INTO " + TABLE_BUILDING +
            " SELECT 1634 AS " + BUILDING_X + ", 1819 AS " + BUILDING_Y + ", '성균관대학교\n 자연과학캠퍼스' AS " + BUILDING_TEXT +
            " UNION ALL SELECT 2487, 991, '제2공학관'" +
            " UNION ALL SELECT 834, 373, '신관 A동'" +
            " UNION ALL SELECT 1072, 501, '신관 B동'";

//    private static final String CREATE_MAPNODE_TABLE = "CREATE TABLE " + TABLE_MAPNODE + "("
//            + MAPNODE_X + " INTEGER NOT NULL," + MAPNODE_Y + " INTEGER NOT NULL," + MAPNODE_NAME + " TEXT,"
//            + MAPNODE_PRIMARY + "INTEGER," + " PRIMARY KEY (x,y)" + ")";
//
//    /*안드로이드는 프로요 2.2부터 FK를 지원함*/
//    private static final String CREATE_MAPEDGE_TABLE = "CREATE TABLE " + TABLE_MAPEDGE + "("
//            + MAPEDGE_WEIGHT + " INTEGER," + MAPEDGE_ID + " INTEGER PRIMARY KEY," + MAPEDGE_NODE_X + " INTEGER," + MAPEDGE_NODE_Y + " INTEGER,"
//            + " FOREIGN KEY (x,y) REFERENCES node(x,y)" +")";
//
//    private static final String CREATE_CLASSLIST_TABLE = "CREATE TABLE " + TABLE_CLASSLIST + "("
//            + CLASSLIST_NUM + " INTEGER PRIMARY KEY," + CLASSLIST_NODE_X +" INTEGER," + CLASSLIST_NODE_Y + " INTEGER,"
//            + " FOREIGN KEY (x,y) REFERENCES node(x,y)" + ")";


    public DB(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BUILDING_TABLE);
        db.execSQL(INSERT_BUILDING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUILDING);
        // create new tables
        onCreate(db);
    }

    public ArrayList<Building> getBuildings(){
        ArrayList<Building> buildings = new ArrayList<Building>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_BUILDING, null);
        if(c.moveToFirst()){
            while(!c.isAfterLast()){
                buildings.add(new Building(c.getInt(c.getColumnIndex(BUILDING_X)), c.getInt(c.getColumnIndex(BUILDING_Y)), c.getString(c.getColumnIndex(BUILDING_TEXT))));
                c.moveToNext();
            }
        }

        return  buildings;
    }


//    /*CRUDs concerning Building object*/
//    public void insertBuilding(Building build) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(BUILDING_X, build.getX());
//        values.put(BUILDING_Y, build.getY());
//        values.put(BUILDING_TEXT, build.getText());
//
//        // insert row
//        db.insert(TABLE_BUILDING, null, values);
//    }
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