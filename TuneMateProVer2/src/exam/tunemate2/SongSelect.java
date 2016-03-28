package exam.tunemate2;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class SongSelect extends ListActivity 
{

	 private Cursor mCursor;
	 private ListView mainListView = null;
	 MusicStore musicStore;
	 ArrayList<Song> ListSong = new ArrayList<Song>();
	 ArrayList<Song> ListSong1 = new ArrayList<Song>();
	 ArrayList<String> namemusic = new ArrayList<String>();
	 ArrayList<Integer> position = new ArrayList<Integer>();
	 private DBHelper mDB;
	 
	 ImageView btnBack;
	 
	 ImageView btnNow;
	 

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.songselect);
		
			
		btnBack = (ImageView)findViewById(R.id.btnBackSongSelect);
		
		mDB = new DBHelper(this);
		mDB.open();
		
		musicStore = new MusicStore();
		getData();
		getmusic();		
		
        this.mainListView = getListView();
        mainListView.setCacheColorHint(0);
        mainListView.setAdapter(new ArrayAdapter<String>(SongSelect.this, android.R.layout.simple_list_item_multiple_choice, namemusic));
        mainListView.setItemsCanFocus(false);
        
        mainListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);   
        check();
		
        Button btnPlaylist = (Button) findViewById(R.id.button1);
        
		btnPlaylist.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				SaveSelect();
				//Log.i("savsasasas", "Da save");
			    Intent backlist = new Intent(getApplicationContext(),Playlist.class);
			    startActivity(backlist);
			    finish();
			    			    
			}
		});
		
		btnBack.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(getApplicationContext(), Playlist.class);
				startActivity(intent);
				finish();
				
			}
		});
		
		btnNow =(ImageView)findViewById(R.id.btnNowSSelect);
		btnNow.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				finish();
				
			}
		});
						
	}
	
	public void getmusic()
	{
        ContentResolver contentResolver = this.getContentResolver();
		
		Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		
		String[] projection = { 
				android.provider.MediaStore.Audio.Media.TITLE ,
				android.provider.MediaStore.Audio.Media._ID ,
				android.provider.MediaStore.Audio.Media.ALBUM_ID,
				android.provider.MediaStore.Audio.Media.DURATION};
		
		Cursor cursor = contentResolver.query(uri, projection , null, null, null);
		
		int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
	    int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
        int albumidColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM_ID);
        int durationColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DURATION);

        try
        {
        	
        	cursor.moveToFirst();
        	
            int i = 0;
            
		    do 
		    {
		       long thisId = cursor.getLong(idColumn);
		       String thisTitle = cursor.getString(titleColumn); //title album
               long albumid = cursor.getLong(albumidColumn);
               long duration = cursor.getLong(durationColumn);
               
               
               //

		       
		       if(duration > 10000)
		       {		       
		    	   Song song = new Song(thisId, thisTitle, albumid);
			       namemusic.add(thisTitle);
			       ListSong.add(song); 
			       
			       for (Song s : ListSong1)
			       {
			    	   if(s.id == thisId)
			    	   {		    		  
			    		   position.add(i);		    		   	    		   
			    	   }
			       }
			       
			       i++; 
		       }
		       else
		       {
		    	   Log.e("SONG NAME", thisTitle);
		       }
           	  //
                      
		       
		    } 
		    while (cursor.moveToNext());
		    
        }
        catch(Exception e)
        {
        	
        }
        finally
        {
		    
		    mCursor.close();
		}	
		
	}
	
	public void SaveSelect()
	{
		int count = this.mainListView.getAdapter().getCount();
		Bundle extras = getIntent().getExtras();
        int idlist = extras.getInt("idlist");
        ArrayList<Song> songInPl = SongSelected.getSongsInPlist(getApplicationContext(), idlist);
        int sizelist = songInPl.size();
        
		for(int i=0;i<count;i++)
		{
			
			if(mainListView.isItemChecked(i))
			{
				Song select = ListSong.get(i);
				int id = (int)select.getId();
				String title = select.getTitle();
                int albumid = (int)select.getAlbumid();
				
				//mDB.open();
				mDB.createPlaylistSong(id, title, idlist,albumid);				
			} 
			else 
			{
				// SIZELIST > 0 : EDIT MODE ,SIZELIST < 0 ADD
				if(sizelist >0)
				{			
					
					Song select = ListSong.get(i);
					int id = (int)select.getId();
					for(int j =  0; j <sizelist ; j++)
					{
						if(id == songInPl.get(j).id)
						{
						//String title = select.getTitle();
						//mDB.open();
							mDB.deleteSongnotexist(id, idlist);		
						}
					}
				}
			}
			
		}
		
	}
	
	public void getData() 
	{
		Bundle extras = getIntent().getExtras();
        int idlist = extras.getInt("idlist");
		mCursor = mDB.getPlaylistSong(idlist);
		
		try
		{
		
			mCursor.moveToFirst();
			
			while (!mCursor.isAfterLast())
			{
				int idsong = mCursor.getInt(0);
				String title = mCursor.getString(1);	
				int albumid = mCursor.getInt(3);
				//Log.i("IDsONG", idsong+"");
				//Log.i("Title", title);
				
				 Song song = new Song(idsong, title, albumid);
			     ListSong1.add(song);			
				 mCursor.moveToNext();
			}
		}
		catch(Exception e)
		{
			
		}
		finally
		{
			 mCursor.close();
		}
	   
	}
	
	public void check()
	{
		int count = this.mainListView.getAdapter().getCount();
		int countposition = position.size();
			
		
		if(countposition!=0)
		{
			for(int i=0 ;i<count;i++)
			{
				for(int j = 0; j<countposition;j++)
					if(i == position.get(j))
					{
						mainListView.setItemChecked(i, true);
					
					} 
			}
		}
	}
	
	@Override
	protected void onPause() 
	{
		mDB.close();
		super.onPause();
	}

		
	
}
