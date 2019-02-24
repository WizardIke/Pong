
public class Paddle {
	double posX, posY;
	double height, width;
	public Paddle(double posX, double posY, double height, double width) {
		this.posX = posX;
		this.posY = posY;
		this.height = height;
		this.width = width;
	}
	
	void render(GameEngine engine) {
		engine.drawSolidRectangle(posX, posY, width, height);
	}
}
