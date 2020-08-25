package htmlparser;


import java.util.List;

import htmlparser.data.Composition;
import htmlparser.data.Parameter;

public interface DataCollector {
	void addParameter(Parameter parameter);
	void addComposition(Composition composition);
	void appendCompositionList(List<Composition> list);
	void transferDataTo(DataCollector collector);
}
