package exam.tunemate2;

public class AlbumInfo 
{
	
		String name;
		long Id;
		
		public AlbumInfo(String name, long id)
		{
			this.name = name;
			this.Id = id;
		}

		@Override
		public String toString() 
		{
			return this.name ;
		}
		
}
