package my.com.tm.idraw;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class mainholewall extends AppCompatActivity {

    LatLng objLatLng;
    String Markername;
    ArrayList<String> nestduct = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainholewall);



        objLatLng=getIntent().getExtras().getParcelable("markerlatlng");
        Markername = getIntent().getStringExtra("markertitle");



    }


    public void clickduct(View view){

     String idresource = view.getResources().getResourceName(view.getId());
     String ids = idresource.replace("my.com.tm.idraw:id/","");



        TextView texttouch = (TextView)findViewById(view.getId());



        int intID = getBackgroundColor(texttouch);

             if(intID == Color.parseColor("#3f51b5")){

             texttouch.setBackgroundColor(Color.parseColor("#ffffff"));
             nestduct.add(ids);

             }
            if(intID == Color.parseColor("#ffffff")){

                texttouch.setBackgroundColor(Color.parseColor("#3f51b5"));
                nestduct.remove(ids);

            }


//                Toast.makeText(mainholewall.this, "Clicking "+ids, Toast.LENGTH_LONG)
//                .show();

    }

    public void clickductbutton(View view){

        String idresource = view.getResources().getResourceName(view.getId());
        String ids = idresource.replace("my.com.tm.idraw:id/","");
        String wall = ids.substring(6);

        Integer n = nestduct.size();
        Boolean sent_ok = false;

        if(n>0){


            for(int l=0; l<n; l++){

                String wallduct = nestduct.get(l);
                String duct = wallduct.substring(2);
                String walls = wallduct.substring(0, 2);

                if(walls.equals(wall)){

                    Toast.makeText(mainholewall.this, "duct "+wallduct, Toast.LENGTH_LONG)
                            .show();
                }


            }


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
}
