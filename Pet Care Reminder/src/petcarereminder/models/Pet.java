package petcarereminder.models;

import java.util.UUID;

public class Pet {
    public String id = UUID.randomUUID().toString();
    public String name;
    public String type;
    public int age;

    public Pet(String name, String type, int age) {
        this.name = name;
        this.type = type;
        this.age = age;
    }
}
