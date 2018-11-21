package module06;

import java.util.logging.Logger;
/*
 * Song Han
 * 
 * */
public class MqttPubClientTestApp {

// Constructors for this class.
	public  MqttPubClientTestApp() {
		super();
	}
	
	private static final Logger _pubClientLogger = Logger.getLogger(MqttSubClientTestApp.class.getName());
	
	
// Starting the whole process which includes topic and payload.
	public void start() {
		
		MqttClientConnector _publishClient = new MqttClientConnector(null,true);		
		
		_publishClient.connect();		
		String topicName = "test";		
		String payload = "This is a test......";
		
// Publish the message and then finish the connection.
		_publishClient.publishMessage(topicName, 1, payload.getBytes());		
		_publishClient.disconnect();
		
	}
	
	
	public static void main(String[] args) {
		
		MqttPubClientTestApp _pubClient = new MqttPubClientTestApp();
		
		try {
			_pubClient.start();
			_pubClientLogger.info("PubClient starts.");
		}catch (Exception E) {
			E.printStackTrace();
		}
	}

}