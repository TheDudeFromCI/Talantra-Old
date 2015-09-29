package com.wraithavens.conquest.SinglePlayer.Blocks.Landscape;

import java.util.Arrays;

class VertexStorage{
	private Vertex[] vertices = new Vertex[100];
	private int size;
	public Vertex get(int index){
		return vertices[index];
	}
	public int indexOf(float x, float y, float z, byte shade, float tx, float ty, byte texture){
		for(int i = 0; i<size; i++)
			if(vertices[i].getX()==x&&vertices[i].getY()==y&&vertices[i].getZ()==z
				&&vertices[i].getShade()==shade&&vertices[i].getTx()==tx&&vertices[i].getTy()==ty
				&&vertices[i].getTexture()==texture)
				return i;
		if(size==vertices.length)
			vertices = Arrays.copyOf(vertices, vertices.length+100);
		Vertex v = new Vertex(x, y, z, shade, tx, ty, texture);
		vertices[size] = v;
		size++;
		return size-1;
	}
	public int size(){
		return size;
	}
	void clear(){
		size = 0;
	}
}
