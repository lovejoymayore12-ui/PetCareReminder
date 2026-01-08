package petcarereminder;

import petcarereminder.models.Pet;
import petcarereminder.models.Schedule;
import petcarereminder.storage.DataStore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class PetManager {
    public static ArrayList<Pet> pets = new ArrayList<>();
    public static ArrayList<Schedule> schedules = new ArrayList<>();

    static {
        HashMap<String, Object> data = DataStore.load();
        try {
            pets = (ArrayList<Pet>) data.get("pets");
            schedules = (ArrayList<Schedule>) data.get("schedules");
        } catch (Exception e) {
            pets = new ArrayList<>();
            schedules = new ArrayList<>();
        }

        if (pets == null) pets = new ArrayList<>();
        if (schedules == null) schedules = new ArrayList<>();
    }

    public static void addPet(String name, String type, int age) {
        pets.add(new Pet(name, type, age));
        save();
    }

    public static void deletePet(String id) {
        pets.removeIf(p -> p.id.equals(id));
        schedules.removeIf(s -> s.petId.equals(id));
        save();
    }

    public static void addSchedule(String petId, String task, LocalDateTime time) {
        schedules.add(new Schedule(petId, task, time));
        save();
    }

    public static ArrayList<Schedule> getUpcoming() {
        ArrayList<Schedule> list = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Schedule s : schedules) {
            if (!s.scheduleTime.toLocalDate().isBefore(now.toLocalDate())) {
                list.add(s);
            }
        }
        return list;
    }

    private static void save() {
        DataStore.save(pets, schedules);
    }
}
