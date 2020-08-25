package test.xmltemplatebuilder;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static test.utils.XmlFileTemplateBuilderReflection.getTemplateRegister;

import htmlparser.ParsingSpecialTags;
import htmlparser.Template;
import htmlparser.sequencer.HtmlParsingSequencer;
import htmlparser.templatebuilders.XmlConfigSpecialTags;
import htmlparser.templatebuilders.XmlFileTemplateBuilder;

import static test.xmltemplatebuilder.ComplexConfigs.allConfigsPresent;
import static test.xmltemplatebuilder.ComplexConfigs.createAllConfigFiles;
import static test.xmltemplatebuilder.ComplexConfigs.deleteAllConfigFiles;
import static test.xmltemplatebuilder.ComplexConfigs.getTopmostConfigName;
import static test.xmltemplatebuilder.ComplexConfigs.getTopmostHtml;
import static test.xmltemplatebuilder.ComplexConfigs.isCorrectInstance;
import static test.xmltemplatebuilder.ComplexConfigs.correctDataForTopmostHtml;


public class NewTemplateRegistringTest implements ParsingSpecialTags, XmlConfigSpecialTags {
	
	final static String basePath = "c:/temp";
	
	@BeforeAll
	public static void create() {
		File baseDir = new File(basePath);
		baseDir.mkdirs();
		createAllConfigFiles(baseDir);
	}
	
	@Test
	public void registerNewTemplatesCheck() {
		final String templateName = getTopmostConfigName();
		XmlFileTemplateBuilder builder = new XmlFileTemplateBuilder(basePath);
		Template template = builder.getTemplate(templateName);
		Map<String, Template> templateRegistry = getTemplateRegister(builder);
		assertTrue(isCorrectInstance(templateName, template));
		for (Entry<String, Template> e : templateRegistry.entrySet()) {
			String name = e.getKey();
			assertTrue(isCorrectInstance(name, e.getValue()), "Failed on " + name);
		}
		assertTrue(allConfigsPresent(templateRegistry));
	}
	
	@Test
	public void correctTemplateMapCheck() {
		final String templateName = getTopmostConfigName();
		XmlFileTemplateBuilder builder = new XmlFileTemplateBuilder(basePath);
		Template template = builder.getTemplate(templateName);
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		Node startNode = Jsoup.parse(getTopmostHtml()).body().childNode(0);
		sequencer.parse(template, startNode);
		assertTrue(sequencer.isParsed());
		assertTrue(correctDataForTopmostHtml(sequencer.getDataCollector()));
		
	}
	
	@AfterAll
	public static void delete() {
		File baseDir = new File(basePath);
		deleteAllConfigFiles(baseDir);
	}
	
}
