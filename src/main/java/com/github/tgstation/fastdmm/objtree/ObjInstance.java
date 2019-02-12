package com.github.tgstation.fastdmm.objtree;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ObjInstance {
	public abstract String getVar(String key);
	public abstract String typeString();
	public abstract boolean istype(String path);
	public abstract String toStringTGM();
	
	public int cachedDir = -1;
	public int cachedPixelX = -1234;
	public int cachedPixelY = -1234;
	public float cachedLayer = -1234;
	public int cachedPlane = -1234;
	public Color cachedColor = null;
	public String cachedIconState = null;
	public String cachedIcon = null;

	public String getIcon() {
		if(cachedIcon == null) {
			String var = getVar("icon");
			if(var == null)
				return cachedIcon = "";
			Matcher m = Pattern.compile("'(.+)'").matcher(var);
			if(m.find())
				cachedIcon = m.group(1);
			else
				cachedIcon = "";
		}
		return cachedIcon;
	}

	public String getIconState() {
		if(cachedIconState == null) {
			if (getVar("icon_state") != null) {
				Matcher m = Pattern.compile("\"(.+)\"").matcher(getVar("icon_state"));
				if(m.find())
					cachedIconState = m.group(1);
				else
					cachedIconState = "";
			} else {
				cachedIconState = "";
			}
		}
		return cachedIconState;
	}

	public int getDir() {
		if(cachedDir == -1) {
			if (getVar("dir") != null) {
				try {
					cachedDir = Integer.parseInt(getVar("dir"));
				} catch (NumberFormatException e) {
					cachedDir = 2;
				}
			} else {
				cachedDir = 2;
			}
		}
		return cachedDir;
	}
	
	public int getPixelX() {
		if(cachedPixelX == -1234) {
			if (getVar("pixel_x") != null) {
				try {
					cachedPixelX = Integer.parseInt(getVar("pixel_x"));
				} catch (NumberFormatException e) {
					cachedPixelX = 2;
				}
			} else {
				cachedPixelX = 2;
			}
		}
		return cachedPixelX;
	}
	
	public int getPixelY() {
		if(cachedPixelY == -1234) {
			if (getVar("pixel_y") != null) {
				try {
					cachedPixelY = Integer.parseInt(getVar("pixel_y"));
				} catch (NumberFormatException e) {
					cachedPixelY = 2;
				}
			} else {
				cachedPixelY = 2;
			}
		}
		return cachedPixelY;
	}
	
	public float getLayer() {
		if(cachedLayer == -1234) {
			if (getVar("layer") != null) {
				try {
					cachedLayer = Float.parseFloat(getVar("layer"));
				} catch (NumberFormatException e) {
					cachedLayer = 2;
				}
			} else {
				cachedLayer = 2;
			}
		}
		return cachedLayer;
	}

	public Color getColor() {
		if(cachedColor == null) {
			String var = getVar("color");
			
			if (var == null)
				return cachedColor = new Color(255,255,255);
			
			Matcher m = Pattern.compile("(#[\\d\\w][\\d\\w][\\d\\w][\\d\\w][\\d\\w][\\d\\w])").matcher(var);
			if(m.find())
				return cachedColor = Color.decode(m.group(1));
			m = Pattern.compile("rgb ?\\( ?([\\d]+) ?, ?([\\d]+) ?, ?([\\d]+) ?\\)").matcher(var);
			if(m.find()) {
				int r = Integer.parseInt(m.group(1));
				int g = Integer.parseInt(m.group(2));
				int b = Integer.parseInt(m.group(3));
				if(r > 255)
					r = 255;
				if(g > 255)
					g = 255;
				if(b > 255)
					b = 255;
				return cachedColor = new Color(r, g, b);
			}
			m = Pattern.compile("\"(black|silver|grey|gray|white|maroon|red|purple|fuchsia|magenta|green|lime|olive|gold|yellow|navy|blue|teal|aqua|cyan)\"").matcher(var);
			if(m.find()) {
				switch(m.group(1)) {
				case "black":
					return cachedColor = Color.decode("#000000");
				case "silver":
					return cachedColor = Color.decode("#C0C0C0");
				case "gray":
					return cachedColor = Color.decode("#808080");
				case "grey":
					return cachedColor = Color.decode("#808080");
				case "white":
					return cachedColor = Color.decode("#FFFFFF");
				case "maroon":
					return cachedColor = Color.decode("#800000");
				case "red":
					return cachedColor = Color.decode("#FF0000");
				case "purple":
					return cachedColor = Color.decode("#800080");
				case "fuchsia":
					return cachedColor = Color.decode("#FF00FF");
				case "magenta":
					return cachedColor = Color.decode("#FF00FF");
				case "green":
					return cachedColor = Color.decode("#00C000");
				case "lime":
					return cachedColor = Color.decode("#00FF00");
				case "olive":
					return cachedColor = Color.decode("#808000");
				case "gold":
					return cachedColor = Color.decode("#808000");
				case "yellow":
					return cachedColor = Color.decode("#FFFF00");
				case "navy":
					return cachedColor = Color.decode("#000080");
				case "blue":
					return cachedColor = Color.decode("#0000FF");
				case "teal":
					return cachedColor = Color.decode("#008080");
				case "aqua":
					return cachedColor = Color.decode("#00FFFF");
				case "cyan":
					return cachedColor = Color.decode("#00FFFF");
				}
			}
			if(var != null && !var.equals("null"))
				System.err.println("Unrecognized color " + var);
			return cachedColor = new Color(255,255,255);
		}
		return cachedColor;
	}
	
	public int getPlane() {
		if(cachedPlane == -1234 && getVar("plane") != null) {
			try {
				cachedPlane = Integer.parseInt(getVar("plane"));
			} catch (NumberFormatException e) {
				cachedPlane = 0;
			}
		}
		return cachedPlane;
	}
}
