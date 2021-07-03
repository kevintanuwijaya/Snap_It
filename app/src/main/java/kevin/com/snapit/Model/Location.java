package kevin.com.snapit.Model;

public class Location {
    String name;
    String address;
    String postalCode;
    String country;
    String[] openHours;
    String phone;
    String website;
    double latitude;
    double longitude;
    double distance;
    double rating;

    public Location(String name, String address, String postalCode, String country, String[] openHours, String phone, String website, double latitude, double longitude, double distance, double rating) {
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.country = country;
        this.openHours = openHours;
        this.phone = phone;
        this.website = website;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.rating = rating;
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

    public String[] getOpenHours() {
        return openHours;
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
}
