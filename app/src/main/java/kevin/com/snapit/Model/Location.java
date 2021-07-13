package kevin.com.snapit.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.huawei.hms.maps.model.LatLng;

public class Location implements Parcelable {
    String image;
    String name;
    String address;
    String postalCode;
    String country;
    String phone;
    String website;
    double latitude;
    double longitude;
    double distance;
    double rating;

    public Location(String image, String name, String address, String postalCode, String country, String phone, String website, double latitude, double longitude, double distance, double rating) {
        this.image = image;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.country = country;
        this.phone = phone;
        this.website = website;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.rating = rating;
    }

    protected Location(Parcel in) {
        image = in.readString();
        name = in.readString();
        address = in.readString();
        postalCode = in.readString();
        country = in.readString();
        phone = in.readString();
        website = in.readString();
        latitude = in.readDouble();
        longitude = in.readByte();
        distance = in.readDouble();
        rating = in.readDouble();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }


    public String getPhone() {
        return phone;
    }

    public String getWebsite() {
        return website;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDistance() {
        return distance;
    }

    public double getRating() {
        return rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(postalCode);
        dest.writeString(country);
        dest.writeString(phone);
        dest.writeString(website);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(distance);
        dest.writeDouble(rating);
    }
}
