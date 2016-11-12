import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.api.client.repackaged.com.google.common.base.Strings;

public class BulletsToHtml {

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		int previousLineLevel = 0;
		String lineOrig = null;
		while ((lineOrig = reader.readLine()) != null) {
			// do something with every line, one at a time
			int currentLineLevel = getBulletIndentationLevel(lineOrig);
			String line = lineOrig.substring(currentLineLevel);
			System.err.println("["+previousLineLevel + " --> " + currentLineLevel + "]");
			String padding;
			if (currentLineLevel > 0) {
				padding = Strings.repeat("  ", currentLineLevel - 1);
			} else {
				padding = "";
			}
			if (previousLineLevel < currentLineLevel && previousLineLevel == 0) {
				System.out.println(padding + "<ul>");
				System.out.println(padding + "<li>" + line + "</li>");
			} else if (previousLineLevel > currentLineLevel && currentLineLevel > 0) {
				System.out.println(padding + "  " + "</ul>");
				System.out.println(padding + "<li>" + line + "</li>");
			} else if (previousLineLevel == currentLineLevel && currentLineLevel > 0) {
				System.out.println(padding + "<li>" + line + "</li>");
			} else if (previousLineLevel < currentLineLevel && previousLineLevel > 0) {
				System.out.println(padding + "<ul>");
				System.out.println(padding + "<li>" + line + "</li>");
			} else if (previousLineLevel > currentLineLevel) {
				System.out.println(padding + "</ul>");
				System.out.println(line);
			} 
			else {
				System.out.println(line);
			}
			previousLineLevel = currentLineLevel;
		}
	}

	private static int getBulletIndentationLevel(String line) {
		int count = 0;
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == '*') {
				count++;
			} else {
				break;
			}
		}
		return count;
	}
}