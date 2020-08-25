package htmlparser.data.collectors;

import java.util.ArrayList;
import java.util.List;

import htmlparser.DataCollector;
import htmlparser.data.Composition;
import htmlparser.data.Parameter;

/**collects compositions to list, which will be transfered*/
public class CompositionCollector implements DataCollector {
	protected ArrayList<Composition> compositions;
	
	public CompositionCollector() {
		compositions = new ArrayList<>();
	}
	
	@Override
	public void addParameter(Parameter parameter) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void addComposition(Composition composition) {
		compositions.add(composition);
	}

	@Override
	public void appendCompositionList(List<Composition> list) {
		compositions.addAll(list);
	}

	@Override
	public void transferDataTo(DataCollector collector) {
		collector.appendCompositionList(compositions);
	}
	
	public List<Composition> getCompositionList() {
		return new ArrayList<Composition>(compositions);
	}

}
