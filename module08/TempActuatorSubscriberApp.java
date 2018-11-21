package module08;

import java.util.logging.Logger;

// This class will subscribe the information from Ubidots.

public class TempActuatorSubscriberApp {
	
	// Constructors
	public TempActuatorSubscriberApp() {
		super();
	}
	
	private static final Logger _Logger =
			Logger.getLogger(TempActuatorSubscriberApp.class.getName());
	
	// Configure information that can connect to Ubidots.
	private String host 		= "things.ubidots.com";
	private String token		= "A1E-AhT6ZfwmZvtXQMo5Nhf3lkhl6Bb7XM";            //Token from Ubidots API.
	private String pemCert	    = "C:\\Users\\HSong\\Desktop\\ubidots_cert.pem";   // The location of Ubidots' certificate.
	private MqttClientConnector _mqttClientConnector;
	
	
	
	// This method start the whole process, and subscribe certain information.
	public void start() {
		_mqttClientConnector = new MqttClientConnector(host,token,null, pemCert);
		_mqttClientConnector.connect();
		String topic = "/v1.6/devices/SongHan/Temperature";
		_mqttClientConnector.subscribeToTopic(topic);
		_mqttClientConnector.disconnect();
		
		
	}
	
	private static TempActuatorSubscriberApp subApp;
	public static void main(String[] args) {
		subApp = new TempActuatorSubscriberApp();
		_Logger.info("The actuator has been started.");
		subApp.start();
	}

}