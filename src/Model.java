import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.JOptionPane;

import java.awt.event.MouseEvent;

//Comparator used with the collections framework to sort the ArrayList
class SpriteComparator implements Comparator<Sprite>
{
	public int compare(Sprite a, Sprite b)
	{
		if(a.x < b.x)
			return -1;
		else if(a.x > b.x)
			return 1;
		else return 0;
	}
	
	public boolean equals(Object obj)
	{
		return false;
	}
}

//This class holds the attributes of the picture's location and intended destination
class Model
{
	//variable holding Sprite objects, where savedSprites holds the saved sprites when s is pressed
	ArrayList<Sprite> sprites, savedSprites;
	//references to the view and controller
	View view;
	//reference to the controller
	Controller controller;
	//instance of mario
	Mario mario = new Mario(200, 200);
	
	//standard empty constructor
	Model()
	{
		savedSprites = new ArrayList<Sprite>();
		sprites = new ArrayList<Sprite>();
		sprites.add(mario);
	}
	//initialize sprites with t
	Model(Tube t)
	{
		savedSprites = new ArrayList<Sprite>();
		sprites = new ArrayList<Sprite>();
		sprites.add(mario);
		sprites.add(t);
	}
	
	Model(String filename)
	{
		startGame();
	}
	
	void unmarshall() 
	{
		//create a Json object
		Json jsonOb = Json.newObject();
		//load objects into the Json object
		jsonOb = Json.load("map.json");
			
		//allocate new memory for sprites
		sprites = new ArrayList<Sprite>();
		//add mario back to sprites
		sprites.add(mario);
		
		Json spriteList = jsonOb.get("sprites");
		//extract sprites from the JSON file and put in sprites arraylist
		//spriteList = jsonOb.get("sprites");
		
		for(int i=0; i < spriteList.size(); i++)
		{
			String s = spriteList.get(i).getString("type");
			if(s.equals("Tube")) 	    sprites.add(new Tube(spriteList.get(i)));
			else if(s.equals("Goomba")) sprites.add(new Goomba(spriteList.get(i)));
		}
		
	}
	
	//save sprites to a JSON file
	void marshal()
	{
		Json jsonOb = Json.newObject();
		Json spriteList = Json.newList();
		jsonOb.add("sprites", spriteList);
		
		for(int i=0; i < sprites.size(); i++)
		{
			Sprite s = sprites.get(i);
			if(s.isTube())
				spriteList.add(s.marshal("Tube"));
			else if(s.isGoomba())
				spriteList.add(s.marshal("Goomba"));
		}
		
		try
		{
			jsonOb.save("map.json");
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(view, "Map could not be saved, exiting");
			e.printStackTrace();
			System.exit(-1);
		}
		
		
		JOptionPane.showMessageDialog(view, "Map has been saved to map.json");
	}
	
	//used to start/restart the game by loading in a map (if applicable)
	void startGame()
	{	
		try
		{
			unmarshall();
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(view, "Error: map.json could not be loaded or does not exist.\nCreating new empty map.");
			sprites = new ArrayList<Sprite>();
			sprites.add(mario);
		}
		
		if(controller != null) controller.clearKeys();
	}
	
	//updates all sprites
	public void update()
	{
		for(Iterator<Sprite> it = sprites.iterator(); it.hasNext();)
		{
			Sprite s = (Sprite) it.next();
			
			if(s.isGoomba())
			{
				Goomba g = (Goomba) s;
				
				//if goomba has been marked for deletion, delete
				if(g.delete) it.remove();
				else g.update();
			}
			else if(s.isFireball())
			{
				Fireball f = (Fireball) s;
				
				//if a fireball is marked for deletion, delete
				if(f.delete) it.remove();
				//if the fireball has lost vertical momentum, delete
				else if(f.vertMomentum > 0) it.remove();
				else f.update();
			}
			else if(s.isMario())
			{
				//is the mario delete flag has been set
				if(mario.killed)
				{
					//reset killed so an infinite loop doesn't occur
					mario.killed = false;
					//reset mario's x pos to original pos
					mario.x = 200;
					//reset view relative to mario's x pos
					view.scrollPos = mario.x - 160;
					//if controller is not null, clear the keys to avoid sticking
					if(controller != null) controller.clearKeys();
					//restart the game by loading in from map.json and reinitializing sprites arraylist
					startGame();
				}
				else s.update();
			}
			else
			{
				s.update();
			}
		}
	}
	
	//check if a sprite is located where clicked
	void checkForSprite(int x, int y, MouseEvent e)
	{
		Iterator<Sprite> it = sprites.iterator();
		
		while(it.hasNext())
		{
			Sprite s = (Sprite) it.next();
			
			if(s.spriteClicked(x + view.scrollPos, y) & !s.isMario())
			{
				it.remove();
				return;
			}
		}
		//Now add sprites based on the type of click
		//if left click, spawn tube
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			Tube tube = new Tube(x + view.scrollPos, y);
			sprites.add(tube);
		}
		//if right mouse has been clicked, spawn goomba
		else
		{
			Goomba goomba = new Goomba(x + view.scrollPos, y);
			sprites.add(goomba);
		}
		//sort the sprites arraylist
		Collections.sort(sprites, new SpriteComparator());
	}
	
	boolean spritesCollide(Sprite s1, Sprite s2)
	{
		//don't care if tubes collide...yet
		if(s1.isTube() & s2.isTube()) return false;
		
		//check if right side of s2 < left side of s2
		if(s1.x + s1.w < s2.x) return false;
		//check if left side of s1 > right side of s2
		if(s1.x > s2.x + s2.w) return false;
		//check if bottom of s1 above top of s2
		if(s1.y + s1.h < s2.y) return false;
		//check if top of s1 is below bottom of s2
		if(s1.y > s2.y + s2.h) return false;
		
		//otherwise, s1 is colliding with s2 somehow
		return true;
	}
	//this method handles sprite-sprite collisions 
	void checkForSpriteCollisions()
	{
		//these for loops compare every sprite in view with every other sprite except itself
		for(int i = 0; i < sprites.size(); i++)
		{
			Sprite s1 = sprites.get(i);
			
			for(int j = 0; j < sprites.size(); j++)
			{
				//skips if comparing a sprite to itself for collisions
				if(i == j) continue;
				
				Sprite s2 = sprites.get(j);
				
				if(spritesCollide(s1, s2))
				{
					//check if s1's bottom was previously higher than s2's top
					if(s1.prev_y + s1.h <= s2.y) s1.handleTopCollision(s2);
					
					//check if s1's top was previously lower than bottom of s2
		 	  		else if(s1.prev_y >= s2.y + s2.h) s1.handleBottomCollision(s2);
					
					//check if s1's left side was previously to the left of s2's left side
					else if(s1.prev_x <= s2.x) s1.handleLeftCollision(s2);
					
					//check if s1's right side was previously to the right of s2's right side
					else if(s1.prev_x + s1.w >= s2.x + s2.w) s1.handleRightCollision(s2);
				}
			}
		}
	}
	
	void spawnFireball()
	{
		//create a fireball at mario's origin
		Fireball fb = new Fireball(mario.x, mario.y);
		//shoot the fireball in the opposite direction if mario is facing the left
		if(!mario.marioMovingRight)
			fb.horz_vel*= -1;
		//add fireball to arraylist of sprites
		sprites.add(fb);
	}
	
	//used to call the mario jump method through the model
	void marioJump()
	{
		mario.jump();
	}
	//set the view
	void setView(View v)
	{
		view = v;
	}
	//set the controller
	void setController(Controller c)
	{
		controller = c;
	}
}