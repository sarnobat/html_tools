import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.csv.CSVParser;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class CsvHeap2List {

	public static void main(String[] args) {
		BufferedReader br = null;
		Multimap<String, String> m = HashMultimap.create();
		Set<String> children = new HashSet<String>();
		try {
			br = new BufferedReader(new InputStreamReader(System.in));
			String line;
			while ((line = br.readLine()) != null) {
				// log message
				// System.err.println("[DEBUG] current line is: " + line);
				CSVParser p = new CSVParser(new StringReader(line));
				String[] line2 = p.getLine();
				// System.err.println("[DEBUG] p.getLine(): " + line2);
				String parent = line2[0];
				String child = line2[1];
				m.put(parent, child);
				children.add(child);
				// program output
//				System.out.println(line);
			}

			// Find out the root nodes
			Set<String> roots = Sets.difference(m.keySet(), children);

			// Create trees
			Set<TreeNode> trees = new HashSet<TreeNode>();
			for (String root : roots) {
				TreeNode rootNode = buildTree(root, m);
				trees.add(rootNode);
			}

			// Initialize the queue
			Queue<TreeNode> q = new LinkedList<TreeNode>();
			for (TreeNode rootNode : trees) {
				q.add(rootNode);
			}

			List<String> l = new LinkedList<String>();
			while (!q.isEmpty()) {
				TreeNode n = q.remove();
				l.add(n.getData());
				Set<TreeNode> childNodes = n.getChild();
				q.addAll(childNodes);
				//System.err.println("queue: " + q.size());
			}

			for (String l2 : l) {
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

		TreeNode(String data, Set<TreeNode> child) {
			this.data = data;
			this.child = ImmutableSet.copyOf(child);
		}

		public String getData() {
			return data;
		}

		public Set<TreeNode> getChild() {
			return child;
		}
	}
}