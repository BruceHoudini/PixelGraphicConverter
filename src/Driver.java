/**
 * Created by Weston Ford on 2/12/2016.
 *
 * Imported code credited on location.
 *
 */
public class Driver {
    public static void main(String[] args){
        String fileName = "smokehands";
        int loop = 1;
       // ImageToText itt = new ImageToText(fileName);
        //itt.colorText();

        while (loop < 4) {
            ImageToText itt = new ImageToText("dragonball" + loop);
            itt.colorText();
            loop++;
        }
        //itt.gifMake();

    }
}
