package org.example.storage;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.TaskStorage;
import org.example.model.Task;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoTaskStorage implements TaskStorage {
    private final MongoCollection<Document> collection;

    public MongoTaskStorage(MongoDatabase db) {
        this.collection = db.getCollection("tasks");
    }

    @Override
    public List<Task> loadTasks(String userEmail) {
        List<Task> tasks = new ArrayList<>();
        Iterable<Document> docs;
        if (userEmail == null || userEmail.isBlank()) {
            docs = collection.find();
        } else {
            docs = collection.find(eq("userEmail", userEmail.toLowerCase()));
        }
        for (Document doc : docs) {
            tasks.add(Task.fromDocument(doc));
        }
        return tasks;
    }

    @Override
    public void addTask(Task task) {
        collection.insertOne(task.toDocument());
    }

    @Override
    public void updateTask(Task task) {
        if (task.getId() == null || !ObjectId.isValid(task.getId())) {
            throw new IllegalArgumentException("Task id is invalid or missing");
        }
        collection.replaceOne(eq("_id", new ObjectId(task.getId())), task.toDocument());
    }

    @Override
    public void deleteTask(String id) {
        collection.deleteOne(eq("_id", new ObjectId(id)));
    }
}
