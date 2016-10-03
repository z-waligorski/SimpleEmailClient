package com.z_waligorski.simpleemailclient.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.z_waligorski.simpleemailclient.Database.DBManager;
import com.z_waligorski.simpleemailclient.R;

import java.util.HashMap;

public class EmailViewActivity extends AppCompatActivity {

    private TextView emailFrom;
    private TextView emailDate;
    private TextView emailSubject;

    private WebView emailContent;
    private HashMap<String, String> mailMap;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_view);

        emailFrom = (TextView) findViewById(R.id.email_from);
        emailDate = (TextView) findViewById(R.id.email_date);
        emailSubject = (TextView) findViewById(R.id.email_subject);
        emailContent = (WebView) findViewById(R.id.email_content);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        int messageId = Integer.parseInt(id);

        // Get data from database
        DBManager dbManager = new DBManager(this);
        mailMap = dbManager.getMessage(messageId);

        emailFrom.setText("From: " + mailMap.get("address"));
        emailDate.setText(mailMap.get("date"));
        emailSubject.setText(mailMap.get("subject"));

        // Load data to WebView
        emailContent.loadDataWithBaseURL(null, mailMap.get("content"), "text/html", "UTF-8", null);

        // Mark message as read
        dbManager.markMessageAsRead(messageId);
    }
}
