package com.SeeYourUse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {
	static File file;
	static FileWriter fw;
	
	public Logger() {}
	
	public static void log(String text) throws IOException {
		fw = new FileWriter("logy.txt",true);
		fw.write(text);
		close();
	}
	public static void logErr(Exception e) {

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		
		try {
			fw = new FileWriter("logy.txt",true);
			fw.write(sw.toString());
			close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	public static void close() throws IOException {
		fw.close();
	}
}
