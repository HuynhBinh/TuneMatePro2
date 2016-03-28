package exam.tunemate2;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

public class GridViewActivity extends Activity 
{
	ArrayList<AlbumInfo> AlbumNames ;
	
	ImageView btnBack;
	ImageView btnNow;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gridviewalbum);
		
		
		
		AlbumNames = new ArrayList<AlbumInfo>();
		
		AlbumNames = getAlbum();
		
		GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(new GridViewImageAdapter(this));
	    
	    
	    gridview.setOnItemClickListener(new OnItemClickListener() 
		 {           
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
				{
					Intent intent = new Intent(getApplicationContext(), SongInAlbumGV.class);
					intent.putExtra("albumID", AlbumNames.get(position).Id);
					intent.putExtra("albumName", AlbumNames.get(position).name );
					startActivity(intent);
						
					finish();
				}
	        });
	    
	    
	    btnBack = (ImageView)findViewById(R.id.btnBackGridAlbum);
	    btnBack.setOnClickListener(new OnClickListener() 
	    {
			
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(getApplicationContext(), Library.class);
				startActivity(intent);
				finish();
				
			}
		});
	    
	    
	    btnNow = (ImageView)findViewById(R.id.btnNowGridAlbum);
	    btnNow.setOnClickListener(new OnClickListener() 
	    {
			
			@Override
			public void onClick(View v) 
			{
				finish();
				
			}
		});
	    
		
	}
	
	public  ArrayList<AlbumInfo> getAlbum()
	{
		ContentResolver contentResolver = this.getContentResolver();
	
		ArrayList<AlbumInfo> albumNames = new ArrayList<AlbumInfo>();
		
		Uri uri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
		
		String[] projection = { 
								android.provider.MediaStore.Audio.Albums.ALBUM ,
								android.provider.MediaStore.Audio.Albums._ID };
			
		Cursor cursor = contentResolver.query(uri, projection, null, null, null);
		
		int index = cursor.getColumnIndex(android.provider.MediaStore.Audio.Albums.ALBUM);
		int IdIndex = cursor.getColumnIndex(android.provider.MediaStore.Audio.Albums._ID);

		try
		{
			cursor.moveToFirst();
						
			do 
		    {
				
				String name = cursor.getString(index);
				long id = cursor.getLong(IdIndex);
				
				AlbumInfo albumInfo = new AlbumInfo(name, id);
						
				albumNames.add(albumInfo);
		    } 
		    while (cursor.moveToNext());
				
		}
		catch(Exception e)
		{
			
		}
		finally
		{
			cursor.close();
		}
		
		return albumNames;
	}
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		 if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) 
		 {
			 Intent intent = new Intent(this ,CoverFlowActivity.class);
			 startActivity(intent);
			 finish(); // voi cho nay`
			 //Toast.makeText(this, "Land", Toast.LENGTH_LONG).show();
		 }
		 else if (getResources().getConfiguration().orientation ==  Configuration.ORIENTATION_PORTRAIT) 
		 {
			 // do nothing
			 //Toast.makeText(this, "Port", Toast.LENGTH_LONG).show();
		 }
		super.onConfigurationChanged(newConfig);
	}
	

	
}

