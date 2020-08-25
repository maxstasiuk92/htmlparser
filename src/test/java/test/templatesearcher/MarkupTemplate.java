package test.templatesearcher;

import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;

import htmlparser.Element;
import htmlparser.ParsingSpecialTags;
import htmlparser.Template;
import htmlparser.data.collectors.ParameterCollector;
import htmlparser.elements.MarkupComparator;

import static htmlparser.elements.MarkupComparator.ReferenceHierarchy;
import static htmlparser.elements.MarkupComparator.ReferenceScope;

public class MarkupTemplate implements Template, ParsingSpecialTags {
	static final String twoItemListWithParameter =
				"<ul>"
					+ "<il><" + dataTag + " " + parameterNameAttribute + "=\"param1\"/></il>"
					+ "<il></il>"
				+ "</ul>";
	
	static final String threeItemListWithParameter =
				"<ul>"
					+ "<il></il>"
					+ "<il><" + dataTag + " " + parameterNameAttribute + "=\"param2\"/></il>"
					+ "<il></il>"
				+ "</ul>";
	
	String htmlTemplate;
	
	MarkupTemplate(String htmlTemplate) {
		this.htmlTemplate = htmlTemplate;
	}

	@Override
	public Element getParserElement() {
		ParameterCollector dataCollector = new ParameterCollector();
		Map<String, Template> noParserTemplates = null;
		Node startRefNode = Jsoup.parse(htmlTemplate).body().childNode(0);
		MarkupComparator parserElement = new MarkupComparator(dataCollector, noParserTemplates, 
				startRefNode, ReferenceScope.SUBSET, ReferenceHierarchy.EXACT);
		
		return parserElement;
	}
}
