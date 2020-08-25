package htmlparser.elements;

import java.util.Iterator;
import java.util.Map;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import htmlparser.DataCollector;
import htmlparser.Element;
import htmlparser.Template;
import htmlparser.data.Parameter;

public class MarkupComparator extends Element {
	public static enum ReferenceScope{
				/**
				 * Each node for check should have corresponding reference node.
				 */
				SUBSET,
				/**
				 * Each reference node should have corresponding node for check and there should be
				 * no other nodes for check. 
				 */
				ALL};
	public static enum ReferenceHierarchy{
				/**
				 * If reference node does not have child nodes, node for check is not checked for child nodes.
				 */
				BASIC,
				/**
				 * If reference node does not have child nodes, node for check also should not have child nodes. 
				 */
				EXACT};
	
	protected DataCollector dataCollector;
	protected Map<String, Template> parserTemplates;
	protected ReferenceScope refScope;
	protected ReferenceHierarchy refHierarchy;
	
	protected Node checkNode, referenceNode;
	protected boolean resumeFromReturnedNode;
	
	protected enum Processing {STOP, PROCEED};
	
	public MarkupComparator(DataCollector dataCollector,
			Map<String, Template> parserTemplates,
			Node startRefNode, ReferenceScope refScope, ReferenceHierarchy refHierarchy) {
		if (refScope == null || refHierarchy == null) {
			throw new IllegalArgumentException();
		}
		this.dataCollector = dataCollector;
		this.parserTemplates = parserTemplates;
		this.referenceNode = startRefNode;
		this.refScope = refScope;
		this.refHierarchy = refHierarchy;
	}
	
	@Override
	public void call(Node startNode) {
		checkNode = startNode;
		resumeFromReturnedNode = false;
		process();
	}

	@Override
	public void resume(boolean match, DataCollector dataCollector, Node nextNode) {
		final boolean MATCH = true;
		final boolean NOT_MATCH = !MATCH;
		if (match) {
			if (resumeFromReturnedNode) {
				checkNode = nextNode;
				resumeFromReturnedNode = false;
			}
			if (dataCollector != null && dataCollector != this.dataCollector) {
				dataCollector.transferDataTo(this.dataCollector);
			}
			process();
		} else {
			sequencer.makeReturn(NOT_MATCH, this.dataCollector, this.checkNode);
		}
	}
	
	protected void process() {
		final boolean MATCH = true;
		final boolean NOT_MATCH = !MATCH;
		Processing processing = Processing.PROCEED;
		
		do {
			if (referenceNode == null) {
				processing = Processing.STOP;
				if (refScope == ReferenceScope.SUBSET) {
					sequencer.makeReturn(MATCH, dataCollector, checkNode);
				} else {
					if(!findNonTextCheckNode()) {
						sequencer.makeReturn(MATCH, dataCollector, checkNode);
					} else {
						sequencer.makeReturn(NOT_MATCH, dataCollector, checkNode);
					}
				}
			} else {
				switch (referenceNode.nodeName()) {
				case dataTag:
					processing = processTagWithData();
					break;
					
				case parserTag:
					processing = processTagWithParser();
					break;
					
				case valuableTextTag:
					processing = processTagWithText();
					break;
					
				default:
					processing = matchTags();
				}
			}
		} while (processing == Processing.PROCEED);
	}
	
	protected Processing processTagWithData() {
		final boolean MATCH = true;
		final boolean NOT_MATCH = !MATCH;
		Processing result = Processing.PROCEED;
		String parameterName, parameterValue;
		parameterName = referenceNode.attr(parameterNameAttribute);
		if (checkNode == null || !checkNode.nodeName().equals("#text")) {
			sequencer.makeReturn(NOT_MATCH, dataCollector, checkNode);
			result = Processing.STOP;
		} else {
			parameterValue = ((TextNode)checkNode).getWholeText();
			dataCollector.addParameter(new Parameter(parameterName, parameterValue));
			
			referenceNode = referenceNode.nextSibling();
			checkNode = checkNode.nextSibling();
		}
		return result;
	}
		
	protected Processing processTagWithParser() {
		String parserName = referenceNode.attr(parserNameAttribute);
		Element parser = parserTemplates.get(parserName).getParserElement();
		sequencer.makeCall(parser, checkNode);
		
		referenceNode = referenceNode.nextSibling();
		resumeFromReturnedNode = true;
		
		return Processing.STOP;
	}
	
	protected Processing matchTags() {
		Processing result = Processing.PROCEED;
		final boolean MATCH = true;
		final boolean NOT_MATCH = !MATCH;
		if (findNonTextCheckNode() && nodeMatchWithReference()) {
			boolean refHasChildNodes = referenceNode.childNodeSize() > 0;
			boolean checkHasChildNodes = checkNode.childNodeSize() > 0;
			if (refHasChildNodes || checkHasChildNodes && refHierarchy == ReferenceHierarchy.EXACT) {
				Node refChildNode = refHasChildNodes ? referenceNode.childNode(0) : null;
				Node checkChildNode = checkHasChildNodes ? checkNode.childNode(0) : null;
				MarkupComparator parser = new MarkupComparator(dataCollector, parserTemplates,
						refChildNode, ReferenceScope.ALL, refHierarchy);
				sequencer.makeCall(parser, checkChildNode);
				result = Processing.STOP;
			}
			referenceNode = referenceNode.nextSibling();
			checkNode = checkNode.nextSibling();
		} else {
			sequencer.makeReturn(NOT_MATCH, dataCollector, checkNode);
			result = Processing.STOP;
		} 
		return result;
	}
	
	protected Processing processTagWithText() {
		final boolean MATCH = true;
		final boolean NOT_MATCH = !MATCH;
		Processing result = Processing.PROCEED;
		String referenceText, checkText;
		if(checkNode.nodeName().equals("#text")) {
			referenceText = ((TextNode)referenceNode.childNode(0)).getWholeText();
			checkText = ((TextNode)checkNode).getWholeText(); 
			if (textMatchWithReference(checkText, referenceText)) {
				referenceNode = referenceNode.nextSibling();
				checkNode = checkNode.nextSibling();
			} else {
				sequencer.makeReturn(NOT_MATCH, dataCollector, checkNode);
				result = Processing.STOP;
			}
		} else {
			sequencer.makeReturn(NOT_MATCH, dataCollector, checkNode);
			result = Processing.STOP;
		}
		return result;
	}
	
	protected boolean findNonTextCheckNode() {
		final boolean FOUND = true;
		final boolean NOT_FOUND = !FOUND;
		boolean result = NOT_FOUND;
		while (checkNode != null) {
			if (!checkNode.nodeName().equals("#text")) {
				result = FOUND;
				break;
			}
			checkNode = checkNode.nextSibling();
		}
		return result;
	}
	
	protected boolean nodeMatchWithReference() {
		final boolean MATCH = true;
		final boolean NOT_MATCH = !MATCH;
		boolean result = NOT_MATCH;
		
		if (referenceNode.nodeName().equals(checkNode.nodeName())) {
			result = attributesMatchWithReference(checkNode.attributes(), referenceNode.attributes());
		}
		return result;
	}
	
	/**
	 * @return true if texts are equal
	 */
	protected boolean textMatchWithReference(String checkText, String referenceText) {
		return checkText.equals(referenceText);
	}
	
	/**
	 * @return true if checkAttributes is superset of referenceAttributes
	 */
	protected boolean attributesMatchWithReference(Attributes checkAttributes, Attributes referenceAttributes) {
		final boolean MATCH = true;
		final boolean NOT_MATCH = !MATCH;
		Attribute refAttr;
		String refAttrName, refAttrValue;
		Iterator<Attribute> refAttrIterator = referenceNode.attributes().iterator();
		while (refAttrIterator.hasNext()) {
			refAttr = refAttrIterator.next();
			refAttrName = refAttr.getKey();
			refAttrValue = refAttr.getValue();
			if (!checkNode.hasAttr(refAttrName) || !checkNode.attr(refAttrName).equals(refAttrValue)) {
				return NOT_MATCH;
			}
		}
		return MATCH;
	}
}
