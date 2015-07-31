package com.wraithavens.conquest.Math;

import java.nio.FloatBuffer;

public class Vector3f extends Vector{
	public float x, y, z;
	public Vector3f(){
		super();
	}
	public Vector3f(float x, float y, float z){
		set(x, y, z);
	}
	public void cross(Vector3f v){
		float cx = y*v.z-z*v.y;
		float cy = z*v.x-x*v.z;
		float cz = x*v.y-y*v.x;
		x = cx;
		y = cy;
		z = cz;
	}
	@Override
	public boolean equals(Object obj){
		if(this==obj)
			return true;
		if(obj==null)
			return false;
		if(getClass()!=obj.getClass())
			return false;
		Vector3f other = (Vector3f)obj;
		if(x==other.x&&y==other.y&&z==other.z)
			return true;
		return false;
	}
	@Override
	public float lengthSquared(){
		return x*x+y*y+z*z;
	}
	@Override
	public Vector load(FloatBuffer buf){
		x = buf.get();
		y = buf.get();
		z = buf.get();
		return this;
	}
	@Override
	public Vector negate(){
		x = -x;
		y = -y;
		z = -z;
		return this;
	}
	public void normalise(){
		double length = Math.sqrt(lengthSquared());
		x /= length;
		y /= length;
		z /= length;
	}
	@Override
	public Vector scale(float scale){
		x *= scale;
		y *= scale;
		z *= scale;
		return this;
	}
	public void set(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	@Override
	public Vector store(FloatBuffer buf){
		buf.put(x);
		buf.put(y);
		buf.put(z);
		return this;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder(64);
		sb.append("Vector3f[");
		sb.append(x);
		sb.append(", ");
		sb.append(y);
		sb.append(", ");
		sb.append(z);
		sb.append(']');
		return sb.toString();
	}
}