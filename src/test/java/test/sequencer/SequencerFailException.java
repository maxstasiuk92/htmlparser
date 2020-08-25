package test.sequencer;

public class SequencerFailException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public SequencerFailException() {
		super();
	}
	
	public SequencerFailException(String message) {
		super(message);
	}
}
