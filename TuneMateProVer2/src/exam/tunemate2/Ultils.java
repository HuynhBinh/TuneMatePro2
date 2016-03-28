package exam.tunemate2;

public class Ultils 
{
	
	 public static String getDirectory(String fullPath) 
	 {
	        int index = fullPath.lastIndexOf('/');
	        if (index > 0)
	            return fullPath.substring(0, index);
	        return "";
	 }
	 
	 
	 public static String getFolderName(String path)
	 {
	        int index = path.lastIndexOf('/');
	        if (index > 0)
	            return path.substring(index + 1, path.length());
	        return "";
	 }

}
