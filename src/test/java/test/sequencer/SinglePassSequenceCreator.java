package test.sequencer;

import htmlparser.Element;
import htmlparser.Template;

public class SinglePassSequenceCreator implements Template {
	int targetSequenceLength;
	int createdSequenceLength;
	SinglePassMockElement head;
	boolean returnedElement;
	
	SinglePassSequenceCreator(int sequenceLength) {
		targetSequenceLength = sequenceLength;
		createdSequenceLength = 0;
		returnedElement = false;
		prepareSequence();
	}
	
	void prepareSequence() {
		final boolean headParsedValue = true;
		final SinglePassMockElement headPreviousValue = null;
		SinglePassMockElement previous, current;
				
		head = new SinglePassMockElement(headPreviousValue, headParsedValue);
		
		previous = head;
		for (int length = 1; length < targetSequenceLength; length++) {
			current = new SinglePassMockElement(previous, !previous.parsedValue);
			previous.next = current;
			previous = current;
		}
	}
	
	public Element getParserElement() {
		if (createdSequenceLength >= targetSequenceLength) {
			throw new SequencerFailException("createdSequenceLength >= targetSequenceLength");
		} else if (returnedElement) {
			throw new SequencerFailException("already returnedElement");
		} else {
			returnedElement = true;
		}
		return head;
	}
	
	

}
