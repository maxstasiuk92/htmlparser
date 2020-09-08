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

public class TagWithValuableTextTest implements ParsingSpecialTags{
	
	final static String refHtml = 
			"<h1></h1>"
			+ "<" + valuableTextTag + ">text</" + valuableTextTag +">"
			+ "<h2></h2>"
			+ "<" + valuableTextTag + ">line1\nline2</" + valuableTextTag + ">"
			+ "<h3></h3>";

	@ParameterizedTest(name = "data-set {index}")
	@ValueSource(strings = {
				"<h1></h1>"
				+ "text"
				+ "<h2></h2>"
				+ "line1\nline2"
				+ "<h3></h3>",
			
				"txt"
				+ "<h1></h1>"
				+ "text"
				+ "<h2></h2>"
				+ "line1\nline2"
				+ "<h3></h3>"
				+ "txt",
				
				"<!--comm-->"
				+ "<h1></h1>"
				+ "text"
				+ "<h2></h2>"
				+ "line1\nline2"
				+ "<h3></h3>"
				+ "<!--comm-->"
			})
	public void valuableTextMatch(String html) {
		MarkupTemplate template = new MarkupTemplate(refHtml, ReferenceScope.ALL, ReferenceHierarchy.EXACT);
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		Node startNode = Jsoup.parse(html).body().childNode(0);
		
		assertTimeoutPreemptively(Duration.ofMillis(100), ()->sequencer.parse(template, startNode));
		assertTrue(sequencer.isParsed());
	}
		
	@ParameterizedTest(name = "data-set {index}")
	@ValueSource(strings = {
				"<h1></h1>"
				+ "text2" //wrong text
				+ "<h2></h2>"
				+ "line1\nline2"
				+ "<h3></h3>",
			
				"<h1></h1>"
				+ "text"
				+ "<h2></h2>"
				+ "line1line2" //no line-break
				+ "<h3></h3>",
				
				"<h1></h1>"
				+ "text"
				+ "<h2></h2>"
				+ "<h4></h4>" //tag instead of text
				+ "<h3></h3>"
			})
	public void valuableTextNotMatchTest(String html) {
		MarkupTemplate template = new MarkupTemplate(refHtml, ReferenceScope.ALL, ReferenceHierarchy.EXACT);
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		Node startNode = Jsoup.parse(html).body().childNode(0);
		
		assertTimeoutPreemptively(Duration.ofMillis(100), ()->sequencer.parse(template, startNode));
		assertFalse(sequencer.isParsed());
	}
}
