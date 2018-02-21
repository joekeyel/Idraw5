package my.com.tm.idraw;

import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;

/**
 * Created by hasanulisyraf on 20/09/2017.
 */

class InfoWindowRefresher implements Callback {
    private Marker markerToRefresh;

    InfoWindowRefresher(Marker markerToRefresh) {
        this.markerToRefresh = markerToRefresh;
    }

    @Override
    public void onSuccess() {
        markerToRefresh.showInfoWindow();
        markerToRefresh.showInfoWindow();
    }

    @Override
    public void onError() {}
}