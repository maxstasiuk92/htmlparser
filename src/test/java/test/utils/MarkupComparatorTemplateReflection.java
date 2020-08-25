package test.utils;

import java.lang.reflect.Field;
import java.util.Map;

import org.jsoup.nodes.Node;

import htmlparser.DataCollectorTemplate;
import htmlparser.Template;
import htmlparser.elements.MarkupComparatorTemplate;
import htmlparser.elements.MarkupComparator.ReferenceHierarchy;
import htmlparser.elements.MarkupComparator.ReferenceScope;

public class MarkupComparatorTemplateReflection {
	
	public static ReferenceHierarchy getReferenceHierarchy(MarkupComparatorTemplate template) {
		ReferenceHierarchy refHierarchy;
		try {
			Field f = template.getClass().getDeclaredField("refHierarchy");
			f.setAccessible(true);
			refHierarchy = (ReferenceHierarchy)f.get(template);
		} catch (Exception e) {
			throw new RuntimeException("Not reflected MarkupComparatorTemplate.refHierarchy");
		}
		return refHierarchy;
	}
	
	public static ReferenceScope getReferenceScope(MarkupComparatorTemplate template) {
		ReferenceScope refScope;
		try {
			Field f = template.getClass().getDeclaredField("refScope");
			f.setAccessible(true);
			refScope = (ReferenceScope)f.get(template);
		} catch (Exception e) {
			throw new RuntimeException("Not reflected MarkupComparatorTemplate.refScope");
		}
		return refScope;
	}
	
	public static Node getStartRefNode(MarkupComparatorTemplate template) {
		Node startRefNode;
		try {
			Field f = template.getClass().getDeclaredField("startRefNode");
			f.setAccessible(true);
			startRefNode = (Node)f.get(template);
		} catch (Exception e) {
			throw new RuntimeException("Not reflected MarkupComparatorTemplate.startRefNode");
		}
		return startRefNode;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Template> getParserTemplates(MarkupComparatorTemplate template) {
		Map<String, Template> parserTemplates;
		try {
			Field f = template.getClass().getDeclaredField("parserTemplates");
			f.setAccessible(true);
			parserTemplates = (Map<String, Template>)f.get(template);
		} catch (Exception e) {
			throw new RuntimeException("Not reflected MarkupComparatorTemplate.parserTemplates");
		}
		return parserTemplates;
	}
	
	public static DataCollectorTemplate getDataCollectorTemplate(MarkupComparatorTemplate template) {
		DataCollectorTemplate dataCollectorTemplate;
		try {
			Field f = template.getClass().getDeclaredField("dataCollectorTemplate");
			f.setAccessible(true);
			dataCollectorTemplate = (DataCollectorTemplate)f.get(template);
		} catch (Exception e) {
			throw new RuntimeException("Not reflected MarkupComparatorTemplate.dataCollectorTemplate");
		}
		return dataCollectorTemplate;
	}
}
