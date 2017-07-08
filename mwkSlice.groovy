import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

/**
 * 
 * cat ~/mwk/new.mwk | groovy ~/github/html_tools/mwkSlice.groovy | tee ~/mwk/new.mwk.sliced
 */
public class MwkSlice {

	public static void main(String[] args) throws IOException, InterruptedException {

		BufferedReader br = null;

		br = new BufferedReader(new InputStreamReader(System.in));

		String level3snippet = "";
		String currentLevel2Heading = null;
		String line = "";
		boolean insideLevel3Snippet = false;
		String rootDir = Files.createTempDirectory("snippets").toString();//getWorkingDirectory().toString();
		Path remnantInputFile = Files.createTempFile("", ".txt");
		if (!remnantInputFile.toFile().exists()) {
			throw new RuntimeException("Couldn't create renmant file: " + remnantInputFile.toString());
		}
		String headingText = null;
		Path targetDirPath = null;
		int currentLevel = 0;
		while ((line = br.readLine()) != null) {
			if (isHeading(line)) {
				currentLevel = getHeadingLevel(line);
				
				if (currentLevel < 4) {
					// emit previous snippet
					// TODO: We can remove this condition later
					//System.err.println("MwkSlice.main() heading: " + line);
					if (currentLevel2Heading != null) {
						if ("2".equals(currentLevel2Heading)
								|| currentLevel2Heading.length() == 0) {
							if (!targetDirPath.toFile().exists()) {
								if (!targetDirPath.toFile().mkdirs()) {
									throw new RuntimeException(
											"Failed to make target dir");
								}
							} else {
								// print previously accumulated new snippet to file
								Path path = Paths.get(targetDirPath.toString() + "/" + "snpt_" + System.currentTimeMillis() + "_" + ((int)Math.random() * 100000) + ".mwk");
								File newFile = path.toFile();
								if (newFile.exists()) {
									throw new RuntimeException("Snippet already exists");
								}
								//newFile.createNewFile();
								FileUtils.writeStringToFile(newFile, level3snippet, "UTF8");
							}
						} else {
							System.out.print(level3snippet);
						}
					}
					level3snippet = "";
				}
				
				headingText = getHeadingText(line);

				if (getHeadingLevel(line) == 1) {
					System.out.println(line);
					
				}
				else if (getHeadingLevel(line) == 2) {
					String targetDir = rootDir + "/snippets/" + headingText;
					targetDirPath = Paths.get(targetDir);
					currentLevel2Heading = headingText;
					System.out.println(line);
				} else if (getHeadingLevel(line) == 3) {
					level3snippet += line + "\n" ;
				} else if (getHeadingLevel(line) > 3) {
					level3snippet += line + "\n" ;
				}

			} else {
				if (currentLevel < 3) {
					System.err.println("MwkSlice.main() >>>> " + line  + "<<<<<");
//					if (line.length() == 0) {
//						System.out.print("\n"+"(empty)");
//					} else {
						System.out.println(line);
//					}
					if (level3snippet.length() > 0) {
						throw new RuntimeException("Shouldn't happen");
					}
					
				} else {
					level3snippet += line + "\n";
				}
			}
		}
		// print out what remains
		System.out.println(level3snippet);
		Thread.sleep(1000);
		System.err.println("");
		System.err.println("Snippets created in:");
		System.err.println(rootDir);
		System.err.println("mv -v " + rootDir + "/* ~/mwk/snippets/");
	}

	private static String getHeadingText(String line) {
		Pattern p = Pattern.compile("^=+(.*?)=+");
		Matcher m = p.matcher(line);
		if (m.find()) {
			return m.group(1).trim();
		} else {
			throw new RuntimeException("Couldn't determine heading");
		}
	}

	private static Path getWorkingDirectory() {
		return Paths.get(".").toAbsolutePath();
	}

	private static boolean isInsideLevel3Heading(String line,
			boolean insideLevel3Snippet) {
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
			// System.err.println("isInsideLevel3Heading() - " +
			// insideLevel3Snippet);
			insideLevel3SnippetRet = insideLevel3Snippet;
		}
		return insideLevel3SnippetRet;
	}

	private static boolean isHeading(String line) {
		// return line.startsWith("=");// && line.matches("");
		return line.matches("[=]+[^=]+[=]+") && line.startsWith("=");
	}

	private static int getHeadingLevel(String line) {
		int level = 0;
		int i = 0;
		// System.err.println("getHeadingLevel() : " +line);
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
