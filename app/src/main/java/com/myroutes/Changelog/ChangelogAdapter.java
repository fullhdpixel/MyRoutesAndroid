package com.myroutes.Changelog;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.myroutes.R;

import java.util.ArrayList;

public class ChangelogAdapter extends BaseAdapter implements ListAdapter {
  private ArrayList<ChangeLogUpdates> list = new ArrayList<>();
  private Context context;

  public ChangelogAdapter(ArrayList<ChangeLogUpdates> list, Context context) {
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
      view = inflater.inflate(R.layout.changelogitem, null);
    }

    //Handle TextView and display string from your list
    TextView date = (TextView)view.findViewById(R.id.changelog_date);
    TextView version = (TextView)view.findViewById(R.id.changelog_version);
    TextView text = (TextView)view.findViewById(R.id.changelog_text);

    date.setText(list.get(position).date);
    version.setText(list.get(position).version);
    text.setText(Html.fromHtml(list.get(position).text));

    return view;
  }
}