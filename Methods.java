import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;

import net.proteanit.sql.DbUtils;

import java.awt.*;

public class Methods {
	//variables
	Connection c = null;
	Statement stmt = null;
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	SimpleDateFormat sqldf = new SimpleDateFormat("dd_MM_yyyy");
	String todaysDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
	
	//connect to the database
	public void connectDB() {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:BiometricDb.db");
			//JOptionPane.showMessageDialog(null, "Connected to DB! ");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error Connecting: " + e.getMessage());
		}
	}

	//disconnect from database
	public void closeConnection() {
		try {
			c.close();
			//JOptionPane.showMessageDialog(null, "Disconnected from DB");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error closeConnection: " + e.getMessage());
		}
	}
	
	//view current set of tables in database
	public DefaultTableModel viewTables(JTable table) {
		DefaultTableModel model =  new DefaultTableModel();
			try {
				this.stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND tbl_name != 'sqlite_sequence' AND tbl_name NOT LIKE 'IMAGES%'");
				ResultSetMetaData rsMD = rs.getMetaData();
				int colCount = rsMD.getColumnCount();
				Object[] obj = new Object[colCount];
				
				for(int i = 0; i<colCount; i++) {
					obj[i] = rsMD.getColumnLabel(i+1);
				}
				model.setColumnIdentifiers(obj);
				
				while(rs.next()) {
					Object[] data = new Object[model.getColumnCount()];
					for(int i = 0; i < model.getColumnCount(); i++) {
						data[i] = rs.getObject(i+1);
					}
					model.addRow(data);
				}
				
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Error getData: " + e.getMessage());
			}
			return model;
		}
	
	//create a biometric response table from a given file path
	public void createBiometricTable(String inFolderPath) {

			try {
				this.stmt = c.createStatement();
				
				//read folder into an array
				File folder = new File(inFolderPath);
				File[] fileArray = folder.listFiles();
				
				//loop through each file in folder
				for(File f:fileArray) {
					//extracts file name from file
					String fileName = f.getName();
					
					//ignore files that are not the following
					if(!fileName.equals("EDA.csv") && !fileName.equals("HR.csv") && !fileName.equals("TEMP.csv")) {
						System.out.println("Found: "+ fileName +", continue loop");
						continue;
					}
					
					//buffer to read csv
					BufferedReader reader = new BufferedReader(new FileReader(f));
					String line = reader.readLine();
					
					//fills array list with csv contents
					List<Double> lines = new ArrayList<>();
					while(line != null) {
						lines.add(Double.parseDouble(line));
						line = reader.readLine();
					}
					reader.close();
					
					//set up timestamps and lists based on the timestamp in file 
					Double unix = lines.get(0);
					Date date = new Date((long) (unix*1000));
					String sqlDate = sqldf.format(date);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					List<Double> arraySum = new ArrayList<>();
					double sum;
					
					//create table and upload contents based on file
					switch(fileName) {
						case "EDA.csv":
							//create sql table
							stmt.execute("drop table if exists `EDA_" + sqlDate + "`;");
							stmt.execute("CREATE TABLE IF NOT EXISTS `EDA_" + sqlDate + "` \r\n" + 
									" (`ID`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\r\n" + 
									"	`TimeStamp`	NUMERIC,\r\n" + 
									"	`EDA`	NUMERIC );");
							
							//get average of every 4 electrodermal readings and add to new list
							for(int i = 2; i < lines.size(); i = i+4) {
								sum = lines.get(i) + lines.get(i + 1) + lines.get(i + 2) + lines.get(i + 3);
								sum = sum/4;
								arraySum.add(sum);
							}
							
							//upload file contents
							for(int j = 0; j < arraySum.size(); j++) {
								stmt.execute("insert into EDA_" + sqlDate + " (TimeStamp, EDA) values ('"+ sdf.format(calendar.getTime()) +"', '"+ arraySum.get(j) +"');");
								calendar.add(Calendar.SECOND, 1);
							}
							break;
						case "HR.csv":
							//create sql table
							stmt.execute("drop table if exists `HR_" + sqlDate + "`;");
							stmt.execute("CREATE TABLE IF NOT EXISTS `HR_" + sqlDate + "` \r\n" + 
									" (`ID`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\r\n" + 
									"	`TimeStamp`	NUMERIC,\r\n" + 
									"	`HR`	NUMERIC );");
							
							//upload file contents
							for(int i = 2; i < lines.size(); i++) {
								stmt.execute("insert into HR_" + sqlDate + " (TimeStamp, HR) values ('"+ sdf.format(calendar.getTime()) +"', '"+ lines.get(i) +"');");
								calendar.add(Calendar.SECOND, 1);
							}
							
							break;
						case "TEMP.csv":
							//create sql table
							stmt.execute("drop table if exists `TEMP_" + sqlDate + "`;");
							stmt.execute("CREATE TABLE IF NOT EXISTS `TEMP_" + sqlDate + "` \r\n" + 
									" (`ID`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\r\n" + 
									"	`TimeStamp`	NUMERIC,\r\n" + 
									"	`TEMP`	NUMERIC );");
							
							//get average of every 4 temperatures and add to new list
							for(int i = 2; i < lines.size(); i = i+4) {
								sum = lines.get(i) + lines.get(i + 1) + lines.get(i + 2) + lines.get(i + 3);
								sum = sum/4;
								arraySum.add(sum);
							}
							
							//upload file contents
							for(int j = 0; j < arraySum.size(); j++) {
								stmt.execute("insert into TEMP_" + sqlDate + " (TimeStamp, TEMP) values ('"+ sdf.format(calendar.getTime()) +"', '"+ arraySum.get(j) +"');");
								calendar.add(Calendar.SECOND, 1);
							}
							break;
						default:
							JOptionPane.showMessageDialog(null, "Error in creating table. Please check that this is the correct file: " + fileName);
							continue;
				}
			}
			JOptionPane.showMessageDialog(null, "All files have been uploaded");

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error createBiometricTable: " + e.getMessage());
		}
	}

	//create a table of image paths from a folder path
	public void createImageTable(String inFolderPath) {
		try {
			
			File folder = new File(inFolderPath);
			File[] fileArray = folder.listFiles();
			String timeStamp = sqldf.format(fileArray[0].lastModified());
			
			this.stmt = c.createStatement();
			
			stmt.execute("CREATE TABLE `IMAGES_" + timeStamp + "` \r\n" + 
					" (`ID`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\r\n" + 
					"	`TimeStamp`	NUMERIC,\r\n" + 
					"	`ImagePath`	TEXT );");
			JOptionPane.showMessageDialog(null, "Created table successfully! Please wait until upload has completed");
			
			
			
			for(File f:fileArray) {
				stmt.execute("insert into IMAGES_" + timeStamp + " (TimeStamp, ImagePath) values ('"+ sdf.format(f.lastModified()) +"', '"+ f +"');");
			}
			JOptionPane.showMessageDialog(null, "Upload completed successfully!");
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error createImageTable: " + e.getMessage());
		}
		
		
	}
	
	//generate a csv file with events
	public void generateEvents(int quantity, String event, String folderPath) {
		try {
			this.stmt = c.createStatement();
			
			//split event into name and date
			String[] eventInfo = event.split("_", 2);
			String eventName = eventInfo[0];
			String eventDate = "_"+eventInfo[1];
			
			//create folder and set up files
			String eventFolder = folderPath + "\\events_" + sqldf.format(new Date());
			String trueDataCSV = eventFolder + "\\trueData.csv";
			//String quizCSV = eventFolder + "\\Quiz.csv";
			new File(folderPath + "\\events_" + sqldf.format(new Date())).mkdirs();			
			File fileTrueData = new File(trueDataCSV);
			Files.deleteIfExists(fileTrueData.toPath());
			
			//set up file writer and result sets
			FileWriter fw = new FileWriter(trueDataCSV, true);
		    StringBuilder sb = new StringBuilder();
		    ResultSet rsBA = null;
		    ResultSet rsAA = null;
		    ResultSet rsMax = null;
		    ResultSet rsMin = null;
		    
		    List<String> imagePath = new ArrayList<>();
		    List<File> img = new ArrayList<>();

		    
		    //store each query in a string using event variables
		    String BA = "select IMG.ImagePath, IMG.TimeStamp as ImageTime, BIO."+eventName+", BIO.TimeStamp as BioTime\r\n"+
		    		"from "+event+" BIO\r\n"+
		    		"inner join IMAGES"+eventDate+" IMG on IMG.TimeStamp = BIO.TimeStamp\r\n"+
		    		"where BIO."+eventName+" < (select AVG("+event+"."+eventName+") from "+event+")\r\n"+
		    		"order by random()\r\n"+
		    		"limit "+quantity+"";
		    
		    String AA = "select IMG.ImagePath, IMG.TimeStamp as ImageTime, BIO."+eventName+", BIO.TimeStamp as BioTime\r\n"+
		    		"from "+event+" BIO\r\n"+
		    		"inner join IMAGES"+eventDate+" IMG on IMG.TimeStamp = BIO.TimeStamp\r\n"+
		    		"where BIO."+eventName+" > (select AVG("+event+"."+eventName+") from "+event+")\r\n"+
		    		"order by random()\r\n"+
		    		"limit "+quantity+"";
		    
		    String max = "select IMG.ImagePath, IMG.TimeStamp as ImageTime, BIO."+eventName+", BIO.TimeStamp\r\n"+
		    		"from "+event+" BIO\r\n"+
		    		"inner join IMAGES"+eventDate+" IMG on IMG.TimeStamp = BIO.TimeStamp\r\n"+
		    		"order by BIO."+eventName+" desc\r\n"+
		    		"limit "+quantity+"";
		    
		    String min = "select IMG.ImagePath, IMG.TimeStamp as ImageTime, BIO."+eventName+", BIO.TimeStamp\r\n"+
		    		"from "+event+" BIO\r\n"+
		    		"inner join IMAGES"+eventDate+" IMG on IMG.TimeStamp = BIO.TimeStamp\r\n"+
		    		"order by BIO."+eventName+" asc\r\n"+
		    		"limit "+quantity+"";
		    
		    //execute queries and store results in seperate result sets
		    PreparedStatement psBA = c.prepareStatement(BA);
		    PreparedStatement psAA = c.prepareStatement(AA);
		    PreparedStatement psMax = c.prepareStatement(max);
		    PreparedStatement psMin = c.prepareStatement(min);
		    rsBA = psBA.executeQuery();
		    rsAA = psAA.executeQuery();
		    rsMin = psMax.executeQuery();
		    rsMax = psMin.executeQuery();
		    
		    //write result set contents to CSV file
		    while (rsBA.next() ) {
		    	//img.add(new File(rsBA.getString("imagePath")));
				sb.append(rsBA.getString("ImagePath"));
				sb.append(",");
				sb.append(rsBA.getString("ImageTime"));
				sb.append(",");
				sb.append(rsBA.getString(eventName));
				sb.append(",");
				sb.append("Below Average");
				sb.append("\r\n");
		    }
		    while (rsAA.next() ) {
		    	//img.add(new File(rsBA.getString("imagePath")));
				sb.append(rsAA.getString("ImagePath"));
				sb.append(",");
				sb.append(rsAA.getString("ImageTime"));
				sb.append(",");
				sb.append(rsAA.getString(eventName));
				sb.append(",");
				sb.append("Above Average");
				sb.append("\r\n");
		    }
		    while (rsMax.next() ) {
		    	//img.add(new File(rsBA.getString("imagePath")));
				sb.append(rsMax.getString("ImagePath"));
				sb.append(",");
				sb.append(rsMax.getString("ImageTime"));
				sb.append(",");
				sb.append(rsMax.getString(eventName));
				sb.append(",");
				sb.append("Maximum");
				sb.append("\r\n");
		    }
		    while (rsMin.next() ) {
		    	//img.add(new File(rsBA.getString("imagePath")));
				sb.append(rsMin.getString("ImagePath"));
				sb.append(",");
				sb.append(rsMin.getString("ImageTime"));
				sb.append(",");
				sb.append(rsMin.getString(eventName));
				sb.append(",");
				sb.append("Minimum");
				sb.append("\r\n");
		    }
		    fw.write(sb.toString());;
		    fw.close();
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error generateEvents: " + e.getMessage());
		}

	}
	
	
}
