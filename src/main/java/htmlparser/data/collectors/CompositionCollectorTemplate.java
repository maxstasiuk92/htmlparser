package htmlparser.data.collectors;

import htmlparser.DataCollector;
import htmlparser.DataCollectorTemplate;

public class CompositionCollectorTemplate implements DataCollectorTemplate {

	@Override
	public DataCollector getDataCollector() {
		return new CompositionCollector();
	}

}
