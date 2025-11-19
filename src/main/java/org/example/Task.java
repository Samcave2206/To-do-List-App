package org.example;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {

    private ObjectId id;
    private String title;
    private String description;
    private int priority;
    private LocalDate dueDate;
    private LocalDateTime createdAt;

    public Task(String title, String description, int priority, LocalDate dueDate) {
        this.id = new ObjectId();
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.createdAt = LocalDateTime.now();
    }
    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getId() {
        return id;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    // Convert Task -> Document
    public Document toDocument() {
        Document doc = new Document("_id", id)
                .append("title", title)
                .append("description", description)
                .append("priority", priority)
                .append("dueDate", dueDate.toString())
                .append("createdAt", createdAt.toString());
        return doc;
    }

    // Convert Document -> Task: phải cẩn thận hai cái này
    public static Task fromDocument(Document doc) {
        Task t = new Task(
                doc.getString("title"),
                doc.getString("description"),
                doc.getInteger("priority"),
                LocalDate.parse(doc.getString("dueDate"))
        );

        t.id = doc.getObjectId("_id");
        t.createdAt = LocalDateTime.parse(doc.getString("createdAt"));

        return t;
    }



}
