package wraithaven.conquest.client;

import java.awt.Color;
import java.awt.Graphics2D;
import wraith.library.Multiplayer.Client;
import wraith.library.Multiplayer.ClientListener;
import wraith.library.WindowUtil.GUI.ScrollPaneEntry;
import wraithaven.conquest.Pong;
import wraithaven.conquest.PacketType;

public class ServerListSlot implements ScrollPaneEntry{
	private String ip;
	private String serverName;
	private int port;
	private boolean selected;
	private long clickTime;
	private boolean serverUp;
	private boolean unknownHost;
	private boolean pinging;
	private ServerList serverList;
	private Client client;
	private Pong pong;
	private long pingStart;
	private int pingSpeed;
	public ServerListSlot(ServerList serverList, String lastName, String ip, int port){
		this.ip=ip;
		this.port=port;
		this.serverList=serverList;
		serverName=lastName;
		refreash();
	}
	public void renderEntry(Graphics2D g, int x, int y, int width, int height){
		g.setColor(selected?Color.red:Color.green);
		g.fillRect(x, y, width, height);
		g.setColor(Color.black);
		g.drawRect(x, y, width, height);
		g.drawString("Ip: "+ip, x+3, y+13);
		g.drawString("Port: "+port, x+3, y+26);
		g.drawString("Ping: "+pingSpeed, x+3, y+65);
		if(pinging){
			g.drawString("Server Name: "+(serverName==null?"Unknown":serverName), x+3, y+39);
			g.drawString("Status: Pinging...", x+3, y+52);
		}else{
			if(unknownHost){
				g.drawString("Server Name: "+(serverName==null?"Unknown":serverName), x+3, y+39);
				g.drawString("Status: Unknown Host", x+3, y+52);
			}else if(!serverUp){
				g.drawString("Server Name: "+(serverName==null?"Unknown":serverName), x+3, y+39);
				g.drawString("Status: Could Not Connect", x+3, y+52);
			}else{
				g.drawString("Server Name: "+pong.getName(), x+3, y+39);
				g.drawString("Status: Server Online", x+3, y+52);
				g.drawString("Player Count: "+pong.getPlayerCount()+"/"+pong.getMaxPlayerCount(), x+300, y+13);
				g.drawString("Channel Count: "+pong.getChannelCount()+"/"+pong.getMaxChannelCount(), x+300, y+26);
				g.drawString("MOTD: "+pong.getMOTD(), x+300, y+39);
			}
		}
	}
	public void onEntryClick(){
		long time = System.currentTimeMillis();
		if(selected&&time-clickTime<600){
			//TODO Load server.
			return;
		}
		if(serverList.getSelectedServer()!=null)serverList.getSelectedServer().selected=false;
		selected=true;
		clickTime=time;
		serverList.setSelectedServer(this);
		repaint();
	}
	public void refreash(){
		pingStart=System.currentTimeMillis();
		pinging=true;
		createClient();
		repaint();
	}
	private void createClient(){
		unknownHost=false;
		serverUp=true;
		new Thread(new Runnable(){
			public void run(){
				try{
					client=new Client(ip, port, new ClientListener(){
						public void unknownHost(){
							unknownHost=true;
							pinging=false;
							pingSpeed=(int)(System.currentTimeMillis()-pingStart);
							repaint();
						}
						public void couldNotConnect(){
							serverUp=false;
							pinging=false;
							pingSpeed=(int)(System.currentTimeMillis()-pingStart);
							repaint();
						}
						public void serverClosed(){
							serverUp=false;
							pinging=false;
							pingSpeed=(int)(System.currentTimeMillis()-pingStart);
							repaint();
							client.dispose();
						}
						public void disconnected(){
							pinging=false;
							pingSpeed=(int)(System.currentTimeMillis()-pingStart);
							repaint();
							client.dispose();
						}
						public void recivedInput(String msg){
							try{ pong=new Pong(msg);
							}catch(Exception exception){
								serverUp=false;
								pinging=false;
								pingSpeed=(int)(System.currentTimeMillis()-pingStart);
								repaint();
								if(client.isConnected())client.dispose();
							}
						}
						public void connectedToServer(){}
					});
					if(client.isConnected())client.send(PacketType.ping.getHexId());
				}catch(Exception exception){
					exception.printStackTrace();
					System.exit(1);
				}
			}
		}).start();
	}
	public boolean isSelected(){ return selected; }
	public String getIp(){ return ip; }
	public int getPort(){ return port; }
	private void repaint(){ serverList.getScrollPanel().setNeedsRepaint(); }
}