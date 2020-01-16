// Skeletal program for the "Image Histogram" assignment
// Written by:  Minglun Gong

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import javax.imageio.*;

// Main class
public class ImageHistogram extends Frame implements ActionListener {
	BufferedImage input;
	int width, height;
	TextField texRad, texThres;
	ImageCanvas source, target;
	PlotCanvas plot;
	
	// Constructor
	public ImageHistogram(String name) {
		super("Image Histogram");
		// load image
		try {
			input = ImageIO.read(new File(name));
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}
		width = input.getWidth();
		height = input.getHeight();
		// prepare the panel for image canvas.
		Panel main = new Panel();
		source = new ImageCanvas(input);
		plot = new PlotCanvas();
		target = new ImageCanvas(input);
		main.setLayout(new GridLayout(1, 3, 10, 10));
		main.add(source);
		main.add(plot);
		main.add(target);
		// prepare the panel for buttons.
		Panel controls = new Panel();
		Button button = new Button("Display Histogram");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("Histogram Stretch");
		button.addActionListener(this);
		controls.add(button);
		controls.add(new Label("Cutoff fraction:"));
		texThres = new TextField("10", 2);
		controls.add(texThres);
		button = new Button("Aggressive Stretch");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("Histogram Equalization");
		button.addActionListener(this);
		controls.add(button);
		// add two panels
		add("Center", main);
		add("South", controls);
		addWindowListener(new ExitListener());
		setSize(width*2+400, height+100);
		setVisible(true);
	}
	class ExitListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
	// Action listener for button click events
	public void actionPerformed(ActionEvent e) {
		// example -- compute the average color for the image
		if ( ((Button)e.getSource()).getLabel().equals("Display Histogram") ) {
			int[] red = new int[256];
			int[] blue = new int[256];
			int[] green = new int[256];
			boolean displayLines = false;
			boolean grey = false;

			for ( int y=0, i=0 ; y<height ; y++ ) {
				for ( int x=0 ; x<width ; x++, i++ ) {
					Color clr = new Color(input.getRGB(x, y));
					red[clr.getRed()]++;
					green[clr.getGreen()]++;
					blue[clr.getBlue()]++;
				}
			}

			/*for ( int i=0; i<256; i++ ) {
				if(red[i]==[green[i]==blue[i]){

				}
			}*/

			for ( int j=0; j<256; j++ ) {
				float ired =0.0f;
				float igreen =0.0f;
				float iblue =0.0f;

				ired = red[j] / 10;
				igreen = green[j] / 10;
				iblue = blue[j] / 10;
				
				plot.addLineSegment(j,(int)ired,(int)igreen,(int)iblue);
			}

			plot.drawHist(0);
		}

		else if ( ((Button)e.getSource()).getLabel().equals("Histogram Stretch") ) {
			
		}

		else if ( ((Button)e.getSource()).getLabel().equals("Aggressive Stretch") ) {
			
		}

		else if ( ((Button)e.getSource()).getLabel().equals("Histogram Equalization") )
		{
			float[] hslValues = null ;
                
               		float Ered = 0,Egreen=0,Eblue=0;
               		for(int y=0, i=0 ; y<height ; y++)
                	{
                    	     for(int x=0 ; x<width ; x++, i++)
                            {
                      		Color clr = new Color(input.getRGB(x, y));
                        	float red = clr.getRed();
                        	float green = clr.getGreen();
                        	float blue = clr.getBlue();
                        	Ered = red;
                       		Egreen = green;
                        	Eblue = blue;
                    	    }    
                	}
                	float max = Math.max(Math.max(Ered, Egreen),Eblue);
                	float min = Math.min(Math.min(Ered, Egreen),Eblue);
                	float delta = max - min;
                	float hue = 0.f;
                	if(delta==0)
                	{
                   		hue = 0;
                	}
               		else if(max == Ered)
                	{
                    		hue = (float) (Egreen - Eblue)/delta;
                    		if(hue <0) 
                   		{
                        	    hue += 6.f;
                    		}
                	}
                	else if (max == Egreen)
               		{
                    		hue = (float) (Eblue - Ered)/(delta + 2.f);
                	}
                	else if(max == Eblue)
               		{
                    		hue = (float) (Ered - Egreen)/(delta + 4.f);
                	}
                	hue = 60.f * hue;
                
                	float lightness = (max + min) * 0.5f;
                	float saturation;
                
                	if(delta==0)
               		{
                   		 saturation = 0.f;
                	}
               		else
                	{
                    		saturation = delta/(1-Math.abs(2.f * 1 - 1.f));
                	}
                
                	hslValues[0] = hue;
               		hslValues[1] = saturation;
                	hslValues[2] = lightness;

		}
	}

	public static void main(String[] args) {
		new ImageHistogram(args.length==1 ? args[0] : "baboon.png");
	}
}

// Canvas for plotting histogram
class PlotCanvas extends Canvas {
	// lines for plotting axes and mean color locations
	LineSegment x_axis, y_axis;
	LineSegment red, green, blue;
	boolean showMean = false;
	boolean showSegments = false;
	int pri =0;
	int pgi =0;
	int pbi =0;
	int insertIndex = 0;

	LineSegment[] redSegments= new LineSegment[256];
	LineSegment[] blueSegments= new LineSegment[256];
	LineSegment[] greenSegments= new LineSegment[256];


	public PlotCanvas() {
		x_axis = new LineSegment(Color.BLACK, -10, 0, 256+10, 0);
		y_axis = new LineSegment(Color.BLACK, 0, -10, 0, 200+10);
	}
	// set mean image color for plot
	public void setMeanColor(Color clr) {
		red = new LineSegment(Color.RED, clr.getRed(), 0, clr.getRed(), 100);
		green = new LineSegment(Color.GREEN, clr.getGreen(), 0, clr.getGreen(), 100);
		blue = new LineSegment(Color.BLUE, clr.getBlue(), 0, clr.getBlue(), 100);
		showMean = true;
		repaint();
	}

	public void addLineSegment(int value, int ri, int gi, int bi) {
		int endpoint = value+1 > 256 ? value : value+1;
	
		red = new LineSegment(ri == 0 && pri == 0 ? Color.BLACK : Color.RED, value, pri, endpoint, ri);
		green = new LineSegment(gi == 0 && pgi == 0 ? Color.BLACK : Color.GREEN, value, pgi, endpoint, gi);
		blue = new LineSegment(bi == 0 && pbi == 0 ? Color.BLACK : Color.BLUE, value, pbi, endpoint, bi);

		redSegments[insertIndex] = red;
		greenSegments[insertIndex] = green;
		blueSegments[insertIndex] = blue;
		insertIndex++;

		pri = ri;
		pbi = bi;
		pgi = gi;
	}

	public void drawHist(int option){
		if (option == 0) showSegments = true;
		repaint();
	}

	// redraw the canvas
	public void paint(Graphics g) {
		// draw axis
		int xoffset = (getWidth() - 256) / 2;
		int yoffset = (getHeight() - 200) / 2;
		x_axis.draw(g, xoffset, yoffset, getHeight());
		y_axis.draw(g, xoffset, yoffset, getHeight());

		if ( showMean ) {
			red.draw(g, xoffset, yoffset, getHeight());
			green.draw(g, xoffset, yoffset, getHeight());
			blue.draw(g, xoffset, yoffset, getHeight());
		}

		if (showSegments){
			for(int i = 0; i < 256; i ++){
				redSegments[i].draw(g, xoffset, yoffset, getHeight());
				greenSegments[i].draw(g, xoffset, yoffset, getHeight());
				blueSegments[i].draw(g, xoffset, yoffset, getHeight());
			}
		}
	}
}

// LineSegment class defines line segments to be plotted
class LineSegment {
	// location and color of the line segment
	int x0, y0, x1, y1;
	Color color;
	// Constructor
	public LineSegment(Color clr, int x0, int y0, int x1, int y1) {
		color = clr;
		this.x0 = x0; this.x1 = x1;
		this.y0 = y0; this.y1 = y1;
	}
	public void draw(Graphics g, int xoffset, int yoffset, int height) {
		g.setColor(color);
		g.drawLine(x0+xoffset, height-y0-yoffset, x1+xoffset, height-y1-yoffset);
	}
}
