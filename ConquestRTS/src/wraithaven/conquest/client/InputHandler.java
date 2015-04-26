package wraithaven.conquest.client;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import wraith.library.LWJGL.Camera;
import wraith.library.LWJGL.Voxel.VoxelBlock;
import wraith.library.LWJGL.Voxel.VoxelChunk;
import wraith.library.LWJGL.Voxel.VoxelWorld;
import wraith.library.MiscUtil.BoundingBox;
import wraith.library.MiscUtil.Sphere;
import static org.lwjgl.glfw.GLFW.*;

public class InputHandler{
	private boolean w, a, s, d, shift, space, q, e;
	private int x, y, z, camChunkX, camChunkY, camChunkZ, cStartX, cStartY, cStartZ, cEndX, cEndY, cEndZ, lastCamChunkX, lastCamChunkY, lastCamChunkZ;
	private int bcx1, bcy1, bcz1, bcx2, bcy2, bcz2;
	private Sphere cameraSphere = new Sphere();
	private BoundingBox boundingBox = new BoundingBox();
	private float currentCamX, currentCamY, currentCamZ;
	private final Camera cam;
	private final long window;
	private final DoubleBuffer mouseX = BufferUtils.createDoubleBuffer(1);
	private final DoubleBuffer mouseY = BufferUtils.createDoubleBuffer(1);
	private final IntBuffer screenWidth = BufferUtils.createIntBuffer(1);
	private final IntBuffer screenHeight = BufferUtils.createIntBuffer(1);
	private static final float MOUSE_SENSITIVITY = 3;
	private static final float MOVE_SPEED = 8;
	private static final int WORLD_RADIUS = 7;
	private static final int WORLD_HEIGHT = WorldGenerator.WORLD_HEIGHT>>4;
	public static final boolean NO_CLIP = true;
	public InputHandler(Camera cam, long window){
		this.cam=cam;
		this.window=window;
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
		glfwGetWindowSize(window, screenWidth, screenHeight);
		screenWidth.put(0, screenWidth.get(0)/2);
		screenHeight.put(0, screenHeight.get(0)/2);
		cameraSphere.r=0.2f;
	}
	public void onKey(long window, int key, int action){
		if(key==GLFW.GLFW_KEY_F12&&action==GLFW.GLFW_RELEASE)GLFW.glfwSetWindowShouldClose(window, GL11.GL_TRUE);
		if(key==GLFW.GLFW_KEY_W){
			if(action==GLFW.GLFW_PRESS)w=true;
			else if(action==GLFW.GLFW_RELEASE)w=false;
		}
		if(key==GLFW.GLFW_KEY_A){
			if(action==GLFW.GLFW_PRESS)a=true;
			else if(action==GLFW.GLFW_RELEASE)a=false;
		}
		if(key==GLFW.GLFW_KEY_S){
			if(action==GLFW.GLFW_PRESS)s=true;
			else if(action==GLFW.GLFW_RELEASE)s=false;
		}
		if(key==GLFW.GLFW_KEY_D){
			if(action==GLFW.GLFW_PRESS)d=true;
			else if(action==GLFW.GLFW_RELEASE)d=false;
		}
		if(key==GLFW.GLFW_KEY_LEFT_SHIFT){
			if(action==GLFW.GLFW_PRESS)shift=true;
			else if(action==GLFW.GLFW_RELEASE)shift=false;
		}
		if(key==GLFW.GLFW_KEY_SPACE){
			if(action==GLFW.GLFW_PRESS)space=true;
			else if(action==GLFW.GLFW_RELEASE)space=false;
		}
		if(key==GLFW.GLFW_KEY_Q)if(action==GLFW.GLFW_PRESS)q=true;
		if(key==GLFW.GLFW_KEY_E)if(action==GLFW.GLFW_PRESS)e=true;
	}
	public void updateChunks(VoxelWorld world){
		VoxelChunk chunk;
		camChunkX=(int)cam.x>>4;
		camChunkY=(int)cam.y>>4;
		camChunkZ=(int)cam.z>>4;
		if(camChunkX==lastCamChunkX&&camChunkY==lastCamChunkY&&camChunkZ==lastCamChunkZ)return;
		lastCamChunkX=camChunkX;
		lastCamChunkY=camChunkY;
		lastCamChunkZ=camChunkZ;
		for(int i = 0; i<world.getChunkCount(); i++){
			chunk=world.getChunk(i);
			if(Math.abs(camChunkX-chunk.chunkX)>WORLD_RADIUS||Math.abs(camChunkY-chunk.chunkY)>WORLD_RADIUS||Math.abs(camChunkZ-chunk.chunkZ)>WORLD_RADIUS)world.unloadChunk(chunk);
		}
		cStartX=camChunkX-WorldGenerator.CAMERA_RADIUS;
		cStartY=Math.max(camChunkY-WorldGenerator.CAMERA_RADIUS, 0);
		cStartZ=camChunkZ-WorldGenerator.CAMERA_RADIUS;
		cEndX=camChunkX+WorldGenerator.CAMERA_RADIUS;
		cEndY=Math.min(camChunkY+WorldGenerator.CAMERA_RADIUS, WORLD_HEIGHT);
		cEndZ=camChunkZ+WorldGenerator.CAMERA_RADIUS;
		for(x=cStartX; x<=cEndX; x++)for(y=cStartY; y<=cEndY; y++)for(z=cStartZ; z<=cEndZ; z++)world.getChunk(x, y, z);
	}
	public void update(VoxelWorld world, float delta){
		processMouse(delta*MOUSE_SENSITIVITY);
		processWalk(world, delta*MOVE_SPEED);
	}
	private void processMouse(float delta){
		glfwGetCursorPos(window, mouseX, mouseY);
		cam.goalRY=cam.ry+=(mouseX.get(0)-screenWidth.get(0))*delta;
		cam.goalRX=cam.rx=(float)Math.max(Math.min(cam.rx+(mouseY.get(0)-screenHeight.get(0))*delta, 90), -90);
		glfwSetCursorPos(window, screenWidth.get(0), screenHeight.get(0));
	}
	private void processWalk(VoxelWorld world, float delta){
		currentCamX=cam.goalX;
		currentCamY=cam.goalY;
		currentCamZ=cam.goalZ;
		if(w)currentCamX+=delta*(float)Math.sin(Math.toRadians(cam.ry));
		if(a)currentCamX+=delta*(float)Math.sin(Math.toRadians(cam.ry-90));
		if(s)currentCamX-=delta*(float)Math.sin(Math.toRadians(cam.ry));
		if(d)currentCamX+=delta*(float)Math.sin(Math.toRadians(cam.ry+90));
		if(canMoveTo(world, currentCamX, currentCamY, currentCamZ))cam.goalX=cam.x=currentCamX;
		currentCamX=cam.goalX;
		if(w)currentCamZ-=delta*(float)Math.cos(Math.toRadians(cam.ry));
		if(a)currentCamZ-=delta*(float)Math.cos(Math.toRadians(cam.ry-90));
		if(s)currentCamZ+=delta*(float)Math.cos(Math.toRadians(cam.ry));
		if(d)currentCamZ-=delta*(float)Math.cos(Math.toRadians(cam.ry+90));
		if(canMoveTo(world, currentCamX, currentCamY, currentCamZ))cam.goalZ=cam.z=currentCamZ;
		currentCamZ=cam.goalZ;
		if(shift)currentCamY-=delta;
		if(space)currentCamY+=delta;
		if(canMoveTo(world, currentCamX, currentCamY, currentCamZ))cam.goalY=cam.y=currentCamY;
		if(q){
			cam.goalRY-=22.5f;
			q=false;
		}
		if(e){
			cam.goalRY+=22.5f;
			e=false;
		}
	}
	private boolean canMoveTo(VoxelWorld world, float sx, float sy, float sz){
		if(NO_CLIP)return true;
		VoxelBlock block;
		cameraSphere.x=sx;
		cameraSphere.y=sy;
		cameraSphere.z=sz;
		bcx1=(int)Math.floor(sx)-1;
		bcy1=(int)Math.floor(sy)-1;
		bcz1=(int)Math.floor(sz)-1;
		bcx2=bcx1+2;
		bcy2=bcy1+2;
		bcz2=bcz1+2;
		for(x=bcx1; x<=bcx2; x++){
			for(y=bcy1; y<=bcy2; y++){
				for(z=bcz1; z<=bcz2; z++){
					block=world.getBlock(x, y, z);
					if(block==null)continue;
					boundingBox.x1=block.x;
					boundingBox.y1=block.y;
					boundingBox.z1=block.z;
					boundingBox.x2=block.x+1;
					boundingBox.y2=block.y+1;
					boundingBox.z2=block.z+1;
					if(intersectsWith(boundingBox, cameraSphere))return false;
				}
			}
		}
		return true;
	}
	private static boolean intersectsWith(BoundingBox bb, Sphere sphere){
		float dmin = 0;
		if(sphere.x<bb.x1)dmin+=Math.pow(sphere.x-bb.x1, 2);
		else if(sphere.x>bb.x2)dmin+=Math.pow(sphere.x-bb.x2, 2);
		if(sphere.y<bb.y1)dmin+=Math.pow(sphere.y-bb.y1, 2);
		else if(sphere.y>bb.y2)dmin+=Math.pow(sphere.y-bb.y2, 2);
		if(sphere.z<bb.z1)dmin += Math.pow(sphere.z-bb.z1, 2);
		else if(sphere.z>bb.z2)dmin+=Math.pow(sphere.z-bb.z1, 2);
		return dmin<=Math.pow(sphere.r, 2);
	}
}