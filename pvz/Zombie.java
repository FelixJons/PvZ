package pvz;
import java.util.*;
import java.net.*;

public class Zombie extends Sprite implements Runnable{
	protected int hp;
	protected int damage;
	protected int speed;
	protected int slowtime;
	protected boolean isAlive;
	protected boolean isEating;
	protected boolean isSlowed;
	protected Stage stage;
	protected ZombieVar zombievar;
	public Zombie(int xPos, int yPos,Stage stage){
		super(xPos, yPos,100, 100, "sprites/zombies/Zombie.gif","audio/zombie.wav");
		this.hp=10;
		this.damage=1;
		this.speed=50;
		this.isAlive=true;
		this.isSlowed=false;
		this.stage=stage;
		this.zombievar =new ZombieVar(damage, speed, hp, xPos, yPos, width, height, isAlive, isEating, isSlowed, imageLocation, audioLocation,"Zombie");
	}
	public Zombie(ZombieVar zombievar,Stage stage){
		super(zombievar.getXPos(),zombievar.getYPos(),zombievar.getWidth(),zombievar.getHeight(),zombievar.getImageLocation(),zombievar.getAudioLocation());
		this.hp=zombievar.getHP();
		this.damage=zombievar.getDamage();
		this.speed=zombievar.getSpeed();
		this.isAlive=zombievar.getIsAlive();
		this.isEating=zombievar.getIsEating();
		this.isSlowed=zombievar.getIsSlowed();
		this.zombievar=zombievar;
		this.stage=stage;
	}
	//USE THIS FOR OTHER ZOMBIES
	// public Zombie(int xPos,int yPos, int width,int height, String filename, int damage,int speed, int hp){
	// 	super(xPos, yPos,width, height, filename);
	// 	this.isAlive=true;
	// 	this.hp=hp;
	// 	this.damage=damage;
	// 	this.speed=speed;
	// }
	public int getDamage(){
		return this.damage;
	}
	public boolean getStatus(){
		return this.isAlive;
	}
	private void move(){
		if(this.isAlive){
			this.xPos-=1;
		}
	}
	//applies the damage from the zombie
	public void damaged(int damage){
		this.hp-=damage;
		this.zombievar.setXPos(this.xPos);
		System.out.println("Z HP: "+this.hp);
		if(this.hp<=0){	
			this.setStatus();
			System.out.println("Zombie dead");
		}
	}
	//sets the status to false then removes the zombie
	public void setStatus(){
		this.isAlive=false;
		this.zombievar.setIsAlive(this.isAlive);
		this.stage.clearDeadZombie(this);
	}
	//zombie eats the plant
	protected void eat(Plant plant){
		plant.setHP(this.damage);
	}
	//when zombie is hit by slow particle
	protected void slowZombie(){
		this.isSlowed=true;
		this.zombievar.setIsSlowed(this.isSlowed);
		System.out.println("Slowed");
	}
	//checks if zombie collide with a plant
	protected Plant checkColission(ArrayList<Plant> plantList){
		if(plantList.size()!=0){
			for(int i=0;i<plantList.size();i+=1){
				if(this.getRectangle().intersects(plantList.get(i).getRectangle())&&this.getStatus()){
					if(this.xPos>plantList.get(i).getXPos()+25){
						return plantList.get(i);
					}
				}
			}
		}return null;
	}
	public ZombieVar getZombieVar(){
		return this.zombievar;
	}
	@Override
	public void run(){
		while(this.isAlive){
			this.updateRectangle();
			if(this.xPos<0) 
				this.setStatus();
			if(this.isSlowed){
				if(this.checkColission(stage.getPlantList())!=null){
					this.isEating=true;
					this.zombievar.setIsEating(this.isEating);
					this.eat(this.checkColission(stage.getPlantList()));
					try{
						Thread.sleep(1000*2); 
					}catch(Exception e){}
				}else{
					this.isEating=false;
					this.zombievar.setIsEating(this.isEating);
					for(int i=0;i<3;i++){
						if(!this.isEating) 
							this.move();
						this.repaint();
						try{
							synchronized(this){
								while(suspendFlag) wait();
							}
						}catch (InterruptedException e){}
						try {
							Thread.sleep(speed*3);
						} catch (Exception e){}
					}
				}
				this.isSlowed=false;	
			}else{
				if(this.checkColission(stage.getPlantList())!=null){
					this.isEating=true;
					this.zombievar.setIsEating(this.isEating);
					this.eat(this.checkColission(stage.getPlantList()));
					try{
						Thread.sleep(1000);
					}catch(Exception e){}
				}else {
					this.isEating=false;
					this.zombievar.setIsEating(this.isEating);
				}
				if(!this.isEating) 
					this.move();
				this.repaint();
				try{
					synchronized(this){
						while(suspendFlag) wait();
					}
				}catch (InterruptedException e){}
				try {
					Thread.sleep(speed);
				} catch (Exception e){}
			}	
		}
	}
}