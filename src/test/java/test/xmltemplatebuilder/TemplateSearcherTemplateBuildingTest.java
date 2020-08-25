package test.xmltemplatebuilder;

import htmlparser.templatebuilders.TemplateNotFoundException;
import htmlparser.templatebuilders.XmlConfigSpecialTags;

import htmlparser.elements.TemplateSearcherTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.jupiter.api.Test;

import htmlparser.Template;

import htmlparser.elements.TemplateSearcher.Traverse;
import htmlparser.elements.TemplateSearcher.StopOn;

import static test.utils.TemplateSearcherTemplateReflection.getDataCollectorTemplate;
import static test.utils.TemplateSearcherTemplateReflection.getParserTemplates;
import static test.utils.TemplateSearcherTemplateReflection.getStopOn;
import static test.utils.TemplateSearcherTemplateReflection.getTraverse;

import static test.utils.ExistingDataCollectorTemplates.collectorTemplateNames;
import static test.utils.ExistingDataCollectorTemplates.isCorrectDataCollectorTemplate;

public class TemplateSearcherTemplateBuildingTest implements XmlConfigSpecialTags {
	
	@Test
	public void validDataCollectorTemplateName() {
		Attributes attributes;
		TestAdapter builder = new TestAdapter("");
		
		for (Traverse traverse : Traverse.values()) {
			for (StopOn stopOn : StopOn.values()) {
				for (String collectorName : collectorTemplateNames) {
					String failMsg = "Failed on " + traverse + " " + stopOn + " " + collectorName;
					attributes = new Attributes();
					attributes.add(templateClassAttribute, TemplateSearcherTemplate.class.getCanonicalName());
					attributes.add(Traverse.class.getSimpleName(), traverse.name());
					attributes.add(StopOn.class.getSimpleName(), stopOn.name());
					attributes.add(dataCollectorAttribute, collectorName);
					
					Map<String, Template> parserTemplates = new HashMap<>();
					Node startConfNode = new Element("h1");
					TemplateSearcherTemplate template;
					try {
						template = (TemplateSearcherTemplate)builder.buildTemplate(attributes, startConfNode, parserTemplates);
					} catch (ClassCastException e) {
						throw new RuntimeException(failMsg);
					}
					assertTrue(traverse == getTraverse(template), failMsg);
					assertTrue(stopOn == getStopOn(template), failMsg);
					assertTrue(parserTemplates == getParserTemplates(template), failMsg);
					assertTrue(isCorrectDataCollectorTemplate(collectorName, getDataCollectorTemplate(template)), failMsg);
				}
			}
		}
	}
	
	@Test
	public void invalidDataCollectorTemplateName() {
		Attributes attributes;
		TestAdapter builder = new TestAdapter("");
		final String collectorName = "InvalidName";
		for (Traverse traverse : Traverse.values()) {
			for (StopOn stopOn : StopOn.values()) {
				String exceptioName = "null";
				boolean expectedException = false;
				attributes = new Attributes();
				attributes.add(templateClassAttribute, TemplateSearcherTemplate.class.getCanonicalName());
				attributes.add(Traverse.class.getSimpleName(), traverse.name());
				attributes.add(StopOn.class.getSimpleName(), stopOn.name());
				attributes.add(dataCollectorAttribute, collectorName);

				Map<String, Template> parserTemplates = new HashMap<>();
				Node startConfNode = new Element("h1");
				
				try {
					builder.buildTemplate(attributes, startConfNode, parserTemplates);
				} catch (TemplateNotFoundException e) {
					expectedException = true;
				} catch (Exception e) {
					exceptioName = e.getClass().getSimpleName();
				}
				assertTrue(expectedException, "Failed on " + traverse + " " + stopOn + " " + exceptioName);
			}
		}
	}
	
}
