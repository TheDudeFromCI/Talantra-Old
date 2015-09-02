package com.wraithavens.conquest.SinglePlayer.Entities;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL20;
import com.wraithavens.conquest.Launcher.WraithavensConquest;
import com.wraithavens.conquest.Math.Vector3f;
import com.wraithavens.conquest.SinglePlayer.Blocks.Landscape.LandscapeWorld;
import com.wraithavens.conquest.SinglePlayer.RenderHelpers.Camera;
import com.wraithavens.conquest.SinglePlayer.RenderHelpers.GlError;
import com.wraithavens.conquest.SinglePlayer.RenderHelpers.ShaderProgram;

public class EntityDatabase{
	static int SingularShaderAttrib;
	private final ArrayList<Entity> entities = new ArrayList();
	private final Comparator entitySorter = new Comparator<Entity>(){
		public int compare(Entity a, Entity b){
			if(a.mesh==b.mesh)
				return 0;
			if(a.isColorBlended()!=b.isColorBlended())
				return (a.isColorBlended()?1:-1)*(isColorBlended?-1:1);
			if(a.mesh.getType().sways!=b.mesh.getType().sways)
				return (a.sways()?1:-1)*(isSwaying?-1:1);
			return a.mesh.getId()>b.mesh.getId()?1:-1;
		}
	};
	private final ShaderProgram shader;
	private final Camera camera;
	private LandscapeWorld landscape;
	private boolean isSwaying = false;
	private boolean isColorBlended = false;
	public EntityDatabase(Camera camera){
		this.camera = camera;
		GlError.out("Creating entity database.");
		shader =
			new ShaderProgram(new File(WraithavensConquest.assetFolder, "ModelShader.vert"), null, new File(
				WraithavensConquest.assetFolder, "ModelShader.frag"));
		shader.bind();
		shader.loadUniforms("uni_swayAmount", "uni_meshCenter", "uni_time", "uni_colorBlended",
			"uni_textureOffset", "uni_textureSize");
		SingularShaderAttrib = shader.getAttributeLocation("shade");
		GL20.glEnableVertexAttribArray(SingularShaderAttrib);
		GlError.dumpError();
	}
	public void addEntity(Entity e){
		entities.add(e);
		// ---
		// Let's sort them so that entities of the same type render together.
		// This allows us to render batches, without have to rebind the same
		// VBOs multiple times per frame.
		// ---
		sort();
		GlError.dumpError();
	}
	public void clear(){
		GlError.out("Clearing entity database.");
		for(Entity e : entities)
			e.dispose();
		entities.clear();
		GlError.dumpError();
	}
	public void dispose(){
		clear();
		shader.dispose();
	}
	public void removeEntity(Entity e){
		entities.remove(e);
	}
	public void render(){
		// ---
		// Render all entities. Switching mesh types as nessicary.
		// ---
		EntityMesh mesh = null;
		boolean shaderBound = false;
		Vector3f textureOffset3d, textureSize3D;
		for(Entity e : entities){
			if(!e.canRender(landscape, camera))
				continue;
			if(e.getLod()>0){
				// ---
				// TODO Make object render more... eh, father away-ish.
				// ---
				continue;
			}
			if(!shaderBound){
				shaderBound = true;
				shader.bind();
				shader.setUniform1f(2, (float)GLFW.glfwGetTime());
				shader.setUniform1f(0, 0.0f);
			}
			if(mesh==null||e.getMesh()!=mesh){
				mesh = e.getMesh();
				mesh.bind();
				if(mesh.getType().sways!=isSwaying){
					isSwaying = mesh.getType().sways;
					shader.setUniform1f(0, isSwaying?0.0375f:0.0f);
				}
				if(mesh.getType().colorBlended!=isColorBlended){
					isColorBlended = mesh.getType().colorBlended;
					shader.setUniform1I(3, isColorBlended?1:0);
				}
				if(isColorBlended){
					textureOffset3d = mesh.getTextureOffset3D();
					textureSize3D = mesh.getTextureSize3D();
					shader.setUniform3f(4, textureOffset3d.x, textureOffset3d.y, textureOffset3d.z);
					shader.setUniform3f(5, textureSize3D.x, textureSize3D.y, textureSize3D.z);
				}
			}
			shader.setUniform2f(1, e.getX(), e.getZ());
			e.render();
		}
		GlError.dumpError();
	}
	public void setLandscape(LandscapeWorld landscape){
		this.landscape = landscape;
	}
	private void sort(){
		entities.sort(entitySorter);
	}
}
