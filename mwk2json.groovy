import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.mortbay.util.StringUtil;
import com.google.common.collect.*;
import com.google.common.base.Joiner;

/**
 * Emits heading 3 snippets as json objects, otherwise emits verbatim.
 */
public class Mwk2Json {

private static String printQueue(java.util.Collection<String> queue) {
	StringBuffer sb = new StringBuffer();
	for (String s : queue) {
		sb.append(s);
		sb.append("\n");
}
return sb.toString();
}

	public static void main(String[] args) {
if (true) {
//System.err.println("=== ABC ===".matches("[=]+[^=]+[=]+"));
//return;
}
		BufferedReader br = null;
		try {

			br = new BufferedReader(new InputStreamReader(System.in));

Collection<String> previousLines = EvictingQueue.<String>create(5);

			String level3snippet = "";
			String currentLevel2Heading = null;
int currentLevel = 0;
			String line = "";
			boolean insideLevel3Snippet = false;
			while ((line = br.readLine()) != null) {
				previousLines.add(line);
//System.err.println("current line: " + line);
				if (isHeading(line)) {
currentLevel = getHeadingLevel(line);
					if (getHeadingLevel(line) == 3) {
						if (insideLevel3Snippet) {
							// emit
							JSONObject snippet3Json = createEmitJson(level3snippet, currentLevel2Heading);
							System.out.println(snippet3Json);
							level3snippet = "";
							insideLevel3Snippet = false; // Pointless but just for completeness
//System.err.println(" insideLevel3Snippet = " + insideLevel3Snippet + " :: 1");
						} 
						// start appending
						level3snippet += line + "\n";
						insideLevel3Snippet = true;
//System.err.println(" insideLevel3Snippet = " + insideLevel3Snippet + " :: 2");
					} else if (getHeadingLevel(line) > 3) {
						// continue appending
						level3snippet += line + "\n";
						if (!insideLevel3Snippet) {
							System.err.println("previous lines: " + printQueue(previousLines));
							throw new RuntimeException("We must be inside a level 3 snippet: " + line );
						}
					} else if (getHeadingLevel(line) < 3) {

						// first emit under the old heading
						JSONObject snippet3Json = createEmitJson(level3snippet, currentLevel2Heading);
						System.out.println(snippet3Json);
						level3snippet = "";
						insideLevel3Snippet = false;
//						System.err.println(" insideLevel3Snippet = " + insideLevel3Snippet + " :: 3");
						// second, update the level 2 heading
						currentLevel2Heading = null;
						if (getHeadingLevel(line) == 2) {
							currentLevel2Heading = line;
						}
					} else {
						throw new RuntimeException("Invalid case");
					}
				} else {
					insideLevel3Snippet = isInsideLevel3Heading(line, insideLevel3Snippet);
//System.err.println(" insideLevel3Snippet = " + insideLevel3Snippet + " :: 4");
					if (insideLevel3Snippet) {
						// continue appending
						level3snippet += line + "\n";
						if (level3snippet.contains("purchase funnel")) {
							//System.err.println("Mwk2Json.main() level3snippet += >>>" + line + "\n<<");
						}
					} else {
						// emit
						System.out.println(line);
						// there shouldn't be anything accumulated
						if (level3snippet.length() > 0) {
							throw new RuntimeException(
									"nothing should be accumulated if we are outside a level 3 snippet: >>>>"
											+ level3snippet + "<<<<");
						}
					}
				}
System.err.println(currentLevel + " :: " + line);
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

	private static JSONObject createEmitJson(String level3snippet, @Nullable String currentLevel2Heading) {
		// TODO : this eliminates blank lines from the source snippet. Not a huge problem but it would be nice to fix.
		String[] snippet3Lines = level3snippet.split("\\n");
		JSONObject snippet3Json = new JSONObject();
		String heading = snippet3Lines[0];
		snippet3Json.put("heading", heading);
		
		String bodyOut = Joiner.on('\n').join(
				Arrays.copyOfRange(snippet3Lines, 1, snippet3Lines.length));

		String allLinesExceptHeading = StringUtils.replace(level3snippet, heading + "\n", "", 1);
//		String trailingBlankLines =  StringUtils.replace(trailingBlankLines1, bodyOut

		snippet3Json.put("body", allLinesExceptHeading);
		if (currentLevel2Heading != null) {
			snippet3Json.put("parent", currentLevel2Heading);
		}
		return snippet3Json;
	}

	private static boolean isInsideLevel3Heading(String line, boolean insideLevel3Snippet) {
		boolean insideLevel3SnippetRet;
		if (isHeading(line)) {
			if (getHeadingLevel(line) == 3) {
				insideLevel3SnippetRet = true;
			} else if (getHeadingLevel(line) > 3) {
				insideLevel3SnippetRet = true;
			} else if (getHeadingLevel(line) < 3) {
				insideLevel3SnippetRet = false;
			} else {
				throw new RuntimeException("Invalid case");
			}
		} else {
//System.err.println("isInsideLevel3Heading() - " + insideLevel3Snippet);
			insideLevel3SnippetRet = insideLevel3Snippet;
		}
		return insideLevel3SnippetRet;
	}

	private static boolean isHeading(String line) {
//		return line.startsWith("=");// && line.matches("");
		return line.matches("[=]+[^=]+[=]+") && line.startsWith("=");
	}

	private static int getHeadingLevel(String line) {
		int level = 0;
		int i = 0;
//System.err.println("getHeadingLevel() : " +line);
		while (line.charAt(i) == '=') {
			++i;
			++level;
		}

		if (level == 0) {
			throw new RuntimeException("Not a heading: " + line);
		} else {
			return i;
		}
	}
}
