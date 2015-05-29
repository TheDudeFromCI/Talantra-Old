package wraithaven.conquest.client.GameWorld.Voxel.BlockShapes;

import wraithaven.conquest.client.GameWorld.Voxel.BlockShape;

public class Shape16 extends BlockShape{
	private static final boolean[] BLOCKS = {
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		
		false, false, false, false, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		
		false, false, false, false, false, false, false, false,
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
		false, false, false, false, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		true, true, true, true, false, false, false, false,
		true, true, true, true, false, false, false, false,
		
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		false, false, false, false, false, false, false, false,
		true, true, true, true, false, false, false, false,
	};
	Shape16(){}
	protected boolean[] getBlocks(){ return BLOCKS; }
}