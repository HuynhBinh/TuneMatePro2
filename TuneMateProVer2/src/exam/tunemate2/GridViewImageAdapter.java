package exam.tunemate2;

import java.util.ArrayList;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridViewImageAdapter extends BaseAdapter 
{

	int mGalleryItemBackground;
    Context mContext; 
    ArrayList<AlbumInfo> albums;
    
    
  
    private ArrayList<String> mImageIds = new ArrayList<String>();
   
    public GridViewImageAdapter(Context c) 
    {
        mContext = c;
        albums = getAlbum();
                   
    }
    
    public ArrayList<AlbumInfo> getAlbumName()
    {
    	return getAlbum();
    }

   
    public int getCount() 
    {
        return mImageIds.size();
    }

    public Object getItem(int position) 
    {
        return position;
    }

    public long getItemId(int position) 
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) 
    {   	
    	   View grid;
    	    
    	   if(convertView==null)
    	   {
	    	    grid = new View(mContext);
	    	    LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	    grid = inflater.inflate(R.layout.griditem, parent, false);
    	    
    	   }
    	   else
    	   {
    		   grid = (View)convertView;
    	   }
    	   
    	   ImageView imageView = (ImageView)grid.findViewById(R.id.imagepart);
    	   TextView textView = (TextView)grid.findViewById(R.id.textpart);
    	   
           imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
           imageView.setPadding(8, 8, 8, 8);
           textView.setPadding(8,8,8,8);

    	          
           
        if(!mImageIds.get(position).equals("noImage"))
        {
        	Bitmap bmImg = BitmapFactory.decodeFile(mImageIds.get(position));
        	imageView.setImageBitmap(bmImg);
        	textView.setText(albums.get(position).name);
        }
        else
        {
        	imageView.setImageResource(R.drawable.btnalbum);
        	textView.setText(albums.get(position).name);
        }
        
        return grid;
    }

    
    public  ArrayList<AlbumInfo> getAlbum()
	{
		ContentResolver contentResolver = mContext.getContentResolver();
	
		ArrayList<AlbumInfo> albumNames = new ArrayList<AlbumInfo>();
		
		Uri uri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
		
		String[] projection = { 
								android.provider.MediaStore.Audio.Albums.ALBUM ,
								android.provider.MediaStore.Audio.Albums._ID ,
								android.provider.MediaStore.Audio.Albums.ALBUM_ART };
		
		Cursor cursor = contentResolver.query(uri, projection, null, null, null);
		
				
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
}
