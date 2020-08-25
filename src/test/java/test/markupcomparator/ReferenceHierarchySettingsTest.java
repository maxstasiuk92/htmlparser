package test.markupcomparator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import htmlparser.elements.MarkupComparator.ReferenceHierarchy;
import htmlparser.elements.MarkupComparator.ReferenceScope;
import htmlparser.sequencer.HtmlParsingSequencer;

public class ReferenceHierarchySettingsTest {
	
	final static String exactHierarchyTemplate =
			"<ul>"
				+ "<li>"
					+ "<ul>"
						+ "<li>"
							+ "<h1></h1>"
							+ "<h2></h2>"
						+ "</li>"
							
						+ "<li>"
							+ "<h3></h3>"
							+ "<h4></h4>"
						+ "</li>"
					+ "</ul>"
					
					+ "<ul>"
						+ "<li>"
								+ "<h5></h5>"
						+ "</li>"
					+ "</ul>"
				+"</li>"
					
				+ "<li>"
				+ "</li>"
			+ "</ul>";
	
	@ParameterizedTest(name = "data-set {index}")
	@ValueSource(strings = {
			"<ul>"
				+ "<li>"
					+ "<ul>"
						+ "<li>"
							+ "<h1></h1>"
							+ "<h2></h2>"
						+ "</li>"
							
						+ "<li>"
							+ "<h3></h3>"
							+ "<h4></h4>"
						+ "</li>"
					+ "</ul>"
					
					+ "<ul>"
						+ "<li>"
							+ "<h5></h5>"
						+ "</li>"
					+ "</ul>"
				+"</li>"
					
				+ "<li>"
				+ "</li>"
			+ "</ul>",
			//with non-valuable text
			"txt"
			+ "<ul>"
				+ "<li>"
					+ "<ul>"
						+ "<li>"
							+ "<h1>txt</h1>"
							+ "<h2>txt</h2>"
						+ "</li>"
							
						+ "<li>"
							+ "<h3>txt</h3>"
							+ "<h4>txt</h4>"
						+ "</li>"
					+ "</ul>"
					
					+ "<ul>"
						+ "<li>"
							+ "<h5>txt</h5>"
						+ "</li>"
					+ "</ul>"
				+"</li>"
					
				+ "<li>"
				+ "</li>"
			+ "</ul>"
			+ "txt"
			})
	public void exactHierarchyMatch(String html) {
		MarkupTemplate template = new MarkupTemplate(exactHierarchyTemplate, ReferenceScope.ALL, ReferenceHierarchy.EXACT);
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		Node startNode = Jsoup.parse(html).body().childNode(0);
		
		sequencer.parse(template, startNode);
		//assertTimeoutPreemptively(Duration.ofMillis(100), ()->sequencer.parse(template, startNode));
		assertTrue(sequencer.isParsed());
	}
	
	@ParameterizedTest(name = "data-set {index}")
	@ValueSource(strings = {
			"<ul>"
				+ "<li>"
					+ "<ul>"
						+ "<li>"
							+ "<h1></h1>"
							//no node
						+ "</li>"
							
						+ "<li>"
							+ "<h3></h3>"
							+ "<h4></h4>"
						+ "</li>"
					+ "</ul>"
					
					+ "<ul>"
						+ "<li>"
							+ "<h5></h5>"
						+ "</li>"
					+ "</ul>"
				+"</li>"
					
				+ "<li>"
				+ "</li>"
			+ "</ul>",
			
			"<ul>"
				+ "<li>"
					+ "<ul>"
						+ "<li>"
							+ "<h1></h1>"
							+ "<h2></h2>"
						+ "</li>"
							
						+ "<li>"
							+ "<h3></h3>"
							+ "<h4></h4>"
							+ "<h6></h6>" //extra node
						+ "</li>"
					+ "</ul>"
					
					+ "<ul>"
						+ "<li>"
							+ "<h5></h5>"
						+ "</li>"
					+ "</ul>"
				+"</li>"
					
				+ "<li>"
				+ "</li>"
			+ "</ul>",
			
			"<ul>"
				+ "<li>"
					+ "<ul>"
						+ "<li>"
							+ "<h1></h1>"
							+ "<h2></h2>"
						+ "</li>"
							
						+ "<li>"
							+ "<h3></h3>"
							+ "<h4></h4>"
						+ "</li>"
					+ "</ul>"
					
					+ "<ul>"
						+ "<li>"
							+ "<h5></h5>"
						+ "</li>"
					+ "</ul>"
				+"</li>"
					
				+ "<li>"
				+ "</li>"
			+ "</ul>"
			+ "<h6></h6>", //extra node
			
			"<ul>"
				+ "<li>"
					+ "<ul>"
						+ "<li>"
							+ "<h1></h1>"
							+ "<h2></h2>"
						+ "</li>"
							
						+ "<li>"
							+ "<h3></h3>"
							+ "<h4></h4>"
						+ "</li>"
					+ "</ul>"
					
					+ "<ul>"
						+ "<li>"
							+ "<h5><h6></h6></h5>" //extra level
						+ "</li>"
					+ "</ul>"
				+"</li>"
					
				+ "<li>"
				+ "</li>"
			+ "</ul>"
			})
	public void exactHierarchyNotMatch(String html) {
		MarkupTemplate template = new MarkupTemplate(exactHierarchyTemplate, ReferenceScope.ALL, ReferenceHierarchy.EXACT);
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		Node startNode = Jsoup.parse(html).body().childNode(0);
		
		sequencer.parse(template, startNode);
		//assertTimeoutPreemptively(Duration.ofMillis(100), ()->sequencer.parse(template, startNode));
		assertFalse(sequencer.isParsed());
	}
	
	final static String basicHierarchyTemplate = 
			"<ul>"
				+ "<li>"
					+ "<ul>"
						+ "<li>"
							+ "<h1></h1>"
							+ "<h2></h2>"
						+ "</li>"
					+ "</ul>"
				+"</li>"
					
				+ "<li>"
				+ "</li>"
			+ "</ul>";
	
	@ParameterizedTest(name = "data-set {index}")
	@ValueSource(strings = {
			"<ul>"
				+ "<li>"
					+ "<ul>"
						+ "<li>"
							+ "<h1></h1>"
							+ "<h2></h2>"
						+ "</li>"
					+ "</ul>"
				+"</li>"
					
				+ "<li>"
				+ "</li>"
			+ "</ul>",
			
			"<ul>"
				+ "<li>"
					+ "<ul>"
						+ "<li>"
							+ "<h1></h1>"
							+ "<h2></h2>"
						+ "</li>"
					+ "</ul>"
				+"</li>"
					
				+ "<li>"
					+ "<h3></h3>" //extra child nodes
					+ "<h4></h4>"
				+ "</li>"
			+ "</ul>"
			})
	public void basicHierarchyMatch(String html) {
		MarkupTemplate template = new MarkupTemplate(basicHierarchyTemplate, ReferenceScope.ALL, ReferenceHierarchy.BASIC);
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		Node startNode = Jsoup.parse(html).body().childNode(0);
		
		sequencer.parse(template, startNode);
		//assertTimeoutPreemptively(Duration.ofMillis(100), ()->sequencer.parse(template, startNode));
		assertTrue(sequencer.isParsed());
	}
	
	@ParameterizedTest(name = "data-set {index}")
	@ValueSource(strings = {
			"<ul>"
				+ "<li>"
					+ "<ul>"
						+ "<li>"
							+ "<h1></h1>"
							//no child node
						+ "</li>"
					+ "</ul>"
				+"</li>"
					
				+ "<li>"
				+ "</li>"
			+ "</ul>"
			})
	public void basicHierarchyNotMatch(String html) {
		MarkupTemplate template = new MarkupTemplate(basicHierarchyTemplate, ReferenceScope.ALL, ReferenceHierarchy.BASIC);
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		Node startNode = Jsoup.parse(html).body().childNode(0);
		
		sequencer.parse(template, startNode);
		//assertTimeoutPreemptively(Duration.ofMillis(100), ()->sequencer.parse(template, startNode));
		assertFalse(sequencer.isParsed());
	}
}
