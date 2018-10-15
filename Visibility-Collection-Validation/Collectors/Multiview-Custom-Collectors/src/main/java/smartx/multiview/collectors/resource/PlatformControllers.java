/**
 * @author Muhammad Usman
 * @version 0.1
 */

package smartx.multiview.collectors.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.result.UpdateResult;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.StreamGobbler;
import smartx.multiview.DataLake.MongoDB_Connector;

public class PlatformControllers implements Runnable {
	private Thread thread;
	private String ThreadName = "Platform Controller Thread";

	private MongoDB_Connector mongoConnector;
	private FindIterable<Document> controllerList;

	private String controllerMongoCollection = "playground-controllers-list";
	private String controllerStatus = "", pingResult = "";

	protected Date timestamp;

	public PlatformControllers(MongoDB_Connector MongoConn){
		mongoConnector = MongoConn;
	}

	public void getControllerStatus() {
		//System.out.println("Running " + ThreadName);

		// Get List of Controllers
		controllerList = mongoConnector.getDataDB(controllerMongoCollection);

		controllerList.forEach(new Block<Document>() {
			String controllerIP;
			String controllerUser;
			String controllerPass;
			public void apply(final Document document) {
				pingResult     = "";
				controllerIP   = (String) document.get("controllerIP");
				controllerUser = (String) document.get("controllerUser");
				controllerPass = (String) document.get("controllerPass");
				String pingCmd = "ping -w 2 -c 2 " + controllerIP;
				//System.out.println("[INFO][Platform][Controller : " + controllerIP + " cmd: "+pingCmd);
				try {
					Runtime r = Runtime.getRuntime();
					Process p = r.exec(pingCmd);

					BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String inputLine;
					timestamp = new Date();
					while ((inputLine = in.readLine()) != null) {
						pingResult += inputLine;
					}

					if (pingResult.contains("icmp_seq") == true) {
						controllerStatus = "ORANGE";
						//System.out.println("[INFO][Platform][Controller : " + controllerIP + " cmd: "+controllerStatus);
					} else {
						controllerStatus = "RED";
					}
					in.close();
				} catch (IOException e) {
					System.out.println("[INFO][Controller][Box : " + controllerIP + " Failed ");
				}

				// Check Additional Services
				if (controllerStatus.equals("ORANGE")) {
					//System.out.println("[INFO][Platform][Controller : " + controllerIP + " cmd: "+controllerStatus);
					checkService(controllerIP, "ps aux | grep nova", controllerUser, controllerPass);
					}
				
				UpdateResult result = mongoConnector.getDbConnection().getCollection(controllerMongoCollection)
				.updateOne(new Document("controllerIP", controllerIP),
						new Document("$set", new Document("controllerStatus", controllerStatus)));
				System.out.println("[INFO][Platform][Controller : " + controllerIP + " status updated: "+result.getMatchedCount());
			}
		});
	}

	public void checkService(String serverIp, String command, String usernameString, String password) {
		try {
			Connection conn = new Connection(serverIp);
			conn.connect();
			boolean isAuthenticated = conn.authenticateWithPassword(usernameString, password);
			if (isAuthenticated == false)
				throw new IOException("Authentication failed.");
			ch.ethz.ssh2.Session sess = conn.openSession();
			sess.execCommand(command);
			InputStream stdout = new StreamGobbler(sess.getStdout());
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

			while (true) {
				String line = br.readLine();
				if (line == null)
					break;
				if (line != null) {
					if (line.contains("nova-api")) {
						controllerStatus = "GREEN";
						break;
					}
				}
			}
			br.close();
			sess.close();
			conn.close();
		} catch (IOException e) {
			System.out.println("[INFO][Platform][Controller : " + serverIp + " Failed");
		}
	}

	public void run() {
		while (true) {
			getControllerStatus();

			try {
				// Sleep For 30 Seconds
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void start() {
		System.out.println("Starting Controller Checking Thread");
		if (thread == null) {
			thread = new Thread(this, ThreadName);
			thread.start();
		}
	}
}
