import java.awt.*;

/**
 * Created by Weston Ford on 2/12/2016.
 *
 * Imported code credited on location.
 *
 */

//Resultant imagesize = (OriginalWidth/reduceBy * font.maxAdvance) x (OriginalHeight/reduceby * font.maxAscent)

public class Driver {
    public static void main(String[] args){

        int simpleswitch = 1;
        String fileName = "ripples";
        String extension = ".gif";
        int reduceBy = 2;
        ColorTag colortype = ColorTag.SNES;
        boolean dithering = true;

        FType filetype;
        if (extension.compareTo(".gif") == 0)
            filetype = FType.GIF_TYPE;
        else
            filetype = FType.PNG_TYPE;


        if(simpleswitch == 0) {
            int fontsize = 20;
            boolean transparency = true;
            Color background = Color.WHITE;

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
