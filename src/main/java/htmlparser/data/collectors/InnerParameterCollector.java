package htmlparser.data.collectors;

import htmlparser.DataCollector;
import htmlparser.data.Parameter;

public class InnerParameterCollector extends ParameterCollector {
	@Override
	public void transferDataTo(DataCollector collector) {
		for(Parameter p: parameterList) {
			collector.addParameter(p);
		}
	}
}
