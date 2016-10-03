package com.z_waligorski.simpleemailclient.Fragments.TabFragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.z_waligorski.simpleemailclient.Database.DBManager;
import com.z_waligorski.simpleemailclient.Email.EmailReceiver;
import com.z_waligorski.simpleemailclient.Activities.EmailViewActivity;
import com.z_waligorski.simpleemailclient.R;
import com.z_waligorski.simpleemailclient.UI.RefreshUI;


public class InboxFragment extends Fragment implements SimpleCursorAdapter.ViewBinder, AdapterView.OnItemClickListener {
    private ListView inboxList;
    private FrameLayout buttonFrame;
    private DBManager dbManager;
    private SimpleCursorAdapter adapter;
    private Cursor cursor;

    public InboxFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = new DBManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        inboxList = (ListView) view.findViewById(R.id.inbox_list);
        buttonFrame = (FrameLayout) view.findViewById(R.id.buttonFrame);
        buttonFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiveMessages();
            }
        });

        cursor = dbManager.getAll();

        // Adapter for ListView
        adapter = new SimpleCursorAdapter(getActivity(),
                R.layout.inbox_single_item,
                cursor,
                new String[] {"_id", "address", "date", "subject", "new"},
                new int[] {R.id.mail_id, R.id.sender, R.id.date, R.id.subject, R.id.new_message},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        adapter.setViewBinder(this);

        inboxList.setAdapter(adapter);

        inboxList.setOnItemClickListener(this);

        return view;
    }

    // Download emails and refresh ListView
    public void receiveMessages(){
        dbManager = new DBManager(getActivity());
        RefreshUI refresh = new RefreshUI(dbManager, cursor, adapter);
        EmailReceiver r = new EmailReceiver(getActivity(), refresh);
        r.execute();
    }

    // Binds cursor column with proper view
    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        switch (view.getId()) {
            case R.id.sender:
                String add = cursor.getString(columnIndex);
                ((TextView) view).setText(add);
                return true;
            case R.id.date:
                String date = cursor.getString(columnIndex);
                ((TextView) view).setText(date);
                return true;
            case R.id.subject:
                String subject = cursor.getString(columnIndex);
                ((TextView) view).setText(subject);
                return true;
            case R.id.new_message:
                String newMessage = cursor.getString(columnIndex);
                ((TextView) view).setText(newMessage);
            default:
                return false;
        }
    }

    // On click method for ListView
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView mailId = (TextView) view.findViewById(R.id.mail_id);
        String clickedId = mailId.getText().toString();

        Intent intent = new Intent(getActivity(), EmailViewActivity.class);
        intent.putExtra("id", clickedId);
        startActivity(intent);
    }
}
