package com.SeeYourUse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {
	File file;
	FileWriter fw;
	
	public Logger() {}
	
	public void log(String text) throws IOException {
		fw = new FileWriter("logy.txt",true);
		fw.write(text);
		close();
	}
	public void close() throws IOException {
		fw.close();
	}
}
