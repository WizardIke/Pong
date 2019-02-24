// Clancy, Isaac, 16125296, Assignment 1, 159.103
/* A simple pong game with one paddle that can be controlled by the AI or a player and another paddle that is controlled by a player.*/ 

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

public class PongMain extends GameEngine {
	private Ball ball1;
	private Paddle leftPaddle, rightPaddle;
	Input input;
	SoundFX soundFX;
	String score1 = "0", score2 = "0";
	GameState currentGameState = GameState.Menu;
	boolean singlePlayer = false;
	boolean controlledBounce = false;
	int scoreToWin = 3;
	FireWorks fireWorks1, fireWorks2;
	
	//AI state
	double reactionTime = 0.0;
	double targetY = -1.0;
	double bounceX;
	
	enum GameState
	{
		Menu,
		Options,
		level1,
		gameOver,
		pauseMenu
	}
	public static void main(String[] args)
	{
		int refreshRate;
		try
		{
			refreshRate = PongMain.getRefreshRate();
		}
		catch(Exception e)
		{
			refreshRate = 60;
		}
		//constructs GameEngine and PongMain and starts game loop
		createGame(new PongMain(), refreshRate);
	}
	
	public PongMain() {
		//construct GameEngine with window client area 670 * 500 px
		super(670, 500);
		input = new Input();
		soundFX = new SoundFX(this);
		//shutters first time played
		playAudio(soundFX.beep, -10000.f);
		
		
		//get starting angle for ball in rad
		double angle = rand(6.283185307179586);
		while(angle > 0.25 * Math.PI && angle < 0.75 * Math.PI || angle > 1.25 * Math.PI && angle < 1.75 * Math.PI) {
			angle = rand(6.283185307179586);
		}
		ball1 = new Ball(670.0 * 0.5, 500.0 * 0.5, 150.0 * Math.cos(angle),
				150.0 * Math.sin(angle), 500.0 / 50.0, 6.7, 493.3, this, soundFX);
		
		leftPaddle = new Paddle(670.0 / 600.0, 500.0 * 3.0 / 8.0, 500.0 / 4.0, 670.0 / 150.0);
		
		rightPaddle = new Paddle(670.0 * 595.0 / 600.0, 500.0 * 3.0 / 8.0, 500.0 / 4.0, 670.0 / 150.0);
	}
	
	@Override
	public void init() {
		//not needed, but here if something needs adding
	}

	@Override
	public void update(double dt) {
		if(currentGameState == GameState.level1) {
			ball1.update(dt);
			if(controlledBounce) {
				if(ball1.posX + ball1.radius > 670.f * 595.f / 600.f && (ball1.posY + ball1.radius > rightPaddle.posY &&  //ball has hit right paddle
						ball1.posY - ball1.radius < rightPaddle.posY + rightPaddle.height)) {
					ball1.posX = 670.f * 595.f / 600.f - ball1.radius;
					double angle = ((ball1.posY - rightPaddle.posY) / rightPaddle.height * Math.PI * -0.25) + 0.125 * Math.PI;
					double temp = -ball1.velX;
					ball1.velX = (temp * Math.cos(angle) + ball1.velY * -Math.sin(angle)) * 1.05f;
					ball1.velY = (temp * Math.sin(angle) + ball1.velY * Math.cos(angle)) * 1.05f;
					playAudio(soundFX.beep);
				}
				else if (ball1.posX - ball1.radius < 670.f * 5.f / 600.f && (ball1.posY + ball1.radius > leftPaddle.posY &&  //ball has hit left paddle
						ball1.posY - ball1.radius < leftPaddle.posY + leftPaddle.height)) {
					ball1.posX = 670.f * 5.f / 600.f + ball1.radius;
					double angle = ((ball1.posY - leftPaddle.posY) / leftPaddle.height * Math.PI * 0.25) - 0.125 * Math.PI;
					double temp = -ball1.velX;
					ball1.velX = (temp * Math.cos(angle) + ball1.velY * -Math.sin(angle)) * 1.05f;
					ball1.velY = (temp * Math.sin(angle) + ball1.velY * Math.cos(angle)) * 1.05f;
					playAudio(soundFX.beep);
				}
			}
			else {
				if(ball1.posX + ball1.radius > 670.f * 595.f / 600.f && (ball1.posY + ball1.radius > rightPaddle.posY &&    //ball has hit right paddle
						ball1.posY - ball1.radius < rightPaddle.posY + rightPaddle.height)) {
					ball1.posX = 670.f * 595.f / 600.f - ball1.radius;
					ball1.velX = -ball1.velX * 1.1f;
					ball1.velY = 1.1f * ball1.velY;
					playAudio(soundFX.beep);
				}
				else if (ball1.posX - ball1.radius < 670.f * 5.f / 600.f && (ball1.posY + ball1.radius > leftPaddle.posY &&  //ball has hit left paddle
						ball1.posY - ball1.radius < leftPaddle.posY + leftPaddle.height)) {
					ball1.posX = 670.f * 5.f / 600.f + ball1.radius;
					ball1.velX = -ball1.velX * 1.1f;
					ball1.velY = 1.1f * ball1.velY;
					playAudio(soundFX.beep);
				}
			}
			if(ball1.posX + ball1.radius > 670.f) {  //left player scored
				playAudio(soundFX.score);
				score1 = String.valueOf(Integer.parseInt(score1) + 1);
				if(scoreToWin != 0 && (Integer.parseInt(score1) == scoreToWin)) {
					if(singlePlayer) {
							fireWorks1 = new FireWorks(mWidth * 0.25, mHeight * 0.4, mHeight * 0.2, mHeight * 0.4, this);
							fireWorks2 = new FireWorks(mWidth * 0.75 - mHeight * 0.2, mHeight * 0.4, mHeight * 0.2, mHeight * 0.4, this);
					}
					else {
						fireWorks1 = new FireWorks(mWidth * 0.25, mHeight * 0.4, mHeight * 0.2, mHeight * 0.4, this);
					}
					currentGameState = GameState.gameOver;
					return;
				}
				
				//new angle for ball in rad
				double angle = rand(6.283185307179586);
				while(angle > 0.25 * Math.PI && angle < 0.75 * Math.PI || angle > 1.25 * Math.PI && angle < 1.75 * Math.PI) {
					angle = rand(6.283185307179586);
				}
				//reset pos of ball
				ball1.posX = 670.f * 0.5f;
				ball1.posY = 500.f * 0.5f;
				ball1.velX = 150.0 * Math.cos(angle);
				ball1.velY = 150.0 * Math.sin(angle);
			}
			else if (ball1.posX - ball1.radius < 0.0f) {  //right player scored
				score2 = String.valueOf(Integer.parseInt(score2) + 1);
				playAudio(soundFX.score);
				if(scoreToWin != 0 && (Integer.parseInt(score2) == scoreToWin)) {
					if(singlePlayer) {
						//no fireworks
					}
					else {
						fireWorks2 = new FireWorks(mWidth * 0.75 - mHeight * 0.2, mHeight * 0.4, mHeight * 0.2, mHeight * 0.4, this);
					}
					currentGameState = GameState.gameOver;
					return;
				}
				
				//reset ball
				double angle = rand(6.283185307179586);
				while(angle > 0.25 * Math.PI && angle < 0.75 * Math.PI || angle > 1.25 * Math.PI && angle < 1.75 * Math.PI) {
					angle = rand(6.283185307179586);
				}
				ball1.posX = 670.f * 0.5f;
				ball1.posY = 500.f * 0.5f;
				ball1.velX = 150.0 * Math.cos(angle);
				ball1.velY = 150.0 * Math.sin(angle);
			}
			
			//move left paddle
			if(input.aDown){
				leftPaddle.posY -= dt * 200.0;
			}
			if(input.zDown) {
				leftPaddle.posY += dt * 200.0;
			}
			//stop paddle going off screen
			if(leftPaddle.posY < 6.7f) leftPaddle.posY = 6.7f;
			else if(leftPaddle.posY + leftPaddle.height > 493.3f) leftPaddle.posY = 493.3f - leftPaddle.height;
			
			if(singlePlayer) { //needs to take ball going in wrong direction into account //needs better accuracy at close range
				if(reactionTime > 0.0) { //do nothing
					reactionTime -= dt;
				}
				else {
					if(targetY < 0.0) { //no target //look for target
						double dtime; //time for ball to hit top or bottom of screen
						if(ball1.velY == 0.0) {
							dtime = 1000000.0;
						}
						else if(ball1.velY > 0) {
							dtime = (493.3 - ball1.radius - ball1.posY) / ball1.velY;
						}
						else {
							dtime = (6.7 + ball1.radius - ball1.posY) / ball1.velY;
						}
						bounceX = ball1.posX + ball1.velX * dtime;
						 if(bounceX > 670.0 * 595.0 / 600.0 - ball1.radius) {
							double inaccuracy = 1.0 / 400.0 * (670.0 * 595.0 / 600.0 - ball1.posX - ball1.radius);
							targetY = (670.0 * 595.0 / 600.0 - ball1.posX) / ball1.velX * ball1.velY + ball1.posY + (rand(2.0) - 1.0) * rightPaddle.height * 
									inaccuracy - rightPaddle.height * 0.5;
							if(targetY < 6.7) {
								targetY = 6.7;
							}
							else if(targetY > 493.3 - rightPaddle.height) {
								targetY = 493.3 - rightPaddle.height;
							}
							reactionTime = rand(0.2) + 0.1;
							if(targetY + rightPaddle.height * 0.3 > rightPaddle.posY && targetY < rightPaddle.height * 0.3 + rightPaddle.posY) {
								//if in about at about the right height don't constantly fidget
								targetY = -1.0;
							}
						}
						 else if(bounceX < 670.0 * 5.0 / 600.0 + ball1.radius) { //bounce in y-axis
							 bounceX = 670.0 * 5.0 / 600.0 - bounceX;
							if(bounceX > 670.0 * 595.0 / 600.0 - ball1.radius) {
								double inaccuracy = 1.0 / 600.0 / (670.0 * 595.0 / 600.0 - ball1.posX - ball1.radius);
								targetY = (ball1.posX - 670.0 * 5.0 / 600.0 + 670.0 - 6.7 * 2.0) / -ball1.velX * ball1.velY + ball1.posY + 
										(rand(2.0) - 1.0) * rightPaddle.height * inaccuracy - rightPaddle.height * 0.5;
								if(targetY < 6.7) {
									targetY = 6.7;
								}
								else if(targetY > 493.3 - rightPaddle.height) {
									targetY = 493.3 - rightPaddle.height;
								}
								reactionTime =  rand(0.24) + 0.12;
								if(targetY + rightPaddle.height * 0.6 > rightPaddle.posY && targetY < rightPaddle.height * 0.6 + rightPaddle.posY) {
									//if in about at about the right height don't constantly fidget
									targetY = -1.0;
								}
							}
						 }
						else {
							if(ball1.velY > 0.0) {
								dtime = (493.3 - 6.7) / ball1.velY;
							}
							else {
								dtime = (6.7 - 493.3) / ball1.velY;
							}
							double temp = bounceX;
							bounceX = bounceX + ball1.velX * dtime;
							if(bounceX > 670.0 * 595.0 / 600.0 - ball1.radius) {
								dtime = (670.0 * 595.0 / 600 - temp) / ball1.velX;
								if(ball1.velY > 0.0) {
									double inaccuracy = 1.0 / 600.0 / (ball1.posX - ball1.radius - 670.0 * 5.0 / 600.0 + 670.0 * 590.0 / 600);
									targetY = 493.3 - ball1.velY * dtime + (rand(2.0) - 1.0) * rightPaddle.height * inaccuracy - rightPaddle.height * 0.5;
								}
								else {
									double inaccuracy = 1.0 / 600.0 / (ball1.posX - ball1.radius - 670.0 * 5.0 / 600.0 + 670.0 * 590.0 / 600);
									targetY = -ball1.velY * dtime + (rand(2.0) - 1.0) * rightPaddle.height * inaccuracy - rightPaddle.height * 0.5;
								}
								if(targetY < 6.7) {
									targetY = 6.7;
								}
								else if(targetY > 493.3 - rightPaddle.height) {
									targetY = 493.3 - rightPaddle.height;
								}
								reactionTime =  rand(0.24) + 0.12;
								if(targetY + rightPaddle.height * 1.2 > rightPaddle.posY && targetY < rightPaddle.height * 1.2 + rightPaddle.posY) {
									//if in about at about the right height don't constantly fidget
									targetY = -1.0;
								}
							}
						}
					}
					else { //move
						if(rightPaddle.posY >= targetY) {
							if(rightPaddle.posY - targetY <= dt * 200.0) {
								rightPaddle.posY = targetY;
								targetY = -1.0;
							}
							else {
								rightPaddle.posY -= dt * 200.0;
							}
						}
						else {
							if(targetY - rightPaddle.posY <= dt * 200.0) {
								rightPaddle.posY = targetY;
								targetY = -1.0;
							}
							else {
								rightPaddle.posY += dt * 200.0;
							}
						}
					}
				}
			}
			else {
				if(input.upDown) {
					rightPaddle.posY -= dt * 200.0;
				}
				if(input.downDown) {
					rightPaddle.posY += dt * 200.0;
				}
				if(rightPaddle.posY < 6.7f) rightPaddle.posY = 6.7f;
				else if(rightPaddle.posY + rightPaddle.height > 493.3f) rightPaddle.posY = 493.3f - rightPaddle.height;
			}
		}
		else if(currentGameState == GameState.gameOver) {
			if(fireWorks1 != null) {
				fireWorks1.update(dt);
			}
			if(fireWorks2 != null) {
				fireWorks2.update(dt);
			}
		}
	}

	@Override
	public void paintComponent() {
		if(currentGameState == GameState.level1) {
			changeBackgroundColor(black);
			clearBackground(mWidth, mHeight);
			ball1.render();
			DottedLine.render(this);
			drawSolidRectangle(0.0, 0.0, mWidth, 6.7);
			drawSolidRectangle(0.0, (double)mHeight - 6.7, mWidth, 6.7);
			drawText((double)mWidth / 12.0, (double)mHeight / 10.0, score1);
			drawText((double)mWidth * 10.7 / 12.0, (double)mHeight / 10.0, score2);
			leftPaddle.render(this);
			rightPaddle.render(this);
		}
		else if(currentGameState == GameState.Menu) {
			changeBackgroundColor(black);
			clearBackground(mWidth, mHeight);
			changeColor(white);
			drawText((double)mWidth / 2.5, (double)mHeight / 2.3, "Play");
			drawText((double)mWidth / 2.5, (double)mHeight / 1.7, "Options");
		}
		else if(currentGameState == GameState.Options) {
			changeBackgroundColor(black);
			clearBackground(mWidth, mHeight);
			changeColor(white);
			if(singlePlayer) {
				drawText((double)mWidth / 5.0, (double)mHeight / 3.0, "Single Player   On");
			}
			else {
				drawText((double)mWidth / 5.0, (double)mHeight / 3.0, "Single Player  Off");
			}
			if(controlledBounce) {
				drawText((double)mWidth / 5.0, (double)mHeight / 3.0 + 60.0, "Controlled Bounce   On");
			}
			else {
				drawText((double)mWidth / 5.0, (double)mHeight / 3.0 + 60.0, "Controlled Bounce  Off");
			}
			if(scoreToWin != 0) {
				if(scoreToWin == 1) {
					drawText((double)mWidth / 5.0, (double)mHeight / 3.0 + 120.0, "Game mode   Instant death");
				}
				else {
					drawText((double)mWidth / 5.0, (double)mHeight / 3.0 + 120.0, "Game mode   First to " + scoreToWin);
				}
			}
			else {
				drawText((double)mWidth / 5.0, (double)mHeight / 3.0 + 120.0, "Game mode   Endless");
			}
			drawText((double)mWidth / 5.0, (double)mHeight / 3.0 + 180.0, "Back");
		}
		else if(currentGameState == GameState.gameOver) {
			changeBackgroundColor(black);
			clearBackground(mWidth, mHeight);
			ball1.render();
			DottedLine.render(this);
			drawSolidRectangle(0.0, 0.0, mWidth, 6.7);
			drawSolidRectangle(0.0, (double)mHeight - 6.7, mWidth, 6.7);
			drawText((double)mWidth / 12.0, (double)mHeight / 10.0, score1);
			drawText((double)mWidth * 10.7 / 12.0, (double)mHeight / 10.0, score2);
			leftPaddle.render(this);
			rightPaddle.render(this);
			
			
			drawText((double)mWidth / 2.7, (double)mHeight / 2.2, "Play again");
			drawText((double)mWidth * 0.364, (double)mHeight * 0.45 + 40.0, "Main menu");
			if(singlePlayer) {
				if(Integer.parseInt(score1) > Integer.parseInt(score2)) {
					changeColor(green);
					drawText((double)mWidth * 0.25, (double)mHeight / 3.0, "You Win", "Arial", 80);
				}
				else {
					changeColor(red);
					drawText((double)mWidth / 4.6, (double)mHeight / 3.0, "You Lose", "Arial", 80);
				}
			}
			else {
				if(Integer.parseInt(score1) > Integer.parseInt(score2)) {
					changeColor(blue);
					drawText((double)mWidth / 10.0, (double)mHeight / 5.0,"Left hand player wins", "Arial", 60);
				}
				else {
					changeColor(blue);
					drawText((double)mWidth / 20.0, (double)mHeight / 5.0, "Right hand player wins", "Arial", 60);
				}
			}
			if(fireWorks1 != null) {
				fireWorks1.render();
			}
			if(fireWorks2 != null) {
				fireWorks2.render();
			}
		}
		else if (currentGameState == GameState.pauseMenu) {
			changeColor(white);
			drawText((double)mWidth * 0.05, (double)mHeight / 5.0, "Resume");
			drawText((double)mWidth * 0.05, (double)mHeight / 5.0 + 40, "Restart");
			drawText((double)mWidth * 0.05, (double)mHeight / 5.0 + 80, "Main menu");
			drawText((double)mWidth * 0.05, (double)mHeight / 5.0 + 120, "Quit");
		}
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		if(currentGameState == GameState.level1) {
			switch(event.getKeyCode()) {
			case KeyEvent.VK_A :
				input.aDown = true;
				break;
			case KeyEvent.VK_Z :
				input.zDown = true;
				break;
			case KeyEvent.VK_UP :
				input.upDown = true;
				break;
			case KeyEvent.VK_DOWN :
				input.downDown = true;
				break;
			case KeyEvent.VK_TAB :
			case KeyEvent.VK_SPACE :
			case KeyEvent.VK_ESCAPE :
				currentGameState = GameState.pauseMenu;
				break;
			}
		}
		else if (currentGameState == GameState.pauseMenu) {
			switch(event.getKeyCode()) {
			case KeyEvent.VK_TAB :
			case KeyEvent.VK_SPACE :
			case KeyEvent.VK_ESCAPE :
				currentGameState = GameState.level1;
				break;
			}
		}
	}
	
	@Override
	public void keyReleased(KeyEvent event) {
		switch(event.getKeyCode()) {
		case KeyEvent.VK_A :
			input.aDown = false;
			break;
		case KeyEvent.VK_Z :
			input.zDown = false;
			break;
		case KeyEvent.VK_UP :
			input.upDown = false;
			break;
		case KeyEvent.VK_DOWN :
			input.downDown = false;
			break;
		}
	}
	
	public void mouseClicked(MouseEvent event) {
		if(currentGameState == GameState.Menu) {
			if(event.getX() > (double)mWidth / 2.5 && event.getX() < (double)mWidth / 2.5 + 80.0 &&
					event.getY() > (double)mHeight / 2.3 - 40 && event.getY() < (double)mHeight / 2.3) {
				currentGameState = GameState.level1;
			}
			else if(event.getX() > (double)mWidth / 2.5 && event.getX() < (double)mWidth / 2.5 + 150.0 &&
					event.getY() > (double)mHeight / 1.7 - 40 && event.getY() < (double)mHeight / 1.7) {
				currentGameState = GameState.Options;
			}
		}
		else if(currentGameState == GameState.Options) {
			if(event.getX() > (double)mWidth / 5.0 && event.getX() < (double)mWidth / 5.0 + 320.0 &&
					event.getY() > (double)mHeight / 3.0 - 40 && event.getY() < (double)mHeight / 3.0) {
				singlePlayer = !singlePlayer;
			}
			else if(event.getX() > (double)mWidth / 5.0 && event.getX() < (double)mWidth / 5.0 + 420.0 &&
					event.getY() > (double)mHeight / 3.0 + 20.0 && event.getY() < (double)mHeight / 3.0 + 60.0) {
				controlledBounce = !controlledBounce;
			}
			else if(event.getX() > (double)mWidth / 5.0 && event.getX() < (double)mWidth / 5.0 + 410.0 &&
					event.getY() > (double)mHeight / 3.0 + 80.0 && event.getY() < (double)mHeight / 3.0 + 120.0) {
				if(scoreToWin == 1) {
					scoreToWin = 3;
				}
				else if(scoreToWin == 3) {
					scoreToWin = 5;
				}
				else if(scoreToWin == 5) {
					scoreToWin = 0;
				}
				else {
					scoreToWin = 1;
				}
			}
			else if(event.getX() > (double)mWidth / 5.0 && event.getX() < (double)mWidth / 5.0 + 90.0 &&
					event.getY() > (double)mHeight / 3.0 + 140.0 && event.getY() < (double)mHeight / 3.0 + 180.0) {
				currentGameState = GameState.Menu;
			}
		}
		else if(currentGameState == GameState.gameOver) {
			if(event.getX() > (double)mWidth / 2.7 && event.getX() < (double)mWidth / 2.7 + 180.0 &&
					event.getY() > (double)mHeight / 2.2 - 40.0 && event.getY() < (double)mHeight / 2.2) {
				reset();
				fireWorks1 = null;
				fireWorks2 = null;
				currentGameState = GameState.level1;
			}
			else if(event.getX() > (double)mWidth * 0.364 && event.getX() < (double)mWidth * 0.364 + 190.0 &&
					event.getY() > (double)mHeight * 0.45 && event.getY() < (double)mHeight * 0.45 + 40) {
				reset();
				fireWorks1 = null;
				fireWorks2 = null;
				currentGameState = GameState.Menu;
			}
		}
		else if (currentGameState == GameState.pauseMenu) {
			if(event.getX() > (double)mWidth * 0.05 && event.getX() < (double)mWidth * 0.05 + 150.0 &&
					event.getY() > (double)mHeight * 0.2 - 40.0 && event.getY() < (double)mHeight * 0.2) {
				currentGameState = GameState.level1;
			}
			else if(event.getX() > (double)mWidth * 0.05 && event.getX() < (double)mWidth * 0.05 + 130.0 &&
					event.getY() > (double)mHeight * 0.2 && event.getY() < (double)mHeight * 0.2 + 40.0) {
				reset();
				currentGameState = GameState.level1;
			}
			else if(event.getX() > (double)mWidth * 0.05 && event.getX() < (double)mWidth * 0.05 + 200.0 &&
					event.getY() > (double)mHeight * 0.2 + 40.0 && event.getY() < (double)mHeight * 0.2 + 80.0) {
				reset();
				currentGameState = GameState.Menu;
			}
			else if(event.getX() > (double)mWidth * 0.05 && event.getX() < (double)mWidth * 0.05 + 90.0 &&
					event.getY() > (double)mHeight * 0.2 + 80.0 && event.getY() < (double)mHeight * 0.2 + 120.0) {
				mFrame.dispatchEvent(new WindowEvent(mFrame, WindowEvent.WINDOW_CLOSING));
			}
		}
	}
	
	private void reset() {
		score1 = "0";
		score2 = "0";
		leftPaddle.posY = 500.0 * 3.0 / 8.0;
		rightPaddle.posY = 500.0 * 3.0 / 8.0;
		double angle = rand(6.283185307179586);
		while(angle > 0.25 * Math.PI && angle < 0.75 * Math.PI || angle > 1.25 * Math.PI && angle < 1.75 * Math.PI) {
			angle = rand(6.283185307179586);
		}
		ball1.posX = 670.f * 0.5f;
		ball1.posY = 500.f * 0.5f;
		ball1.velX = 150.0 * Math.cos(angle);
		ball1.velY = 150.0 * Math.sin(angle);
	}
	
	public static int getRefreshRate() throws Exception  {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice[] gs = ge.getScreenDevices();

	    DisplayMode dm = gs[0].getDisplayMode();
	    int min = dm.getRefreshRate();
	      if (min == DisplayMode.REFRESH_RATE_UNKNOWN) {
	    	  min = 60;
	      }
	    for (int i = 0; i < gs.length; i++) {
	      dm = gs[i].getDisplayMode();

	       int refreshRate = dm.getRefreshRate();
	      if (refreshRate == DisplayMode.REFRESH_RATE_UNKNOWN) {
	    	  refreshRate = 60;
	      }
	      if(refreshRate < min) min = refreshRate;
	    }
	    return min;
	}
}
