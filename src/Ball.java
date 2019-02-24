
public class Ball {
	double velX, velY;
	private double screenY;
	double posX, posY;
	double radius;
	private GameEngine engine;
	private SoundFX soundFX;
	private double  screenYStart;
	public Ball(double PosX, double PosY, double VelX, double VelY, double Radius, double screenYStart,
			double ScreenY, GameEngine Engine, SoundFX soundFX){
		this.posX = PosX;
		this.posY = PosY;
		this.radius = Radius;
		this.velX = VelX;
		this.velY = VelY;
		this.screenYStart = screenYStart;
		this.screenY = ScreenY;
		this.engine = Engine;
		this.soundFX = soundFX;
	}
	
	public void update(double framTime){
		posX += velX * framTime;
		posY += velY * framTime;
		if(posY + radius > screenY) {
			posY = screenY - radius;
			velY = -velY;
			engine.playAudio(soundFX.beep);
		}
		else if (posY - radius < screenYStart) {
			posY = screenYStart + radius;
			velY = -velY;
			engine.playAudio(soundFX.beep);
		}
	}
	
	public void render(){
		engine.changeColor(engine.white);
		engine.drawSolidCircle(posX, posY, radius);
	}
}
