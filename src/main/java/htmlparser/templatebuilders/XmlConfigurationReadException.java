package htmlparser.templatebuilders;

public class XmlConfigurationReadException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public XmlConfigurationReadException() {
		super();
	}
	
	public XmlConfigurationReadException(String message) {
		super(message);
	}
	
	public XmlConfigurationReadException(Throwable cause) {
		super(cause);
	}
}
