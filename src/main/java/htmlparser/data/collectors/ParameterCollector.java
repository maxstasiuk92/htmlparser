package htmlparser.data.collectors;

import java.util.List;

import htmlparser.DataCollector;
import htmlparser.data.Composition;
import htmlparser.data.Parameter;

import java.util.ArrayList;

/**Collects parameters and pack them in composition, which will be transfered*/
public class ParameterCollector implements DataCollector {
	protected ArrayList<Parameter> parameterList;
	
	public ParameterCollector() {
		parameterList = new ArrayList<Parameter>();
	}

	@Override
	public void addParameter(Parameter parameter) {
		parameterList.add(parameter);	
	}

	@Override
	public void addComposition(Composition map) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void appendCompositionList(List<Composition> list) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void transferDataTo(DataCollector collector) {
		collector.addComposition(new Composition(parameterList));
	}
	
	public List<Parameter> getParameterList() {
		return new ArrayList<Parameter>(parameterList);
	}
}
