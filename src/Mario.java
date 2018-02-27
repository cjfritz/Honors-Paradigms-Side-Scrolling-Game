import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.Graphics;
import javax.imageio.ImageIO;

//class used to represent mario character
class Mario extends Sprite
{
	//static image array used to keep images of mario movements
	static BufferedImage[] mario_images = null;
	//vertical velocity of mario
	double vert_vel;
	//jump counter used to limit jump size
	int jump_counter = 0;
	//used to represent where mario will stand
	int ground = 500;
	//used to represent the next mario image when left or right arrow is pressed
	int nextMarioImage;
	//used to determine if mario if moving left or right
	boolean marioMovingRight;
	//used to disable collisions and not draw mario when in map editor mode
	boolean disableCollisions;
	//used to detect if mario was touched killed, and signal to end the game
	boolean killed;
	
	Mario()
	{
		super();
		//load in mario images
		try
		{
			mario_images = new BufferedImage[5];
			mario_images[0] = ImageIO.read(new File("mario1.png"));
			mario_images[1] = ImageIO.read(new File("mario2.png"));
			mario_images[2] = ImageIO.read(new File("mario3.png"));
			mario_images[3] = ImageIO.read(new File("mario4.png"));
			mario_images[4] = ImageIO.read(new File("mario5.png"));
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			System.exit(1);
		}
		
		w = mario_images[0].getWidth();
		h = mario_images[0].getHeight();
	}
	
	Mario(int x, int y)
	{
		super(x, y);
		//load in mario images
		try
		{
			mario_images = new BufferedImage[5];
			mario_images[0] = ImageIO.read(new File("mario1.png"));
			mario_images[1] = ImageIO.read(new File("mario2.png"));
			mario_images[2] = ImageIO.read(new File("mario3.png"));
			mario_images[3] = ImageIO.read(new File("mario4.png"));
			mario_images[4] = ImageIO.read(new File("mario5.png"));
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			System.exit(1);
		}
		
		w = mario_images[0].getWidth();
		h = mario_images[0].getHeight();
	}
	
	void update()
	{
		//first store the previous x and y values
		prev_x = x;
		prev_y = y;
		//operation used to apply gravity effect to mario by manipulating y coord
		vert_vel += 2.5;
		y += vert_vel;
		
		//keep mario from falling when he hits the ground
		if(y > ground)
		{
			vert_vel = 0.0;
			y = ground;
			jump_counter = 0;
		}
		//increment the jump counter while mario is flying up through the air
		if(y < ground)
		{
			jump_counter++;
		}
	}
	
	void draw(Graphics g, int scrollPos)
	{
		if(disableCollisions) return;
		//draw mario
		//if right key is pressed, draw mario with normal scale, otherwise reverse the scale and add width to x to set origin back to top left corner of image
		if(marioMovingRight)
			g.drawImage(getCurrentImage(), x - scrollPos, y, null);
		else
			g.drawImage(getCurrentImage(), x + w - scrollPos, y, -w, h, null);
	}
	
	//cycle through the mario images and reset the image counter to 0 when needed
	BufferedImage getCurrentImage()
	{
		if(nextMarioImage > 4) nextMarioImage = 0;
		return mario_images[nextMarioImage];
	}
	
	void setNextMarioImage()
	{
		nextMarioImage++;
	}
	
	//if mario has been in the air for less than 5 jump counts, accelerate him upward
	void jump()
	{
		if(jump_counter < 5)
			vert_vel -= 8.5;
	}
	
	void setMarioMovingRight(boolean movingRight)
	{
		if(movingRight) marioMovingRight = true;
		else marioMovingRight = false;
	}
	
	//collision handling methods
	void handleTopCollision(Sprite s)
	{
		if(disableCollisions) return;
		
		if(s.isTube())
		{
			//adjust mario height to height of the tube + mario's height
			y = s.y - h;
			//don's allow mario to be affected by gravity when touching a tube, but allow him to jump up
			if(vert_vel > 0) vert_vel = 0;
			//keep the jump counter at 0 while he's touching a tube
			jump_counter = 0;
		}
		else if(s.isGoomba())
		{
			//adjust mario height to height of the tube + mario's height
			y = s.y - h;
			//don't allow mario to be affected by gravity when touching a tube, but allow him to jump up
			if(vert_vel > 0) vert_vel = 0;
			//keep the jump counter at 0 while he's touching a tube
			jump_counter = 0;
			
			//delete the goomba since mario gave him a structurally superfluous new behind
			Goomba g = (Goomba) s;
			g.delete = true;
		}
	}
	
	void handleBottomCollision(Sprite s)
	{
		if(disableCollisions) return;
		
		if(s.isTube())
		{
			//stop acceleration and pop mario back out of the bottom of the tube to previous location
			vert_vel = 0;
			y = prev_y;
		}
		else if(s.isGoomba()) killed = true;
	}
	
	void handleLeftCollision(Sprite s)
	{
		if(disableCollisions) return;
		
		if(s.isTube())
		{
			//this snaps mario right before the left side of the tube
			x -= (x + w) - (s.x) - 1;
		}
		else if(s.isGoomba()) killed = true;
	}
	
	void handleRightCollision(Sprite s)
	{
		if(disableCollisions) return;
		
		if(s.isTube())
		{
			//this snaps mario right after the right side of the tube
			x += (s.x + s.w) - x + 1;
		}
		else if(s.isGoomba()) killed = true;
	}
	
	boolean isTube() {return false;}
	boolean isMario() {return true;}
	boolean isGoomba() {return false;}
	boolean isFireball() {return false;}
}
