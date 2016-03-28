package exam.tunemate2;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;





public class Playlist extends ListActivity  implements OnGesturePerformedListener
{

	private DBHelper mDB;
	
	private Cursor mCursor;
	
	private ArrayList<Plist> mPlist = new ArrayList<Plist>();
	
	private ArrayList<String> namelist = new ArrayList<String>();
	
	ArrayAdapter<String> array;
	
	int idpl;
	
	String nameplista;
	
	GlobalVariable Gvar;
	
	int NumOfUseApp = 0;
	
	// gesture library to load gesture from SD
	private GestureLibrary gestureLib;
	
	// Context
	Context context = this;
	
	// Path to SD card
	String PATH = "";
	
	// Back button
	ImageView btnBack;
	
	ImageView btnNow;
	
	Button btnAddSong;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);
		
		
		// GESTURE
        // load gesture from SD card
        //PATH = Environment.getExternalStorageDirectory() +  "/gestures";
        //gestureLib = GestureLibraries.fromFile(PATH);
        
        // load from raw resource folder
        gestureLib = GestureLibraries.fromRawResource(context, R.raw.gestures);
        
        // if not load finish app
		if (!gestureLib.load()) 
		{
			finish();
		}
		
		// layout for playing gesture
        GestureOverlayView gestureOverlayView = (GestureOverlayView) findViewById(R.id.gesturesPL);
        gestureOverlayView.addOnGesturePerformedListener(this);
        // GESTURE
		
		
        
		Gvar = (GlobalVariable)getApplicationContext();
		

		mDB = new DBHelper(this);
		mDB.open();
			
		
		getData();
		
		
		array = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, namelist);
		
		setListAdapter(array);

		
		getListView().setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{

				// position = 0 => all songs list
				// position !=0 => other list
				
				if(position == 0)
				{
					// All songs list
					
					Intent intent = new Intent(getApplicationContext(), AllSongActivity.class);
					startActivity(intent);
				
					finish();	
				}
				else
				{
					// Other play list
					
					Plist p = mPlist.get(position);
					int idpl = p.id;
					Intent songselected = new Intent(getApplicationContext(), SongSelected.class);
					songselected.putExtra("idplist", idpl);
					songselected.putExtra("plistName", p.namePlaylist);
					startActivity(songselected);
					
					finish();
				}
			}
		});

		getListView().setOnItemLongClickListener(new OnItemLongClickListener() 
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				// position = 0 => all songs list
				// position !=0 => other list
				
				if(position == 0)
				{
					// All song  => do not modify => no action on All songs	
				}
				else
				{
					// Other play list
					
					Plist p = mPlist.get(position);
				    idpl = p.id;
				    nameplista = p.namePlaylist;
					
					final CharSequence[] items = { "Play", "Edit", "Delete" , "Cancel" };
					
					AlertDialog.Builder builder = new AlertDialog.Builder(Playlist.this);
	
							builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() 
							{	
								public void onClick(DialogInterface dialog, int item) 
								{
	
									switch (item) 
									{
										case 0:
											// Fixed playlist is null
											Gvar.GListSong = SongSelected.getSongsInPlist(getApplicationContext(), idpl);
											if(Gvar.GListSong.size() > 0)
											{
												Gvar.isReturnMain = true;
												Gvar.playListId = idpl;
												Gvar.currentSongInList = -1;
												if(Gvar.isPlaying == false)
												{
													Gvar.isPlaying = true;
												}
												
											}
											else 
											{
												Toast.makeText(getApplicationContext(), "The playlist is empty!!!", Toast.LENGTH_LONG).show();
											}
											finish();
											break;
										case 1:
											Intent edit = new Intent(getApplicationContext(),SongSelect.class);
											edit.putExtra("idlist", idpl);
											startActivity(edit);
											finish();
											break;
										case 2: 
											confirmdelete();	
											dialog.cancel();
											break;
										case 3:
											dialog.cancel();
											break;
									}
	
								}
							});
							
					AlertDialog alert = builder.create();
					alert.show();
				}
				
				return false;
				
			}
			
		});
		
		
		btnBack = (ImageView)findViewById(R.id.btnBackPList);
		
		btnBack.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(context, Library.class);
				startActivity(intent);
				finish();
				
			}
		});
		
		btnNow =(ImageView)findViewById(R.id.btnNowPlist);
		btnNow.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				finish();
				
			}
		});
		
		btnAddSong = (Button)findViewById(R.id.btnAddSongPlaylist);
		btnAddSong.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				addSong();
				
			}
		});

	}
	
	private void addSong() 
	{
		// testing
        AlertDialog.Builder alert = new AlertDialog.Builder(Playlist.this);

		alert.setMessage("New Playlist");

		// Set an EditText view to get user input
		final EditText input = new EditText(getApplicationContext());
		input.setHint("Title");
		alert.setView(input);

		
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{
				public void onClick(DialogInterface dialog, int whichButton) 
				{
						// do nothing
				}
		});

		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() 
		{
				public void onClick(DialogInterface dialog, int whichButton) 
				{
						String value = input.getText().toString();
						if(!value.equals(""))
						{
						  mDB.createPlaylist(value);
						
						  int id = getidplist(value);

						  Intent intent = new Intent(getApplicationContext(), SongSelect.class);
						  intent.putExtra("idlist", id);
						  startActivity(intent);
						
						  finish();
						}
						else 
						{
							Toast.makeText(context, "Please enter playlist name!",Toast.LENGTH_LONG).show();
						}
				}
		});

		
		alert.show();
        // testing
		
	}
	
	
	private void getData() 
	{
		mCursor = mDB.getPlaylist();
		
		try
		{
			mCursor.moveToFirst();
			
			while (!mCursor.isAfterLast()) 
			{
				int id = mCursor.getInt(0);
				String namePlaylist = mCursor.getString(1);
	
				Plist p = new Plist(id, namePlaylist);
				mPlist.add(p);
				namelist.add(namePlaylist);
				
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
	

	private int getidplist(String nameplist) 
	{
		int id = 0;
		
		mCursor = mDB.getidPlaylist(nameplist);
		
		try
		{
			mCursor.moveToFirst();
			
			while (!mCursor.isAfterLast()) 
			{
				id = mCursor.getInt(0);
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

		return id;
	}
	
	
	public void confirmdelete()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder( Playlist.this);

		alert.setMessage("Delete Playlist " + nameplista);

		// Set an EditText view to get user input
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
		{
					public void onClick(DialogInterface dialog, int whichButton) 
					{
						mDB.deletePlaylist(idpl);
						mPlist.clear();
						namelist.clear();
						getData();
						setListAdapter(array);
						//Log.i("da delete playlist roi nhe", idpl+"");
					}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{
					public void onClick(DialogInterface dialog, int whichButton) 
					{
                       
					}
		});
        
		alert.show();
	}

	
	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) 
	{
		ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
		
	    if (predictions.size() > 0 && predictions.get(0).score > 2.0) 
	    {
	        String action = predictions.get(0).name;
	        
	        if ("add".equals(action)) 
	        {
	     
	           addSong();
	            
	        }
	        else if("none".equals(action))
 	        {
	        	finish();
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
