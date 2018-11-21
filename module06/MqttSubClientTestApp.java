package module06;

import java.util.logging.Logger;
/*
 * Song Han
 * */

public class MqttSubClientTestApp {
	
// Constructors.	
	public MqttSubClientTestApp(){
		
		super();
		
	}
	
// The logger for this class.
	private static final Logger _subLogger =
		Logger.getLogger(MqttSubClientTestApp.class.getName());
	
 
/* 1. This is a method to start connecting to the client.
 * 2. We define a given topic here.
 * 3. This method will subscribe to the topic we set.
 * 4. Disconnect.
 * */
	 
	public void start() {
		MqttClientConnector _mqttClientConnector = new MqttClientConnector();
		_mqttClientConnector.connect();		
		String topicName = "test";		
		_mqttClientConnector.subscribeToTopic(topicName); 
		_mqttClientConnector.disconnect();
	}
	

// Main function, starts the whole process.
	public static void main(String[] args){
		MqttSubClientTestApp _subApp = new MqttSubClientTestApp();
		
		try {
			_subApp.start();
			_subLogger.info("Subclient starts.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}