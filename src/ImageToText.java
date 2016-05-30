import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;


/**
 * Created by Weston Ford on 2/12/2016.
 *
 */
public class ImageToText {

    private String name;
    private String extension;
    private FType filetype;
    private ColorTag colortype;
    private DitherTag dithertype;
    private ImageFrame[] inbuff;
    private GifToolKit toolkit = new GifToolKit();
    private Palette colorkit = new Palette();


    public ImageToText(String name, String extension, FType filetype, ColorTag colortype, DitherTag dithertype){
        this.name = name;
        this.extension = extension;
        this.filetype = filetype;
        this.colortype = colortype;
        this.dithertype = dithertype;
    }

    public void populateColorArray(Color[][] carray, BufferedImage im, int cwidth, int cdepth, int blockSize, boolean dithering){
        int pixel;
        int d = blockSize*blockSize;
        int r, g, b;

        for (int i = 0; i < cwidth; i++){
            r = 0;
            g = 0;
            b = 0;
            for (int j = cdepth*blockSize; j < cdepth*blockSize + blockSize; j++){
                for (int k = 0; k < blockSize; k++){
                    pixel = im.getRGB(i*blockSize + k, j);
                    r += (pixel & 0xff0000) >> 16;
                    g += (pixel & 0xff00) >> 8;
                    b += pixel & 0xff;
                }
            }
            if(dithering == false) {
                if (colortype == ColorTag.NES)
                    carray[i][cdepth] = colorkit.colorNES(r / d, g / d, b / d);
                else if (colortype == ColorTag.SNES)
                    carray[i][cdepth] = colorkit.color15Bit(r / d, g / d, b / d);
                else if (colortype == ColorTag.MONO)
                    carray[i][cdepth] = colorkit.color2Bit(r / d, g / d, b / d);
                else
                    carray[i][cdepth] = new Color(r / d, g / d, b / d);
            }
            else {
                if (blockSize == 1)
                    carray[i][cdepth] = new Color(r, g, b);
                else
                    carray[i][cdepth] = new Color(r / d, g / d, b / d);
            }
        }
    }
    //Floyd-Steinberg Dithering algorithm
    public BufferedImage ditherFS(BufferedImage img){
        int width = img.getWidth();
        int height = img.getHeight();
        int oldpixel, newpixel, temppixel, r, g, b, rn, gn, bn;
        double quantr, quantg, quantb;
        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                oldpixel = img.getRGB(j, i);
                r = (oldpixel & 0xff0000) >> 16;
                g = (oldpixel & 0xff00) >> 8;
                b = (oldpixel & 0xff);
                if(colortype == ColorTag.NES)
                    newpixel = colorkit.colorNES(r, g, b).getRGB();
                else if(colortype == ColorTag.SNES)
                    newpixel = colorkit.color15Bit(r, g, b).getRGB();
                else if(colortype == ColorTag.MONO)
                    newpixel = colorkit.color2Bit(r, g, b).getRGB();
                else
                    newpixel = new Color(r, g, b).getRGB();

                rn = (newpixel & 0xff0000) >> 16;
                gn = (newpixel & 0xff00) >> 8;
                bn = (newpixel & 0xff);

                img.setRGB(j, i, new Color(rn, gn, bn).getRGB());

                    quantr = r - rn;
                    quantg = g - gn;
                    quantb = b - bn;



                //System.out.println("quantr = " + quantr + " quantg = " + quantg + "quantb = " + quantb);
                if(j < width-1) {
                    temppixel = img.getRGB(j + 1, i);
                    rn = (temppixel & 0xff0000) >> 16;
                    gn = (temppixel & 0xff00) >> 8;
                    bn = (temppixel & 0xff);
                    rn += quantr * (7.0 / 16.0);
                    gn += quantg * (7.0 / 16.0);
                    bn += quantb * (7.0 / 16.0);
                    if (rn < 0)
                        rn = 0;
                    else if (rn > 255)
                        rn = 0;
                    if (gn < 0)
                        gn = 0;
                    else if (gn > 255)
                        gn = 255;
                    if (bn < 0)
                        bn = 0;
                    else if (bn > 255)
                        bn = 255;
                    img.setRGB(j + 1, i, new Color(rn, gn, bn).getRGB());
                }
                if(i < height - 1 && j > 1) {
                    temppixel = img.getRGB(j - 1, i + 1);
                    rn = (temppixel & 0xff0000) >> 16;
                    gn = (temppixel & 0xff00) >> 8;
                    bn = (temppixel & 0xff);
                    rn += quantr * (3.0 / 16.0);
                    gn += quantg * (3.0 / 16.0);
                    bn += quantb * (3.0 / 16.0);
                    if (rn < 0)
                        rn = 0;
                    else if (rn > 255)
                        rn = 0;
                    if (gn < 0)
                        gn = 0;
                    else if (gn > 255)
                        gn = 255;
                    if (bn < 0)
                        bn = 0;
                    else if (bn > 255)
                        bn = 255;
                    img.setRGB(j - 1, i + 1, new Color(rn, gn, bn).getRGB());
                }
                if(i < height -1) {
                    temppixel = img.getRGB(j, i + 1);
                    rn = (temppixel & 0xff0000) >> 16;
                    gn = (temppixel & 0xff00) >> 8;
                    bn = (temppixel & 0xff);
                    rn += quantr * (5.0 / 16.0);
                    gn += quantg * (5.0 / 16.0);
                    bn += quantb * (5.0 / 16.0);
                    if (rn < 0)
                        rn = 0;
                    else if (rn > 255)
                        rn = 0;
                    if (gn < 0)
                        gn = 0;
                    else if (gn > 255)
                        gn = 255;
                    if (bn < 0)
                        bn = 0;
                    else if (bn > 255)
                        bn = 255;
                    img.setRGB(j, i + 1, new Color(rn, gn, bn).getRGB());
                }
                if (j < width - 1 && i < height -1) {
                    temppixel = img.getRGB(j + 1, i + 1);
                    rn = (temppixel & 0xff0000) >> 16;
                    gn = (temppixel & 0xff00) >> 8;
                    bn = (temppixel & 0xff);
                    rn += quantr * (1.0 / 16.0);
                    gn += quantg * (1.0 / 16.0);
                    bn += quantb * (1.0 / 16.0);
                    if (rn < 0)
                        rn = 0;
                    else if (rn > 255)
                        rn = 255;
                    if (gn < 0)
                        gn = 0;
                    else if (gn > 255)
                        gn = 255;
                    if (bn < 0)
                        bn = 0;
                    else if (bn > 255)
                        bn = 255;
                    img.setRGB(j + 1, i + 1, new Color(rn, gn, bn).getRGB());
                }
            }
        }
        return img;
    }

    public void blockImageCreate(int reduceBy, int blockSize, int expandSize, boolean ditheringcheck){
        GifSequenceWriter writ = null;
        ImageOutputStream output = null;
        FileInputStream fis;
        BufferedImage img;
        BufferedImage imgpass;
        BlockGraphicConverter converter = new BlockGraphicConverter();
        int count = 0;
        int frames = 1;

        try {
            if(filetype == FType.GIF_TYPE){
                output = new FileImageOutputStream(new File("C:\\Images\\gifs\\recombined\\" + name + "-" + colortype + ".gif"));
                fis = new FileInputStream(new File("C:\\Images\\gifs\\original\\" + name + ".gif"));
                inbuff = toolkit.readGif(fis);
                writ = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, inbuff[1].getDelay(), Boolean.TRUE);
                frames = inbuff.length;
                System.out.println("Frame Count: " + inbuff.length);
            }

            System.out.println("Beginning image scan.");
            long startTime = System.currentTimeMillis();
            while (count < frames) {

                if (filetype == FType.GIF_TYPE)
                    imgpass = inbuff[count].getImage();
                else
                    imgpass = ImageIO.read(new File("C:\\Images\\original\\" + name + extension));

                int width = imgpass.getWidth();
                int height = imgpass.getHeight();


                img = toolkit.scale(imgpass, width / reduceBy, height / reduceBy);

                width = img.getWidth();
                height = img.getHeight();
                int bwidth = width/blockSize;
                int bheight = height/blockSize;


                Color[][] colorarray = new Color[bwidth][bheight];

                //System.out.println("THIS IS THE VALUE OF BWIDTH: " + bwidth);
                //System.out.println("THIS IS THE VALUE OF BHEIGHT: " + bheight);
                if (ditheringcheck) {
                    if (dithertype == DitherTag.FS)
                        img = ditherFS(img);
                }
                for (int i = 0; i < bheight; i++)
                    populateColorArray (colorarray, img, bwidth, i, blockSize, ditheringcheck);
                if (dithertype == DitherTag.OR4)
                    converter.orderedDither4x4(colorarray, colortype, bwidth, bheight);

                BufferedImage image = converter.convertToBlockGraphic(colorarray, bwidth, bheight, expandSize);

                if (filetype == FType.GIF_TYPE)
                    writ.writeToSequence(image);
                else
                    ImageIO.write(image, "png", new File("C:\\Images\\png\\converted\\" + name + "-" + colortype + ".png"));

                count++;
            }
            if(filetype == FType.GIF_TYPE) {
                writ.close();
                output.close();
            }
            long endtime = System.currentTimeMillis();
            double tottime = endtime - startTime;
            System.out.println("Process Complete.");
            System.out.println("Total elapsed time: " + tottime/1000 + "s");

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("TRYING TO CATCH LEAK");
    }

    public void colorText(Color background, int reduceBy, int fontsize, boolean transparency){
        GifSequenceWriter writ = null;
        ImageOutputStream output = null;
        FileInputStream fis;
        BufferedImage img;
        BufferedImage imgpass;
        int count = 0;
        int frames = 1;

        try {
            if(filetype == FType.GIF_TYPE){
                output = new FileImageOutputStream(new File("C:\\Images\\gifs\\recombined\\" + name + ".gif"));
                fis = new FileInputStream(new File("C:\\Images\\gifs\\original\\" + name + ".gif"));
                inbuff = toolkit.readGif(fis);
                writ = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, inbuff[1].getDelay(), Boolean.TRUE);
                frames = inbuff.length;
                System.out.println("Frame Count: " + inbuff.length);
            }

            System.out.println("Beginning image scan.");
            long startTime = System.currentTimeMillis();
            while (count < frames) {
                ArrayList<Color> colorvals = new ArrayList<>();
                colorvals.clear();

                if (filetype == FType.GIF_TYPE)
                    imgpass = inbuff[count].getImage();
                else
                    imgpass = ImageIO.read(new File("C:\\Images\\original\\" + name + extension));

                StringWriter sw = new StringWriter();
                BufferedWriter bw = new BufferedWriter(sw);

                int width = imgpass.getWidth();
                int height = imgpass.getHeight();


                img = toolkit.scale(imgpass, width / reduceBy, height / reduceBy);

                width = img.getWidth();
                height = img.getHeight();

                int pixel;
                int r;
                int g;
                int b;
                int rand=0;

                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        pixel = img.getRGB(j, i);
                        r = (pixel & 0xff0000) >> 16;
                        g = (pixel & 0xff00) >> 8;
                        b = pixel & 0xff;
                        //Change multiplied by value to increase or decrease random character occurence
                        //rand = (int)(Math.random() * 1000);

                        if (Math.abs(b-r) < 10 && Math.abs(b-g) < 10 && Math.abs(g-r) < 10){
                            if (b < 245 || r < 245 || g < 245) {
                                if (b < 200 || r < 200 || g < 200){
                                    if (b < 125 || r < 125 || g < 125) {
                                        if (b > 50 || r > 50 || g > 50)
                                            bw.write("o");
                                        else
                                            bw.write("*");
                                    }
                                    else
                                        bw.write("n");
                                } else
                                    bw.write("e");
                            } else
                                bw.write("#");
                        }
                        else if (r > g && r > b)
                            if(rand < 123 && rand > 96)
                                bw.write(rand);
                            else
                                bw.write("%");
                        else if (g > r && g > b)
                            if(rand < 123 && rand > 96)
                                bw.write(rand);
                            else
                                bw.write("$");
                        else if (b > g && b > r)
                            if(rand < 123 && rand > 96)
                                bw.write(rand);
                            else
                                bw.write("&");
                        else
                            if(rand < 123 && rand > 96)
                                bw.write(rand);
                            else
                                bw.write("?");
                        if (background == Color.BLACK)
                            colorvals.add(new Color(r, g, b, 255));
                        else
                            colorvals.add(new Color(r, g, b, 255));
                    }
                    bw.newLine();
                }
                bw.close();

                BufferedReader br = new BufferedReader(new StringReader(sw.toString()));
                BufferedImage image = new TextToGraphicConverter().convertColorTextToGraphic(new Font("Courier New", Font.BOLD, fontsize), br, height, background, colorvals);

                if (transparency)
                    toolkit.makeBackTransparent(image, background);
                if (filetype == FType.GIF_TYPE)
                    writ.writeToSequence(image);
                else
                    ImageIO.write(image, "png", new File("C:\\Images\\png\\converted\\" + name + ".png"));

                count++;
                br.close();
            }
            if(filetype == FType.GIF_TYPE) {
                writ.close();
                output.close();
            }
            long endtime = System.currentTimeMillis();
            double tottime = endtime - startTime;
            System.out.println("Process Complete.");
            System.out.println("Total elapsed time: " + tottime/1000 + "s");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void gifMake(Color background, int reduceBy) {
        GifSequenceWriter writ;
        BufferedImage img;
        BufferedImage imgpass;
        int count = 0;

            try {
                ImageOutputStream output = new FileImageOutputStream(new File("C:\\Images\\gifs\\recombined\\" + name + ".gif"));
                FileInputStream fis = new FileInputStream(new File("C:\\Images\\gifs\\original\\" + name + ".gif"));
                inbuff = toolkit.readGif(fis);
                writ = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, inbuff[1].getDelay(), Boolean.TRUE);


                //framesToFile(inbuff);
                //toolkit.scaledFramesToFile(inbuff);

                //imgpass = ImageIO.read(new File("C:\\Images\\gifs\\expanded\\" + name + "\\tmp-" + count + ".gif"));
                //File file = new File("C:\\Images\\gifs\\converted\\textform\\" + name + "\\" + name + "-" + i + ".txt");

                System.out.println("This is the length of inbuff: " + inbuff.length);

                while (count < inbuff.length) {
                    //File file = new File("C:\\Images\\gifs\\converted\\textform\\" + name + "\\" + name + "-" + count + ".txt");
                    imgpass = inbuff[count].getImage();

                    //file.createNewFile();
                    //FileWriter fw = new FileWriter(file);
                    StringWriter sw = new StringWriter();
                    BufferedWriter bw = new BufferedWriter(sw);
                    //boolean hasAlphaChannel = imgpass.getAlphaRaster() != null;
                    int width = imgpass.getWidth();
                    int height = imgpass.getHeight();

                    //img = imgpass;

                    img = toolkit.scale(imgpass, width / reduceBy, height / reduceBy);

                    width = img.getWidth();
                    height = img.getHeight();

                    int pixel;
                    int r;
                    int g;
                    int b;
                    //int a;


                    //Alpha value handling is causing problems.
                    //Gif expansion with toolkit.readGif() adds alpha value field.
                    //On images which do not have alpha values.

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

                    bw.close();


                    //BufferedReader br = new BufferedReader(new FileReader("C:\\Images\\gifs\\converted\\textform\\" + name + "\\" + name + "-" + count + ".txt"));
                    BufferedReader br = new BufferedReader(new StringReader(sw.toString()));
                    BufferedImage image = new TextToGraphicConverter().convertTextToGraphic(new Font("Courier New", Font.PLAIN, 10), br, height, background);
                    //write BufferedImage to file
                    //outbuff[count]= new ImageFrame(image, inbuff[count].getDelay(), inbuff[count].getDisposal(), image.getWidth(), image.getHeight());
                    writ.writeToSequence(image);
                    //ImageIO.write(image, "png", new File("C:\\Images\\gifs\\converted\\imageform\\" + name + "\\" + name + "-" + count + ".png"));

                    count++;
                    br.close();
                }
                writ.close();
                output.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
}

