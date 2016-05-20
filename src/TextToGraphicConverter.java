import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Edited by Weston Ford on 2/12/2016.
 *
 * Original code from user Gabriel Ruiu
 * http://stackoverflow.com/questions/23568114/converting-text-to-image-in-java/23568524#23568524
 *
 */
public class TextToGraphicConverter {

    public BufferedImage convertTextToGraphic(Font font, BufferedReader br, int lines, Color background) throws IOException {
        String text = br.readLine();

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(text);
        int height = fm.getAscent();
        g2d.dispose();

        img = new BufferedImage(width, height*(lines-1), BufferedImage.TYPE_INT_ARGB);

        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(background);
        g2d.drawRect(0, 0, width, height*(lines-1));
        g2d.fillRect(0, 0, width, height*(lines-1));
        g2d.setColor(Color.BLACK);
        int currentLine = 1;
        g2d.drawString(text, 0, 0);
        while((text = br.readLine()) != null) {
            g2d.drawString(text, 0, fm.getAscent() * currentLine);
            currentLine++;
        }

        g2d.dispose();
        return img;
    }

    public BufferedImage convertColorTextToGraphic(Font font, BufferedReader br, int lines, Color background, ArrayList<Color> colorlist) throws IOException{
        String text = br.readLine();

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(text);
        int height = fm.getAscent();
        g2d.dispose();

        img = new BufferedImage(width, height*(lines-1), BufferedImage.TYPE_INT_ARGB);

        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(background);
        g2d.drawRect(0, 0, width, height*(lines-1));
        g2d.fillRect(0, 0, width, height*(lines-1));
        int charwidth = fm.getMaxAdvance();
        int colorcount = 0;
        for (int i = 0; i*height < height*(lines-1); i++) {
            for (int j = 0; j * charwidth < width; j++) {
                g2d.setColor(colorlist.get(colorcount));
                g2d.drawString(Character.toString(text.charAt(j)), j*charwidth, i*height);
                colorcount++;
            }
            text = br.readLine();
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