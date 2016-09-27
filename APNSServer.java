package com.firebase.notifications;



import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.ApnsServiceBuilder;

public class APNSServer {
	private static final String DEVICE_TOKEN = "4afadccf9be26d8fe91c71d35289d883f3958623d6bce9d36c567dcba2e76106";
	

    public static void main(String [] args) throws Exception {
        System.out.println("Sending an iOS push notification...");

        
        String type = "dev";
        String message = "the test push notification message";

        ApnsServiceBuilder serviceBuilder = APNS.newService();

        if (type.equals("prod")) {
            System.out.println("using prod API");
            String certPath = APNSServer.class.getResource("prod_cert.p12").getPath();
            serviceBuilder.withCert(certPath, "password")
                    .withProductionDestination();
        } else if (type.equals("dev")) {
            System.out.println("using dev API");
            String certPath = APNSServer.class.getResource("aps_dev_java.p12").getPath();
            serviceBuilder.withCert(certPath, "12345678")
                    .withSandboxDestination();
        } else {
            System.out.println("unknown API type "+type);
            return;
        }

        ApnsService service = serviceBuilder.build();


        //Payload with custom fields
        String payload = APNS.newPayload()
                .alertBody(message)
                .alertTitle("test alert title")
                .sound("default")
                .customField("custom", "custom value").build();

        ////Payload with custom fields
        //String payload = APNS.newPayload()
        //        .alertBody(message).build();

        ////String payload example:
        //String payload = "{\"aps\":{\"alert\":{\"title\":\"My Title 1\",\"body\":\"My message 1\",\"category\":\"Personal\"}}}";


        System.out.println("payload: "+payload);
        service.push(DEVICE_TOKEN, payload);

        System.out.println("The message has been hopefully sent...");
    }
}
