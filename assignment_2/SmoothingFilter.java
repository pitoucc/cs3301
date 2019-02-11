import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

// Main class
public class SmoothingFilter extends Frame implements ActionListener {
	BufferedImage input;
	ImageCanvas source, target;
	TextField texSigma;
	int width, height;
	// Constructor
	public SmoothingFilter(String name) {
		super("Smoothing Filters");
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
		target = new ImageCanvas(input);
		main.setLayout(new GridLayout(1, 2, 10, 10));
		main.add(source);
		main.add(target);
		// prepare the panel for buttons.
		Panel controls = new Panel();
		Button button = new Button("Add noise");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("5x5 mean");
		button.addActionListener(this);
		controls.add(button);
		controls.add(new Label("Sigma:"));
		texSigma = new TextField("1", 1);
		controls.add(texSigma);
		button = new Button("5x5 Gaussian");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("5x5 median");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("5x5 Kuwahara");
		button.addActionListener(this);
		controls.add(button);
		// add two panels
		add("Center", main);
		add("South", controls);
		addWindowListener(new ExitListener());
		setSize(width*2+100, height+100);
		setVisible(true);
	}
	class ExitListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
	// Action listener for button click events
	public void actionPerformed(ActionEvent e) {
		// example -- add random noise
		if ( ((Button)e.getSource()).getLabel().equals("Add noise") ) {
			Random rand = new Random();
			int dev = 64;
			for ( int y=0, i=0 ; y<height ; y++ )
				for ( int x=0 ; x<width ; x++, i++ ) {
					Color clr = new Color(source.image.getRGB(x, y));
					int red = clr.getRed() + (int)(rand.nextGaussian() * dev);
					int green = clr.getGreen() + (int)(rand.nextGaussian() * dev);
					int blue = clr.getBlue() + (int)(rand.nextGaussian() * dev);
					red = red < 0 ? 0 : red > 255 ? 255 : red;
					green = green < 0 ? 0 : green > 255 ? 255 : green;
					blue = blue < 0 ? 0 : blue > 255 ? 255 : blue;
					source.image.setRGB(x, y, (new Color(red, green, blue)).getRGB());
				}
			source.repaint();
		}

		if ( ((Button)e.getSource()).getLabel().equals("5x5 mean") ) {

			int w = 2;

			for(int q=0; q<height; q++){
				int rSum = 0;
				int gSum = 0;
				int bSum = 0;
				for (int u=-w; u<=w; u++){
					rSum += new Color(source.image.getRGB(q,u+w)).getRed();
					gSum += new Color(source.image.getRGB(q,u+w)).getGreen();	
					bSum += new Color(source.image.getRGB(q,u+w)).getBlue();
				}				
				for ( int p=w+1 ; p<width-w ; p++ ){
					rSum += (new Color(source.image.getRGB(q,p+w)).getRed()) - (new Color(source.image.getRGB(q,p-w-1)).getRed());
					gSum += (new Color(source.image.getRGB(q,p+w)).getGreen()) - (new Color(source.image.getRGB(q,p-w-1)).getGreen());	
					bSum += (new Color(source.image.getRGB(q,p+w)).getBlue()) - (new Color(source.image.getRGB(q,p-w-1)).getBlue());

					target.image.setRGB(q,p,(new Color(rSum / (2*w+1), gSum / (2*w+1) , bSum / (2*w+1)).getRGB()));
				}
			}
				
			for(int p=0; p<width; p++){
				int rSum = 0;
				int gSum = 0;
				int bSum = 0;
				for (int u=-w; u<=w; u++){
					rSum += new Color(source.image.getRGB(p,u+w)).getRed();
					gSum += new Color(source.image.getRGB(p,u+w)).getGreen();	
					bSum += new Color(source.image.getRGB(p,u+w)).getBlue();
				}				
				for ( int q=w+1 ; q<width-w ; q++ ){
					rSum += (new Color(source.image.getRGB(q+w,p)).getRed()) - (new Color(source.image.getRGB(q-w-1,p)).getRed());
					gSum += (new Color(source.image.getRGB(q+w,p)).getGreen()) - (new Color(source.image.getRGB(q-w-1,p)).getGreen());	
					bSum += (new Color(source.image.getRGB(q+w,p)).getBlue()) - (new Color(source.image.getRGB(q-w-1,p)).getBlue());

					target.image.setRGB(q,p,(new Color(rSum / (2*w+1), gSum / (2*w+1) , bSum / (2*w+1)).getRGB()));
				}
			}
			
			target.repaint();
		}
		
		if ( ((Button)e.getSource()).getLabel().equals("5x5 Gaussian") ) {
			
			int w = 2;

			double m = 1.0/571.0;
			double[] gauss = {
				2.0*m, 7.0*m, 12.0*m, 7.0*m, 2.0*m,
				7.0*m, 31.0*m, 52.0*m, 31.0*m, 7.0*m,
				12.0*m, 52.0*m, 127.0*m, 52.0*m, 12.0*m,
				7.0*m, 31.0*m, 52.0*m, 31.0*m, 7.0*m,
				2.0*m, 7.0*m, 12.0*m, 7.0*m, 2.0*m
			};

			for(int q=0; q<height; q++){
				double rSum = 0;
				double gSum = 0;
				double bSum = 0;
				for (int u=-w; u<=w; u++){
					double g = gauss[u+w];
					rSum += new Color(source.image.getRGB(q,u+w)).getRed()*g;
					gSum += new Color(source.image.getRGB(q,u+w)).getGreen()*g;	
					bSum += new Color(source.image.getRGB(q,u+w)).getBlue()*g;
				}				
				for ( int p=w+1 ; p<width-w ; p++ ){
					rSum += (new Color(source.image.getRGB(q,p+w)).getRed()) - (new Color(source.image.getRGB(q,p-w-1)).getRed());
					gSum += (new Color(source.image.getRGB(q,p+w)).getGreen()) - (new Color(source.image.getRGB(q,p-w-1)).getGreen());	
					bSum += (new Color(source.image.getRGB(q,p+w)).getBlue()) - (new Color(source.image.getRGB(q,p-w-1)).getBlue());

					rSum = Math.round(rSum);
					gSum = Math.round(gSum);
					bSum = Math.round(bSum);
					if (rSum > 255) rSum = 255;
					else if (rSum < 0) rSum = 0;
					if (gSum > 255) gSum = 255;
					else if (gSum < 0) gSum = 0;
					if (bSum > 255) bSum = 255;
					else if (bSum < 0) bSum = 0;


					target.image.setRGB(q,p,(new Color((int)rSum / (2*w+1), (int)gSum / (2*w+1) ,(int) bSum / (2*w+1)).getRGB()));
				}
			}
				
			for(int p=0; p<width; p++){
				double rSum = 0;
				double gSum = 0;
				double bSum = 0;
				for (int u=-w; u<=w; u++){
					double g = gauss[u+w];
					rSum += new Color(source.image.getRGB(p,u+w)).getRed()*g;
					gSum += new Color(source.image.getRGB(p,u+w)).getGreen()*g;	
					bSum += new Color(source.image.getRGB(p,u+w)).getBlue()*g;
				}				
				for ( int q=w+1 ; q<width-w ; q++ ){
					rSum += (new Color(source.image.getRGB(q+w,p)).getRed()) - (new Color(source.image.getRGB(q-w-1,p)).getRed());
					gSum += (new Color(source.image.getRGB(q+w,p)).getGreen()) - (new Color(source.image.getRGB(q-w-1,p)).getGreen());	
					bSum += (new Color(source.image.getRGB(q+w,p)).getBlue()) - (new Color(source.image.getRGB(q-w-1,p)).getBlue());

					rSum = Math.round(rSum);
					gSum = Math.round(gSum);
					bSum = Math.round(bSum);
					if (rSum > 255) rSum = 255;
					else if (rSum < 0) rSum = 0;
					if (gSum > 255) gSum = 255;
					else if (gSum < 0) gSum = 0;
					if (bSum > 255) bSum = 255;
					else if (bSum < 0) bSum = 0;

					target.image.setRGB(q,p,(new Color((int)rSum / (2*w+1), (int)gSum / (2*w+1) , (int)bSum / (2*w+1)).getRGB()));
				}
			}

			target.repaint();
		}
		
		if ( ((Button)e.getSource()).getLabel().equals("5x5 median") ) {
			int kernalSize = 5;

			for(int x = 0; x < width; x++){
				for(int y = 0; y < height; y++){
					
					Color kernal[] = getKernal(source, x, y, kernalSize);
					quickSort(kernal,0,(kernalSize*kernalSize)-1);
					
					target.image.setRGB(x,y,kernal[(kernal.length/2)+1].getRGB());
				}
			}
		}

		if ( ((Button)e.getSource()).getLabel().equals("5x5 Kuwahara") ) {
			int kernalSize = 5;
			
			for(int x = 0; x < width; x++){
				for(int y = 0; y < height; y++){
					Color newCol = new Color(0,0,0);
					Color kernal[] = getKernal(source, x, y, kernalSize);
					
					for(int i=0; i < 4; i++){
						int subKernalSize = (kernalSize /2) +1;
						int lowerBound = i*subKernalSize;
						int rSum =0;
						int gSum =0;
						int bSum =0;
						int var = Integer.MAX_VALUE;
						int newVar = 0;
						int section =1;
						for(int j =0; j < subKernalSize*subKernalSize; j++){
							if(j > lowerBound*section && j < (lowerBound*section)+subKernalSize){
								rSum += kernal[j].getRed();
								gSum += kernal[j].getGreen();
								bSum += kernal[j].getBlue();
							}
							if(section <= subKernalSize && j%subKernalSize == 0) section++;
						}

						for(int j =0; j < subKernalSize*subKernalSize; j++){
							if(j > lowerBound*section && j < (lowerBound*section)+subKernalSize){
								bSum += (kernal[j].getRed()-rSum/subKernalSize*subKernalSize)*(kernal[j].getRed()-rSum/subKernalSize*subKernalSize);
								gSum += (kernal[j].getGreen()-rSum/subKernalSize*subKernalSize)*(kernal[j].getGreen()-rSum/subKernalSize*subKernalSize);
								bSum += (kernal[j].getBlue()-rSum/subKernalSize*subKernalSize)*(kernal[j].getGreen()-rSum/subKernalSize*subKernalSize);
							}
							if(section <= subKernalSize && j%subKernalSize == 0) section++;
						}

						newVar = rSum/subKernalSize*subKernalSize + gSum/subKernalSize*subKernalSize + bSum/subKernalSize*subKernalSize;

						if(newVar < var){
							newCol = new Color(rSum/subKernalSize*subKernalSize,gSum/subKernalSize*subKernalSize,bSum/subKernalSize*subKernalSize);
						}
					}

					target.image.setRGB(x,y,newCol.getRGB());

				}
			}
		}
	}

	private void quickSort(Color[] array, int start, int end) {
		int partition = partition(array, start, end);

		if(partition-1>start){
			quickSort(array, start, partition -1);
		}

		if(partition+1<end){
			quickSort(array, partition + 1, end);
		}
	}

	private int partition(Color [] array, int start, int end) {
		int intPivot = array[end].getRed() + array[end].getGreen() + array[end].getBlue();
		Color pivot = array[end];

		for(int i=0; i<end; i++){
			if((array[i].getRed() + array[i].getGreen() + array[i].getBlue()) < intPivot) {
				Color temp = array[start];
				array[start] = array[i];
				array[i] = temp;
				start++;
			}
		}

		Color temp = array[start];
		array[start] = pivot;
		array[end] = temp;
		
		return start;
	}

	private Color[] getKernal (ImageCanvas image, int x, int y, int size){		
		Color[] kernalMatrix = new Color[size*size];
		int width = image.getWidth();
		int height = image.getHeight();
		int xstart = x - (int)Math.floor(size / 2);
		int ystart = y - (int)Math.floor(size / 2);
		int index = 0;

		for(int i = xstart; i < (xstart+size); i++){
			for(int j = ystart; j < (ystart+size); j++){
				try{
				kernalMatrix[index] = (i < 0 || j < 0 || i > width || j > height) ? new Color(0,0,0) : new Color(image.image.getRGB(i,j));
				}
				catch(ArrayIndexOutOfBoundsException e){
					System.out.println(i + " " + j);
					System.out.println(width + "  " + height);
					System.out.println(x + " " + y);
				}
				index++;
			}
		}

		return kernalMatrix;
	}

	public static void main(String[] args) {
		new SmoothingFilter(args.length==1 ? args[0] : "baboon.png");
	}
}
