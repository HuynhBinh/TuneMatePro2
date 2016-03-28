package exam.tunemate2;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SongSelected extends Activity implements Runnable
{

	private DBHelper mDB;
	private Cursor mCursor;
	//public ArrayList<Song> ListSong = new ArrayList<Song>();
	private ArrayList<String> namelist1 = new ArrayList<String>();
	private ArrayList <HashMap<String, Object>> mySongs; 
	private static final String TITLE = "title";  
    private static final String IMAGE = "image"; 
    
    

	ArrayAdapter<String> array;
	GlobalVariable Gvar;
	int idplist;
	
	String PlaylistName="";
	
	ImageView btnBack;
	
	ImageView btnNow;
	
	TextView txtSongSelected;
	int tmp = 0;
	boolean isexits = false;
	
	Thread thread;
	

	SimpleAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.songselected);
			
     	
			
		ListView listView = (ListView)findViewById(R.id.list);
		mySongs = new ArrayList<HashMap<String,Object>>(); 
		
		btnBack = (ImageView)findViewById(R.id.btnBackSongSelected);
		txtSongSelected = (TextView)findViewById(R.id.txtSongSelected);
		
		
		Gvar = (GlobalVariable)getApplicationContext();
		
		mDB = new DBHelper(this);
		mDB.open();
		
		Bundle getsong = getIntent().getExtras();
		idplist = getsong.getInt("idplist");
		PlaylistName = getsong.getString("plistName");
		
		
		txtSongSelected.setText(PlaylistName);
		
		getData(idplist);
	
		
		adapter = new SimpleAdapter(this, mySongs, R.layout.custom_playbt_listview, new String[]{TITLE,IMAGE}, new int[]{R.id.title,R.id.button}); 			                

		listView.setAdapter(adapter);  

		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);  

	
		
		listView.setOnItemClickListener(new OnItemClickListener() 
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				
				try
				{
					thread.interrupt();
				}
				catch(Exception e)
				{
					
				}
				
				
				//Toast.makeText(getApplicationContext(), position + "", Toast.LENGTH_SHORT).show();
				Gvar.GListSong = SongSelected.getSongsInPlist(getApplicationContext(), idplist);
				Gvar.isReturnMain = true;
				Gvar.ListMode = "playlist";
				Gvar.playListId = idplist;
				Gvar.currentSongInList = position - 1 ;
				
				if(Gvar.isPlaying == false)
				{
					Gvar.isPlaying = true;
				}
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
		
		btnNow =(ImageView)findViewById(R.id.btnNowSSted);
		btnNow.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
		
		
		try 
		{
			thread = new Thread(this);
			thread.start();
		} 
		catch (Exception e)		
		{
				
			
		}
		
		
	}
	
	public static  ArrayList<Song> getSongsInPlist(Context context, int idplist)
	{
	
		DBHelper mDB1 = new DBHelper(context);
		mDB1.open();
		
		Cursor mCursor1 = null;
		
		ArrayList<Song> ReturnList = new ArrayList<Song>();
		try
		{
		mCursor1 = mDB1.getPlaylistSong(idplist);
		
		
			mCursor1.moveToFirst();
			
			while (!mCursor1.isAfterLast()) 
			{
				
				int idsong = mCursor1.getInt(0);
				String title = mCursor1.getString(1);
				int albumid = mCursor1.getInt(3);
			
			
				Song song = new Song(idsong, title, albumid);
				ReturnList.add(song);
	
				
				mCursor1.moveToNext();
			}
	
			
		}
		catch(Exception e)
		{
			
		}
		finally
		{
			mCursor1.close();
		}
		
		mDB1.close();
		
		return ReturnList;
	}
	
	public void getData(int idplist) 
	{

		mCursor = mDB.getPlaylistSong(idplist);
		// Position song in list 
		int j =0;
		try
		{
		
			mCursor.moveToFirst();
		
			while (!mCursor.isAfterLast()) 
			{
				int idsong = mCursor.getInt(0);
				String title = mCursor.getString(1);	
				
			
				boolean exists = checkexist(title);
			
				if(exists == false)
				{
					mDB.deleteSongnotexist(idsong, idplist);
				}
				else
				{
					int i = (int)Gvar.GListSong.get(Gvar.currentSongInListInSongselected).id ;
					
					HashMap<String, Object> hm;
					hm = new HashMap<String, Object>();
					hm.put(TITLE, title);		
					
					if(idsong == i)
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
					namelist1.add(title);
					j++;
				}
				
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
	
	public static ArrayList<Song>  getMusics(Context context)
	{
		ArrayList<Song> ListSong = new ArrayList<Song>();
		
		ContentResolver contentResolver = context.getContentResolver();
		
		Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		
		String[] projection = { 
								android.provider.MediaStore.Audio.Media.TITLE ,
								android.provider.MediaStore.Audio.Media._ID ,
								android.provider.MediaStore.Audio.Media.ALBUM_ID };
			
		Cursor cursor = contentResolver.query(uri, projection , null, null, null);	
		
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
		       ListSong.add(song);
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
		
		return ListSong;	
	}
	
	
	public boolean checkexist (String title)
	{
		for (Song song1 : getMusics(getApplicationContext()))
		{
			if(song1.title.equals(title))
			{
				return true;
			}
			
		}
		return false;
	}

	
	@Override
	protected void onPause() 
	{
		mDB.close();
		super.onPause();
	}
	
	@Override
	protected void onStop() 
	{
		try
		{
			if(!thread.isInterrupted())
			{
				thread.interrupt();
			}
		}
		catch(Exception e)
		{
			
		}
		
		thread = null;
		
		super.onStop();
				
	}

	@Override
	public void run() {
		int CurrentPosition=  Gvar.currentSongInList;
		int i =  Gvar.currentSongInList ;
		
	
		while(i == CurrentPosition)
		{
			 try
	            {
	                Thread.sleep(1000);
	                CurrentPosition =  Gvar.currentSongInList;
	                Log.i("CurrentPosition in while ", CurrentPosition + "");
	                
	                if(i != CurrentPosition)
	                {             
	                	
	                	Message mess = new Message();
	     				Bundle bund = new Bundle();
	     	            bund.putInt("id", i);
	     	            mess.setData(bund);	  
	     	            Thread.sleep(1000);
	     	            i = Gvar.currentSongInList ;	     	           
	     	            handler.sendMessage(mess);	     	               	      
	                }
	              
	                
	            }
			 catch (InterruptedException e) 
	            {
	                return;
	            } 
	         catch (Exception e)
	            {
	                return;
	            } 
			 
		}
		
	}
	
	
	private Handler handler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) 
		{
			// Previous Song id
			int i  = msg.getData().getInt("id");
			
			// Current Song id
			int j = Gvar.currentSongInList;
			
			
			if(Gvar.ListMode.equals("playlist") && Gvar.playListId == idplist)
			{
				// Set play image by current Song id
			    HashMap<String, Object> hm1;
				hm1 = new HashMap<String, Object>();
				hm1.put(TITLE, Gvar.GListSong.get(j).getTitle());
				hm1.put(IMAGE,R.drawable.splay);				
				mySongs.set(j, hm1);
			
				// Set stop image by previous Song id
				HashMap<String, Object> hm;
				hm = new HashMap<String, Object>();
				hm.put(TITLE, Gvar.GListSong.get(i).getTitle());
				hm.put(IMAGE,R.drawable.sstop);				
				mySongs.set(i, hm);
			
				adapter.notifyDataSetChanged();
			
			}
			
			else 
			{
				int i1 = (int)Gvar.GListSong.get(Gvar.currentSongInListInSongselected).id ;
				
				int position = 0;
				
				ArrayList<Song> ListSong = new ArrayList<Song>();
				
				ListSong = getSongsInPlist(getApplicationContext(), idplist);
				
				
				
				for (Song s : ListSong)
				{
					if(s.id == i1 && isexits == true)
					{
						    HashMap<String, Object> hm1;
							hm1 = new HashMap<String, Object>();
							hm1.put(TITLE, s.title);
							hm1.put(IMAGE,R.drawable.splay);				
							mySongs.set(position, hm1);
						
							// Set stop image by previous Song id
							HashMap<String, Object> hm;
							hm = new HashMap<String, Object>();
							hm.put(TITLE, Gvar.GListSong.get(i).getTitle());
							hm.put(IMAGE,R.drawable.sstop);				
							mySongs.set(tmp, hm);
						
							adapter.notifyDataSetChanged();
							//Update lai bien tam
							tmp = position;
					}else				
					
					if (s.id == i1 && isexits == false)
					{
						HashMap<String, Object> hm1;
						hm1 = new HashMap<String, Object>();
						hm1.put(TITLE, s.title);
						hm1.put(IMAGE,R.drawable.splay);				
						mySongs.set(position, hm1);
						adapter.notifyDataSetChanged();
						isexits = true;
						tmp = position;
					}
					else {
						HashMap<String, Object> hm1;
						hm1 = new HashMap<String, Object>();
						hm1.put(TITLE, s.title);
						hm1.put(IMAGE,R.drawable.sstop);				
						mySongs.set(position, hm1);
						adapter.notifyDataSetChanged();
						
					}
				
					position ++;
					
				}
								
			}
			
		}
	};
	

}
