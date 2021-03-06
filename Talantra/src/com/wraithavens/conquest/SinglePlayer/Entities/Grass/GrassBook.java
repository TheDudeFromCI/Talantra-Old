package com.wraithavens.conquest.SinglePlayer.Entities.Grass;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;
import com.wraithavens.conquest.Launcher.WraithavensConquest;
import com.wraithavens.conquest.SinglePlayer.Entities.EntityType;

class GrassBook{
	private static int loadTexture(EntityType type){
		File file = new File(WraithavensConquest.modelFolder, type.fileName);
		// BinaryFile bin = new BinaryFile(file);
		// bin.decompress(null, true);
		// ByteBuffer data = BufferUtils.createByteBuffer(bin.size());
		// {
		//
		// }
		// for(int i = 0; i<bin.size(); i++)
		// data.put(bin.getByte());
		// data.flip();
		// int size = (int)Math.sqrt(bin.size()/4);
		ByteBuffer data = BufferUtils.createByteBuffer(64*64*4);
		try{
			BufferedImage buf = ImageIO.read(file);
			int[] pixels = new int[64*64];
			buf.getRGB(0, 0, 64, 64, pixels, 0, 64);
			int rgb;
			for(int i = 0; i<pixels.length; i++){
				rgb = pixels[i];
				data.put((byte)(rgb>>16&0xFF));
				data.put((byte)(rgb>>8&0xFF));
				data.put((byte)(rgb&0xFF));
				data.put((byte)(rgb>>24&0xFF));
			}
		}catch(Exception exception){
			exception.printStackTrace();
			throw new RuntimeException();
		}
		data.flip();
		int textureId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, 64, 64, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
			data);
		return textureId;
	}
	private final HashMap<EntityType,GrassTypeData> types = new HashMap();
	private final ArrayList<GrassPatch> patches;
	private final int OffsetAttribLocation;
	private final int RotScaleAttribLocation;
	private final int ColorAttribLocation;
	GrassBook(
		int OffsetAttribLocation, int RotScaleAttribLocation, int ColorAttribLocation,
		ArrayList<GrassPatch> patches){
		this.OffsetAttribLocation = OffsetAttribLocation;
		this.RotScaleAttribLocation = RotScaleAttribLocation;
		this.ColorAttribLocation = ColorAttribLocation;
		this.patches = patches;
	}
	private int bindType(EntityType type){
		GrassTypeData data = types.get(type);
		data.bind();
		GL20.glVertexAttribPointer(OffsetAttribLocation, 3, GL11.GL_FLOAT, false, 32, 0);
		GL20.glVertexAttribPointer(RotScaleAttribLocation, 2, GL11.GL_FLOAT, false, 32, 12);
		GL20.glVertexAttribPointer(ColorAttribLocation, 3, GL11.GL_FLOAT, false, 32, 20);
		return data.getCount();
	}
	private void rebuildDataBuffer(EntityType type){
		int count = 0;
		for(GrassPatch patch : patches){
			patch.calculateView();
			if(patch.getType()==type&&patch.inView())
				count += patch.getCount();
		}
		GrassTypeData grassType = types.get(type);
		FloatBuffer data = grassType.allocateData(count);
		for(GrassPatch patch : patches)
			if(patch.getType()==type&&patch.inView())
				patch.store(data);
		grassType.recompile();
	}
	void addReference(EntityType type){
		if(types.containsKey(type))
			types.get(type).addReference();
		else{
			GrassTypeData data = new GrassTypeData(loadTexture(type));
			data.addReference();
			types.put(type, data);
		}
		rebuildDataBuffer(type);
	}
	void dispose(){
		for(EntityType type : types.keySet())
			types.get(type).dispose();
		types.clear();
	}
	void removeReference(EntityType type){
		GrassTypeData data = types.get(type);
		if(data.removeReferences()){
			data.dispose();
			types.remove(type);
			return;
		}
		rebuildDataBuffer(type);
	}
	void render(){
		for(EntityType type : types.keySet())
			GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, 12, GL11.GL_UNSIGNED_SHORT, 0, bindType(type));
	}
	void updateVisibility(){
		for(EntityType type : types.keySet())
			rebuildDataBuffer(type);
	}
}
