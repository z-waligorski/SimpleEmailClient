package com.z_waligorski.simpleemailclient.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.z_waligorski.simpleemailclient.UI.LeftMenuItem;
import com.z_waligorski.simpleemailclient.R;

import java.util.List;

// Adapter for NavigationDrawer
public class MenuAdapter extends ArrayAdapter<LeftMenuItem> {

    private Context context;
    private int layout;
    private List<LeftMenuItem> listMenuItem;

    public MenuAdapter (Context context, int layout, List listMenuItem) {
        super(context, layout, listMenuItem);
        this.context = context;
        this.layout = layout;
        this.listMenuItem = listMenuItem;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = View.inflate(context, layout, null);

        TextView title = (TextView) view.findViewById(R.id.title);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);

        LeftMenuItem menuItem = listMenuItem.get(position);

        title.setText(menuItem.getTitle());
        icon.setImageResource(menuItem.getIcon());

        return view;
    }
}
