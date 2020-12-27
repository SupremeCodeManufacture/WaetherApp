package logic.push_notification;

import com.google.gson.annotations.SerializedName;

public class CloudDataObj {

    @SerializedName("tip")
    private String tipText;

    @SerializedName("msg")
    private MsgNotification userNotification;


    public String getTipText() {
        return tipText;
    }

    public MsgNotification getUserNotification() {
        return userNotification;
    }

    public void setTipText(String tipText) {
        this.tipText = tipText;
    }

    public void setUserNotification(MsgNotification userNotification) {
        this.userNotification = userNotification;
    }

    public static class MsgNotification {

        @SerializedName("pid")
        private int notificId;

        @SerializedName("from")
        private String senderName;

        @SerializedName("txt")
        private String text;


        public int getNotificId() {
            return notificId;
        }

        public String getSenderName() {
            return senderName;
        }

        public String getText() {
            return text;
        }
    }
}