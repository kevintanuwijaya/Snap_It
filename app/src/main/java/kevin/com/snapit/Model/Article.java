package kevin.com.snapit.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Article implements Parcelable {
    private int image;
    private String contain;
    private String title;
    private String author;

    public Article(int image, String contain, String title, String author) {
        this.image = image;
        this.contain = contain;
        this.title = title;
        this.author = author;
    }

    protected Article(Parcel in) {
        image = in.readInt();
        contain = in.readString();
        title = in.readString();
        author = in.readString();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
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
