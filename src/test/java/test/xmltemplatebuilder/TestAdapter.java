package test.xmltemplatebuilder;

import java.util.Map;
import java.util.Queue;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Node;

import htmlparser.DataCollectorTemplate;
import htmlparser.Template;
import htmlparser.templatebuilders.XmlConfigurationReadException;
import htmlparser.templatebuilders.XmlFileTemplateBuilder;

public class TestAdapter extends XmlFileTemplateBuilder {
	
	public TestAdapter(String basePath) {
		super(basePath);
	}
	
	@Override
	public void registerNewTemplates(String startingTemplateName) {
		super.registerNewTemplates(startingTemplateName);
	}
	
	@Override
	public Node getConfigurationTree(String templateName) throws XmlConfigurationReadException {
		return super.getConfigurationTree(templateName);
	}
	
	@Override
	public void processTree(Node confNode, Map<String, Template> emptyParserMap, Queue<String> notRegisteredTemplates) {
		super.processTree(confNode, emptyParserMap, notRegisteredTemplates);
	}
	
	@Override
	public Template buildTemplate(Attributes attrib, Node startRefNode, Map<String, Template> parserTemplates) {
		return super.buildTemplate(attrib, startRefNode, parserTemplates);
	}
	
	@Override
	public DataCollectorTemplate getDataCollectorTemplate(String className) {
		return super.getDataCollectorTemplate(className);
	}
	
	public void putParserTemplate(String name, Template inst) {
		templateRegister.put(name, inst);
	}
}
