package my.com.tm.idraw;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class mainholewall extends AppCompatActivity {

    LatLng objLatLng;
    String Markername;
    String createby;

    ArrayList<String> nestduct = new ArrayList();
    ArrayList<Integer> nestductid = new ArrayList();

    ArrayList<ductviewmodel> nestductmodelsarray = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainholewall);



        objLatLng=getIntent().getExtras().getParcelable("markerlatlng");
        Markername = getIntent().getStringExtra("markertitle");

       createby = getIntent().getStringExtra("markercreateby");


       loadfirebasewallduct();



    }


    public void clickduct(View view){

     String idresource = view.getResources().getResourceName(view.getId());
     String ids = idresource.replace("my.com.tm.idraw:id/","");

        ductviewmodel nestductobject = new ductviewmodel();


        TextView texttouch = (TextView)findViewById(view.getId());



        int intID = getBackgroundColor(texttouch);

             if(intID == Color.parseColor("#3f51b5")){

             texttouch.setBackgroundColor(Color.parseColor("#ffffff"));


             nestductobject.setWallduct(ids);
             nestductobject.setWallductview(view.getId());
             nestductmodelsarray.add(nestductobject);




             }
            if(intID == Color.parseColor("#ffffff")){

                texttouch.setBackgroundColor(Color.parseColor("#3f51b5"));
                Integer n = nestductmodelsarray.size();



                if(n>0) {


                    for (int j = n-1; j >= 0; j--) {
                        String wallduct = nestductmodelsarray.get(j).getWallduct();
                        if (wallduct.equals(ids)) {
                            nestductmodelsarray.remove(j);
                        }
                    }


                }

            }




    }

    public void clickductbutton(View view){

        String idresource = view.getResources().getResourceName(view.getId());
        String ids = idresource.replace("my.com.tm.idraw:id/","");
        String wall = ids.substring(6);

        Integer n = nestductmodelsarray.size();




        if(n>0) {


            for (int l = 0; l < n; l++) {

                String wallduct = nestductmodelsarray.get(l).getWallduct();
                Integer viewid = nestductmodelsarray.get(l).getWallductview();


                String walls = wallduct.substring(0, 2);

                if (walls.equals(wall)) {



                    updatefirebase(wallduct,viewid);
                }


            }


        }

        loadfirebasewallduct();

    }

    public void updatefirebase(String wallduct,Integer viewid){

        if (createby.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {

            FirebaseDatabase databasefirebase = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = databasefirebase.getReference();
            myRef.child("photomarkeridraw/" + FirebaseAuth
                    .getInstance().getCurrentUser().getUid() + "/" + Markername).child(wallduct).setValue(viewid);


        }


    }

    public static int getBackgroundColor(TextView textView) {
        ColorDrawable drawable = (ColorDrawable) textView.getBackground();
        if (Build.VERSION.SDK_INT >= 11) {
            return drawable.getColor();
        }
        try {
            Field field = drawable.getClass().getDeclaredField("mState");
            field.setAccessible(true);
            Object object = field.get(drawable);
            field = object.getClass().getDeclaredField("mUseColor");
            field.setAccessible(true);
            return field.getInt(object);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return 0;
    }


    public  void loadfirebasewallduct(){

        FirebaseDatabase databasefirebaseinitial = FirebaseDatabase.getInstance();
        final DatabaseReference myRefdatabaseinitial = databasefirebaseinitial.getReference("photomarkeridraw");

        myRefdatabaseinitial.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot child: dataSnapshot.getChildren()) {


                    for (DataSnapshot child2 : child.getChildren()) {

                      if(child2.getKey().toString().equals(Markername)){





                        for (DataSnapshot child3 : child2.getChildren()) {

                            String key = child3.getKey().toString();
                            String viewid =  child3.getValue().toString();

                            if(!key.equals("createdby") && !key.equals("lat") && !key.equals("lng")){

                                TextView occupyduct = (TextView)findViewById(Integer.parseInt(viewid));
                                occupyduct.setBackgroundColor(Color.GREEN);
                            }

                            }


                        }




                    }
                }

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FIREBASELOAD", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

    }

}
