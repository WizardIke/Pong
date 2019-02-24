import java.awt.Color;

public class FireWorks {
	double posX, posY, lengthX, lengthY;
	private GameEngine engine;
	private java.util.Random randomGen = new java.util.Random();
	//Color color;
	private double time = 0.0;
	public FireWorks(double posX, double posY, double lengthX, double lengthY, GameEngine engine) {
		this.posX = posX;
		this.posY = posY;
		this.lengthX = lengthX;
		this.lengthY = lengthY;
		this.engine = engine;
	}
	public void update(double frameTime) {
		time += frameTime;
		if(time > 2.0) {
			time = 0.0;
		}
	}
	public void render() {
		if(time < 1.0){
			engine.drawSolidRectangle(posX + lengthX / 2.0 - 2.5, posY + lengthY - time * lengthY / 2.0, 5.0, 20.0);
		}
		else {
			setRandomColor();
			engine.drawSolidRectangle(posX + lengthX / 2.0 - 1.25, posY + lengthY - time * lengthY / 2.0, 2.5, 10.0);
			setRandomColor();
			engine.drawSolidRectangle(posX + lengthX / 2.0 - 1.25, posY + time * lengthY / 2.0, 2.5, 10.0);
			setRandomColor();
			engine.drawSolidRectangle(posX + lengthX / 2.0 - 5.0 - time * lengthX + lengthX, posY + lengthY * 0.5, 10.0, 2.5);
			setRandomColor();
			engine.drawSolidRectangle(posX + lengthX / 2.0 - 5.0 + time * lengthX - lengthX, posY + lengthY * 0.5, 10.0, 2.5);
		}
	}
	
	private void setRandomColor() {
		switch(randomGen.nextInt(7)) {
		case 0 :
			engine.changeColor(engine.blue);
			break;
		case 1 :
			engine.changeColor(engine.green);
			break;
		case 2 :
			engine.changeColor(engine.orange);
			break;
		case 3 :
			engine.changeColor(engine.pink);
			break;
		case 4 :
			engine.changeColor(engine.purple);
			break;
		case 5 :
			engine.changeColor(engine.red);
			break;
		case 6 :
			engine.changeColor(engine.yellow);
			break;
		}
	}
}
