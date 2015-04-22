package wraithaven.conquest.client;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import wraith.library.MiscUtil.FadeListener;
import wraith.library.MiscUtil.FadeTimer;
import wraith.library.WindowUtil.ImageWindow;

public class SplashScreen extends ImageWindow{
	private SplashScreenListener listener;
	private String username;
	private String password;
	private int usernameCarret;
	private int passwordCarret;
	private boolean showCarret;
	public static final int TEXT_X_POSITION = LogInSplash.TEXT_BOX_X+3;
	public SplashScreen(BufferedImage image, SplashScreenListener listener){
		super(image);
		this.listener=listener;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public void fadeIn(int fadeTicks, int tickDelay){
		final FadeTimer fadeTimer = new FadeTimer(fadeTicks, 0, 0, tickDelay);
		fadeTimer.addListener(new FadeListener(){
			public void onFadeInTick(){ updateFadeLevel(fadeTimer.getFadeLevel()); }
			public void onComplete(){ listener.onFadedIn(); }
			public void onFadeOutTick(){}
			public void onFadeOutComplete(){}
			public void onFadeInComplete(){}
			public void onFadeStayTick(){}
			public void onFadeStayComplete(){}
		});
		fadeTimer.start();
	}
	public void fadeOut(int fadeTicks, int tickDelay){
		final FadeTimer fadeTimer = new FadeTimer(0, 0, fadeTicks, tickDelay);
		fadeTimer.addListener(new FadeListener(){
			public void onFadeOutTick(){ updateFadeLevel(fadeTimer.getFadeLevel()); }
			public void onComplete(){ listener.onFadedOut(); }
			public void onFadeInTick(){}
			public void onFadeOutComplete(){}
			public void onFadeInComplete(){}
			public void onFadeStayTick(){}
			public void onFadeStayComplete(){}
		});
		fadeTimer.start();
	}
	@Override protected JPanel createPanel(){
		return new JPanel(){
			@Override public void paintComponent(Graphics g1){
				Graphics2D g = (Graphics2D)g1;
				g.setColor(getBackground());
				g.clearRect(0, 0, getWidth(), getHeight());
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fade));
				g.drawImage(img, 0, 0, this);
				g.setColor(Color.black);
				int ver = centerTextVertically(g.getFontMetrics());
				if(username!=null)g.drawString(username, TEXT_X_POSITION, LogInSplash.TEXT_BOX_1_Y+ver);
				if(password!=null)g.drawString(password, TEXT_X_POSITION, LogInSplash.TEXT_BOX_2_Y+ver);
				g.dispose();
			}
		};
	}
	public void setUsername(String username, int carretPosition){
		this.username=username;
		usernameCarret=carretPosition;
	}
	public void setPassword(String password, int carretPosition){
		this.password=password;
		passwordCarret=carretPosition;
	}
	public JPanel getPanel(){ return panel; }
	private static int centerTextVertically(FontMetrics fm){ return fm.getAscent()+(LogInSplash.TEXT_BOX_HEIGHT-(fm.getAscent()+fm.getDescent()))/2; }
}