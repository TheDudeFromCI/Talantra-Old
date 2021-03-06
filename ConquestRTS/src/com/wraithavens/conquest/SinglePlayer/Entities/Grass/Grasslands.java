package com.wraithavens.conquest.SinglePlayer.Entities.Grass;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL33;
import com.wraithavens.conquest.SinglePlayer.RenderHelpers.ShaderProgram;

public class Grasslands{
	private final ArrayList<GrassPatch> patches = new ArrayList();
	private final int vbo;
	private final int ibo;
	private final ShaderProgram shader;
	private final int SwayAttribLocation;
	private final int OffsetAttribLocation;
	private final int RotScaleAttribLocation;
	private final int ColorAttribLocation;
	private final int UvAttribLocation;
	private final GrassBook grassBook;
	private final HashMap<GrassPatch,Boolean> que = new HashMap();
	private double time;
	public Grasslands(){
		vbo = GL15.glGenBuffers();
		ibo = GL15.glGenBuffers();
		{
			// ---
			// Build the index buffer.
			// ---
			ShortBuffer indexData = BufferUtils.createShortBuffer(12);
			indexData.put((byte)0).put((byte)1).put((byte)2);
			indexData.put((byte)0).put((byte)2).put((byte)3);
			indexData.put((byte)4).put((byte)5).put((byte)6);
			indexData.put((byte)4).put((byte)6).put((byte)7);
			indexData.flip();
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexData, GL15.GL_STATIC_DRAW);
			// ---
			// Build the vertex buffer.
			// ---
			FloatBuffer vertexData = BufferUtils.createFloatBuffer(48);
			float r = 0.5f;
			float h = (float)Math.sqrt(2);
			vertexData.put(-r).put(0.0f).put(-r).put(0.0f).put(1.0f).put(0.0f);
			vertexData.put(r).put(0.0f).put(r).put(1.0f).put(1.0f).put(0.0f);
			vertexData.put(r).put(h).put(r).put(1.0f).put(0.0f).put(1.0f);
			vertexData.put(-r).put(h).put(-r).put(0.0f).put(0.0f).put(1.0f);
			vertexData.put(r).put(0.0f).put(-r).put(0.0f).put(1.0f).put(0.0f);
			vertexData.put(-r).put(0.0f).put(r).put(1.0f).put(1.0f).put(0.0f);
			vertexData.put(-r).put(h).put(r).put(1.0f).put(0.0f).put(1.0f);
			vertexData.put(r).put(h).put(-r).put(0.0f).put(0.0f).put(1.0f);
			vertexData.flip();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData, GL15.GL_STATIC_DRAW);
		}
		// ---
		// Load the shader.
		// ---
		shader = new ShaderProgram("Grass");
		shader.bind();
		shader.loadUniforms("texture1", "time");
		shader.setUniform1I(0, 0);
		SwayAttribLocation = shader.getAttributeLocation("att_swayTolerance");
		OffsetAttribLocation = shader.getAttributeLocation("att_offset");
		RotScaleAttribLocation = shader.getAttributeLocation("att_rotScale");
		ColorAttribLocation = shader.getAttributeLocation("att_color");
		UvAttribLocation = shader.getAttributeLocation("att_uv");
		GL20.glEnableVertexAttribArray(SwayAttribLocation);
		GL20.glEnableVertexAttribArray(OffsetAttribLocation);
		GL20.glEnableVertexAttribArray(RotScaleAttribLocation);
		GL20.glEnableVertexAttribArray(ColorAttribLocation);
		GL20.glEnableVertexAttribArray(UvAttribLocation);
		grassBook = new GrassBook(OffsetAttribLocation, RotScaleAttribLocation, ColorAttribLocation, patches);
	}
	public void addPatch(GrassPatch patch){
		que.put(patch, true);
	}
	public void dispose(){
		grassBook.dispose();
		shader.dispose();
	}
	public void removePatch(GrassPatch patch){
		que.put(patch, false);
	}
	public void render(){
		if(patches.isEmpty())
			return;
		shader.bind();
		GL33.glVertexAttribDivisor(OffsetAttribLocation, 1);
		GL33.glVertexAttribDivisor(RotScaleAttribLocation, 1);
		GL33.glVertexAttribDivisor(ColorAttribLocation, 1);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 24, 0);
		GL20.glVertexAttribPointer(UvAttribLocation, 2, GL11.GL_FLOAT, false, 24, 12);
		GL20.glVertexAttribPointer(SwayAttribLocation, 1, GL11.GL_FLOAT, false, 24, 20);
		GL11.glDisable(GL11.GL_CULL_FACE);
		shader.setUniform1f(1, (float)time);
		grassBook.render();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL33.glVertexAttribDivisor(OffsetAttribLocation, 0);
		GL33.glVertexAttribDivisor(RotScaleAttribLocation, 0);
		GL33.glVertexAttribDivisor(ColorAttribLocation, 0);
	}
	public void update(double time){
		this.time = time;
		if(que.isEmpty()){
			checkCameraPos();
			return;
		}
		for(GrassPatch p : que.keySet())
			if(que.get(p)){
				patches.add(p);
				grassBook.addReference(p.getType());
			}else{
				patches.remove(p);
				grassBook.removeReference(p.getType());
			}
		que.clear();
		checkCameraPos();
	}
	public void updateVisibility(){
		grassBook.updateVisibility();
	}
	private void checkCameraPos(){
		grassBook.updateVisibility();
	}
}
