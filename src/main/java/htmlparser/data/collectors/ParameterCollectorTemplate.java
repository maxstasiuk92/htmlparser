package htmlparser.data.collectors;

import htmlparser.DataCollector;
import htmlparser.DataCollectorTemplate;

public class ParameterCollectorTemplate implements DataCollectorTemplate {

	@Override
	public DataCollector getDataCollector() {
		return new ParameterCollector();
	}

}
