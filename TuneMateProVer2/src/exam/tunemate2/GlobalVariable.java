package exam.tunemate2;

import java.util.ArrayList;

import android.app.Application;
import android.media.MediaPlayer;
import android.test.suitebuilder.TestSuiteBuilder.FailedToCreateTests;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class GlobalVariable extends Application 
{
	
	
	
	// luu listsong tam thoi ben PlayList.java tra ve`
	ArrayList<Song> GListSong = new ArrayList<Song>();
	
	// Set gia tri == true neu nhu user chon. play 
	// Ben on Resume se tuy` vao` gia tri nay` ma` play list song moi hoac la` ko
	boolean isReturnMain = false;
	
	String ListMode = "";
	// Luu lai play list id cho lan` sau load len lai.
	int playListId;
	// luu lai bai` hat dang choi trong list (chua qua xu ly' phai dua vo ham next moi dc xu ly' vi tri max
	// va vi tri = -1)
	int currentSongInList;
	// Luu lai gia tri is playing de~ biet ma` su ly play stop pause
	// isplaying luon luon = false trong file GlobalVariable nay vi 2 li do
	// 1. ko FC khi co cuoc goi den
	// 2. Muon doi trang thai cua Isplaying thi doi~ ben cho khac chu ko doi~ o day vi` se gay FC
	boolean isPlaying = false;
	
	ImageView image;
	SeekBar seek;
	
	// Use to update playbutton
	String CurrentSongTitle;
	String PreviousSongTitle;
	
	TextView txtTitle;
	
	int mode;
	
	boolean isInApp;
	
	Shaker shaker = null;
	
	TextView txtTime;
	
	//folder name use for folder only
	String folderName = "";
	
	
	// current song position in list (Da qua xu ly cua ham` next,  previous // ko bi truong hop out of bound
	// xu dung cho class Songselected cua thang Vu)
	int currentSongInListInSongselected;
	
	MediaPlayer mediaPlayer;
	
	
	
	public ArrayList<Song> getGListSong() 
	{
		return GListSong;
	}

	public void setGListSong(ArrayList<Song> gListSong) 
	{
		GListSong = gListSong;
	}
	
}
