package wraithaven.conquest.client.GameWorld.Voxel.BlockShapes;

import wraithaven.conquest.client.GameWorld.Voxel.BlockShape;

public class Shape13 extends BlockShape{
	private static final boolean[] BLOCKS = {
		true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, true, true,
		true, true, true, true, true, true, true, true,
		
		true, true, true, true, true, true, true, false,
		true, true, true, true, true, true, true, false,
		true, true, true, true, true, true, true, false,
		true, true, true, true, true, true, true, false,
		true, true, true, true, true, true, true, false,
		true, true, true, true, true, true, true, false,
		true, true, true, true, true, true, true, false,
		false, false, false, false, false, false, false, false,
		
		true, true, true, true, true, true, false, false,
		true, true, true, true, true, true, false, false,
		true, true, true, true, true, true, false, false,
		true, true, true, true, true, true, false, false,
		true, true, true, true, true, true, false, false,
		true, true, true, true, true, true, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		
		true, true, true, true, true, false, false, false,
		true, true, true, true, true, false, false, false,
		true, true, true, true, true, false, false, false,
		true, true, true, true, true, false, false, false,
		true, true, true, true, true, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		
		true, true, true, false, false, false, false, false,
		true, true, true, false, false, false, false, false,
		true, true, true, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		
		true, true, false, false, false, false, false, false,
		true, true, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		
		true, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
	};
	Shape13(){}
	protected boolean[] getBlocks(){ return BLOCKS; }
}