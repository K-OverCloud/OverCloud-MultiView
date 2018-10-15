/**
 * @author Muhammad Usman
 * @version 0.1
 */

package smartx.multiview.collectors.CollectorsMain;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import smartx.multiview.collectors.resource.*;
import smartx.multiview.DataLake.*;

public class CustomCollectorsMain 
{
	private String VISIBILITY_CENTER;
	private String MONGO_DB_HOST;
	private int    MONGO_DB_PORT;
	private String MONGO_DB_DATABASE;
	private String devopscontrollers;
	private String ControllerPassword;
	private String ControllerUser;
	private String SmartXBox_USER;
	private String SmartXBox_PASSWORD;
	private String pboxMongoCollection                  = "pbox-list";
	private String vboxMongoCollection                  = "vm-instance-list-history";
	private String vboxMongoCollectionRT                = "vm-instance-list";
	private String pboxstatusMongoCollection            = "resourcelevel-ppath";
	private String pboxstatusMongoCollectionRT          = "resourcelevel-ppath-rt";
	private String ovsListMongoCollection               = "configuration-vswitch-list";
	private String ovsstatusMongoCollection             = "configuration-vswitch-status";
	private String flowConfigMongoCollection            = "flow-configuration-sdn-controller";
	private String flowConfigMongoCollectionRT          = "flow-configuration-sdn-controller-rt";
	private String flowStatsMongoCollection             = "flow-stats-sdn-controller";
	private String flowStatsMongoCollectionRT           = "flow-stats-sdn-controller-rt";
	private String flowConfigOpenStackMongoCollection   = "flow-stats-openstack-bridges";
	private String flowConfigOpenStackMongoCollectionRT = "flow-stats-openstack-bridges-rt";
		
	public String getVISIBILITY_CENTER() {
		return VISIBILITY_CENTER;
	}
	
	public String getMONGO_DB_HOST() {
		return MONGO_DB_HOST;
	}

	public int getMONGO_DB_PORT() {
		return MONGO_DB_PORT;
	}

	public String getMONGO_DB_DATABASE() {
		return MONGO_DB_DATABASE;
	}

	public String getDevopscontrollers() {
		return devopscontrollers;
	}

	public String getControllerPassword() {
		return ControllerPassword;
	}

	public String getControllerUser() {
		return ControllerUser;
	}

	public String getSmartXBox_USER() {
		return SmartXBox_USER;
	}

	public String getSmartXBox_PASSWORD() {
		return SmartXBox_PASSWORD;
	}

	public String getPboxMongoCollection() {
		return pboxMongoCollection;
	}

	public String getVboxMongoCollection() {
		return vboxMongoCollection;
	}

	public String getVboxMongoCollectionRT() {
		return vboxMongoCollectionRT;
	}

	public String getPboxstatusMongoCollection() {
		return pboxstatusMongoCollection;
	}

	public String getPboxstatusMongoCollectionRT() {
		return pboxstatusMongoCollectionRT;
	}

	public String getOvsListMongoCollection() {
		return ovsListMongoCollection;
	}

	public String getOvsstatusMongoCollection() {
		return ovsstatusMongoCollection;
	}

	public String getFlowConfigMongoCollection() {
		return flowConfigMongoCollection;
	}

	public String getFlowConfigMongoCollectionRT() {
		return flowConfigMongoCollectionRT;
	}

	public String getFlowStatsMongoCollection() {
		return flowStatsMongoCollection;
	}

	public String getFlowStatsMongoCollectionRT() {
		return flowStatsMongoCollectionRT;
	}

	public String getFlowConfigOpenStackMongoCollection() {
		return flowConfigOpenStackMongoCollection;
	}

	public String getFlowConfigOpenStackMongoCollectionRT() {
		return flowConfigOpenStackMongoCollectionRT;
	}

	public void getProperties(){
		Properties prop = new Properties();
    	InputStream input = null;
    	try {

    		input = new FileInputStream("../MultiView-Configurations/Custom_Collectors.properties");

    		// load a properties file
    		prop.load(input);
    		//Visibility Center IP
    		VISIBILITY_CENTER    = prop.getProperty("VISIBILITY_CENTER");
    		
    		//MongoDB Properties
    		MONGO_DB_HOST        = prop.getProperty("MONGODB_HOST");
    		MONGO_DB_PORT        = Integer.parseInt(prop.getProperty("MONGODB_PORT"));
    		MONGO_DB_DATABASE    = prop.getProperty("MONGODB_DATABASE");
    		
    		//OpenDayLight Properties
    		devopscontrollers    = prop.getProperty("devopscontrollers");
    		ControllerUser       = prop.getProperty("CONTROLLER_USER");
    		ControllerPassword   = prop.getProperty("CONTROLLER_PASSWORD");
    		
    		//SmartX Box Properties
    		SmartXBox_USER       = prop.getProperty("SmartXBox_USER");
    		SmartXBox_PASSWORD   = prop.getProperty("SmartXBox_PASSWORD");
    		
    		} catch (IOException ex) {
    		 ex.printStackTrace();
    	 }	finally {
    		 if (input != null) {
    			 try {
    				 input.close();
    			 } catch (IOException e) {
				e.printStackTrace();
    			 }
    		 }
    	 }
	}
	
	public static void main( String[] args )
    {
		CustomCollectorsMain ccMain = new CustomCollectorsMain();
		ccMain.getProperties();
		
		MongoDB_Connector MongoConnector = new MongoDB_Connector();
		MongoConnector.setDbConnection(ccMain.MONGO_DB_HOST, ccMain.MONGO_DB_PORT, ccMain.MONGO_DB_DATABASE);
		
		//Start Visibility Collection for Platform Controllers
		PlatformControllers platformcontroller = new PlatformControllers(MongoConnector);
		platformcontroller.start();
		    	
    	//Start Visibility Data Collection for Ping Data from SmartX Boxes
    	PingStatusCollectClass pingStatusCollect = new PingStatusCollectClass(ccMain.VISIBILITY_CENTER, MongoConnector, ccMain.pboxMongoCollection, ccMain.pboxstatusMongoCollection, ccMain.pboxstatusMongoCollectionRT);
    	pingStatusCollect.start();
    	try {
			TimeUnit.SECONDS.sleep(15);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//Update Instant Visibility Collection for Box status using Ping Data
    	PingStatusUpdateClass pingStatusUpdate = new PingStatusUpdateClass(ccMain.SmartXBox_USER, ccMain.SmartXBox_PASSWORD, MongoConnector, ccMain.pboxMongoCollection, ccMain.pboxstatusMongoCollectionRT);
    	pingStatusUpdate.start(); 
        try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        //Start Visibility Collection for VM's Data
        InstaceStatusNovaClass instanceNovaStatus = new InstaceStatusNovaClass(ccMain.SmartXBox_USER, ccMain.SmartXBox_PASSWORD, ccMain.MONGO_DB_HOST, ccMain.MONGO_DB_PORT, ccMain.MONGO_DB_DATABASE, ccMain.vboxMongoCollection, ccMain.vboxMongoCollectionRT);
        instanceNovaStatus.start();
        try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        //Start Instant Visibility Collection for OVS Data
        ovsBridgeStatusClass bridgeStatus  = new ovsBridgeStatusClass(ccMain.SmartXBox_USER, ccMain.SmartXBox_PASSWORD, ccMain.MONGO_DB_HOST, ccMain.MONGO_DB_PORT, ccMain.MONGO_DB_DATABASE, ccMain.pboxMongoCollection, ccMain.ovsListMongoCollection, ccMain.ovsstatusMongoCollection);
        bridgeStatus.start();
        try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
