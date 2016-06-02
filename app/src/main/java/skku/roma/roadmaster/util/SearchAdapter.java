package skku.roma.roadmaster.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import skku.roma.roadmaster.R;

/**
 * Created by nyu53 on 2016-06-02.
 */
public class SearchAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<Classroom> list;
    LayoutInflater inflater;

    public SearchAdapter(Context context, int layout, ArrayList<Classroom> list) {
        this.context = context;
        this.layout = layout;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = inflater.inflate(layout, viewGroup, false);
        }

        TextView primary = (TextView) view.findViewById(R.id.listview_primary);
        primary.setText(list.get(i).primary);

        TextView name = (TextView) view.findViewById(R.id.listview_name);
        name.setText(list.get(i).name);

        return view;
    }

    public void setList(ArrayList<Classroom> list){
        this.list = list;
    }
}
