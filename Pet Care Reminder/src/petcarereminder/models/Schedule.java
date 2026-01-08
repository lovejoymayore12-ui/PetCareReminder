package petcarereminder.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Schedule {
    public String id = UUID.randomUUID().toString();
    public String petId;
    public String task;
    public LocalDateTime scheduleTime;

    public boolean done = false;

    public Schedule(String petId, String task, LocalDateTime time) {
        this.petId = petId;
        this.task = task;
        this.scheduleTime = time;
    }
}
