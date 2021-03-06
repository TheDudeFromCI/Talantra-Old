package com.wraithavens.conquest.Utility.Debug;

import java.util.ArrayList;
import com.wraithavens.conquest.Utility.Debug.Commands.HelpCommand;
import com.wraithavens.conquest.Utility.Debug.Commands.SettingsCommand;
import com.wraithavens.conquest.Utility.Debug.Commands.TpCommand;

public class ConsoleCommands implements ConsoleListener{
	private final ColorConsole console;
	private final ArrayList<Command> commands = new ArrayList();
	public ConsoleCommands(ColorConsole console){
		this.console = console;
		{
			commands.add(new TpCommand(console));
			commands.add(new HelpCommand(console, commands));
			commands.add(new SettingsCommand(console));
		}
	}
	public void onCommandSent(String command){
		console.println(ChatColor.DARK_GRAY+">> "+command);
		String[] args = command.toLowerCase().split(" ");
		for(Command c : commands)
			if(c.getCommandName().equals(args[0])){
				c.parse(args);
				return;
			}
		console.println(ChatColor.RED+"Unknown command, '"+ChatColor.GOLD+args[0]+ChatColor.RED+"'.");
	}
}
