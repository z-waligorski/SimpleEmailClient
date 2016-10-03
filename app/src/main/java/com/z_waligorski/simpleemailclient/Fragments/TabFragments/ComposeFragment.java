package com.z_waligorski.simpleemailclient.Fragments.TabFragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.z_waligorski.simpleemailclient.R;
import com.z_waligorski.simpleemailclient.Email.SendEmail;

public class ComposeFragment extends Fragment {

    private EditText address;
    private EditText subject;
    private EditText message;

    public ComposeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compose, container, false);

        address = (EditText) view.findViewById(R.id.address);
        subject = (EditText) view.findViewById(R.id.subject);
        message = (EditText) view.findViewById(R.id.message);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendEmail();
            }
        });

        return view;
    }

    public void sendEmail() {
        String addressText = address.getText().toString();
        String subjectText = subject.getText().toString();
        String mesageText = message.getText().toString();

        SendEmail sendEmail = new SendEmail(getActivity(), addressText, subjectText, mesageText);
        sendEmail.execute();
    }
}
