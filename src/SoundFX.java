
public class SoundFX {
	GameEngine engine;
	GameEngine.AudioClip beep;
	GameEngine.AudioClip score;
	
	public SoundFX(GameEngine engine) {
		this.engine = engine;
		beep = engine.loadAudio("../data/beep.wav");
		score = engine.loadAudio("../data/score.wav");
	}
}
