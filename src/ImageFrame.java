/**
 * Modified by Weston Ford on 2/12/2016.
 *
 * Code credit to user Alex Orzechowski
 * http://stackoverflow.com/questions/8933893/convert-each-animated-gif-frame-to-a-separate-bufferedimage
 *
 */
import java.awt.image.BufferedImage;
public class ImageFrame {
    private final int delay;
    private final BufferedImage image;
    private final String disposal;
    private final int width, height;

    public ImageFrame (BufferedImage image, int delay, String disposal, int width, int height){
        this.image = image;
        this.delay = delay;
        this.disposal = disposal;
        this.width = width;
        this.height = height;
    }

    public ImageFrame (BufferedImage image){
        this.image = image;
        this.delay = -1;
        this.disposal = null;
        this.width = -1;
        this.height = -1;
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getDelay() {
        return delay;
    }

    public String getDisposal() {
        return disposal;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}