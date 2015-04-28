package wraithaven.conquest.client.BuildingCreator;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import wraith.library.LWJGL.Camera;
import wraith.library.LWJGL.CameraTarget;
import wraith.library.LWJGL.Voxel.VoxelBlock;
import wraith.library.LWJGL.Voxel.VoxelWorld;
import wraith.library.MiscUtil.BoundingBox;
import wraith.library.MiscUtil.Sphere;
import wraithaven.conquest.client.Test;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;

public class InputController{
	private boolean w, a, s, d, shift, space;
	private int x, y, z;
	private int bcx1, bcy1, bcz1, bcx2, bcy2, bcz2;
	private float currentCamX, currentCamY, currentCamZ;
	public float mouseSensitivity = 8;
	public float moveSpeed = 8;
	private final Sphere cameraSphere = new Sphere();
	private final BoundingBox boundingBox = new BoundingBox();
	private final Camera cam;
	private final long window;
	private final DoubleBuffer mouseX = BufferUtils.createDoubleBuffer(1);
	private final DoubleBuffer mouseY = BufferUtils.createDoubleBuffer(1);
	private final IntBuffer screenWidth = BufferUtils.createIntBuffer(1);
	private final IntBuffer screenHeight = BufferUtils.createIntBuffer(1);
	public InputController(Camera cam, long window){
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
	}
	private CameraTarget cameraTarget;
	public void onMouse(int key, int action){
		if(cameraTarget==null)cameraTarget=new CameraTarget(cam);
		if(key==GLFW.GLFW_MOUSE_BUTTON_LEFT){
			if(action==GLFW.GLFW_PRESS){
				VoxelBlock block = cameraTarget.getTargetBlock(Test.voxelWorld, 500, false);
				if(block!=null)block.getChunk().setBlock(block.x, block.y, block.z, null);
			}
		}
	}
	public void update(VoxelWorld world, float delta){
		processMouse(delta*mouseSensitivity);
		processWalk(world, delta*moveSpeed);
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
	}
	private boolean canMoveTo(VoxelWorld world, float sx, float sy, float sz){
		if(sx<0||sy<0||sz<0||sx>=BuildingCreator.WORLD_BOUNDS_SIZE||sy>=BuildingCreator.WORLD_BOUNDS_SIZE||sz>=BuildingCreator.WORLD_BOUNDS_SIZE)return false;
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