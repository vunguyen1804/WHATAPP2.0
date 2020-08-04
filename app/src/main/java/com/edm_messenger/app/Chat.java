/*
 * Name: Zuhao Zhang
 *       Adrian Wong
 *       Yuxiang Liu
 *       Zhe Xu
 *       Vu Nguyen
 *       Boram Wong
 *       Srinivas Venkatraman
 *       Apurva Goenka
 *       Steven Kan
 *       Edgar Matias
 *
 * Project: EDM-Messenger
 * Date: 9/23/2019
 * Description: UCSD Fall 2019 Gary's CSE 110 Team EDM's messenger project
 */
package com.edm_messenger.app;

/**
 *  Class: Chat.java
 *  Description: Chat object to handle chat's information from Firebase
 */
public class Chat {
    // static variables for client to use to send load/sent message
    public final static String TEXT_TYPE = "text";
    public final static String IMAGE_TYPE = "image";
    public final static String LOCATION_TYPE = "location";
    public final static String PDF_TYPE = "pdf";
    public final static String IMAGE_EXTENSION = ".jpg";
    public final static String PDF_EXTENSION = ".pdf";
    public final static String CONTENT_KEY = "content";
    public final static String SENDER_KEY = "sender";
    public final static String TYPE_KEY = "type";

    private String sender;
    private String content;
    private String type;
    private String timeStamp;

    // empty Constructor, required for Firebase
    public Chat() {

    }

    public Chat(String sender, String content, String type) {

        this.sender = sender;

        this.type = type;

        this.content = content;
    }

    public Chat(String sender, String content, String type, String timeStamp) {

        this.sender = sender;

        this.type = type;

        this.content = content;

        this.timeStamp = timeStamp;

    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

}
