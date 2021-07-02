package kevin.com.snapit.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Articel implements Parcelable {
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

    protected Articel(Parcel in) {
        image = in.readInt();
        contain = in.readString();
        title = in.readString();
        author = in.readString();
    }

    public static final Creator<Articel> CREATOR = new Creator<Articel>() {
        @Override
        public Articel createFromParcel(Parcel in) {
            return new Articel(in);
        }

        @Override
        public Articel[] newArray(int size) {
            return new Articel[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(image);
        dest.writeString(contain);
        dest.writeString(title);
        dest.writeString(author);
    }
}
