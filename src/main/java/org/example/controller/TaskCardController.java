package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.layout.StackPane;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import org.example.model.Task;

public class TaskCardController {

    @FXML private Label taskTitle;
    @FXML private Label dueDateLabel;
    @FXML private Label priorityFlag;
    @FXML private Circle statusCircle;
    @FXML private Label statusCheck;
    @FXML private StackPane statusContainer;

    private Task task;

    // Callbacks provided by MainController
    private Consumer<Task> onMarkCompleted;
    private Consumer<Task> onViewDetails;
    private Consumer<Task> onDelete;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("MMM dd, yyyy");

    // ------------------------------------------------------------
    // ASSIGN TASK + CALLBACKS
    // ------------------------------------------------------------
    public void setTask(Task task,
                        Consumer<Task> onMarkCompleted,
                        Consumer<Task> onViewDetails,
                        Consumer<Task> onDelete) {

        this.task = task;
        this.onMarkCompleted = onMarkCompleted;
        this.onViewDetails = onViewDetails;
        this.onDelete = onDelete;

        loadTaskIntoUI();
    }

    // ------------------------------------------------------------
    // RENDER TASK INFO INTO UI
    // ------------------------------------------------------------

    private void loadTaskIntoUI() {
        taskTitle.setText(task.getTitle());

        if (task.getDueDate() != null) {
            dueDateLabel.setText(task.getDueDate().format(formatter));
        } else {
            dueDateLabel.setText("No deadline");
        }

        // Priority Flag Icon
        switch (task.getPriority()) {
            case 3 -> priorityFlag.setText("🔴");  // High
            case 2 -> priorityFlag.setText("🟡");  // Medium
            case 1 -> priorityFlag.setText("🔵");  // Low
            default -> priorityFlag.setText("");
        }

        boolean completed = isTaskCompleted();
        statusContainer.setVisible(true);
        statusContainer.setManaged(true);
        if (completed) {
            statusCircle.setStyle("-fx-stroke: #58D68D; -fx-fill: #58D68D;");
            statusCheck.setVisible(true);
            priorityFlag.setText("");
        } else {
            statusCircle.setStyle("-fx-stroke: #A9ACB3; -fx-fill: transparent;");
            statusCheck.setVisible(false);
        }
    }

    // ------------------------------------------------------------
    // USER INTERACTION HANDLERS
    // ------------------------------------------------------------

    @FXML
    private void handleStatusClick(MouseEvent event) {
        event.consume(); // prevent bubbling to card click handler
        // Mark as completed only if not already completed
        if (!isTaskCompleted()) {
            task.setStatus("Completed");
            loadTaskIntoUI();
            onMarkCompleted.accept(task);
        }
    }

    @FXML
    private void handleCardClick(MouseEvent event) {
        if (isStatusArea(event.getTarget())) {
            return;
        }
        // Clicking anywhere on the card shows details
        onViewDetails.accept(task);
    }

    @FXML
    private void handleDeleteClick(MouseEvent event) {
        // Optional: attach this to a delete icon if you add one in FXML
        onDelete.accept(task);
    }

    private boolean isStatusArea(Object target) {
        if (!(target instanceof Node node)) {
            return false;
        }
        while (node != null) {
            if (node == statusContainer) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }

    private boolean isTaskCompleted() {
        return "Completed".equalsIgnoreCase(task.getStatus());
    }
}
