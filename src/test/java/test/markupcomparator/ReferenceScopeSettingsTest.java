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
import htmlparser.sequencer.HtmlParsingSequencer;

import htmlparser.elements.MarkupComparator.ReferenceHierarchy;
import htmlparser.elements.MarkupComparator.ReferenceScope;

public class ReferenceScopeSettingsTest implements ParsingSpecialTags {
	final static String refHtml = "<h2></h2><h3></h3><h4></h4>";
	
	@ParameterizedTest(name = "data-set {index}")
	@ValueSource(strings = {
			"<h2></h2><h3></h3><h4></h4>",
			//with non-valuable text
			"txt<h2></h2>txt<h3></h3>txt<h4></h4>txt"
			})
	public void allSequenceMatch(String html) {
		MarkupTemplate template = new MarkupTemplate(refHtml, ReferenceScope.ALL, ReferenceHierarchy.EXACT);
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		Node startNode = Jsoup.parse(html).body().childNode(0);
		
		assertTimeoutPreemptively(Duration.ofMillis(100), ()->sequencer.parse(template, startNode));
		assertTrue(sequencer.isParsed());
	}
	
	@ParameterizedTest(name = "data-set {index}")
	@ValueSource(strings = {
			"<h1></h1><h2></h2><h3></h3>",
			"<h2></h2><h3></h3><h5></h5>",
			"<h2></h2><h3></h3><h4></h4><h5></h5>",
			"<h2></h2><h3></h3>",
			
			"txt<h1></h1>txt<h2></h2>txt<h3></h3>txt",
			"txt<h2></h2>txt<h3></h3>txt<h5></h5>txt",
			"txt<h2></h2>txt<h3></h3>txt<h4></h4>txt<h5></h5>txt",
			"txt<h2></h2>txt<h3></h3>txt"
			})
	public void allSequenceNotMatch(String html) {
		MarkupTemplate template = new MarkupTemplate(refHtml, ReferenceScope.ALL, ReferenceHierarchy.EXACT);
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		Node startNode = Jsoup.parse(html).body().childNode(0);
		
		assertTimeoutPreemptively(Duration.ofMillis(100), ()->sequencer.parse(template, startNode));
		assertFalse(sequencer.isParsed());
	}
	
	@ParameterizedTest(name = "data-set {index}")
	@ValueSource(strings = {
			"<h2></h2><h3></h3><h4></h4><h5></h5><h6></h6>",
			"<h2></h2><h3></h3><h4></h4>",
			
			"txt<h2></h2>txt<h3></h3>txt<h4></h4>txt<h5></h5>txt<h6></h6>txt",
			"txt<h2></h2>txt<h3></h3>txt<h4></h4>txt"
			})
	public void subSequenceMatch(String html) {
		MarkupTemplate template = new MarkupTemplate(refHtml, ReferenceScope.SUBSET, ReferenceHierarchy.EXACT);
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		Node startNode = Jsoup.parse(html).body().childNode(0);
		
		assertTimeoutPreemptively(Duration.ofMillis(100), ()->sequencer.parse(template, startNode));
		assertTrue(sequencer.isParsed());
	}
	
	@ParameterizedTest(name = "data-set {index}")
	@ValueSource(strings = {
			"<h1></h1><h2></h2><h3></h3><h4></h4><h5></h5>",
			"<h1></h1><h5></h5>",
			"<h2></h2><h3></h3><h5></h5><h6></h6>",
			"<h2></h2><h3></h3>",
			
			"txt<h1></h1>txt<h2></h2>txt<h3></h3>txt<h4></h4>txt<h5></h5>txt",
			"txt<h1></h1>txt<h5></h5>txt",
			"txt<h2></h2>txt<h3></h3>txt<h5></h5>txt<h6></h6>txt",
			"txt<h2></h2>txt<h3></h3>txt"
			})
	public void subSequenceNotMatch(String html) {
		MarkupTemplate template = new MarkupTemplate(refHtml, ReferenceScope.SUBSET, ReferenceHierarchy.EXACT);
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		Node startNode = Jsoup.parse(html).body().childNode(0);
		
		assertTimeoutPreemptively(Duration.ofMillis(100), ()->sequencer.parse(template, startNode));
		assertFalse(sequencer.isParsed());
	}
	
	@ParameterizedTest(name = "data-set {index}")
	@ValueSource(strings = {
				"<h2></h2>"
				+"<h3></h3>"
				+"<h4></h4>"
				
				+"<h2></h2>"
				+"<h3></h3>"
				+"<h4></h4>"
				
				+"<h2></h2>"
				+"<h3></h3>"
				+"<h4></h4>",
				//with non-valuable text
				"txt<h2></h2>"
				+"txt<h3></h3>"
				+"txt<h4></h4>"
				
				+"txt<h2></h2>"
				+"txt<h3></h3>"
				+"txt<h4></h4>"
				
				+"txt<h2></h2>"
				+"txt<h3></h3>"
				+"txt<h4></h4>txt"
				})
	public void returnNodeTest(String html) {
		MarkupTemplate template = new MarkupTemplate(refHtml, ReferenceScope.SUBSET, ReferenceHierarchy.EXACT);
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		Node startNode = Jsoup.parse(html).body().childNode(0);
		
		assertTimeoutPreemptively(Duration.ofMillis(200), ()->sequencer.parse(template, startNode));
		assertTrue(sequencer.isParsed());
		
		assertTimeoutPreemptively(Duration.ofMillis(200), ()->sequencer.parse(template, sequencer.getNextNode()));
		assertTrue(sequencer.isParsed());
		
		assertTimeoutPreemptively(Duration.ofMillis(200), ()->sequencer.parse(template, sequencer.getNextNode()));
		assertTrue(sequencer.isParsed());
	}
}
