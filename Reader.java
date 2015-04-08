package LavaBucket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Reader {

	static ArrayList<String> strings;

	public static ArrayList<String> inputModel(String s) {
		// two lines, first is the verts, second is faces
		BufferedReader inputStream = null;

		ArrayList<String> strings = new ArrayList<String>();

		try {
			// InputStream is = TextInit.class
			// .getResourceAsStream("res/FolderSave1.txt");
			InputStream is = new FileInputStream(s);

			inputStream = new BufferedReader(new InputStreamReader(is));

			String l;
			while ((l = inputStream.readLine()) != null) {
				strings.add(l);
				// System.out.println("add:::");
			}
		} catch (Exception ex) {
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
		return strings;
	}

	static void saveNew(String src, String st) {
		try {
			BufferedWriter writer = null;

			OutputStream os = new FileOutputStream(src);
			writer = new BufferedWriter(new OutputStreamWriter(os));

			writer.write(st);
			// writer.newLine();
			writer.flush();

			os.close();
			// System.out.println("saved");
		} catch (Exception ex) {
		}
	}

	/*-
	 * What do I want this to do. Reding. Take in both lines to a string. Return
	 * the strings. Parse later.
	 * Should I only collect the first two lines?
	 */

	public static String getstrings() {
		return strings.get(0);
	}

	public static int[][] decypherMap(String s) {
		String a = Reader.inputModel(s).get(0);
		int inc = 0;
		for (int i = 0; i < a.length(); i++) {
			if (a.charAt(i) == '!') {
				inc++;
			}
		}
		String[] values = new String[inc];
		inc = 0;
		int last = 0;
		for (int i = 0; i < a.length(); i++) {
			if (a.charAt(i) == '!') {
				values[inc] = a.substring(last, i);
				last = i + 1;
				inc++;
			}
		}
		if (last != a.length()) {
			values[inc] = a.substring(last, a.length());
		}
		int[][] grid = new int[values.length][];
		// Count the number of spacesthat have characters bwtween them.
		for (int i = 0; i < values.length; i++) {
			last = 0;
			inc = 0;
			for (int j = 0; j < values[i].length(); j++) {
				if (values[i].charAt(j) == ' ') {
					if (j != last) {
						inc++;
					}
					last = j + 1;
				}
			}
			if (last != values[i].length()) {
				inc++;
			}
			grid[i] = new int[inc];
			last = 0;
			inc = 0;
			for (int j = 0; j < values[i].length(); j++) {
				if (values[i].charAt(j) == ' ') {
					if (j != last) {
						grid[i][inc] = Integer.parseInt(values[i].substring(
								last, j));
						inc++;
					}
					last = j + 1;
				}
			}
			if (last != values[i].length()) {
				grid[i][inc] = Integer.parseInt(values[i].substring(last,
						values[i].length()));
			}
		}
		return grid;
	}

}
