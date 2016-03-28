package exam.tunemate2;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;


public class CallReciever extends BroadcastReceiver 
{

	 
	@Override
    public void onReceive(Context context, Intent intent) 
	{
		
		try
		{
					
			GlobalVariable Gvar = (GlobalVariable)context.getApplicationContext();
									
	        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) 
	        {	
	        			
	        		if(Gvar.mediaPlayer.isPlaying())
	        		{
	        			
	        			context.startService(new Intent(MusicStore.ACTION_PAUSE));
	        		}
	        		
	        		
	        		
	        }
	        else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)) 
	        {
	        	if(Gvar.isPlaying == true)
	        	{
	        		// This code will execute when the call is answered or disconnected
	        		context.startService(new Intent(MusicStore.ACTION_PLAYCURRENT));
	        	}
	        }
	        else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) 
	        {	
    			
        		if(Gvar.mediaPlayer.isPlaying())
        		{
        			
        			context.startService(new Intent(MusicStore.ACTION_PAUSE));
        		}
        		
        		
        		
        }
	        
	        
	        
		}
		catch(Exception ex)
		{
			
		}

    }

}
