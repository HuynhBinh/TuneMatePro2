package exam.tunemate2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class Tourial extends Activity 
{
   
	Control c1;
 	Control c2;
 	Control c3;
 	Control c4;
 	Control c5;
 	Control c6;
 	Control c7;
 	Control c8;
 	Control c9;
 	Control c10;
 	Control c11;
 	Control c12;
 	Control c13;
 	Control c14;
 	Control c15;
 	Control c16;
 	Control c17;
 	Control c18;
 	Control c19;
 	Control c20;
 	
 	Control c21;
 	Control c22;
 	Control c23;
 	Control c24;
 	Control c25;
 	
 	Control c26;
 	Control c27;

 	
	Control c28;
 	Control c29;
 	ImageView btnBack;
 	
 	ImageView btnNow;
 	

	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tourial);
        
  
        
       btnBack = (ImageView)findViewById(R.id.btnBackTourial);
       
       btnBack.setOnClickListener(new OnClickListener() 
       {
			
			@Override
			public void onClick(View v) 
			{
				finish();
				
			}
		});
       
       	btnNow =(ImageView)findViewById(R.id.btnNowTour);
		btnNow.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				finish();
				
			}
		});

        
        c1 = (Control)findViewById(R.id.c1);
        c1.setIcon(getResources().getDrawable(R.drawable.next1));
        c1.setDescription("Next");
        
        c2 = (Control)findViewById(R.id.c2);
        c2.setIcon(getResources().getDrawable(R.drawable.previous1));
        c2.setDescription("Previous");
        
        
        c3 = (Control)findViewById(R.id.c3);
        c3.setIcon(getResources().getDrawable(R.drawable.next));
        c3.setDescription("Next");
        
        c4 = (Control)findViewById(R.id.c4);
        c4.setIcon(getResources().getDrawable(R.drawable.previous));
        c4.setDescription("Previous");
        
       /* c5 = (Control)findViewById(R.id.c5);
        c5.setIcon(getResources().getDrawable(R.drawable.up));
        c5.setDescription("Increase volume");*/
        
        /*c6 = (Control)findViewById(R.id.c6);
        c6.setIcon(getResources().getDrawable(R.drawable.down));
        c6.setDescription("Decrease volume");*/
        
        c7 = (Control)findViewById(R.id.c7);
        c7.setIcon(getResources().getDrawable(R.drawable.up1));
        c7.setDescription("Increase volume");
        
        c8 = (Control)findViewById(R.id.c8);
        c8.setIcon(getResources().getDrawable(R.drawable.down1));
        c8.setDescription("Decrease volume");
        
        c9 = (Control)findViewById(R.id.c9);
        c9.setIcon(getResources().getDrawable(R.drawable.shuffle));
        c9.setDescription("Shuffle mode");
        
        c10 = (Control)findViewById(R.id.c10);
        c10.setIcon(getResources().getDrawable(R.drawable.repeat));
        c10.setDescription("Repeat mode");
        
        c11 = (Control)findViewById(R.id.c11);
        c11.setIcon(getResources().getDrawable(R.drawable.none));
        c11.setDescription("Normal mode");
        
        c12 = (Control)findViewById(R.id.c12);
        c12.setIcon(getResources().getDrawable(R.drawable.list));
        c12.setDescription("Playlist");
        
        c13 = (Control)findViewById(R.id.c13);
        c13.setIcon(getResources().getDrawable(R.drawable.add));
        c13.setDescription("Add new a Playlist");
        
        
        c14 = (Control)findViewById(R.id.c14);
        c14.setIcon(getResources().getDrawable(R.drawable.tourial));
        c14.setDescription("Tourial");
        
       
        c15 = (Control)findViewById(R.id.c15);
        c15.setIcon(getResources().getDrawable(R.drawable.gesture));
        c15.setDescription("Gesture builder");
        
        
        c21 = (Control)findViewById(R.id.c21);
        c21.setIcon(getResources().getDrawable(R.drawable.shuffle1));
        c21.setDescription("Shuffle mode");
        
        c22 = (Control)findViewById(R.id.c22);
        c22.setIcon(getResources().getDrawable(R.drawable.shuffle2));
        c22.setDescription("Shuffle mode");
        
        c23 = (Control)findViewById(R.id.c23);
        c23.setIcon(getResources().getDrawable(R.drawable.repeat1));
        c23.setDescription("Repeat mode");
        
        
        c24 = (Control)findViewById(R.id.c24);
        c24.setIcon(getResources().getDrawable(R.drawable.plist1));
        c24.setDescription("Playlist");
        
        
        c25 = (Control)findViewById(R.id.c25);
        c25.setIcon(getResources().getDrawable(R.drawable.none2));
        c25.setDescription("Normal mode");
        
        
        
        c16 = (Control)findViewById(R.id.c16);
        c16.setIcon(getResources().getDrawable(R.drawable.add));
        c16.setDescription("Add new a Playlist");
        
        c17 = (Control)findViewById(R.id.c17);
        c17.setIcon(getResources().getDrawable(R.drawable.add1));
        c17.setDescription("Add new a Playlist");
        
        c18 = (Control)findViewById(R.id.c18);
        c18.setIcon(getResources().getDrawable(R.drawable.add2));
        c18.setDescription("Add new a Playlist");
        
        c19 = (Control)findViewById(R.id.c19);
        c19.setIcon(getResources().getDrawable(R.drawable.previous));
        c19.setDescription("Back");
        
        c20 = (Control)findViewById(R.id.c20);
        c20.setIcon(getResources().getDrawable(R.drawable.none));
        c20.setDescription("Now playing");
        
        
        
        c26 = (Control)findViewById(R.id.c26);
        c26.setIcon(getResources().getDrawable(R.drawable.previous));
        c26.setDescription("Back");
        
        
        c27 = (Control)findViewById(R.id.c27);
        c27.setIcon(getResources().getDrawable(R.drawable.none));
        c27.setDescription("Now playing");
        
        
        c28 = (Control)findViewById(R.id.c28);
        c28.setIcon(getResources().getDrawable(R.drawable.previous1));
        c28.setDescription("Back");
        
        
        c29 = (Control)findViewById(R.id.c29);
        c29.setIcon(getResources().getDrawable(R.drawable.previous1));
        c29.setDescription("Back");
        
              
        
    }


}