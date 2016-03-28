package exam.tunemate2;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class TuneMateProVer2 extends Activity implements OnGesturePerformedListener, Shaker.Callback, Runnable
{

    // Variable

    // coordinate for up and down event
    private Coordinate downCoor;

    private Coordinate upCoor;

    // layout inside the gesture overlay
    LinearLayout mainlayout;

    // gesture library to load gesture from SD
    private GestureLibrary gestureLib;

    // is play or not
    // private boolean isPlaying;

    // App context
    Context context = this;

    // List for to be played songs
    private ArrayList<Song> ListSong;

    // For incre and decre volume
    private AudioFactory audioFactory;

    // Music store for play stop pause random continue
    // private MusicStore musicStore;

    // Shareprefer to save state of App
    private SharedPreferences mPrefs;

    // Number use of app use for the first time in Shareprefer
    private int mNumOfUseApp = 0;

    // PLAY LIST ID
    private int mPlaylistID = 0;

    // Number of current songs saved in Shareprefer
    private int mNumOfSongid = 0;

    // saved mode in Shareprefer
    private int mSavedMode = NONE;

    // Path to SD
    public String PATH = "";

    // state of playing music store
    // N : none : play continue
    // S : shuffle : play random in list without overlap
    // R : repeat : repeat a songs for as many time as user want
    private static int NONE = 0;

    private static int SHUFFLE = 1;

    private static int REPEAT = 2;

    // mode for app
    private int mode = NONE;

    // distance bt two coordinate
    public int diff = 0;

    // listen for sensor change (shake)
    // private SensorManager mSensorManager;
    // private ShakeListener mSensorListener;
    // private ShakeEvent mSensorListener;

    // Session variable
    GlobalVariable Gvar;

    // Progress bar
    SeekBar seekbar;

    // Volume bar
    ProgressBar volumeBar;

    // Database control
    private DBHelper Database;

    // ImageView to show Album art
    // ImageView imageView;

    // for shake
    // public Shaker shaker = null;

    // current time for volume bar
    long volumeTime = 0;

    // gif View
    public ImageView imageView;

    // Image view for button show to know what mode is playing
    ImageView imageBtnView;

    // Image Mode
    ImageView imgMode;

    // Image list
    ImageView imgList;

    //
    public int currentInListInSongSelected = 0;

    //
    ImageView imgSetting;

    //
    ArrayList<Song> listSong1 = new ArrayList<Song>();

    // txt Time

    // List mode
    String listMode = "";

    // Folder name
    String folderName = "";

    // Variable

    // Run when back from other activity
    @Override
    protected void onResume()
    {

	// Log.i("Vao Resume", "Vao Resume");
	// Check if user press play or not
	if (Gvar.isReturnMain == true)
	{
	    // Get list song from Playlist.java
	    ListSong = Gvar.GListSong;

	    if (ListSong.size() != 0)
	    {
		startService(new Intent(MusicStore.ACTION_STOP));

		Gvar.setGListSong(ListSong);
		Gvar.image = imageView;
		Gvar.seek = seekbar;

		startService(new Intent(MusicStore.ACTION_NEXT));

	    }
	}

	Gvar.isReturnMain = false;
	super.onResume();
    }

    // Override button Back like button Home
    // When press Back not finish() Activity
    // Tourial and Gesture
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

	if (keyCode == KeyEvent.KEYCODE_HOME)
	{

	}
	else if (keyCode == KeyEvent.KEYCODE_SEARCH)
	{
	    // Intent intent = new Intent(context, Galery.class);
	    // startActivity(intent);
	    Intent intent = new Intent(context, CoverFlowActivity.class);
	    startActivity(intent);
	}
	else if (keyCode == KeyEvent.KEYCODE_MENU)
	{
	    // Intent intent = new Intent(context, Library.class);
	    // startActivity(intent);

	    Intent i = new Intent(context, FolderActivity.class);
	    startActivity(i);

	}
	return super.onKeyDown(keyCode, event);
    }

    // Main activity
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

	// re move title bar.
	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	// Remove notification bar
	// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	// WindowManager.LayoutParams.FLAG_FULLSCREEN);

	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);

	// Log.i("Vao Create", "Vao Create");

	// get global variable of the app
	Gvar = (GlobalVariable) this.getApplicationContext();
	// De isplaying o day = true co 2 ly do
	// 1. Phu` hop voi nut play va pause khi moi' vao
	// 2. Ko bi FC khi co cuoic goi den
	Gvar.isPlaying = true;

	// image view for album art
	imageView = (ImageView) findViewById(R.id.imageView);

	// Image view for button
	imageBtnView = (ImageView) findViewById(R.id.ViewBtn);

	// txt title
	Gvar.txtTitle = (TextView) findViewById(R.id.txtTitle);

	// Image mode
	imgMode = (ImageView) findViewById(R.id.imgMode);

	// Time
	Gvar.txtTime = (TextView) findViewById(R.id.txtTime);

	// Image list
	imgList = (ImageView) findViewById(R.id.imgList);

	// Image setting
	imgSetting = (ImageView) findViewById(R.id.imgSetting);

	// gif view for album art
	// gifView = (GifView) findViewById(R.id.gifView);
	// gifView.setGif(R.drawable.music);

	// DB store playlist
	Database = new DBHelper(context);
	Database.open();

	// thread = new Thread((Runnable) context);
	// main layout for coordinate target
	mainlayout = (LinearLayout) findViewById(R.id.main);

	// save instance state
	mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

	// GESTURE
	// load gesture from SD card
	PATH = Environment.getExternalStorageDirectory() + "/gestures";
	// gestureLib = GestureLibraries.fromFile(PATH);

	// load from raw resource folder
	gestureLib = GestureLibraries.fromRawResource(context, R.raw.gestures);

	// if not load finish app
	if (!gestureLib.load())
	{
	    finish();
	}

	// layout for playing gesture
	GestureOverlayView gestureOverlayView = (GestureOverlayView) findViewById(R.id.gestures);
	gestureOverlayView.addOnGesturePerformedListener(this);
	// GESTURE

	// mode for play and stop

	// audio increment and decrement
	audioFactory = new AudioFactory(context);

	volumeBar = (ProgressBar) findViewById(R.id.seekBarVolume);
	volumeBar.setMax(audioFactory.getMaxVolume());
	volumeBar.setVisibility(ProgressBar.GONE);

	// get value from share preference to setting
	mNumOfUseApp = mPrefs.getInt("NUMOFUSE", 0);
	mNumOfSongid = mPrefs.getInt("NUMOFID", 0);
	mSavedMode = mPrefs.getInt("MODE", 0);
	mPlaylistID = mPrefs.getInt("PLAYLISTID", 0);
	currentInListInSongSelected = mPrefs.getInt("CURRENTSONGSELECTED", 0);
	listMode = mPrefs.getString("LISTMODE", "");
	Gvar.ListMode = listMode;

	// new
	folderName = mPrefs.getString("FOLDERNAME", "");
	Gvar.folderName = folderName;
	//

	if (mSavedMode == SHUFFLE)
	{
	    // img mode
	    imgMode.setImageResource(R.drawable.imgshuffle);
	    // img mode
	}
	else if (mSavedMode == REPEAT)
	{
	    // img mode
	    imgMode.setImageResource(R.drawable.imgrepeat);
	    // img mode
	}
	else if (mSavedMode == NONE)
	{
	    // img mode
	    imgMode.setImageResource(R.drawable.imgnone);
	    // img mode
	}

	if (mNumOfUseApp == 0)
	{
	    // create all song in list
	    Database.createPlaylist("All songs");
	    ListSong = MusicStore.getMusics(context);
	}
	else if (mNumOfUseApp != 0)
	{
	    if (Gvar.ListMode.equals("playlist"))
	    {
		// if playlist id == 0 get all song
		// else SongSelected.getSongsInPlist(context, mPlaylistID);
		//
		if (mPlaylistID == 0)
		{
		    ListSong = MusicStore.getMusics(context);
		}
		else
		{

		    // ListSong = SongSelected.getSongsInPlist(context,
		    // mPlaylistID);
		    boolean flag = false;

		    ListSong = SongSelected.getSongsInPlist(context, mPlaylistID);

		    listSong1 = MusicStore.getMusics(getApplicationContext());

		    for (Song song : ListSong)
		    {
			if (checkexist(song.title) == false)
			{
			    // Database.open();
			    Database.deleteSongnotexist((int) song.id, mPlaylistID);
			    flag = true;
			    // Database.close();
			}
		    }

		    if (flag == true)
		    {
			ListSong = SongSelected.getSongsInPlist(context, mPlaylistID);

			mNumOfSongid = 0;
		    }

		    // ListSong = SongSelected.getSongsInPlist(context,
		    // mPlaylistID);
		}
	    }
	    else if (Gvar.ListMode.equals("album"))
	    {
		ListSong = SongInAlbum.getSong(context, mPlaylistID);
	    }
	    else if (Gvar.ListMode.equals("folder"))
	    {
		ListSong = SongsInFolder.getSongInFolder(context, folderName);
	    }
	    else
	    {
		ListSong = MusicStore.getMusics(context);
	    }

	}

	// increase number of use app if need here (optional) to make sure app
	// works well

	mNumOfUseApp++;

	SharedPreferences.Editor mPrefsEditor = mPrefs.edit();
	mPrefsEditor.putInt("NUMOFUSE", mNumOfUseApp);
	mPrefsEditor.commit();

	// Tao code xong tao deo hiu khuc nay` de lam` gi`, nhung ma` deo dam
	// delete vi` ko bit no co anh hg gi`
	if (ListSong.size() != 0)
	{
	    Gvar.setGListSong(ListSong);
	}
	else
	{
	    ListSong = MusicStore.getMusics(context);
	    Gvar.setGListSong(ListSong);
	}

	if (Gvar.GListSong.size() == 0)
	{
	    // Dialog
	    AlertDialog.Builder alert = new AlertDialog.Builder(TuneMateProVer2.this);

	    alert.setMessage("Notice");

	    // Set an EditText view to get user input
	    final TextView input = new TextView(getApplicationContext());
	    input.setText("No music found in your phone. Download some songs and play!");
	    alert.setView(input);

	    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
	    {

		public void onClick(DialogInterface dialog, int whichButton)
		{
		    finish();
		}
	    });

	    alert.show();
	    //
	    return;
	}

	// set current value to music store
	Gvar.mode = mSavedMode;
	Gvar.currentSongInList = mNumOfSongid;
	// Gvar.currentSongInListInSongselected = currentInListInSongSelected;

	// Progress bar
	seekbar = (SeekBar) findViewById(R.id.seekBar);

	// new Thread(this).start();

	// intent to webservice

	// new
	if (Gvar.isPlaying == false)
	{
	    Gvar.setGListSong(ListSong);
	    Gvar.image = imageView;
	    Gvar.seek = seekbar;
	    // Gvar.txtTime =
	    Gvar.isPlaying = false;

	    // new
	    imageBtnView.setImageResource(R.drawable.btnpause);
	    imageBtnView.setVisibility(ImageView.VISIBLE);
	    new Thread(this).start();

	    // new

	    startService(new Intent(MusicStore.ACTION_RESTORE));

	}
	else
	{
	    Gvar.setGListSong(ListSong);
	    Gvar.image = imageView;
	    Gvar.seek = seekbar;
	    Gvar.isPlaying = true;

	    // new
	    imageBtnView.setImageResource(R.drawable.btnplay);
	    imageBtnView.setVisibility(ImageView.VISIBLE);
	    new Thread(this).start();

	    // new
	    startService(new Intent(MusicStore.ACTION_PLAYCURRENT));

	}

	seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
	{

	    @Override
	    public void onStopTrackingTouch(SeekBar seekBar)
	    {

		// touch the progress bar
		Gvar.mediaPlayer.seekTo(seekBar.getProgress());
	    }

	    @Override
	    public void onStartTrackingTouch(SeekBar seekBar)
	    {

	    }

	    @Override
	    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	    {

	    }
	});
	// Progress bar

	imgList.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		// intent to list

		Intent intent = new Intent(context, Library.class);
		startActivity(intent);

	    }
	});

	imgSetting.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		// intent to Tour
		Intent intent = new Intent(context, Tourial.class);
		startActivity(intent);

	    }
	});

	// Some supported gesture for play pause and increase decrease volume
	// depend on coordinate
	mainlayout.setOnTouchListener(new OnTouchListener()
	{

	    @Override
	    public boolean onTouch(View v, MotionEvent event)
	    {

		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
		    // get down event coordinate
		    downCoor = new Coordinate((int) event.getX(), (int) event.getY());
		}
		if (event.getAction() == MotionEvent.ACTION_UP)
		{
		    // get up event coordinate
		    upCoor = new Coordinate((int) event.getX(), (int) event.getY());

		    if ((diff = downCoor.y - upCoor.y) < -60)
		    {
			// decrease volume

			volumeBar.setVisibility(ProgressBar.VISIBLE);

			int step = (upCoor.y - downCoor.y) / 60;

			for (int i = 0; i < step; i++)
			{
			    audioFactory.decreaseVolume();
			    volumeBar.setProgress(audioFactory.getCurrentVolume());
			}

			new Thread((Runnable) context).start();

			// Toast.makeText(context, "decrease volume",
			// Toast.LENGTH_SHORT).show();
		    }

		    if ((diff = downCoor.y - upCoor.y) > 60)
		    {
			// increase volume

			volumeBar.setVisibility(ProgressBar.VISIBLE);

			int step = (downCoor.y - upCoor.y) / 60;

			for (int i = 0; i < step; i++)
			{
			    audioFactory.increaseVolume();

			    volumeBar.setProgress(audioFactory.getCurrentVolume());
			}
			new Thread((Runnable) context).start();

			// Toast.makeText(context, "increase volume",
			// Toast.LENGTH_SHORT).show();
		    }

		    // play and pause
		    if (((downCoor.x - upCoor.x) <= 9 && (downCoor.x - upCoor.x) >= -9) && ((downCoor.y - upCoor.y) <= 9 && (downCoor.y - upCoor.y) >= -9))
		    {

			if (Gvar.isPlaying == false)
			{
			    Gvar.isPlaying = true;

			}
			else
			{
			    Gvar.isPlaying = false;

			}

			if (Gvar.isPlaying == true)
			{
			    // new
			    imageBtnView.setImageResource(R.drawable.btnplay);
			    imageBtnView.setVisibility(ImageView.VISIBLE);
			    new Thread((Runnable) context).start();

			    // new
			    // if this is the first time play
			    if (mNumOfUseApp == 0)
			    {

				Gvar.setGListSong(ListSong);
				Gvar.image = imageView;
				Gvar.seek = seekbar;
				startService(new Intent(MusicStore.ACTION_PLAYCURRENT));

				mNumOfUseApp++;
				SharedPreferences.Editor mPrefsEditor = mPrefs.edit();
				mPrefsEditor.putInt("NUMOFUSE", mNumOfUseApp);
				mPrefsEditor.commit();
			    }
			    else
			    // else if not the first time => play the saved
			    // current song in SharePrefer
			    {

				// new
				imageBtnView.setImageResource(R.drawable.btnplay);
				imageBtnView.setVisibility(ImageView.VISIBLE);
				new Thread((Runnable) context).start();

				// new
				Gvar.setGListSong(ListSong);
				Gvar.image = imageView;
				Gvar.seek = seekbar;
				startService(new Intent(MusicStore.ACTION_PLAYCURRENT));

			    }

			    // Toast.makeText(context, "start",
			    // Toast.LENGTH_SHORT).show();
			}
			else
			{
			    // new
			    imageBtnView.setImageResource(R.drawable.btnpause);
			    imageBtnView.setVisibility(ImageView.VISIBLE);
			    new Thread((Runnable) context).start();

			    // new
			    Gvar.mediaPlayer.pause();
			    // Toast.makeText(context, "pause",
			    // Toast.LENGTH_SHORT).show();
			}

		    }

		}

		return true;
	    }
	});
	// Some supported gesture for play pause and increase decrease volume
	// depend on coordinate

	Gvar.isInApp = true;

	// new shake
	if (Gvar.shaker == null)
	{
	    Gvar.shaker = new Shaker(this, 9.0d, 500, this);
	}
	// new shake

	// shake
	/*mSensorListener = new ShakeListener();
	mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
	mSensorListener.setOnShakeListener(new ShakeListener.OnShakeListener() 
	{
	  public void onShake() 
	  {
	  }
	});*/
	// shake

    }

    // Gesture player
    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture)
    {

	ArrayList<Prediction> predictions = gestureLib.recognize(gesture);

	if (predictions.size() > 0 && predictions.get(0).score > 2.0)
	{
	    String action = predictions.get(0).name;

	    if ("next".equals(action))
	    {
		// next song

		if (Gvar.mode == NONE)
		{

		    // new
		    imageBtnView.setImageResource(R.drawable.btnnext);
		    imageBtnView.setVisibility(ImageView.VISIBLE);
		    new Thread((Runnable) context).start();

		    // new

		    // new
		    Gvar.setGListSong(ListSong);
		    Gvar.image = imageView;
		    Gvar.seek = seekbar;

		    startService(new Intent(MusicStore.ACTION_STOP));
		    startService(new Intent(MusicStore.ACTION_NEXT));
		    // new

		}
		else if (Gvar.mode == SHUFFLE)
		{

		    // new
		    imageBtnView.setImageResource(R.drawable.btnnext);
		    imageBtnView.setVisibility(ImageView.VISIBLE);
		    new Thread((Runnable) context).start();

		    // new

		    Gvar.setGListSong(ListSong);
		    Gvar.image = imageView;
		    Gvar.seek = seekbar;

		    startService(new Intent(MusicStore.ACTION_STOP));
		    startService(new Intent(MusicStore.ACTION_RANDOM));

		}
		else if (Gvar.mode == REPEAT)
		{

		    // new
		    imageBtnView.setImageResource(R.drawable.btnnext);
		    imageBtnView.setVisibility(ImageView.VISIBLE);
		    new Thread((Runnable) context).start();

		    // new

		    Gvar.setGListSong(ListSong);
		    Gvar.image = imageView;
		    Gvar.seek = seekbar;

		    startService(new Intent(MusicStore.ACTION_STOP));
		    startService(new Intent(MusicStore.ACTION_PLAYCURRENT));
		    // new

		}

		Gvar.isPlaying = true;

		// Toast.makeText(this, "next", Toast.LENGTH_SHORT).show();
	    }
	    else if ("previous".equals(action))
	    {
		// previous song

		if (Gvar.mode == NONE)
		{

		    // new
		    imageBtnView.setImageResource(R.drawable.btnprevious);
		    imageBtnView.setVisibility(ImageView.VISIBLE);
		    new Thread((Runnable) context).start();

		    // new

		    Gvar.setGListSong(ListSong);
		    Gvar.image = imageView;
		    Gvar.seek = seekbar;
		    startService(new Intent(MusicStore.ACTION_STOP));
		    startService(new Intent(MusicStore.ACTION_PREVIOUS));

		}
		else if (Gvar.mode == SHUFFLE)
		{

		    // new
		    imageBtnView.setImageResource(R.drawable.btnprevious);
		    imageBtnView.setVisibility(ImageView.VISIBLE);
		    new Thread((Runnable) context).start();

		    // new

		    Gvar.setGListSong(ListSong);
		    Gvar.image = imageView;
		    Gvar.seek = seekbar;
		    startService(new Intent(MusicStore.ACTION_STOP));
		    startService(new Intent(MusicStore.ACTION_RANDOM));

		}
		else if (Gvar.mode == REPEAT)
		{

		    // new
		    imageBtnView.setImageResource(R.drawable.btnprevious);
		    imageBtnView.setVisibility(ImageView.VISIBLE);
		    new Thread((Runnable) context).start();

		    // new

		    Gvar.setGListSong(ListSong);
		    Gvar.image = imageView;
		    Gvar.seek = seekbar;
		    startService(new Intent(MusicStore.ACTION_STOP));
		    startService(new Intent(MusicStore.ACTION_PLAYCURRENT));

		}

		Gvar.isPlaying = true;

		// Toast.makeText(this, "previous", Toast.LENGTH_SHORT).show();
	    }
	    else if ("shuffle".equals(action))
	    {

		// img mode
		imgMode.setImageResource(R.drawable.imgshuffle);
		// img mode

		// shuffle mode

		mode = SHUFFLE;
		Gvar.mode = mode;

		// save in share preference
		SharedPreferences.Editor mPrefsEditor = mPrefs.edit();
		mPrefsEditor.putInt("MODE", mode);
		mPrefsEditor.commit();

		// Toast.makeText(this, "shuffle", Toast.LENGTH_SHORT).show();
	    }
	    else if ("repeat".equals(action))
	    {
		// img mode
		imgMode.setImageResource(R.drawable.imgrepeat);
		// img mode

		// repeat mode

		mode = REPEAT;
		Gvar.mode = mode;

		// save in share preference
		SharedPreferences.Editor mPrefsEditor = mPrefs.edit();
		mPrefsEditor.putInt("MODE", mode);
		mPrefsEditor.commit();

		// Toast.makeText(this, "repeat", Toast.LENGTH_SHORT).show();
	    }
	    else if ("none".equals(action))
	    {

		// img mode
		imgMode.setImageResource(R.drawable.imgnone);
		// img mode

		// normal mode

		mode = NONE;
		Gvar.mode = mode;

		// save in share preference
		SharedPreferences.Editor mPrefsEditor = mPrefs.edit();
		mPrefsEditor.putInt("MODE", mode);
		mPrefsEditor.commit();

	    }
	    else if ("playlist".equals(action))
	    {
		// intent to list

		Intent intent = new Intent(context, Library.class);
		startActivity(intent);

	    }
	    else if ("gesture".equals(action)) // Bo Gesture builder ko xai nua
	    {
		// Intent intent = new Intent(context,
		// GestureBuilderActivity.class);
		// startActivity(intent);
	    }
	    else if ("tourial".equals(action))
	    {
		Intent intent = new Intent(context, Tourial.class);
		startActivity(intent);
	    }
	}
    }

    // Gesture player

    // When Exit App save play list ID
    @Override
    protected void onPause()
    {

	SharedPreferences.Editor mPrefsEditor = mPrefs.edit();
	mPrefsEditor.putInt("PLAYLISTID", Gvar.playListId);

	// new folder
	mPrefsEditor.putString("FOLDERNAME", Gvar.folderName);
	//
	mPrefsEditor.putInt("CURRENTSONGSELECTED", Gvar.currentSongInListInSongselected);
	mPrefsEditor.putString("LISTMODE", Gvar.ListMode);
	mPrefsEditor.commit();

	Gvar.isInApp = false;

	Database.close();
	super.onPause();
    }

    // When Finish App
    @Override
    protected void onDestroy()
    {

	// musicStore.release();
	super.onDestroy();
	// shaker.close();
    }

    // Class for increment and decrement volume
    public class AudioFactory
    {

	AudioManager audioManager;

	public AudioFactory(Context context)
	{

	    audioManager = (AudioManager) context.getApplicationContext().getSystemService(AUDIO_SERVICE);
	}

	public AudioManager getAudioManager()
	{

	    return this.audioManager;
	}

	public void increaseVolume()
	{

	    audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
	}

	public void decreaseVolume()
	{

	    audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
	}

	public int getCurrentVolume()
	{

	    return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	public int getMaxVolume()
	{

	    return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

    }

    @Override
    public void shakingStarted()
    {

	if (Gvar.isInApp == true)
	{

	    if (Gvar.mode == NONE)
	    {

		// new
		imageBtnView.setImageResource(R.drawable.btnshake);
		imageBtnView.setVisibility(ImageView.VISIBLE);
		new Thread((Runnable) context).start();

		// new

		// next
		// Gvar.setGListSong(ListSong);
		// Gvar.image = imageView;
		// Gvar.seek = seekbar;

		startService(new Intent(MusicStore.ACTION_STOP));
		startService(new Intent(MusicStore.ACTION_NEXT));

		// Toast.makeText(context, "Shake!", Toast.LENGTH_SHORT).show();
	    }
	    else if (Gvar.mode == SHUFFLE)
	    {

		// new
		imageBtnView.setImageResource(R.drawable.btnshake);
		imageBtnView.setVisibility(ImageView.VISIBLE);
		new Thread((Runnable) context).start();

		// new

		// play random
		// Gvar.setGListSong(ListSong);
		// Gvar.image = imageView;
		// Gvar.seek = seekbar;

		startService(new Intent(MusicStore.ACTION_STOP));
		startService(new Intent(MusicStore.ACTION_RANDOM));

		// Toast.makeText(context, "Shake!", Toast.LENGTH_SHORT).show();
	    }
	    else if (Gvar.mode == REPEAT)
	    {

		// new
		imageBtnView.setImageResource(R.drawable.btnshake);
		imageBtnView.setVisibility(ImageView.VISIBLE);
		new Thread((Runnable) context).start();

		// new

		// repeat

		// Gvar.setGListSong(ListSong);
		// Gvar.image = imageView;
		// Gvar.seek = seekbar;

		startService(new Intent(MusicStore.ACTION_STOP));
		startService(new Intent(MusicStore.ACTION_PLAYCURRENT));

	    }

	    Gvar.isPlaying = true;
	}
	else if (Gvar.isInApp == false)
	{

	    if (Gvar.isPlaying == true)
	    {

		if (Gvar.mode == NONE)
		{

		    // new
		    imageBtnView.setImageResource(R.drawable.btnshake);
		    imageBtnView.setVisibility(ImageView.VISIBLE);
		    new Thread((Runnable) context).start();

		    // new

		    // next
		    // Gvar.setGListSong(ListSong);
		    // Gvar.image = imageView;
		    // Gvar.seek = seekbar;

		    startService(new Intent(MusicStore.ACTION_STOP));
		    startService(new Intent(MusicStore.ACTION_NEXT));

		    // Toast.makeText(context, "Shake!",
		    // Toast.LENGTH_SHORT).show();
		}
		else if (Gvar.mode == SHUFFLE)
		{

		    // new
		    imageBtnView.setImageResource(R.drawable.btnshake);
		    imageBtnView.setVisibility(ImageView.VISIBLE);
		    new Thread((Runnable) context).start();

		    // new

		    // play random
		    // Gvar.setGListSong(ListSong);
		    // Gvar.image = imageView;
		    // Gvar.seek = seekbar;

		    startService(new Intent(MusicStore.ACTION_STOP));
		    startService(new Intent(MusicStore.ACTION_RANDOM));

		    // Toast.makeText(context, "Shake!",
		    // Toast.LENGTH_SHORT).show();
		}
		else if (Gvar.mode == REPEAT)
		{

		    // new
		    imageBtnView.setImageResource(R.drawable.btnshake);
		    imageBtnView.setVisibility(ImageView.VISIBLE);
		    new Thread((Runnable) context).start();

		    // new

		    // repeat

		    // Gvar.setGListSong(ListSong);
		    // Gvar.image = imageView;
		    // Gvar.seek = seekbar;

		    startService(new Intent(MusicStore.ACTION_STOP));
		    startService(new Intent(MusicStore.ACTION_PLAYCURRENT));

		}

		Gvar.isPlaying = true;
	    }
	}

    }

    @Override
    public void shakingStopped()
    {

    }

    @Override
    public void run()
    {

	try
	{
	    Thread.sleep(2 * 1000);
	}
	catch (InterruptedException e)
	{

	    e.printStackTrace();
	}

	handler.sendEmptyMessage(0);

    }

    private Handler handler = new Handler()
    {

	@Override
	public void handleMessage(Message msg)
	{

	    volumeBar.setVisibility(ProgressBar.GONE);
	    imageBtnView.setVisibility(ImageView.GONE);
	}
    };

    public boolean checkexist(String title)
    {

	for (Song song1 : listSong1)
	{
	    if (song1.title.equals(title))
	    {
		return true;
	    }
	}
	return false;
    }

}

/*else if ("up".equals(action)) 
	 	        {
	 	        	
	 	        	//increase volume
					volumeBar.setVisibility(SeekBar.VISIBLE);
	 	        	
	 	        	audioFactory.increaseVolume();
	 	        	
	 	        	volumeBar.setProgress(audioFactory.getCurrentVolume());
	 	        	
	 	        	new Thread((Runnable) context).start();
	 	        	
	 	        	
	 	            Toast.makeText(this, "up", Toast.LENGTH_SHORT).show();
	 	        }
	 	        else if ("down".equals(action)) 
	 	        {
	 	        	//decrease volume
	 	        	
					volumeBar.setVisibility(SeekBar.VISIBLE);
					
					audioFactory.decreaseVolume();
	 	        	   	
	 	        	volumeBar.setProgress(audioFactory.getCurrentVolume());
	 	        	
	 	        	new Thread((Runnable) context).start();
	 	        	
	 	        	    
	 	            Toast.makeText(this, "down", Toast.LENGTH_SHORT).show();
	 	        }*/
