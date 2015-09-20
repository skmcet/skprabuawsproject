/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aws.action;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.opencsv.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import com.aws.db.DataBaseOperation;

import java.io.FileReader;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author shantha-2230
 */
public class LargeScaleConfigurableAction {

    private static Logger logger = Logger.getLogger(LargeScaleConfigurableAction.class.getName());
    private static LargeScaleConfigurableAction LargeScaleConfigurableInstance = null;
    private static String csvdirPath = "E:\\Amazon_Hackton_19-9-2015\\CsvDir";
    private static String processedCsvDirPath = "E:\\Amazon_Hackton_19-9-2015\\ProccessedCsvDir";
    private static String booktableName = "book";
    private static String bookChangeTableName = "bookChanges";
    private static String attributeTableName = "attribute_details";
    public static String notificationLogsFolder ="E:"+File.separator+"Amazon_Hackton_19-9-2015"+File.separator+"NotficationLogs";

    
    public static LargeScaleConfigurableAction getInstance() {
        if (LargeScaleConfigurableInstance == null) {
            LargeScaleConfigurableInstance = new LargeScaleConfigurableAction();
        }
        return LargeScaleConfigurableInstance;
    }
    
    
    public static void main(String[] args) {
        try {
            //Ware intiating the Scheduler thread from here and it will be always alive
            getInstance().initateSchedulerThread();
            getInstance().initateCsvProcessThread(); 
            if(args[0]!=null)
            {
             Integer itemId = Integer.valueOf(args[0]);
             DataBaseOperation.getInstance().getItem(itemId,booktableName);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Caught exception while Procesing the file from directory " + csvdirPath, e);
        }
    }
    
    public static void largeScaleProccess()
    {
        try
        {
        File csvdirPathFile = new File(csvdirPath);
        while(csvdirPathFile.isDirectory())
        {
            if (csvdirPathFile.isDirectory()) {
                String allFiles[] = csvdirPathFile.list();
                for (int i = 0; i < allFiles.length; i++) {
                    String srcfilePath = csvdirPath + File.separator + allFiles[i];
                    String destfilePath = processedCsvDirPath + File.separator + allFiles[i];
                    File fileName = new File(srcfilePath);
                    logger.log(Level.INFO, "File Processed from directory is:  " + fileName);
                    processCSVFile(srcfilePath);
                    //Move Oepration is not used as it is operating system dependent, to be safe we copy to the processed directory and delete the Source File
                    Boolean copySuccess = copyFile(new File(srcfilePath), new File(destfilePath));
                    if (copySuccess) {
                        Boolean isDeltedFile = deleteFile(srcfilePath);
                        if (isDeltedFile) {
                            logger.log(Level.INFO, "Successfuly moved the Proccessed CSV File to: " + destfilePath);
                        } else {
                            logger.log(Level.INFO, "Cannot move the Proccessed CSV File: " + srcfilePath);
                        }
                    }
                }
            }
        }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Caught exception while Procesing the file from directory " + csvdirPath, e);
        }
    }
    
    
    
    //Thread to Call Zip Process
    Runnable createSchThread = new Runnable() {
        public void run() {
            StartSchedular.schedulerStart();
            }
     };
    
    public void initateSchedulerThread() {
        //Intiating Thread to Call Notification Process
        try
        {
        if (!createSchTh.isAlive()) {
            createSchTh = new Thread(createSchThread);
            createSchTh.start();//No I18N
            logger.log(Level.INFO, "######## Notification Scheduler Thread Started ################");
        } else {
            logger.log(Level.INFO, "######## Notification Scheduler Thread is Running ################");
        }
      }
        catch(Exception e)
        {
            logger.log(Level.WARNING, "######## Exception in Starting the Notification Scheduler Thread ################");
        }
    }
    Thread createSchTh = new Thread(createSchThread); //Creating the Thread for the Runnable


    //Thread to Call Zip Process
    Runnable createCsvThread = new Runnable() {
        public void run() {
            largeScaleProccess();
            }
     };
    
    public void initateCsvProcessThread() {
        //Intiating Thread to Call Notification Process
        try
        {
        if (!createCsvprocessTh.isAlive()) {
            createCsvprocessTh = new Thread(createCsvThread);
            createCsvprocessTh.start();//No I18N
            logger.log(Level.INFO, "######## CSV Process Scheduler Thread Started ################");
        } else {
            logger.log(Level.INFO, "######## CSV Process Scheduler Thread is Running ################");
        }
      }
        catch(Exception e)
        {
            logger.log(Level.WARNING, "######## Exception in Starting the CSV Process Scheduler Thread ################");
        }
    }
    Thread createCsvprocessTh = new Thread(createCsvThread); //Creating the Thread for the Runnable

    public static void processCSVFile(String FileName) throws Exception {
    CsvToBean csv = null;
    CSVReader csvReader = null;
        try {
            csv = new CsvToBean();
            csvReader = new CSVReader(new FileReader(FileName), ',');
            List list = csv.parse(setColumMapping(), csvReader);
            ArrayList<BookChanges> bookList = new ArrayList<BookChanges>();
            for (Object object : list) {
                BookChanges bookChange = (BookChanges) object;
                logger.log(Level.INFO, "\nThe Row Value of the CSV File is :  " + bookChange);
                logger.log(Level.INFO, "\nThe Item Id is :  " + bookChange.getItemid());
                logger.log(Level.INFO, "\nThe Attribute Name is :  " + bookChange.getAttributeName());
                logger.log(Level.INFO, "\nThe Attribute Value is :  " + bookChange.getAttributeValue());
                Boolean isDataChanged = checkDataChanged(bookChange);
                if (isDataChanged) {
                    logger.log(Level.INFO, "\nThe Row Value is Changed for the ItemID: " + bookChange.getItemid() + "  DB  Value is :  " + bookChange);
                    bookList.add(bookChange);
                }
            }
            if (!bookList.isEmpty()) {
                DataBaseOperation.getInstance().dataBulkUpdate(bookList, booktableName,bookChangeTableName,attributeTableName);
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Caught exception while Processing the CSV File in processCSVFile.. " + FileName, ex);
        }
        finally {
            try {
                if (csvReader!=null) {
                    csvReader.close();
              }
            } catch (Exception ex) {
                logger.log(Level.WARNING, "In processCSVFile()...Caught exception while there closing the CVSReader Connection... " + ex);
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static ColumnPositionMappingStrategy setColumMapping() {
        ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
        strategy.setType(BookChanges.class);
        String[] columns = new String[]{"itemid", "attribute_name", "attribute_value"};
        strategy.setColumnMapping(columns);
        return strategy;
    }

    private static Boolean checkDataChanged(BookChanges bookChange) {
        try {
            Boolean isDataChanged = DataBaseOperation.getInstance().isDataChanged(bookChange, booktableName);
            return isDataChanged;
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Caught exception while Checking there are Any Changes have to be Reflected in the Database for the object " + bookChange, ex);
            return false;
        }
    }

    private static boolean copyFile(File srcFile, File destFile) throws Exception {
        boolean retType = false;
        InputStream inFile = null;
        OutputStream outFile = null;
        logger.log(Level.INFO, "Going to copy file.......From: " + srcFile + "  To: " + destFile);
        try {
            String parentLoc = destFile.getParent();
            if (parentLoc != null && !parentLoc.equals("") && !(new File(parentLoc)).exists()) {
                new File(parentLoc).mkdirs();
            }
            inFile = new FileInputStream(srcFile);
            outFile = new FileOutputStream(destFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = inFile.read(buf)) > 0) {
                outFile.write(buf, 0, len);
            }
            retType = true;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception while copying file.......", e);
        } finally {
            if (inFile != null) {
                inFile.close();
            }
            if (outFile != null) {
                outFile.close();
            }
        }
        return retType;
    }

    private static boolean deleteFile(String fileName) throws Exception {
        return new File(fileName).delete();
    }

}
