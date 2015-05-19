package wraithaven.conquest.client.BuildingCreator;

import java.util.ArrayList;
import wraithaven.conquest.client.GameWorld.Voxel.MipmapQuality;
import wraithaven.conquest.client.GameWorld.Voxel.BlockRotation;
import wraithaven.conquest.client.GameWorld.Voxel.BlockShapes.ShapeType;
import wraithaven.conquest.client.ClientLauncher;
import wraithaven.conquest.client.GameWorld.Voxel.Texture;
import wraithaven.conquest.client.GameWorld.Voxel.BlockShape;
import wraithaven.conquest.client.GameWorld.LoopControls.MatrixUtils;
import wraithaven.conquest.client.GameWorld.Voxel.CubeTextures;
import static org.lwjgl.glfw.GLFW.*;

public class Inventory{
	private int scrollPosition;
	private final ArrayList<BlockIcon> blocks = new ArrayList();
	private static final int ROWS_SHOWN = 10;
	private static final int COLS_SHOWN = 10;
	private static final int TOTAL_SHOWN = ROWS_SHOWN*COLS_SHOWN;
	private static final float BLOCK_SPACING = 2;
	private static final float BLOCK_ZOOM = 30;
	{
		CubeTextures textures = new CubeTextures();
		Texture dirt = Texture.getTexture(ClientLauncher.textureFolder, "Light Plank.png", 4, MipmapQuality.HIGH);
		textures.xUp=dirt;
		textures.xUpRotation=0;
		textures.xDown=dirt;
		textures.xDownRotation=1;
		textures.yUp=dirt;
		textures.yUpRotation=3;
		textures.yDown=dirt;
		textures.yDownRotation=0;
		textures.zUp=dirt;
		textures.zUpRotation=3;
		textures.zDown=dirt;
		textures.zDownRotation=2;
		{
			blocks.add(new BlockIcon(ShapeType.SHAPE_0.shape, textures, BlockRotation.ROTATION_0));
			blocks.add(new BlockIcon(ShapeType.SHAPE_1.shape, textures, BlockRotation.ROTATION_0));
			blocks.add(new BlockIcon(ShapeType.SHAPE_2.shape, textures, BlockRotation.ROTATION_0));
			blocks.add(new BlockIcon(ShapeType.SHAPE_3.shape, textures, BlockRotation.ROTATION_0));
			blocks.add(new BlockIcon(ShapeType.SHAPE_4.shape, textures, BlockRotation.ROTATION_0));
			blocks.add(new BlockIcon(ShapeType.SHAPE_5.shape, textures, BlockRotation.ROTATION_0));
			blocks.add(new BlockIcon(ShapeType.SHAPE_6.shape, textures, BlockRotation.ROTATION_0));
			blocks.add(new BlockIcon(ShapeType.SHAPE_7.shape, textures, BlockRotation.ROTATION_0));
			blocks.add(new BlockIcon(ShapeType.SHAPE_8.shape, textures, BlockRotation.ROTATION_0));
			blocks.add(new BlockIcon(ShapeType.SHAPE_9.shape, textures, BlockRotation.ROTATION_0));
			blocks.add(new BlockIcon(ShapeType.SHAPE_10.shape, textures, BlockRotation.ROTATION_0));
			blocks.add(new BlockIcon(ShapeType.SHAPE_11.shape, textures, BlockRotation.ROTATION_0));
			blocks.add(new BlockIcon(ShapeType.SHAPE_12.shape, textures, BlockRotation.ROTATION_0));
			blocks.add(new BlockIcon(ShapeType.SHAPE_13.shape, textures, BlockRotation.ROTATION_0));
			blocks.add(new BlockIcon(ShapeType.SHAPE_14.shape, textures, BlockRotation.ROTATION_0));
			blocks.add(new BlockIcon(ShapeType.SHAPE_15.shape, textures, BlockRotation.ROTATION_0));
			blocks.add(new BlockIcon(ShapeType.SHAPE_16.shape, textures, BlockRotation.ROTATION_0));
			blocks.add(new BlockIcon(ShapeType.SHAPE_17.shape, textures, BlockRotation.ROTATION_0));
			blocks.add(new BlockIcon(ShapeType.SHAPE_18.shape, textures, BlockRotation.ROTATION_0));
			blocks.add(new BlockIcon(ShapeType.SHAPE_19.shape, textures, BlockRotation.ROTATION_0));
		}
	}
	public void render(){
		if(!shown)return;
		MatrixUtils.setupOrtho(BLOCK_ZOOM*Loop.screenRes.width/Loop.screenRes.height, BLOCK_ZOOM, -100, 100);
		int lastShown = Math.min(scrollPosition+TOTAL_SHOWN, blocks.size());
		for(int i = scrollPosition; i<lastShown; i++)blocks.get(i).render(getX(i), getY(i));
		if(selectedIcon!=-1)blocks.get(selectedIcon).render(getX(mouseX), getY(mouseY));
		if(selectedHotbar!=-1)Loop.INSTANCE.getGuiHandler().getIconManager().getIcon(selectedHotbar).render(getX(mouseX), getY(mouseY));
	}
	public void updateSliderPosition(float percent){
		int size = blocks.size()-TOTAL_SHOWN;
		if(size<=0)size=0;
		scrollPosition=(int)(percent*size);
	}
	public void onMouseDown(double x, double y){
		mouseX=x;
		mouseY=Loop.screenRes.height-y;
		int lastShown = Math.min(scrollPosition+TOTAL_SHOWN, blocks.size());
		selectedIcon=-1;
		selectedHotbar=-1;
		for(int i = scrollPosition; i<lastShown; i++){
			if(isMousedOver(i)){
				selectedIcon=i;
				return;
			}
		}
		if(mouseX>=Loop.screenRes.width-GuiHandler.HOTBAR_SLOT){
			float tempY;
			for(int i = 0; i<10; i++){
				if(Loop.INSTANCE.getGuiHandler().getIconManager().getIcon(i)==null)continue;
				if(mouseY>=(tempY=(Loop.screenRes.height-GuiHandler.HOTBAR_SLOT*10)/2+GuiHandler.HOTBAR_SLOT*(9-i%10))){
					if(mouseY<tempY+GuiHandler.HOTBAR_SLOT){
						selectedHotbar=i;
						Loop.INSTANCE.getGuiHandler().getIconManager().tempHeld=i;
						break;
					}
				}
			}
		}
	}
	private boolean isMousedOver(int index){
		float x = getX(index);
		float y = getY(index);
		float minX = ((x-0.7f)/(BLOCK_ZOOM*Loop.screenRes.width/Loop.screenRes.height)+0.5f)*Loop.screenRes.width;
		float minY = ((y-0.7f)/BLOCK_ZOOM+0.5f)*Loop.screenRes.height;
		float maxX = ((x+0.7f)/(BLOCK_ZOOM*Loop.screenRes.width/Loop.screenRes.height)+0.5f)*Loop.screenRes.width;
		float maxY = ((y+0.7f)/BLOCK_ZOOM+0.5f)*Loop.screenRes.height;
		return mouseX>minX&&mouseX<maxX&&mouseY>minY&&mouseY<maxY;
	}
	public void setShown(boolean shown){
		this.shown=shown;
		if(shown)glfwSetInputMode(Loop.INSTANCE.getBuildingCreator().getWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		else{
			glfwSetCursorPos(Loop.INSTANCE.getBuildingCreator().getWindow(), Loop.screenRes.width/2.0, Loop.screenRes.height/2.0);
			glfwSetInputMode(Loop.INSTANCE.getBuildingCreator().getWindow(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
		}
	}
	private boolean shown;
	private double mouseX, mouseY;
	private int selectedIcon = -1;
	private int selectedHotbar = -1;
	public void onMouseMove(double x, double y){
		mouseX=x;
		mouseY=Loop.screenRes.height-y;
	}
	public BlockIcon getBlockIcon(BlockShape shape, CubeTextures textures){
		for(int i = 0; i<blocks.size(); i++){
			if(blocks.get(i).shape==shape&&blocks.get(i).textures==textures)return blocks.get(i);
		}
		return null;
	}
	public void onMouseUp(){
		if(selectedIcon==-1&&selectedHotbar==-1)return;
		if(selectedIcon>-1){
			if(mouseX>=Loop.screenRes.width-GuiHandler.HOTBAR_SLOT){
				float y;
				for(int i = 0; i<10; i++){
					if(mouseY>=(y=(Loop.screenRes.height-GuiHandler.HOTBAR_SLOT*10)/2+GuiHandler.HOTBAR_SLOT*(9-i%10))){
						if(mouseY<y+GuiHandler.HOTBAR_SLOT){
							Loop.INSTANCE.getGuiHandler().getIconManager().addIcon(blocks.get(selectedIcon), i);
							if(Loop.INSTANCE.getGuiHandler().getHotbarSelectorId()==i)Loop.INSTANCE.getGuiHandler().updateBlockRotations();
							break;
						}
					}
				}
			}
		}else{
			if(mouseX<Loop.screenRes.width-GuiHandler.HOTBAR_SLOT)Loop.INSTANCE.getGuiHandler().getIconManager().addIcon(null, selectedHotbar);
			else{
				float y;
				for(int i = 0; i<10; i++){
					if(mouseY>=(y=(Loop.screenRes.height-GuiHandler.HOTBAR_SLOT*10)/2+GuiHandler.HOTBAR_SLOT*(9-i%10))){
						if(mouseY<y+GuiHandler.HOTBAR_SLOT){
							BlockIcon temp = Loop.INSTANCE.getGuiHandler().getIconManager().getIcon(i);
							Loop.INSTANCE.getGuiHandler().getIconManager().addIcon(Loop.INSTANCE.getGuiHandler().getIconManager().getIcon(selectedHotbar), i);
							Loop.INSTANCE.getGuiHandler().getIconManager().addIcon(temp, selectedHotbar);
							if(Loop.INSTANCE.getGuiHandler().getHotbarSelectorId()==i||Loop.INSTANCE.getGuiHandler().getHotbarSelectorId()==selectedHotbar)Loop.INSTANCE.getGuiHandler().updateBlockRotations();
							break;
						}
					}
				}
			}
		}
		selectedIcon=-1;
		selectedHotbar=-1;
		Loop.INSTANCE.getGuiHandler().getIconManager().tempHeld=-1;
	}
	public void addBlock(BlockShape shape, CubeTextures cubeTextures){ blocks.add(new BlockIcon(shape, cubeTextures, BlockRotation.ROTATION_0)); }
	public boolean isShown(){ return shown; }
	public ArrayList<BlockIcon> getBlocks(){ return blocks; }
	private static float getX(int index){ return -(COLS_SHOWN-1)*BLOCK_SPACING/2f+(index%COLS_SHOWN)*BLOCK_SPACING; }
	private static float getY(int index){ return -(-(ROWS_SHOWN-1)*BLOCK_SPACING/2f+(index/COLS_SHOWN)*BLOCK_SPACING); }
	private static float getX(double mouseX){ return ((float)mouseX/Loop.screenRes.width-0.5f)*BLOCK_ZOOM*Loop.screenRes.width/Loop.screenRes.height; }
	private static float getY(double mouseY){ return ((float)mouseY/Loop.screenRes.height-0.5f)*BLOCK_ZOOM; }
}