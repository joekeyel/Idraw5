package my.com.tm.idraw;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by hasanulisyraf on 01/01/2018.
 */

public class markermodel {

    private String title;
    private LatLng coordinate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LatLng getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(LatLng coordinate) {
        this.coordinate = coordinate;
    }
}
