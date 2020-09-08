package test.xmltemplatebuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static test.utils.TestUtils.equalTree;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import htmlparser.ParsingSpecialTags;
import htmlparser.Template;
import htmlparser.templatebuilders.XmlConfigSpecialTags;

public class TreeProcessingTest implements ParsingSpecialTags, XmlConfigSpecialTags {
	
	final static String confWithText = 
			"?"
			+ "<" + configRootTag + ">"
				+ "?"
				+ "<ul>"
					+ "?"
					+ "<li>?</li>"
					+ "?"
					+ "<li><" + valuableTextTag + ">text1</" + valuableTextTag + "></li>"
					+ "?"
					+ "<li>"
						+ "?"
						+ "<ul>"
							+ "?"
							+ "<li>?</li>"
							+ "?"
							+ "<li><" + valuableTextTag + ">text2</" + valuableTextTag + "></li>"
							+ "?"
						+ "</ul>"
						+ "?"
					+ "</li>"
					+ "?"
				+ "</ul>"
				+ "?"
			+ "</" + configRootTag + ">";
	
	final static String confWithParsers = 
			"<" + configRootTag + ">"
				+ "<" + parserTag + " " + parserNameAttribute + "=\"parser1\"/>"
				+ "<" + parserTag + " " + parserNameAttribute + "=\"newParser1\"/>"
				+ "<ul>"
					+ "<li><" + parserTag + " " + parserNameAttribute + "=\"newParser2\"/></li>"
					+ "<li><" + parserTag + " " + parserNameAttribute + "=\"parser2\"/></li>"
					+ "<li>"
						+ "<ul>"
							+ "<li><" + parserTag + " " + parserNameAttribute + "=\"parser3\"/></li>"
							+ "<li><" + parserTag + " " + parserNameAttribute + "=\"newParser3\"/></li>"
						+ "</ul>"
					+ "</li>"
				+ "</ul>"
			+ "</" + configRootTag + ">";
	
	@ParameterizedTest(name = "Insert: {0}")
	@ValueSource(strings = {"", "not-valuable", "<!--comment-->"})
	public void removeNonValuableTextCheck(String insert) {
		final String noBaseUri = "";
		TestAdapter builder = new TestAdapter("");
		String test = confWithText.replace("?", insert);
		String reference = confWithText.replace("?", "");
		Node testNode = Jsoup.parse(test, noBaseUri, Parser.xmlParser()).child(0);
		Node refNode = Jsoup.parse(reference, noBaseUri, Parser.xmlParser()).child(0);
		
		Map<String, Template> emptyParserMap = new HashMap<>();
		Queue<String> notRegisteredTemplates = new LinkedList<>();
		builder.processTree(testNode, emptyParserMap, notRegisteredTemplates);
		assertEquals(0, emptyParserMap.size());
		assertEquals(0, notRegisteredTemplates.size());
		assertTrue(equalTree(testNode, refNode));
	}
	
	@Test
	public void collectParserTemplatesCheck() {
		final String noBaseUri = "";
		TestAdapter builder = new TestAdapter("");
		builder.putParserTemplate("parser1", null);
		builder.putParserTemplate("parser2", null);
		builder.putParserTemplate("parser3", null);
		Node testNode = Jsoup.parse(confWithParsers, noBaseUri, Parser.xmlParser()).child(0);
		Map<String, Template> emptyParserMap = new HashMap<>();
		Queue<String> notRegisteredTemplates = new LinkedList<>();
		builder.processTree(testNode, emptyParserMap, notRegisteredTemplates);
		
		assertEquals(6, emptyParserMap.size());
		assertTrue(emptyParserMap.containsKey("parser1"));
		assertTrue(emptyParserMap.containsKey("parser2"));
		assertTrue(emptyParserMap.containsKey("parser3"));
		assertTrue(emptyParserMap.containsKey("newParser1"));
		assertTrue(emptyParserMap.containsKey("newParser2"));
		assertTrue(emptyParserMap.containsKey("newParser3"));
		
		assertEquals(3, notRegisteredTemplates.size());
		assertTrue(notRegisteredTemplates.contains("newParser1"));
		assertTrue(notRegisteredTemplates.contains("newParser2"));
		assertTrue(notRegisteredTemplates.contains("newParser3"));
	}
}
