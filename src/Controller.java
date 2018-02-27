import java.awt.event.MouseListener;

import javax.swing.JOptionPane;

import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

//This class is responsible for updating the model class's code when input events occur, like mouse or key presses
class Controller implements MouseListener, KeyListener, ActionListener
{
	//declare instances of the view panel and model (turtle)
	View view;
	Model model;
	
	//declare boolean variables used to keep track when left, right, up, or down arrow keys are pressed
	boolean keyLeft, keyRight, keySpace, keyCtrl;
	//keeps track of spaces to make sure they are registered even if a jump occurs before or after update()
	int spaceQueue;
	//determines what view mode is selected and disables map editing
	boolean mapEditorEnabled;
	
	//construct the controller using model instance
	Controller(Model m)
	{
		model = m;
	}
	
	//set the view of the model
	void setView(View v)
	{
		view = v;
	}
	
	//handle mouse press events
	public void mousePressed(MouseEvent e)
	{
		if(mapEditorEnabled)
		{
			//check if mouse co-ords are within any of the model's sprites
			model.checkForSprite(e.getX(), e.getY(), e);
		}
	}
	
	
	//"implement" remaining functions to make code functional
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	
	//handle key presses and allow for simultaneous actions
	public void keyPressed(KeyEvent e)
	{
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_RIGHT: keyRight = true; break;
			case KeyEvent.VK_LEFT: keyLeft = true; break;
			case KeyEvent.VK_SPACE: keySpace = true; spaceQueue++; break;
			case KeyEvent.VK_CONTROL: keyCtrl = true; break;
		}
		
		if(mapEditorEnabled)
		{
			//is s is pressed, save the tubes on the screen to a JSON file
			if(e.getKeyCode() == KeyEvent.VK_S)
			{
				model.marshal();
			}
			//if l is pressed, load the tubes from a JSON file
			if(e.getKeyCode() == KeyEvent.VK_L)
			{
				//unmarshall the tubes into model
				model.unmarshall();
			}
		}
	}
	//handle action events, like when the menu bar radio buttons change
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "showGame")
		{
			mapEditorEnabled = false;
			view.scrollPos = 0;
			model.mario.x = 200;
			clearKeys();
			model.startGame();
			
		}
		else if(e.getActionCommand() == "showMapEditor")
		{
			JOptionPane.showMessageDialog(view, "Map Editor View Usage:\n"
					+ "1)Use arrow keys to move about.\n"
					+ "2)Left click spawns or deletes tubes.\n"
					+ "3)Right click spawns or deletes goombas.\n"
					+ "Choose game view to start a new game.");
			mapEditorEnabled = true;
		}
		
	}
	
	//handle key releases and allow for simultaneous actions
	public void keyReleased(KeyEvent e)
	{
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_RIGHT: keyRight = false; break;
			case KeyEvent.VK_LEFT: keyLeft = false; break;
			case KeyEvent.VK_SPACE: keySpace = false; break;
			case KeyEvent.VK_CONTROL: keyCtrl = false; break;
		}
	}
	
	//used to clear the keys after a new game is started to avoid sticking
	void clearKeys()
	{
		keyRight = false;
		keyLeft = false;
		keySpace = false;
		keyCtrl = false;
	}
	
	//update model destinations by incrementing 4 pixels every cycle
	void update()
	{
		//disable collisions and don't draw mario when map editing
		if(mapEditorEnabled)
			model.mario.disableCollisions = true;
		else model.mario.disableCollisions = false;
		
		if(keyRight)
		{
			//indicate to mario class that he is moving right
			model.mario.setMarioMovingRight(true);
			//cycle to the next mario image
			model.mario.setNextMarioImage();
			//move mario forward 4 pixels
			model.mario.x += 4;
			//scroll to the right
			view.scrollPos = model.mario.x - 160;
		}
		if(keyLeft)
		{
			//indicate to mario class that he is moving right
			model.mario.setMarioMovingRight(false);
			//cycle to the next mario image
			model.mario.setNextMarioImage();
			//move mario backward 4 pixels
			model.mario.x -= 4;
			//scroll to the left
			view.scrollPos = model.mario.x - 160;
		}
		if(keyCtrl)
		{
			model.spawnFireball();
		}
		//jump when space is pressed
		if(keySpace | spaceQueue > 0)
		{
			model.marioJump();
		}
		
		//check for mario-tube collisions
		model.checkForSpriteCollisions();
		//reset the spaceQueue, since at this point all jumps have been processed
		spaceQueue = 0;
	}
	//setter for the model
	void setModel(Model m)
	{
		model = m;
	}
	
	//"implement" the other method to make the code functional
	public void keyTyped(KeyEvent e){}
}
