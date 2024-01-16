package minty.io;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class AtlasData {

	public static String imageName;
	public static int width;
	public static int height;
	public static String format = "RGBA8888";
	public static String filter = "Nearest,Nearest";
	public static String repeat = "none";
	
	private String name;
	private String rotate = "false";
	private int x;
	private int y;
	private int w;
	private int h;
	private int ox;
	private int oy;
	private int offx = 0;
	private int offy = 0;
	private int index = -1;
	
	public AtlasData(String name, int x, int y, int w, int h) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.ox = w;
		this.oy = h;
	}
	private AtlasData() {
		
	}
	
	public void render(Graphics g, int scale, Color c) {
		g.setColor(c);
		g.drawRect(x*scale, y*scale, w*scale, h*scale);
	}
	
	@Override 
	public String toString(){
		return name;
	}
	
	public static String generateHeader() {
		StringBuilder sb = new StringBuilder();
		sb.append(imageName).append("\n");
		sb.append("size: ").append(width).append(",").append(height).append("\n");
		sb.append("format: ").append(format).append("\n");
		sb.append("filter: ").append(filter).append("\n");
		sb.append("repeat: ").append(repeat).append("\n");
		return sb.toString();
	}
	public String generateEntry() {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append("\n");
		sb.append("  ").append("rotate: ").append(rotate).append("\n");
		sb.append("  ").append("xy: ").append(x).append(", ").append(y).append("\n");
		sb.append("  ").append("size: ").append(w).append(", ").append(h).append("\n");
		sb.append("  ").append("orig: ").append(ox).append(", ").append(oy).append("\n");
		sb.append("  ").append("offset: ").append(offx).append(", ").append(offy).append("\n");
		sb.append("  ").append("index: ").append(index).append("\n");
		return sb.toString();
	}
	
	public static void parseHeader(Scanner s) {
		imageName = s.next();
		s.next(); // size
		width = s.nextInt();
		height = s.nextInt();
		s.next(); // format
		format = s.next();
		s.next(); // filter
		filter = s.next() + "," + s.next();
		s.next(); // repeat
		repeat = s.next();
	}
	
	public static ArrayList<AtlasData> parseEntries(Scanner s) {
		ArrayList<AtlasData> data = new ArrayList<AtlasData>();
		while(s.hasNext()) {
			AtlasData ad = new AtlasData();
			ad.name = s.next();
			
			s.next(); // rotate
			ad.rotate = s.next();
			
			s.next(); // xy
			ad.x = s.nextInt();
			s.next(); // empty
			ad.y = s.nextInt();
			
			s.next(); // size
			ad.w = s.nextInt();
			s.next(); // empty
			ad.h = s.nextInt();
			
			s.next(); // orig
			ad.ox = s.nextInt();
			s.next(); // empty
			ad.oy = s.nextInt();
			
			s.next(); // offset
			ad.offx = s.nextInt();
			s.next(); // empty
			ad.offy = s.nextInt();
			
			s.next(); // index
			ad.index = s.nextInt();
			
			data.add(ad);
		}
		return data;
	}
}
