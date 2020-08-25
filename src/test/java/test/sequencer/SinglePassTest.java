package test.sequencer;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import htmlparser.sequencer.HtmlParsingSequencer;

public class SinglePassTest {
	
	@ParameterizedTest(name = "Sequance length: {0}")
	@ValueSource(ints = {1, 2, 10, 100})
	public void test(int length) {
		HtmlParsingSequencer sequencer = new HtmlParsingSequencer();
		SinglePassSequenceCreator sequenceCreator = new SinglePassSequenceCreator(length);
		
		SinglePassMockElement headElement = sequenceCreator.head;
			
		boolean parsedValue = sequencer.parse(sequenceCreator, null);
		
		assertTrue(parsedValue == headElement.parsedValue);
		assertTrue(headElement.getDataCollector() == sequencer.getDataCollector());
		assertTrue(headElement.returnNode == sequencer.getNextNode());
	}
}
