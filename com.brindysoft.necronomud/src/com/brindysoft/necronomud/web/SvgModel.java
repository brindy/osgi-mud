package com.brindysoft.necronomud.web;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SvgModel {

	private List<Element> list = new LinkedList<Element>();
	private int width;
	private int height;

	public int size() {
		return list.size();
	}

	public void add(Element e) {
		if ("line".equals(e.name)) {
			list.add(0, e);
		} else {
			list.add(e);
		}
	}

	public Element get(int i) {
		return list.get(i);
	}

	public static class TextElement extends Element {

		private String text;

		public TextElement(String text) {
			super("text");
			this.text = text;
		}

		@Override
		public void render(PrintWriter out) {
			out.println("<text ");
			for (Map.Entry<String, String> entry : attributes.entrySet()) {
				out.println("\t" + entry.getKey() + "=\"" + entry.getValue() + "\"");
			}
			out.println(">" + text + "</text>");
		}

	}

	public static class SimpleElement extends Element {

		public SimpleElement(String name) {
			super(name);
		}

		@Override
		public void render(PrintWriter out) {
			out.println("<" + name);
			for (Map.Entry<String, String> entry : attributes.entrySet()) {
				out.println("\t" + entry.getKey() + "=\"" + entry.getValue() + "\"");
			}
			out.println("/>");
		}

	}

	public static abstract class Element {

		protected final Map<String, String> attributes = new HashMap<String, String>();

		public final String name;

		public Element(String name) {
			this.name = name;
		}

		public String getAttribute(String name) {
			return attributes.get(name);
		}

		public void setAttribute(String name, String value) {
			attributes.put(name, value);
		}

		public void setAttribute(String name, int value) {
			attributes.put(name, String.valueOf(value));
		}

		public abstract void render(PrintWriter out);

	}

	public void writeTo(OutputStream stream) {
		PrintWriter out = new PrintWriter(stream);
		out.println("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"" + width + "\" height=\"" + height + "\">");

		for (Element e : list) {
			e.render(out);
		}

		out.println("</svg>");
		out.flush();
		out.close();
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
