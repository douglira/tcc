package models;

import enums.MessengerType;

public class Messenger {
	private String content;
	private MessengerType type;

	public Messenger(String content, MessengerType type) {
		this.content = content;
		this.type = type;
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
}
