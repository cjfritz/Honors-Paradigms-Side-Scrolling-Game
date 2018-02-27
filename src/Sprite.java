import java.awt.Graphics;

abstract class Sprite
{
	int x, y, w, h, prev_x, prev_y;
	
	Sprite()
	{
		x = 0;
		y = 0;
	}
	
	Sprite(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	//MVC methods
	abstract void update();
	abstract void draw(Graphics g, int scrollPos);
	
	//subclass identification methods
	abstract boolean isTube();
	abstract boolean isMario();
	abstract boolean isGoomba();
	abstract boolean isFireball();
	
	//collision handling methods
	abstract void handleTopCollision(Sprite s);
	abstract void handleBottomCollision(Sprite s);
	abstract void handleLeftCollision(Sprite s);
	abstract void handleRightCollision(Sprite s);
	
	
	boolean spriteClicked(int x, int y)
	{
		if((x >= this.x & x <= w + this.x) & (y >= this.y & y <= h + this.y))
			return true;
		else return false;
	}
	
	//marshal method used to store Sprite objects.  Used with the model's mashalling
	//method, where this Json object is returned to be stored in a list object.
	Json marshal(String type)
	{
		Json jsonOb = Json.newObject();
		jsonOb.add("type", type);
		jsonOb.add("x", x);
		jsonOb.add("y", y);
		return jsonOb;
	}
}