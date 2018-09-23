package models.socket;

import enums.NotificationStatus;
import enums.NotificationTopic;
import models.User;

public class Notification {
	private User from = new User() ;
	private User to = new User();
	private NotificationTopic topic;
	private String action;
	private String content;
	private NotificationStatus status;

	public User getFrom() {
		return from;
	}

	public void setFrom(User from) {
		this.from = from;
	}

	public User getTo() {
		return to;
	}

	public void setTo(User to) {
		this.to = to;
	}

	public NotificationTopic getTopic() {
		return topic;
	}

	public void setTopic(NotificationTopic topic) {
		this.topic = topic;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public NotificationStatus getStatus() {
		return status;
	}

	public void setStatus(NotificationStatus status) {
		this.status = status;
	}
}
