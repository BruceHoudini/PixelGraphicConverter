import java.awt.*;

/**
 * Created by Weston Ford on 2/12/2016.
 *
 * Imported code credited on location.
 *
 */

    //Resultant imagesize = (OriginalWidth/reduceBy * font.maxAdvance) x (OriginalHeight/reduceby * font.maxAscent)
    //Filesize = Width*Height * pixeldepth (This will be 32 in most cases)
    //NOTE! Dithering doesn't work if blocksize > 1.
    //SECOND NOTE! Floyd-Steinberg Dithering doesn't produce any noticeable differences anyways so it doesn't matter.
    //THIRD NOTE! reduceBy and blocksize accomplish identical tasks at different points during image production.
    //COLOR TAGS ARE STD | NES | SNES.
    //Also, you're going to have to change the file path in the ImageToText class for images to be found and stored correctly.
    //This program is the product of whimsy and as a result is a hacked together mess. Any organization is welcome.



public class Driver {
    public static void main(String[] args){

        int simpleswitch = 1;
        String fileName = "rain2";
        String extension = ".gif";
        int reduceBy = 1;
        ColorTag colortype = ColorTag.NES;
        boolean dithering = true;

        FType filetype;
        if (extension.compareTo(".gif") == 0)
            filetype = FType.GIF_TYPE;
        else
            filetype = FType.PNG_TYPE;


        if(simpleswitch == 0) {
            int fontsize = 20;
            boolean transparency = false;
            Color background = Color.BLACK;

            int loop = 1;


            if (loop == 1) {
                ImageToText itt = new ImageToText(fileName, extension, filetype, colortype);
                itt.colorText(background, reduceBy, fontsize, transparency);
            }
            if (loop > 1) {
                while (loop < 4) {
                    ImageToText itt = new ImageToText(fileName + loop, extension, filetype, colortype);
                    itt.colorText(background, reduceBy, fontsize, transparency);
                    loop++;
                }
            }
        }
        else {
            int blockSize = 1;
            int expandSize = 2;
            ImageToText itt = new ImageToText(fileName, extension, filetype, colortype);
            itt.blockImageCreate(reduceBy, blockSize, expandSize, dithering);
        }
    }
}
