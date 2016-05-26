import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.ArrayList;
import java.util.Timer;

/**
 * Created by Weston Ford on 2/12/2016.
 */
public class ImageToText {

    private String name;
    private String extension;
    private FType filetype;
    private ColorTag colortype;
    private ImageFrame[] inbuff;


    public ImageToText(String name, String extension, FType filetype, ColorTag colortype){
        this.name = name;
        this.extension = extension;
        this.filetype = filetype;
        this.colortype = colortype;

    }

    public void toText(){
        //Partitioned text function
        //Previously implemented but removed
        //Should be reimplemented here as its own method
    }
    /*@FunctionalInterface
    public void variableColorMethod{
        Color colorMethod(int r, int g, int b);
    }*/

    public void populateColorArray(Color[][] carray, BufferedImage im, int cwidth, int cdepth, int blockSize, boolean dithering){
        int pixel;
        int d = blockSize*blockSize;
        int r, g, b;
       // VariableColorInterface colorConvert;
       //colorConvert = (r1, g1, b1) -> ImageToText.color15Bit(r1, g1, b1);
        //if (colortype == ColorTag.NES)
        //    colorConvert.colorMethod = colorNES();
        int compare, match;
        for (int i = 0; i < cwidth; i++){
            r = 0;
            g = 0;
            b = 0;
            pixel = 0;
            compare = 0;
            match = 0;
            for (int j = cdepth*blockSize; j < cdepth*blockSize + blockSize; j++){
                for (int k = 0; k < blockSize; k++){
                    pixel = im.getRGB(i*blockSize + k, j);
                    r += (pixel & 0xff0000) >> 16;
                    g += (pixel & 0xff00) >> 8;
                    b += pixel & 0xff;
                }
            }
            if(!dithering) {
                if (colortype == ColorTag.NES)
                    carray[i][cdepth] = colorNES(r / d, g / d, b / d);
                if (colortype == ColorTag.SNES)
                    carray[i][cdepth] = color15Bit(r / d, g / d, b / d);
            }
            else
               carray[i][cdepth] = new Color(r / d, g / d, b / d);
        }
    }
    //Floyd-Steinberg Dithering algorithm
    public void ditherFS(BufferedImage img){
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
                    newpixel = colorNES(r, g, b).getRGB();
                else
                    newpixel = color15Bit(r, g, b).getRGB();
                rn = (newpixel & 0xff0000) >> 16;
                gn = (newpixel & 0xff00) >> 8;
                bn = (newpixel & 0xff);

                img.setRGB(j, i, new Color(rn, gn, bn).getRGB());

                quantr = r - rn;
                quantg = g - gn;
                quantb = b - bn;
                if(j < width-1) {
                    temppixel = img.getRGB(j + 1, i);
                    rn = (temppixel & 0xff0000) >> 16;
                    gn = (temppixel & 0xff00) >> 8;
                    bn = (temppixel & 0xff);
                    rn += quantr * (7 / 16);
                    gn += quantg * (7 / 16);
                    bn += quantg * (7 / 16);
                    img.setRGB(j + 1, i, new Color(rn, gn, bn).getRGB());
                }
                if(i < height - 1 && j > 1) {
                    temppixel = img.getRGB(j - 1, i + 1);
                    rn = (temppixel & 0xff0000) >> 16;
                    gn = (temppixel & 0xff00) >> 8;
                    bn = (temppixel & 0xff);
                    rn += quantr * (3 / 16);
                    gn += quantg * (3 / 16);
                    bn += quantg * (3 / 16);
                    img.setRGB(j - 1, i + 1, new Color(rn, gn, bn).getRGB());
                }
                if(i < height -1) {
                    temppixel = img.getRGB(j, i + 1);
                    rn = (temppixel & 0xff0000) >> 16;
                    gn = (temppixel & 0xff00) >> 8;
                    bn = (temppixel & 0xff);
                    rn += quantr * (5 / 16);
                    gn += quantg * (5 / 16);
                    bn += quantg * (5 / 16);
                    img.setRGB(j, i + 1, new Color(rn, gn, bn).getRGB());
                }
                if (j < width - 1 && i < height -1) {
                    temppixel = img.getRGB(j + 1, i + 1);
                    rn = (temppixel & 0xff0000) >> 16;
                    gn = (temppixel & 0xff00) >> 8;
                    bn = (temppixel & 0xff);
                    rn += quantr * (1 / 16);
                    gn += quantg * (1 / 16);
                    bn += quantg * (1 / 16);
                    img.setRGB(j + 1, i + 1, new Color(rn, gn, bn).getRGB());
                }
            }
        }
    }

    public void blockImageCreate(int reduceBy, int blockSize, int expandSize, boolean ditheringcheck){
        GifSequenceWriter writ = null;
        ImageOutputStream output = null;
        FileInputStream fis;
        BufferedImage img;
        BufferedImage imgpass;
        int count = 0;
        int frames = 1;

        try {
            if(filetype == FType.GIF_TYPE){
                output = new FileImageOutputStream(new File("C:\\Images\\gifs\\recombined\\" + name + "-" + colortype + ".gif"));
                fis = new FileInputStream(new File("C:\\Images\\gifs\\original\\" + name + ".gif"));
                inbuff = readGif(fis);
                writ = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, inbuff[1].getDelay(), Boolean.TRUE);
                frames = inbuff.length;
                System.out.println("Frame Count: " + inbuff.length);
            }
            Timer clock = new Timer();

            System.out.println("Beginning image scan.");
            long startTime = System.currentTimeMillis();
            while (count < frames) {

                if (filetype == FType.GIF_TYPE)
                    imgpass = inbuff[count].getImage();
                else
                    imgpass = ImageIO.read(new File("C:\\Images\\original\\" + name + extension));

                int width = imgpass.getWidth();
                int height = imgpass.getHeight();


                img = scale(imgpass, width / reduceBy, height / reduceBy);

                width = img.getWidth();
                height = img.getHeight();
                int bwidth = width/blockSize;
                int bheight = height/blockSize;


                Color[][] colorarray = new Color[bwidth][bheight];

                //System.out.println("THIS IS THE VALUE OF BWIDTH: " + bwidth);
                //System.out.println("THIS IS THE VALUE OF BHEIGHT: " + bheight);
                if (ditheringcheck)
                    ditherFS(img);
                for (int i = 0; i < bheight; i++)
                    populateColorArray (colorarray, img, bwidth, i, blockSize, ditheringcheck);

                BufferedImage image = new BlockGraphicConverter().convertToBlockGraphic(colorarray, bwidth, bheight, expandSize);

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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
                inbuff = readGif(fis);
                writ = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, inbuff[1].getDelay(), Boolean.TRUE);
                frames = inbuff.length;
                System.out.println("Frame Count: " + inbuff.length);
            }
            Timer clock = new Timer();

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


                img = scale(imgpass, width / reduceBy, height / reduceBy);

                width = img.getWidth();
                height = img.getHeight();

                int pixel;
                int r;
                int g;
                int b;

                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        pixel = img.getRGB(j, i);
                        r = (pixel & 0xff0000) >> 16;
                        g = (pixel & 0xff00) >> 8;
                        b = pixel & 0xff;
                        if (Math.abs(b-r) < 10 && Math.abs(b-g) < 10 && Math.abs(g-r) < 10){
                            if (b < 245 || r < 245 || g < 245) {
                                if (b < 200 || r < 200 || g < 200){
                                    if (b < 125 || r < 125 || g < 125) {
                                        if (b > 50 || r > 50 || g > 50)
                                            bw.write("^");
                                        else
                                            bw.write("*");
                                    }
                                    else
                                        bw.write("i");
                                } else
                                    bw.write("e");
                            } else
                                bw.write("#");
                        }
                        else if (r > g && r > b)
                            bw.write("%");
                        else if (g > r && g > b)
                            bw.write("$");
                        else if (b > g && b > r)
                            bw.write("&");
                        else
                            bw.write("?");
                        colorvals.add(new Color(r, g, b, 255));
                    }
                    bw.newLine();
                }
                bw.close();

                BufferedReader br = new BufferedReader(new StringReader(sw.toString()));
                BufferedImage image = new TextToGraphicConverter().convertColorTextToGraphic(new Font("Courier New", Font.BOLD, fontsize), br, height, background, colorvals);

                if (transparency)
                    makeBackTransparent(image, background);
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
                inbuff = readGif(fis);
                writ = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, inbuff[1].getDelay(), Boolean.TRUE);


                //framesToFile(inbuff);
                //scaledFramesToFile(inbuff);

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

                    img = scale(imgpass, width / reduceBy, height / reduceBy);

                    width = img.getWidth();
                    height = img.getHeight();

                    int pixel;
                    int r;
                    int g;
                    int b;
                    //int a;


                    //Alpha value handling is causing problems.
                    //Gif expansion with readGif() adds alpha value field.
                    //On images which do not have alpha values.
                    /*
                    if (width < 2) {
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
                    }*/
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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    /**
     * This scale method is not original code but I cannot for the life of me find its source.
     */
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
    private ImageFrame[] readGif(InputStream stream) throws IOException{
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
    public void framesToFile(ImageFrame[] imgframe){
        for (int i = 0; i < imgframe.length; i++) {
            try {
                ImageIO.write(imgframe[i].getImage(), "gif", new File("C:\\Images\\gifs\\converted\\imageform\\" + name + "\\" + name + "-test" + i + ".gif"));
            }
            catch(IOException ioe){
                ioe.printStackTrace();
            }
        }
    }
    public void scaledFramesToFile(ImageFrame[] imgframe){
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
    public Color colorNES(int r, int g, int b){
        int x = Palette.nescolors.length;
        double rn, gn, bn, low, compare;
        int match = 0;

        int pixel = Palette.nescolors[0];
        rn = (pixel & 0xff0000) >> 16;
        gn = (pixel & 0xff00) >> 8;
        bn = pixel & 0xff;
        low = Math.pow(Math.abs(rn - r), 4) + Math.pow(Math.abs(gn - g), 4) + Math.pow(Math.abs(bn - b), 4);

        for(int i = 1; i < x; i++) {
            pixel = Palette.nescolors[i];
            rn = (pixel & 0xff0000) >> 16;
            gn = (pixel & 0xff00) >> 8;
            bn = pixel & 0xff;

                //r*=1.0;

                //g*=1.0;

                //b*=1.0;
            compare = Math.pow(Math.abs(rn - r), 2) + Math.pow(Math.abs(gn - g), 2) + Math.pow(Math.abs(bn - b), 2);

            if (compare < low) {
                low = compare;
                match = i;
            }
        }
        return new Color(Palette.nescolors[match]);
    }

    public Color color15Bit(int r, int g, int b){
        int valr, valg, valb;
        valr = Math.round(r/32);
        if (valr == 8)
            valr = 255;
        else
            valr = valr*32;
        valg = Math.round(g/32);
        if (valg == 8)
            valg = 255;
        else
            valg = valg*32;
        valb = Math.round(b/32);
        if (valb == 8)
            valb = 255;
        else
            valb = valb*32;
        return new Color(valr, valg, valb);
    }
}

