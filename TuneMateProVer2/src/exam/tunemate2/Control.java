package exam.tunemate2;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Control extends LinearLayout 
{
	
	private ImageView Icon;
	private ImageView Title;
	private TextView Description;

	public Control(Context context) 
	{
		super(context);
		loadViews();
	}

	public Control(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.row, this);
		loadViews();
	}

	private void loadViews() 
	{
		Icon = (ImageView)findViewById(R.id.ivIcon);
		Title = (ImageView)findViewById(R.id.ivTitle);
		Description = (TextView)findViewById(R.id.tvName);
	}

	public void setIcon(Drawable icon) 
	{
		Icon.setImageDrawable(icon);
	}

	public void setTitle(Drawable title) 
	{
		Title.setImageDrawable(title);
	}

	public void setDescription(String name) 
	{
		Description.setText("::  " + name + "  ::");
	}


}
