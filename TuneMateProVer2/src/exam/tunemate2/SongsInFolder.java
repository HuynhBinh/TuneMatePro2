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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SongsInFolder extends Activity implements Runnable
{
	
	ArrayList<Song> listSongs;
	String folderName;
	private static final String TITLE = "title";  
    private static final String IMAGE = "image"; 
			
	//ArrayAdapter<Song> adapter;
	
	GlobalVariable Gvar;
	
	private ArrayList <HashMap<String, Object>> mySongs; 
    ImageView btnBack;	
	ImageView btnNow;
	ImageView btnTest;
	SimpleAdapter adapter1;
	int sizeListSong = 0;
	Thread thread;
	int tmp = 0;
	boolean isexits = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.songsinfolder);
		
		Gvar = (GlobalVariable)getApplicationContext();
		ListView listView = (ListView)findViewById(R.id.list);
		
		mySongs = new ArrayList<HashMap<String,Object>>(); 
		listSongs = new ArrayList<Song>();
		
		Bundle getsong = getIntent().getExtras();
		
		folderName = getsong.getString("folder");
		
		
		listSongs = getSongInFolder(getApplicationContext(), folderName);		
		
		
		getdata();
		
		
		 adapter1 = new SimpleAdapter(this, mySongs, R.layout.custom_playbt_listview, new String[]{TITLE,IMAGE}, new int[]{R.id.title,R.id.button}); 			                

		listView.setAdapter(adapter1);  

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
				
				Gvar.GListSong = listSongs;
				sizeListSong = Gvar.GListSong.size();
				Gvar.isReturnMain = true;
				Gvar.ListMode = "folder";
				// Neu fail thi PlaylistId = 0 => get Allsong => ko crash
				Gvar.playListId = 0;
				//new for folder only
				Gvar.folderName = folderName;
				//
				Gvar.currentSongInList = position - 1 ;
				//Log.i("currentsong",position - 1 +"" );
				if(Gvar.isPlaying == false)
				{
					Gvar.isPlaying = true;
				}
				finish();		
			}
			
		});
		
		btnBack = (ImageView)findViewById(R.id.btnBackSonginFolder);
		btnBack.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				// intent to list
 	        	
 	        	Intent intent = new Intent(getApplicationContext(), FolderActivity.class);
				startActivity(intent);
				finish();
				
			}
		});
		
		btnNow =(ImageView)findViewById(R.id.btnNowSonginFolder);
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
		
		super.onPause();
	}
	
	
	public static ArrayList<Song> getSongInFolder(Context context, String folderName)
	{
		ArrayList<Song> songsInFolder = new ArrayList<Song>();
		try{
				
		if(folderName.equals("sdcard"))
		{
			songsInFolder = getSongInSDcardOnly(context);
		}
		else
		{
	
			ContentResolver contentResolver = context.getContentResolver();
				
			Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			
			String fullPath = android.provider.MediaStore.Audio.Media.DATA;
			
			
			// 26-02-2012 Updated projection and fixed sql injection
			String[] projection = { 
					android.provider.MediaStore.Audio.Media.TITLE ,
					android.provider.MediaStore.Audio.Media._ID ,
					android.provider.MediaStore.Audio.Media.ALBUM_ID };
			
			
			Cursor cursor = contentResolver.query(uri, projection, fullPath + " like '%" + folderName.replace("'", "''").trim() + "%'" , null, null);
					
			try
			{
				cursor.moveToFirst();
							
				int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
			    int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
			    int albumidColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM_ID);
			   
			    do 
			    {
			       long thisId = cursor.getLong(idColumn);
			       String thisTitle = cursor.getString(titleColumn); //title album
			       long albumid = cursor.getLong(albumidColumn);
			     
			      
			       Song song = new Song(thisId, thisTitle, albumid);
			       songsInFolder.add(song);
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
		
		}
		}
		catch(Exception e)
		{
			Log.e("Cursor null", "null nullnullnullnullnullnullv");
		}
			
		return songsInFolder;
	}
	
	
	public static ArrayList<Song> getSongInSDcardOnly(Context context)
	{
		
		ArrayList<Song> listSongsInSD = new ArrayList<Song>();
		
		ContentResolver contentResolver = context.getContentResolver();
		
		Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		
		String[] projection = { android.provider.MediaStore.Audio.Media.DATA ,
								android.provider.MediaStore.Audio.Media.TITLE ,
								android.provider.MediaStore.Audio.Media._ID ,
								android.provider.MediaStore.Audio.Media.ALBUM_ID };
		
		Cursor cursor = contentResolver.query(uri, projection, null, null, null);
		
		int DataIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.DATA);
		int TitleIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.TITLE);
		int IDIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media._ID);
		int AlbumIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.ALBUM_ID);
		
		
		try
		{
			cursor.moveToFirst();
						
			do 
		    {
				
				String fullPath = cursor.getString(DataIndex);
				
				String folder = Ultils.getFolderName(Ultils.getDirectory(fullPath));
				
				// if folder == "" it is SDcard 
				if (folder.equals("sdcard"))
				{
					
					long thisId = cursor.getLong(IDIndex);
				    String thisTitle = cursor.getString(TitleIndex); //title album
				    long albumid = cursor.getLong(AlbumIndex);
					
					Song song = new Song(thisId, thisTitle, albumid);
					listSongsInSD.add(song);
				}
								
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
		
		return listSongsInSD;
	}
	
	
	public void getdata()
	{
		
		int i = (int)Gvar.GListSong.get(Gvar.currentSongInListInSongselected).id ;
		
		Log.i("list song in getdata", listSongs.size() +"" );
		
		// Position song in list 
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
			
			
		
			if (folderName.equals(Gvar.folderName) && Gvar.ListMode.equals("folder"))
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
			
				adapter1.notifyDataSetChanged();
			
			}
		
			else 
			{
				int i1 = (int)Gvar.GListSong.get(Gvar.currentSongInListInSongselected).id ;				
				int position = 0;
				
				Log.i("LIST SONG SIZE", listSongs.size() +"");
				
				
				
				for (Song s : listSongs)
				{
					if(s.id == i1 && isexits == true)
					{
						 HashMap<String, Object> hm1;
							hm1 = new HashMap<String, Object>();
							hm1.put(TITLE, Gvar.GListSong.get(j).getTitle());
							hm1.put(IMAGE,R.drawable.splay);
							
							Log.i("POSITION 1" , position +"");
							
							mySongs.set(position, hm1);
							
							// Set stop image by previous Song id
							HashMap<String, Object> hm;
							hm = new HashMap<String, Object>();
							hm.put(TITLE, Gvar.GListSong.get(i).getTitle());
							hm.put(IMAGE,R.drawable.sstop);	
							
							Log.i("Bien Tam 1" , tmp +"");
							
							mySongs.set(tmp, hm);
						
							//adapter1.notifyDataSetChanged();
					    	tmp = position;
					    	
					}else
					
					if (s.id == i1 && isexits == false)
					{
						HashMap<String, Object> hm1;
						hm1 = new HashMap<String, Object>();
						hm1.put(TITLE, s.title);
						hm1.put(IMAGE,R.drawable.splay);	
						Log.i("POSITION 2" , position +"");
						
						mySongs.set(position, hm1);
						//adapter1.notifyDataSetChanged();
						isexits = true;
						tmp = position;
					}
					else {
						
						HashMap<String, Object> hm1;
						hm1 = new HashMap<String, Object>();
						hm1.put(TITLE, s.title);
						hm1.put(IMAGE,R.drawable.sstop);													
						mySongs.set(position, hm1);
												
					}
					
					
					position++;
				}
				
				adapter1.notifyDataSetChanged();
			}
			
		}
	};
	
	

}
