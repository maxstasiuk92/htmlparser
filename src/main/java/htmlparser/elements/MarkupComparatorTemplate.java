package htmlparser.elements;

import java.util.Map;

import org.jsoup.nodes.Node;

import htmlparser.DataCollector;
import htmlparser.DataCollectorTemplate;
import htmlparser.Element;
import htmlparser.ParsingSpecialTags;
import htmlparser.Template;
import htmlparser.elements.MarkupComparator.ReferenceHierarchy;
import htmlparser.elements.MarkupComparator.ReferenceScope;

public class MarkupComparatorTemplate implements Template, ParsingSpecialTags {
	
	protected DataCollectorTemplate dataCollectorTemplate;
	protected Map<String, Template> parserTemplates;
	protected ReferenceScope refScope;
	protected ReferenceHierarchy refHierarchy;
	protected Node startRefNode;
	
	public MarkupComparatorTemplate (DataCollectorTemplate dataCollectorTemplate,
			Map<String, Template> parserTemplates,
			Node startRefNode, ReferenceScope refScope, ReferenceHierarchy refHierarchy) {
		if (refScope == null || refHierarchy == null) {
			throw new IllegalArgumentException();
		}
		this.dataCollectorTemplate = dataCollectorTemplate;
		this.parserTemplates = parserTemplates;
		this.startRefNode = startRefNode;
		this.refScope = refScope;
		this.refHierarchy = refHierarchy;
	}
	
	@Override
	public Element getParserElement() {
		DataCollector dataCollector = null;
		if (dataCollectorTemplate != null) {
			dataCollector = dataCollectorTemplate.getDataCollector();
		}
		return new MarkupComparator(dataCollector, parserTemplates,	startRefNode, refScope, refHierarchy);
	}

}
