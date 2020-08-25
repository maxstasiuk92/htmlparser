package htmlparser.sequencer;

import java.util.LinkedList;
import org.jsoup.nodes.Node;

import htmlparser.DataCollector;
import htmlparser.Element;
import htmlparser.Template;

public class HtmlParsingSequencer {
	private LinkedList<Element> elementStack;
	private ExecutionDirrection executionDirrection;
	CallParameters callParameters;
	ReturnValues returnValues;
	
	protected enum ExecutionDirrection {NotDefined, CallNext, ResumePrevious}
	
	protected class CallParameters {
		public Element element;
		public Node startNode;
	}
	
	protected class ReturnValues {
		public boolean parsed;
		public DataCollector dataCollector;
		public Node nextNode;
	}
	
	public boolean parse(Template parserTemplate, Node startNode) {
		executionDirrection = ExecutionDirrection.NotDefined;
		elementStack = new LinkedList<Element>();
		callParameters = new CallParameters();
		returnValues = new ReturnValues();
		
		makeCall(parserTemplate.getParserElement(), startNode);
		sequencer();
		
		return returnValues.parsed;
	}
	
	public boolean isParsed() {
		return returnValues.parsed;
	}
	
	public DataCollector getDataCollector() {
		return returnValues.dataCollector;
	}
	
	public Node getNextNode() {
		return returnValues.nextNode;
	}
	
	protected void sequencer() {
		Element parser;
		boolean sequenceFinished = false;
		
		do {
			switch (executionDirrection) {
			case CallNext:
				executionDirrection = ExecutionDirrection.NotDefined;
				parser = callParameters.element;
				elementStack.addFirst(parser);
				parser.setSequencer(this);
				parser.call(callParameters.startNode);
				break;
				
			case ResumePrevious:
				executionDirrection = ExecutionDirrection.NotDefined;
				elementStack.removeFirst();
				if (!elementStack.isEmpty()) {
					parser = elementStack.getFirst();
					parser.resume(returnValues.parsed, returnValues.dataCollector, returnValues.nextNode);
				} else {
					sequenceFinished = true;
				}
				break;
				
			default:
				throw new IllegalStateException("Not " + ExecutionDirrection.CallNext.name() 
				+ " neither " + ExecutionDirrection.ResumePrevious.name() + " were not requested");
			}
		} while (!sequenceFinished);
	}
	
	public void makeCall(Element element, Node startNode) {
		if (executionDirrection != ExecutionDirrection.NotDefined) {
			throw new IllegalStateException(executionDirrection.name() + " was already requested");
		}
		executionDirrection = ExecutionDirrection.CallNext;
		callParameters.element = element;
		callParameters.startNode = startNode;
	}
	
	public void makeReturn(boolean parsed, DataCollector dataCollector, Node nextNode) {
		if (executionDirrection != ExecutionDirrection.NotDefined) {
			throw new IllegalStateException(executionDirrection.name() + " was already requested");
		}
		executionDirrection = ExecutionDirrection.ResumePrevious;
		returnValues.parsed = parsed;
		returnValues.dataCollector = dataCollector;
		returnValues.nextNode = nextNode;
	}
}
