package exam.tunemate2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class Library extends Activity 
{

	
	Control c1;
 	Control c2;
 	Control c3;
 	Control c4;
 	
 	ImageView btnBack;
 	ImageView btnNow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.library);
		
		btnBack = (ImageView)findViewById(R.id.btnBackLib);
		btnBack.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				finish();
				
			}
		});
		
		btnNow = (ImageView)findViewById(R.id.btnNowLib);
		btnNow.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				finish();
				
			}
		});
		
		 c1 = (Control)findViewById(R.id.c1);
	     c1.setIcon(getResources().getDrawable(R.drawable.allsongicon));
	     c1.setDescription("All Songs");
	     
	     c2 = (Control)findViewById(R.id.c2);
	     c2.setIcon(getResources().getDrawable(R.drawable.albumicon));
	     c2.setDescription("Album");
	     
	     c3 = (Control)findViewById(R.id.c3);
	     c3.setIcon(getResources().getDrawable(R.drawable.playlisticon));
	     c3.setDescription("Playlist");
	     
	     c4 = (Control)findViewById(R.id.c4);
	     c4.setIcon(getResources().getDrawable(R.drawable.musifolder));
	     c4.setDescription("Folder");
	    
	     c1.setOnClickListener(new OnClickListener() 
	     {
			
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				
				Intent allsong = new Intent(getApplicationContext(),AllSongActivity1.class);
				startActivity(allsong);
				finish();				
			}
		});
	     
	     c2.setOnClickListener(new OnClickListener() 
	     {
				
				@Override
				public void onClick(View v) 
				{
					
					Intent intent = new Intent(getApplicationContext(), GridViewActivity.class);
			        startActivity(intent); 
					
					finish();
				}
	     });
	     
	     c3.setOnClickListener(new OnClickListener() 
	     {
				
				@Override
				public void onClick(View v) 
				{
					// TODO Auto-generated method stub
					
					Intent plist = new Intent(getApplicationContext(),Playlist.class);
					startActivity(plist);
					finish();					
				}
			});
	     
	     c4.setOnClickListener(new OnClickListener() 
	     {
				
				@Override
				public void onClick(View v) 
				{
					// TODO Auto-generated method stub
					
					Intent plist = new Intent(getApplicationContext(),FolderActivity.class);
					startActivity(plist);
					finish();					
				}
			});
			
	}
}
