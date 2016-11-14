package smartx.multiview.collectors.CollectorsMain;

import smartx.multiview.collectors.flow.*;
import smartx.multiview.collectors.resource.*;

public class CustomCollectorsMain 
{
	private static String dbHost                       = "103.22.221.55";
	private static int    dbPort                       = 27017;
	private static String dbName                       = "smartxdb";
	private static String OPENSTACK_PASSWORD           = "secrete";
	private static String OPENSTACK_USER_ID            = "admin";
	private static String OPENSTACK_PROJECT_ID         = "demo";
	private static String OPENSTACK_ENDPOINT           = "http://103.22.221.51:5000/v2.0";
	private static String pboxMongoCollection          = "configuration-pbox-list";
	private static String vboxMongoCollection          = "resourcelevel-os-instance-detail";
	private static String vboxMongoCollectionRT        = "configuration-vbox-list";
	private static String pboxstatusMongoCollection    = "resourcelevel-ppath-rt";
	private static String ovsListMongoCollection       = "configuration-vswitch-list";
	private static String ovsstatusMongoCollection     = "configuration-vswitch-status";
	private static String flowConfigMongoCollection    = "flow-configuration-sdn-controller";
	private static String flowConfigMongoCollectionRT  = "flow-configuration-sdn-controller-rt";
	private static String flowStatsMongoCollection     = "flow-stats-sdn-controller";
	private static String flowStatsMongoCollectionRT   = "flow-stats-sdn-controller-rt";
	private static String devopscontrollers            = "103.22.221.152";
	private static String ControllerPassword           = "admin";
	private static String ControllerUser               = "admin";
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
