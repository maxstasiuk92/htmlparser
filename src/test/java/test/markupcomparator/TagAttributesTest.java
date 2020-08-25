package test.markupcomparator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import htmlparser.ParsingSpecialTags;
import htmlparser.elements.MarkupComparator.ReferenceHierarchy;
import htmlparser.elements.MarkupComparator.ReferenceScope;
import htmlparser.sequencer.HtmlParsingSequencer;

public class TagAttributesTest implements ParsingSpecialTags {
	static final String refHtml =
			"<h2></h2>"
			+"<h3 id=\"3\"></h3>"
			+"<h4 id=\"4\" style=\"style4\"></h4>";
	
	@ParameterizedTest(name = "data-set {index}")
	@ValueSource(strings = {
				"<h2></h2>"
				+"<h3 id=\"3\"></h3>"
				+"<h4 id=\"4\" style=\"style4\"></h4>",
			
				"<h2 id=\"2\"></h2>" //extra attribute
				+"<h3 id=\"3\"></h3>"
				+"<h4 id=\"4\" style=\"style4\"></h4>",
				
				"<h2></h2>"
				+"<h3 id=\"3\" style=\"style3\"></h3>" //extra attribute
				+"<h4 id=\"4\" style=\"style4\"></h4>"
			})
	public void attributeMatch(String html) {
		MarkupTemplate template = new MarkupTemplate(refHtml, ReferenceScope.ALL, ReferenceHierarchy.EXACT);
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		Node startNode = Jsoup.parse(html).body().childNode(0);
		
		assertTimeoutPreemptively(Duration.ofMillis(100), ()->sequencer.parse(template, startNode));
		assertTrue(sequencer.isParsed());
	}
	
	@ParameterizedTest(name = "data-set {index}")
	@ValueSource(strings = {
				"<h2></h2>"
				+"<h3></h3>" //absent attribute
				+"<h4 id=\"4\" style=\"style4\"></h4>",
				
				"<h2></h2>"
				+"<h3 id=\"5\"></h3>" //wrong attribute value
				+"<h4 id=\"4\" style=\"style4\"></h4>"
			})
	public void attributeNotMatch(String html) {
		MarkupTemplate template = new MarkupTemplate(refHtml, ReferenceScope.ALL, ReferenceHierarchy.EXACT);
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		Node startNode = Jsoup.parse(html).body().childNode(0);
		
		assertTimeoutPreemptively(Duration.ofMillis(100), ()->sequencer.parse(template, startNode));
		assertFalse(sequencer.isParsed());
	}
}
