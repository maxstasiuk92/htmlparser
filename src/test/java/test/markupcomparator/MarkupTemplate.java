package test.markupcomparator;

import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;

import htmlparser.Element;
import htmlparser.ParsingSpecialTags;
import htmlparser.Template;
import htmlparser.data.collectors.ParameterCollector;
import htmlparser.elements.MarkupComparator;
import htmlparser.elements.MarkupComparator.ReferenceHierarchy;
import htmlparser.elements.MarkupComparator.ReferenceScope;

public class MarkupTemplate implements Template, ParsingSpecialTags {
	String refHtml;
	ReferenceScope refScope;
	ReferenceHierarchy refHierarchy;
	
	public MarkupTemplate(String refHtml, ReferenceScope refScope, ReferenceHierarchy refHierarchy) {
		this.refHtml = refHtml;
		this.refScope = refScope;
		this.refHierarchy = refHierarchy;
	}
	
	@Override
	public Element getParserElement() {
		final Map<String, Template> noParserTemplates = null;
		Node startReferenceNode = Jsoup.parse(refHtml).body().childNode(0);
		Element parserElement = new MarkupComparator(new ParameterCollector(), noParserTemplates,
				startReferenceNode, refScope, refHierarchy);
		return parserElement;
	}

}
