package test.sequencer;

import org.jsoup.nodes.Node;

import htmlparser.*;
import htmlparser.data.collectors.ParameterCollector;

import org.jsoup.nodes.TextNode;

public class SinglePassMockElement extends Element {
	protected DataCollector dataCollector;
	SinglePassMockElement next;
	SinglePassMockElement previous;
	
	Dirrection dirrection;
	Node callNode, returnNode;
	boolean parsedValue;
		
	enum Dirrection {Forward, Backward, Finished};
	
	SinglePassMockElement(SinglePassMockElement previous, boolean parsedValue) {
		this.previous = previous;
		this.next = null;
		this.parsedValue = parsedValue;
		this.callNode = new TextNode("");
		this.returnNode = new TextNode("");
		this.dataCollector = new ParameterCollector();
		this.dirrection = Dirrection.Forward;
	}
	
	public DataCollector getDataCollector () {
		return dataCollector;
	}
	
	@Override
	public void call(Node startNode) {
		if (dirrection == Dirrection.Forward) {
			if (previous != null) {
				if (previous.callNode != startNode) {
					throw new SequencerFailException("previous.callNode != startNode");
				}
			}
			if (next != null) {
				sequencer.makeCall(this.next, this.callNode);
				dirrection = Dirrection.Backward;
			} else {
				sequencer.makeReturn(this.parsedValue, this.dataCollector, this.returnNode);
				dirrection = Dirrection.Finished;
			}
		} else {
			throw new SequencerFailException("Wrong dirrection");
		}
	}

	@Override
	public void resume(boolean parsed, DataCollector dataCollector, Node nextNode) {
		if (dirrection == Dirrection.Backward) {
			if (parsed != next.parsedValue) {
				throw new SequencerFailException("parsed != parsedValue");
			}
			if (dataCollector != next.dataCollector) {
				throw new SequencerFailException("dataCollector != next.dataCollector");
			}
			if (nextNode != next.returnNode) {
				throw new SequencerFailException("nextNode != next.nodeValue");
			}
			sequencer.makeReturn(this.parsedValue, this.dataCollector, this.returnNode);
			dirrection = Dirrection.Finished;
		} else {
			throw new SequencerFailException("Wrong dirrection");
		}
	}

}
