package skku.roma.roadmaster;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import skku.roma.roadmaster.util.Classroom;
import skku.roma.roadmaster.util.DB;
import skku.roma.roadmaster.util.SearchAdapter;


public class SearchActivity extends ActionBarActivity {

    ActionBar actionBar;
    EditText text;
    ImageButton delete;
    ImageButton search;
    InputMethodManager inputMethodManager;

    RelativeLayout empty;

    DB db;
    String type;
    ArrayList<Classroom> classes;
    ListView searchList;
    SearchAdapter searchAdapter;
    TextView searchCache;

    public static int RESULT_SELECT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        empty = (RelativeLayout) findViewById(R.id.search_empty);
        searchCache = (TextView) findViewById(R.id.search_cache);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = LayoutInflater.from(this);
        View customView = inflater.inflate(R.layout.custom_actionbar3, null);

        text = (EditText) customView.findViewById(R.id.actionbar3_text);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if(charSequence.length() == 0){
                    setDelete(false);
                }
                else{
                    setDelete(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                final boolean isEnter = keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN;
                if(isEnter){
                    inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    text.clearFocus();
                    search.callOnClick();
                    return true;
                }
                return false;
            }
        });

        search = (ImageButton) customView.findViewById(R.id.actionbar3_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!text.getText().toString().isEmpty()) {
                    classes = db.searchClass(text.getText().toString());
                    refreshClasses();
                    searchCache.setVisibility(View.GONE);
                }
                else{
                    Toast.makeText(getApplicationContext(), "검색어를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        delete = (ImageButton) customView.findViewById(R.id.actionbar3_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text.setText("");
                text.requestFocus();
            }
        });

        actionBar.setCustomView(customView);
        Toolbar toolbar = (Toolbar) customView.getParent();
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setPadding(0, 0, 0, 0);

        db = new DB(this);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        String key = intent.getStringExtra("string");
        if(!type.equals("search")){
            text.setText(key);
            classes = db.searchClass(key);
            if(classes.isEmpty()){
                empty.setVisibility(View.VISIBLE);
            }
        }
        else{
            searchCache.setVisibility(View.VISIBLE);
            classes = db.getCache();
            Collections.reverse(classes);
            if(classes.size() > 20){
                classes = new ArrayList<Classroom>(classes.subList(0, 20));
            }
        }

        searchList = (ListView) findViewById(R.id.search_list);
        searchAdapter = new SearchAdapter(this, R.layout.custom_listview_item, classes);
        searchList.setAdapter(searchAdapter);
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent1 = new Intent();
                intent1.putExtra("type", type);
                intent1.putExtra("primary", classes.get(i).primary);
                setResult(RESULT_SELECT, intent1);
                db.insertCache(classes.get(i).primary);
                finish();
            }
        });
    }

    private void setDelete(boolean activate){
        if(activate){
            delete.setVisibility(View.VISIBLE);
            delete.setClickable(true);
        }
        else{
            delete.setVisibility(View.INVISIBLE);
            delete.setClickable(false);
        }
    }

    private void refreshClasses(){
        if(classes.isEmpty()){
            empty.setVisibility(View.VISIBLE);
        }
        else{
            empty.setVisibility(View.GONE);
        }
        searchAdapter.setList(classes);
        searchAdapter.notifyDataSetChanged();
    }
}
