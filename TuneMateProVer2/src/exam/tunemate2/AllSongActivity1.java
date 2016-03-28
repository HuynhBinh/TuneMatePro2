package exam.tunemate2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
//import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

public class AllSongActivity1 extends ListActivity implements Runnable
{

    ArrayList<Song> listSong = new ArrayList<Song>();

    // ArrayAdapter<Song> Adapter; // not use anyore -> use adapter1 instead

    GlobalVariable Gvar;

    Context context;

    private ArrayList<HashMap<String, Object>> mySongs;

    private ArrayList<String> namelist1 = new ArrayList<String>();

    private static final String TITLE = "title";

    private static final String IMAGE = "image";

    ImageView btnBack;

    ImageView btnNow;

    SimpleAdapter adapter1;

    Thread thread;

    int tmp = 0;

    // gesture library to load gesture from SD
    private GestureLibrary gestureLib;

    ImageView btnSearch;

    EditText edtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

	super.onCreate(savedInstanceState);
	setContentView(R.layout.allsong);

	// load from raw resource folder
	gestureLib = GestureLibraries.fromRawResource(getApplicationContext(), R.raw.gestures);

	// if not load finish app
	if (!gestureLib.load())
	{
	    finish();
	}

	context = this.getApplicationContext();
	// Get Global Variable
	Gvar = (GlobalVariable) getApplicationContext();

	btnBack = (ImageView) findViewById(R.id.btnBackAllsong);

	btnSearch = (ImageView) findViewById(R.id.btnSearchAllsong);
	edtSearch = (EditText) findViewById(R.id.edtSearch);

	// Get all song
	listSong = MusicStore.getMusics(getApplicationContext());

	//
	mySongs = new ArrayList<HashMap<String, Object>>();

	// Fill to list
	// getListView().setAdapter(Adapter);
	getData();

	adapter1 = new SimpleAdapter(this, mySongs, R.layout.custom_playbt_listview, new String[]
	{
	TITLE, IMAGE
	}, new int[]
	{
	R.id.title, R.id.button
	});

	getListView().setAdapter(adapter1);

	// Click a song and back to Main activity and Play it.
	getListView().setOnItemClickListener(new OnItemClickListener()
	{

	    @Override
	    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
	    {

		try
		{
		    thread.interrupt();
		}
		catch (Exception e)
		{

		}

		// List of all songs
		Gvar.GListSong = listSong;
		Gvar.isReturnMain = true;
		Gvar.ListMode = "allsongs";
		// Playlist ID of All songs = 0 Other playlist begin by 1.
		Gvar.playListId = 0;
		Gvar.currentSongInList = position - 1;
		Gvar.folderName = "";
		if (Gvar.isPlaying == false)
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

		// intent to list

		Intent intent = new Intent(context, Library.class);
		startActivity(intent);
		finish();

	    }
	});

	btnNow = (ImageView) findViewById(R.id.btnNowAllsong);
	btnNow.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		finish();

	    }
	});

	btnSearch.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View v)
	    {

		if (edtSearch.getVisibility() == EditText.GONE)
		{
		    edtSearch.setVisibility(EditText.VISIBLE);
		}
		else
		{
		    edtSearch.setVisibility(EditText.GONE);
		}

	    }
	});

	edtSearch.addTextChangedListener(new TextWatcher()
	{

	    @Override
	    public void onTextChanged(CharSequence s, int start, int before, int count)
	    {

		// String word = s.toString().toLowerCase().trim();
		String word = s.toString().toLowerCase().trim();
		if (!word.equalsIgnoreCase(""))
		{
		    listSong = (ArrayList<Song>) search(word, listSong);
		    mySongs = new ArrayList<HashMap<String, Object>>();

		    getData();

		    adapter1 = new SimpleAdapter(AllSongActivity1.this, mySongs, R.layout.custom_playbt_listview, new String[]
		    {
		    TITLE, IMAGE
		    }, new int[]
		    {
		    R.id.title, R.id.button
		    });

		    getListView().setAdapter(adapter1);

		   
		}
		else
		{
		   
		    //setListAdapter(disadap);

		    //backModeDA = 0;
		}

	    }

	    @Override
	    public void beforeTextChanged(CharSequence s, int start, int count, int after)
	    {

	    }

	    @Override
	    public void afterTextChanged(Editable s)
	    {

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
	    if (!thread.isInterrupted())
	    {
		thread.interrupt();
	    }
	}
	catch (Exception e)
	{

	}

	thread = null;

	super.onPause();
    }

    public static List<Song> search(String value, List<Song> listSongToSearch)
    {

	List<Song> listSongReturn = new ArrayList<Song>();

	for (Song song : listSongToSearch)
	{
	    if (song.title.contains(value))
	    {
		listSongReturn.add(song);
	    }
	}

	return listSongReturn;
    }

    private void getData()
    {

	int i = (int) Gvar.GListSong.get(Gvar.currentSongInListInSongselected).id;

	// Position song in list
	int j = 0;

	for (Song song : listSong)
	{
	    HashMap<String, Object> hm;
	    hm = new HashMap<String, Object>();
	    hm.put(TITLE, song.title);

	    if (song.id == i)
	    {
		hm.put(IMAGE, R.drawable.splay);

		// assign position for temp to update button play
		tmp = j;

	    }
	    else
	    {
		hm.put(IMAGE, R.drawable.sstop);
	    }

	    mySongs.add(hm);
	    namelist1.add(song.title);
	    j++;
	}
    }

    @Override
    public void run()
    {

	int CurrentPosition = Gvar.currentSongInList;
	int i = Gvar.currentSongInList;

	while (i == CurrentPosition)
	{
	    try
	    {
		Thread.sleep(1000);
		CurrentPosition = Gvar.currentSongInList;
		Log.i("CurrentPosition in while ", CurrentPosition + "");

		if (i != CurrentPosition)
		{

		    Message mess = new Message();
		    Bundle bund = new Bundle();
		    bund.putInt("id", i);
		    mess.setData(bund);
		    Thread.sleep(1000);
		    i = Gvar.currentSongInList;
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
	    int i = msg.getData().getInt("id");

	    // Current Song id
	    int j = Gvar.currentSongInList;

	    if (Gvar.ListMode.equals("allsongs"))
	    {
		// Set play image by current Song id
		HashMap<String, Object> hm1;
		hm1 = new HashMap<String, Object>();
		hm1.put(TITLE, Gvar.GListSong.get(j).getTitle());
		hm1.put(IMAGE, R.drawable.splay);
		mySongs.set(j, hm1);

		// Set stop image by previous Song id
		HashMap<String, Object> hm;
		hm = new HashMap<String, Object>();
		hm.put(TITLE, Gvar.GListSong.get(i).getTitle());
		hm.put(IMAGE, R.drawable.sstop);
		mySongs.set(i, hm);

		adapter1.notifyDataSetChanged();

	    }

	    else
	    {
		int i1 = (int) Gvar.GListSong.get(Gvar.currentSongInListInSongselected).id;

		int position = 0;

		for (Song s : listSong)
		{
		    if (s.id == i1)
		    {
			HashMap<String, Object> hm1;
			hm1 = new HashMap<String, Object>();
			hm1.put(TITLE, Gvar.GListSong.get(j).getTitle());
			hm1.put(IMAGE, R.drawable.splay);
			mySongs.set(position, hm1);

			// Set stop image by previous Song id
			HashMap<String, Object> hm;
			hm = new HashMap<String, Object>();
			hm.put(TITLE, Gvar.GListSong.get(i).getTitle());
			hm.put(IMAGE, R.drawable.sstop);
			mySongs.set(tmp, hm);

			adapter1.notifyDataSetChanged();
			// Update lai bien tam
			tmp = position;
		    }

		    position++;

		}

	    }

	}
    };

}
