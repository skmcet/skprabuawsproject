Load the NetBeans Project -> "skprabuaws" into NetBeans

We have bundled the thirdparty opensource CVS Reader opencsv.jar in the library folder of the "skprabuaws"

Create a database named "dbtestdb" and Run the Create Table queries and the insert table queries in the folder "TestDataSet"

Change the "com.aws.action.LargeScaleConfigurableAction.notificationLogsFolder" to the place where you want to display the notification Logs 

Change the "com.aws.action.LargeScaleConfigurableAction.csvdirPath" to the place where you want to keep the CsvFiles 

Change "com.aws.action.LargeScaleConfigurableAction.processedCsvDirPath" to the place where you want to keep the Processed CsvFiles

Change the Datbase Parameter values in the File com.aws.db.DatabaseConnection and 
com.test.model.DatabaseConnection


Get all attributes of an API:
 
Run as java com.aws.action.LargeScaleConfigurableAction 135789