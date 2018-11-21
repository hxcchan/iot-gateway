package module07;

import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;

public class CoapServerConnector {
	// constructors

	public CoapServerConnector() {
		super();
	}
	
	private static final Logger _Logger = 
			Logger.getLogger(CoapServerConnector.class.getName());
	// private var's
	private CoapServer _coapServer;

// Add resources if we could find
	public void addResource(CoapResource resource) {
		if (resource != null) {
			_coapServer.add(resource);
		}
	}

	public void start() {

		_Logger.info("Creating CoAP server instance and 'temp' handler...");

		_coapServer = new CoapServer();
		_coapServer.start();

		TempResourceHandler tempHandler = new TempResourceHandler("temp");

		_coapServer.add(tempHandler);

		
	}

	public void stop() {
		_Logger.info("Stopping CoAP server...");
		_coapServer.stop();
	}
}