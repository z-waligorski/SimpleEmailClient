package com.z_waligorski.simpleemailclient.Email;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.net.UnknownHostException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class SendEmail extends AsyncTask<Void, Void, Void> {

    private String userAddress;
    private String password;
    private String smtpHost;
    private String smtpPort;

    private String emailAddress;
    private String emailSubject;
    private String emailText;

    private Context context;
    private Session session;

    private String exceptionMessage = null;

    private ProgressDialog progressDialog;

    public SendEmail(Context context, String emailAddress, String emailSubject, String emailText) {
        this.context = context;
        this.emailAddress = emailAddress;
        this.emailSubject = emailSubject;
        this.emailText = emailText;
    }

    // Get data from SharedPreferences
    public void connectionSettings() {
        SharedPreferences sharedP = PreferenceManager.getDefaultSharedPreferences(context);
        userAddress = sharedP.getString("USER_ADDRESS", "");
        password = sharedP.getString("PASSWORD", "");
        smtpHost = sharedP.getString("SMTPHOST", "");
        smtpPort = sharedP.getString("SMTPPORT", "");
    }

    // Display ProgressDialog during sending email
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, "Sending message", "Please wait...");
    }

    // Remove ProgressDialog when sending is completed
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
        if(exceptionMessage != null) {
            Toast.makeText(context, exceptionMessage, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Message send", Toast.LENGTH_LONG).show();
        }
    }

    // Performs creating and sending message
    @Override
    protected Void doInBackground(Void... params) {
        connectionSettings();
        // Properties object contains information necessary for sending email
        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.port", smtpPort);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        // Create a mail session with PasswordAuthenticator
        session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userAddress, password);
            }
        });

        try{
            // Create mime message
            MimeMessage mimeMessage = new MimeMessage(session);

            mimeMessage.setFrom(new InternetAddress(userAddress));
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(emailAddress));
            mimeMessage.setSubject(emailSubject);
            mimeMessage.setText(emailText);

            // Send email
            Transport.send(mimeMessage);

        } catch (MessagingException e) {
            e.printStackTrace();
            if(e.getNextException() instanceof UnknownHostException) {
                exceptionMessage = "Failed. Check internet connection";
            } else {
                exceptionMessage = "Failed. Check recipient address and app settings";
            }
        }

        return null;
    }
}
