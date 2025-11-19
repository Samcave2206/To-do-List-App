package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.TaskStorage;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoTaskStorage implements TaskStorage {
    private final MongoCollection<Document> collection;

    public MongoTaskStorage(MongoDatabase db) {
        this.collection = db.getCollection("tasks");
    }

    @Override
    public List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();
        for (Document doc : collection.find()) {
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
        collection.replaceOne(eq("_id", task.getId()), task.toDocument());
    }

    @Override
    public void deleteTask(String id) {
        collection.deleteOne(eq("_id", new ObjectId(id)));
    }
}
