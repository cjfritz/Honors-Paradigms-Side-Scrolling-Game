import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;

import java.util.ArrayList;
import java.util.Iterator;

//This class provides a JPanel container where all the fun takes place
class View extends JPanel
{
	//model instance
	Model model;
	//used to apply to tube or other background objects
	int scrollPos = 0;

	//initialize the variables and add an action listener to the mouse
	//this avoids issue where objects are placed with JFrame bar offset
	View(Controller c, Model m)
	{
		c.setView(this);
		this.addMouseListener(c);
		
		model = m;
	}
	//update the panel with color and the image
	public void paintComponent(Graphics g)
	{
		g.setColor(new Color(128, 255, 255));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		g.setColor(Color.gray);
		g.drawLine(0, 596, 2000, 596);
		
		//just run through all sprites, don't worry about screen view
		for(Iterator<Sprite> it = model.sprites.iterator(); it.hasNext();)
		{
			Sprite s = it.next();
			s.draw(g, scrollPos);
		}
		
		//loop through sprites and determine which ones are drawn
		/*for(int i = findFirstSpriteOnScreen(); i < model.sprites.size(); i++)
		{
			Sprite s = model.sprites.get(i);
			if(spriteRightOfScreen(s)) break;
			//draw whatever sprite s is
			System.out.println("drawing: " + s.x);
			s.draw(g, scrollPos);
		}
		
		for(int i = 0; i < model.sprites.size(); i++)
		{
			Sprite s = model.sprites.get(i);
			if(s.isMario())
			{
				s.draw(g, scrollPos);
				break;
			}
		}*/
	}
	
	//uses binary search to return the index of the first tube found on screen
	int findFirstSpriteOnScreen()
	{
		ArrayList<Sprite> sprites = model.sprites;
		int start = 0;
		int end = sprites.size()-1;
		
		while(true)
		{
			//get middle sprite based on start and end
			int mid = (start + end) / 2;
			//if start == mid, the first sprite on the left is found
			if(start == mid)
			{
				//System.out.println("First sprite on screen is: " + sprites.get(start).x);
				//System.out.println("First sprite on screen is Mario?:" + sprites.get(start).isMario());
				return start;
			}
				
			//make a new sprite reference equal to the middle sprite
			Sprite s = sprites.get(mid);
			
			//if scrollPos is 100 pixels greater than the sprite pos, start = mid
			if(s.x - scrollPos < -100)
				start = mid;
			else end = mid;
		}
	}
	//determines if a tube is right of the screen by summing its position
	boolean spriteRightOfScreen(Sprite s)
	{
		if(s.isMario()) return false;
		
		if(s.x > this.getWidth() + scrollPos) return true;
		else return false;
	}
	//setter for model
	void setModel(Model m)
	{
		model = m;
	}
}
