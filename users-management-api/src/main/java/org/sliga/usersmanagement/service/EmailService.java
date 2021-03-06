package org.sliga.usersmanagement.service;

import com.sun.mail.smtp.SMTPTransport;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static org.sliga.usersmanagement.utils.EmailConstants.*;

@Service
public class EmailService {

    public void sendWelcomeEmail(String firstname, String username, String email) {
        try {
            Message message = createWelcomeEmail(firstname, username, email);
            SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
            smtpTransport.connect(GMAIL_SMTP_SERVER, EMAIL_ACCOUNT_USERNAME, EMAIL_ACCOUNT_PASSWORD);
            smtpTransport.sendMessage(message, message.getAllRecipients());
            smtpTransport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendNewPasswordEmail(String firstname, String username, String password, String email) {
        try {
            Message message = createNewPasswordEmail(firstname, username, password, email);
            SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
            smtpTransport.connect(GMAIL_SMTP_SERVER, EMAIL_ACCOUNT_USERNAME, EMAIL_ACCOUNT_PASSWORD);
            smtpTransport.sendMessage(message, message.getAllRecipients());
            smtpTransport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendUpdateUserEmail(String firstname, String username, String email) {
        try {
            Message message = createUpdateUserEmail(firstname, username, email);
            SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
            smtpTransport.connect(GMAIL_SMTP_SERVER, EMAIL_ACCOUNT_USERNAME, EMAIL_ACCOUNT_PASSWORD);
            smtpTransport.sendMessage(message, message.getAllRecipients());
            smtpTransport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    private Message createWelcomeEmail(String firstname, String username, String email) throws MessagingException {
        Message message = createEmail(email);
        message.setText(getWelcomeEmailContent(firstname, username));
        message.saveChanges();
        return message;
    }

    private Message createUpdateUserEmail(String firstname, String username, String email) throws MessagingException {
        Message message = createEmail(email);
        message.setText(getUpdateUserEmailContent(firstname, username));
        message.saveChanges();
        return message;
    }

    private Message createNewPasswordEmail(String firstname, String username, String password, String email) throws MessagingException {
        Message message = createEmail(email);
        message.setText(getNewPasswordEmailContent(firstname, username, password));
        message.saveChanges();
        return message;
    }

    private Message createEmail(String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(EMAIL_ISSUER));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(EMAIL_CC, false));
        message.setSubject(EMAIL_SUBJECT);
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    private String getWelcomeEmailContent(String firstname, String username){
        return String.format("Hi %s ! \n\n" +
                "           WELCOME dear!  \n\n" +
                "Your account is created and then active. \n" +
                "You can now get access by going on the login page and log in with your login details.\n" +
                "      Here are your login details : \n" +
                "- username : %s\n" +
                "- password: the one that you gave when registering.\n" +
                "\n" +
                "Feel free to contact your administration for any concerns about your account.", firstname, username);
    }

    private String getNewPasswordEmailContent(String firstname, String username, String password){
        return String.format("Hi %s ! \n\n" +
                "Your password has been reset at your request. \n" +
                "Here are your current login details.\n" +
                "- username : %s\n" +
                "- password: %s.\n" +
                "\n" +
                "Feel free to contact your administration for any concerns about your account.", firstname, username, password);
    }

    private String getUpdateUserEmailContent(String firstname, String username){
        return String.format("Hi %s ! \n\n" +
                "Your account has been updated at your request. \n" +
                "Here are your current login details.\n" +
                "Here are your current login details.\n" +
                "- username : %s\n" +
                "- password: the one that you gave when registering.\n\n" +
                "Log in to get more details of the updated information."+
                "\n" +
                "Feel free to contact your administration for any concerns about your account.", firstname, username);
    }

    private Session getEmailSession(){
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST,GMAIL_SMTP_SERVER);
        properties.put(SMTP_AUTH,true);
        properties.put(SMTP_PORT,DEFAULT_PORT);
        properties.put(SMTP_STARTTLS_ENABLE,true);
        properties.put(SMTP_STARTTLS_REQUIRED,true);
        return Session.getDefaultInstance(properties);
    }
}
