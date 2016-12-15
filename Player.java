package Entity;

import TileMap.*;

import java.util.ArrayList;
import Audio.AudioHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends MapObject {

	// player stuff

	public static int health;
	public static int maxHealth;
	// private int shoot;
	// private int maxShoot;
	private int knifeEnergy; // knife
	private int maxknifeEnergy; // knife
	private boolean dead;
	private boolean flinching;
	private long flinchTimer;
	private boolean doubleJump;
	private boolean alreadyDoubleJump;
	private double doubleJumpStart;

	// shooting
	private boolean shooting;
	// private int shootCost;
	private int shootDamage;
	private ArrayList<Bullet> bullets;

	// score
	private long score;

	// knifing
	private boolean knifing;
	private int knifingTick;
	private int knifingDamage;
	private int knifeRange;
	private int knifingEnergyCost;

	// doublejump
	private boolean doublejump;

	// animations
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = { 1, 2, 1, 1, 1 };

	// animation actions
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 3;
	// private static final int DOUBLEJUMPING = 4;
	private static final int SHOOTING = 4;
	private static final int KNIFING = 5;
	private static final int DEAD = 86;

	public Player(TileMap tm) {

		super(tm);

		width = 45; // 30 // update: 70
		height = 51; // 30 // update: 70
		cwidth = 45; // 20 // update: 50
		cheight = 49; // 20 // update 55 //update 51

		moveSpeed = 3; // 0.4
		maxSpeed = 5; // 1.6
		stopSpeed = 1.6; // 0.5
		fallSpeed = 0.3; // 0.25
		maxFallSpeed = 9.0; // 4.0
		jumpStart = -7.8; // -4.8
		stopJumpSpeed = 0.1; // default 0.1
		doubleJumpStart = -9; // -4

		facingRight = true;

		health = maxHealth = 3;
		knifeEnergy = maxknifeEnergy = 3; // 500

		knifingEnergyCost = 1; // 100

		// score
		score = 0;

		// KNIFE ENERGY REGEN

		shootDamage = 1;

		bullets = new ArrayList<Bullet>();

		knifingDamage = 1;
		knifeRange = 100;

		// load sprites
		try {

			BufferedImage spritesheet = ImageIO.read(getClass()
					.getResourceAsStream(
							"/Sprites/Player/bnb_player_spritesheet.png"));

			sprites = new ArrayList<BufferedImage[]>();
			for (int i = 0; i < 5; i++) {

				BufferedImage[] bi = new BufferedImage[numFrames[i]];

				for (int j = 0; j < numFrames[i]; j++) {

					if (i != KNIFING) {
						bi[j] = spritesheet.getSubimage(j * width, i * height,
								width, height);
					} else {
						bi[j] = spritesheet.getSubimage(j * width * 2, i
								* height, width * 2, height);
					}

				}

				sprites.add(bi);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// load sfx
//		AudioHandler.load("/SFX/bnb_player_flinch.mp3", "playerflinch");
		AudioHandler.load("/SFX/playerflinch.mp3", "playerflinch");
		AudioHandler.load("/SFX/bnb_player_jump.mp3", "playerjump");
		AudioHandler.load("/SFX/bnb_player_shoot.mp3", "playershoot");

		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(150);

	}

	public int getHealth() {
		return health;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void Death() {
		dead = true;
		moveSpeed = 0;
		dx = 0;

	}

	// public int getShoot() {
	// return shoot;
	// }

	// public int getMaxShoot() {
	// return maxShoot;
	// }

	public int getKnifeEnergy() {
		return knifeEnergy;
	}

	public int getMaxKnifeEnergy() {
		return maxknifeEnergy;
	}

	public void setShooting() {
		shooting = true;
		AudioHandler.play("playershoot");

	}

	public void setKnifing() {
		// if(knockback) return;
		if (!knifing) {
			knifing = true;
			// JukeBox.play("playercharge");
			knifingTick = 0;
		}
	}

	public void setJumping(boolean b) {
		// if(knockback) return;
		if (b && !jumping && falling && !alreadyDoubleJump) {
			doubleJump = true;
			AudioHandler.play("playerjump");
		}
		jumping = b;

	}

	public void checkAttack(ArrayList<Enemy> enemies) {

		// loop through enemies
		for (int i = 0; i < enemies.size(); i++) {

			Enemy e = enemies.get(i);

			// knife attack
			if (knifing) {
				if (facingRight) {
					if (e.getx() > x && e.getx() < x + knifeRange
							&& e.gety() > y - height / 2
							&& e.gety() < y + height / 2) {
						e.hit(knifingDamage);
					}
				} else {
					if (e.getx() < x && e.getx() > x - knifeRange
							&& e.gety() > y - height / 2
							&& e.gety() < y + height / 2) {
						e.hit(knifingDamage);
					}
				}
			}

			// fireballs
			for (int j = 0; j < bullets.size(); j++) {
				if (bullets.get(j).intersects(e)) {
					e.hit(shootDamage);
					bullets.get(j).setHit();
					break;
				}
			}

			// check enemy collision
			if (intersects(e)) {
				hit(e.getDamage());
			}

		}
	}

	public long getScore() {
		return score;
	}

	public void incrementScore(long l) {
		score += l;
	}

	public void hit(int damage) {
		if (flinching)
			return;
		AudioHandler.play("playerflinch");
		health -= damage;
		if (health < 0)
			health = 0;
		if (health == 0) {
			// dead = true;
			// moveSpeed = 0;
			Death();
		}
		flinching = true;
		flinchTimer = System.nanoTime();
	}

	private void getNextPosition() {

		// movement
		if (left) {
			dx -= moveSpeed;
			if (dx < -maxSpeed) {
				dx = -maxSpeed;
			}
		} else if (right) {
			dx += moveSpeed;
			if (dx > maxSpeed) {
				dx = maxSpeed;
			}
		} else {
			if (dx > 0) {
				dx -= stopSpeed;
				if (dx < 0) {
					dx = 0;
				}
			} else if (dx < 0) {
				dx += stopSpeed;
				if (dx > 0) {
					dx = 0;
				}
			}
		}

		// stops shooting while knifing
		if (currentAction == KNIFING) {
			shooting = false;
		}

		// cannot move while attacking, except in air
		// if ((currentAction == KNIFING || currentAction == SHOOTING)
		// && !(jumping || falling)) {
		// dx = 0;
		// }

		// knifing
		if (knifeEnergy > knifingEnergyCost) {
			if (knifing) {
				knifingTick--;
				if (facingRight)
					dx = moveSpeed * (2 - knifingTick * 0.07); /* 0.07 */
				else
					dx = -moveSpeed * (2 - knifingTick * 0.07); /* 0.07 */
			} else {
				knifing = false;
			}
		}

		// jumping
		if (jumping && !falling) {
			AudioHandler.play("playerjump");

			dy = jumpStart;
			falling = true;
		}

		if (doubleJump) {
			dy = doubleJumpStart;
			alreadyDoubleJump = true;
			doubleJump = false;

		}

		if (!falling)
			alreadyDoubleJump = false;

		// falling
		if (falling) {

			if (dy > 0 && doublejump)
				dy += fallSpeed * 0.1;
			else
				dy += fallSpeed;

			if (dy > 0)
				jumping = false;
			if (dy < 0 && !jumping)
				dy += stopJumpSpeed;

			if (dy > maxFallSpeed)
				dy = maxFallSpeed;

		}

	}

	public void update() {

		// update position
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);

		// check attack has stopped
		if (currentAction == KNIFING) {
			if (animation.hasPlayedOnce())
				knifing = false;
		}
		if (currentAction == SHOOTING) {
			if (animation.hasPlayedOnce())
				shooting = false;

		}

		// shoot attack
		// shoot += 1;
		// if(shoot > maxShoot) shoot = maxShoot;
		if (shooting && currentAction != SHOOTING) {
			// if(shoot > shootCost) {
			// shoot -= shootCost;
			Bullet fb = new Bullet(tileMap, facingRight);
			fb.setPosition(x, y);
			bullets.add(fb);
			// }
		}

		// knife energy
		// int delay = 5000; // delay for 5 sec.
		// int period = 5000; // repeat every sec.
		//
		// Timer timer = new Timer();
		// timer.scheduleAtFixedRate(new TimerTask()
		// {
		// public void run()
		// {
		// KNIFING
		// if (knifeEnergy >= 1) {
		// knifeEnergy -= knifingEnergyCost;
		// }
		//
		// }
		// }, delay, period);
		//
		// knifeEnergy ++;
		// if(knifeEnergy > maxknifeEnergy) knifeEnergy = maxknifeEnergy;
		// if(knifeEnergy < knifingEnergyCost) {
		//
		// knifing = false;
		// }
		// if(knifing && currentAction != KNIFING) {
		// if(knifeEnergy >= knifingEnergyCost) {
		// knifeEnergy -= knifingEnergyCost;
		// }
		// }

		// update bullets
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).update();
			if (bullets.get(i).shouldRemove()) {
				bullets.remove(i);
				i--;
			}
		}

		// check done flinching
		if (flinching) {
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
			if (elapsed > 1000) {
				flinching = false;
			}
		}

		// set animation

		if (shooting) {
			if (currentAction != SHOOTING) {
				currentAction = SHOOTING;
				animation.setFrames(sprites.get(SHOOTING));
				animation.setDelay(40); // default: 40
				width = 45;
			}
		} else if (dy > 0) {
			if (doublejump) {
				// if (currentAction != DOUBLEJUMPING) {
				// currentAction = DOUBLEJUMPING;
				// animation.setFrames(sprites.get(DOUBLEJUMPING));
				// animation.setDelay(100); //default 100
				// width = 30;
				// }
			} else if (currentAction != FALLING) {
				currentAction = FALLING;
				animation.setFrames(sprites.get(FALLING));
				animation.setDelay(100); // default: 100
				width = 45;
			}
		} else if (dy < 0) {
			if (currentAction != JUMPING) {

				currentAction = JUMPING;
				animation.setFrames(sprites.get(JUMPING));
				animation.setDelay(-1); // default: -1
				width = 45;
			}
		} else if (left || right) {
			if (currentAction != WALKING) {
				currentAction = WALKING;
				animation.setFrames(sprites.get(WALKING));
				animation.setDelay(100); // default: 40
				width = 45;
			}
		} else {
			if (currentAction != IDLE) {
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(150); // default: 200
				width = 45;
			}
		}

		animation.update();

		// set direction
		if (currentAction != KNIFING && currentAction != SHOOTING) {
			if (right)
				facingRight = true;
			if (left)
				facingRight = false;
		}

	}

	public void draw(Graphics2D g) {

		setMapPosition();

		// draw bullets
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).draw(g);
		}

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
