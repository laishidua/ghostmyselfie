/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.laishidua.mobilecloud.ghostmyselfie.controller;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

import java.util.concurrent.ThreadLocalRandom;

import com.laishidua.mobilecloud.ghostmyselfie.model.GhostMySelfie;

/**
 * This class provides a simple implementation to store ghostmyselfie binary
 * data on the file system in a "ghostmyselfies" folder. The class provides
 * methods for saving ghostmyselfies and retrieving their binary data.
 * 
 * @author Armando Palacios based on Jules code
 *
 */
public class GhostMySelfieFileManager {

	/**
	 * This static factory method creates and returns a 
	 * GhostMySelfieFileManager object to the caller. Feel free to customize
	 * this method to take parameters, etc. if you want.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static GhostMySelfieFileManager get() throws IOException {
		return new GhostMySelfieFileManager();
	}

	private static final String DEFAULT_GHOSTMYSELFIES_FOLDER = "ghostmyselfies";
	//Directory where has to be placed the ghost images.
	private static final String DEFAULT_GHOSTS_FOLDER = "ghosts";
	
	private Path targetDir_;
	private Path ghostsDir_;
	
	
	public GhostMySelfieFileManager() throws IOException{
		this(DEFAULT_GHOSTMYSELFIES_FOLDER, DEFAULT_GHOSTS_FOLDER);
	}
	
	// The GhostMySelfieFileManager.get() method should be used
	// to obtain an instance
	public GhostMySelfieFileManager(String dir1, String dir2) throws IOException{
		targetDir_ = Paths.get(dir1);
		ghostsDir_ = Paths.get(dir2);
		if(!Files.exists(targetDir_)){
			Files.createDirectories(targetDir_);
		}
		if(!Files.exists(ghostsDir_)){
			Files.createDirectories(ghostsDir_);
		}		
	}
	
	// Private helper method for resolving ghostmyselfies file paths
	public Path getGhostMySelfiePath(GhostMySelfie v){
		//assert(v != null); 
		return targetDir_.resolve("ghostMySelfie-" + v.getId() + ".jpg");
	}
	
	/**
	 * This method returns true if the specified GhostMySelfie has binary
	 * data stored on the file system.
	 * 
	 * @param v
	 * @return
	 */
	public boolean hasGhostMySelfieData(GhostMySelfie v){
		Path source = getGhostMySelfiePath(v);
		return Files.exists(source);
	}
	
	/**
	 * This method copies the binary data for the given ghostmyselfie to
	 * the provided output stream. The caller is responsible for
	 * ensuring that the specified GhostMySelfie has binary data associated
	 * with it. If not, this method will throw a FileNotFoundException.
	 * 
	 * @param v 
	 * @param out
	 * @throws IOException 
	 */
	public void copyGhostMySelfieData(GhostMySelfie v, OutputStream out) throws IOException {
		Path source = getGhostMySelfiePath(v);
		if(!Files.exists(source)){
			throw new FileNotFoundException("Unable to find the referenced ghostMySelfie file for ghostMySelfieId:"+v.getId());
		}
		Files.copy(source, out);
	}
	
	/**
	 * This method reads all of the data in the provided InputStream and stores
	 * it on the file system. The data is associated with the GhostMySelfie object that
	 * is provided by the caller.
	 * 
	 * @param v
	 * @param ghostMySelfieData
	 * @throws IOException
	 */
	public void saveGhostMySelfieData(GhostMySelfie v, InputStream ghostMySelfieData) throws IOException{
		//assert(ghostMySelfieData != null);
		Path target = getGhostMySelfiePath(v);
		String mimeType = URLConnection.guessContentTypeFromStream(ghostMySelfieData);
		ByteArrayOutputStream jpgOutputStream = new ByteArrayOutputStream();
		int randomImageNum = ThreadLocalRandom.current().nextInt(1, 7);
		//Getting a ghost.
		BufferedImage layertmp = ImageIO.read(ghostsDir_.resolve("ghost-" + randomImageNum + ".png").toFile());		
		if (mimeType.equals("image/png")) {
			// Convert PNG to JPG
			ImageIO.write(ImageIO.read(ghostMySelfieData), "jpeg", jpgOutputStream);
			if(!v.getFilters().isEmpty()) {
				if (v.getFilters().contains("ghost")) {
				  jpgOutputStream = overlayImage(createImageFromBytes(jpgOutputStream.toByteArray()), layertmp);
				}
				if (v.getFilters().contains("gray")) {
				  jpgOutputStream = grayFilter(createImageFromBytes(jpgOutputStream.toByteArray()));
				}
				if (v.getFilters().contains("blur")) {
					  jpgOutputStream = blurFilter(createImageFromBytes(jpgOutputStream.toByteArray()));
				}	
				if (v.getFilters().contains("dark")) {
					  jpgOutputStream = darkFilter(createImageFromBytes(jpgOutputStream.toByteArray()));
				}				
			}
			layertmp.flush();
			ByteArrayInputStream bais = new ByteArrayInputStream(jpgOutputStream.toByteArray());
			Files.copy(bais, target, StandardCopyOption.REPLACE_EXISTING);
		} else {
			if(!v.getFilters().isEmpty()) {			
				if (v.getFilters().contains("ghost")) {
				  jpgOutputStream = overlayImage(ImageIO.read(ghostMySelfieData), layertmp);
				  ghostMySelfieData = new ByteArrayInputStream(jpgOutputStream.toByteArray());
				}
				if (v.getFilters().contains("gray")) {
				   jpgOutputStream = grayFilter(ImageIO.read(ghostMySelfieData));
				   ghostMySelfieData = new ByteArrayInputStream(jpgOutputStream.toByteArray());
				}
				if (v.getFilters().contains("blur")) {
					   jpgOutputStream = blurFilter(ImageIO.read(ghostMySelfieData));
					   ghostMySelfieData = new ByteArrayInputStream(jpgOutputStream.toByteArray());
				}	
				if (v.getFilters().contains("dark")) {
					   jpgOutputStream = darkFilter(ImageIO.read(ghostMySelfieData));
					   ghostMySelfieData = new ByteArrayInputStream(jpgOutputStream.toByteArray());
				}				
			}
			layertmp.flush();
			Files.copy(ghostMySelfieData, target, StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	private BufferedImage createImageFromBytes(byte[] imageData) {
	    ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
	    try {
	        return ImageIO.read(bais);
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	}	

	/**
	* It reads two Buffered Images,
	* drawing the second one over the first one but considering green pixels on the second one as
	* transparent/translucent -- the closer the color is to green, the more transparent it is.
	* In this example, the similarity to green will be calculated using the hue coordinate (HSB color
	* space).
	*/
	public ByteArrayOutputStream overlayImage(BufferedImage background, BufferedImage layertmp) 
			throws IOException {
		  // Read the input images. We assume that the first image is the background, and that it is larger
		  // than the second image.
		  WritableRaster raster = background.getRaster();
		  //Max scale value.
		  int scaleVal = 0;
		  //Max isplacement value.
		  int displacementX = 0;
		  if (background.getWidth() > background.getHeight()) {
		      scaleVal = background.getHeight();
		      displacementX = background.getWidth() - background.getHeight();
		  } else {
		      scaleVal = background.getWidth();
		      displacementX = background.getHeight() - background.getWidth();
		  }
		  BufferedImage layer = Scalr.resize(layertmp, Method.QUALITY, 
		                                       scaleVal, scaleVal, Scalr.OP_ANTIALIAS);
		  int width = layer.getWidth();
		  int height = layer.getHeight();
		  // We will shift the overlay image over the background this amount.
		  //It moves the ghost over X.
		  int shiftX = 0;
		  if (background.getWidth() > background.getHeight()) {
		   shiftX = ThreadLocalRandom.current().nextInt(0, displacementX - 1);
		  }
		  int shiftY = 0;
		  // Slow method: scan all input (layer) image pixels and corresponding background pixels.
		  // Calculate its "greenness" and translucency and recreate the pixels' values, plotting
		  // them over the background.
		  int iPixel,lPixel;
		  float targetHue = 1f/3f;
		  float tolerance = 0.1f;
		  int iRed,iGreen,iBlue,lRed,lGreen,lBlue,oRed,oGreen,oBlue;
		  for(int w=0;w<width;w++)
		  for(int h=0;h<height;h++)
		        {
		        // Background pixels.
		        iPixel = background.getRGB(w+shiftX,h+shiftY);
		        iRed   = (int)((iPixel&0x00FF0000)>>>16); // Red level
		        iGreen = (int)((iPixel&0x0000FF00)>>>8);  // Green level
		        iBlue  = (int) (iPixel&0x000000FF);       // Blue level
		        // Layer pixels.
		        lPixel = layer.getRGB(w,h);
		        lRed   = (int)((lPixel&0x00FF0000)>>>16); // Red level
		        lGreen = (int)((lPixel&0x0000FF00)>>>8);  // Green level
		        lBlue  = (int) (lPixel&0x000000FF);       // Blue level
		        float[] lHSB = Color.RGBtoHSB(lRed,lGreen,lBlue,null);
		        // Calculate the translucency, based on the green value of the layer, using HSB coordinates.
		        // To make calculations easier, let's assume that the translucency is a value between 0 
		        // (invisible) and 1 (opaque).
		        float deltaHue = Math.abs(lHSB[0]-targetHue);
		        float translucency = (deltaHue/tolerance);
		        translucency = Math.min(translucency,0.4f);
		        // Recalculate the RGB coordinates of the layer and background pixels, using the translucency
		        // as a weight.
		        oRed = (int)(translucency*lRed+(1-translucency)*iRed);
		        oGreen = (int)(translucency*lGreen+(1-translucency)*iGreen);
		        oBlue = (int)(translucency*lBlue+(1-translucency)*iBlue);
		        // Set the pixel on the output image's raster.
		        raster.setPixel(w+shiftX,h+shiftY,new int[]{oRed,oGreen,oBlue,255});        
		        } // end for
		        // Save the image as a PNG via ImageIO.
		  ByteArrayOutputStream jpgOutputStream = new ByteArrayOutputStream();
		  ImageIO.write(background,"JPG", jpgOutputStream);	
		  return jpgOutputStream;
	}
	
	public ByteArrayOutputStream grayFilter(BufferedImage input) throws IOException {
		 // Create a gray level image of the same size.
		 BufferedImage im =
		   new BufferedImage(input.getWidth(),input.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
		 // Get the graphics context for the gray level image.
		 Graphics2D g2d = im.createGraphics();
		 // Render the input image on it.
		 g2d.drawImage(input,0,0,null);
		 // Store the resulting image using the PNG format.
		 ByteArrayOutputStream jpgOutputStream = new ByteArrayOutputStream();
		 ImageIO.write(im,"JPG", jpgOutputStream);
		 im.flush();
		 input.flush();
		 return jpgOutputStream;
	}
	
	public ByteArrayOutputStream blurFilter(BufferedImage input) throws IOException {
		 float[] matrix = new float[400];
         for (int i = 0; i < 400; i++)
            matrix[i] = 1.0f/400.0f;
		 
		  BufferedImageOp op=new ConvolveOp(new Kernel(20,20,matrix), ConvolveOp.EDGE_NO_OP, null);
		  input=op.filter(input,null);
		  // Get rid of garbage border
		  Graphics2D g2d=input.createGraphics();
		  g2d.drawImage(input, null, 0, 0);
		  ByteArrayOutputStream jpgOutputStream = new ByteArrayOutputStream();
		  ImageIO.write(input,"JPG", jpgOutputStream);	
		  input.flush();
		  return jpgOutputStream;		 
		 
	}
	
	public ByteArrayOutputStream darkFilter(BufferedImage input) throws IOException {
		 float[] matrix = {
		            0.01f, 0.01f, 0.01f,
		            0.01f, 0.5f, 0.01f,
		            0.01f, 0.01f, 0.01f
			};
		 BufferedImageOp op = new ConvolveOp( new Kernel(3, 3, matrix) );
		 input = op.filter(input, null);
		 // Store the resulting image using the PNG format.
		 ByteArrayOutputStream jpgOutputStream = new ByteArrayOutputStream();
		 ImageIO.write(input,"JPG", jpgOutputStream);
		 input.flush();
		 return jpgOutputStream;
	}		
	
}
