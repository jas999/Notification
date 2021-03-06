package com.firebase.notifications;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;


public class FCMNotifications {

	private String NOTIFICATION_SERVER_URL="https://fcm.googleapis.com/fcm/send";
	private String API_KEY  = "AIzaSyBASg2VWvqn4N4WS-ow6VCrBhuPo_2XvC8";
	private static final Logger logger = Logger.getLogger(FCMNotifications.class);
	
	private void sendNotifications() {

		try {
			String device = "cQDxGvQ7CiA:APA91bGSFRjcEvcfrh1gezEOrZcJsYJEYQIg8svNi-HGUrbF6irSlFA4rvRhnnWAKnMYqeHeXK8jU1n2He1D5BBneYn21xyx_aalA753iTYuBvxj-8Yaw0fe5ZSPF5whS5ShKMbiEMUn";
			URL url = new URL(NOTIFICATION_SERVER_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			conn.setRequestProperty("Authorization", "key=" + API_KEY);

			conn.setDoOutput(true);
			JSONObject obj = new JSONObject();
			obj.put("to", device);
			obj.put("data", new JSONObject().put("message", "Hello Jasmeet"));
			System.out.println(obj.toString());
			String input = obj.toString();
			//String input = "{\"to\" : [\"\"],\"data\" : {\"message\": \"hi  welcome\"}}";
			System.out.println(input);
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
	
	/**
	 * This method will send the notifications to the device.
	 * @param deviceId
	 * @param message
	 * @return
	 * @throws IOException
	 */
	public String sendNotifications(String deviceId,String message) throws IOException {
		logger.debug("Inside Method Send Notification.");
		logger.debug("Going to Send Notification to Device " + deviceId);
		logger.debug("Message : " + message);
		API_KEY = PropertyFileLoader.getPropertiesInstance().getProperty("fcm.apikey");
		NOTIFICATION_SERVER_URL = PropertyFileLoader.getPropertiesInstance().getProperty("fcm.notificationurl");
		logger.debug("Initializing Request with API Key " + API_KEY);
		try {
			String device = deviceId;
			URL url = new URL(NOTIFICATION_SERVER_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			conn.setRequestProperty("Authorization", "key=" + API_KEY);

			conn.setDoOutput(true);
			JSONObject obj = new JSONObject();
			obj.put("to", device);
			obj.put("data", new JSONObject().put("message", message));
			
			String input = obj.toString();
			
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
			os.close();

			int responseCode = conn.getResponseCode();
			logger.debug("\nSending 'POST' request to URL : " + url);
			logger.debug("Post parameters : " + input);
			logger.debug("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			// print result
			logger.debug(response.toString());			
			return response.toString();			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static void main(String[] args) throws JsonProcessingException{
		
		FCMNotifications noti = new FCMNotifications();
		noti.sendNotifications();
	}

	
	
}
