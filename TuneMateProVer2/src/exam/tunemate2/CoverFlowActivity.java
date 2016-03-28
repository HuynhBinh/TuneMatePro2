package exam.tunemate2;



import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class CoverFlowActivity extends Activity 
{
	   /** Called when the activity is first created. */
		private ArrayList<String> mImageIds = new ArrayList<String>();
		ArrayList<AlbumInfo> AlbumNames ;
		TextView namealbum;
	    @Override
	    public void onCreate(Bundle savedInstanceState) 
	    {
	     super.onCreate(savedInstanceState);
	     setContentView(R.layout.coverflow);
	
	     AlbumNames = new ArrayList<AlbumInfo>();
	     AlbumNames = getAlbum();
	     namealbum = (TextView) findViewById(R.id.textView1);
	     CoverFlow coverFlow = (CoverFlow) findViewById(R.id.coverflow);
	     
	     coverFlow.setAdapter(new ImageAdapter(this));

	     
	     coverFlow.setSpacing(-25);
	 
	     coverFlow.setAnimationDuration(1000);         
	    
	     
	     coverFlow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				
				Intent intent = new Intent(getApplicationContext(), SongInAlbumGLR.class);
				intent.putExtra("albumID", AlbumNames.get(position).Id);
				intent.putExtra("albumName", AlbumNames.get(position).name );
				startActivity(intent);
				
				finish();
			}
	    	 
		});
	     
	     coverFlow.setOnItemSelectedListener(new OnItemSelectedListener() 
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
	    
	    

	 public class ImageAdapter extends BaseAdapter 
	 {
	 
	     private Context mContext;
	 
	     
	     public ImageAdapter(Context c) 
	     {
	    	 mContext = c;
	    	 //getAlbum();
	     }
	
	     
	     public int getCount() {
	         return mImageIds.size();
	     }

	     public Object getItem(int position) {
	         return position;
	     }

	     public long getItemId(int position) {
	         return position;
	     }

	     public View getView(int position, View convertView, ViewGroup parent) {

	    	 
	    	 
	    	 ImageView imageView;
	     	
	     	if(convertView ==null)
	     	{
	     		 imageView = new ImageView(mContext);
	     		 imageView.setLayoutParams(new CoverFlow.LayoutParams(150, 200));
	             imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE); 
	     	   
	     	}
	     	else
	     	{
	     		imageView = (ImageView) convertView;
	     	}
	     	
	         if(!mImageIds.get(position).equals("noImage"))
	         {
	         	
	       
	         	Bitmap bmImg = BitmapFactory.decodeFile(mImageIds.get(position));
	        	Bitmap bmImgScale =  Bitmap.createScaledBitmap(bmImg, 180, 140, false);
	         	imageView.setImageBitmap(bmImgScale);
	          
	         }
	         else
	         {
	         	imageView.setImageResource(R.drawable.btnalbum);
	         }
	        
	    	
	     
	         return imageView;
	      
	    
	     }
	   /** Returns the size (0.0f to 1.0f) of the views 
	      * depending on the 'offset' to the center. */ 
	      public float getScale(boolean focused, int offset) { 
	        /* Formula: 1 / (2 ^ offset) */ 
	          return Math.max(0, 1.0f / (float)Math.pow(2, Math.abs(offset))); 
	      } 

	 }
	 
	  public  ArrayList<AlbumInfo> getAlbum()
		{
			ContentResolver contentResolver = this.getContentResolver();
		
			ArrayList<AlbumInfo> albumNames = new ArrayList<AlbumInfo>();
			
			Uri uri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
			
			String[] projection = { 
									android.provider.MediaStore.Audio.Albums.ALBUM ,
									android.provider.MediaStore.Audio.Albums._ID ,
									android.provider.MediaStore.Audio.Albums.ALBUM_ART};
			
			Cursor cursor = contentResolver.query(uri, projection , null, null, null);
			
					
			int index = cursor.getColumnIndex(android.provider.MediaStore.Audio.Albums.ALBUM);
			int IdIndex = cursor.getColumnIndex(android.provider.MediaStore.Audio.Albums._ID);
			int artIndex = cursor.getColumnIndex(android.provider.MediaStore.Audio.Albums.ALBUM_ART);
			
			try
			{
				cursor.moveToFirst();
							
				do 
			    {
					String ImagePath = "";
					String name = cursor.getString(index);
					long id = cursor.getLong(IdIndex);
					ImagePath = cursor.getString(artIndex);
								
					AlbumInfo albumInfo = new AlbumInfo(name, id);
											
					if(ImagePath == null)
					{
						mImageIds.add("noImage");
					}
					else
					{
						mImageIds.add(ImagePath);
					}
					
					
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
				
			 }
			 else if (getResources().getConfiguration().orientation ==  Configuration.ORIENTATION_PORTRAIT) 
			 {
				 
				 Intent intent = new Intent(getApplicationContext(), GridViewActivity.class);
				 startActivity(intent);
				 finish(); 
				 
			 }
			super.onConfigurationChanged(newConfig);
		}
}