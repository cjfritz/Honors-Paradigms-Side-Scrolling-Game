import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.Graphics;
import javax.imageio.ImageIO;

//Class used to define a tube object
class Tube extends Sprite
{
	//add static tube image
	static BufferedImage tube_image = null;
	
	//default constructor
	Tube()
	{
		super();
		
		try{
			tube_image = ImageIO.read(new File("tube.png"));
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			System.exit(1);
		}
		
		w = tube_image.getWidth();
		h = tube_image.getHeight();
	}
	//set location of tube object and lazy load tube image
	Tube(int x, int y)
	{
		super(x, y);
		
		try{
			tube_image = ImageIO.read(new File("tube.png"));
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			System.exit(1);
		}
		
		w = tube_image.getWidth();
		h = tube_image.getHeight();
	}
	//unmarshalling constructor to unpack a tube from a JSON file
	//used in conjunction with the model's unmarshalling constructor
	Tube(Json jsonOb)
	{
		x = (int) jsonOb.getLong("x");
		y = (int) jsonOb.getLong("y");
		
		try{
			tube_image = ImageIO.read(new File("tube.png"));
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			System.exit(1);
		}
		
		w = tube_image.getWidth();
		h = tube_image.getHeight();
	}
	
	void update()
	{
		//don't need to changing anything about a tube yet
	}
	
	//draws a tube
	void draw(Graphics g, int scrollPos)
	{
		g.drawImage(tube_image, x - scrollPos, y, null);
	}
	
	//collision handling methods
	void handleTopCollision(Sprite s)
	{
		
	}
	
	void handleBottomCollision(Sprite s)
	{
		
	}
	
	void handleLeftCollision(Sprite s)
	{
		
	}
	
	void handleRightCollision(Sprite s)
	{
		
	}
	
	boolean isTube() {return true;}
	boolean isMario() {return false;}
	boolean isGoomba() {return false;}
	boolean isFireball() {return false;}
}