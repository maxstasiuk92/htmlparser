package test.markupcomparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import htmlparser.Element;
import htmlparser.Template;
import htmlparser.ParsingSpecialTags;
import htmlparser.data.collectors.InnerParameterCollector;
import htmlparser.data.collectors.ParameterCollector;
import htmlparser.data.Parameter;
import htmlparser.elements.MarkupComparator;
import htmlparser.sequencer.HtmlParsingSequencer;
import htmlparser.elements.MarkupComparator.ReferenceHierarchy;
import htmlparser.elements.MarkupComparator.ReferenceScope;

public class TagWithParserTest implements ParsingSpecialTags {
	
	@ParameterizedTest(name = "data-set {index}")
	@ValueSource(strings = {
			"<h1></h1>"
			+ "<h2>value1</h2>"
			+ "<ul>"
				+ "<li></li>"
				+ "<li>value2</li>"
				+ "<li>value3</li>"
			+ "</ul>"
			+ "<h3>value4</h3>"
			+ "", //nothing for empty template
			//extra not valuable text everywhere
			"txt"
			+ "<h1></h1>"
			+ "txt"
			+ "<h2>value1</h2>"
			+ "txt"
			+ "<ul>"
				+ "<li>txt</li>"
				+ "<li>value2</li>"
				+ "<li>value3</li>"
			+ "</ul>"
			+ "txt"
			+ "<h3>value4</h3>"
			+ "txt" //not valuable text for empty template
			})
	public void match(String html) {
		CascadedTemplate template = new CascadedTemplate();
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		Node startNode = Jsoup.parse(html).body().childNode(0);
		
		sequencer.parse(template, startNode);
		//assertTimeoutPreemptively(Duration.ofMillis(100), ()->sequencer.parse(template, startNode));
		assertTrue(sequencer.isParsed());
		ParameterCollector dataCollector = (ParameterCollector)sequencer.getDataCollector();
		List<Parameter> parameterList = dataCollector.getParameterList();
		assertEquals(4, parameterList.size());
		
		assertEquals("param1", parameterList.get(0).getName());
		assertEquals("value1", parameterList.get(0).getValue());
		
		assertEquals("param2", parameterList.get(1).getName());
		assertEquals("value2", parameterList.get(1).getValue());
		
		assertEquals("param3", parameterList.get(2).getName());
		assertEquals("value3", parameterList.get(2).getValue());
		
		assertEquals("param4", parameterList.get(3).getName());
		assertEquals("value4", parameterList.get(3).getValue());
	}
	
	@ParameterizedTest(name = "data-set {index}")
	@ValueSource(strings = {
			"<h1></h1>"
			+ "<h2>value1</h2>"
			+ "<ul>"
				+ "<li></li>"
				+ "<li>value2</li>"
				//no node
			+ "</ul>"
			+ "<h3>value4</h3>"
			+ "", //nothing for empty template			
			"<h1></h1>"
			+ "<h2>value1</h2>"
			+ "<ul>"
				+ "<li></li>"
				+ "<li>value2</li>"
				+ "<li>value3</li>"
			+ "</ul>"
			+ "<h3>value4</h3>"
			+ "<h4></h4>", //node for empty template
			"<h1></h1>"
			+ "<h2>value1</h2>"
			+ "<ul>"
				+ "<li></li>"
				+ "<li>value2</li>"
				+ "<li>value3</li>"
			+ "</ul>"
			+ "<h3></h3>" //no value
			+ "" //nothing for empty template
	})
	public void notMatch(String html) {
		CascadedTemplate template = new CascadedTemplate();
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		Node startNode = Jsoup.parse(html).body().childNode(0);
		
		sequencer.parse(template, startNode);
		//assertTimeoutPreemptively(Duration.ofMillis(100), ()->sequencer.parse(template, startNode));
		assertFalse(sequencer.isParsed());
	}
}

class CascadedTemplate implements Template, ParsingSpecialTags {
	
	/*
	 *  OuterTemplate
	 * ---- InnerStructureTemplate
	 * ---- InnerDataTemplate
	 * ---- InnerEmptyTemplate
	 */
	
	Node startReferenceNode;
	MarkupComparator parserElement;
	
	OuterTemplate outerTemplate;
	InnerStructureTemplate innerStructureTemplate;
	InnerDataTemplate innerDataTemplate;
	InnerEmptyTemplate innerEmptyTemplate; 
	
	CascadedTemplate() {
		outerTemplate = new OuterTemplate();
		innerStructureTemplate = new InnerStructureTemplate();
		innerDataTemplate = new InnerDataTemplate();
		innerEmptyTemplate = new InnerEmptyTemplate();
	}
	
	class OuterTemplate implements Template, ParsingSpecialTags {
		final String htmlTemplate = 
				"<h1></h1>"
				+ "<h2><" + dataTag + " " + parameterNameAttribute + "=\"param1\"/></h2>"
				+ "<" + parserTag + " " + parserNameAttribute + "=\"struct\"/>"
				+ "<h3><" + dataTag + " " + parameterNameAttribute + "=\"param4\"/></h3>"
				+ "<" + parserTag + " " + parserNameAttribute + "=\"empty\"/>";
		
		@Override
		public Element getParserElement() {
			ParameterCollector dataCollector = new ParameterCollector();
			HashMap<String, Template> parserTemplates = new HashMap<>();
			parserTemplates.put("struct", CascadedTemplate.this.innerStructureTemplate);
			parserTemplates.put("empty", CascadedTemplate.this.innerEmptyTemplate);
			Node startReferenceNode = Jsoup.parse(htmlTemplate).body().childNode(0);
			
			MarkupComparator parserElement = new MarkupComparator(dataCollector, parserTemplates,
					startReferenceNode, ReferenceScope.ALL, ReferenceHierarchy.EXACT);
			return parserElement;
		}
		
	}
	
	class InnerStructureTemplate implements Template, ParsingSpecialTags {
		String htmlTemplate = 
				"<ul>"
					+ "<li></li>"
					+ "<" + parserTag + " " + parserNameAttribute + "=\"data\"/>"
				+ "</ul>";
		
		@Override
		public Element getParserElement() {
			InnerParameterCollector dataCollector = new InnerParameterCollector();
			HashMap<String, Template> parserTemplates = new HashMap<>();
			parserTemplates.put("data", CascadedTemplate.this.innerDataTemplate);
			Node startReferenceNode = Jsoup.parse(htmlTemplate).body().childNode(0);
			
			MarkupComparator parserElement = new MarkupComparator(dataCollector, parserTemplates,
					startReferenceNode, ReferenceScope.SUBSET, ReferenceHierarchy.EXACT);
			return parserElement;
		}
		
	}
	
	class InnerDataTemplate implements Template, ParsingSpecialTags {
		String htmlTemplate = 
				"<li><" + dataTag + " " + parameterNameAttribute + "=\"param2\"/></li>"
				+ "<li><" + dataTag + " " + parameterNameAttribute + "=\"param3\"/></li>";
				
		
		@Override
		public Element getParserElement() {
			InnerParameterCollector dataCollector = new InnerParameterCollector();
			HashMap<String, Template> noParserTemplates = null;
			Node startReferenceNode = Jsoup.parse(htmlTemplate).body().childNode(0);
			
			MarkupComparator parserElement = new MarkupComparator(dataCollector, noParserTemplates,
					startReferenceNode, ReferenceScope.ALL, ReferenceHierarchy.EXACT);
			return parserElement;
		}
		
	}
	
	class InnerEmptyTemplate implements Template, ParsingSpecialTags {
		String htmlTemplate = "";
		
		@Override
		public Element getParserElement() {
			InnerParameterCollector dataCollector = new InnerParameterCollector();
			HashMap<String, Template> noParserTemplates = null;
			Node startReferenceNode = null;
			
			MarkupComparator parserElement = new MarkupComparator(dataCollector, noParserTemplates,
					startReferenceNode, ReferenceScope.ALL, ReferenceHierarchy.EXACT);
			return parserElement;
		}
		
	}
	
	@Override
	public Element getParserElement() {
		return outerTemplate.getParserElement();
	}
	
}
