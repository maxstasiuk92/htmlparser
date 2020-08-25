package htmlparser.elements;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.nodes.Node;

import htmlparser.DataCollector;
import htmlparser.Element;
import htmlparser.Template;

public class TemplateSearcher extends Element {
	public static enum Traverse {PLAIN, DEEP};
	public static enum StopOn {NO_NODES, FIRST_MATCH};
	
	protected DataCollector dataCollector;
	protected Map<String, Template> parserTemplates;
	protected Traverse traverse;
	protected StopOn stopOn;
	
	protected Iterator<Entry<String, Template>> templateIterator;
	protected Node checkNode;
	protected boolean continueFromReturnedNode;
	
	protected int matchNumber;
		
	public TemplateSearcher(DataCollector dataCollector,
			Map<String, Template> parserTemplates,
			Traverse traverse, StopOn stopOn) {
		if (parserTemplates == null || parserTemplates.size() < 1) {
			throw new IllegalArgumentException();
		}
		if (traverse == null || stopOn == null) {
			throw new IllegalArgumentException();
		}
		this.dataCollector = dataCollector;
		this.parserTemplates = parserTemplates;
		this.traverse = traverse;
		this.stopOn = stopOn;
		this.matchNumber = 0;
		this.continueFromReturnedNode = false;
	}
			
	@Override
	public void call(Node startNode) {
		startChecksOnNode(startNode);
		planNext();
	}
	
	@Override
	public void resume(boolean parsed, DataCollector dataCollector, Node nextNode) {
		boolean continueSearch = processReturnedData(parsed, dataCollector, nextNode);
		if (continueSearch) {
			planNext();
		}
	}
	
	protected boolean processReturnedData(boolean match, DataCollector dataCollector, Node nextNode) {
		boolean continueSearch = true;
		if (match) {
			matchNumber += 1;
			if(dataCollector != null && dataCollector != this.dataCollector) {
				dataCollector.transferDataTo(this.dataCollector);
			}
			
			if (continueFromReturnedNode) {
				startChecksOnNode(nextNode);
			}
			
			if(stopOn == StopOn.FIRST_MATCH) {
				sequencer.makeReturn(matchNumber > 0, this.dataCollector, this.checkNode);
				continueSearch = false;
			}
		}
		return continueSearch;
	}
	
	protected void planNext() {
		if (checkNode == null) {
			sequencer.makeReturn(matchNumber > 0, dataCollector, checkNode);
		} else if (!planTemplateCheck() && !planSearchOnChildNodes()) {
			startChecksOnNode(checkNode.nextSibling());
			planNext();
		}
	}
	
	protected boolean planTemplateCheck() {
		boolean planned = false;
		if (templateIterator.hasNext()) {
			Entry<String, Template> s = templateIterator.next();
			Element parser = parserTemplates.get(s.getKey()).getParserElement();
			sequencer.makeCall(parser, checkNode);
			continueFromReturnedNode = true;
			planned = true;
		}
		return planned;
	}
	
	protected void startChecksOnNode(Node checkNode) {
		this.checkNode = checkNode;
		templateIterator = parserTemplates.entrySet().iterator();
	}
	
	protected boolean planSearchOnChildNodes() {
		boolean planned = false;
		if (traverse == Traverse.DEEP && checkNode.childNodeSize() > 0) {
			TemplateSearcher searcher = new TemplateSearcher(dataCollector, parserTemplates, traverse, stopOn);
			sequencer.makeCall(searcher, checkNode.childNode(0));
			continueFromReturnedNode = false;
			startChecksOnNode(checkNode.nextSibling());
			planned = true;
		}
		return planned;
	}
}


