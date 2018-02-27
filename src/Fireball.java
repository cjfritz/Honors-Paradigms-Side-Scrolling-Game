import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

class Fireball extends Sprite
{
	//keeps the fireball image
	static BufferedImage fireballImage = null;
	//keeps the vertical, horizontal velocity of ball, and vertMomentum slowly reduces upward mobility of ball, simulating momentum
	double vert_vel, horz_vel = 15.0, vertMomentum = -15.0;
	
	//y location of the ground
	int ground = 550;
	//used to indicate if the fireball needs to be deleted
	boolean delete = false;
	
	Fireball()
	{
		super();
		
		try
		{
			fireballImage = ImageIO.read(new File("fireball.png"));
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			System.exit(1);
		}
		
		w = fireballImage.getWidth();
		h = fireballImage.getHeight();
	}
	
	Fireball(int x, int y)
	{
		super(x, y);
		
		try
		{
			fireballImage = ImageIO.read(new File("fireball.png"));
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			System.exit(1);
		}
		
		w = fireballImage.getWidth();
		h = fireballImage.getHeight();
	}
	
	void update()
	{
		//first store the previous x and y values
		prev_x = x;
		prev_y = y;
		//operation used to apply gravity effect to mario by manipulating y coord
		vert_vel += 2.5;
		y += vert_vel;
		
		//apply horizontal position from velocity
		x += horz_vel;
		
		//keep mario from falling when he hits the ground
		if(y > ground)
		{
			y = ground;
			//allow vertMomentum to regain half the vertical velocity after hitting the ground, simulating conversvation of energy
			vert_vel = vertMomentum - (vert_vel / 2);
			
			//subtract from momentum so the fireball slowly loses vertical height
			vertMomentum += 1.0;
		}
		
		//if there is no more upward vertical momentum, set velocities to 0
		if(vertMomentum > 0){
			horz_vel = 0;
			vert_vel = 0;
		}
	}
	
	//draws the fireball
	void draw(Graphics g, int scrollPos)
	{
		g.drawImage(fireballImage, x - scrollPos, y, null);
	}
	
	//collision handling methods
	void handleTopCollision(Sprite s)
	{
		if(s.isTube())
		{
			//adjust fireball height to height of the tube + mario's height
			y = s.y - h - 1;
			//apply slightly greater reverse velocity upon hitting the top of a tube, like the ground
			vert_vel = vertMomentum - (vert_vel / 2);
			
			//reduce momentum so the fireball slowly loses vertical height
			vertMomentum += 1.0;
		}
		else if(s.isGoomba())
		{
			//delete the fireball
			delete = true;
		}
	}
	
	void handleBottomCollision(Sprite s)
	{
		if(s.isTube())
		{
			//stop acceleration and pop mario back out of the bottom of the tube to previous location
			vert_vel = 0;
			y = prev_y;
		}
		else if(s.isGoomba())
		{
			//delete the fireball
			delete = true;
		}
	}
	
	void handleLeftCollision(Sprite s)
	{
		if(s.isTube())
		{
			//set the x position to the left of the tube
			x -= (x + w) - s.x - 1;
			
			//decrease horz_vel, which is positive at this point, and invert it
			if(horz_vel > 0)
			{
				horz_vel -= 1.5;
				horz_vel *= -1;
			}
		}
		else if(s.isGoomba())
		{
			//delete the fireball
			delete = true;
		}
	}
	
	void handleRightCollision(Sprite s)
	{
		if(s.isTube())
		{
			//set x pos to right of tube
			x += (s.x + s.w) - x + 1;
			
			//decrease horzMomentum, which is negative at this point, and invert it
			
			if(horz_vel < 0)
			{
				horz_vel += 1.5;
				horz_vel *= -1;
			}
		}
		else if(s.isGoomba())
		{
			//delete the fireball
			delete = true;
		}
	}
	
	boolean isTube() {return false;}
	boolean isMario() {return false;}
	boolean isGoomba() {return false;}
	boolean isFireball() {return true;}
}