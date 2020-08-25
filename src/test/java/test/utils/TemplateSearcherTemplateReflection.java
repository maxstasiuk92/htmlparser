package test.utils;

import java.lang.reflect.Field;
import java.util.Map;

import htmlparser.elements.TemplateSearcher.StopOn;
import htmlparser.elements.TemplateSearcher.Traverse;
import htmlparser.elements.TemplateSearcherTemplate;
import htmlparser.DataCollectorTemplate;
import htmlparser.Template;

public class TemplateSearcherTemplateReflection {
	
	public static Traverse getTraverse(TemplateSearcherTemplate template) {
		Traverse traverse;
		try {
			Field f = template.getClass().getDeclaredField("traverse");
			f.setAccessible(true);
			traverse = (Traverse)f.get(template);
		} catch (Exception e) {
			throw new RuntimeException("Not reflected TemplateSearcherTemplate.traverse");
		}
		return traverse;
	}
	
	public static StopOn getStopOn(TemplateSearcherTemplate template) {
		StopOn stopOn;
		try {
			Field f = template.getClass().getDeclaredField("stopOn");
			f.setAccessible(true);
			stopOn = (StopOn)f.get(template);
		} catch (Exception e) {
			throw new RuntimeException("Not reflected TemplateSearcherTemplate.stopOn");
		}
		return stopOn;
	}
	
	public static DataCollectorTemplate getDataCollectorTemplate(TemplateSearcherTemplate template) {
		DataCollectorTemplate dataCollectorTemplate;
		try {
			Field f = template.getClass().getDeclaredField("dataCollectorTemplate");
			f.setAccessible(true);
			dataCollectorTemplate = (DataCollectorTemplate)f.get(template);
		} catch (Exception e) {
			throw new RuntimeException("Not reflected TemplateSearcherTemplate.dataCollectorTemplate");
		}
		return dataCollectorTemplate;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Template> getParserTemplates(TemplateSearcherTemplate template) {
		Map<String, Template> parserTemplates;
		try {
			Field f = template.getClass().getDeclaredField("parserTemplates");
			f.setAccessible(true);
			parserTemplates = (Map<String, Template>)f.get(template);
		} catch (Exception e) {
			throw new RuntimeException("Not reflected TemplateSearcherTemplate.parserTemplates");
		}
		return parserTemplates;
	}
}
