package com.wraithavens.conquest.Launcher;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.system.MemoryUtil;

public class MainLoop{
	public static int FPS_SYNC;
	public static final LinkedBlockingQueue<Runnable> endLoopTasks = new LinkedBlockingQueue();
	private GLFWCursorPosCallback cursorPosCallback;
	private GLFWErrorCallback errorCallback;
	private GLFWKeyCallback keyCallback;
	private GLFWMouseButtonCallback mouseButtonCallback;
	private WindowInitalizer recreateInitalizer;
	private GLFWScrollCallback scrollCallback;
	private long variableYieldTime, lastTime;
	private long window;
	private WindowInitalizer windowInitalizer;
	private void init(){
		FPS_SYNC = WraithavensConquest.Settings.getFpsCap();
		GLFW.glfwSetErrorCallback(errorCallback = Callbacks.errorCallbackPrint(System.err));
		if(GLFW.glfwInit()!=GL11.GL_TRUE)
			throw new IllegalStateException("Unable to initialize GLFW");
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_FALSE);
		window = GLFW.glfwCreateWindow(windowInitalizer.width, windowInitalizer.height, "Wraithaven's Conquest",
			windowInitalizer.fullscreen?GLFW.glfwGetPrimaryMonitor():MemoryUtil.NULL, MemoryUtil.NULL);
		if(window==MemoryUtil.NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		GLFW.glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback(){
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods){
				windowInitalizer.loopObjective.key(window, key, action);
			}
		});
		GLFW.glfwSetMouseButtonCallback(window, mouseButtonCallback = new GLFWMouseButtonCallback(){
			@Override
			public void invoke(long window, int button, int action, int mods){
				windowInitalizer.loopObjective.mouse(window, button, action);
			}
		});
		GLFW.glfwSetCursorPosCallback(window, cursorPosCallback = new GLFWCursorPosCallback(){
			@Override
			public void invoke(long window, double xpos, double ypos){
				windowInitalizer.loopObjective.mouseMove(window, xpos, ypos);
			}
		});
		GLFW.glfwSetScrollCallback(window, scrollCallback = new GLFWScrollCallback(){
			@Override
			public void invoke(long window, double xoffset, double yoffset){
				windowInitalizer.loopObjective.mouseWheel(window, xoffset, yoffset);
			}
		});
		ByteBuffer vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		if(!windowInitalizer.fullscreen)
			GLFW.glfwSetWindowPos(window, (GLFWvidmode.width(vidmode)-windowInitalizer.width)/2,
				(GLFWvidmode.height(vidmode)-windowInitalizer.height)/2);
		GLFW.glfwMakeContextCurrent(window);
		GLFW.glfwSwapInterval(windowInitalizer.vSync?1:0);
		GLFW.glfwShowWindow(window);
	}
	private void loop(){
		GLContext.createFromCurrent();
		double lastTime = 0;
		double currentTime;
		double delta;
		windowInitalizer.loopObjective.preLoop();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		while(GLFW.glfwWindowShouldClose(window)==GL11.GL_FALSE){
			currentTime = GLFW.glfwGetTime();
			delta = currentTime-lastTime;
			lastTime = currentTime;
			GLFW.glfwPollEvents();
			windowInitalizer.loopObjective.update(delta, currentTime);
			windowInitalizer.loopObjective.render();
			if(MainLoop.FPS_SYNC>0)
				sync();
			while(!endLoopTasks.isEmpty())
				try{
					endLoopTasks.take().run();
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			GLFW.glfwSwapBuffers(window);
		}
	}
	private void runLoop(WindowInitalizer windowInitalizer){
		this.windowInitalizer = windowInitalizer;
		recreateInitalizer = null;
		try{
			init();
			loop();
			windowInitalizer.loopObjective.cleanUp();
			keyCallback.release();
			mouseButtonCallback.release();
			cursorPosCallback.release();
			scrollCallback.release();
		}catch(Exception exception){
			exception.printStackTrace();
		}finally{
			errorCallback.release();
			GLFW.glfwDestroyWindow(window);
			GLFW.glfwTerminate();
			System.exit(0);
		}
	}
	private void sync(){
		long sleepTime = 1000000000/FPS_SYNC;
		long yieldTime = Math.min(sleepTime, variableYieldTime+sleepTime%(1000*1000));
		long overSleep = 0;
		try{
			long t;
			while(true){
				t = System.nanoTime()-lastTime;
				if(t<sleepTime-yieldTime)
					Thread.sleep(1);
				else if(t<sleepTime)
					Thread.yield();
				else{
					overSleep = t-sleepTime;
					break;
				}
			}
		}catch(InterruptedException e){
			e.printStackTrace();
		}finally{
			lastTime = System.nanoTime()-Math.min(overSleep, sleepTime);
			if(overSleep>variableYieldTime)
				variableYieldTime = Math.min(variableYieldTime+200*1000, sleepTime);
			else if(overSleep<variableYieldTime-200*1000)
				variableYieldTime = Math.max(variableYieldTime-2*1000, 0);
		}
	}
	void create(WindowInitalizer windowInitalizer){
		runLoop(windowInitalizer);
		while(recreateInitalizer!=null)
			runLoop(recreateInitalizer);
	}
	long getWindow(){
		return window;
	}
}
