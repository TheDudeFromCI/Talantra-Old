package wraithaven.conquest.client.GameWorld.Voxel.BlockShapes;

import wraithaven.conquest.client.GameWorld.Voxel.BlockShape;

public class Pyramid extends BlockShape{
	private static final boolean[] BLOCKS = {
		true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, true, true,
		
		true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, true, true,
		
		false, false, false, false, false, false, false, false,
		false, true, true, true, true, true, true, false,
		false, true, true, true, true, true, true, false,
		false, true, true, true, true, true, true, false,
		false, true, true, true, true, true, true, false,
		false, true, true, true, true, true, true, false,
		false, true, true, true, true, true, true, false,
		false, false, false, false, false, false, false, false,
		
		false, false, false, false, false, false, false, false,
		false, true, true, true, true, true, true, false,
		false, true, true, true, true, true, true, false,
		false, true, true, true, true, true, true, false,
		false, true, true, true, true, true, true, false,
		false, true, true, true, true, true, true, false,
		false, true, true, true, true, true, true, false,
		false, false, false, false, false, false, false, false,
		
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, true, true, true, true, false, false,
		false, false, true, true, true, true, false, false,
		false, false, true, true, true, true, false, false,
		false, false, true, true, true, true, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, true, true, true, true, false, false,
		false, false, true, true, true, true, false, false,
		false, false, true, true, true, true, false, false,
		false, false, true, true, true, true, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, true, true, false, false, false,
		false, false, false, true, true, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, true, true, false, false, false,
		false, false, false, true, true, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
	};
	protected boolean[] getBlocks(){ return BLOCKS; }
	public boolean fullSide(int side){ return side==3; }
}