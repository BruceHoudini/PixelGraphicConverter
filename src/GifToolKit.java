import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Weston Ford on 2/12/2016.
 */
public class GifToolKit {
    public BufferedImage scale(BufferedImage img, int targetWidth, int targetHeight) {

        int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = img;
        BufferedImage scratchImage = null;
        Graphics2D g2 = null;

        int w = img.getWidth();
        int h = img.getHeight();

        int prevW = w;
        int prevH = h;

        do {
            if (w > targetWidth) {
                w /= 2;
                w = (w < targetWidth) ? targetWidth : w;
            }

            if (h > targetHeight) {
                h /= 2;
                h = (h < targetHeight) ? targetHeight : h;
            }

            if (scratchImage == null) {
                scratchImage = new BufferedImage(w, h, type);
                g2 = scratchImage.createGraphics();
            }

            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(ret, 0, 0, w, h, 0, 0, prevW, prevH, null);

            prevW = w;
            prevH = h;
            ret = scratchImage;
        } while (w != targetWidth || h != targetHeight);

        if (g2 != null) {
            g2.dispose();
        }

        if (targetWidth != ret.getWidth() || targetHeight != ret.getHeight()) {
            scratchImage = new BufferedImage(targetWidth, targetHeight, type);
            g2 = scratchImage.createGraphics();
            g2.drawImage(ret, 0, 0, null);
            g2.dispose();
            ret = scratchImage;
        }

        return ret;

    }
    /**
     *
     * Code credit to user Alex Orzechowski
     * http://stackoverflow.com/questions/8933893/convert-each-animated-gif-frame-to-a-separate-bufferedimage
     *
     */
    public ImageFrame[] readGif(InputStream stream) throws IOException {
        ArrayList<ImageFrame> frames = new ArrayList<ImageFrame>(2);

        ImageReader reader = (ImageReader) ImageIO.getImageReadersByFormatName("gif").next();
        reader.setInput(ImageIO.createImageInputStream(stream));

        int lastx = 0;
        int lasty = 0;

        int width = -1;
        int height = -1;

        IIOMetadata metadata = reader.getStreamMetadata();

        Color backgroundColor = null;

        if(metadata != null) {
            IIOMetadataNode globalRoot = (IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());

            NodeList globalColorTable = globalRoot.getElementsByTagName("GlobalColorTable");
            NodeList globalScreeDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");

            if (globalScreeDescriptor != null && globalScreeDescriptor.getLength() > 0){
                IIOMetadataNode screenDescriptor = (IIOMetadataNode) globalScreeDescriptor.item(0);

                if (screenDescriptor != null){
                    width = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
                    height = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
                }
            }

            if (globalColorTable != null && globalColorTable.getLength() > 0){
                IIOMetadataNode colorTable = (IIOMetadataNode) globalColorTable.item(0);

                if (colorTable != null) {
                    String bgIndex = colorTable.getAttribute("backgroundColorIndex");

                    IIOMetadataNode colorEntry = (IIOMetadataNode) colorTable.getFirstChild();
                    while (colorEntry != null) {
                        if (colorEntry.getAttribute("index").equals(bgIndex)) {
                            int red = Integer.parseInt(colorEntry.getAttribute("red"));
                            int green = Integer.parseInt(colorEntry.getAttribute("green"));
                            int blue = Integer.parseInt(colorEntry.getAttribute("blue"));

                            backgroundColor = new Color(red, green, blue);
                            break;
                        }

                        colorEntry = (IIOMetadataNode) colorEntry.getNextSibling();
                    }
                }
            }
        }

        BufferedImage master = null;
        boolean hasBackround = false;

        for (int frameIndex = 0;; frameIndex++) {
            BufferedImage image;
            try{
                image = reader.read(frameIndex);
            }catch (IndexOutOfBoundsException io){
                break;
            }

            if (width == -1 || height == -1){
                width = image.getWidth();
                height = image.getHeight();
            }

            IIOMetadataNode root = (IIOMetadataNode) reader.getImageMetadata(frameIndex).getAsTree("javax_imageio_gif_image_1.0");
            IIOMetadataNode gce = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
            NodeList children = root.getChildNodes();

            int delay = Integer.valueOf(gce.getAttribute("delayTime"));

            String disposal = gce.getAttribute("disposalMethod");

            if (master == null){
                master = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                master.createGraphics().setColor(backgroundColor);
                master.createGraphics().fillRect(0, 0, master.getWidth(), master.getHeight());

                hasBackround = image.getWidth() == width && image.getHeight() == height;

                master.createGraphics().drawImage(image, 0, 0, null);
            }else{
                int x = 0;
                int y = 0;

                for (int nodeIndex = 0; nodeIndex < children.getLength(); nodeIndex++){
                    Node nodeItem = children.item(nodeIndex);

                    if (nodeItem.getNodeName().equals("ImageDescriptor")){
                        NamedNodeMap map = nodeItem.getAttributes();

                        x = Integer.valueOf(map.getNamedItem("imageLeftPosition").getNodeValue());
                        y = Integer.valueOf(map.getNamedItem("imageTopPosition").getNodeValue());
                    }
                }

                if (disposal.equals("restoreToPrevious")){
                    BufferedImage from = null;
                    for (int i = frameIndex - 1; i >= 0; i--){
                        if (!frames.get(i).getDisposal().equals("restoreToPrevious") || frameIndex == 0){
                            from = frames.get(i).getImage();
                            break;
                        }
                    }

                    {
                        ColorModel model = from.getColorModel();
                        boolean alpha = from.isAlphaPremultiplied();
                        WritableRaster raster = from.copyData(null);
                        master = new BufferedImage(model, raster, alpha, null);
                    }
                }else if (disposal.equals("restoreToBackgroundColor") && backgroundColor != null){
                    if (!hasBackround || frameIndex > 1){
                        master.createGraphics().fillRect(lastx, lasty, frames.get(frameIndex - 1).getWidth(), frames.get(frameIndex - 1).getHeight());
                    }
                }
                master.createGraphics().drawImage(image, x, y, null);

                lastx = x;
                lasty = y;
            }

            {
                BufferedImage copy;

                {
                    ColorModel model = master.getColorModel();
                    boolean alpha = master.isAlphaPremultiplied();
                    WritableRaster raster = master.copyData(null);
                    copy = new BufferedImage(model, raster, alpha, null);
                }
                frames.add(new ImageFrame(copy, delay, disposal, image.getWidth(), image.getHeight()));
            }

            master.flush();
        }
        reader.dispose();

        return frames.toArray(new ImageFrame[frames.size()]);
    }
    public void framesToFile(ImageFrame[] imgframe, String name){
        for (int i = 0; i < imgframe.length; i++) {
            try {
                ImageIO.write(imgframe[i].getImage(), "gif", new File("C:\\Images\\gifs\\converted\\imageform\\" + name + "\\" + name + "-test" + i + ".gif"));
            }
            catch(IOException ioe){
                ioe.printStackTrace();
            }
        }
    }
    public void scaledFramesToFile(ImageFrame[] imgframe, String name){
        for (int i = 0; i < imgframe.length; i++) {
            try {
                ImageIO.write(scale(imgframe[i].getImage(),imgframe[i].getWidth()/2, imgframe[i].getHeight()/2), "gif", new File("C:\\Images\\gifs\\converted\\imageform\\" + name + "\\" + name + "-scaletest" + i + ".gif"));
            }
            catch(IOException ioe){
                ioe.printStackTrace();
            }
        }
    }

    //Sets all white pixels within image to be transparent.
    public void makeBackTransparent(BufferedImage bi, Color background){

        int markerRGB = background.getRGB() | 0xFFFFFFFF;

        int height = bi.getHeight();
        int width = bi.getWidth();
        int compare;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                compare = bi.getRGB(j, i) | 0xFF000000;
                if (compare == markerRGB){
                    bi.setRGB(j, i, 0x00FFFFFF);
                }
            }
        }
    }
}
