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

EXAMPLE:

cat ~/mwk/new_slice_these.mwk | groovy ~/github/html_tools/mwkSlice.groovy | tee ~/mwk/new_not_sliced.mwk 2> ~/mwk/new.mwk.sliced

NOTES
	(-) System.err captures what WAS sliced successfully. System.out captures what was NOT (and it needs to be tee'd out to new_not_sliced.mwk for losslessness)
	(-) This should be idempotent
	(-) This only works on Mac. Only linux the unmappable characters are a problem  


 */
public class MwkSlice {

	private static final boolean PRINT_SLICED = false;

	public static void main(String[] args) throws IOException, InterruptedException {

		BufferedReader br = null;

		br = new BufferedReader(new InputStreamReader(System.in));

		String level3snippet = "";
		String currentLevel2Heading = null;
		String line = "";
		String rootDir = Files.createTempDirectory("snippets").toString();//getWorkingDirectory().toString();
		Path remnantInputFile = Files.createTempFile("", ".txt");
		if (!remnantInputFile.toFile().exists()) {
			throw new RuntimeException("Couldn't create renmant file: " + remnantInputFile.toString());
		}
		String headingText = null;
		Path targetDirPath = null;
		int currentLevel = 0;
		int lineNumber = 0;
		while ((line = br.readLine()) != null) {
			lineNumber++;
			if (isHeading(line)) {
				currentLevel = getHeadingLevel(line);
				
				if (currentLevel < 4) {
					// emit previous snippet
					
					// We could remove this condition, but I'm not convinced it's
					// an improvement.
					if (currentLevel2Heading != null) {
// 						if ("2".equals(currentLevel2Heading)
// 								|| "Self, Personal Development".equalsIgnoreCase(currentLevel2Heading)
// 								|| currentLevel2Heading.length() == 0) {
							if (!targetDirPath.toFile().exists()) {
								if (!targetDirPath.toFile().mkdirs()) {
									throw new RuntimeException(
											"Failed to make target dir");
								}
							} else {
								// print previously accumulated new snippet to file
								// This only works on Mac. Only linux the unmappable characters are a problem
								String summary = getSummary(level3snippet);//.replaceAll("[“'é\\s]","_");
								Path path = Paths.get(targetDirPath.toString() + "/" + "snpt_" + System.currentTimeMillis() + "_" + ((int)Math.random() * 100000) +  "__" + summary +".mwk");
								File newFile = path.toFile();
// 								if (newFile.exists()) {
// 									throw new RuntimeException("Snippet already exists: " + newFile.toString());
// 								}
								String deduplicationSuffix = "";
								while (newFile.exists()) {
									deduplicationSuffix += "1";
									path = Paths.get(targetDirPath.toString() + "/" + "snpt_" + System.currentTimeMillis() + "_" + ((int)Math.random() * 100000) +  "__" + summary +"_"+deduplicationSuffix+".mwk");
									newFile = path.toFile();
								}
								FileUtils.writeStringToFile(newFile, level3snippet, "UTF8");
								System.err.print(level3snippet);
							}
// 						} else {
// 							// we have a level 3 heading which is handled by the below code
// 								System.out.print(level3snippet);
// 						}
					}
					level3snippet = "";
				}
				
				headingText = getHeadingText(line);

				if (getHeadingLevel(line) == 1) {
					System.out.println(line);
					
				}
				else if (getHeadingLevel(line) == 2) {
					// The snippet already exists under a level 2 category heading. Put the snippet in the corresponding subdir. Don't lose whatever sorting we did under the existing system.
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
					// Text found that is NOT inside a level 3 heading (which ideally we don't want). Emit it to stdout (and it should be tee'd to an unsliced file)
					System.out.println(line);

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
		System.err.println("Backup: ");
		System.err.println("mv ~/mwk/new_not_sliced.mwk\t~/mwk/bak/new_not_sliced.mwk." + System.currentTimeMillis());
		System.err.println("mv ~/mwk/new.mwk.sliced\t\t~/mwk/bak/new.mwk.sliced." + System.currentTimeMillis());		
		System.err.println("");
		System.err.println("Snippets created in new temporary dir. Clear your existing unsliced file.");
		//System.err.println(rootDir);
		System.err.println("mv -n -v " + rootDir + "/snippets/* ~/mwk/snippets/");
		
	}

	private static String getSummary(String level3snippet) {
		if (!level3snippet.startsWith("=")) {
			System.err.println("MwkSlice.getSummary() Develoepr error - snippet doesn't start with a heading: " + level3snippet);
			return "";
		}
		String[] lines = level3snippet.split("\n");
		if (lines[0].matches("^=+\\s+=+")) {
			if (lines.length > 1) {
				int i = 1;
				String nextNonBlankLine = lines[i];
				while(nextNonBlankLine.trim().length() < 1) {
					++i;
					if (i == lines.length) {
						nextNonBlankLine = "";
						break;
					} else {
						nextNonBlankLine = lines[i];
					}
				}
				return cleanse(nextNonBlankLine.substring(0, Math.min(23, nextNonBlankLine.length())).trim());
			} else {
				return "";
			}
		} else {
			try {
				String headingText = getHeadingText(lines[0]);
				if (headingText.contains('/')) {
					//throw new RuntimeException("Heading text needs to be cleansed: " + headingText);
				}
				return cleanse(headingText);
			} catch (Exception e) {
				System.err.println("MwkSlice.getSummary() " + level3snippet);
				throw e;
			}
		}
	}

	private static String cleanse(String trim) {
		return trim.replace("~", "_")
				.replace("!", "_")
				.replace("\"", "_")
				.replace("'", "_")
				.replace(",", "_")
				.replace("/", "_")
				.replace("’", "_")
				.replace(".", "_")
				.replace(" ", "_")
				.replace("?", "_")
				.replace(">", "_")
				.replace("<", "_")
				.replace("=", "_")
				.replaceAll("[\\[:']","_")
				.replaceAll("\\d\\d\\d\\d-\\d\\d-\\d\\d", "");
	}

	private static String getHeadingText(String line) {
		Pattern p = Pattern.compile("^=+(.*?)=+");
		Matcher m = p.matcher(line);
		if (m.find()) {
			return m.group(1).trim();
		} else {
			throw new RuntimeException("Couldn't determine heading: >>>" + line + "<<<");
		}
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
