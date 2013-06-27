package edu.cnu.cs.gooey;

public class GooeyFlag {
	public static enum Menu {
		BY_TEXT, BY_NAME,  // name- or text-based search. text takes precedence when both or none are present.
		FLAT, NESTED // nested- or flat-based search. nested takes precedence when both or none are present.
	}
	
}
