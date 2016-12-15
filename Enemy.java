package Entity;

import Audio.AudioHandler;
import TileMap.TileMap;

public class Enemy extends MapObject {
	
	protected int health;
	protected int maxHealth;
	protected boolean dead;
	protected int damage;
	
	protected boolean flinching;
	protected long flinchTimer;
	
	private int score = 1;
	
	public Enemy(TileMap tm) {
		super(tm);
		
		//load sfx
		AudioHandler.load("/SFX/bnb_enemy_flinch.mp3", "enemyflinch");
		AudioHandler.load("/SFX/bnb_enemy_death.mp3", "enemydeath");
	}
	
	public boolean isDead() { 
		return dead; 
		
		}
	
	public int getDamage() { return damage; }
	
	public int getScore() { return score;}
	
	
	
	public void hit(int damage) {
		if(dead || flinching) return;
		health -= damage;
		if(health < 0) health = 0;
		if(health == 0) dead = true;
		flinching = true;
		flinchTimer = System.nanoTime();
	}
	
	public void update() {}
	
}