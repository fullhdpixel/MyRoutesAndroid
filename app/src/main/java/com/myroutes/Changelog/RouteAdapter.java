package com.myroutes.Changelog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.myroutes.R;

import java.util.ArrayList;

/**
 * Created by thijssmudde on 22/05/2017.
 */

public class RouteAdapter extends BaseAdapter implements ListAdapter {
  private ArrayList<OnlineRoute> list = new ArrayList<>();
  private Context context;

  public RouteAdapter(ArrayList<OnlineRoute> list, Context context) {
    this.list = list;
    this.context = context;
  }

  @Override
  public int getCount() {
    return list.size();
  }

  @Override
  public Object getItem(int pos) {
    return list.get(pos);
  }

  @Override
  public long getItemId(int pos) {
    return 0;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    View view = convertView;
    if (view == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.routeitem, null);
    }

    //Handle TextView and display string from your list
    TextView date = (TextView)view.findViewById(R.id.route_date);
    TextView distance = (TextView)view.findViewById(R.id.route_distance);
    TextView name = (TextView)view.findViewById(R.id.route_name);

    date.setText(list.get(position).name);
    distance.setText(list.get(position).name);
    name.setText(list.get(position).name);

    return view;
  }
}
