package com.firebase.notifications;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FCMServer implements ServletContextListener {

	private static final String DBNAME = "https://dgc-connect.firebaseio.com/ ";
	private static final String SERVICE_ACCOUNT_KEY_FILE_PATH = "DGC_Connect-bfca9e40d132.json";
	private static final String NOTIFICATION_SERVER_URL="https://fcm.googleapis.com/fcm/send";
	private static final String API_KEY  = "AIzaSyAR2PSHpot_YyP-ePRR1ckY6PE_4J6gMAA";
	private static final String PUSH_MESSAGES_NODE = "push_messages/chat";
	private static final Logger logger = Logger.getLogger(FCMServer.class);
	
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void contextInitialized(ServletContextEvent servletContextListener) {
		logger.debug("==========Initializing Firebase============");
		System.out.println("Going to initialise");
		initializeFirebaseDatabase();
		
	}
	
	public void initializeFirebaseDatabase(){
		
		try {
			// Initialize the app with a service account, granting admin
			// privileges
			FirebaseOptions options = new FirebaseOptions.Builder().setDatabaseUrl(DBNAME)
					.setServiceAccount(new FileInputStream(SERVICE_ACCOUNT_KEY_FILE_PATH)).build();
			FirebaseApp.initializeApp(options);

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
					System.out.println("=========ON Child Changed=======");

				}
				
				public void onChildAdded(DataSnapshot dataSnapshot, String arg1) {
					Object document = dataSnapshot.getValue();
					System.out.println(document);
					System.out.println("=========ON Child Added=======");
				}
				public void onCancelled(DatabaseError arg0) {
					// TODO Auto-generated method stub
					
				}
				public void onChildMoved(DataSnapshot arg0, String arg1) {
					// TODO Auto-generated method stub
					
				}
				
			});
						
		} catch (Exception e) {
			
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
	
	

}
