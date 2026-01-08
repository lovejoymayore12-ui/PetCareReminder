package petcarereminder;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import petcarereminder.models.Pet;
import petcarereminder.models.Schedule;

import java.time.LocalDateTime;
import java.util.List;

public class Dashboard {
    private VBox layout;
    private Stage stage;

    public Dashboard(Stage stage) {
        this.stage = stage;
        layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F4F4F9;");

        rebuildUI();
        startReminderWatcher();
    }

    private void rebuildUI() {
        layout.getChildren().clear();

        // Header Section
        Label title = new Label("🐾 Pet Care Dashboard");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setStyle("-fx-text-fill: white;");

        HBox header = new HBox(title);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: linear-gradient(to right, #6A11CB, #2575FC);");

        // Buttons Section
        Button addPetBtn = new Button("Add Pet");
        addPetBtn.setStyle(buttonStyle());
        addPetBtn.setOnAction(e -> showAddPet());

        Button exitBtn = new Button("Exit App");
        exitBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold;");
        exitBtn.setOnAction(e -> {
            stage.close();
            System.exit(0);
        });

        HBox buttonBar = new HBox(15, addPetBtn, exitBtn);
        buttonBar.setAlignment(Pos.CENTER);

        // Main Content
        VBox petSection = petList();
        VBox upcomingSection = upcoming();

        // Adding all sections to the layout
        layout.getChildren().addAll(
                header,
                buttonBar,
                petSection,
                upcomingSection
        );
    }

    private VBox petList() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-border-color: #D1D1D1; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        box.getChildren().add(new Label("Your Pets:"));

        for (Pet p : PetManager.pets) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Label info = new Label(p.name + " (" + p.type + "), " + p.age + " y/o");
            info.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

            Button del = new Button("Delete");
            del.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");
            del.setOnAction(e -> {
                PetManager.deletePet(p.id);
                refresh();
            });

            Button set = new Button("Set Reminder");
            set.setStyle(buttonStyle());
            set.setOnAction(e -> setReminder(p.id));

            row.getChildren().addAll(info, set, del);
            box.getChildren().add(row);
        }
        return box;
    }

    private VBox upcoming() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-border-color: #D1D1D1; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        box.getChildren().add(new Label("Upcoming Tasks:"));

        boolean hasTask = false;

        List<Schedule> list = PetManager.getUpcoming();
        for (Schedule s : list) {
            if (!s.done) {
                hasTask = true;
                Label taskLabel = new Label("⏰ " + s.task + " - " +
                        s.scheduleTime.toLocalDate() + " @ " +
                        s.scheduleTime.toLocalTime());
                taskLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                box.getChildren().add(taskLabel);
            }
        }

        if (!hasTask) {
            Label noTaskLabel = new Label("No upcoming tasks yet.");
            noTaskLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
            box.getChildren().add(noTaskLabel);
        }

        return box;
    }

    private void showAddPet() {
        // Fields
        TextField name = new TextField();
        name.setPromptText("Pet Name");

        TextField type = new TextField();
        type.setPromptText("Type (Dog/Cat/etc)");

        TextField age = new TextField();
        age.setPromptText("Age");

        // Buttons
        Button save = new Button("Save");
        save.setStyle(buttonStyle());
        save.setDefaultButton(true);

        Button cancel = new Button("Cancel");
        cancel.setStyle("-fx-background-color: #95A5A6; -fx-text-fill: white;");

        // Form layout
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(12));

        Label nameLabel = new Label("Name:");
        Label typeLabel = new Label("Type:");
        Label ageLabel = new Label("Age:");

        form.add(nameLabel, 0, 0);
        form.add(name, 1, 0);
        form.add(typeLabel, 0, 1);
        form.add(type, 1, 1);
        form.add(ageLabel, 0, 2);
        form.add(age, 1, 2);

        HBox btnBar = new HBox(10, save, cancel);
        btnBar.setAlignment(Pos.CENTER_RIGHT);
        btnBar.setPadding(new Insets(8, 12, 12, 12));

        // Header design
        Label header = new Label("Add Pet");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        header.setStyle("-fx-text-fill: white;");
        HBox headerBox = new HBox(header);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(12));
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #6A11CB, #2575FC); -fx-background-radius:6;");

        VBox root = new VBox(headerBox, form, btnBar);
        root.setSpacing(6);
        root.setStyle("-fx-background-color: white; -fx-border-radius:8; -fx-background-radius:8;");

        // Create a dedicated Stage so the OS window X always works reliably
        Stage dialog = new Stage();
        dialog.initOwner(stage);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Add Pet");
        dialog.setResizable(false);

        Scene scene = new Scene(root);
        dialog.setScene(scene);

        // Cancel just closes the dialog
        cancel.setOnAction(ev -> dialog.close());

        // Save logic
        save.setOnAction((ActionEvent ev) -> {
            String nm = name.getText().trim();
            String tp = type.getText().trim();
            String ageText = age.getText().trim();

            if (nm.isEmpty() || tp.isEmpty() || ageText.isEmpty()) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Fill all fields!", ButtonType.OK);
                a.initOwner(dialog);
                a.showAndWait();
                return;
            }

            try {
                int petAge = Integer.parseInt(ageText);
                PetManager.addPet(nm, tp, petAge);

                Alert a = new Alert(Alert.AlertType.INFORMATION, "Pet added successfully!", ButtonType.OK);
                a.initOwner(dialog);
                a.showAndWait();

                dialog.close();
                refresh();
            } catch (NumberFormatException ex) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Invalid age!", ButtonType.OK);
                a.initOwner(dialog);
                a.showAndWait();
            }
        });

        // Ensure the window X (close) works — no event filters consuming close events
        dialog.setOnCloseRequest(e -> {
            // allow default close behavior
        });

        dialog.showAndWait();
    }

    private void setReminder(String petId) {
        // Fields
        TextField task = new TextField();
        task.setPromptText("Task (Feed, Bath, etc)");

        DatePicker date = new DatePicker();

        // Buttons
        Button save = new Button("Save Reminder");
        save.setStyle(buttonStyle());
        save.setDefaultButton(true);

        Button cancel = new Button("Cancel");
        cancel.setStyle("-fx-background-color: #95A5A6; -fx-text-fill: white;");

        // Form layout
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(12));

        Label taskLabel = new Label("Task:");
        Label dateLabel = new Label("Date:");

        form.add(taskLabel, 0, 0);
        form.add(task, 1, 0);
        form.add(dateLabel, 0, 1);
        form.add(date, 1, 1);

        HBox btnBar = new HBox(10, save, cancel);
        btnBar.setAlignment(Pos.CENTER_RIGHT);
        btnBar.setPadding(new Insets(8, 12, 12, 12));

        // Header design
        Label header = new Label("Set Reminder");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        header.setStyle("-fx-text-fill: white;");
        HBox headerBox = new HBox(header);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(12));
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #2575FC, #6A11CB); -fx-background-radius:6;");

        VBox root = new VBox(headerBox, form, btnBar);
        root.setSpacing(6);
        root.setStyle("-fx-background-color: white; -fx-border-radius:8; -fx-background-radius:8;");

        Stage dialog = new Stage();
        dialog.initOwner(stage);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Set Reminder");
        dialog.setResizable(false);

        Scene scene = new Scene(root);
        dialog.setScene(scene);

        cancel.setOnAction(ev -> dialog.close());

        save.setOnAction((ActionEvent ev) -> {
            String t = task.getText().trim();
            if (t.isEmpty() || date.getValue() == null) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Fill all fields!", ButtonType.OK);
                a.initOwner(dialog);
                a.showAndWait();
                return;
            }

            LocalDateTime remindTime = date.getValue().atTime(8, 0);
            PetManager.addSchedule(petId, t, remindTime);

            Alert a = new Alert(Alert.AlertType.INFORMATION, "Reminder added!", ButtonType.OK);
            a.initOwner(dialog);
            a.showAndWait();

            dialog.close();
            refresh();
        });

        dialog.showAndWait();
    }

    private void startReminderWatcher() {
        Thread watcher = new Thread(() -> {
            while (true) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    for (Schedule s : PetManager.schedules) {
                        if (!s.done && (now.isAfter(s.scheduleTime) || now.isEqual(s.scheduleTime))) {
                            s.done = true;
                            Platform.runLater(() -> {
                                Alert a = new Alert(Alert.AlertType.INFORMATION);
                                a.setTitle("Reminder");
                                a.setHeaderText(null);
                                a.setContentText("Reminder for pet: " + s.task);
                                a.show();
                                refresh();
                            });
                        }
                    }
                    Thread.sleep(60_000);
                } catch (InterruptedException ignored) {
                    // restore interrupt status and exit
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception ex) {
                    // log and continue
                    ex.printStackTrace();
                    try { Thread.sleep(60_000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
                }
            }
        });
        watcher.setDaemon(true);
        watcher.start();
    }

    private void refresh() {
        Platform.runLater(this::rebuildUI);
    }

    private String buttonStyle() {
        return "-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 12; -fx-background-radius: 6;";
    }

    public VBox getView() {
        return layout;
    }
}