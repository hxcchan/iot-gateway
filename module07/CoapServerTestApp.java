package module07;

import java.util.logging.Logger;

public class CoapServerTestApp {
	
	// constructors
	public CoapServerTestApp() {
		super();
	}
	
	private static final Logger _Logger = 
			Logger.getLogger(CoapServerTestApp.class.getName());
	
	private static CoapServerTestApp _App;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		_App = new CoapServerTestApp();
			_Logger.info("Coap Server starts.");
			_App.start();
	}

	// private var's
	private CoapServerConnector _coapServer;


	// public methods
	/**
	*
	*/
	public void start() {
		_coapServer = new CoapServerConnector();
		_coapServer.start();
	}
}
