package htmlparser.data.collectors;

import htmlparser.DataCollector;
import htmlparser.DataCollectorTemplate;

public class InnerParameterCollectorTemplate implements DataCollectorTemplate {

	@Override
	public DataCollector getDataCollector() {
		return new InnerParameterCollector();
	}

}
