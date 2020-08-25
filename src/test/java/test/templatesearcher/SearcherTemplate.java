package test.templatesearcher;

import java.util.HashMap;

import htmlparser.Element;
import htmlparser.Template;
import htmlparser.data.collectors.CompositionCollector;
import htmlparser.elements.TemplateSearcher;

import static htmlparser.elements.TemplateSearcher.StopOn;
import static htmlparser.elements.TemplateSearcher.Traverse;

public class SearcherTemplate implements Template {

	Traverse traverse;
	StopOn stopOn;
	
	public SearcherTemplate(Traverse traverse, StopOn stopOn) {
		this.traverse = traverse;
		this.stopOn = stopOn;
	}
	
	@Override
	public Element getParserElement() {
		CompositionCollector dataCollector = new CompositionCollector();
		HashMap<String, Template> parserTemplates = new HashMap<>();
		MarkupTemplate twoItemsTemplate = new MarkupTemplate(MarkupTemplate.twoItemListWithParameter);
		MarkupTemplate threeItemsTemplate = new MarkupTemplate(MarkupTemplate.threeItemListWithParameter);
		parserTemplates.put("twoItem", twoItemsTemplate);
		parserTemplates.put("threeItem", threeItemsTemplate);
		TemplateSearcher searcherElement = new TemplateSearcher(dataCollector, 
				parserTemplates, traverse, stopOn);
		
		return searcherElement;
	}
}
