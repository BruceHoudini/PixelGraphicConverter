import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Weston Ford on 2/12/2016.
 */
public class BlockGraphicConverter {

    public BufferedImage convertToBlockGraphic(Color[][] colors, int width, int height, int blockSize) throws IOException {

        BufferedImage img = new BufferedImage(width*blockSize, height*blockSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g2d.setColor(Color.WHITE);

        g2d.fillRect(0, 0, width*blockSize, height*blockSize);


        for (int i = 0; i*blockSize < height*blockSize; i++){
            for (int j = 0; j * blockSize < width * blockSize; j++){
                g2d.setColor(colors[j][i]);
                g2d.fillRect(j*blockSize, i*blockSize, blockSize, blockSize);
            }
        }

       /* int currentLine = 1;
        g2d.drawString(text, 0, 0);
        while((text = br.readLine()) != null) {
            g2d.drawString(text, 0, fm.getAscent() * currentLine);
            currentLine++;
        }*/

        g2d.dispose();
        return img;
    }

}
