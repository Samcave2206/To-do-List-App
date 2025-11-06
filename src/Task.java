
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class Task{   
    public enum Priority{LOW, MEDIUM, HIGH}

    // ========================================
    // Attributes
    // ========================================
    private String name;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime deadline;     // Combined of 2 parts: date & time
    private LocalDate dateDeadline;
    private String timeDeadline;
    private Priority priority;
    private String status;

    // ========================================
    // Constructor
    // ========================================
    public Task (String name, String description, LocalDate dateDeadline, String timeDeadline, Priority priority){
        this.name = name;
        this.description = description;
        this.createdDate = LocalDateTime.now();

        this.dateDeadline = dateDeadline;
        this.timeDeadline = timeDeadline;
        LocalTime time = LocalTime.parse(timeDeadline);
        this.deadline = LocalDateTime.of(dateDeadline, time);

        this.priority = priority != null ? priority : Priority.MEDIUM;
        this.status = "Not done yet";
    }

    // ========================================
    // Getters
    // ========================================
    public String getName(){
        return name;
    }
    public String getDescription(){
        return description;
    }
    public LocalDateTime getCreatedDate(){
        return createdDate;
    }
    public LocalDateTime getDeadline(){
        return deadline;
    }
    public LocalDate getDateDeadline(){
        return dateDeadline;
    }
    public String getTimeDeadline(){
        return timeDeadline;
    }
    public Priority getPriority(){
        return priority;
    }
    public String getStatus(){
        return status;
    }

    // ========================================
    // Setters
    // ========================================
    public void setName(String name){
        this.name = name;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public void setCreatedDate(LocalDateTime createdDate){
        this.createdDate = createdDate;
    }
    public void setDeadline(LocalDate date, LocalTime time){
        this.deadline = LocalDateTime.of(date, time);
    }
    public void setDateDeadline(LocalDate dateDeadline){
        this.dateDeadline = dateDeadline;
    }
    public void setTimeDeadline(String timeDeadline){
        this.timeDeadline = timeDeadline;
    }
    public void setPriority(Priority priority){
        this.priority = priority;
    }
    public void setStatus(String status){
        this.status = status;
    }


    // ========================================
    // IsOverdue method
    // ========================================
    // public boolean IsOverdue(){
    //     if (deadline != null && deadline.isAfter(LocalDateTime.now()) && completed)
    //         return true;
    //     else return false;
    // }

}