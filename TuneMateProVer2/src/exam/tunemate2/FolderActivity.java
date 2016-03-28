package exam.tunemate2;

import java.util.ArrayList;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

public class FolderActivity extends ListActivity 
{
	public Context context;
	
	ArrayList<String> folderName;
	
	ArrayAdapter<String> array;
	
	ImageView btnBack;		
	ImageView btnNow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.folderactivity);
		
		context = getApplicationContext();
		
		folderName = new ArrayList<String>();
		
		getFolder();
		
		array = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, folderName);
		
		setListAdapter(array);
		
		getListView().setOnItemClickListener(new OnItemClickListener() 
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				
				Intent intent = new Intent(context, SongsInFolder.class);
				intent.putExtra("folder",  folderName.get(position));
				startActivity(intent);
				finish();
			}
		});
		
		
		btnBack = (ImageView)findViewById(R.id.btnBackFolderActivity);
		btnBack.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				// intent to list
 	        	
 	        	Intent intent = new Intent(getApplicationContext(), Library.class);
				startActivity(intent);
				finish();
				
			}
		});
		
		btnNow =(ImageView)findViewById(R.id.btnNowFolderActivity);
		btnNow.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				finish();
				
			}
		});
		
		
	}
	
	
	public void getFolder()
	{
		ContentResolver contentResolver = context.getContentResolver();
			
		Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		
		String[] projection = {android.provider.MediaStore.Audio.Media.DATA};
		
		Cursor cursor = contentResolver.query(uri, projection, null, null, null);
		
		int index = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.DATA);
	
		try
		{
			cursor.moveToFirst();
						
			do 
		    {
				
				String fullPath = cursor.getString(index);
				
				String folder = Ultils.getFolderName(Ultils.getDirectory(fullPath));
				
				if (folder.equals("")) 
				{
					folder = "sdcard";		
				}
				
				if(!folderName.contains(folder))
				{				
					folderName.add(folder);
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
		
	}

}
