package com.wraithavens.conquest.SinglePlayer.Blocks.Landscape;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import com.wraithavens.conquest.Math.Vector3f;
import com.wraithavens.conquest.SinglePlayer.BlockPopulators.Block;
import com.wraithavens.conquest.SinglePlayer.Blocks.BlockMesher.BlockData;
import com.wraithavens.conquest.SinglePlayer.Blocks.BlockMesher.MeshFormatter;
import com.wraithavens.conquest.SinglePlayer.Blocks.BlockMesher.MeshRenderer;
import com.wraithavens.conquest.SinglePlayer.Entities.EntityType;
import com.wraithavens.conquest.SinglePlayer.Entities.DynmapEntities.GiantEntityDictionary;
import com.wraithavens.conquest.SinglePlayer.Noise.Biome;
import com.wraithavens.conquest.SinglePlayer.Noise.PointGenerator2D;
import com.wraithavens.conquest.SinglePlayer.Noise.WorldNoiseMachine;
import com.wraithavens.conquest.SinglePlayer.RenderHelpers.Camera;
import com.wraithavens.conquest.Utility.Algorithms;
import com.wraithavens.conquest.Utility.BinaryFile;

public class SecondaryLoop implements Runnable{
	private static Biome randomBiomeObject(float h, float t, float l){
		Biome c0 = Biome.getFittingBiome(h, t, l);
		if(c0.isWaterType())
			return c0;
		final float mapSize = WorldNoiseMachine.BiomeTransitionSize;
		h *= mapSize;
		t *= mapSize;
		Biome c1 = Biome.getFittingBiome((int)h/mapSize, (int)t/mapSize, 1.0f);
		Biome c2 = Biome.getFittingBiome((int)(h+1)/mapSize, (int)t/mapSize, 1.0f);
		Biome c3 = Biome.getFittingBiome((int)h/mapSize, (int)(t+1)/mapSize, 1.0f);
		Biome c4 = Biome.getFittingBiome((int)(h+1)/mapSize, (int)(t+1)/mapSize, 1.0f);
		if(c1==c2&&c2==c3&&c3==c4)
			return c1;
		float x = h-(int)h;
		float y = t-(int)t;
		float p1 = 1/(float)(Math.pow(x, 2)+Math.pow(y, 2));
		float p2 = 1/(float)(Math.pow(x-1, 2)+Math.pow(y, 2));
		float p3 = 1/(float)(Math.pow(x, 2)+Math.pow(y-1, 2));
		float p4 = 1/(float)(Math.pow(x-1, 2)+Math.pow(y-1, 2));
		float c = p1+p2+p3+p4;
		p1 /= c;
		p2 = p2/c+p1;
		p3 = p3/c+p2;
		// Normally this should also be done to p4, but since it's never used
		// again, I'll skip it. :P (Though it would always == 1.)
		float r = (float)Math.random();
		if(r<=p1)
			return c1;
		if(r<=p2)
			return c2;
		if(r<=p3)
			return c3;
		return c4;
	}
	private static EntityType randomPlant(float h, float t, float l, int x, int y, int z, long seed){
		if(y<0)
			return null;
		{
			long tr = seed;
			tr = tr*s+x;
			tr = tr*s+z;
			tr += x*x+s;
			tr += z*z+s;
			random.setSeed(tr);
			random.nextFloat(); // This helps shuffle up the random number, a
			// lot.
		}
		if(random.nextFloat()<0.2){
			Biome biome = randomBiomeObject(h, t, l);
			switch(biome){
				case TayleaMeadow:
					if(random.nextFloat()<0.02)
						return EntityType.getVariation(EntityType.TayleaFlower, (int)(random.nextFloat()*6));
					if(random.nextFloat()<0.0025)
						return EntityType.getVariation(EntityType.VallaFlower, (int)(random.nextFloat()*4));
					if(random.nextFloat()<0.005)
						return EntityType.values()[EntityType.TayleaMeadowRock1.ordinal()
						                           +(int)(random.nextFloat()*3)];
					break;
				case ArcstoneHills:
					break;
				case AesiaFields:
					if(random.nextFloat()<0.03)
						return EntityType.getVariation(EntityType.AesiaStems, (int)(random.nextFloat()*24));
					if(random.nextFloat()<0.1)
						return EntityType.getVariation(EntityType.AesiaPedals, (int)(random.nextFloat()*7));
					break;
				case Ocean:
					return null;
				default:
					throw new RuntimeException();
			}
			return EntityType.values()[EntityType.Grass.ordinal()+(int)(random.nextFloat()*4)];
		}
		return null;
	}
	private static final long s = 4294967291L;
	private static final Random random = new Random();
	private volatile boolean running = true;
	private final SpiralGridAlgorithm spiral;
	private volatile Camera camera;
	private final int[] temp = new int[2];
	private int lastX = Integer.MAX_VALUE;
	private int lastZ = Integer.MAX_VALUE;
	private MassChunkHeightData massChunkHeightData;
	private final ChunkWorkerQue que;
	private final WorldNoiseMachine machine;
	private final int[][] heights = new int[66][66];
	private final GiantEntityDictionary dictionary;
	private final PointGenerator2D giantEntitySpawner;
	private final ArrayList<float[]> giantEntityListTemp = new ArrayList();
	private Thread t;
	private boolean working;
	SecondaryLoop(Camera camera, WorldNoiseMachine machine, int maxLoadDistance){
		dictionary = new GiantEntityDictionary();
		giantEntitySpawner =
			new PointGenerator2D(machine.getGiantEntitySeed(), dictionary.getAverageDistance(),
				dictionary.getMinDistance(), 1.0f);
		this.camera = camera;
		this.machine = machine;
		spiral = new SpiralGridAlgorithm();
		// ---
		// And this should prevent the map from generating too many chunks will
		// AFK.
		// ---
		spiral.setMaxDistance(maxLoadDistance);
		que = new ChunkWorkerQue();
		t = new Thread(this);
		t.setName("Secondary Loading Thread");
		t.setDaemon(true);
	}
	public void run(){
		while(running)
			loadNext();
	}
	public void setMaxLoadDistance(int distance){
		spiral.setMaxDistance(distance);
	}
	private void attemptGenerateChunk(){
		int x = spiral.getX()*LandscapeChunk.LandscapeSize;
		int z = spiral.getY()*LandscapeChunk.LandscapeSize;
		loadMassChunkHeightData(x, z);
		ChunkHeightData heightData = null;
		if(!massChunkHeightData.getHeights(x, z, temp)){
			heightData = new ChunkHeightData(machine, x, z, massChunkHeightData);
			heightData.getChunkHeight(temp);
		}
		int y;
		for(int i = 0; i<temp[1]; i++){
			y = i*LandscapeChunk.LandscapeSize+temp[0];
			File file = Algorithms.getChunkPath(x, y, z);
			if(file.exists()&&file.length()>0){
				try{
					Thread.sleep(1);
				}catch(Exception exception){
					exception.printStackTrace();
				}
				continue;
			}
			if(heightData==null)
				heightData = new ChunkHeightData(machine, x, z, massChunkHeightData);
			if(que.size()>0){
				ChunkWorkerTask task = que.take();
				genChunk(Algorithms.getChunkPath(task.getX(), task.getY(), task.getZ()), task.getX(),
					task.getY(), task.getZ(), task.getHeightData());
				task.setFinished();
			}
			genChunk(Algorithms.getChunkPath(x, y, z), x, y, z, heightData);
		}
	}
	private void genChunk(File file, int x, int y, int z, ChunkHeightData heightData){
		FloatBuffer vertexData, waterVertexData;
		ShortBuffer indexData, waterIndexData;
		ByteBuffer colorData;
		ArrayList<EntityDataRaw> entityLocations = new ArrayList();
		ArrayList<GrassDataRaw> grassLocations = new ArrayList();
		int grassPatchCount = 0;
		{
			// ---
			// Add mesh data.
			// ---
			// TODO Reuse this, and stop wasting memory!
			MeshFormatter meshFormatter = new MeshFormatter();
			BlockData blockData = new BlockData(meshFormatter);
			int a, b, c;
			int tempA, tempC;
			byte type;
			byte waterId = Block.Water.id();
			for(a = 0; a<66; a++){
				tempA = a-1+x;
				for(b = 0; b<66; b++){
					heights[a][b] =
						a==0||b==0||a==65||b==65?machine.getGroundLevel(tempA, b-1+z):heightData.getHeight(
							tempA, b-1+z);
						type = Block.Grass.id();
						for(c = 0; c<66; c++){
							tempC = c-1+y;
							if(tempC<heights[a][b])
								blockData.setBlock(a-1, c-1, b-1, type);
							else if(tempC<0)
								blockData.setBlock(a-1, c-1, b-1, waterId);
						}
				}
			}
			MeshRenderer render = blockData.mesh(false);
			vertexData = render.getVertexData();
			indexData = render.getIndexData();
			waterVertexData = render.getWaterVertexData();
			waterIndexData = render.getWaterIndexData();
		}
		{
			// ---
			// Add entity data.
			// ---
			int a, b, h;
			int tempA, tempB;
			float humidity;
			float tempature;
			EntityType type;
			Vector3f colorVec = new Vector3f();
			for(a = 0; a<64; a++)
				for(b = 0; b<64; b++){
					h = heights[a+1][b+1];
					if(h<y||h>=y+64)
						continue;
					tempA = a+x;
					tempB = b+z;
					type =
						randomPlant(humidity = heightData.getHumidity(tempA, tempB),
						tempature = heightData.getTempature(tempA, tempB),
							heightData.getLevel(tempA, tempB), tempA, h, tempB,
							machine.getGiantEntitySeed()^100799);
					if(type==null)
						continue;
					if(type.isGrass){
						WorldNoiseMachine.getBiomeColorAt(humidity, tempature, colorVec);
						grassLocations.add(new GrassDataRaw(type.ordinal(), tempA+0.5f, heightData.getHeight(
							tempA, tempB), tempB+0.5f, (float)(Math.random()*Math.PI*2), 2.0f+(float)(Math
								.random()*0.3f-0.15f), colorVec.x, colorVec.y, colorVec.z));
						continue;
					}
					entityLocations.add(new EntityDataRaw(type.ordinal(), tempA+0.5f, heightData.getHeight(
						tempA, tempB), tempB+0.5f, (float)(Math.random()*Math.PI*2),
						(float)(Math.random()*0.1f+0.15f)));
				}
			giantEntitySpawner.noise(x, z, 64, giantEntityListTemp);
			int fx, fz;
			for(float[] f : giantEntityListTemp){
				fx = (int)Math.floor(f[0]);
				fz = (int)Math.floor(f[1]);
				type =
					dictionary.randomEntity(heightData.getBiome(fx, fz), machine.getGiantEntitySeed()+1, fx, fz);
				if(type!=null)
					entityLocations.add(new EntityDataRaw(type.ordinal(), f[0], type.groundHits.getGround(
						machine, heightData, f[0], f[1], f[2], f[3]), f[1], f[2], f[3]));
			}
			giantEntityListTemp.clear();
			grassLocations.sort(new Comparator<GrassDataRaw>(){
				public int compare(GrassDataRaw a, GrassDataRaw b){
					return a.getType()==b.getType()?0:a.getType()<b.getType()?1:-1;
				}
			});
			int lastType = -1;
			for(GrassDataRaw data : grassLocations)
				if(data.getType()!=lastType){
					lastType = data.getType();
					grassPatchCount++;
				}
		}
		{
			// ---
			// Add biome color data.
			// ---
			colorData = ByteBuffer.allocate(64*64*3);
			int blockX, blockZ;
			int tempX, tempZ;
			Vector3f colors = new Vector3f();
			for(blockZ = 0; blockZ<64; blockZ++)
				for(blockX = 0; blockX<64; blockX++){
					tempX = blockX+x;
					tempZ = blockZ+z;
					WorldNoiseMachine.getBiomeColorAt(heightData.getHumidity(tempX, tempZ),
						heightData.getTempature(tempX, tempZ), colors);
					colorData.put((byte)Math.round(colors.x*255));
					colorData.put((byte)Math.round(colors.y*255));
					colorData.put((byte)Math.round(colors.z*255));
				}
			colorData.flip();
		}
		{
			// ---
			// Compile file.
			// ---
			int byteCount = 0;
			boolean hasWater = waterIndexData!=null;
			byteCount += vertexData.capacity()*4+4;
			byteCount += indexData.capacity()*2+4;
			byteCount += entityLocations.size()*6*4+4;
			byteCount += grassLocations.size()*9*4+8;
			byteCount += 64*64*3;
			byteCount += 1;
			if(waterIndexData!=null){
				byteCount += waterVertexData.capacity()*4+4;
				byteCount += waterIndexData.capacity()*2+4;
			}
			BinaryFile bin = new BinaryFile(byteCount);
			bin.addInt(vertexData.capacity());
			bin.addInt(indexData.capacity());
			bin.addInt(entityLocations.size());
			bin.addInt(grassLocations.size());
			bin.addInt(grassPatchCount);
			bin.addBoolean(hasWater);
			if(hasWater){
				bin.addInt(waterVertexData.capacity());
				bin.addInt(waterIndexData.capacity());
			}
			while(vertexData.hasRemaining())
				bin.addFloat(vertexData.get());
			while(indexData.hasRemaining())
				bin.addShort(indexData.get());
			for(EntityDataRaw data : entityLocations){
				bin.addInt(data.getType());
				bin.addFloat(data.getX());
				bin.addFloat(data.getY());
				bin.addFloat(data.getZ());
				bin.addFloat(data.getR());
				bin.addFloat(data.getS());
			}
			for(GrassDataRaw data : grassLocations){
				bin.addInt(data.getType());
				bin.addFloat(data.getX());
				bin.addFloat(data.getY());
				bin.addFloat(data.getZ());
				bin.addFloat(data.getR());
				bin.addFloat(data.getS());
				bin.addFloat(data.getRed());
				bin.addFloat(data.getGreen());
				bin.addFloat(data.getBlue());
			}
			while(colorData.hasRemaining())
				bin.addByte(colorData.get());
			if(hasWater){
				while(waterVertexData.hasRemaining())
					bin.addFloat(waterVertexData.get());
				while(waterIndexData.hasRemaining())
					bin.addShort(waterIndexData.get());
			}
			bin.compress(false);
			bin.compile(file);
		}
	}
	private void loadMassChunkHeightData(int x, int z){
		if(massChunkHeightData==null){
			massChunkHeightData =
				new MassChunkHeightData(Algorithms.groupLocation(x, 128*64), Algorithms.groupLocation(z, 128*64));
			return;
		}
		int minX = massChunkHeightData.getX();
		int minZ = massChunkHeightData.getZ();
		if(x>=minX&&z>=minZ&&x<minX+128*64&&z<minZ+128*64)
			return;
		massChunkHeightData =
			new MassChunkHeightData(Algorithms.groupLocation(x, 128*64), Algorithms.groupLocation(z, 128*64));
	}
	private void loadNext(){
		try{
			Thread.sleep(1);
		}catch(Exception exception){
			exception.printStackTrace();
		}
		updateCameraLocation();
		if(spiral.hasNext()){
			updateWorkingState(true);
			spiral.next();
			attemptGenerateChunk();
		}else{
			updateWorkingState(false);
			try{
				Thread.sleep(50);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
	private void updateCameraLocation(){
		int x =
			Algorithms.groupLocation((int)camera.x, LandscapeChunk.LandscapeSize)/LandscapeChunk.LandscapeSize;
		int z =
			Algorithms.groupLocation((int)camera.z, LandscapeChunk.LandscapeSize)/LandscapeChunk.LandscapeSize;
		if(x!=lastX||z!=lastZ){
			lastX = x;
			lastZ = z;
			spiral.setOrigin(lastX, lastZ);
			spiral.reset();
		}
	}
	private void updateWorkingState(boolean state){
		if(state==working)
			return;
		working = state;
		if(working)
			System.out.println("Generator is now working.");
		else
			System.out.println("Generator is now resting.");
	}
	void dispose(){
		running = false;
	}
	ChunkWorkerQue getQue(){
		return que;
	}
	void start(){
		t.start();
		t = null;
	}
}
