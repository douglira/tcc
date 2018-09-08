package models;

import java.util.Calendar;

import enums.Status;

public class Category {
	private Integer id;
	private String title;
	private String description;
	private int layer;
	private boolean isLastChild;
	private Category parent;
	private Status status;
	private Calendar createdAt;
	private Calendar updatedAt;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public boolean isLastChild() {
		return isLastChild;
	}

	public void setLastChild(boolean isLastChild) {
		this.isLastChild = isLastChild;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Calendar getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Calendar createdAt) {
		this.createdAt = createdAt;
	}

	public Calendar getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Calendar updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public void toggleStatus() {
		if (this.status == Status.ACTIVE) {
			this.status = Status.INACTIVE;
		} else {
			this.status = Status.ACTIVE;
		}
	}
}
