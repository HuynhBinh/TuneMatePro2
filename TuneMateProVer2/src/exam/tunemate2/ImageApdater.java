package exam.tunemate2;


import java.util.ArrayList;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;


public class ImageApdater extends BaseAdapter 
{
    int mGalleryItemBackground;
    Context mContext; 
    
    
  
    private ArrayList<String> mImageIds = new ArrayList<String>();
   
    public ImageApdater(Context c) 
    {
        mContext = c;
        getAlbum();
            
        TypedArray attr = mContext.obtainStyledAttributes(R.styleable.HelloGallery);
        mGalleryItemBackground = attr.getResourceId(R.styleable.HelloGallery_android_galleryItemBackground, 0);
        attr.recycle();
        
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
    	ImageView imageView;
    	 Matrix matrix = new Matrix();
	        matrix.preScale(1, 1);
    	
    	if(convertView ==  null)
    	{
    		imageView = new ImageView(mContext);
    		imageView.setLayoutParams(new Gallery.LayoutParams(200, 300));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            
           // imageView.setBackgroundResource(mGalleryItemBackground);

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
        	//bmImg.recycle();
        }
        else
        {
        	imageView.setImageResource(R.drawable.btnalbum);
        }
        

        return imageView;
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
			cursor = null;
		}
		
		return albumNames;
	}
	
}