package htmlparser.templatebuilders;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;

import htmlparser.DataCollectorTemplate;
import htmlparser.ParsingSpecialTags;
import htmlparser.Template;
import htmlparser.TemplateBuilder;
import htmlparser.data.collectors.CompositionCollectorTemplate;
import htmlparser.data.collectors.InnerParameterCollectorTemplate;
import htmlparser.data.collectors.ParameterCollectorTemplate;
import htmlparser.elements.MarkupComparator.ReferenceHierarchy;
import htmlparser.elements.MarkupComparator.ReferenceScope;
import htmlparser.elements.MarkupComparatorTemplate;
import htmlparser.elements.TemplateSearcherTemplate;
import htmlparser.elements.TemplateSearcher.Traverse;
import htmlparser.elements.TemplateSearcher.StopOn;


public abstract class XmlTemplateBuilder implements TemplateBuilder, ParsingSpecialTags, XmlConfigSpecialTags {
	
	protected Map<String, Template> templateRegister;
	
	protected XmlTemplateBuilder () {
		this.templateRegister = new HashMap<String, Template>();
	}
	
	/**
	 * @param templateName is name of xml-file.
	 */
	@Override
	public Template getTemplate(String templateName) {
		if (!templateRegister.containsKey(templateName)) {
			registerNewTemplates(templateName);
		}
		return templateRegister.get(templateName);
	}
	
	protected void registerNewTemplates(String startingTemplateName) {
		Node confNode = null;
		Queue<String> foundNewTemplates = new LinkedList<String>();
		LinkedList<Map<String, Template>> emptyParserMaps = new LinkedList<>();
		
		foundNewTemplates.add(startingTemplateName);
		while (foundNewTemplates.size() > 0) {
			String templateName = foundNewTemplates.poll();
			if (!templateRegister.containsKey(templateName)) {
				confNode = getConfigurationTree(templateName);
				Map<String, Template> emptyParserMap = new HashMap<>();
				processTree(confNode, emptyParserMap, foundNewTemplates);
				Attributes tempClassAttrib = confNode.attributes();
				Template newTemplate = buildTemplate(tempClassAttrib, confNode.childNode(0), emptyParserMap);
				if (newTemplate == null) {
					throw new TemplateNotFoundException();
				}
				emptyParserMaps.add(emptyParserMap);
				templateRegister.put(templateName, newTemplate);
			}
		}
		for (Map<String, Template> parserMap : emptyParserMaps) {
			for (Entry<String, Template> entry : parserMap.entrySet()) {
				entry.setValue(templateRegister.get(entry.getKey()));
			}
		}
	}
	
	protected Node getConfigurationTree(String templateName) throws XmlConfigurationReadException {
		InputStream xmlConfigStream = null;
		Node confNode = null;
		try {
			xmlConfigStream = getInputStream(templateName);
			final String noBaseUri = "";
			confNode = Jsoup.parse(xmlConfigStream, "UTF-8", noBaseUri, Parser.xmlParser()).child(0);
		} catch (IOException e) {
			throw new XmlConfigurationReadException(e);
		} finally {
			try {
				xmlConfigStream.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return confNode;
	}
	
	protected abstract InputStream getInputStream(String templateName) throws IOException;
	
	//traverse tree and 1)removes non-valuable text nodes; 2)collects new parser templates
	protected void processTree(Node confNode, Map<String, Template> emptyParserMap, Queue<String> foundNewTemplates) {
		Node currentNode = confNode.childNode(0);
		while (currentNode != null) {
			String currentNodeName = currentNode.nodeName();
			if (currentNodeName.equals("#text")) {
				Node n = currentNode;
				currentNode = nextNonChild(currentNode, confNode);
				n.remove();
			} else if (currentNodeName.equals(valuableTextTag)) {
				currentNode = nextNonChild(currentNode, confNode);
			} else if (currentNodeName.equals(parserTag)) {
				String parserName = currentNode.attributes().get(parserNameAttribute);
				emptyParserMap.put(parserName, null);
				if (!templateRegister.containsKey(parserName) && !foundNewTemplates.contains(parserName)) {
					foundNewTemplates.add(parserName);
				}
				currentNode = nextNonChild(currentNode, confNode);
			} else {
				if (currentNode.childNodeSize() > 0) {
					currentNode = currentNode.childNode(0);
				} else {
					currentNode = nextNonChild(currentNode, confNode);
				}
			}
		}
	}
	
	protected Node nextNonChild(Node currentNode, Node confNode) {
		Node parentNode;
		Node prevNode = currentNode;
		currentNode = currentNode.nextSibling();
		while (currentNode == null && (parentNode = prevNode.parentNode()) != confNode) {
			prevNode = parentNode;
			currentNode = prevNode.nextSibling();
		}
		return currentNode;
	}
	
	protected Template buildTemplate(Attributes attrib, Node startConfNode, Map<String, Template> parserTemplates) {
		String tempClassName = attrib.get(templateClassAttribute);
		Template template = null;
		if (tempClassName.equals(MarkupComparatorTemplate.class.getCanonicalName())) {
			template = buildMarkupComparatorTemplate(attrib, startConfNode, parserTemplates);
		} else if (tempClassName.equals(TemplateSearcherTemplate.class.getCanonicalName())) {
			template = buildTemplateSearcherTemplate(attrib, startConfNode, parserTemplates);
		}
		return template;
	}
	
	protected DataCollectorTemplate getDataCollectorTemplate(String className) {
		DataCollectorTemplate template = null;
		if (className.equals(InnerParameterCollectorTemplate.class.getCanonicalName())) {
			template = new InnerParameterCollectorTemplate();
		} else if (className.equals(ParameterCollectorTemplate.class.getCanonicalName())) {
			template = new ParameterCollectorTemplate();
		} else if (className.equals(CompositionCollectorTemplate.class.getCanonicalName())) {
			template = new CompositionCollectorTemplate();
		}
		return template;
	}
	
	protected MarkupComparatorTemplate buildMarkupComparatorTemplate(Attributes attrib, Node startRefNode, Map<String, Template> parserTemplates) {
		String dataCollectorTempClassName = attrib.get(dataCollectorAttribute);
		DataCollectorTemplate dataCollectorTemplate = null;
		if (!dataCollectorTempClassName.equals(nullAttributeValue)) {
			if (null == (dataCollectorTemplate = getDataCollectorTemplate(dataCollectorTempClassName))) {
				throw new TemplateNotFoundException();
			}
		}
		ReferenceScope refScope = ReferenceScope.valueOf(attrib.get(ReferenceScope.class.getSimpleName()));
		ReferenceHierarchy refHierarchy = ReferenceHierarchy.valueOf(attrib.get(ReferenceHierarchy.class.getSimpleName()));
		MarkupComparatorTemplate template = new MarkupComparatorTemplate(dataCollectorTemplate, parserTemplates,
				startRefNode, refScope, refHierarchy);
		return template;
	}
	
	protected TemplateSearcherTemplate buildTemplateSearcherTemplate(Attributes attrib, Node startRefNode, 
			Map<String, Template> parserTemplates) {
		String dataCollectorTempClassName = attrib.get(dataCollectorAttribute);
		DataCollectorTemplate dataCollectorTemplate = null;
		if (!dataCollectorTempClassName.equals(nullAttributeValue)) {
			if (null == (dataCollectorTemplate = getDataCollectorTemplate(dataCollectorTempClassName))) {
				throw new TemplateNotFoundException();
			}
		}
		Traverse traverse = Traverse.valueOf(attrib.get(Traverse.class.getSimpleName()));
		StopOn stopOn = StopOn.valueOf(attrib.get(StopOn.class.getSimpleName()));
		TemplateSearcherTemplate template = new TemplateSearcherTemplate(dataCollectorTemplate, parserTemplates, traverse, stopOn);
		return template;
	}
	
}
