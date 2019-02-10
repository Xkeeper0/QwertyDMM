package com.github.tgstation.fastdmm.constant;

public class constants {
	public static final int NORTH = 1;
	public static final int SOUTH = 2;
	public static final int EAST = 4;
	public static final int WEST = 8;
	public static final int NORTHEAST = 5;
	public static final int NORTHWEST = 9;
	public static final int SOUTHEAST = 6;
	public static final int SOUTHWEST = 10;
	public static final int UP = 16;
	public static final int DOWN = 23;
	
	public static int[] DIRECTIONS = {
		NORTH,
		SOUTH,
		EAST,
		WEST,
		NORTHEAST,
		NORTHWEST,
		SOUTHEAST,
		SOUTHWEST,
	};
	
	public static String[] DIRECTION_NAMES = {
		"North",
		"South",
		"East",
		"West",
		"Northeast",
		"Northwest",
		"Southeast",
		"Southwest"
	};
	
}
