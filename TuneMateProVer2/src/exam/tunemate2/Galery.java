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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Galery extends Activity 
{
	TextView namealbum;
	ArrayList<AlbumInfo> AlbumNames ;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.galery);
		AlbumNames = new ArrayList<AlbumInfo>();
		
		AlbumNames = getAlbum();
		namealbum = (TextView) findViewById(R.id.namealbum);
		
		Gallery gallery = (Gallery) findViewById(R.id.gallery);
		gallery.setAdapter(new ImageApdater(this));
		
		 gallery.setOnItemClickListener(new OnItemClickListener() 
		 {           
				@Override
				public void onItemClick(AdapterView<?> arg0,  View arg1, int position, long arg3) 
				{
					Intent intent = new Intent(getApplicationContext(), SongInAlbumGLR.class);
					intent.putExtra("albumID", AlbumNames.get(position).Id);
					intent.putExtra("albumName", AlbumNames.get(position).name );
					startActivity(intent);
					
					finish();
				}
	        });
		 
		 gallery.setOnItemSelectedListener(new OnItemSelectedListener() 
		 {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{
				
				namealbum.setText(AlbumNames.get(arg2).name);
				 
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) 
			{
				
				
			}
		});

	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		 if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) 
		 {
			 //Toast.makeText(getApplicationContext(), "Land", Toast.LENGTH_LONG).show();
		 }
		 else if (getResources().getConfiguration().orientation ==  Configuration.ORIENTATION_PORTRAIT) 
		 {
			 // do nothing
			 Intent intent = new Intent(getApplicationContext(), GridViewActivity.class);
			 startActivity(intent);
			 finish(); 
			 //Toast.makeText(getApplicationContext(), "Port", Toast.LENGTH_LONG).show();
		 }
		super.onConfigurationChanged(newConfig);
	}

	public  ArrayList<AlbumInfo> getAlbum()
	{
		ContentResolver contentResolver = this.getContentResolver();
	
		ArrayList<AlbumInfo> albumNames = new ArrayList<AlbumInfo>();
		
		Uri uri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
		
		String[] projection = { 
									android.provider.MediaStore.Audio.Albums.ALBUM ,
									android.provider.MediaStore.Audio.Albums._ID };
			
		Cursor cursor = contentResolver.query(uri, projection , null, null, null);
		
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
	
	
}
