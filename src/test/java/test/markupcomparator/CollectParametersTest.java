package test.markupcomparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


import htmlparser.ParsingSpecialTags;
import htmlparser.data.Parameter;
import htmlparser.data.collectors.ParameterCollector;
import htmlparser.elements.MarkupComparator.ReferenceHierarchy;
import htmlparser.elements.MarkupComparator.ReferenceScope;
import htmlparser.sequencer.HtmlParsingSequencer;

public class CollectParametersTest implements ParsingSpecialTags {
	
	final static String sameLevelRefHtml = 
			"<" + dataTag + " " + parameterNameAttribute + "=\"param1\"/>"
			+ "<h1></h1>"
			+ "<" + dataTag + " " + parameterNameAttribute + "=\"param2\"/>"
			+ "<h2></h2>"
			+ "<" + dataTag + " " + parameterNameAttribute + "=\"param3\"/>";
	
	final static String differentLevelRefHtml =
			"<ul>"
				+ "<li>"
					+ "<ul>"
						+ "<li>"
							+ "<h1><"+ dataTag + " " + parameterNameAttribute + "=\"param1\"></h1>"
						+ "</li>"
						+ "<li>"
							+ "<" + dataTag + " " + parameterNameAttribute + "=\"param2\">"
						+ "</li>"
					+ "</ul>"
				+"</li>"
				+ "<li>"
					+ "<" + dataTag + " " + parameterNameAttribute + "=\"param3\">"
				+ "</li>"
			+ "</ul>"
			+ "<" + dataTag + " " + parameterNameAttribute + "=\"param4\">";
	
	@ParameterizedTest(name = "data-set {index}")
	@ValueSource(strings = {
				"value1"
				+ "<h1></h1>"
				+ "value2"
				+ "<h2></h2>"
				+ "value3"})
	public void parametersOnSameLevel(String html) {
		MarkupTemplate template = new MarkupTemplate(sameLevelRefHtml, ReferenceScope.ALL, ReferenceHierarchy.EXACT);
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		Node startNode = Jsoup.parse(html).body().childNode(0);
		
		sequencer.parse(template, startNode);
		//assertTimeoutPreemptively(Duration.ofMillis(100), ()->sequencer.parse(template, startNode));
		assertTrue(sequencer.isParsed());
		
		ParameterCollector parameterCollector = (ParameterCollector)sequencer.getDataCollector();
		List<Parameter> parameterList = parameterCollector.getParameterList();
		
		assertEquals(parameterList.size(), 3);
		assertTrue(parameterList.get(0).getName().equals("param1"));
		assertTrue(parameterList.get(0).getValue().equals("value1"));
		
		assertTrue(parameterList.get(1).getName().equals("param2"));
		assertTrue(parameterList.get(1).getValue().equals("value2"));
		
		assertTrue(parameterList.get(2).getName().equals("param3"));
		assertTrue(parameterList.get(2).getValue().equals("value3"));
	}
	
	@ParameterizedTest(name = "data-set {index}")
	@ValueSource(strings = {
			"<ul>"
				+ "<li>"
					+ "<ul>"
						+ "<li>"
							+ "<h1>value1</h1>"
						+ "</li>"
						+ "<li>"
							+ "value2"
						+ "</li>"
					+ "</ul>"
				+"</li>"
				+ "<li>"
					+ "value3"
				+ "</li>"
			+ "</ul>"
			+ "value4"
			})
	public void parametersOnDifferentLevels(String html) {
		MarkupTemplate template = new MarkupTemplate(differentLevelRefHtml, ReferenceScope.ALL, ReferenceHierarchy.EXACT);
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		Node startNode = Jsoup.parse(html).body().childNode(0);
		
		assertTimeoutPreemptively(Duration.ofMillis(100), ()->sequencer.parse(template, startNode));
		assertTrue(sequencer.isParsed());
		
		ParameterCollector parameterCollector = (ParameterCollector)sequencer.getDataCollector();
		List<Parameter> parameterList = parameterCollector.getParameterList();
		
		assertEquals(parameterList.size(), 4);
		assertTrue(parameterList.get(0).getName().equals("param1"));
		assertTrue(parameterList.get(0).getValue().equals("value1"));
		
		assertTrue(parameterList.get(1).getName().equals("param2"));
		assertTrue(parameterList.get(1).getValue().equals("value2"));
		
		assertTrue(parameterList.get(2).getName().equals("param3"));
		assertTrue(parameterList.get(2).getValue().equals("value3"));
		
		assertTrue(parameterList.get(3).getName().equals("param4"));
		assertTrue(parameterList.get(3).getValue().equals("value4"));
	}
}
