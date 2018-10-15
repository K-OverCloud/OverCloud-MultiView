/**
 * @author Muhammad Usman
 * @version 0.1
 */

package smartx.multiview.collectors.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.result.UpdateResult;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.StreamGobbler;
import smartx.multiview.DataLake.MongoDB_Connector;

@SuppressWarnings("unused")
public class PingStatusUpdateClass implements Runnable {
	private String box = "", activeVM, m_ip = "", d_ip = "";
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String m_status, m_status_new, d_status;
	private MongoDB_Connector mongoConnector;
	private FindIterable<Document> pBoxList;
	private String pboxMongoCollection, pboxstatusMongoCollectionRT;
	private FindIterable<Document> pBoxStatus;
	private String SmartXBox_USER, SmartXBox_PASSWORD;
	private Thread thread;

	private String ThreadName = "pBox Status Thread";
	private Date timestamp;

	// private static Logger logger =
	// Logger.getLogger(PingStatusUpdateClass.class.getName());

	public PingStatusUpdateClass(String boxUser, String boxPassword, MongoDB_Connector MongoConn, String pbox,
			String pboxstatus) {
		SmartXBox_USER = boxUser;
		SmartXBox_PASSWORD = boxPassword;
		mongoConnector = MongoConn;
		pboxMongoCollection = pbox;
		pboxstatusMongoCollectionRT = pboxstatus;
	}

	public String getBoxStatus(String serverMgmtIp, String serverDataIp, String command, String usernameString,
			String password) {
		String InterfaceStatus = null;
		try {
			Connection conn = new Connection(serverMgmtIp);
			conn.connect();
			boolean isAuthenticated = conn.authenticateWithPassword(usernameString, password);
			if (isAuthenticated == false)
				throw new IOException("Authentication failed.");
			ch.ethz.ssh2.Session sess = conn.openSession();
			sess.execCommand(command);
			InputStream stdout = new StreamGobbler(sess.getStdout());
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

			if (br.readLine() != null) {
				InterfaceStatus = "GREEN";
				ch.ethz.ssh2.Session sess2 = conn.openSession();
				sess2.execCommand(
						"ovs-ofctl show brcap | grep ovs_vxlan | cut -f 1 -d : | cut -f 2 -d '(' | cut -f 1 -d ')'");
				sess.close();
				conn.close();
			} else {
				InterfaceStatus = "RED";
				sess.close();
				conn.close();
			}
			br.close();
		} catch (IOException e) {
			System.out.println("[INFO][PING][MVC][Box : " + serverMgmtIp + " Failed");
			e.printStackTrace(System.err);
		}

		return InterfaceStatus;
	}

	public String getPingStatus(String BoxIP, String command, String usernameString, String password) {
		String pingResult = "", inputLine, interfaceStatus = null;

		try {
			Connection conn = new Connection(BoxIP);
			conn.connect();
			boolean isAuthenticated = conn.authenticateWithPassword(usernameString, password);
			if (isAuthenticated == false)
				throw new IOException("Authentication failed.");
			ch.ethz.ssh2.Session sess = conn.openSession();
			sess.execCommand(command);
			InputStream stdout = new StreamGobbler(sess.getStdout());
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

			while ((inputLine = br.readLine()) != null) {
				pingResult += inputLine;
			}

			if (pingResult.contains("Host seems down") == true) {
				interfaceStatus = "RED";
				System.out.println(
						"[" + dateFormat.format(timestamp) + "][INFO][PING][Box: " + BoxIP + " Data Status: Down]");
			} else {
				interfaceStatus = "GREEN";
				System.out.println(
						"[" + dateFormat.format(timestamp) + "][INFO][PING][Box: " + BoxIP + " Data Status: Up]");
			}
			br.close();

		} catch (IOException e) {
			System.out.println("[INFO][PING][Box : " + BoxIP + " Failed " + e);
		}
		return interfaceStatus;
	}

	public void run() {
		while (true) {
			update_status();

			try {
				// Sleep For 1 Minute
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void start() {
		System.out.println("Starting pBox Status Update Thread");
		if (thread == null) {
			thread = new Thread(this, ThreadName);
			thread.start();
		}

	}

	public void update_status() {
		timestamp = new Date();

		pBoxList = mongoConnector.getDataDB(pboxMongoCollection);
		// pBoxList = db.getCollection(pboxMongoCollection).find(new Document("$or",
		// asList(new Document("type", BoxType[0]),new Document("type", BoxType[1]))));

		pBoxList.forEach(new Block<Document>() {
			public void apply(final Document document) {
				box = (String) document.get("boxID");
				m_ip = (String) document.get("management_ip");
				d_ip = (String) document.get("data_ip");
				m_status = (String) document.get("management_ip_status");

				// Get Management Plane Status & Update pBox Status Collection
				pBoxStatus = mongoConnector.getDataDB(pboxstatusMongoCollectionRT, "destination", m_ip);
				pBoxStatus.forEach(new Block<Document>() {
					public void apply(final Document document2) {
						m_status_new = document2.get("status").toString().toUpperCase();

						// Get Data Plane Status
						if (m_status_new.equalsIgnoreCase("UP")) {
							m_status_new = "GREEN";
							d_status = getPingStatus(m_ip, "nmap -sP -R 10.0.0.9", SmartXBox_USER, SmartXBox_PASSWORD);
						} else {
							m_status_new = "RED";
							d_status = "RED";
						}

						UpdateResult result = mongoConnector.getDbConnection().getCollection(pboxMongoCollection)
								.updateOne(new Document("management_ip", m_ip),
										new Document("$set", new Document("management_ip_status", m_status_new)
												.append("data_ip_status", d_status).append("active_ovs_vm", activeVM)));
						System.out.println("[" + dateFormat.format(timestamp) + "][INFO][PING][MVC][Box: " + m_ip
								+ " Management Status: " + m_status_new + " Data Status: " + d_status
								+ " Records Updated :" + result.getModifiedCount() + "]");
						activeVM = null;
					}
				});
			}
		});
	}
}
