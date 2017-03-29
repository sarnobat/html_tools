import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.csv.CSVParser;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class CsvHeap2ListReversed {

	public static void main(String[] args) {
		BufferedReader br = null;
		Multimap<String, String> m = HashMultimap.create();
		Set<String> children = new HashSet<String>();
		try {
			br = new BufferedReader(new InputStreamReader(System.in));
			String line;
			while ((line = br.readLine()) != null) {
				CSVParser p = new CSVParser(new StringReader(line));
				String[] line2 = p.getLine();
				String parent = line2[0];
				String child = line2[1];
				m.put(parent, child);
				children.add(child);
			}

			// Find out the root nodes
			Set<String> roots = Sets.difference(m.keySet(), children);

			// Create trees
			Set<TreeNode> trees = new HashSet<TreeNode>();
			for (String root : roots) {
				TreeNode rootNode = buildTree(root, m);
				trees.add(rootNode);
			}

			// Find all leaf nodes
			List<TreeNode> leafNodes = new LinkedList<TreeNode>();
			for (TreeNode rootNode : trees) {
				leafNodes.addAll(getLeafNodes(rootNode));
			}

			// add the nodes bottom-up breadth first
			Set<TreeNode> visited = new HashSet<TreeNode>();
			Queue<TreeNode> bottomUpBreadthFirstQueue = new LinkedList<TreeNode>();
			bottomUpBreadthFirstQueue.addAll(leafNodes);
			List<String> output = new LinkedList<String>();

			int i = 0;
			while (bottomUpBreadthFirstQueue.size() > 0) {
				i++;
				if (i > 100) {
					// System.exit(-1);
				}
				System.err.println("size() = " + bottomUpBreadthFirstQueue.size());
				TreeNode n = bottomUpBreadthFirstQueue.remove();
				output.add(n.getData());
				visited.add(n);
				if (n.getParent() == null) {
				} else {
					if (visited.containsAll(n.getParent().getChild())) {
						// System.err.println("CsvHeap2ListReversed.main() - Parent added: "
						// + n);
						bottomUpBreadthFirstQueue.add(n.getParent());
					}
				}
			}

			for (String l2 : ImmutableList.copyOf(output).reverse()) {
				System.out.println(l2);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static Set<TreeNode> getLeafNodes(TreeNode rootNode) {
		Set<TreeNode> leafNodes1 = new HashSet<TreeNode>();
		getLeafNodes(rootNode, leafNodes1);
		return ImmutableSet.copyOf(leafNodes1);
	}

	// TODO: Bad - mutable input
	private static void getLeafNodes(TreeNode rootNode, Set<TreeNode> oLeafNodes) {
		if (rootNode.getChild().size() == 0) {
			oLeafNodes.add(rootNode);
		} else {
			for (TreeNode child : rootNode.getChild()) {
				getLeafNodes(child, oLeafNodes);
			}
		}
	}

	private static TreeNode buildTree(String root, Multimap<String, String> m) {
		Set<TreeNode> childNodes = new HashSet<TreeNode>();
		for (String childNodeData : m.get(root)) {
			TreeNode childNode = buildTree(childNodeData, m);
			childNodes.add(childNode);
		}
		TreeNode n = new TreeNode(root, childNodes);
		return n;
	}

	private static class TreeNode {
		private final String data;

		private final Set<TreeNode> child;
		// TODO: can we make this final?
		private TreeNode parent;

		TreeNode(String data, Set<TreeNode> child) {
			this.data = data;
			this.child = ImmutableSet.copyOf(child);
			for (TreeNode child1 : child) {
				child1.setParent(this);
			}
		}

		public void setParent(TreeNode parent1) {
			if (parent == null) {
				parent = parent1;
			} else {
				throw new RuntimeException(parent.getData());
			}
		}

		public TreeNode getParent() {
			return parent;
		}

		public String getData() {
			return data;
		}

		public Set<TreeNode> getChild() {
			return child;
		}
	}
}