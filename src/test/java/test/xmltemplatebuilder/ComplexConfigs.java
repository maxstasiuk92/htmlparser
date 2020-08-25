package test.xmltemplatebuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import htmlparser.DataCollector;
import htmlparser.ParsingSpecialTags;
import htmlparser.Template;
import htmlparser.data.Composition;
import htmlparser.data.collectors.CompositionCollector;
import htmlparser.data.collectors.CompositionCollectorTemplate;
import htmlparser.data.collectors.ParameterCollectorTemplate;
import htmlparser.templatebuilders.XmlConfigSpecialTags;

import htmlparser.elements.MarkupComparator.ReferenceScope;
import htmlparser.elements.MarkupComparatorTemplate;
import htmlparser.elements.TemplateSearcherTemplate;
import htmlparser.elements.MarkupComparator.ReferenceHierarchy;
import htmlparser.elements.TemplateSearcher.Traverse;
import htmlparser.elements.TemplateSearcher.StopOn;


public class ComplexConfigs implements XmlConfigSpecialTags, ParsingSpecialTags{
	
	private final static String nameList = "List";
	private final static String configList = 
			"<"+configRootTag + " "
					+ dataCollectorAttribute + "=\"" + nullAttributeValue + "\" "
					+ templateClassAttribute +"=\""+ MarkupComparatorTemplate.class.getCanonicalName() + "\" "
					+ ReferenceScope.class.getSimpleName() + "=\""+ ReferenceScope.SUBSET.toString() + "\" "
					+ ReferenceHierarchy.class.getSimpleName() + "=\"" + ReferenceHierarchy.EXACT.toString() + "\">\n"
			+ "\t<ul>\n"
			+ "\t\t<li>not valuable</li>\n"
			+ "\t\t<li><" + valuableTextTag + ">valuable text</" + valuableTextTag + "></li>\n"
			+ "\t\t<li></li>\n"
			+ "\t</ul>\n"
			+ "</" + configRootTag + ">\n";
	private final static String htmlList = 
			"<ul>\n"
			+ "\t<li>not valuable</li>\n"
			+ "\t<li>valuable text</li>\n"
			+ "\t<li></li>\n"
			+ "</ul>\n";
	
	private final static String nameListWithParameters = "ListWithParameters";
	private final static String configListWithParameters = 
			"<"+configRootTag + " "
					+ dataCollectorAttribute + "=\"" + ParameterCollectorTemplate.class.getCanonicalName() + "\" "
					+ templateClassAttribute +"=\""+ MarkupComparatorTemplate.class.getCanonicalName() + "\" "
					+ ReferenceScope.class.getSimpleName() + "=\""+ ReferenceScope.SUBSET.toString() + "\" "
					+ ReferenceHierarchy.class.getSimpleName() + "=\"" + ReferenceHierarchy.EXACT.toString() + "\">\n"
			+ "\t<ul>\n"
			+ "\t\t<li><" + dataTag + " " + parameterNameAttribute + "=\"li_param1\"/></li>\n"
			+ "\t\t<li><" + dataTag + " " + parameterNameAttribute + "=\"li_param2\"/></li>\n"
			+ "\t\t<li><" + dataTag + " " + parameterNameAttribute + "=\"li_param3\"/></li>\n"
			+ "\t</ul>\n"
			+ "</" + configRootTag + ">\n";
	private final static String htmlListWithParameters = 
			"<ul>\n"
			+ "\t<li>li_value1</li>\n"
			+ "\t<li>li_value2</li>\n"
			+ "\t<li>li_value3</li>\n"
			+ "</ul>\n";
	
	private final static String nameTableWithParameters = "TableWithParameters";
	private final static String configTableWithParameters =
			"<"+configRootTag + " "
					+ dataCollectorAttribute + "=\"" + ParameterCollectorTemplate.class.getCanonicalName() + "\" "
					+ templateClassAttribute +"=\""+ MarkupComparatorTemplate.class.getCanonicalName() + "\" "
					+ ReferenceScope.class.getSimpleName() + "=\""+ ReferenceScope.SUBSET.toString() + "\" "
					+ ReferenceHierarchy.class.getSimpleName() + "=\"" + ReferenceHierarchy.EXACT.toString() + "\">\n"
			+ "\t<table>\n"
			+ "\t\t<tbody>"
			+ "\t\t\t<tr><td><" + dataTag + " " + parameterNameAttribute + "=\"td_param1\"/></td></tr>\n"
			+ "\t\t\t<tr><td><" + dataTag + " " + parameterNameAttribute + "=\"td_param2\"/></td></tr>\n"
			+ "\t\t\t<tr><td><" + dataTag + " " + parameterNameAttribute + "=\"td_param3\"/></td></tr>\n"
			+ "\t\t</tbody>"
			+ "\t</table>\n"
			+ "</" + configRootTag + ">\n";
	private final static String htmlTableWithParameters =
			"<table>\n"
			+ "\t<tbody>"
			+ "\t\t<tr><td>td_value1</td></tr>\n"
			+ "\t\t<tr><td>td_value2</td></tr>\n"
			+ "\t\t<tr><td>td_value3</td></tr>\n"
			+ "\t</tbody>"
			+ "</table>\n";
	
	private final static String nameTemplateSearcher = "TemplateSearcher";
	private final static String configTemplateSearcher =
			"<"+configRootTag + " "
					+ dataCollectorAttribute + "=\"" + CompositionCollectorTemplate.class.getCanonicalName() + "\" "
					+ templateClassAttribute +"=\""+ TemplateSearcherTemplate.class.getCanonicalName() + "\" "
					+ Traverse.class.getSimpleName() + "=\""+ Traverse.DEEP.toString() + "\" "
					+ StopOn.class.getSimpleName() + "=\"" + StopOn.NO_NODES.toString() + "\">\n"
			+ "\t<" + parserTag + " " + parserNameAttribute + "=\"" + nameList + "\"/>\n"
			+ "\t<" + parserTag + " " + parserNameAttribute + "=\"" + nameListWithParameters + "\"/>\n"
			+ "\t<" + parserTag + " " + parserNameAttribute + "=\"" + nameTableWithParameters + "\"/>\n"
			+ "</" + configRootTag + ">\n";
	
	private final static String htmlTemplateSearcher = 
				htmlList 
				+ htmlListWithParameters
				+ htmlList
				+ htmlList //multiple lists
				+ htmlList
				+ htmlTableWithParameters;
	
	private final static String[][] allConfigurations = 
			{{nameList, configList},
			{nameListWithParameters, configListWithParameters},
			{nameTableWithParameters, configTableWithParameters},
			{nameTemplateSearcher, configTemplateSearcher}};
	
	public static String getTopmostConfigName() {
		return nameTemplateSearcher;
	}
	
	public static String getTopmostHtml() {
		return htmlTemplateSearcher;
	}
	
	public static boolean correctDataForTopmostHtml(DataCollector dataCollector) {
		boolean result = true;
		Composition c;
		if (((CompositionCollector)dataCollector).getCompositionList().size() != 2) {
			return false;
		}
		
		c = ((CompositionCollector)dataCollector).getCompositionList().get(0);
		if (c.size() != 3) {
			return false;
		}
		result &= "li_param1".equals(c.getParameter(0).getName()) & "li_value1".equals(c.getParameter(0).getValue());
		result &= "li_param2".equals(c.getParameter(1).getName()) & "li_value2".equals(c.getParameter(1).getValue());
		result &= "li_param3".equals(c.getParameter(2).getName()) & "li_value3".equals(c.getParameter(2).getValue());
		
		c = ((CompositionCollector)dataCollector).getCompositionList().get(1);
		if (c.size() != 3) {
			return false;
		}
		result &= "td_param1".equals(c.getParameter(0).getName()) & "td_value1".equals(c.getParameter(0).getValue());
		result &= "td_param2".equals(c.getParameter(1).getName()) & "td_value2".equals(c.getParameter(1).getValue());
		result &= "td_param3".equals(c.getParameter(2).getName()) & "td_value3".equals(c.getParameter(2).getValue());
		return result;
	}
	
	public static void createAllConfigFiles(File baseDir) {
		File file;
		for (String[] configuration : allConfigurations) {
			file = new File(baseDir, configuration[0]);
			writeToFile(file, configuration[1]);
		}
	}
	
	public static void deleteAllConfigFiles(File baseDir) {
		File file;
		for (String[] configuration : allConfigurations) {
			file = new File(baseDir, configuration[0]);
			file.delete();
		}
	}
	
	public static boolean isCorrectInstance(String templateName, Template templateInst) {
		boolean result = false;
		switch (templateName) {
		case nameList:
			result = templateInst instanceof MarkupComparatorTemplate;
			break;
		case nameListWithParameters:
			result = templateInst instanceof MarkupComparatorTemplate;
			break;
		case nameTableWithParameters:
			result = templateInst instanceof MarkupComparatorTemplate;
			break;
		case nameTemplateSearcher:
			result = templateInst instanceof TemplateSearcherTemplate;
			break;
		}
		return result;
	}
	
	public static boolean allConfigsPresent(Map<String, Template> templateMap) {
		if (allConfigurations.length != templateMap.size()) {
			return false;
		}
		for (String[] config : allConfigurations) {
			if (!templateMap.containsKey(config[0])) {
				return false;
			}
		}
		return true;
	}
	
	private static void writeToFile(File file, String text) {
		final boolean autoFlush = true;
		PrintStream writer = null;
		try {
			file.createNewFile();
			writer = new PrintStream(new FileOutputStream(file), autoFlush, StandardCharsets.UTF_8.name());
			writer.print(text);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				writer.close();
			} catch (NullPointerException e) {
				//nothing to do
			}
		}
	}
	
	public static void main(String[] args) {
		System.out.println(nameList);
		System.out.println(configList);
		System.out.println(htmlList);
		
		System.out.println(nameListWithParameters);
		System.out.println(configListWithParameters);
		System.out.println(htmlListWithParameters);
		
		System.out.println(nameTableWithParameters);
		System.out.println(configTableWithParameters);
		System.out.println(htmlTableWithParameters);
		
		System.out.println(nameTemplateSearcher);
		System.out.println(configTemplateSearcher);
		
	}
	
}
