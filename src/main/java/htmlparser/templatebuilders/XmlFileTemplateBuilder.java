package htmlparser.templatebuilders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class XmlFileTemplateBuilder extends XmlTemplateBuilder {

	protected String basePath;
	
	public XmlFileTemplateBuilder (String basePath) {
		super();
		this.basePath = basePath;
	}
	
	@Override
	protected InputStream getInputStream(String templateName) throws IOException {
		File xmlFile = new File(basePath + System.getProperty("file.separator") + templateName);
		return new FileInputStream(xmlFile);
	}
}
