package test.utils;

import java.lang.reflect.Field;
import java.util.Map;

import htmlparser.Template;
import htmlparser.templatebuilders.XmlFileTemplateBuilder;

public class XmlFileTemplateBuilderReflection {
	
	@SuppressWarnings("unchecked")
	public static Map<String, Template> getTemplateRegister(XmlFileTemplateBuilder builder) {
		Map<String, Template> templateRegister; 
		try {
			Field f = builder.getClass().getSuperclass().getDeclaredField("templateRegister");
			f.setAccessible(true);
			templateRegister = (Map<String, Template>)f.get(builder);
		} catch (Exception e) {
			throw new RuntimeException("Not reflected XmlFileTemplateBuilder.templateRegister", e);
		}
		return templateRegister;
	}
}
