import java.awt.*;

/**
 * Created by Weston Ford on 2/12/2016.
 *
 * Imported code credited on location.
 *
 */

    //Resultant imagesize = (OriginalWidth/reduceBy * font.maxAdvance) x (OriginalHeight/reduceby * font.maxAscent)
    //Filesize = Width*Height * pixeldepth (pixeldepth = 32 in most cases)
    //NOTE! FS Dithering doesn't work if blocksize > 1. Well, it works, but it is undone by blocksize...
    //THIRD NOTE! reduceBy and blocksize accomplish identical tasks at different points during image production. reduce is first, blocksize is second.
    //COLOR TAGS ARE STD | NES | SNES | MONO
    //DITHER TAGS ARE FS | OR4
    //Also, you're going to have to change the file path in the ImageToText class for images to be found and stored correctly.
    //This program is the product of whimsy and as a result is a hacked together mess. Any organization is welcome.

    //some fontsizes produce messed up images. No idea why. 10, 20, and 30 function correclty.
    //Fontsize can also MASSIVELY INCREASE FILE SIZE AND PROCESSING TIME so it is recommended to start low and if
    //you do increase the fontsize make sure to increase the "reduceBy" value to compensate... or don't. I'm not your mother.


public class Driver {
    public static void main(String[] args){

        int simpleswitch = 1;
        String fileName = "deepforest1";
        String extension = ".jpg";
        int reduceBy = 4;
        ColorTag colortype = ColorTag.SNES;
        DitherTag dithertype = DitherTag.FS;
        //NOTE! If dithertype = DitherTag.OR4 then dithering always equals true
        boolean dithering = true;

        FType filetype;
        if (extension.compareTo(".gif") == 0)
            filetype = FType.GIF_TYPE;
        else
            filetype = FType.PNG_TYPE;

        ImageToText itt = new ImageToText(fileName, extension, filetype, colortype, dithertype);


        if(simpleswitch == 0) {
            int fontsize = 20;
            boolean transparency = false;
            Color background = Color.BLACK;

            int loop = 1;

            if (loop == 1) {
                itt.colorText(background, reduceBy, fontsize, transparency);
            }
            if (loop > 1) {
                while (loop < 4) {
                    itt.colorText(background, reduceBy, fontsize, transparency);
                    loop++;
                }
            }
        }
        else {
            int blockSize = 1;
            int expandSize = 4;
            itt.blockImageCreate(reduceBy, blockSize, expandSize, dithering);
        }
    }
}
