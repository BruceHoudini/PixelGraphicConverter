import java.awt.*;

/**
 * Created by Weston Ford on 2/12/2016.
 *
 * Imported code credited on location.
 *
 */
public class Driver {
    public static void main(String[] args){
        String fileName = "praiseit";
        int loop = 1;
        //Resultant imagesize = (OriginalWidth/reduceBy * font.maxAdvance) x (OriginalHeight/reduceby * font.maxAscent)
        int reduceBy = 8;
        if (loop == 1) {
            ImageToText itt = new ImageToText(fileName);
            itt.colorText(Color.WHITE, reduceBy);
        }
        if (loop > 1) {
            while (loop < 4) {
                ImageToText itt = new ImageToText(fileName + loop);
                itt.colorText(Color.BLACK, reduceBy);
                loop++;
            }
        }
    }
}
