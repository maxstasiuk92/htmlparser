package htmlparser.elements;

import java.util.Map;

import htmlparser.DataCollector;
import htmlparser.DataCollectorTemplate;
import htmlparser.Element;
import htmlparser.ParsingSpecialTags;
import htmlparser.Template;
import htmlparser.elements.TemplateSearcher.StopOn;
import htmlparser.elements.TemplateSearcher.Traverse;

public class TemplateSearcherTemplate implements Template, ParsingSpecialTags {
	
	protected DataCollectorTemplate dataCollectorTemplate;
	protected Map<String, Template> parserTemplates;
	protected Traverse traverse;
	protected StopOn stopOn;
	
	public TemplateSearcherTemplate(DataCollectorTemplate dataCollectorTemplate,
			Map<String, Template> parserTemplates,	Traverse traverse, StopOn stopOn) {
		this.dataCollectorTemplate = dataCollectorTemplate;
		this.parserTemplates = parserTemplates;
		this.traverse = traverse;
		this.stopOn = stopOn;
	}
	
	@Override
	public Element getParserElement() {
		DataCollector dataCollector = null;
		if (dataCollectorTemplate != null) {
			dataCollector = dataCollectorTemplate.getDataCollector();
		}
		return new TemplateSearcher(dataCollector, parserTemplates, traverse, stopOn);
	}

	
}
