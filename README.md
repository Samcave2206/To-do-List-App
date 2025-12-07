# JavaFX To-Do List App

A modern JavaFX desktop application for managing personal tasks, featuring secure authentication, OTP email verification, AI-assisted task descriptions, and cloud-based storage using MongoDB Atlas.

This project follows a clean MVC-ish architecture with separate layers for controllers, services, storage, and models.

## Features

### Authentication & Security
- User registration & login  
- BCrypt password hashing (`PasswordUtils`)  
- OTP email verification using SMTP  
- Session management (`Session`)

### Task Management
- Create, edit, delete tasks  
- Task details view with separate UI  
- TaskManager service handles business logic  
- Flexible storage interface (`TaskStorage`)  
- MongoDB cloud persistence (`MongoTaskStorage`)

### AI-Assisted UX
- Built-in `AIService` for generating smart task descriptions  
- HTTP integration using OkHttp  
- JSON parsing via org.json

### Cloud Database
- MongoDB Atlas  
- `MongoUserStorage` & `MongoTaskStorage` handle persistence  
- Strongly typed models (`Task`, `User`)

### JavaFX UI
- FXML-based interface  
- Controllers for each screen:
  - Login / Register  
  - OTP verification  
  - Main dashboard  
  - Add task  
  - Edit task  
  - Task card  
  - Task details  
  - Profile view  

### Config & Environment
- `.env` with java-dotenv  
- SMTP email credentials  
- MongoDB URI  
- Optional AI API keys

## Tech Stack

- **Java 17**  
- **JavaFX 21** (controls + FXML)  
- **MongoDB Driver Sync 4.11**  
- **BCrypt (Favre lib)**  
- **JavaMail (SMTP)**  
- **OkHttp 4.12**  
- **org.json**  
- **Maven**  
- **dotenv (java-dotenv)**  

## Project Structure

```
src/main/java/
└─ org/example/

    Main.java                      # App entry point (JavaFX launcher)
    SceneManager.java              # Manage screens
    TaskStorage.java               # Storage interface
    TaskManager.java               # Core business logic for tasks

    model/
    ├─ User.java                   # User model
    ├─ Task.java                   # Task model

    session/
    ├─ Session.java                # Logged-in user session

    utils/
    ├─ PasswordUtils.java          # BCrypt hashing & verification

    service/
    ├─ AuthService.java            # Register, login, verify login
    ├─ OTPService.java             # OTP generation & validation
    ├─ EmailService.java           # Sends OTP via SMTP
    ├─ AIService.java              # Generates AI task suggestions

    storage/
    ├─ MongoUserStorage.java       # MongoDB: users
    └─ MongoTaskStorage.java       # MongoDB: tasks

    controller/
    ├─ LoginController.java
    ├─ RegisterController.java
    ├─ OTPController.java
    ├─ MainController.java
    ├─ AddTaskController.java
    ├─ EditTaskController.java
    ├─ TaskCardController.java
    ├─ TaskDetailsController.java
    └─ ProfileController.java

src/main/resources/
├─ views/       # All FXML UI screens
├─ css/         # Stylesheets
```

## Installation

Clone the project:
```bash
git clone
cd ToDoListApp
```

Install dependencies:
```bash
mvn clean install
```

## Environment Variables

Create a file named `.env` in the project root:

```
MONGO_URI=your_mongodb_atlas_connection_string
FROM_EMAIL=your_email@gmail.com
APP_PASSWORD=your_app_password
API_KEY=optional_ai_key
```

## Running the Application

Using JavaFX Maven plugin:
```bash
mvn javafx:run
```

## Architecture Flow

### Registration
1. User submits email + password  
2. Password hashed via BCrypt  
3. OTP generated & emailed  
4. Verified user stored in MongoDB

### Login
1. Email + password checked  
2. BCrypt verifies hash  
3. Session created

### Task Management
- addTask()  
- updateTask()  
- deleteTask()  
- getTasksByUser()  

### AI Assistance
- OkHttp sends request  
- JSON parsed  
- Suggestion returned: gemini

