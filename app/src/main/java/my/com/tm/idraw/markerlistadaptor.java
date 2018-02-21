package my.com.tm.idraw;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joe on 1/25/2016.
 */
public class markerlistadaptor extends ArrayAdapter implements Filterable {

    private List<MarkerOptions> ttmodellist;
    private List<MarkerOptions> orig;
    private int resource;
    private LayoutInflater inflator;

    public markerlistadaptor(Context context, int resource, List<MarkerOptions> objects) {
        super(context, resource, objects);

        ttmodellist = objects;
        this.resource = resource;
        inflator = (LayoutInflater)getContext().getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<MarkerOptions> results = new ArrayList<MarkerOptions>();
                if (orig == null)
                    orig = ttmodellist;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final MarkerOptions g : orig) {
                            if (g.getTitle().toLowerCase()
                                    .contains(constraint.toString()))
                                results.add(g);


                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                ttmodellist = (ArrayList<MarkerOptions>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return ttmodellist.size();
    }

    @Override
    public Object getItem(int position) {
        return ttmodellist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){

            convertView = inflator.inflate(resource,null);
        }


        TextView title;
        TextView coordinates;
        final ImageView markerimage;

        title = (TextView) convertView.findViewById(R.id.tvtitlemarker);
        coordinates = (TextView) convertView.findViewById(R.id.tvcoordinates);
        markerimage = (ImageView)convertView.findViewById(R.id.imagemarker);

       title.setText(ttmodellist.get(position).getTitle());

       String latstr = String.valueOf(ttmodellist.get(position).getPosition().latitude);
       String lngstr = String.valueOf(ttmodellist.get(position).getPosition().longitude);

        coordinates.setText("Latittude:"+latstr+",Longitude:"+lngstr);


        //this part to load the image

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();


        storageRef.child("remote_camera" + File.separator +ttmodellist.get(position).getSnippet()+ File.separator + ttmodellist.get(position).getTitle() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // TODO: handle uri


                Context context = markerimage.getContext();

                markerimage.invalidate();

                Picasso.with(context).load(uri).networkPolicy(NetworkPolicy.NO_CACHE).into(markerimage);




            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


        return convertView;
    }
}
