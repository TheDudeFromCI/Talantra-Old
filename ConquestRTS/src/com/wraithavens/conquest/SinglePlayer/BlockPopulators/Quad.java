package com.wraithavens.conquest.SinglePlayer.BlockPopulators;

import java.nio.FloatBuffer;

public class Quad{
	public final FloatBuffer data; // Vertices (0-11), Color(12-14)
	public final int side;
	public final short blockType;
	Quad(float[] points, float[] color, int side, short blockType){
		data = FloatBuffer.allocate(20);
		data.put(points);
		data.put(color);
		this.side = side;
		this.blockType = blockType;
	}
}