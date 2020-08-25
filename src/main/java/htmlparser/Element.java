package htmlparser;

import org.jsoup.nodes.Node;
import htmlparser.sequencer.*;

public abstract class Element implements ParsingSpecialTags{
	
	protected HtmlParsingSequencer sequencer;
	
	public void setSequencer(HtmlParsingSequencer sequencer) {
		this.sequencer = sequencer;
	}
	
	public abstract void call(Node startNode);
	
	public abstract void resume(boolean parsed, DataCollector dataCollector, Node nextNode);
}
