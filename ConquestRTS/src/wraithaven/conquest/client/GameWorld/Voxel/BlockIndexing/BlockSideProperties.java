package wraithaven.conquest.client.GameWorld.Voxel.BlockIndexing;

import wraithaven.conquest.client.GameWorld.Voxel.Texture;

public class BlockSideProperties{
	public Texture texture;
	public int rotation, side;
	public float r, g, b;
	@Override public boolean equals(Object o){
		if(o instanceof BlockSideProperties){
			BlockSideProperties p = (BlockSideProperties)o;
			if(p.texture!=texture)return false;
			if(p.rotation!=rotation)return false;
			if(p.side!=side)return false;
			if(p.r!=r||p.g!=g||p.b!=b)return false;
			return true;
		}
		return false;
	}
}