import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;


/**
 * Emits heading 3 snippets as json objects, otherwise emits verbatim.
 */
public class Mwk2Json {

	public static void main(String[] args) {

		BufferedReader br = null;
		try {

			br = new BufferedReader(new InputStreamReader(System.in));

			// TODO: rename to currentSnippet (could be level 2 or 3)
			String level3snippet = "";
			String currentLevel1Heading = "";
			String currentLevel2Heading = null;
			String line = "";
			boolean insideLevel3Snippet = false;
			boolean insideLevel2Snippet = false;
			while ((line = br.readLine()) != null) {

				////
				//// Heading
				////
				if (isHeading(line)) {
					if (getHeadingLevel(line) == 3) {
						boolean previousLineWasInLevel2 = insideLevel2Snippet;
						insideLevel2Snippet = false;
						if (previousLineWasInLevel2) {
							// emit
							JSONObject snippet3Json = createEmitJson(level3snippet, currentLevel2Heading);
							System.err.println("5");
							System.out.println(snippet3Json);
							level3snippet = "";
							insideLevel3Snippet = false; // Pointless but just for completeness
						} else if (insideLevel3Snippet) {
							// emit
							JSONObject snippet3Json = createEmitJson(level3snippet, currentLevel2Heading);
							System.err.println("1");
							System.out.println(snippet3Json);
							level3snippet = "";
							insideLevel3Snippet = false; // Pointless but just for completeness
						}
						// start appending
						level3snippet += line + "\n";
						insideLevel3Snippet = true;
					} else if (getHeadingLevel(line) > 3) {
						insideLevel2Snippet = false;
						// continue appending
						level3snippet += line + "\n";
						if (!insideLevel3Snippet) {
							throw new RuntimeException("We must be inside a level 3 snippet: " + line );
						}
					} else if (getHeadingLevel(line) < 3) {

						// first emit under the old heading
						JSONObject snippet3Json = createEmitJson(level3snippet, currentLevel2Heading);
						System.err.println("2");
						System.out.println(snippet3Json);
						level3snippet = "";
						insideLevel3Snippet = false;
						// second, update the level 2 heading
						currentLevel2Heading = null;
						if (getHeadingLevel(line) == 2) {
							currentLevel2Heading = line;
							insideLevel2Snippet = true;
						} else {
							insideLevel2Snippet = false;
						}
					} else {
						throw new RuntimeException("Invalid case");
					}

				////
				//// Not Heading
				////
				} else {
					insideLevel3Snippet = isInsideLevel3Heading(line, insideLevel3Snippet);
					if (insideLevel3Snippet) {
						// continue appending
						level3snippet += line + "\n";
					}
					else if (insideLevel2Snippet) {
						// continue appending
						level3snippet += "(none)\n" + line + "\n";
					} else {
						// TODO: I think this case shouldn't occur anymore. We
						// never want to emit unless we hit a new heading, or
						// the end of file
						// emit
						System.err.println("3");
						System.out.println(line);
						// there shouldn't be anything accumulated
						if (level3snippet.length() > 0) {
							throw new RuntimeException(
									"nothing should be accumulated if we are outside a level 3 snippet: >>>>"
											+ level3snippet + "<<<<");
						}
					}
				}
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

	// TODO: swap param order
	private static JSONObject createEmitJson(String level3snippet, @Nullable String currentLevel2Heading) {
		// TODO : this eliminates blank lines from the source snippet. Not a huge problem but it would be nice to fix.
		String[] snippet3Lines = level3snippet.split("\\n");
		JSONObject snippet3Json = new JSONObject();
		String heading = snippet3Lines[0];
		snippet3Json.put("heading", heading);
		

		String allLinesExceptHeading = StringUtils.replace(level3snippet, heading + "\n", "", 1);

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
