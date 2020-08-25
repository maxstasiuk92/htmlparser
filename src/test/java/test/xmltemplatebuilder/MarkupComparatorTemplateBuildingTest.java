package test.xmltemplatebuilder;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.jupiter.api.Test;


import htmlparser.Template;
import htmlparser.elements.MarkupComparatorTemplate;

import htmlparser.elements.MarkupComparator.ReferenceHierarchy;
import htmlparser.elements.MarkupComparator.ReferenceScope;
import htmlparser.templatebuilders.TemplateNotFoundException;
import htmlparser.templatebuilders.XmlConfigSpecialTags;

import static test.utils.MarkupComparatorTemplateReflection.getDataCollectorTemplate;
import static test.utils.MarkupComparatorTemplateReflection.getParserTemplates;
import static test.utils.MarkupComparatorTemplateReflection.getReferenceHierarchy;
import static test.utils.MarkupComparatorTemplateReflection.getReferenceScope;
import static test.utils.MarkupComparatorTemplateReflection.getStartRefNode;

import static test.utils.ExistingDataCollectorTemplates.collectorTemplateNames;
import static test.utils.ExistingDataCollectorTemplates.isCorrectDataCollectorTemplate;

public class MarkupComparatorTemplateBuildingTest implements XmlConfigSpecialTags {
	
	@Test
	public void validDataCollectorTemplateName() {
		Attributes attributes;
		TestAdapter builder = new TestAdapter("");
		
		for (ReferenceHierarchy hierarchy : ReferenceHierarchy.values()) {
			for (ReferenceScope scope : ReferenceScope.values()) {
				for (String collectorName : collectorTemplateNames) {
					String failMsg = "Failed on " + hierarchy + " " + scope + " " + collectorName;
					attributes = new Attributes();
					attributes.add(templateClassAttribute, MarkupComparatorTemplate.class.getCanonicalName());
					attributes.add(ReferenceHierarchy.class.getSimpleName(), hierarchy.name());
					attributes.add(ReferenceScope.class.getSimpleName(), scope.name());
					attributes.add(dataCollectorAttribute, collectorName);
					
					Node startRefNode = new Element("h1");
					Map<String, Template> parserTemplates = new HashMap<>();
					MarkupComparatorTemplate template;
					try {
						template = (MarkupComparatorTemplate)builder.buildTemplate(attributes, startRefNode, parserTemplates);
					} catch (ClassCastException e) {
						throw new RuntimeException(failMsg);
					}
					assertTrue(hierarchy == getReferenceHierarchy(template), failMsg);
					assertTrue(scope == getReferenceScope(template), failMsg);
					assertTrue(startRefNode == getStartRefNode(template), failMsg);
					assertTrue(parserTemplates == getParserTemplates(template), failMsg);
					assertTrue(isCorrectDataCollectorTemplate(collectorName, getDataCollectorTemplate(template)), failMsg);
				}
			}
		}
	}
	
	@Test
	public void invalidDataCollectorTemplateName() {
		final String collectorName = "InvalidName";
		Attributes attributes;
		TestAdapter builder = new TestAdapter("");
		
		for (ReferenceHierarchy hierarchy : ReferenceHierarchy.values()) {
			for (ReferenceScope scope : ReferenceScope.values()) {
				String exceptioName = "null";
				boolean expectedException = false;
				attributes = new Attributes();
				attributes.add(templateClassAttribute, MarkupComparatorTemplate.class.getCanonicalName());
				attributes.add(ReferenceHierarchy.class.getSimpleName(), hierarchy.name());
				attributes.add(ReferenceScope.class.getSimpleName(), scope.name());
				attributes.add(dataCollectorAttribute, collectorName);
				Node startRefNode = new Element("h1");
				Map<String, Template> parserTemplates = new HashMap<>();
				try {
					builder.buildTemplate(attributes, startRefNode, parserTemplates);
				} catch (TemplateNotFoundException e) {
					expectedException = true;
				} catch (Exception e) {
					exceptioName = e.getClass().getSimpleName();
				}
				assertTrue(expectedException, "Failed on " + hierarchy + " " + scope + " " + exceptioName);
			}
		}
	}
}

	
