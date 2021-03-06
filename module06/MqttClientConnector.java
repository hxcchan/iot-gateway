package module06;


// These are necessary components of MQTT services and for loggers.
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

// General IO functions.
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;


public class MqttClientConnector implements MqttCallback {

// Constructors.
	
	public MqttClientConnector() {
		// use defaults
		this(null, false);
	}

	
// The logger for this class.
	private static final Logger _Logger = Logger.getLogger(MqttClientConnector.class.getName());
	
	// params
	private String _protocol	 	= "tcp";
	private String _host     		= "test.mosquitto.org";
	private int    _port	 		= 1883;
	

	private String _clientID;
    private String _brokerAddr;
    
    private MqttClient _mqttClient;
    
	private String _userName 		= null;
	private String _pemFileName 	= null;
	private Boolean _isSecureConn 	= false;


	/**
	 * Constructor.
	 * 
	 * @param host       The name of the host.
	 * @param isSecure   Ignored now.
	 */
	public MqttClientConnector(String host, boolean isSecure) {
		super();
		
		// NOTE: 'isSecure' ignored for now
		
		if(host != null && host.trim().length() > 0 ) {
			_host = host;
		}
		
		// NOTE: URL does not have a protocol handler for "tcp",
		// so we need to construct the URL manually
		_clientID = MqttClient.generateClientId();
		
		_Logger.info("Using client ID for broker conn: " + _clientID);
		
		_brokerAddr = _protocol + "://" + _host + ":" + _port;
		
		_Logger.info("Using URL for broker conn: " + _brokerAddr);
		
		MemoryPersistence persistence = new MemoryPersistence();
		
		try {
			_mqttClient = new MqttClient(_brokerAddr, _clientID, persistence);
		} catch(MqttException e) {
			
			_Logger.log(Level.SEVERE, "Failed to create a client. ", e);
			
		}
	}
	
	public MqttClientConnector(String host, String userName, String pemFileName) {
		super();
		
		if (host !=null && host.trim().length() >0) {
			_host = host;
		}

		if (pemFileName != null) {
			File file = new File(pemFileName);
			System.out.println(file.exists());
			if (file.exists()) {
				_protocol   	= "ssl";
				_port			= 8883;
				_pemFileName 	= pemFileName;
				_isSecureConn 	= true;
				
				_Logger.info("PEM file valid. Using secure connection: " + _pemFileName);
			}else {
				_Logger.info("Pem file invalid. Using insecure connection: " + pemFileName);
			}
		}
		
		_clientID 	= MqttClient.generateClientId();
		_brokerAddr = _protocol + "://" + _host + ":" + _port;
		
		_Logger.info("Using URL for broker coon: " + _brokerAddr) ;
		
		MemoryPersistence persistence = new MemoryPersistence();
		
		try {
			_mqttClient = new MqttClient(_brokerAddr, _clientID, persistence);
		} catch(MqttException e) {
			
			_Logger.log(Level.SEVERE, "Failed to create a client. ", e);
			
		}
	}
	
	
	// public methods

	public void connect() {
		if (_mqttClient != null) {
			
			try {
				MqttConnectOptions connOpts = new MqttConnectOptions();
				
				
				connOpts.setCleanSession(true); 							// StateFUL!
				
				_Logger.info("Connecting to broker: " + _brokerAddr);
				
				if (_userName != null) {
					connOpts.setUserName(_userName);
				}
				
				_mqttClient.setCallback(this);                             // Connect to the session.
				_mqttClient.connect(connOpts);
				
				_Logger.info("Connected to broker: " + _brokerAddr);
			
			}catch(MqttException e){									   // Catch Exception!
				_Logger.log(Level.SEVERE, "Failed to connect to broker: " + _brokerAddr, e);				
			}
		}
		
	}
	
	
	public void disconnect() {
		try {
			
			_mqttClient.disconnect();
			_Logger.info("Disconnected form brokder: " + _brokerAddr);
			
		}catch(Exception e) {
			_Logger.log(Level.SEVERE, "Failed to disconnect from broker: " + _brokerAddr, e);
		}
		
	}
	
	/**
	 * Pulishes the given payload to broker directly to topic 'topic'.
	 * 
	 * @param topic
	 * @param qosLevel
	 * @param payload
	 */
	
	public boolean publishMessage(String topic, int qosLevel, byte[] payload) {
		boolean success = false;
		
		try {
			_Logger.info("Publishing message to topic: " + topic);
			
	// We create a new MqttMessage here, and pass 'payload' to the constructor
			MqttMessage message = new MqttMessage(payload);
			
	// Set the QoS on the message to qosLevel
			message.setQos(qosLevel);
			
	// We set it to 'true' which means the server will keep the message.
		    message.setRetained(true);
			
	// Call publish
			_mqttClient.publish(topic, payload, qosLevel, true);
			
	// Log the result.
			_Logger.info("Message " + message.getId() + " completed.");
			
			success = true;
			
		} catch(MqttException e) {
			_Logger.log(Level.SEVERE, "Failure: " + e.getMessage());
		}
		
		return success;
	}
	
	public boolean subscribeToAll() {
		try {
			_mqttClient.subscribe("$SYS/#");
			return true;
		} catch (MqttException e) {
			_Logger.log(Level.WARNING, "Failed to subscribe to all topics.", e);
		}
		return false;
	}


// This method subscribes the certain topic.
	public boolean subscribeToTopic(String topic) {
		try {
			_mqttClient.subscribe(topic);
			return true;
		} catch (MqttException e) {
			_Logger.log(Level.WARNING, "Failure.", e);
		}
		return false;
	}
	
	

// This is an Exception handling method. When it happens it will try to connect again.
	public void connectionLost(Throwable t) {
		_Logger.log(Level.WARNING, "Connection to broker lost. Will retry soon.", t);

		if (!_mqttClient.isConnected()) {
			try {
				_mqttClient.connect();
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
	}
	
	
// If the deliver is complete, we log the information or we catch the exception here.
	public void deliveryComplete(IMqttDeliveryToken token) {
		try {
			_Logger.info("Delivery complete: " + token.getMessageId() + " - " + token.getResponse() + " - "
					+ token.getMessage());
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to retrieve message from token.", e);
		}
		
	}

// This method aims to get and analyze the message. 
	public void messageArrived(String data, MqttMessage msg) throws Exception {		
		_Logger.info("Message arrived: " + data + ", " + msg.getId() + msg.toString());

	}



}