package Entity;

import TileMap.*;

import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import Audio.AudioHandler;

public class Ship extends MapObject {

	// player stuff
	// protected static Player player;

	// private int health;
	// private int maxHealth;

	private boolean dead;
	private boolean flinching;
	private long flinchTimer;

	// animations
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = { 5 };

	// animation actions
	private static final int IDLE = 0;

	public Ship(TileMap tm) {

		super(tm);

		width = 168; // 30 // update: 70	//222
		height = 130; // 30 // update: 70	//193
		cwidth = 30; // 20 // update: 50
		cheight = 101; // 20 // update 55

		facingRight = true;

		// health = maxHealth = 3;
		Player.health = Player.maxHealth = 3;

		// load sprites
		try {

			BufferedImage spritesheet = ImageIO.read(getClass()
					.getResourceAsStream("/Sprites/Player/bnb_ship_spritesheet.png"));

			sprites = new ArrayList<BufferedImage[]>();
			for (int i = 0; i < 1; i++) {

				BufferedImage[] bi = new BufferedImage[numFrames[i]];

				for (int j = 0; j < numFrames[i]; j++) {

					if (i != IDLE) {
						bi[j] = spritesheet.getSubimage(j * width, i * height,
								width, height);
					} else {
						bi[j] = spritesheet.getSubimage(j * width, i * height,
								width, height);
					}

				}

				sprites.add(bi);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		AudioHandler.load("/SFX/bnb_ship_flinch.mp3", "shipflinch");

		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(300);

	}

	public int getHealth() {
		return Player.health;
	}

	public int getMaxHealth() {
		return Player.maxHealth;
	}

	public void checkAttack(ArrayList<Enemy> enemies) {

		// loop through enemies
		for (int i = 0; i < enemies.size(); i++) {

			Enemy e = enemies.get(i);

			// check enemy collision
			if (intersects(e)) {
				hit(e.getDamage());
			}

		}
	}

	public void hit(int damage) {
		if (flinching)
			return;
		AudioHandler.play("shipflinch");
		Player.health -= damage;
		if (Player.health < 0)
			Player.health = 0;
		if (Player.health == 0) {
			dead = true;
		}
		flinching = true;
		flinchTimer = System.nanoTime();
	}

	public void update() {

		// update position
		checkTileMapCollision();
		setPosition(xtemp, ytemp);

		// check done flinching
		if (flinching) {
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
			if (elapsed > 2000) {
				flinching = false;
			}
		}

		// set animation
		if (currentAction != IDLE) {
			currentAction = IDLE;
			animation.setFrames(sprites.get(IDLE));
			animation.setDelay(0); // default: 200
			width = 168;
		}

		animation.update();

	}

	public void draw(Graphics2D g) {

		setMapPosition();

		// draw player
		if (flinching) {
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
			if (elapsed / 100 % 2 == 0) {
				return;
			}
		}

		super.draw(g);

	}

}
