package org.example.model;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {

    private String id;                  // MongoDB document id
    private String userEmail;           // owner of the task

    private String title;
    private String description;

    private LocalDateTime createdAt;  // When the task is added

    private LocalDate dueDate;          // e.g. 2025-11-20

    private int priority;               // Low, Medium, High
    private String status;              // Pending, Completed, etc.

    public Task() {}

    public Task(String userEmail, String title, String description,
                LocalDate dueDate, int priority, String status) {

        this.userEmail = userEmail;
        this.title = title;
        this.description = description;

        this.createdAt = LocalDateTime.now();

        this.dueDate = dueDate;

        this.setPriority(priority);
        this.status = (status == null || status.isBlank()) ? "Pending" : status;
    }

    // --------------------------------------------
    // GETTERS & SETTERS
    // --------------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail.toLowerCase();
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

    // Prevent multi-line text breaking storage read/write
    public void setDescription(String description) {
        if (description == null) {
            this.description = "";
            return;
        }
        this.description = description.replace("\n", " ");
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }


    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = normalizePriority(priority);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = (status == null || status.isBlank()) ? "Pending" : status;
    }

    // --------------------------------------------
    // Utility
    // --------------------------------------------

    public String getDeadlineFormatted() {
        if (dueDate == null)
            return "";
        return dueDate.toString();
    }

    // Convert Task -> Document
    public Document toDocument() {
        Document doc = new Document();

        if (id != null && ObjectId.isValid(id)) {
            doc.put("_id", new ObjectId(id));
        }

        doc.append("userEmail", userEmail)
                .append("title", title)
                .append("description", description)
                .append("priority", priority)
                .append("status", status)
                .append("dueDate", dueDate != null ? dueDate.toString() : null)
                .append("createdAt", (createdAt != null ? createdAt : LocalDateTime.now()).toString());

        return doc;
    }


    // Convert Document -> Task: phải cẩn thận hai cái này
    public static Task fromDocument(Document doc) {

        String dueDateStr = doc.getString("dueDate");
        LocalDate dueDate = null;
        if (dueDateStr != null && !dueDateStr.isBlank()) {
            dueDate = LocalDate.parse(dueDateStr);
        }

        Integer priorityValue = doc.getInteger("priority");
        int priority = priorityValue != null ? priorityValue : 1;
        String status = doc.getString("status");
        if (priorityValue != null && priorityValue <= 0) {
            status = "Completed";
            priority = 1;
        }

        Task t = new Task(
                doc.getString("userEmail"),
                doc.getString("title"),
                doc.getString("description"),
                dueDate,
                priority,
                status
        );

        ObjectId oid = doc.getObjectId("_id");
        t.id = oid != null ? oid.toHexString() : null;

        if (doc.containsKey("createdAt") && doc.getString("createdAt") != null) {
            t.createdAt = LocalDateTime.parse(doc.getString("createdAt"));
        }

        return t;
    }

    private int normalizePriority(int value) {
        if (value < 1) {
            return 1;
        }
        if (value > 3) {
            return 3;
        }
        return value;
    }

}
