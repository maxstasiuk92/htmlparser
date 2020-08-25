package htmlparser.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class Composition implements Iterable<Parameter>{
	private ArrayList<Parameter> composition;
	
	public Composition() {
		composition = new ArrayList<Parameter>();
	}
	
	public Composition(List<Parameter> parameterList) {
		composition = new ArrayList<Parameter>(parameterList);
	}
	
	public void addParameter(Parameter parameter) {
		composition.add(parameter);
	}
	
	public Parameter getParameter(int index) {
		return composition.get(index);
	}
	
	public int size() {
		return composition.size();
	}

	@Override
	public Iterator<Parameter> iterator() {
		return composition.iterator();
	}

}
