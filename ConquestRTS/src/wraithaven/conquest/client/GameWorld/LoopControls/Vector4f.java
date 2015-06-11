package wraithaven.conquest.client.GameWorld.LoopControls;

import java.nio.FloatBuffer;

public class Vector4f extends Vector{
	public static Vector4f add(Vector4f left, Vector4f right, Vector4f dest){
		if(dest==null) return new Vector4f(left.x+right.x, left.y+right.y, left.z+right.z, left.w+right.w);
		dest.set(left.x+right.x, left.y+right.y, left.z+right.z, left.w+right.w);
		return dest;
	}
	public static float angle(Vector4f a, Vector4f b){
		float dls = Vector4f.dot(a, b)/(a.length()*b.length());
		if(dls<-1f) dls = -1f;
		else if(dls>1.0f) dls = 1.0f;
		return (float)Math.acos(dls);
	}
	public static float dot(Vector4f left, Vector4f right){
		return left.x*right.x+left.y*right.y+left.z*right.z+left.w*right.w;
	}
	public static Vector4f sub(Vector4f left, Vector4f right, Vector4f dest){
		if(dest==null) return new Vector4f(left.x-right.x, left.y-right.y, left.z-right.z, left.w-right.w);
		dest.set(left.x-right.x, left.y-right.y, left.z-right.z, left.w-right.w);
		return dest;
	}
	public float x,
			y,
			z,
			w;
	public Vector4f(){
		super();
	}
	public Vector4f(float x, float y, float z, float w){
		set(x, y, z, w);
	}
	@Override public boolean equals(Object obj){
		if(this==obj) return true;
		if(obj==null) return false;
		if(getClass()!=obj.getClass()) return false;
		Vector4f other = (Vector4f)obj;
		if(x==other.x&&y==other.y&&z==other.z&&w==other.w) return true;
		return false;
	}
	public float getW(){
		return w;
	}
	public final float getX(){
		return x;
	}
	public final float getY(){
		return y;
	}
	public float getZ(){
		return z;
	}
	@Override public float lengthSquared(){
		return x*x+y*y+z*z+w*w;
	}
	@Override public Vector load(FloatBuffer buf){
		x = buf.get();
		y = buf.get();
		z = buf.get();
		w = buf.get();
		return this;
	}
	@Override public Vector negate(){
		x = -x;
		y = -y;
		z = -z;
		w = -w;
		return this;
	}
	public Vector4f negate(Vector4f dest){
		if(dest==null) dest = new Vector4f();
		dest.x = -x;
		dest.y = -y;
		dest.z = -z;
		dest.w = -w;
		return dest;
	}
	public Vector4f normalise(Vector4f dest){
		float l = length();
		if(dest==null) dest = new Vector4f(x/l, y/l, z/l, w/l);
		else dest.set(x/l, y/l, z/l, w/l);
		return dest;
	}
	@Override public Vector scale(float scale){
		x *= scale;
		y *= scale;
		z *= scale;
		w *= scale;
		return this;
	}
	public void set(float x, float y){
		this.x = x;
		this.y = y;
	}
	public void set(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public void set(float x, float y, float z, float w){
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	public void setW(float w){
		this.w = w;
	}
	public final void setX(float x){
		this.x = x;
	}
	public final void setY(float y){
		this.y = y;
	}
	public void setZ(float z){
		this.z = z;
	}
	@Override public Vector store(FloatBuffer buf){
		buf.put(x);
		buf.put(y);
		buf.put(z);
		buf.put(w);
		return this;
	}
	@Override public String toString(){
		return "Vector4f: "+x+" "+y+" "+z+" "+w;
	}
	public Vector4f translate(float x, float y, float z, float w){
		this.x += x;
		this.y += y;
		this.z += z;
		this.w += w;
		return this;
	}
}