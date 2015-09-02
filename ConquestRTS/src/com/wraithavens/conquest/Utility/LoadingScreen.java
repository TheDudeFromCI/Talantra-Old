package com.wraithavens.conquest.Utility;

import java.io.File;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import com.wraithavens.conquest.Launcher.MainLoop;
import com.wraithavens.conquest.Launcher.WraithavensConquest;
import com.wraithavens.conquest.Math.MatrixUtils;
import com.wraithavens.conquest.SinglePlayer.RenderHelpers.ShaderProgram;
import com.wraithavens.conquest.SinglePlayer.RenderHelpers.Texture;

public class LoadingScreen{
	private static final float SPINNER_SPEED = 2f;
	private static final int IMAGE_FLIP_SECONDS = 8;
	private LoadingScreenTask task;
	private final UiElement spinner;
	private final UiElement background;
	private final UiElement backgroundOverlay;
	private final ShaderProgram shader;
	private final int fileCount;
	private double lastImageTime;
	private int lastPictureIndex;
	private boolean setup = false;
	public LoadingScreen(){
		shader = new ShaderProgram("LoadingScreen");
		shader.loadUniforms("texture", "alpha");
		shader.setUniform1I(0, 0);
		spinner =
			new UiElement(Texture.getTexture(new File(WraithavensConquest.assetFolder, "Loading Spinner.png")));
		background =
			new UiElement(Texture.getTexture(new File(WraithavensConquest.loadingScreenImagesFolder,
				lastPictureIndex+".png")));
		backgroundOverlay = new UiElement(background.texture);
		{
			int screenWidth = WraithavensConquest.INSTANCE.getScreenWidth();
			int screenHeight = WraithavensConquest.INSTANCE.getScreenHeight();
			spinner.w = 64;
			spinner.h = 64;
			spinner.x = screenWidth-spinner.w/2f;
			spinner.y = spinner.h/2f;
			String[] list = new File(WraithavensConquest.loadingScreenImagesFolder).list();
			int count = 0;
			for(String s : list)
				if(s.endsWith(".png"))
					count++;
			fileCount = count;
			lastPictureIndex = (int)(Math.random()*fileCount);
			background.w = screenWidth;
			background.h = screenHeight;
			background.x = screenWidth/2f;
			background.y = screenHeight/2f;
			backgroundOverlay.w = screenWidth;
			backgroundOverlay.h = screenHeight;
			backgroundOverlay.x = screenWidth/2f;
			backgroundOverlay.y = screenHeight/2f;
			lastImageTime = GLFW.glfwGetTime();
		}
	}
	public void dispose(){
		background.texture.dispose();
		backgroundOverlay.texture.dispose();
		spinner.texture.dispose();
		shader.dispose();
	}
	public boolean hasTask(){
		return task!=null;
	}
	public void render(){
		if(!setup){
			MatrixUtils.setupImageOrtho(WraithavensConquest.INSTANCE.getScreenWidth(),
				WraithavensConquest.INSTANCE.getScreenHeight(), -1, 1);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			shader.bind();
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_BLEND);
			MainLoop.FPS_SYNC = false;
			setup = true;
		}
		background.render(shader);
		backgroundOverlay.render(shader);
		spinner.render(shader);
	}
	public void setTask(LoadingScreenTask task){
		this.task = task;
		setup = false;
	}
	public void update(double time){
		spinner.r = (float)(time*SPINNER_SPEED);
		if(task.runStep()){
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			MainLoop.FPS_SYNC = true;
			task = null;
			return;
		}
		if(time-lastImageTime>IMAGE_FLIP_SECONDS){
			if(background.texture!=backgroundOverlay.texture)
				background.texture.dispose();
			int randomPicture = (int)(Math.random()*fileCount);
			while(randomPicture==lastPictureIndex)
				randomPicture = (int)(Math.random()*fileCount);
			lastPictureIndex = randomPicture;
			Texture newTexture =
				Texture.getTexture(new File(WraithavensConquest.loadingScreenImagesFolder, lastPictureIndex
					+".png"));
			background.texture = backgroundOverlay.texture;
			backgroundOverlay.texture = newTexture;
			lastImageTime = time;
		}
		backgroundOverlay.a = (float)Math.min(time-lastImageTime, 1);
	}
}
