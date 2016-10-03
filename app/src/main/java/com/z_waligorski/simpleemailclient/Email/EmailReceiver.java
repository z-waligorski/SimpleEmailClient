package com.z_waligorski.simpleemailclient.Email;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.z_waligorski.simpleemailclient.Database.DBManager;
import com.z_waligorski.simpleemailclient.UI.RefreshUI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;

public class EmailReceiver extends AsyncTask<Void, Void, Void> {

    private Context context;
    private String imap;
    private String userAddress;
    private String password;
    private int emailsToLoad;
    private boolean exceptionOccured = false;
    private ProgressDialog progressDialog;
    private RefreshUI refreshUI;

    public EmailReceiver(Context context, RefreshUI refreshUI) {
        this.context = context;
        this.refreshUI = refreshUI;
    }

    // Get settings from SharedPreferences
    public void connectionSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        imap = sharedPreferences.getString("IMAP", "");
        userAddress = sharedPreferences.getString("USER_ADDRESS", "");
        password = sharedPreferences.getString("PASSWORD", "");
        emailsToLoad = Integer.parseInt(sharedPreferences.getString("NUMBER_OF_EMAILS", ""));
    }

    // Show dialog box when background process is carried out
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Refreshing messages");
        progressDialog.setMessage("Please wait...");
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "HIDE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        progressDialog.show();
    }

    // Dismiss dialog box when background process is finished and refresh UI
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
        refreshUI.refresh();
        if(exceptionOccured)
            Toast.makeText(context, "Failed. Check connection settings", Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        connectionSettings();

        String mailFrom;
        String mailDate;
        String mailSubject;
        String mailContent;
        String newMessage;

        // Set properties for communication session
        Properties properties = new Properties();
        properties.setProperty("mail.store.protocol", "imaps");

        try {
            Session session = Session.getInstance(properties, null);
            Store store = session.getStore();
            store.connect(imap, userAddress, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            ArrayList<HashMap<String, String>> mailList = new ArrayList<HashMap<String, String>>();

            // Iterate through messages and add them to ArrayList
            for(int i = 0; i<emailsToLoad; i++) {
                Message message = inbox.getMessage(inbox.getMessageCount() - i);
                Address[] address = message.getFrom();
                mailFrom = "";

                // If address contains sender's name - get name, if not - get raw address
                for(Address a : address) {
                    mailFrom += ((InternetAddress) a).getPersonal() == null ? a.toString() : ((InternetAddress) a).getPersonal();
                }

                if(!message.getFlags().contains(Flags.Flag.SEEN)) {
                    newMessage = "NEW";
                } else newMessage = "";

                SimpleDateFormat format = new SimpleDateFormat("MMM d");
                mailDate = format.format(message.getReceivedDate());

                mailSubject = message.getSubject();

                // Search for main text content
                mailContent = "";
                Object content = message.getContent();
                if(content instanceof String){
                    mailContent = (String) content;
                } else if(content instanceof Multipart) {
                    Multipart mp = (Multipart) content;

                    for(int iter = 0; iter < mp.getCount(); iter++) {
                        BodyPart bp = mp.getBodyPart(iter);
                        Object bpContent = bp.getContent();
                        if(bpContent instanceof String) {
                            mailContent = bpContent.toString();
                        }
                    }
                }

                HashMap<String, String> mailMap = new HashMap<String, String>();
                mailMap.put("address", mailFrom);
                mailMap.put("date", mailDate);
                mailMap.put("subject", mailSubject);
                mailMap.put("content", mailContent);
                mailMap.put("new", newMessage);

                mailList.add(mailMap);
            }

            DBManager manager = new DBManager(context);

            // Delete messages from database and insert new data
            manager.deleteMessages();
            manager.insertMessages(mailList);

            inbox.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
            exceptionOccured = true;
        }

        return null;
    }
}
