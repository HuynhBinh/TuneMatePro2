package exam.tunemate2;

public class Song 
{
	long id;
	String title;
	long albumid = -1;
	
	
	public Song (long id, String title , long albumid)
	{
		super();
		this.id = id;
		this.title = title;
		this.albumid = albumid;
	} 
	
	public long getId() 
	{
		return id;
	}
	public void setId(long id) 
	{
		this.id = id;
	}
	public String getTitle() 
	{
		return title;
	}
	public void setTitle(String title) 
	{
		this.title = title;
	}
	@Override
	public String toString() 
	{
		return title;
	}

	public long getAlbumid() 
	{
		return albumid;
	}

	public void setAlbumid(long albumid) 
	{
		this.albumid = albumid;
	}
	
	
	
}
