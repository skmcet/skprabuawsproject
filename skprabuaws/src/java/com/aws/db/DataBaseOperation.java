//$Id$
package com.aws.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.aws.action.BookChanges;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataBaseOperation {

    private static Logger logger = Logger.getLogger(DataBaseOperation.class.getName());
    private static final DataBaseOperation dataBaseOperationInstance = new DataBaseOperation();
    private static Connection con = null;
    private static PreparedStatement ps = null;
    private static ResultSet rs = null;

    private DataBaseOperation() {
    }

    /**
     * dataBaseOperationInstance is loaded on the first execution of
     * DataBaseOperation.getInstance() or the first access to
     * DataBaseOperation.dataBaseOperationInstance, not before.
     */
    public static DataBaseOperation getInstance() {
        return DataBaseOperation.dataBaseOperationInstance;
    }

    public boolean isAthenticated(String username, String password) {
        Connection con1 = DataBaseConnection.getInstance().getConnection();
        try {
            String query = "select * from userinfo where emailid like '" + username + "' and password like '" + password + "'";
            PreparedStatement ps = con1.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (!con1.isClosed()) {
                    con1.close();
                }
            } catch (Exception ex) {
                logger.log(Level.WARNING, "In isAthenticated()...Caught exception while there closing the Database Connection... " + ex);
            }
        }
    }

    public Boolean isDataChanged(BookChanges bookChange, String tableName) {
        Connection con1 = DataBaseConnection.getInstance().getConnection();
        try {
            String query = "select * from " + tableName + " where itemid=" + bookChange.getItemid() + " AND attribute_name like \'" + bookChange.getAttributeName() + "\' ;";;
            logger.log(Level.INFO, "\nDatabase Query is:" + query);
            PreparedStatement ps = con1.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return true;
            }
            while (rs.next()) {
                logger.log(Level.INFO, "\nDatabase Values are :  " + rs.getInt(1) + "," + rs.getString(2) + "," + rs.getString(3));
                String dbAttributeName = String.valueOf(rs.getString(2)).trim();
                String dbAttributeValue = String.valueOf(rs.getString(3)).trim();
                String attributeName = bookChange.getAttributeName().trim();
                String attributeValue = bookChange.getAttributeValue().trim();
                if (dbAttributeName.equalsIgnoreCase(attributeName) && ((dbAttributeValue.compareTo(attributeValue)) != 0)) {
                    return true;
                }
            }
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            try {
                if (!con1.isClosed()) {
                    con1.close();
                }
            } catch (Exception ex) {
                logger.log(Level.WARNING, "In isAthenticated()...Caught exception while there closing the Database Connection... " + ex);
            }
        }
    }

    public void getItem(int itemid,String booktableName) {
        Connection con1 = DataBaseConnection.getInstance().getConnection();
        try {
            String query = "select * from " + booktableName + " where itemid=" + itemid +";";;
            logger.log(Level.INFO, "\nDatabase Query is:" + query);
            PreparedStatement ps = con1.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                logger.log(Level.INFO, "\nNo Item with this ItemID: "+itemid);
            }
            while (rs.next()) {
                logger.log(Level.INFO, "\nDatabase Values are :  " + rs.getInt(1) + "," + rs.getString(2) + "," + rs.getString(3));
        }
        }catch (Exception ex) {
            ex.printStackTrace();
           
        } finally { 
            try {
                if (!con1.isClosed()) {
                    con1.close();
                }
            } catch (Exception ex) {
                logger.log(Level.WARNING, "In isAthenticated()...Caught exception while there closing the Database Connection... " + ex);
            }
        }
    }
    
    
    public Boolean dataBulkUpdate(ArrayList<BookChanges> bookList, String booktableName, String bookChangeTableName, String attributeTableName) {
        Connection con1 = DataBaseConnection.getInstance().getConnection();
        try {
            //String query = "select * from "+ tableName +" where itemid=" + bookChange.getItemid();
            Long sysTime = System.currentTimeMillis();
            for (int i = 0; i < bookList.size(); i++) {
                BookChanges bookChange = bookList.get(i);
                String query = "select * from " + booktableName + " where itemid=" + bookChange.getItemid() + " AND attribute_name like \'" + bookChange.getAttributeName() + "\' ;";;
                logger.log(Level.INFO, "\nDatabase Query is:" + query);
                con1 = DataBaseConnection.getInstance().getConnection();
                PreparedStatement ps = con1.prepareStatement(query);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    String query5 = "INSERT into " + booktableName + " VALUES ( " +bookChange.getItemid()+",\'" + bookChange.getAttributeName() + "\', \'" + bookChange.getAttributeValue()+ "\') ;";
                    logger.log(Level.INFO, "\nDatabase Query is:" + query5);
                    PreparedStatement ps5 = con1.prepareStatement(query5);
                    //rs = ps5.executeUpdate();
                    //if (rs.next()) {
                    Integer count = ps5.executeUpdate(); 
                    if(count>1)
                    {
                    logger.log(Level.INFO, "\nDatabase Values are Inserted Successfully ");
                    }
                }
                else
                {
                String query4 = "UPDATE " + booktableName + " SET attribute_value =  \'" + bookChange.getAttributeValue() + "\' WHERE itemid = " + bookChange.getItemid() + " AND attribute_name like \'" + bookChange.getAttributeName() + "\' ;";
                logger.log(Level.INFO, "\nDatabase Update Query is:" + query4);
                PreparedStatement ps4 = con1.prepareStatement(query4);
                Integer count = ps4.executeUpdate();
                if (count == 1) {
                    logger.log(Level.INFO, "\nDatabase Values are Updated Successfully ");
                }
                }
                Long attributeId = getAttributeID(bookChange.getAttributeName(), attributeTableName);
                if (attributeId == null) {
                    String query2 = "INSERT into " + attributeTableName +" (attribute_name) VALUES (\'" + bookChange.getAttributeName() + "\') ;";
                    logger.log(Level.INFO, "\nDatabase Query is:" + query2);
                    PreparedStatement ps2 = con1.prepareStatement(query2);
                    //rs = ps2.executeQuery();
                    //if (rs.next()) {
                    Integer count = ps2.executeUpdate(); 
                    if(count>1)
                    {
                    
                        logger.log(Level.INFO, "\nDatabase Values are Inserted Successfully ");
                    }
                }
                attributeId = getAttributeID(bookChange.getAttributeName(), attributeTableName);
                String query3 = "INSERT into " + bookChangeTableName + " VALUES ( " + sysTime + "," + bookChange.getItemid() + "," + attributeId + "," + "\'" + bookChange.getAttributeValue() + "\') ;";
                logger.log(Level.INFO, "\nDatabase Insert Query is:" + query3);
                PreparedStatement ps3 = con1.prepareStatement(query3);
                //rs = ps.executeQuery();
                //if (rs.next()) {
                Integer count = ps3.executeUpdate(); 
                if(count>1)
                    {
                    logger.log(Level.INFO, "\nDatabase Values are Inserted Successfully ");
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            try {
                if (!con1.isClosed()) {
                    con1.close();
                }
            } catch (Exception ex) {
                logger.log(Level.WARNING, "In isAthenticated()...Caught exception while there closing the Database Connection... " + ex);
            }
        }
    }

    public Long getAttributeID(String attributeName, String attributeTableName) {
        Connection con1 = DataBaseConnection.getInstance().getConnection();
        try {
            Long attributeID = null;
            String query = "select * from " + attributeTableName + " where attribute_name= \'" + attributeName + " \'";
            logger.log(Level.INFO, "\nDatabase Query is:" + query);
            PreparedStatement ps = con1.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                logger.log(Level.INFO, "\nDatabase Values are :  " + rs.getInt(1) + "," + rs.getString(2));
                attributeID = Long.valueOf(rs.getString(1));
            }
            return attributeID;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                if (!con1.isClosed()) {
                    con1.close();
                }
            } catch (Exception ex) {
                logger.log(Level.WARNING, "In isAthenticated()...Caught exception while there closing the Database Connection... " + ex);
            }
        }
    }

    public String getAttributeName(Long attributeId, String attributeTableName) {
        Connection con1 = DataBaseConnection.getInstance().getConnection();
        try {
            String attributeName = null;
            String query = "select * from " + attributeTableName + " where attribute_id= " + attributeId + " ;";
            logger.log(Level.INFO, "\nDatabase Query is:" + query);
            PreparedStatement ps = con1.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                logger.log(Level.INFO, "\nDatabase Values are :  " + rs.getInt(1) + "," + rs.getString(2));
                attributeName = String.valueOf(rs.getString(2)).trim();
            }
            return attributeName;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                if (!con1.isClosed()) {
                    con1.close();
                }
            } catch (Exception ex) {
                logger.log(Level.WARNING, "In isAthenticated()...Caught exception while there closing the Database Connection... " + ex);
            }
        }
    }

}
