package smartx.multiview.collectors.CollectorsMain;

import smartx.multiview.collectors.flow.*;
import smartx.multiview.collectors.resource.*;

public class CustomCollectorsMain 
{
	private static String dbHost                       = "";
	private static int    dbPort                       = ;
	private static String dbName                       = "";
	private static String OPENSTACK_PASSWORD           = "";
	private static String OPENSTACK_USER_ID            = "";
	private static String OPENSTACK_PROJECT_ID         = "";
	private static String OPENSTACK_ENDPOINT           = "";
	private static String pboxMongoCollection          = "";
	private static String vboxMongoCollection          = "";
	private static String vboxMongoCollectionRT        = "";
	private static String pboxstatusMongoCollection    = "";
	private static String ovsListMongoCollection       = "";
	private static String ovsstatusMongoCollection     = "";
	private static String flowConfigMongoCollection    = "";
	private static String flowConfigMongoCollectionRT  = "";
	private static String flowStatsMongoCollection     = "";
	private static String flowStatsMongoCollectionRT   = "";
	private static String devopscontrollers            = "";
	private static String ControllerPassword           = "";
	private static String ControllerUser               = "";
	private static String [] BoxType = {"B**", "C**"};
			
    public static void main( String[] args )
    {
        PingStatusClass pingStatus         = new PingStatusClass(dbHost, dbPort, dbName, pboxMongoCollection, pboxstatusMongoCollection, BoxType);
        pingStatus.start(); 
        
        InstaceStatusNovaClass instanceNovaStatus = new InstaceStatusNovaClass(dbHost, dbPort, dbName, OPENSTACK_USER_ID, OPENSTACK_PASSWORD, OPENSTACK_PROJECT_ID, OPENSTACK_ENDPOINT, vboxMongoCollection, vboxMongoCollectionRT);
        instanceNovaStatus.start();
        
        //InstanceStatusClass instanceStatus = new InstanceStatusClass(dbHost, dbPort, dbName, pboxMongoCollection, vboxMongoCollection, vboxMongoCollectionRT);
        //instanceStatus.start();
        
        ovsBridgeStatusClass bridgeStatus  = new ovsBridgeStatusClass(dbHost, dbPort, dbName, pboxMongoCollection, ovsListMongoCollection, ovsstatusMongoCollection, BoxType);
        bridgeStatus.start();
        
        SDNControllerStatusClass sdnStatus = new SDNControllerStatusClass(dbHost, dbPort, dbName, flowConfigMongoCollection, flowConfigMongoCollectionRT, devopscontrollers, ControllerUser, ControllerPassword);
        sdnStatus.start();
        
        SDNControllerStatsClass sdnStats = new SDNControllerStatsClass(dbHost, dbPort, dbName, flowStatsMongoCollection, flowStatsMongoCollectionRT, devopscontrollers, ControllerUser, ControllerPassword);
        sdnStats.start();
    }
}
