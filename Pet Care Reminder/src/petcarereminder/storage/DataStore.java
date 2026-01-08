package petcarereminder.storage;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import petcarereminder.models.Pet;
import petcarereminder.models.Schedule;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class DataStore {
    private static final String FILE = "data.json";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // ✅ Custom Gson para sa LocalDateTime
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                    (src, type, ctx) -> new JsonPrimitive(src.format(formatter)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                    (json, type, ctx) -> LocalDateTime.parse(json.getAsString(), formatter))
            .setPrettyPrinting()
            .create();

    public static HashMap<String, Object> load() {
        try {
            File file = new File(FILE);
            if (!file.exists()) save(new ArrayList<>(), new ArrayList<>());

            FileReader reader = new FileReader(FILE);
            HashMap<String, Object> rawData = gson.fromJson(reader, new TypeToken<HashMap<String, Object>>(){}.getType());
            reader.close();

            ArrayList<Pet> pets = gson.fromJson(
                    gson.toJson(rawData.get("pets")),
                    new TypeToken<ArrayList<Pet>>(){}.getType()
            );

            ArrayList<Schedule> schedules = gson.fromJson(
                    gson.toJson(rawData.get("schedules")),
                    new TypeToken<ArrayList<Schedule>>(){}.getType()
            );

            HashMap<String, Object> results = new HashMap<>();
            results.put("pets", pets);
            results.put("schedules", schedules);
            return results;

        } catch (Exception e) {
            e.printStackTrace();
            HashMap<String, Object> empty = new HashMap<>();
            empty.put("pets", new ArrayList<Pet>());
            empty.put("schedules", new ArrayList<Schedule>());
            return empty;
        }
    }

    public static void save(ArrayList<Pet> pets, ArrayList<Schedule> schedules) {
        try {
            HashMap<String, Object> data = new HashMap<>();
            data.put("pets", pets);
            data.put("schedules", schedules);

            FileWriter writer = new FileWriter(FILE);
            gson.toJson(data, writer);
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
