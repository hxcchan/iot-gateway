package module08;

import java.util.logging.Logger;

// This class will publish the temp information to ubidots which we can see on the website.


public class TempSensorPublisherApp {
	
	// Constructors
	public TempSensorPublisherApp() {
		super();
	}
	
	
	private static final Logger _Logger =
			Logger.getLogger(TempSensorPublisherApp.class.getName());
	

	
	// Configure informaion that can have the access to Ubidots.
	private String host 		= "things.ubidots.com";
	private String token 		= "A1E-AhT6ZfwmZvtXQMo5Nhf3lkhl6Bb7XM";
	private String pemCert 		= "C:\\Users\\HSong\\Desktop\\ubidots_cert.pem";    // The location of Ubidots' certificate.
	
	private MqttClientConnector _mqttClientConnector;
	
	
	// Start the thread.
	
	public void start() {	
		_mqttClientConnector = new MqttClientConnector(host,token, null, pemCert);
		_mqttClientConnector.connect();
		
    // The payload that we set will appear on the Ubidots' panel.
		String topic	= "/v1.6/devices/SongHan/Sensors";
		String payload 	= "21";
		
		_mqttClientConnector.publishMessage(topic, 1, payload.getBytes() );
		_mqttClientConnector.disconnect();
		
	}
	
	private static TempSensorPublisherApp pubApp;
	public static void main(String[] args) {
		pubApp = new TempSensorPublisherApp();
		pubApp.start();
		_Logger.info("The publisher has been started.");
	}
}