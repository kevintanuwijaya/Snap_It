package kevin.com.snapit.Model;

public class Articel {
    private int image;
    private String contain;
    private String title;
    private String author;

    public Articel(int image, String contain, String title, String author) {
        this.image = image;
        this.contain = contain;
        this.title = title;
        this.author = author;
    }

    public int getImage() {
        return image;
    }

    public String getContain() {
        return contain;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }
}
