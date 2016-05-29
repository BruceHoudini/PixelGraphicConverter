import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Weston Ford on 2/12/2016.
 */
public class BlockGraphicConverter {
    private Palette colorkit = new Palette();

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

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width*blockSize, height*blockSize);


        for (int i = 0; i*blockSize < height*blockSize; i++){
            for (int j = 0; j * blockSize < width * blockSize; j++){
                g2d.setColor(colors[j][i]);
                g2d.fillRect(j*blockSize, i*blockSize, blockSize, blockSize);
            }
        }
        g2d.dispose();
        return img;
    }

    public void orderedDither4x4(Color[][] colors, ColorTag tag, int width, int height){
        double r, g, b;
        int [][] bayermatrix = new int[][]{
                {1, 9, 3, 11},
                {13, 5, 15, 7},
                {4, 12, 2, 10},
                {16, 18, 14, 6}
            };
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                Color old = new Color(colors[j][i].getRGB());

                if(tag != ColorTag.MONO) {
                    r = old.getRed();
                    g = old.getGreen();
                    b = old.getBlue();

                    r += r * bayermatrix[j % 4][i % 4] * (1.0 / 17.0);
                    g += g * bayermatrix[j % 4][i % 4] * (1.0 / 17.0);
                    b += b * bayermatrix[j % 4][i % 4] * (1.0 / 17.0);

                    if (r > 255)
                        r = 255;
                    if (g > 255)
                        g = 255;
                    if (b > 255)
                        b = 255;

                    Color newp;

                    if (tag == ColorTag.NES)
                        newp = colorkit.colorNES((int) r, (int) g, (int) b);
                    else
                        newp = colorkit.color15Bit((int) r, (int) g, (int) b);

                    colors[j][i] = new Color(newp.getRGB());
                }
                if (tag == ColorTag.MONO){
                    r = old.getRed();
                    g = old.getGreen();
                    b = old.getBlue();

                    if (colorkit.getLuminance((int)r, (int)g, (int)b) > bayermatrix[j % 4][i % 4] * (1.0 / 17.0))
                        colors[j][i] = Color.WHITE;
                    else
                        colors[j][i] = Color.BLACK;
                }
            }
        }
    }

}
