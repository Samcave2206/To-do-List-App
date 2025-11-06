import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class TaskManager {
    private List<Task> listTasks;
    private final String FILE_PATH = "TasksList.txt";


    // ========================================
    // Constructor
    // ========================================
    public TaskManager (){
        this.listTasks = new ArrayList<>();
        loadTasks();         // Load tasks from file at start
    }

    // ========================================
    // Get all Tasks
    // ========================================
    public List<Task> getAllTasks() {
        return listTasks;
    }


    // ========================================
    // Save tasks to file
    // ========================================
    public void saveTasks(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))){
            for (Task t : listTasks){
                String safeDescription = t.getDescription().replaceAll("\n", "\\n");
                writer.write(   t.getName() + "|" + 
                                safeDescription + "|" +
                                t.getCreatedDate() + "|" +
                                t.getDateDeadline() + "|" +
                                t.getTimeDeadline() + "|" +
                                t.getPriority().name() + "|" +
                                t.getStatus());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error Saving Task: " + e.getMessage());
        }
    }


    // ========================================
    // Load tasks from file
    // ========================================
    public void loadTasks(){
        listTasks.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))){
            String line;
            while ((line = reader.readLine()) != null){
                String[] parts = line.split("\\|");
                if (parts.length == 7){
                    String name = parts[0];
                    String description = parts[1].replace("\\n", "\n");
                    LocalDateTime createdDate = LocalDateTime.parse(parts[2]);
                    LocalDate dateDeadline = LocalDate.parse(parts[3]);
                    String timeDeadline = parts[4];
                    Task.Priority priority = Task.Priority.valueOf(parts[5]);
                    String status = parts[6];

                    Task task = new Task(name, description, dateDeadline, timeDeadline, priority);
                    task.setCreatedDate(createdDate);
                    task.setStatus(status);
                    listTasks.add(task);
                }
            }
        } catch (FileNotFoundException e){
            System.out.println("No previous task file found. Starting fresh.");
        } catch (IOException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
        }
    }   


    // ========================================
    // Add a new task to file
    // ========================================
    public void addTask(Task newTask){
        listTasks.add(newTask);
        saveTasks();
    }


    // ========================================
    // Delete a task from file
    // ========================================
    public void deleteTask(int index){
        if (index >= 0 && index < listTasks.size()){
            listTasks.remove(index);
            saveTasks();
        }
    }


    // ========================================
    // Edit a task's information
    // ========================================
    public void editTask(int index, Task newTask){
        if (index >= 0 && index < listTasks.size()){
            listTasks.remove(index);
            listTasks.add(newTask);
            saveTasks();
        }
    }


    // ========================================
    // Mark task as completed
    // ========================================
    public void markAsCompleted(int index){
        if (index >= 0 && index < listTasks.size()){
            listTasks.get(index).setStatus("Done");
            saveTasks();
        }
    }


    // ========================================
    // Sort tasks by deadline
    // ========================================
    public void sortByDeadline(){
        listTasks.sort(Comparator.comparing(Task::getDeadline));

    }


    // ========================================
    // Sort tasks by priority
    // ========================================
    public void sortByPriority(){
        listTasks.sort(Comparator.comparing(Task::getPriority).reversed());     // HIGH > MEDIUM > LOW
    }

}
