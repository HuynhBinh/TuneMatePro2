package exam.tunemate2;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class SongInAlbum extends Activity implements Runnable
{
	
	//ArrayAdapter<Song> adapter;
	SimpleAdapter adapter1;
	
	ArrayList<Song> listSongs;
	
	ListView listView; 
	
	long AlbumId = 0;
	String albumName = "";
	
	TextView txtSongAlbum;
	
	Context context;
	
	ImageView btnBack;
	
	ImageView btnNow;
	
	GlobalVariable Gvar;
	String Check  = "port";
	
	Thread thread;
	int tmp = 0;
	boolean isexits = false;
	
	private static final String TITLE = "title";  
    private static final String IMAGE = "image"; 
    
    private ArrayList <HashMap<String, Object>> mySongs; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.songinalbum);
		
		context = this.getApplicationContext();
		
		Gvar = (GlobalVariable)getApplicationContext();
		
		listView = (ListView)findViewById(R.id.list);
		
		txtSongAlbum = (TextView)findViewById(R.id.txtSongAlbum);
		
		btnBack = (ImageView)findViewById(R.id.btnBackSongInAl);
		
		btnNow =  (ImageView)findViewById(R.id.btnNowSongInAl);
		
		mySongs = new ArrayList<HashMap<String,Object>>(); 
			
		Bundle getsong = getIntent().getExtras();
		
		AlbumId = getsong.getLong("albumID");
		albumName = getsong.getString("albumName");
		
		
		txtSongAlbum.setText(albumName);

		
		listSongs = getSong(context , AlbumId);
		
		getData();
		
		
		adapter1 = new SimpleAdapter(this, mySongs, R.layout.custom_playbt_listview, new String[]{TITLE,IMAGE}, new int[]{R.id.title,R.id.button}); 			                

		listView.setAdapter(adapter1);  

		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);  	
		
	
		btnBack.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				if(Check.equals("port"))
				{
					Intent intent = new Intent(context, GridViewActivity.class);
					startActivity(intent);
				
					finish();
				}
				else
				{
					Intent intent = new Intent(context, CoverFlowActivity.class);
					startActivity(intent);
				
					finish();
				}
			}
		});
		
		btnNow.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				finish();
				
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() 
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				
				
				Gvar.GListSong = getSong(context, AlbumId);
				Gvar.isReturnMain = true;
				Gvar.ListMode = "album";
				Gvar.playListId = (int)AlbumId;
				Gvar.currentSongInList = position - 1 ;
				//Log.i("currentsong",position - 1 +"" );
				if(Gvar.isPlaying == false)
				{
					Gvar.isPlaying = true;
				}
				finish();	
				
			}
			
		});
		
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		 if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) 
		 {
			 Check = "land";
		 }
		 else if (getResources().getConfiguration().orientation ==  Configuration.ORIENTATION_PORTRAIT) 
		 {
			 Check = "port";
		 }
		super.onConfigurationChanged(newConfig);
	}
	
	public static ArrayList<Song> getSong(Context context , long AlbumId)
	{
		ContentResolver contentResolver = context.getContentResolver();
		
		ArrayList<Song> albumSongs = new ArrayList<Song>();
		
		Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		
		String[] projection = { 
								android.provider.MediaStore.Audio.Media.TITLE ,
								android.provider.MediaStore.Audio.Media._ID ,
								android.provider.MediaStore.Audio.Media.ALBUM_ID };
		
		String id = android.provider.MediaStore.Audio.Albums.ALBUM_ID;
		
		Cursor cursor = contentResolver.query(uri, projection , id + " = " + AlbumId , null, null);
		
		int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
	    int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
	    int albumidColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM_ID);
		
		try
		{
			cursor.moveToFirst();
						
		    do 
		    {
		       long thisId = cursor.getLong(idColumn);
		       String thisTitle = cursor.getString(titleColumn); //title album
		       long albumid = cursor.getLong(albumidColumn);
		      
		       
		       Song song = new Song(thisId, thisTitle, albumid);
		       albumSongs.add(song);
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
		
		return albumSongs;
	}
	
	
	public void getData()
	{
		int i = (int)Gvar.GListSong.get(Gvar.currentSongInListInSongselected).id ;
		int j =0;
		
		for(Song song : listSongs)
		{
			HashMap<String, Object> hm;
			hm = new HashMap<String, Object>();
			hm.put(TITLE, song.title);
			
			if(song.id == i)
			{
				hm.put(IMAGE,R.drawable.splay);
				tmp = j;
				isexits = true;
			}
			else
			{
				hm.put(IMAGE,R.drawable.sstop);
			}
			
			mySongs.add(hm);
			j++;
		}
		
		
	}

	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		
	}
	

}
