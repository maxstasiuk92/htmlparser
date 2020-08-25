package test.templatesearcher;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import htmlparser.ParsingSpecialTags;
import htmlparser.data.Composition;
import htmlparser.data.collectors.CompositionCollector;
import htmlparser.sequencer.HtmlParsingSequencer;

import static htmlparser.elements.TemplateSearcher.StopOn;
import static htmlparser.elements.TemplateSearcher.Traverse;

public class PlainSearchTest implements ParsingSpecialTags {
	
	@ParameterizedTest
	@ValueSource(strings = {
			"<ul>"
				+ "<il>value1</il>"
				+ "<il></il>"
			+ "</ul>"
			+ "<ul>"
				+ "<il></il>"
				+ "<il>value2</il>"
				+ "<il></il>"
			+ "</ul>",
			
			"<ul>"
				+ "<il></il>"
				+ "<il>value1</il>"
				+ "<il></il>"
			+ "</ul>"
			})
	public void findOnlyFirstCheck(String html) {
		SearcherTemplate template = new SearcherTemplate(Traverse.PLAIN, StopOn.FIRST_MATCH);
		Node startNode = Jsoup.parse(html).body().childNode(0);
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		
		sequencer.parse(template, startNode);
		
		assertTrue(sequencer.isParsed());
		CompositionCollector dataCollector = (CompositionCollector)sequencer.getDataCollector();
		List<Composition> allCompositions = dataCollector.getCompositionList();
		assertEquals(1, allCompositions.size());
		
		Composition composition;
		
		composition = allCompositions.get(0);
		assertEquals(1, composition.size());
		//parameter names are different
		assertEquals("value1", composition.getParameter(0).getValue());
	}
	
	
	@ParameterizedTest(name = "data set {index}")
	@ValueSource(strings = {
			"<ul>"
				+ "<il>value1</il>"
				+ "<il></il>"
			+ "</ul>"
			+ "<ul>"
				+ "<il></il>"
				+ "<il>value2</il>"
				+ "<il></il>"
			+ "</ul>",
			
			"<h1></h1>" //extra node
			+ "<h2></h2>" //extra node
			+ "<ul>"
				+ "<il>value1</il>"
				+ "<il></il>"
			+ "</ul>"
			+ "<h3></h3>" //extra node
			+ "<h4></h4>" //extra node
			+ "<ul>"
				+ "<il></il>"
				+ "<il>value2</il>"
				+ "<il></il>"
			+ "</ul>"
			+ "<h5></h5>"
			+ "<h6></h6>"
			})
	public void findAllMatch(String html) {
		SearcherTemplate template = new SearcherTemplate(Traverse.PLAIN, StopOn.NO_NODES);
		Node startNode = Jsoup.parse(html).body().childNode(0);
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		
		sequencer.parse(template, startNode);
		
		assertTrue(sequencer.isParsed());
		CompositionCollector dataCollector = (CompositionCollector)sequencer.getDataCollector();
		List<Composition> allCompositions = dataCollector.getCompositionList();
		assertEquals(2, allCompositions.size());
		
		Composition composition;
		
		composition = allCompositions.get(0);
		assertEquals(1, composition.size());
		assertEquals("param1", composition.getParameter(0).getName());
		assertEquals("value1", composition.getParameter(0).getValue());
		
		composition = allCompositions.get(1);
		assertEquals(1, composition.size());
		assertEquals("param2", composition.getParameter(0).getName());
		assertEquals("value2", composition.getParameter(0).getValue());
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"<ul>"
				+ "<il>value1</il>"
				// no node
			+ "</ul>"
			+ "<ul>"
				+ "<il></il>"
				+ "<il>value2</il>"
				//no node
			+ "</ul>"
			})
	public void findAllNotMatch(String html) {
		SearcherTemplate template = new SearcherTemplate(Traverse.PLAIN, StopOn.NO_NODES);
		Node startNode = Jsoup.parse(html).body().childNode(0);
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		
		sequencer.parse(template, startNode);
		
		assertFalse(sequencer.isParsed());
	}
}

