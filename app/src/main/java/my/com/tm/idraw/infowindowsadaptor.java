package my.com.tm.idraw;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;


class infowindowsadaptor implements GoogleMap.InfoWindowAdapter {
    private  View mymarkerview;
    LayoutInflater layoutinflator;
    Context ctx;
     Uri uriimage;


    infowindowsadaptor() {

        if(MyActivity.getInstance()!= null) {
            mymarkerview = MyActivity.getInstance().getLayoutInflater().inflate(R.layout.infowindowlayout, null);
        }
    }

    public View getInfoWindow(Marker marker) {
        render(marker, mymarkerview);
        return mymarkerview;
    }

    public View getInfoContents(Marker marker) {
        return null;
    }

    private void render(final Marker marker, final View view) {

        String imagename = marker.getTitle();
        String createdby = marker.getSnippet();

        if (imagename.indexOf("Cabinet_")>-1 || imagename.indexOf("Main Hole_")>-1 || imagename.indexOf("DP_")>-1) {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
          //  final StorageReference img2 = storageRef.child("remote_camera/Cabinet/" +createdby+"/"+ imagename + ".jpg");





            storageRef.child("remote_camera" + File.separator + createdby+ File.separator + imagename + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // TODO: handle uri
                    ImageView img = (ImageView) view.findViewById(R.id.imageView1);

                    Context context = img.getContext();

                    img.invalidate();

                    if (MyActivity.getInstance().not_first_time_showing_info_window && MyActivity.getInstance() != null) {

                        Picasso.with(context).load(uri).networkPolicy(NetworkPolicy.NO_CACHE).into(img);


                    } else {

                        Picasso.with(context).load(uri).networkPolicy(NetworkPolicy.NO_CACHE).into(img, new InfoWindowRefresher(marker));
                        MyActivity.getInstance().not_first_time_showing_info_window = true;

                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });


            LatLng latlng = marker.getPosition();

            TextView textview1 = (TextView) view.findViewById(R.id.textView1);
            textview1.setText("Latitude " + (Double) latlng.latitude);

            TextView textview2 = (TextView) view.findViewById(R.id.textView2);
            textview2.setText("Longitude " + (Double) latlng.longitude);

            TextView textview3 = (TextView) view.findViewById(R.id.titlemarker);
            textview3.setText(imagename);

        }

        else{

            LatLng latlng = marker.getPosition();

            TextView textview1 = (TextView) view.findViewById(R.id.textView1);
            textview1.setText("Latitude " + (Double) latlng.latitude);

            TextView textview2 = (TextView) view.findViewById(R.id.textView2);
            textview2.setText("Longitude " + (Double) latlng.longitude);

            TextView textview3 = (TextView) view.findViewById(R.id.titlemarker);
            textview3.setText(imagename);

        }


    }

}

