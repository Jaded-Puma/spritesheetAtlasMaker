package minty.io;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class CanvasImage  extends JPanel implements ActionListener {
	// == attributes ==
	private BufferedImage image = null;
	private int imageWidth;
	private int imageHeight;
	private BufferedImage scaledImage = null;
	private static final int SCALE_FACTOR = 3;

	private int mouseX;
    private int mouseY;

    private Color colorGrid  =  new Color(255,255,0);
    private Color colorMouse = new Color(255,0,0);
    private Color colorAtlas = new Color(0,0,255);
    
    private int colorTimer = 0;
    private static final int COLOR_TIMER_MAX = 3;
    private int colorState = 0;
    private Color colorSelect1 = new Color(255,0,255);
    private Color colorSelect2 = new Color(0,127,255);
    private Color colorList1 = new Color(255,255,255);
    private Color colorList2 = new Color(0,0,0);
    
    private Timer timer = new Timer(100, this);
    
    public enum GridMode {
    	g8x8(8),
    	g16x16(16),
    	g32x32(32),
    	g64x64(64),
    	g128x128(128);
    	
    	public final int size;
    	
    	GridMode(int size) {
    		this.size = size;
    	}
    	
    	@Override
    	public String toString() {
			return this.name().substring(1);
    	}
    }
    private GridMode grid;
    
    
    public enum MouseState {
    	FREE,
    	SELECT
    }
    private MouseState mouseState = MouseState.FREE;
    
    private Main gui;
    
    // TODO: arbitrary box size 
    
    private class JLabelMouse extends JLabel implements MouseListener, MouseMotionListener {
    	
    	public JLabelMouse() {
    		super();
    		addMouseListener(this);
            addMouseMotionListener(this);
    	}
    	
    	// == mouse control methods ==
    	@Override
    	public void mouseDragged(MouseEvent ev) {
    		// TODO Auto-generated method stub
    		
    	}

    	@Override
    	public void mouseMoved(MouseEvent ev) {
    		mouseX = ev.getX() / SCALE_FACTOR;
    		mouseY = ev.getY() / SCALE_FACTOR;
    		
    		int gridX = (mouseX / grid.size) * grid.size;
    		int gridY = (mouseY / grid.size) * grid.size;
    		if (mouseState == MouseState.FREE) {
    			mouseBox.updatePosition(gridX, gridY);
    		}
    		// System.out.println("mouse x:" + mouseX /3 +" y:"+ mouseY /3);
    	}

    	@Override
    	public void mouseClicked(MouseEvent ev) {
    		// TODO Auto-generated method stub
    		
    	}

    	@Override
    	public void mouseEntered(MouseEvent ev) {
    		// TODO Auto-generated method stub
    		
    	}

    	@Override
    	public void mouseExited(MouseEvent ev) {
    		// TODO Auto-generated method stub
    		
    	}

    	@Override
    	public void mousePressed(MouseEvent ev) {
    		switch (mouseState) {
    		case FREE:
    			if (ev.getButton() == MouseEvent.BUTTON1) {
    				mouseState = MouseState.SELECT;
    			}
    		break;
    		case SELECT:
    			if (ev.getButton() == MouseEvent.BUTTON3) {
    				mouseState = MouseState.FREE;
    				// TODO: update position here
    			}
    		break;
    		}
    	}

    	@Override
    	public void mouseReleased(MouseEvent ev) {
    		// TODO Auto-generated method stub
    		
    	}
    	
    }
    
    private JLabelMouse picLabel;
 
    
    private class Box {
    	public int x;
    	public int y;
    	public int w;
    	public int h;
    	
    	public Box() {
    		this.x = 0;
    		this.y = 0;
    		this.w = 8;
    		this.h = 8;
    	}
    	public Box(int x, int y, int w, int h) {
    		this.x = x;
    		this.y = y;
    		this.w = w;
    		this.h = h;
    	}
    	
    	
    	public void updatePosition(int x, int y) {
    		this.x = x;
    		this.y = y;
    	}
    	public void updateSize(int w, int h) {
    		this.w = w;
    		this.h = h;
    	}
    	
    	public void render(Graphics g, int scale, Color c) {
    		g.setColor(c);
    		g.drawRect(x * scale, y* scale, w* scale, h* scale);
    	}
    	public void render2x(Graphics g, int scale, Color c) {
    		g.setColor(c);
    		g.drawRect(x * scale, y* scale, w* scale, h* scale);
    		g.drawRect(x * scale -1, y* scale-1, w* scale+2, h* scale+2);
    	}
    	
    }
    private Box mouseBox;
    
    
    public CanvasImage(Main gui) {
    	this.gui = gui;
    	timer.start();

    	// setLayout(new BorderLayout()); new GridBagLayout()
    	setLayout(new GridBagLayout()); 
    	picLabel = new JLabelMouse() {
    		@Override
    		protected void paintComponent(Graphics g) {
    			super.paintComponent(g);
    			paintLabel(g);
    		}
    	};
    	GridBagConstraints gbc = new GridBagConstraints();
    	// gbc.insets = new Insets(0, 0, 5, 5);
    	gbc.gridx = 0;
    	gbc.gridy = 0;
    	//gbc.anchor = GridBagConstraints.NORTHWEST;
    	//gbc.fill = GridBagConstraints.BOTH;
    	add(picLabel, gbc);
    	
        mouseBox = new Box();
        setGrid();
    }
    
//	public BufferedImage getImage() {
//		return image;
//	}
	public void setImage(BufferedImage image) {
		this.image = image;
		imageWidth = image.getWidth();
		imageHeight = image.getHeight();
		int w = imageWidth * SCALE_FACTOR;
		int h = imageHeight * SCALE_FACTOR;
		scaledImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB); // TODO: try catch for memory error
		AffineTransform at = AffineTransform.getScaleInstance(SCALE_FACTOR, SCALE_FACTOR);
        
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.drawRenderedImage(image, at);
        g2d.dispose();
        //this.scaledImage = scaledImage;
		
		// this.setSize(scaledImage.getWidth(), scaledImage.getHeight()); // is this necessary?
		picLabel.setIcon(new ImageIcon(scaledImage));
	}
    
    

	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
                
        // draw picture
        //g.drawImage(scaledImage, 0, 0, null);
        
	}
	private void paintLabel(Graphics g) {
		// draw grid
        // grid = this.gui.getGridMode();
		
		if (gui.isDisplayGrid()) {
			int gridIndexW = imageWidth / grid.size;
			int gridIndexH = imageHeight / grid.size;
			for (int indexX = 0; indexX <= gridIndexW; indexX++) {
				int x = indexX * grid.size * SCALE_FACTOR;
				int ymax = imageHeight * SCALE_FACTOR;
				g.setColor(colorGrid);
				g.drawLine(x, 0, x, ymax);
			}
			for (int indexY = 0; indexY <= gridIndexH; indexY++) {
				int y = indexY * grid.size * SCALE_FACTOR;
				int xmax = imageWidth * SCALE_FACTOR;
				g.setColor(colorGrid);
				g.drawLine(0, y, xmax, y);
			}
		}
		
        Enumeration<AtlasData> data = gui.getAtlasData();
        
        while(data.hasMoreElements()) {
        	AtlasData ad = data.nextElement();
        	ad.render(g, SCALE_FACTOR, colorAtlas);
        }
        
        switch (mouseState) {
		case FREE:
			mouseBox.render(g, SCALE_FACTOR, colorMouse);
		break;
		case SELECT:
			
			// TODO: bug with it drawing the rect
			if (colorState == 0) {
				mouseBox.render2x(g, SCALE_FACTOR, colorSelect1);
			}
			else if (colorState == 1) {
				mouseBox.render2x(g, SCALE_FACTOR, colorSelect2);
			}
			
		break;
		}
        
        AtlasData listAd = gui.getSelectedListItem();
        if (listAd != null) {
        	if (colorState == 0) {
        		listAd.render(g, SCALE_FACTOR, colorList1);
			}
			else if (colorState == 1) {
				listAd.render(g, SCALE_FACTOR, colorList2);
			}
        }
        
	}
	
	
	public void setGrid() {
		grid = this.gui.getGridMode();
		mouseBox.updateSize(grid.size, grid.size);
	}
	public boolean isSelectMode() {
		return mouseState == MouseState.SELECT;
	}
	
	public AtlasData getAtlasData(String name) {
		return new AtlasData(name, mouseBox.x, mouseBox.y, mouseBox.w, mouseBox.h);
	}
	public void changeMouseStateToFree() {
		mouseState = MouseState.FREE;
	}
	
	@Override
	public void actionPerformed(ActionEvent ev) {
		if (ev.getSource() == timer) {
			//repaint();
			picLabel.repaint();

			colorTimer++;
			if (colorTimer == COLOR_TIMER_MAX) {
				colorTimer = 0;
				colorState++;
				if (colorState == 2) {
					colorState = 0;
				}
			}
			
		}
	}

	
	
    
    
}
