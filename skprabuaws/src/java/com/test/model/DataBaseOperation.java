//$Id$
package com.test.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class DataBaseOperation 
{
	private int  getUserIdByConditionId(int conditionid)
	{
		
		Connection con = null;
		try
		{
			con = (new DataBaseConnection()).getConnection();
			String query = "select userid from SubConfig where conditionid= "+conditionid;
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			if(rs != null)
			{
				rs.next();
				return rs.getInt("userid");
			}
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
			return -1;
		}
		finally
		{
			if(con != null)
			{
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return -1;
	}
	private void createNotificationLogs(String emailid, String resultString)
	{
		try
		{
			File file = new File(com.aws.action.LargeScaleConfigurableAction.notificationLogsFolder+File.separator+emailid);
			if (!file.exists()) 
			{
				file.mkdir();
			}
			file = new File(com.aws.action.LargeScaleConfigurableAction.notificationLogsFolder+File.separator+emailid+File.separator+System.currentTimeMillis()+".txt");
			file.createNewFile();
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(resultString);
			bw.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void sendNotificationByServiceType(int userid,String resultString)
	{
		Connection con = null;
		try
		{
			con = (new DataBaseConnection()).getConnection();
			String query = "select emailid from Subscriber where userid = "+userid;
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			if(rs != null)
			{
				rs.next();
				String emailid = rs.getString("emailid");
				createNotificationLogs(emailid,resultString);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(con != null)
			{
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	private void sendNotification(int userid, int itemid)
	{
		Connection con = null;
		try
		{
			con = (new DataBaseConnection()).getConnection();
			String query = "select * from bookChanges  where itemid = "+itemid;
			String resultString = "";
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs;
			rs = ps.executeQuery();
			if(rs != null)
			{
				resultString = "";
                                
				while(rs.next())
				{
                                        Long attributeId;
                                        attributeId = Long.valueOf(rs.getInt(3));
                                        String attributeName= com.aws.db.DataBaseOperation.getInstance().getAttributeName(attributeId, "attribute_details");
                                        System.out.println("\nAttribute Name is: " +attributeName);
					resultString += rs.getString(2) +" -> "+ attributeName +" : "+rs.getString(4)+"\n";
				}
			}
			if(resultString.length() > 0 && userid != -1)
			{
				sendNotificationByServiceType(userid,resultString);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(con != null)
			{
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	private void deleteProcessedNotification(long sessionId)
	{
		Connection con = null;
		try
		{
			con = (new DataBaseConnection()).getConnection();
			//code to delete the modfiedBook table entry for each book and for current session
			String query = "delete from bookChanges where sessionid="+sessionId;
			PreparedStatement ps = con.prepareStatement(query);
			ps.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(con != null)
			{
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public void processNotification()
	{
		Connection con = null;
		long sessionId = -1;
		try
		{
			con = (new DataBaseConnection()).getConnection();
			String query = "select min(sessionid) as sessionid from bookChanges";
			PreparedStatement ps = con.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			if(rs != null)
			{
				rs.next();
				sessionId = rs.getLong("sessionid");
				query = "select itemid from bookChanges where sessionid=" +sessionId;
				ps = con.prepareStatement(query);
				rs = ps.executeQuery();
				if(rs != null)
				{	
					HashMap<Integer,ArrayList<Integer>> map  = new HashMap<Integer, ArrayList<Integer>>();
					while(rs.next())
					{
						int itemId =rs.getInt("itemid");
						
						//////////////
						///////////// case for Single and OR condition
						query = "SELECT Config.conditionid as conditionid FROM bookChanges INNER JOIN Config ON Config.cond_col_id=bookChanges.col_id and Config.value=bookChanges.attribute_value and (operatorId =0 or operatorId = 2) and  bookChanges.itemid="+itemId+" and sessionid="+sessionId;
						ps = con.prepareStatement(query);
						ResultSet rs1 = ps.executeQuery();
						ArrayList<Integer> conditionList = null;
						if(rs1 != null)
						{
							conditionList = new ArrayList<Integer>();
							while(rs1.next())
							{
								if(conditionList.contains(rs1.getInt("conditionid")) ==  false)
								  {
									conditionList.add(rs1.getInt("conditionid"));
								  }
							}
						}
						// AND case condition
						query = "SELECT Config.conditionid as conditionid FROM bookChanges INNER JOIN Config ON Config.cond_col_id=bookChanges.col_id and Config.value=bookChanges.attribute_value and operatorId =1 and  bookChanges.itemid="+itemId+" and sessionid="+sessionId;
						ps = con.prepareStatement(query);
						rs1 = ps.executeQuery();
						if(rs1 != null)
						{
							HashMap<Integer,Integer> countMap = new HashMap<Integer,Integer>();
							while(rs1.next())
							{
								int conditionid  = rs1.getInt("conditionid");
								if(countMap == null)
								{
									countMap = new HashMap<Integer,Integer>();
								}
								if(countMap.get(conditionid) == null)
								{
									countMap.put(conditionid,1);
								}
								else
								{
									countMap.put(conditionid,2);
								}
							}
							if(countMap != null)
							{
								if(conditionList == null)
								{
									conditionList = new ArrayList<Integer>();
								}
								Iterator<Map.Entry<Integer, Integer>> entries = countMap.entrySet().iterator();
								while (entries.hasNext()) 
								{
									  Map.Entry<Integer, Integer> entry = entries.next();
									  Integer key = entry.getKey();
									  Integer value = entry.getValue();
									  if(conditionList.contains(key) ==  false && value > 1)
									  {
										  conditionList.add(key);
									  }
								}
							}
						}
						map.put(itemId,conditionList);
					}
					
					if(map != null)
					{
						Iterator<Map.Entry<Integer, ArrayList<Integer>>> entries = map.entrySet().iterator();
						while (entries.hasNext()) 
						{
							  Entry<Integer, ArrayList<Integer>> entry = entries.next();
							  Integer key = entry.getKey();
							  ArrayList<Integer> list = entry.getValue();
							  if(list != null)
							  {
								  for(int index=0;index < list.size();index++)
								  {
									  int userid = getUserIdByConditionId(list.get(index));
									  sendNotification(userid, key);
								  }
							  }
						}
					}
				}
				deleteProcessedNotification(sessionId);
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(con != null)
			{
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
