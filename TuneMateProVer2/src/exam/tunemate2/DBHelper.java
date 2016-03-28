package exam.tunemate2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;




public class DBHelper 
{

    public static final String TAG ="DBAdapter";	
    public static final String _ID = "_id";
    public static final String _IDSONG = "_idsong";
    public static final String _IDPLAYLIST = "idplaylist";
	public static final String TITLE = "title";
	public static final String NAMEPLAYLIST = "nameplaylist";
    public static final String ALBUMID = "albumid" ;
   
	 
	
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDB;
	
	
	private static final String DATABASE_CREATE = "create table PlaylistTB (_id integer primary key autoincrement ,nameplaylist TEXT not null)";
	private static final String DATABASE_CREATE1 = "create table PlaylistSongTB (_idsong integer,title TEXT,idplaylist integer, albumid integer, PRIMARY KEY (_idsong,idplaylist))";
	private static final String DATABASE_NAME =  "PlaylistDB";
	private static final String DATABASE_TABLE = "PlaylistTB";
	private static final String DATABASE_TABLE1 = "PlaylistSongTB";
	private static final int DATABASE_VERSION = 2;
	
	private final Context mContext;

	
	private static class DatabaseHelper extends SQLiteOpenHelper
	{

		public DatabaseHelper(Context context, String name, CursorFactory factory, int version) 
		{
			super(context, name, factory, version);

		}

		@Override
		public void onCreate(SQLiteDatabase db) 
		{

			db.execSQL(DATABASE_CREATE);
			db.execSQL(DATABASE_CREATE1);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		{

			//Log.i(TAG, "Upgrading DB");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE1);
			onCreate(db);
			
		}
	}
	
	public DBHelper(Context ctx)
	{
		this.mContext = ctx;
	}
	
	public DBHelper open()
	{
		mDbHelper = new DatabaseHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION);
		mDB = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close()
	{
		mDbHelper.close();
	}
	
	public long createPlaylist(String nameplaylist)
	{
		ContentValues inititalValues = new ContentValues();
		
		inititalValues.put(NAMEPLAYLIST, nameplaylist);	
		
		return mDB.insert(DATABASE_TABLE, null, inititalValues);
	}
	
	public long createPlaylistSong(int idsong,String title,int idplaylist, int albumid)
	{
		ContentValues inititalValues = new ContentValues();
		
		inititalValues.put(_IDSONG, idsong);
		inititalValues.put(TITLE, title);
		inititalValues.put(_IDPLAYLIST, idplaylist);
        inititalValues.put(ALBUMID, albumid);
		
		return mDB.insert(DATABASE_TABLE1, null, inititalValues);
	}
	
	public boolean deleteSong(int idsong)
	{
		return mDB.delete(DATABASE_TABLE1,_IDSONG + "= '" + idsong + "'", null) > 0;
		
	}
	
	public boolean deletePlaylist( int idplaylist)
	{
		 mDB.delete(DATABASE_TABLE1,_IDPLAYLIST + "=" + idplaylist , null);
		return mDB.delete(DATABASE_TABLE,_ID + "=" + idplaylist , null) >0;
		
	}
	
	public boolean deleteSongnotexist(int idsong, int idplist)
	{
		return mDB.delete(DATABASE_TABLE1,_IDSONG + "= '" + idsong + "' AND " + _IDPLAYLIST +"='" + idplist +"'"  , null) >0;
		
	} 
	
	
	public Cursor getPlaylist()
	{
		return mDB.rawQuery("SELECT * FROM PlaylistTB ", null);
	}
	
	public Cursor getidPlaylist(String nameplaylist)
	{
		return mDB.rawQuery("SELECT _id FROM PlaylistTB WHERE nameplaylist = '"+ nameplaylist.replace("'", "''") + "'", null);
	}
	 
	
	public Cursor getPlaylistSong(int idplaylist)
	{
		return mDB.rawQuery("SELECT * FROM PlaylistSongTB WHERE idplaylist = " + idplaylist, null);
	}
	
	
	
}
