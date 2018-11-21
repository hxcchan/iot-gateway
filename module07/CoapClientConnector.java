package module07;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

/*
 *
 */
public class CoapClientConnector {
	
	// static
	private static final Logger _Logger = 
			Logger.getLogger(CoapClientConnector.class.getName());
	
	// params
	private String		 _protocol;
	private String	 	 _host;
	private int 		 _port;
	private String		 _serverAddr;
	private CoapClient	 _client;
	private boolean 	 _isInitialized;

	// constructors
	/**
	 * Constructor.
	 *
	 * @param host
	 */
	public CoapClientConnector(String host) {
		super();
		
		_protocol = "coap";
		_port = 5683;

		if (host != null && host.trim().length() > 0) {
			_host = host;
		} else {
			_host = "localhost";
		}
		// NOTE: URL does not have a protocol handler for "coap",
		// so we need to construct the URL manually
		_serverAddr = _protocol + "://" + _host + ":" + _port;
		_Logger.info("Using URL for server conn: " + _serverAddr);
	}

	// public methods
	public void runTests(String resourceName) {
		try {
			_isInitialized = false;
			initClient(resourceName);
			_Logger.info("Current URI: " + getCurrentUri());
			String payload = "Sample payload.";
			pingServer();

			discoverResources();
			sendGetRequest();
			sendGetRequest(true);
			sendPostRequest(payload, false);
			sendPostRequest(payload, true);
			sendPutRequest(payload, false);
			sendPutRequest(payload, true);
			sendDeleteRequest();
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to issue request to CoAP server.", e);
		}
	}

	/**
	 * Returns the CoAP client URI (if set, otherwise returns the _serverAddr, or
	 * null).
	 *
	 * @return String
	 */
	public String getCurrentUri() {
		return (_client != null ? _client.getURI() : _serverAddr);
	}

	public void discoverResources() {
		_Logger.info("Issuing discover...");
		initClient();
		Set<WebLink> wlSet = _client.discover();
		if (wlSet != null) {
			for (WebLink wl : wlSet) {
				_Logger.info(" --> WebLink: " + wl.getURI());
			}
		}
	}

	public void pingServer() {
		_Logger.info("Sending ping...");
		initClient();
		_client.ping();

	}

	public void sendDeleteRequest() {
		initClient();
		handleDeleteRequest();
	}

	public void sendDeleteRequest(String resourceName) {
		_isInitialized = false;
		initClient(resourceName);
		handleDeleteRequest();
	}

	public void sendGetRequest() {
		initClient();
		handleGetRequest(false);
	}

	public void sendGetRequest(String resourceName) {
		_isInitialized = false;
		initClient(resourceName);
		handleGetRequest(false);
	}

	public void sendGetRequest(boolean useNON) {
		initClient();
		handleGetRequest(useNON);
	}

	public void sendGetRequest(String resourceName, boolean useNON)
	{
		_isInitialized = false;
		sendGetRequest(useNON);
	}

	public void sendPostRequest(String payload, boolean useCON) {
		initClient();
		handlePostRequest(payload, useCON);
	}

	public void sendPostRequest(String resourceName, String payload, boolean useCON) {
		_isInitialized = false;
		initClient(resourceName);
		handlePostRequest(payload, useCON);
	}

	public void sendPutRequest(String payload, boolean useCON) {
		initClient();
		handlePutRequest(payload, useCON);
	}

	public void sendPutRequest(String resourceName, String payload, boolean useCON) {
		_isInitialized = false;
		initClient(resourceName);
		handlePutRequest(payload, useCON);
	}

	public void registerObserver(boolean enableWait) {
		_Logger.info("Registering observer...");
		CoapHandler handler = null;
		if (enableWait) {
			_client.observeAndWait(handler);
		} else {
			_client.observe(handler);

		}
	}

// private Delete methods
	private void handleDeleteRequest() {
		
		_Logger.info("Sending DELETE...");
		
		CoapResponse response = _client.delete();
		if(response != null) {
			_Logger.info(
					"Response: " + response.isSuccess() + " - " + response.getOptions() + " - " + response.getCode());
		}else {
			_Logger.warning("No response received.");
		}
		
	}
	

// private Get methods

	private void handleGetRequest(boolean useNON) {
		
		_Logger.info("Sending GET...");
	
		if (useNON) {
			_client.useNONs();
		}
		CoapResponse response = _client.get();
		if(response != null) {
			_Logger.info(
					"Response: " + response.isSuccess() + " - " + response.getOptions() + " - " + response.getCode());
		}else {
			_Logger.warning("Failure.");
		}
		
	}

	
// Put method
	private void handlePutRequest(String payload, boolean useCON) {
		_Logger.info("Sending PUT...");
		CoapResponse response = null;
		if (useCON) {
			_client.useCONs().useEarlyNegotiation(32).get();
		}
		response = _client.put(payload, MediaTypeRegistry.TEXT_PLAIN);
		if (response != null) {
			_Logger.info(
					"Response: " + response.isSuccess() + " - " + response.getOptions() + " - " + response.getCode());
		} else {
			_Logger.warning("No response received.");
		}
	}
	

// Post method

	private void handlePostRequest(String payload, boolean useCON) {
		_Logger.info("Sending POST...");
		CoapResponse response = null;

		if (useCON) {
			_client.useCONs().useEarlyNegotiation(32).get();
		}
		response = _client.post(payload, MediaTypeRegistry.TEXT_PLAIN);
		if (response != null) {
			_Logger.info(
					"Response: " + response.isSuccess() + " - " + response.getOptions() + " - " + response.getCode());
		} else {
			_Logger.warning("No response received.");
		}
	}

	private void initClient() {
		initClient(null);
	}

	/**Initial Coap Client using serverAddr
	 * 
	 * @param resourceName  To update the serverAddress
	 */
	private void initClient(String resourceName) {
		if (_isInitialized) {
			return;
		}
		if (_client != null) {
			_client.shutdown();
			_client = null;
		}
		try {
			if (resourceName != null) {
				_serverAddr += "/" + resourceName;
			}
			_client = new CoapClient(_serverAddr);
			_Logger.info("Created client connection to server / resource: " + _serverAddr);
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to connect to broker: " + getCurrentUri(), e);
		}
	}
}