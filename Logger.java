/**
 * 
 */

/**
 * @author shrey
 *
 */

import java.io.*;
public class Logger 
{

	static FileOutputStream logFile;
	static OutputStreamWriter out;

	public static void startLog(String fileName) throws IOException 
	{
		logFile = new FileOutputStream(fileName);
		//out = new OutputStreamWriter(logFile, "UTF-8");
		out = new OutputStreamWriter(logFile);
	}
	
	public static void writeLog(String str) 
	{
		try 
		{
			out.write(str + '\n');
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void stopLog() 
	{
		try 
		{
			//out.flush();
			out.close();
			logFile.close();
		} 
		
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	}
}
