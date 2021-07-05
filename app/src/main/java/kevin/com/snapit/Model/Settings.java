package kevin.com.snapit.Model;

public class Settings {
    private String name;
    private int icon;

    public Settings(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }
}
