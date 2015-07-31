package com.wraithavens.conquest.SinglePlayer.Heightmap;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import com.wraithavens.conquest.Launcher.WraithavensConquest;
import com.wraithavens.conquest.Math.Vector3f;
import com.wraithavens.conquest.SinglePlayer.Noise.WorldNoiseMachine;

class HeightmapGenerator{
	private static final Vector3f tempVec = new Vector3f();
	private static final int ExtraDetail = 4;
	private final int size;
	private final float scale;
	private final WorldNoiseMachine noise;
	HeightmapGenerator(int size, float scale, WorldNoiseMachine noise){
		this.size = size*ExtraDetail;
		this.noise = noise;
		this.scale = scale;
	}
	private int calculateColor(float x, float y){
		noise.getPrairieColor(x, y, tempVec);
		return ((int)((float)noise.getNormalisedWorldHeight(x, y)*255)<<24)+((int)(tempVec.x*255)<<16)
			+((int)(tempVec.y*255)<<8)+(int)(tempVec.z*255);
	}
	private BufferedImage generate(int offX, int offY){
		System.out.println("Generating height map.");
		System.out.println("  Origin: "+offX+", "+offY);
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		long time = System.currentTimeMillis();
		int[] rgb = new int[size*size];
		int x, y;
		for(x = 0; x<size; x++)
			for(y = 0; y<size; y++)
				rgb[y*size+x] = calculateColor(x*scale/ExtraDetail+offX, y*scale/ExtraDetail+offY);
		img.setRGB(0, 0, size, size, rgb, 0, size);
		System.out.println("Finished in "+(System.currentTimeMillis()-time)+" ms.");
		File heightmapFile =
			new File(WraithavensConquest.saveFolder+File.separatorChar+"Heightmaps", offX+","+offY+".png");
		try{
			// ---
			// First ensure the directory exists, then save the newly generated
			// heightmap for future reference.
			// ---
			heightmapFile.getParentFile().mkdirs();
			ImageIO.write(img, "PNG", heightmapFile);
		}catch(Exception exception){
			exception.printStackTrace();
		}
		return img;
	}
	HeightmapTexture getHeightmapTexture(int x, int y){
		// ---
		// When loading an already generated texture, this method is usally very
		// quick, and it has an almost unnoticable drop in FPS. Through,
		// generating father out does cause some major FPS drop.
		// ---
		File file = new File(WraithavensConquest.saveFolder+File.separatorChar+"Heightmaps", x+","+y+".png");
		if(file.exists()){
			System.out.println("Loading Heightmap: "+file.getName());
			return new HeightmapTexture(file);
		}
		return new HeightmapTexture(generate(x, y));
	}
}