package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import javafx.application.Application;
import javafx.stage.Stage;
import org.example.service.AuthService;
import org.example.service.EmailService;
import org.example.service.OTPService;
import org.example.storage.MongoTaskStorage;
import org.example.storage.MongoUserStorage;
import io.github.cdimascio.dotenv.Dotenv;


// compile: mvn clean compile
// run: mvn javafx:run   

public class Main extends Application {
    Dotenv dotenv = Dotenv.load();

    String apiKey = dotenv.get("API_KEY");
    String appPassword = dotenv.get("APP_PASSWORD");
    String fromEmail = dotenv.get("FROM_EMAIL");
    String mongoURL = dotenv.get("MONGO_URL");

    @Override
    public void start(Stage stage) throws Exception {

        // 1) Kết nối MongoDB
        MongoClient client = MongoClients.create(mongoURL);
        MongoDatabase db = client.getDatabase("todoapp");
        // Cực kỳ cẩn thận với crash, đọc collection liên tục
        MongoUserStorage userStorage = new MongoUserStorage(db);
        // Task storage
        MongoTaskStorage taskStorage = new MongoTaskStorage(db);
        
        // 3) Email Service (Gmail + App Password)
        EmailService emailService = new EmailService(
                fromEmail,
                appPassword //không được up thẳng lên gittt
        );

        //OTP Service - EmailService)
        OTPService otpService = new OTPService(emailService);

        //Auth Service (Login, Register, Verify OTP)
        AuthService authService = new AuthService(userStorage, otpService);

        // Scene manager -> dễ crash
        SceneManager.init(stage, authService, taskStorage);

        SceneManager.switchToLogin();

        stage.setTitle("ToDo App");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
