package data.model;

import com.google.gson.annotations.SerializedName;

public class NotificationData {

    @SerializedName("pid")
    private int notificId;

    @SerializedName("from")
    private String senderName;

    @SerializedName("txt")
    private String text;

    @SerializedName("tip")
    private String tip;

    public int getNotificId() {
        return notificId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getText() {
        return text;
    }

    public String getTip() {
        return tip;
    }
}
