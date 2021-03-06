package com.wraithavens.conquest.Utility.Debug.Commands;

import com.wraithavens.conquest.Launcher.MainLoop;
import com.wraithavens.conquest.SinglePlayer.SinglePlayerGame;
import com.wraithavens.conquest.SinglePlayer.Blocks.World.BetterChunkLoader;
import com.wraithavens.conquest.SinglePlayer.Noise.Biome;
import com.wraithavens.conquest.SinglePlayer.Noise.WorldNoiseMachine;
import com.wraithavens.conquest.SinglePlayer.RenderHelpers.Camera;
import com.wraithavens.conquest.Utility.Debug.ChatColor;
import com.wraithavens.conquest.Utility.Debug.ColorConsole;
import com.wraithavens.conquest.Utility.Debug.Command;

public class TpCommand implements Command{
	private final ColorConsole console;
	public TpCommand(ColorConsole console){
		this.console = console;
	}
	public String getCommandName(){
		return "tp";
	}
	public String getDescription(){
		return "Finds the nearest location with a biome of the requested type.";
	}
	public String getUsage(){
		return "tp [biome] {-maxRange:#}";
	}
	public void parse(String[] args){
		if(args.length!=2&&args.length!=4){
			console.println(ChatColor.RED+"Error! Unknown number of arguments.");
			return;
		}
		if(args.length==2){
			final Biome targetBiome = Biome.getByName(args[1]);
			if(targetBiome==null){
				console.println(ChatColor.RED+"Error! Biome not found. '"+args[1]+"'.");
				return;
			}
			console.println(ChatColor.GREEN+"Begining search...");
			MainLoop.endLoopTasks.add(new Runnable(){
				public void run(){
					SinglePlayerGame game = SinglePlayerGame.INSTANCE;
					if(game==null){
						console.println(ChatColor.RED+"Error! Game not initalized!");
						return;
					}
					WorldNoiseMachine machine = game.getWorldNoiseMachine();
					BetterChunkLoader grid = new BetterChunkLoader();
					grid.setMaxDistance(Integer.MAX_VALUE);
					int searchSize = 100;
					Camera camera = game.getCamera();
					int x, z;
					float[] temp = new float[3];
					while(true){
						x = grid.getX()*searchSize+camera.getBlockX();
						z = grid.getY()*searchSize+camera.getBlockZ();
						if(machine.getBiomeAt(x, z, temp)==targetBiome){
							camera.teleport(x, machine.getGroundLevel(x, z)+6, z);
							break;
						}
						if(!grid.hasNext()){
							console.println(ChatColor.YELLOW+"Biome not found.");
							return;
						}
						grid.next();
					}
					console.println(ChatColor.GREEN+"Biome found.");
				}
			});
		}else{
			final float x, y, z;
			try{
				x = Float.valueOf(args[1]);
			}catch(NumberFormatException exception){
				console.println(ChatColor.RED+"Thats not a number! '"+args[1]+"'");
				return;
			}
			try{
				y = Float.valueOf(args[2]);
			}catch(NumberFormatException exception){
				console.println(ChatColor.RED+"Thats not a number! '"+args[2]+"'");
				return;
			}
			try{
				z = Float.valueOf(args[3]);
			}catch(NumberFormatException exception){
				console.println(ChatColor.RED+"Thats not a number! '"+args[3]+"'");
				return;
			}
			MainLoop.endLoopTasks.add(new Runnable(){
				public void run(){
					SinglePlayerGame game = SinglePlayerGame.INSTANCE;
					if(game==null){
						console.println(ChatColor.RED+"Error! Game not initalized!");
						return;
					}
					game.getCamera().teleport(x, y, z);
					console.println(ChatColor.GREEN+"Teleported to ("+x+", "+y+", "+z+").");
				}
			});
		}
	}
}
