import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

class Goomba extends Sprite
{
	//Image array used to hold images of goomba
	static BufferedImage[] goombaImages = null;
	//current image used for goomba states
	BufferedImage currentImage = null;
	//vertical velocity of goomba
	double vert_vel;
	//used to represent where mario will stand
	int ground = 480;
	//boolean used to determine if goomba touched a tube on the right and change his movement
	boolean touchedTubeOnRight;
	//boolean used to flip goomba image on the next frame
	boolean flipGoomba;
	//keeps track of the number of frames that have passed
	int frames;
	//this boolean keeps track of when goomba needs to be deleted
	boolean delete = false;
	
	Goomba()
	{
		super();
		
		try
		{
			goombaImages = new BufferedImage[2];
			goombaImages[0] = ImageIO.read(new File("gumba.png"));
			goombaImages[1] = ImageIO.read(new File("gumba_fire.png"));
			
			currentImage = goombaImages[0];
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			System.exit(1);
		}
		
		w = goombaImages[0].getWidth();
		h = goombaImages[0].getHeight();
	}
	
	Goomba(int x, int y)
	{
		super(x, y);
		
		try
		{
			goombaImages = new BufferedImage[2];
			goombaImages[0] = ImageIO.read(new File("gumba.png"));
			goombaImages[1] = ImageIO.read(new File("gumba_fire.png"));
			
			currentImage = goombaImages[0];
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			System.exit(1);
		}
		
		w = goombaImages[0].getWidth();
		h = goombaImages[0].getHeight();
	}
	
	//constructor used to unmarshall a Goomba
	Goomba(Json jsonOb)
	{
		x = (int) jsonOb.getLong("x");
		y = (int) jsonOb.getLong("y");
		
		try
		{
			goombaImages = new BufferedImage[2];
			goombaImages[0] = ImageIO.read(new File("gumba.png"));
			goombaImages[1] = ImageIO.read(new File("gumba_fire.png"));
			
			currentImage = goombaImages[0];
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			System.exit(1);
		}
		
		w = goombaImages[0].getWidth();
		h = goombaImages[0].getHeight();
	}
	
	void update()
	{
		//first store the previous x and y values
		prev_x = x;
		prev_y = y;
		//operation used to apply gravity effect to goomba by manipulating y coord
		vert_vel += 2.5;
		y += vert_vel;
		
		//keep goomba from falling when he hits the ground
		if(y > ground)
		{
			vert_vel = 0.0;
			y = ground;
		}
		
		//have goomba shuffle left when touchedTubeOnRight is true, shuffle right otherwise
		if(touchedTubeOnRight)
		{
			x -= 4;
		}
		else
		{
			x += 4;
		}
		
		frames++;
		
		//once goomba is on fire, flag for deletion after 75 frames
		if(currentImage == goombaImages[1])
		{
			if(frames == 75) delete = true;
		}
		
		if(frames > 100) frames = 0;
	}
	
	void draw(Graphics g, int scrollPos)
	{
		//make goomba move less rapidly by shifting pictures every 4 frames
		if(flipGoomba & frames%4 == 0)
		{
			g.drawImage(currentImage, x - scrollPos, y, null);
			flipGoomba = false;
		}
		else
		{
			g.drawImage(currentImage, x + w - scrollPos, y, -w, h, null);
			flipGoomba = true;
		}
	}
	
	//collision handling methods
	void handleTopCollision(Sprite s)
	{
		if(s.isTube())
		{
			//adjust mario height to height of the tube + mario's height
			y = s.y - h;
			//don't allow goomba to be affected by gravity when touching a tube
			if(vert_vel > 0) vert_vel = 0;
		}
		else if(s.isFireball())
		{
			//light him on fire and restart frames to count to goomba's death
			currentImage = goombaImages[1];
			frames = 0;
		}
	}
	
	void handleBottomCollision(Sprite s)
	{
		if(s.isTube())
		{
			//not worried about goombas hitting bottom of tube...yet
		}
		else if(s.isFireball())
		{
			//light him on fire and restart frames to count to goomba's death
			currentImage = goombaImages[1];
			frames = 0;
		}
	}
	
	void handleLeftCollision(Sprite s)
	{
		if(s.isTube())
		{
			touchedTubeOnRight = true;
		}
		else if(s.isFireball())
		{
			//light him on fire and restart frames to count to goomba's death
			currentImage = goombaImages[1];
			frames = 0;
		}
	}
	
	void handleRightCollision(Sprite s)
	{
		if(s.isTube())
		{
			touchedTubeOnRight = false;
		}
		else if(s.isFireball())
		{
			//light him on fire and restart frames to count to goomba's death
			currentImage = goombaImages[1];
			frames = 0;
		}
	}
	
	boolean isTube() {return false;}
	
	boolean isMario() {return false;}
	
	boolean isGoomba() {return true;}
	
	boolean isFireball() {return false;}
}