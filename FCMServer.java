package com.firebase.notifications;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

public class FCMServer implements ServletContextListener {

	private static final String DBNAME = "https://dgc-connect.firebaseio.com";
	private static final String SERVICE_ACCOUNT_KEY_FILE_PATH = "DGC_Connect-bfca9e40d132.json";
	private static final String NOTIFICATION_SERVER_URL="https://fcm.googleapis.com/fcm/send";
	private static final String API_KEY  = "AIzaSyBASg2VWvqn4N4WS-ow6VCrBhuPo_2XvC8";
	private static final String PUSH_MESSAGES_NODE = "push_messages/chat";
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
		logger.debug("Service Account Key JSON File Path : " + SERVICE_ACCOUNT_KEY_FILE_PATH );
		try {
			// Initialize the app with a service account, granting admin
			// privileges
			FirebaseOptions options = new FirebaseOptions.Builder().setDatabaseUrl(DBNAME)
					.setServiceAccount(new FileInputStream(SERVICE_ACCOUNT_KEY_FILE_PATH)).build();
			FirebaseApp.initializeApp(options);
			logger.debug("PUSH Messages Node : " + PUSH_MESSAGES_NODE );
			// As an admin, the app has access to read and write all data,
			// regardless of Security Rules
			DatabaseReference push_messages = FirebaseDatabase.getInstance().getReference(PUSH_MESSAGES_NODE);
			push_messages.addChildEventListener(new ChildEventListener() {
				
				public void onChildRemoved(DataSnapshot dataSnapshot) {
					Object document = dataSnapshot.getValue();
					System.out.println(document);
					System.out.println("=========ON Child Removed=======");

				}
				
				public void onChildChanged(DataSnapshot dataSnapshot, String arg1) {
					Object document = dataSnapshot.getValue();
					System.out.println(document);
					JSONObject json = new JSONObject(document.toString());
					
					logger.debug("message " + json.get("message"));
					logger.debug("device " + json.get("deviceid"));
					System.out.println("=========ON Child Changed=======");

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
								if(snapShot.getKey().equalsIgnoreCase("deviceid")){
									 deviceId = snapShot.getValue().toString();
									
								}
								if(snapShot.getKey().equals("message")){
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
	
	private void sendNotifications() {

		try {
			
			URL url = new URL(NOTIFICATION_SERVER_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			conn.setRequestProperty("Authorization", "key=" + API_KEY);

			conn.setDoOutput(true);

			String input = "{\"to\" : [\"Specify token you got from GCM\"],\"data\" : {\"message\": \"hi  welcome\"},}";

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
			os.close();
			
			int responseCode = conn.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + input);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());

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
