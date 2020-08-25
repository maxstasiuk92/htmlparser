package test.utils;

import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class TestUtils {
	
	public static void printHierarchy(Node startNode) {
		System.out.println(hierarchyToString(startNode));
	}
	
	public static String hierarchyToString(Node startNode) {
		StringBuilder result = new StringBuilder();
		appendLevel(startNode, 0, result);
		return result.toString();
	}
	
	protected static void appendLevel(Node startNode, int level, StringBuilder result) {
		Node currentNode = startNode;
		String indent = levelIndent(level);
		String lineSeparator = System.getProperty("line.separator");
		while (currentNode != null) {
			result.append(indent).append(currentNode.nodeName()).append(lineSeparator);
			if (currentNode.childNodeSize() > 0) {
				appendLevel(currentNode.childNode(0), level+1, result);
			}
			currentNode = currentNode.nextSibling();
		}
	}
	
	protected static String levelIndent(int level) {
		String indentSymbol = "  ";
		StringBuilder indent = new StringBuilder(level*indentSymbol.length());
		for (int i = 0; i < level; i++) {
			indent.append(indentSymbol);
		}
		return indent.toString();
	}
	
	public static boolean equalTree(Node node, Node ref) {
		boolean validNode, validRef;
		while((validNode = node != null) & (validRef = ref != null)) {
			if (!ref.nodeName().equals(node.nodeName())) {
				return false;
			}
			if ("#text".equals(ref.nodeName())) {
				String nodeText = ((TextNode)node).getWholeText();
				String refText = ((TextNode)ref).getWholeText();
				if (!refText.equals(nodeText)) {
					return false;
				}
			}
			boolean nodeHasChild = node.childNodeSize() > 0;
			boolean refHasChild = ref.childNodeSize() > 0;
			if (nodeHasChild ^ refHasChild) {
				return false;
			}
			if (refHasChild && !equalTree(node.childNode(0), ref.childNode(0))) {
				return false;
			}
			node = node.nextSibling();
			ref = ref.nextSibling();
		}
		return !(validNode ^ validRef);
	}
	
}
