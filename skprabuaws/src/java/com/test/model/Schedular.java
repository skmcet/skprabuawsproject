//$Id$
package com.test.model;

public class Schedular implements Runnable
{
	public void run() 
	{
		int i = 0 ;
		DataBaseOperation dboper = new DataBaseOperation();
		while(true)
		{	
			try 
			{
				Thread.sleep(10000);
				dboper.processNotification();
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		
	}
}
