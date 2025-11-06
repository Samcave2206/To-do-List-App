import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class MainController {

    private TaskManager taskManager;
    private ObservableList<Task> observableTasks;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML private TextField txtName;
    @FXML private TextArea txtDescription;
    @FXML private DatePicker dateDeadline;
    @FXML private TextField timeDeadline;
    @FXML private ComboBox<Task.Priority> comboPriority;

    @FXML private TableView<Task> tableTasks;
    @FXML private TableColumn<Task, String> colIndex;
    @FXML private TableColumn<Task, String> colName;
    @FXML private TableColumn<Task, String> colDescription;
    @FXML private TableColumn<Task, String> colCreatedDate;
    @FXML private TableColumn<Task, String> colDeadline;
    @FXML private TableColumn<Task, String> colPriority;
    @FXML private TableColumn<Task, String> colStatus;

    @FXML
    public void initialize() {
        taskManager = new TaskManager();

        // Wrap list for JavaFX TableView
        observableTasks = FXCollections.observableArrayList(taskManager.getAllTasks());
        tableTasks.setItems(observableTasks);

        // Setup priority combo box
        comboPriority.getItems().setAll(Task.Priority.values());

        // Configure table columns
        colIndex.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(String.valueOf(tableTasks.getItems().indexOf(cellData.getValue()) + 1)));
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colDescription.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        colCreatedDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCreatedDate().format(fmt)));
        colDeadline.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDeadline().format(fmt)));
        colPriority.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPriority().toString()));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        // Double click to a row for editing task information
        tableTasks.setRowFactory(tableView -> {
            TableRow<Task> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Task selectedTask = row.getItem();
                    handleEditTask(selectedTask);
                }
            });
            return row;
        });

    }


    // ========================================
    // Update Task Table after successfully adding/deleting/editing/markCompleted a Task
    // ========================================
    private void refreshTable() {
        // taskManager.loadTasks();
        observableTasks.setAll(taskManager.getAllTasks());
        tableTasks.refresh();
    }

    // ========================================
    // Show Errors/Warning when handling events (add/delete/edit/markCompleted)
    // ========================================
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // ========================================
    // Button Event Handlers
    // ========================================
    @FXML
    private void handleAddTask() {
        try {
            String name = txtName.getText();
            String desc = txtDescription.getText();
            LocalDate date = dateDeadline.getValue();
            String timeText = timeDeadline.getText().trim();        
            Task.Priority priority = comboPriority.getValue();

            if (name.isEmpty() || date == null || timeText == null) {
                showAlert("Empty Fields", "Please fill in all required fields (name, date, and time)");
                return;
            }

            LocalTime time;
            try {
                time = LocalTime.parse(timeText); // parses 24h format like "23:59"
            } catch (Exception e) {
                showAlert("Invalid Time Format","Please enter a valid time in HH:mm format (e.g., 23:59)");
                return;
            }

            Task newTask = new Task(name, desc, date, timeText, priority);
            newTask.setDeadline(date, time);
            taskManager.addTask(newTask);
            refreshTable();

            // Clear input fields after success adding a new Task
            txtName.clear();
            txtDescription.clear();
            dateDeadline.setValue(null);
            timeDeadline.clear();
            // comboPriority.setValue(null);

        } catch (Exception e) {
            showAlert("Error", "Invalid input: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteTask() {
        Task selected = tableTasks.getSelectionModel().getSelectedItem();
        if (selected != null) {
            int index = tableTasks.getSelectionModel().getSelectedIndex();
            taskManager.deleteTask(index);
            refreshTable();
            
        } else {
            showAlert("Error", "Please select a task to delete!");
        }
    }

    @FXML
    private void handleMarkAsCompleted() {
        Task selected = tableTasks.getSelectionModel().getSelectedItem();
        if (selected != null) {
            int index = tableTasks.getSelectionModel().getSelectedIndex();
            taskManager.markAsCompleted(index);
            refreshTable();
        } else {
            showAlert("Error", "Please select a task to mark as completed!");
        }
    }

     @FXML
    private void handleSortByDeadline() {
        taskManager.sortByDeadline();
        refreshTable();
    }

    @FXML
    private void handleSortByPriority() {
        taskManager.sortByPriority();
        refreshTable();
    }

    private void handleEditTask(Task task){
        // Create dialog
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Edit Task");
        dialog.setHeaderText("Edit task details below:");

        // Create buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create input fields
        TextField txtName = new TextField(task.getName());
        TextArea txtDescription = new TextArea(task.getDescription());
        DatePicker datePicker = new DatePicker(task.getDateDeadline());
        TextField txtTime = new TextField(task.getTimeDeadline());

        ComboBox<Task.Priority> priorityBox = new ComboBox<>();
        priorityBox.getItems().setAll(Task.Priority.values());
        priorityBox.setValue(task.getPriority());

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Done", "Not done yet");
        statusBox.setValue(task.getStatus());

        // Layout in grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(txtName, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(txtDescription, 1, 1);
        grid.add(new Label("Date Deadline:"), 0, 2);
        grid.add(datePicker, 1, 2);
        grid.add(new Label("Time Deadline:"), 0, 3);
        grid.add(txtTime, 1, 3);
        grid.add(new Label("Priority:"), 0, 4);
        grid.add(priorityBox, 1, 4);
        grid.add(new Label("Status:"), 0, 5);
        grid.add(statusBox, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Convert result to updated task when Save is clicked
        dialog.setResultConverter(dialogButton -> {
            // Validate time format
            LocalTime time;
            try {
                time = LocalTime.parse(txtTime.getText().trim());
            } catch (Exception e) {
                showAlert("Invalid Time Format","Please enter a valid time in HH:mm format (e.g., 23:59)");
                return null;
            }

            if (dialogButton == saveButtonType) {
                task.setName(txtName.getText());
                task.setDescription(txtDescription.getText());
                task.setDateDeadline(datePicker.getValue());
                task.setTimeDeadline(txtTime.getText());
                task.setDeadline(datePicker.getValue(), time);
                task.setPriority(priorityBox.getValue());
                task.setStatus(statusBox.getValue());  

                int index = tableTasks.getSelectionModel().getSelectedIndex();
                taskManager.editTask(index, task);
                return task;
            }
            return null;
        });

        Optional<Task> result = dialog.showAndWait();
        result.ifPresent(updatedTask -> {
            taskManager.loadTasks();
            refreshTable();     // refresh table view after editing
        }); 
    }

   

}