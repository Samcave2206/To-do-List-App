package org.example.storage;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.time.LocalDateTime;

public class MongoUserStorage {

    private final MongoCollection<Document> users;

    public MongoUserStorage(MongoDatabase db) {
        this.users = db.getCollection("users");
    }

    public boolean createUser(String name, String email, String passwordHash) {
        Document existing = users.find(Filters.eq("email", email)).first();
        if (existing != null) return false;

        Document doc = new Document()
                .append("name", name)
                .append("email", email)
                .append("passwordHash", passwordHash)
                .append("emailVerified", false)
                .append("otp", null)
                .append("otpExpire", null);

        users.insertOne(doc);
        return true;
    }

    public Document getUser(String email) {
        return users.find(Filters.eq("email", email)).first();
    }

    public void saveOTP(String email, String otp, LocalDateTime expireAt) {
        users.updateOne(
                Filters.eq("email", email),
                new Document("$set",
                        new Document("otp", otp)
                                .append("otpExpire", expireAt.toString())
                )
        );
    }

    public String getOTP(String email) {
        Document user = getUser(email);
        if (user == null) return null;

        Object raw = user.get("otp");
        return raw == null ? null : raw.toString();
    }

    public LocalDateTime getOTPExpire(String email) {
        Document user = getUser(email);
        if (user == null) return null;

        String expireStr = user.getString("otpExpire");
        if (expireStr == null) return null;

        return LocalDateTime.parse(expireStr);
    }

    public void clearOTP(String email) {
        users.updateOne(
                Filters.eq("email", email),
                new Document("$set",
                        new Document("otp", null)
                                .append("otpExpire", null)
                )
        );
    }
//Phải kiểm tra lại trong collection xem có đúng là true+ cẩn thận crash
    public void setEmailVerified(String email) {
        users.updateOne(
                Filters.eq("email", email),
                new Document("$set",
                        new Document("emailVerified", true)
                )
        );
    }


    public boolean checkPassword(String email, String passwordHash) {
        Document user = getUser(email);
        if (user == null) return false;

        return passwordHash.equals(user.getString("passwordHash"));
    }

    public boolean isEmailVerified(String email) {
        Document user = getUser(email);
        if (user == null) return false;

        return user.getBoolean("emailVerified", false);
    }
}
