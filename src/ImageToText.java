import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by Weston Ford on 2/12/2016.
 */
public class ImageToText {
    String name;
    public ImageToText(String name){
        this.name = name;
    }

    public void gifMake(int count) {

        BufferedImage img = null;
        BufferedImage imgpass = null;
            try {
                imgpass = ImageIO.read(new File("C:\\Images\\gifs\\expanded\\" + name + "\\tmp-" + count + ".gif"));
            } catch (IOException e) {

            }
            File file = new File("C:\\Images\\gifs\\converted\\textform\\" + name + "\\" + name + "-" + count + ".txt");
            try {
                file.createNewFile();
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                boolean hasAlphaChannel = imgpass.getAlphaRaster() != null;
                int width = imgpass.getWidth();
                int height = imgpass.getHeight();

                img = scale(imgpass, width/4, height/4);

                width = img.getWidth();
                height = img.getHeight();

                int pixel;
                int r;
                int g;
                int b;
                int a;


                System.out.println("This is the width of ghost: " + img.getWidth());
                System.out.println("This is the height of ghost: " + img.getHeight());


                if (hasAlphaChannel) {
                    for (int i = 0; i < height; i++) {
                        for (int j = 0; j < width; j++) {
                            pixel = img.getRGB(j, i);

                            a = (pixel & 0xff000000) >>> 24;
                            r = (pixel & 0xff0000) >> 16;
                            g = (pixel & 0xff00) >> 8;
                            b = pixel & 0xff;

                            if (a > 122) {
                                if (b < 122 || g < 122 || r < 122) {
                                    bw.write("*");
                                }
                            } else
                                bw.write(" ");
                        }
                        bw.newLine();
                    }
                } else {
                    for (int i = 0; i < height; i++) {
                        for (int j = 0; j < width; j++) {
                            pixel = img.getRGB(j, i);
                            r = (pixel & 0xff0000) >> 16;
                            g = (pixel & 0xff00) >> 8;
                            b = pixel & 0xff;

                            //122/122/122 halfway point.
                            //Adjust if image is particularly light or dark.
                            if (b < 140 || g < 140 || r < 140) {
                                if (b < 100 || g < 100 || r < 100) {
                                    if (b > 60 || g > 60 || r > 60)
                                        bw.write("^");
                                    else
                                        bw.write("*");
                                } else
                                    bw.write("'");
                            } else
                                bw.write(" ");
                        }
                        bw.newLine();
                    }
                }
                bw.close();


                BufferedReader br = new BufferedReader(new FileReader("C:\\Images\\gifs\\converted\\textform\\" + name + "\\" + name + "-" + count + ".txt"));
            /*int linecount = height;
            String str = br.readLine();
            linecount++;
            String temp;
            while ((temp = br.readLine()) != null) {
                str.concat(temp + "\n");
                //System.out.println(temp);
                linecount++;
            }
            System.out.print(str);*/


                BufferedImage image = new TextToGraphicConverter().convertTextToGraphic(new Font("Courier New", Font.PLAIN, 10), br, height, width);
                //write BufferedImage to file
                ImageIO.write(image, "png", new File("C:\\Images\\gifs\\converted\\imageform\\" + name + "\\" + name + "-" + count + ".png"));


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            count++;
        }

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
    }

