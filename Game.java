import java.awt.Toolkit;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;

//This class defines the game engine code
public class Game extends JFrame
{
	Controller controller;
	View view;
	Model model;
	
	//Initialize the components of the game engine and itself
	public Game()
	{
		//allow the frame to gain focus and handle key press events
		this.setFocusable(true);
		model = new Model("map.json");
		controller = new Controller(model);
		view = new View(controller, model);
		
		//set up the menu bar and menu system to change between map editor and game view
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("View Menu");
		
		//specify the two radio buttons that will decide the view
		ButtonGroup radioGroup = new ButtonGroup();
		
			JRadioButtonMenuItem showGame = new JRadioButtonMenuItem("Show Game View");
			showGame.setSelected(true);
			showGame.setActionCommand("showGame");
			showGame.addActionListener(controller);
			
			JRadioButtonMenuItem showMapEditor = new JRadioButtonMenuItem("Show Map Editor View");
			showMapEditor.setSelected(false);
			showMapEditor.setActionCommand("showMapEditor");
			showMapEditor.addActionListener(controller);
			
		radioGroup.add(showGame);
		radioGroup.add(showMapEditor);
		
		menu.add(showGame);
		menu.add(showMapEditor);
		
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
		
		//give a reference of the view to the model
		model.setView(view);
		
		//activate event listeners
		this.addKeyListener(controller);
		
		//set JFrame attributes
		this.setTitle("Super Mario Goomba Murder Simulator");
		this.setSize(500, 700);
		this.getContentPane().add(view);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	//game loop code
	public void run()
	{
		while(true)
		{
			//update the controller with information about key presses or mouse clicks
			controller.update();
			//make the model attributes react to input and set variables appropriately
			model.update();
			//repaint the panel to reflect input changes
			view.repaint();
			Toolkit.getDefaultToolkit().sync(); // Updates screen (don't need this for Windows 10 > Creators Update

			
			//sleep for 40 milliseconds or run at 25 frames per second
			try{
				Thread.sleep(40);
			}catch(Exception e){
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	//primary entry point to create instance of game and run it
	public static void main(String[] args)
	{
		Game g = new Game();
		g.run();
	}
}
