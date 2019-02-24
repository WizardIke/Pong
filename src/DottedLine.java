
public class DottedLine {
	public static void render(GameEngine engine)
	{
		double y = 0;
		double x = (double)engine.mWidth / 2.0 - (double)engine.mWidth / 200.0;
		for(int i = 0; i < 15; ++i)
		{
			engine.drawSolidRectangle(x, y, (double)engine.mWidth / 100.0, (double)engine.mHeight / 30.0);
			y += (double)engine.mHeight / 15.f;
		}
	}
}
