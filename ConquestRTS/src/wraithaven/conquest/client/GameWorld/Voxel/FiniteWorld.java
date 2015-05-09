package wraithaven.conquest.client.GameWorld.Voxel;

import java.util.ArrayList;
import wraithaven.conquest.client.GameWorld.LoopControls.VoxelWorldBounds;

public class FiniteWorld implements ChunkStorage{
	private final VoxelWorldBounds bounds;
	private final Chunk[][][] chunks;
	private final ArrayList<Chunk> chunkList = new ArrayList();
	public FiniteWorld(VoxelWorldBounds bounds){
		this.bounds=bounds;
		chunks=new Chunk[bounds.chunkEndX-bounds.chunkStartX+1][bounds.chunkEndY-bounds.chunkStartY+1][bounds.chunkEndZ-bounds.chunkStartZ+1];
	}
	public void addChunk(Chunk chunk){
		if(chunk.chunkX<bounds.chunkStartX||chunk.chunkX>bounds.chunkEndX)return;
		if(chunk.chunkY<bounds.chunkStartY||chunk.chunkY>bounds.chunkEndY)return;
		if(chunk.chunkZ<bounds.chunkStartZ||chunk.chunkZ>bounds.chunkEndZ)return;
		chunks[chunk.chunkX-bounds.startX][chunk.chunkY-bounds.startY][chunk.chunkZ-bounds.startZ]=chunk;
		chunkList.add(chunk);
	}
	public void removeChunk(Chunk chunk){
		if(chunk.chunkX<bounds.chunkStartX||chunk.chunkX>bounds.chunkEndX)return;
		if(chunk.chunkY<bounds.chunkStartY||chunk.chunkY>bounds.chunkEndY)return;
		if(chunk.chunkZ<bounds.chunkStartZ||chunk.chunkZ>bounds.chunkEndZ)return;
		chunks[chunk.chunkX-bounds.startX][chunk.chunkY-bounds.startY][chunk.chunkZ-bounds.startZ]=null;
		chunkList.remove(chunk);
	}
	public Chunk getChunk(int x, int y, int z){
		if(x<bounds.chunkStartX||x>bounds.chunkEndX)return null;
		if(y<bounds.chunkStartY||y>bounds.chunkEndY)return null;
		if(z<bounds.chunkStartZ||z>bounds.chunkEndZ)return null;
		return chunks[x-bounds.startX][y-bounds.startY][z-bounds.startZ];
	}
	public int getChunkCount(){ return chunkList.size(); }
	public Chunk getChunk(int index){ return chunkList.get(index); }
}