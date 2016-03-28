package exam.tunemate2;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;

public class MusicStore extends Service implements Runnable
{

    // MP3 player
    // Current song position in a list
    // Song position not song id
    // ex: list.get(song position) # song id
    // int currentSong;

    // List of played songs
    // For not relap in play random
    ArrayList<Integer> playedList = new ArrayList<Integer>();

    // state of playing music store
    // N : none : play continue
    // S : shuffle : play random in list without overlap
    // R : repeat : repeat a songs for as many time as user want
    public static int NONE = 0;

    public static int SHUFFLE = 1;

    public static int REPEAT = 2;

    // mode for app
    public int mode = NONE;

    // Shareprefer to save state of App
    SharedPreferences sharepref;

    // Global variable
    GlobalVariable Gvar;

    // Intent for servie

    public static String ACTION_PLAYCURRENT = "exam.tunemate2.action.PLAY";

    public static String ACTION_PAUSE = "exam.tunemate2.action.PAUSE";

    public static String ACTION_STOP = "exam.tunemate2.action.STOP";

    public static String ACTION_NEXT = "exam.tunemate2.action.NEXT";

    public static String ACTION_RANDOM = "exam.tunemate2.action.RANDOM";

    public static String ACTION_PREVIOUS = "exam.tunemate2.action.PREVIOUS";

    public static String ACTION_RESTORE = "exam.tunemate2.action.RESTORE";

    // Constructor
    // Instance new Mp3 player and Share preference
    public MusicStore()
    {

	// super();
    }

    public MusicStore(Context context)
    {

    }

    @Override
    public void onCreate()
    {

	super.onCreate();
	sharepref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	Gvar = (GlobalVariable) this.getApplicationContext();

	Gvar.mediaPlayer = new MediaPlayer();
	Gvar.mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
	new Thread(this).start();
    }

    // Intent service from main activity
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

	String action = intent.getAction();

	if (action.equals(ACTION_PLAYCURRENT))
	    playCurrentSong(getApplicationContext(), Gvar.getGListSong(), Gvar.mode, Gvar.image, Gvar.seek);
	else if (action.equals(ACTION_RANDOM))
	    playRandom(getApplicationContext(), Gvar.getGListSong(), Gvar.mode, Gvar.image, Gvar.seek);
	else if (action.equals(ACTION_NEXT))
	    next(getApplicationContext(), Gvar.getGListSong(), Gvar.mode, Gvar.image, Gvar.seek);
	else if (action.equals(ACTION_PREVIOUS))
	    previous(getApplicationContext(), Gvar.getGListSong(), Gvar.mode, Gvar.image, Gvar.seek);
	else if (action.equals(ACTION_STOP))
	    stop();
	else if (action.equals(ACTION_PAUSE))
	    pause();
	else if (action.equals(ACTION_RESTORE))
	    restore(getApplicationContext(), Gvar.getGListSong(), Gvar.mode, Gvar.image, Gvar.seek);

	return START_NOT_STICKY;
    }

    // VUPH2 code to set album art
    // Get album art and show on ImageView
    public void setAlbumArt(Context context, ArrayList<Song> listSong, ImageView image)
    {

	String ImagePath = "";

	Cursor cursor = null;

	Song song = listSong.get(Gvar.currentSongInList);

	ContentResolver contentResolver = context.getContentResolver();

	Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

	Uri uri = ContentUris.withAppendedId(sArtworkUri, song.albumid);

	try
	{

	    cursor = contentResolver.query(uri, null, null, null, null);

	    cursor.moveToFirst();

	    do
	    {
		ImagePath = cursor.getString(1);
	    }
	    while (cursor.moveToNext());

	    Uri ImgUri = Uri.parse(ImagePath);
	    image.setImageURI(ImgUri);

	}
	catch (Exception e)
	{
	    // image.filePath = null;

	    Random rand = new Random();

	    int number = rand.nextInt(5);

	    if (number == 0)
	    {
		image.setImageResource(R.drawable.mu6);
	    }
	    else if (number == 1)
	    {
		image.setImageResource(R.drawable.mu2);
	    }
	    else if (number == 2)
	    {
		image.setImageResource(R.drawable.mu3);
	    }
	    else if (number == 3)
	    {
		image.setImageResource(R.drawable.mu4);
	    }
	    else if (number == 4)
	    {
		image.setImageResource(R.drawable.mu5);
	    }

	}
	finally
	{
	    if (cursor != null)
		cursor.close();
	}

    }

    // Get all songs in SD card and add to Listsongs
    public static ArrayList<Song> getMusics(Context context)
    {

	ArrayList<Song> ListSong = new ArrayList<Song>();
	Cursor cursor = null;

	try
	{
	    ContentResolver contentResolver = context.getContentResolver();

	    Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

	    String[] projection =
	    {
	    android.provider.MediaStore.Audio.Media.TITLE, android.provider.MediaStore.Audio.Media._ID, android.provider.MediaStore.Audio.Media.ALBUM_ID, android.provider.MediaStore.Audio.Media.DURATION
	    };

	    cursor = contentResolver.query(uri, projection, null, null, null);

	    int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
	    int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
	    int albumidColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM_ID);
	    int durationColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DURATION);

	    cursor.moveToFirst();

	    do
	    {
		long thisId = cursor.getLong(idColumn);
		String thisTitle = cursor.getString(titleColumn); // title album
		long albumid = cursor.getLong(albumidColumn);
		long duration = cursor.getLong(durationColumn);

		if (duration > 10000)
		{
		    Song song = new Song(thisId, thisTitle, albumid);
		    ListSong.add(song);
		}
		else
		{
		    Log.e("SONG NAME", thisTitle);
		}
	    }
	    while (cursor.moveToNext());

	}
	catch (Exception e)
	{

	}
	finally
	{
	    if (cursor != null)
		cursor.close();
	}

	return ListSong;

    }

    // Get all album
    public static ArrayList<String> getAlbum(Context context)
    {

	ArrayList<String> ListAlbum = new ArrayList<String>();

	String albumAll = "All songs";

	ListAlbum.add(albumAll);

	ContentResolver contentResolver = context.getContentResolver();

	Uri uri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

	String[] projection =
	{
	    android.provider.MediaStore.Audio.Albums.ALBUM
	};

	Cursor cursor = null;

	try
	{

	    cursor = contentResolver.query(uri, projection, null, null, null);

	    int album = cursor.getColumnIndex(android.provider.MediaStore.Audio.Albums.ALBUM);

	    cursor.moveToFirst();

	    do
	    {
		String salbum = cursor.getString(album);

		ListAlbum.add(salbum);
	    }
	    while (cursor.moveToNext());

	}
	catch (Exception e)
	{

	}
	finally
	{
	    if (cursor != null)
		cursor.close();
	}

	return ListAlbum;
    }

    // Get all play list
    public static ArrayList<String> getPlayList(Context context)
    {

	ArrayList<String> playListName = new ArrayList<String>();

	playListName.add("All songs");

	String[] projection =
	{
	MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME
	};

	ContentResolver contentResolver = context.getContentResolver();

	Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

	Cursor cursor = null;

	try
	{

	    cursor = contentResolver.query(uri, projection, null, null, null);

	    int plName = cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME);

	    cursor.moveToFirst();

	    do
	    {
		String Name = cursor.getString(plName);

		if (playListName.contains(Name))
		{
		    continue;
		}
		else
		{
		    playListName.add(Name);
		}

	    }
	    while (cursor.moveToNext());

	}
	catch (Exception e)
	{

	}
	finally
	{
	    if (cursor != null)
		cursor.close();
	}

	return playListName;
    }

    // Get all songs in a play list depend on play list name
    public static ArrayList<Song> getSongsFromPlaylist(Context context, String playlistName)
    {

	ArrayList<Song> ListSong = new ArrayList<Song>();

	Cursor cursor = null;

	String[] projection1 =
	{
	MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME
	};

	ContentResolver contentResolver = context.getContentResolver();

	Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

	try
	{

	    cursor = contentResolver.query(uri, projection1, MediaStore.Audio.Playlists.NAME + " = " + playlistName + "", null, null);

	    cursor.moveToFirst();

	    long playlist_id2 = cursor.getLong(0);

	    if (playlist_id2 > 0)
	    {
		String[] projection =
		{
		MediaStore.Audio.Playlists.Members.ARTIST, MediaStore.Audio.Playlists.Members.TITLE, MediaStore.Audio.Playlists.Members._ID

		};
		cursor = null;

		cursor = contentResolver.query(MediaStore.Audio.Playlists.Members.getContentUri("external", playlist_id2), projection, MediaStore.Audio.Media.IS_MUSIC + " != 0 ", null, null);

		if (cursor == null)
		{

		}
		else if (!cursor.moveToFirst())
		{

		}
		else
		{
		    int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE);
		    int idColumn = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members._ID);
		    int albumidColumn = cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM_ID);
		    do
		    {
			long thisId = cursor.getLong(idColumn);
			String thisTitle = cursor.getString(titleColumn); // title
									  // album
			long albumid = cursor.getLong(albumidColumn);
			Song song = new Song(thisId, thisTitle, albumid);
			ListSong.add(song);
		    }
		    while (cursor.moveToNext());
		}

	    }
	}
	catch (Exception e)
	{

	}
	finally
	{
	    if (cursor != null)
		cursor.close();
	}

	return ListSong;
    }

    // Gom 4 ham play current, play random, next, previous lai.
    // nhung ma chua lam
    public void playDependOnState(final Context context, final ArrayList<Song> listSong, final int state, String playMode)
    {

	if (playMode.equals("current"))
	{

	}
	else if (playMode.equals("random"))
	{

	}
	else if (playMode.equals("next"))
	{

	}
	else if (playMode.equals("previous"))
	{

	}

    }

    // Play current song in the list depend in current song id
    public void playCurrentSong(final Context context, final ArrayList<Song> listSong, final int state, final ImageView image, final SeekBar seekbar)
    {

	try
	{

	    // id of the song
	    long id = listSong.get(Gvar.currentSongInList).id;

	    // Moi them vo cho class song selected => bat button play
	    Gvar.currentSongInListInSongselected = Gvar.currentSongInList;

	    // Toast.makeText(context,
	    // listSong.get(Gvar.currentSongInList).title,
	    // Toast.LENGTH_LONG).show();

	    // Save the current song to share preference
	    SharedPreferences.Editor mPrefsEditor = sharepref.edit();
	    mPrefsEditor.putInt("NUMOFID", Gvar.currentSongInList);
	    mPrefsEditor.commit();

	    // Get Uri of the song to play
	    Uri contentUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

	    // Set music stream prepare for playing
	    Gvar.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

	    try
	    {
		Gvar.mediaPlayer.setDataSource(context.getApplicationContext(), contentUri);
		Gvar.mediaPlayer.prepare();
	    }
	    catch (Exception e)
	    {
		e.printStackTrace();

	    }

	    Gvar.mediaPlayer.start();

	    // title
	    Gvar.txtTitle.setText(Gvar.GListSong.get(Gvar.currentSongInList).title);
	    // title

	    // Set max value for seek bar here
	    // Because do not know the max value when finish 1 song
	    // and continue on setOnCompletionListener
	    seekbar.setMax(Gvar.mediaPlayer.getDuration());

	    new Thread(this).start();

	    // Get album art and display on the ImageView

	    // Fixed connect Sdcard 26-02-2012

	    setAlbumArt(context, listSong, image);

	    // When complete a song, depend on the MODE the next song will be
	    // chosen to be played
	    // May be play random, repeat or next normally
	    Gvar.mediaPlayer.setOnCompletionListener(new OnCompletionListener()
	    {

		@Override
		public void onCompletion(MediaPlayer mp)
		{

		    Gvar.mediaPlayer.stop();
		    Gvar.mediaPlayer.reset();

		    // new check if the state is SHUFF OR REPEAT
		    if (Gvar.mode == NONE)
		    {
			MusicStore.this.next(context, listSong, mode, image, seekbar);
		    }
		    else if (Gvar.mode == SHUFFLE)
		    {
			MusicStore.this.playRandom(context, listSong, mode, image, seekbar);
		    }
		    else if (Gvar.mode == REPEAT)
		    {
			MusicStore.this.playCurrentSong(context, listSong, mode, image, seekbar);
		    }

		}
	    });
	}
	catch (Exception e)
	{

	}

    }

    // Play random a song in the list
    public void playRandom(final Context context, final ArrayList<Song> listSong, final int state, final ImageView image, final SeekBar seekbar)
    {

	try
	{
	    if (listSong.isEmpty())
		return;

	    Random random = new Random(java.lang.System.currentTimeMillis());

	    int rand = random.nextInt(listSong.size());

	    // If this random number is in list => choose another one
	    while (playedList.contains(rand))
	    {
		rand = random.nextInt(listSong.size());
	    }

	    // Add to list of playED songs
	    playedList.add(rand);

	    // remove list if it full after a long time play random
	    if (playedList.size() == listSong.size())
	    {
		playedList.removeAll(playedList);
	    }

	    // id of the song
	    long id = listSong.get(rand).id;

	    // save current id song for next and previous
	    Gvar.currentSongInList = rand;

	    // Moi them vo cho class song selected => bat button play
	    Gvar.currentSongInListInSongselected = Gvar.currentSongInList;

	    // Toast.makeText(context,
	    // listSong.get(Gvar.currentSongInList).title,
	    // Toast.LENGTH_LONG).show();

	    // Save the current song to share preference
	    SharedPreferences.Editor mPrefsEditor = sharepref.edit();
	    mPrefsEditor.putInt("NUMOFID", Gvar.currentSongInList);
	    mPrefsEditor.commit();

	    // Get Uri of the song to play
	    Uri contentUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

	    // Set music stream prepare for playing
	    Gvar.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

	    try
	    {
		Gvar.mediaPlayer.setDataSource(context.getApplicationContext(), contentUri);
		Gvar.mediaPlayer.prepare();
	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	    Gvar.mediaPlayer.start();

	    // title
	    Gvar.txtTitle.setText(Gvar.GListSong.get(Gvar.currentSongInList).title);
	    // title

	    // Set max value for seek bar here
	    // Because do not know the max value when finish 1 song
	    // and continue on setOnCompletionListener
	    seekbar.setMax(Gvar.mediaPlayer.getDuration());
	    new Thread(this).start();

	    // Get album art and display on the ImageView

	    setAlbumArt(context, listSong, image);

	    // When complete a song, depend on the MODE the next song will be
	    // chosen to be played
	    // May be play random, repeat or next normally
	    Gvar.mediaPlayer.setOnCompletionListener(new OnCompletionListener()
	    {

		@Override
		public void onCompletion(MediaPlayer mp)
		{

		    // new check if the state is SHUFF OR REPEAT

		    Gvar.mediaPlayer.stop();
		    Gvar.mediaPlayer.reset();

		    if (Gvar.mode == NONE)
		    {
			MusicStore.this.next(context, listSong, mode, image, seekbar);
		    }
		    else if (Gvar.mode == SHUFFLE)
		    {
			MusicStore.this.playRandom(context, listSong, mode, image, seekbar);
		    }
		    else if (Gvar.mode == REPEAT)
		    {
			MusicStore.this.playCurrentSong(context, listSong, mode, image, seekbar);
		    }

		}
	    });

	}
	catch (Exception e)
	{

	}

    }

    // Play the next song in the list
    public void next(final Context context, final ArrayList<Song> listSong, final int state, final ImageView image, final SeekBar seekbar)
    {

	try
	{
	    // Get the next song in list
	    final int maxSong = listSong.size() - 1;

	    // next song = current + 1
	    Gvar.currentSongInList = Gvar.currentSongInList + 1;

	    // If Current song > max => current song = 0
	    if (Gvar.currentSongInList > maxSong)
	    {
		Gvar.currentSongInList = 0;
	    }

	    // Moi them vo cho class song selected => bat button play
	    Gvar.currentSongInListInSongselected = Gvar.currentSongInList;

	    // Save the current song to share preference
	    SharedPreferences.Editor mPrefsEditor = sharepref.edit();
	    mPrefsEditor.putInt("NUMOFID", Gvar.currentSongInList);
	    mPrefsEditor.commit();

	    // id of the song
	    long id = listSong.get(Gvar.currentSongInList).id;

	    // Toast.makeText(context,
	    // listSong.get(Gvar.currentSongInList).title,
	    // Toast.LENGTH_LONG).show();

	    // Get Uri of the song to play
	    Uri contentUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

	    // Set music stream prepare for playing
	    Gvar.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

	    try
	    {
		Gvar.mediaPlayer.setDataSource(context.getApplicationContext(), contentUri);
		Gvar.mediaPlayer.prepare();
	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	    Gvar.mediaPlayer.start();

	    // title
	    Gvar.txtTitle.setText(Gvar.GListSong.get(Gvar.currentSongInList).title);
	    // title

	    // Set max value for seek bar here
	    // Because do not know the max value when finish 1 song
	    // and continue on setOnCompletionListener
	    seekbar.setMax(Gvar.mediaPlayer.getDuration());

	    new Thread(this).start();
	    // Get album art and display on the ImageView

	    // Fixed connect Sdcard 26-02-2012

	    setAlbumArt(context, listSong, image);

	    // When complete a song, depend on the MODE the next song will be
	    // chosen to be played
	    // May be play random, repeat or next normally
	    Gvar.mediaPlayer.setOnCompletionListener(new OnCompletionListener()
	    {

		@Override
		public void onCompletion(MediaPlayer mp)
		{

		    Gvar.mediaPlayer.stop();
		    Gvar.mediaPlayer.reset();

		    if (Gvar.mode == NONE)
		    {
			MusicStore.this.next(context, listSong, mode, image, seekbar);
		    }
		    else if (Gvar.mode == SHUFFLE)
		    {
			MusicStore.this.playRandom(context, listSong, mode, image, seekbar);
		    }
		    else if (Gvar.mode == REPEAT)
		    {
			MusicStore.this.playCurrentSong(context, listSong, mode, image, seekbar);
		    }

		}
	    });
	}
	catch (Exception e)
	{

	}

    }

    // Play the previous song in the list
    public void previous(final Context context, final ArrayList<Song> listSong, final int state, final ImageView image, final SeekBar seekbar)
    {

	try
	{
	    // Get the previous song position in list
	    // current song = 3 => previous song = 3 - 1
	    Gvar.currentSongInList = Gvar.currentSongInList - 1;

	    // If previous song = -1 => current = 0 => previous = last song in
	    // list
	    if (Gvar.currentSongInList < 0)
	    {
		Gvar.currentSongInList = listSong.size() - 1;
	    }

	    // Moi them vo cho class song selected => bat button play
	    Gvar.currentSongInListInSongselected = Gvar.currentSongInList;

	    // Save the current song to share preference
	    SharedPreferences.Editor mPrefsEditor = sharepref.edit();
	    mPrefsEditor.putInt("NUMOFID", Gvar.currentSongInList);
	    mPrefsEditor.commit();

	    // id of the song
	    long id = listSong.get(Gvar.currentSongInList).id;

	    // Toast.makeText(context,
	    // listSong.get(Gvar.currentSongInList).title,
	    // Toast.LENGTH_LONG).show();

	    // Get Uri of the song to play
	    Uri contentUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

	    // Set music stream prepare for playing
	    Gvar.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

	    try
	    {
		Gvar.mediaPlayer.setDataSource(context.getApplicationContext(), contentUri);
		Gvar.mediaPlayer.prepare();
	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }

	    Gvar.mediaPlayer.start();

	    // title
	    Gvar.txtTitle.setText(Gvar.GListSong.get(Gvar.currentSongInList).title);
	    // title

	    // Set max value for seek bar here
	    // Because do not know the max value when finish 1 song
	    // and continue on setOnCompletionListener
	    seekbar.setMax(Gvar.mediaPlayer.getDuration());
	    new Thread(this).start();

	    // Get album art and display on the ImageView
	    // Fixed connect Sdcard 26-02-2012

	    setAlbumArt(context, listSong, image);

	    // When complete a song, depend on the MODE the next song will be
	    // chosen to be played
	    // May be play random, repeat or next normally
	    Gvar.mediaPlayer.setOnCompletionListener(new OnCompletionListener()
	    {

		@Override
		public void onCompletion(MediaPlayer mp)
		{

		    Gvar.mediaPlayer.stop();
		    Gvar.mediaPlayer.reset();

		    if (Gvar.mode == NONE)
		    {
			MusicStore.this.next(context, listSong, mode, image, seekbar);
		    }
		    else if (Gvar.mode == SHUFFLE)
		    {
			MusicStore.this.playRandom(context, listSong, mode, image, seekbar);
		    }
		    else if (Gvar.mode == REPEAT)
		    {
			MusicStore.this.playCurrentSong(context, listSong, mode, image, seekbar);
		    }

		}
	    });
	}
	catch (Exception E)
	{

	}
    }

    // Pause
    public void pause()
    {

	Gvar.mediaPlayer.pause();

    }

    // restore
    public void restore(final Context context, final ArrayList<Song> listSong, final int state, final ImageView image, final SeekBar seekbar)
    {

	try
	{
	    boolean isCatch = false;
	    // id of the song
	    long id = 0;
	    try
	    {
		id = listSong.get(Gvar.currentSongInList).id;
	    }
	    catch (Exception e)
	    {
		isCatch = true;
		SharedPreferences.Editor mPrefsEditor = sharepref.edit();
		mPrefsEditor.putString("LISTMODE", "playlist");
		mPrefsEditor.putInt("PLAYLISTID", 0);
		mPrefsEditor.putInt("NUMOFID", 0);
		mPrefsEditor.putInt("CURRENTSONGSELECTED", 0);
		mPrefsEditor.commit();
	    }

	    // Moi them vo cho class song selected => bat button play
	    Gvar.currentSongInListInSongselected = Gvar.currentSongInList;

	    // Toast.makeText(context,
	    // listSong.get(Gvar.currentSongInList).title,
	    // Toast.LENGTH_LONG).show();

	    if (isCatch == false)
	    {
		// Save the current song to share preference
		SharedPreferences.Editor mPrefsEditor = sharepref.edit();
		mPrefsEditor.putInt("NUMOFID", Gvar.currentSongInList);
		mPrefsEditor.commit();
	    }

	    // Get Uri of the song to play
	    Uri contentUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

	    // Set music stream prepare for playing
	    Gvar.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

	    try
	    {
		Gvar.mediaPlayer.setDataSource(context.getApplicationContext(), contentUri);
		Gvar.mediaPlayer.prepare();
	    }
	    catch (Exception e)
	    {

	    }

	    // Gvar.mediaPlayer.start();

	    // title
	    Gvar.txtTitle.setText(Gvar.GListSong.get(Gvar.currentSongInList).title);
	    // title

	    // Set max value for seek bar here
	    // Because do not know the max value when finish 1 song
	    // and continue on setOnCompletionListener
	    seekbar.setMax(Gvar.mediaPlayer.getDuration());

	    new Thread(this).start();

	    // Get album art and display on the ImageView
	    // Fixed connect Sdcard 26-02-2012

	    setAlbumArt(context, listSong, image);

	    // When complete a song, depend on the MODE the next song will be
	    // chosen to be played
	    // May be play random, repeat or next normally
	    Gvar.mediaPlayer.setOnCompletionListener(new OnCompletionListener()
	    {

		@Override
		public void onCompletion(MediaPlayer mp)
		{

		    Gvar.mediaPlayer.stop();
		    Gvar.mediaPlayer.reset();

		    // new check if the state is SHUFF OR REPEAT
		    if (Gvar.mode == NONE)
		    {
			MusicStore.this.next(context, listSong, mode, image, seekbar);
		    }
		    else if (Gvar.mode == SHUFFLE)
		    {
			MusicStore.this.playRandom(context, listSong, mode, image, seekbar);
		    }
		    else if (Gvar.mode == REPEAT)
		    {
			MusicStore.this.playCurrentSong(context, listSong, mode, image, seekbar);
		    }

		}
	    });
	}
	catch (Exception e)
	{

	}
    }

    // Play
    public void play()
    {

	Gvar.mediaPlayer.start();

    }

    // Stop
    public void stop()
    {

	Gvar.mediaPlayer.stop();
	Gvar.mediaPlayer.reset();
    }

    // Release memory
    public void release()
    {

	if (Gvar.mediaPlayer != null)
	{
	    Gvar.mediaPlayer.release();
	}
    }

    @Override
    public IBinder onBind(Intent intent)
    {

	return null;
    }

    @Override
    public void run()
    {

	int CurrentPosition = 0;

	int total = Gvar.mediaPlayer.getDuration();

	while (CurrentPosition < total)
	{
	    try
	    {
		Thread.sleep(1000);
		CurrentPosition = Gvar.mediaPlayer.getCurrentPosition();
	    }
	    catch (InterruptedException e)
	    {
		return;
	    }
	    catch (Exception e)
	    {
		return;
	    }
	    Gvar.seek.setProgress(CurrentPosition);
	    Message mess = new Message();
	    Bundle bund = new Bundle();
	    bund.putInt("time", CurrentPosition);
	    mess.setData(bund);
	    handler.sendMessage(mess);
	}

    }

    private Handler handler = new Handler()
    {

	@Override
	public void handleMessage(Message msg)
	{

	    int time = msg.getData().getInt("time");

	    long minute = TimeUnit.MILLISECONDS.toSeconds(time) / 60;
	    long second = TimeUnit.MILLISECONDS.toSeconds(time) % 60;
	    String strTime = String.format("%02d : %02d", minute, second);
	    Gvar.txtTime.setText(strTime);

	}
    };

}
