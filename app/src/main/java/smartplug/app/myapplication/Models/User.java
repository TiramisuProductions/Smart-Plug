package smartplug.app.myapplication.Models;

/**
 * Created by sarveshpalav on 28/01/18.
 */

public class User {
    String email;
    String name;
    boolean hasDevices;

    public User() {
    }

    public User(String email, String name, boolean hasDevices) {
        this.email = email;
        this.name = name;
        this.hasDevices = hasDevices;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public boolean isHasDevices() {
        return hasDevices;
    }
}
