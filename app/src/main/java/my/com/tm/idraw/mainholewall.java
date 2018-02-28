package my.com.tm.idraw;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
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

public class mainholewall  extends AppCompatActivity implements DialogInterface.OnDismissListener {

    LatLng objLatLng;
    String Markername;
    String createby;
    ductviewmodel nesductid = new ductviewmodel();



    ArrayList<ductviewmodel> nestductmodelsarray = new ArrayList();
    ArrayList<ductviewmodel> nestductmodelsarraydel = new ArrayList();

    AlertDialog alert;
    AlertDialog alertoccupancy;

    String occupancystr = "AVAILABLE";
    Spinner occupancyspinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainholewall);



        objLatLng=getIntent().getExtras().getParcelable("markerlatlng");
        Markername = getIntent().getStringExtra("markertitle");

       createby = getIntent().getStringExtra("markercreateby");


       TextView mainholeid = (TextView)findViewById(R.id.manholename);
       mainholeid.setText(Markername);

       loadfirebasewallduct();



    }


    public void clickduct(View view){

     String idresource = view.getResources().getResourceName(view.getId());
     String ids = idresource.replace("my.com.tm.idraw:id/","");


        ductviewmodel nestductobject = new ductviewmodel();


        TextView texttouch = (TextView)findViewById(view.getId());



        int intID = getBackgroundColor(texttouch);

        //if vacant

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

//if taken then user touch it...this plan to delete

        if(intID == Color.GREEN || intID == Color.YELLOW || intID == Color.BLACK || intID ==Color.RED){

            texttouch.setPaintFlags(texttouch.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);



           nesductid.setWallduct(ids);
           nesductid.setWallductview(view.getId());
           nesductid.setNesductid(texttouch.getTag().toString());

           show_alert_occupancy();



            nestductobject.setWallduct(ids);
            nestductobject.setWallductview(view.getId());
            nestductobject.setNesductid(texttouch.getTag().toString());

            nestductmodelsarraydel.add(nestductobject);

        }


    }

    public void clickductbutton(View view){
        Integer n = nestductmodelsarray.size();

        if(n>0) {
            show_dialog_nesduct_name(view);

        }


    }


    public void clickductresetbutton(View view){

        loadfirebasewallduct();
    }

    public void clickductdeletebutton(View view){

        String idresource = view.getResources().getResourceName(view.getId());
        String ids = idresource.replace("my.com.tm.idraw:id/","");
        String wall = ids.substring(6);


        Integer d = nestductmodelsarraydel.size();

        //for delete selected database

        if(d>0) {


            for (int l = 0; l < d; l++) {

                String wallduct = nestductmodelsarraydel.get(l).getWallduct();
                Integer viewid = nestductmodelsarraydel.get(l).getWallductview();
                String nesductid = nestductmodelsarraydel.get(l).getNesductid();


                String walls = wallduct.substring(0, 2);

                if (walls.equals(wall)) {



                    deletefirebase(wallduct,viewid,nesductid);
                }


            }


        }


    }

    public void preparemarkduct(View view,String nestid){

        String idresource = view.getResources().getResourceName(view.getId());
        String ids = idresource.replace("my.com.tm.idraw:id/","");
        String wall = ids.substring(6);

        Integer n = nestductmodelsarray.size();
        Integer d = nestductmodelsarraydel.size();




        if(n>0) {


            for (int l = 0; l < n; l++) {

                String wallduct = nestductmodelsarray.get(l).getWallduct();
                Integer viewid = nestductmodelsarray.get(l).getWallductview();


                String walls = wallduct.substring(0, 2);

                if (walls.equals(wall)) {



                    updatefirebase(wallduct,viewid,nestid);
                }


            }


        }



    }


    public void show_alert_occupancy(){

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertoccupancy = alertDialog.create();

        alertoccupancy.setTitle("Select Occupancy");

        final String ductname = nesductid.getWallduct();
        final Integer viewid = nesductid.getWallductview();
        final String nesductidstr = nesductid.getNesductid();


        LayoutInflater inflater = getLayoutInflater();

        // inflate the custom popup layout
        final View convertView = inflater.inflate(R.layout.alert_occupancy, null);

        final TextView wallductname = (TextView)convertView.findViewById(R.id.ductnameid);
        wallductname.setText(ductname);

        //this to set the slider bar inserting utilization

         SeekBar utilizationseekbar = (SeekBar)convertView.findViewById(R.id.utilizationsb);
         final TextView utizaltiontv = (TextView)convertView.findViewById(R.id.utilizationet);

         utilizationseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           int progressChangedValue = 0;

           public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               progressChangedValue = progress;

               utizaltiontv.setText(String.valueOf(progressChangedValue));
           }

           public void onStartTrackingTouch(SeekBar seekBar) {
               // TODO Auto-generated method stub
           }

           public void onStopTrackingTouch(SeekBar seekBar) {



               utizaltiontv.setText(String.valueOf(progressChangedValue));
               updateutilizationfirebase(String.valueOf(progressChangedValue),ductname,nesductidstr);

           }
       });


        occupancyspinner = (Spinner) convertView.findViewById(R.id.spinneroccupancy);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.occupancyvalues, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        occupancyspinner.setAdapter(adapter);

        occupancyspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                occupancystr = parent.getItemAtPosition(pos).toString();


            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        final Button updateoccupancy = (Button)convertView.findViewById(R.id.updateoccupancy);
        updateoccupancy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                EditText ductcode = (EditText)convertView.findViewById(R.id.cablecodeduct);
                final String ductcodestr = ductcode.getText().toString();


                   updateoccupancyfirebase(occupancystr,ductname,nesductidstr);


                    if(!ductcodestr.isEmpty()){

                        updatecablecodefirebase(ductcodestr,ductname);
                    }

                loadfirebasewallduct();
                alertoccupancy.dismiss();

            }
        });



        Button deleteduct = (Button)convertView.findViewById(R.id.deleteduct);
        deleteduct.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click


                deletefirebase(ductname,viewid,nesductidstr);



                loadfirebasewallduct();
                alertoccupancy.dismiss();

            }
        });

        alertoccupancy.setView(convertView);

        alertoccupancy.show();

    }

    public void show_dialog_nesduct_name(final View view) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alert = alertDialog.create();

        alert.setTitle("Insert NestDuct Name");


        LayoutInflater inflater = getLayoutInflater();

        // inflate the custom popup layout
        final View convertView = inflater.inflate(R.layout.insert_nestduct_name, null);
        // find the ListView in the popup layout



        Button updatenesductaction = (Button)convertView.findViewById(R.id.updatenestduct);



            updatenesductaction.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    EditText nestductetname = (EditText)convertView.findViewById(R.id.nestductnameet);
                    final String nestductnamestr = nestductetname.getText().toString();

                    if(nestductnamestr.isEmpty()){


                        Toast.makeText(mainholewall.this, "Pls Insert NestDuct Name", Toast.LENGTH_SHORT).show();
                        alert.dismiss();

                    }else {
                        preparemarkduct(view,nestductnamestr);
                        loadfirebasewallduct();

                        alert.dismiss();
                    }
                }
            });




        alert.setView(convertView);

        alert.show();

    }

    public void updatefirebase(String wallduct,Integer viewid,String nestid){

        if (createby.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {

            FirebaseDatabase databasefirebase = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = databasefirebase.getReference();
            myRef.child("photomarkeridraw/" + FirebaseAuth
                    .getInstance().getCurrentUser().getUid() + "/" + Markername).child(wallduct).child("textviewid").setValue(viewid);

            myRef.child("photomarkeridraw/" + FirebaseAuth
                    .getInstance().getCurrentUser().getUid() + "/" + Markername).child(wallduct).child("nestductid").setValue(nestid);


            myRef.child("photomarkeridraw/" + FirebaseAuth
                    .getInstance().getCurrentUser().getUid() + "/" + Markername).child(wallduct).child("occupancy").setValue("AVAILABLE");
            //nestduct record

            myRef.child("Nesductid"+ "/"+Markername+"/" + nestid).child(wallduct).setValue("AVAILABLE");
            myRef.child("Nesductidutilization"+ "/"+Markername+"/" + nestid).child(wallduct).child("occupancy").setValue("AVAILABLE");
            myRef.child("Nesductidutilization"+ "/"+Markername+"/" + nestid).child(wallduct).child("utilization").setValue("0");

        }


    }

    public void updateoccupancyfirebase(String occupancy,String wallduct,String nestid){

        if (createby.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {

            FirebaseDatabase databasefirebase = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = databasefirebase.getReference();
            myRef.child("photomarkeridraw/" + FirebaseAuth
                    .getInstance().getCurrentUser().getUid() + "/" + Markername).child(wallduct).child("occupancy").setValue(occupancy);


            myRef.child("Nesductid"+ "/"+Markername+"/" + nestid).child(wallduct).setValue(occupancy);
            myRef.child("Nesductidutilization"+ "/"+Markername+"/" + nestid).child(wallduct).child("occupancy").setValue(occupancy);
           // myRef.child("mainholeutilization"+ "/"+Markername+"/" + nestid).child(wallduct).setValue(occupancy);


        }


    }


    public void updateutilizationfirebase(String utilization,String wallduct,String nestid){

        if (createby.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {

            FirebaseDatabase databasefirebase = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = databasefirebase.getReference();
            myRef.child("photomarkeridraw/" + FirebaseAuth
                    .getInstance().getCurrentUser().getUid() + "/" + Markername).child(wallduct).child("utilization").setValue(utilization);


            //myRef.child("Nesductid"+ "/"+Markername+"/" + nestid).child(wallduct).setValue(utilization);
            myRef.child("Nesductidutilization"+ "/"+Markername+"/" + nestid).child(wallduct).child("utilization").setValue(utilization);
            // myRef.child("mainholeutilization"+ "/"+Markername+"/" + nestid).child(wallduct).setValue(occupancy);


        }


    }


    public void updatecablecodefirebase(String cablecode,String wallduct){

        if (createby.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {

            FirebaseDatabase databasefirebase = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = databasefirebase.getReference();
            myRef.child("photomarkeridraw/" + FirebaseAuth
                    .getInstance().getCurrentUser().getUid() + "/" + Markername).child(wallduct).child("cablecode").setValue(cablecode);



        }


    }


    public void deletefirebase(String wallduct,Integer viewid,String nesductid){

        if (createby.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {

            FirebaseDatabase databasefirebase = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = databasefirebase.getReference();
            myRef.child("photomarkeridraw/" + FirebaseAuth
                    .getInstance().getCurrentUser().getUid() + "/" + Markername).child(wallduct).removeValue();

            TextView strikethru =  (TextView)findViewById(viewid);

            strikethru.setBackgroundColor(Color.parseColor("#3f51b5"));
            strikethru.setPaintFlags(strikethru.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));


            myRef.child("Nesductid"+ "/"+Markername+"/" + nesductid).child(wallduct).removeValue();
            myRef.child("Nesductidutilization"+ "/"+Markername+"/" + nesductid).child(wallduct).removeValue();

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


                for (DataSnapshot child: dataSnapshot.getChildren()) {//useridcreated



                    for (DataSnapshot child2 : child.getChildren()) {//mainholename

                      if(child2.getKey().toString().equals(Markername)){



                        for (DataSnapshot child3 : child2.getChildren()) {//wallductname

                            String key = child3.getKey().toString();


                            if(!key.equals("createdby") && !key.equals("lat") && !key.equals("lng")){

                                String viewid = null;


                                for (DataSnapshot child4 : child3.getChildren()) {//wallduct itemname
                                    //loop to get teamid

                                    if(child4.getKey().toString().equals("textviewid")  ) {

                                        viewid = child4.getValue().toString();



                                    }
                                }


                                //to change color base on occupancy loop again


                                for (DataSnapshot child4 : child3.getChildren()) {//loop again to change color

                                    TextView occupyduct = (TextView) findViewById(Integer.parseInt(viewid));

                                    occupyduct.setPaintFlags(occupyduct.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));

                                    if(child4.getValue().toString().equals("AVAILABLE")  ) {

                                        TextView occupyduct2 = (TextView) findViewById(Integer.parseInt(viewid));
                                        occupyduct2.setBackgroundColor(Color.GREEN);


                                    }

                                    if(child4.getValue().toString().equals("PARTIALLY UTILIZED")  ) {

                                        TextView occupyduct2 = (TextView) findViewById(Integer.parseInt(viewid));
                                        occupyduct2.setBackgroundColor(Color.YELLOW);


                                    }


                                    if(child4.getValue().toString().equals("FULLY UTILIZED")  ) {

                                        TextView occupyduct2 = (TextView) findViewById(Integer.parseInt(viewid));
                                        occupyduct2.setBackgroundColor(Color.RED);


                                    }

                                    if(child4.getValue().toString().equals("ABANDONED")  ) {

                                        TextView occupyduct2 = (TextView) findViewById(Integer.parseInt(viewid));
                                        occupyduct2.setBackgroundColor(Color.BLACK);


                                    }


                                    //tag the textview

                                    if(child4.getKey().toString().equals("nestductid")  ) {

                                        String nesductid = child4.getValue().toString();

                                        TextView occupyduct3 = (TextView) findViewById(Integer.parseInt(viewid));
                                        occupyduct3.setTag(nesductid);


                                    }
                                }


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


      // loadsummaryfirebase("Nesductid2");
        loadsummaryfirebase("Nesductidutilization");

       resetscreen();

    }



    public void loadsummaryfirebase(String column) {

        FirebaseDatabase databasefirebasesummary = FirebaseDatabase.getInstance();
        final DatabaseReference rootref = databasefirebasesummary.getReference(column).child(Markername);


        rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                TextView showdata = (TextView) findViewById(R.id.showdata);
                TextView showdata2 = (TextView) findViewById(R.id.showdata2);
                TextView showdata3 = (TextView) findViewById(R.id.showdata3);
                TextView showdata4 = (TextView) findViewById(R.id.showdata4);

                showdata.setText("");
                showdata2.setText("");
                showdata3.setText("");
                showdata4.setText("");

                int countductw1 = 0;
                int countutilizationw1 = 0;

                int countductw2 = 0;
                int countutilizationw2 = 0;

                int countductw3 = 0;
                int countutilizationw3 = 0;

                int countductw4 = 0;
                int countutilizationw4 = 0;

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    String nesduct = child.getKey().toString();





                    for (DataSnapshot child2 : child.getChildren()) {



                        if (child2.getKey().toString().contains("W1")) {

                            countductw1++;
                            countutilizationw1 = countutilizationw1+Integer.valueOf(child2.child("utilization").getValue().toString());


                            if (!showdata.getText().toString().contains(nesduct)) {


                                showdata.setText(showdata.getText() + "NestDuct:" + nesduct + System.getProperty("line.separator"));


                            }
                            showdata.setText(showdata.getText() + child2.getKey().toString() + " " + child2.child("occupancy").getValue() +" ");
                            showdata.setText(showdata.getText()  + " " + child2.child("utilization").getValue() + System.getProperty("line.separator"));


                        }

                        if (child2.getKey().toString().contains("W2")) {
                            countductw2++;
                            countutilizationw2 = countutilizationw2+Integer.valueOf(child2.child("utilization").getValue().toString());


                            if (!showdata2.getText().toString().contains(nesduct)) {
                                showdata2.setText(showdata2.getText() + "NestDuct:" + nesduct + System.getProperty("line.separator"));
                            }
                            showdata2.setText(showdata2.getText() + child2.getKey().toString() + " " + child2.child("occupancy").getValue() +" ");
                            showdata2.setText(showdata2.getText() +  " " + child2.child("utilization").getValue() + System.getProperty("line.separator"));

                        }
                        if (child2.getKey().toString().contains("W3")) {
                            countductw3++;
                            countutilizationw3 = countutilizationw3+Integer.valueOf(child2.child("utilization").getValue().toString());


                            if (!showdata3.getText().toString().contains(nesduct)) {
                                showdata3.setText(showdata3.getText() + "NestDuct:" + nesduct + System.getProperty("line.separator"));
                            }
                            showdata3.setText(showdata3.getText() + child2.getKey().toString() + " " + child2.child("occupancy").getValue() +" ");
                            showdata3.setText(showdata3.getText() +  " " + child2.child("utilization").getValue() + System.getProperty("line.separator"));
                        }



                        if (child2.getKey().toString().contains("W4")) {
                            countductw4++;
                            countutilizationw4 = countutilizationw4+Integer.valueOf(child2.child("utilization").getValue().toString());

                            if (!showdata4.getText().toString().contains(nesduct)) {
                                showdata4.setText(showdata4.getText() + "NestDuct:" + nesduct + System.getProperty("line.separator"));
                            }
                            showdata4.setText(showdata4.getText() + child2.getKey().toString() + " " + child2.child("occupancy").getValue() +" ");
                            showdata4.setText(showdata4.getText() + " " + child2.child("utilization").getValue() + System.getProperty("line.separator"));
                          }



                    }




                }

                int mainholeutilization = 0;
                int mainholecount = 0;

                if(countductw1>0){

                    Integer avg = countutilizationw1/countductw1;
                    showdata.setText(showdata.getText().toString() + avg +" Utilization"+System.getProperty("line.separator"));
                   mainholeutilization = mainholeutilization+avg;
                   mainholecount++;


                    TextView wall1 = (TextView)findViewById(R.id.wall1utilization);
                    wall1.setText(String.valueOf(avg));
                }

                if(countductw2>0){

                    Integer avg = countutilizationw2/countductw2;
                    showdata2.setText(showdata2.getText().toString() + avg +" Utilization"+System.getProperty("line.separator"));

                    mainholeutilization = mainholeutilization+avg;
                    mainholecount++;

                    TextView wall2 = (TextView)findViewById(R.id.wall2utilization);
                    wall2.setText(String.valueOf(avg));
                }
                if(countductw3>0){

                    Integer avg = countutilizationw3/countductw3;
                    showdata3.setText(showdata3.getText().toString() + avg +" Utilization"+System.getProperty("line.separator"));

                    mainholeutilization = mainholeutilization+avg;
                    mainholecount++;

                    TextView wall3 = (TextView)findViewById(R.id.wall3utilization);
                    wall3.setText(String.valueOf(avg));


                }
                if(countductw4>0){

                    Integer avg = countutilizationw4/countductw4;
                    showdata4.setText(showdata4.getText().toString() + avg +" Utilization"+System.getProperty("line.separator"));
                    mainholeutilization = mainholeutilization+avg;
                    mainholecount++;


                    TextView wall4 = (TextView)findViewById(R.id.wall4utilization);
                    wall4.setText(String.valueOf(avg));


                }

                int avgmanhole = mainholeutilization/mainholecount;
                TextView manhole = (TextView)findViewById(R.id.mainholeutilization);
                manhole.setText(String.valueOf(avgmanhole));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

    }

    public void resetscreen(){

        for (int l = 0; l < nestductmodelsarray.size(); l++) {


            Integer viewid = nestductmodelsarray.get(l).getWallductview();

            TextView texttoclear = (TextView) findViewById(viewid);
            int intID = getBackgroundColor(texttoclear);

            if(intID == Color.parseColor("#ffffff")) {




                texttoclear.setBackgroundColor(Color.parseColor("#3f51b5"));
                texttoclear.setPaintFlags(texttoclear.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            }



        }



        nestductmodelsarray.clear();
        nestductmodelsarraydel.clear();

    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        resetscreen();
    }


}
