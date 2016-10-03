package com.z_waligorski.simpleemailclient.Service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;

import com.z_waligorski.simpleemailclient.Activities.MainActivity;
import com.z_waligorski.simpleemailclient.Database.DBManager;
import com.z_waligorski.simpleemailclient.R;

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

// IntentService for checking email account
public class InboxService extends IntentService {

    private Boolean newMessage = false;
    private String firstInboxMessageSubject;
    private String firstInboxMessageSender;

    private String imap;
    private String userAddress;
    private String password;
    private int emailsToLoad;

    public InboxService() {
        super("");
    }

    // Check if there are new emails. If yes - make notification
    @Override
    protected void onHandleIntent(Intent intent) {
        checkEmail();
        if (newMessage) {
            this.sendNotification(this);
        }
    }

    // Get settings from SharedPreferences
    public void connectionSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        imap = sharedPreferences.getString("IMAP", "");
        userAddress = sharedPreferences.getString("USER_ADDRESS", "");
        password = sharedPreferences.getString("PASSWORD", "");
        emailsToLoad = Integer.parseInt(sharedPreferences.getString("NUMBER_OF_EMAILS", "0"));
    }

    protected void checkEmail(){

        connectionSettings();
        String mailFrom;
        String mailDate;
        String mailSubject;
        String mailContent;
        String messageNew;

        Properties properties = new Properties();
        properties.setProperty("mail.store.protocol", "imaps");

        try {
            Session session = Session.getInstance(properties, null);

            Store store = session.getStore();
            store.connect(imap, userAddress, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message firstInboxMessage = inbox.getMessage(inbox.getMessageCount());

            // Check if first inbox message is unread
            // If yes - check if it has different subject from first message in database
            if(!firstInboxMessage.getFlags().contains(Flags.Flag.SEEN)) {
                firstInboxMessageSubject = firstInboxMessage.getSubject();

                DBManager dbManager = new DBManager(getApplicationContext());
                HashMap<String, String> firstMessage = dbManager.getMessage(1);
                String dbMessageSubject = firstMessage.get("subject");

                if (!dbMessageSubject.equals(firstInboxMessageSubject)) {

                    newMessage = true;

                    //Save address of first message sender. It will help to create notification
                    firstInboxMessageSender = "";
                    Address[] firstAddress = firstInboxMessage.getFrom();
                    for (Address a : firstAddress) {
                        firstInboxMessageSender += ((InternetAddress) a).getPersonal() == null ? a.toString() : ((InternetAddress) a).getPersonal();
                    }

                    ArrayList<HashMap<String, String>> mailList = new ArrayList<HashMap<String, String>>();

                    // Iterate through emails and add them to List
                    for (int i = 0; i < emailsToLoad; i++) {
                        Message message = inbox.getMessage(inbox.getMessageCount() - i);
                        Address[] address = message.getFrom();
                        mailFrom = "";

                        for (Address a : address) {
                            mailFrom += ((InternetAddress) a).getPersonal() == null ? a.toString() : ((InternetAddress) a).getPersonal();
                        }

                        if(!message.getFlags().contains(Flags.Flag.SEEN)) {
                            messageNew = "NEW";
                        } else messageNew = "";

                        SimpleDateFormat format = new SimpleDateFormat("MMM d");
                        mailDate = format.format(message.getReceivedDate());

                        mailSubject = message.getSubject();

                        // Search for main text content
                        mailContent = "";
                        Object content = message.getContent();
                        if (content instanceof String) {
                            mailContent = (String) content;
                        } else if (content instanceof Multipart) {
                            Multipart mp = (Multipart) content;

                            for (int iter = 0; iter < mp.getCount(); iter++) {
                                BodyPart bp = mp.getBodyPart(iter);
                                Object bpContent = bp.getContent();
                                if (bpContent instanceof String) {
                                    mailContent = bpContent.toString();
                                }
                            }
                        }

                        HashMap<String, String> mailMap = new HashMap<String, String>();
                        mailMap.put("address", mailFrom);
                        mailMap.put("date", mailDate);
                        mailMap.put("subject", mailSubject);
                        mailMap.put("content", mailContent);
                        mailMap.put("new", messageNew);

                        mailList.add(mailMap);

                        // Delete old messages and save new
                        dbManager.deleteMessages();
                        dbManager.insertMessages(mailList);
                    }
                }
            }

            inbox.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Make a notification about new message
    private void sendNotification(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setTicker("New message")
                .setSmallIcon(R.drawable.email)
                .setContentTitle("From: "+ firstInboxMessageSender)
                .setContentText(firstInboxMessageSubject)
                .setAutoCancel(true);
        nManager.notify(0, builder.build());
    }
}
