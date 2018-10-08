package models;

import enums.MessengerType;

public class Messenger {
    private String content;
    private MessengerType type;
    private String cause;

    public Messenger(String content, MessengerType type) {
        this.content = content;
        this.type = type;
    }

    public Messenger(String content, MessengerType type, String cause) {
        this.content = content;
        this.type = type;
        this.cause = cause;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessengerType getType() {
        return type;
    }

    public void setType(MessengerType type) {
        this.type = type;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}
