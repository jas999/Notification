package com.firebase.notifications;

import java.io.FileInputStream;
import java.util.Iterator;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
/**
 * This class will be initialized on startup. This is working as a Listener, to listen any changes on Firebase Database
 * @author Jaspreet Singh
 *
 */
public class FCMServer implements ServletContextListener {

	private static final String DBNAME = "https://dgc-connect.firebaseio.com";
	private String SERVICE_ACCOUNT_KEY_FILE_PATH = "DGC_Connect-bfca9e40d132.json";	
	private String PUSH_MESSAGES_NODE = "push_messages/chat";
	private String MESSAGE_NODE = "message";
	private String DEVICE_NODE = "deviceid";
	private static final Logger logger = Logger.getLogger(FCMServer.class);
	
	public void contextDestroyed(ServletContextEvent servletContextListener) {	
		
	}

	public void contextInitialized(ServletContextEvent servletContextListener) {
		logger.debug("==========Initializing Firebase============");
		initializeFirebaseDatabase();		
	}
	
	public void initializeFirebaseDatabase(){
		logger.debug("==========Goint to Initializing Firebase Server============");
		logger.debug("Firebase database : " +  DBNAME);
		try {
			
			SERVICE_ACCOUNT_KEY_FILE_PATH = PropertyFileLoader.getPropertiesInstance().getProperty("fcm.JSON_FILE_PATH");
			logger.debug("SERVICE_ACCOUNT_KEY_FILE_PATH : " + SERVICE_ACCOUNT_KEY_FILE_PATH);
			// Initialize the app with a service account, granting admin
			// privileges
			FirebaseOptions options = new FirebaseOptions.Builder().setDatabaseUrl(DBNAME)
					.setServiceAccount(new FileInputStream(SERVICE_ACCOUNT_KEY_FILE_PATH)).build();
			FirebaseApp.initializeApp(options);
			
			
			PUSH_MESSAGES_NODE = PropertyFileLoader.getPropertiesInstance().getProperty("fcm.PUSH_MESSAGES_NODE");
			
			MESSAGE_NODE = PropertyFileLoader.getPropertiesInstance().getProperty("fcm.MESSAGE_NODE");
			DEVICE_NODE = PropertyFileLoader.getPropertiesInstance().getProperty("fcm.DEVICE_NODE");
			logger.debug("PUSH Messages Node : " + PUSH_MESSAGES_NODE );
			// As an admin, the app has access to read and write all data,
			// regardless of Security Rules
			DatabaseReference push_messages = FirebaseDatabase.getInstance().getReference(PUSH_MESSAGES_NODE);
			push_messages.addChildEventListener(new ChildEventListener() {
				
				public void onChildRemoved(DataSnapshot dataSnapshot) {
					Object document = dataSnapshot.getValue();
					logger.debug(document);
					logger.debug("=========ON Child Removed=======");

				}
				
				public void onChildChanged(DataSnapshot dataSnapshot, String arg1) {
					Object document = dataSnapshot.getValue();
					System.out.println(document);
					JSONObject json = new JSONObject(document.toString());
					
					logger.debug("message " + json.get(MESSAGE_NODE));
					logger.debug("device " + json.get(DEVICE_NODE));
					logger.debug("=========ON Child Changed=======");

				}
				
				public void onChildAdded(DataSnapshot dataSnapshot, String arg1) {
					logger.debug("====Child is added is the Node ===" );
					try {
						 
						String deviceId = "";
						String message = "";
						if(dataSnapshot.hasChildren()){
							Iterator<DataSnapshot> itr = dataSnapshot.getChildren().iterator();
							while(itr.hasNext()){
								DataSnapshot snapShot = itr.next();
								logger.debug("ITR Key : " + snapShot.getKey());
								logger.debug("ITR Value : " + snapShot.getValue());
								if(snapShot.getKey().equalsIgnoreCase(DEVICE_NODE)){
									 deviceId = snapShot.getValue().toString();
									
								}
								if(snapShot.getKey().equals(MESSAGE_NODE)){
									message = snapShot.getValue().toString();
								}
							}
							FCMNotifications notification = new FCMNotifications();							
							String response = notification.sendNotifications(deviceId, message);
							if(response != null && !response.equals("")){
								Integer success = processResponse(response);
								logger.debug("Success  " + success);
								logger.debug(success == 1 ? "Message Sent." : "Failed");
								dataSnapshot.getRef().removeValue();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				public void onCancelled(DatabaseError arg0) {
					// TODO Auto-generated method stub
					
				}

				public void onChildMoved(DataSnapshot arg0, String arg1) {
					// TODO Auto-generated method stub
					
				}
			});
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args){
		//FCMServer server = new FCMServer();
		//server.sendNotifications();
		
		String response = "{\"multicast_id\":8540671014123456377,\"success\":1,\"failure\":0,\"canonical_ids\":0,\"results\":[{\"message_id\":\"0:1476105375457652%92851f0ff9fd7ecd\"}]}";
		
		JSONObject json = new JSONObject(response);
		System.out.println(json.get("success"));
	}
	
	/**
	 * To process the response returned from the server
	 * @param response
	 * @return
	 */
	private Integer processResponse(String response){
		logger.debug("Going to processResponse" + response);
		Integer success = 0 ;
		try {
			JSONObject json = new JSONObject(response);
			success = (Integer) json.get("success");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return success;
		
	}

}
