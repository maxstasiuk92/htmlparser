package test.utils;

import htmlparser.DataCollectorTemplate;
import htmlparser.data.collectors.CompositionCollectorTemplate;
import htmlparser.data.collectors.InnerParameterCollectorTemplate;
import htmlparser.data.collectors.ParameterCollectorTemplate;
import htmlparser.templatebuilders.XmlConfigSpecialTags;

public class ExistingDataCollectorTemplates implements XmlConfigSpecialTags {
	
	public final static String[] collectorTemplateNames = {
			InnerParameterCollectorTemplate.class.getCanonicalName(),
			ParameterCollectorTemplate.class.getCanonicalName(),
			CompositionCollectorTemplate.class.getCanonicalName(),
			nullAttributeValue
		};
	
	public static boolean isCorrectDataCollectorTemplate(String templateName, DataCollectorTemplate templateInst) {
		boolean correct = false;
		if (templateName.equals(InnerParameterCollectorTemplate.class.getCanonicalName())) {
			correct = templateInst instanceof InnerParameterCollectorTemplate;
		} else if (templateName.equals(ParameterCollectorTemplate.class.getCanonicalName())) {
			correct = templateInst instanceof ParameterCollectorTemplate;
		} else if (templateName.equals(CompositionCollectorTemplate.class.getCanonicalName())) {
			correct = templateInst instanceof CompositionCollectorTemplate;
		} else if (templateName.equals(nullAttributeValue)) {
			correct = templateInst == null;
		} else {
			correct = templateInst == null;
		}
		return correct;
	}
}
