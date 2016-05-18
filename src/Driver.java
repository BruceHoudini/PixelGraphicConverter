/**
 * Created by Weston Ford on 2/12/2016.
 */
public class Driver {
    public static void main(String[] args){
        String fileName = "ripple";
        int total = 100;
        int count = 0;
        ImageToText itt = new ImageToText(fileName);
        while (count < total){
            itt.gifMake(count);
            count++;
        }

    }
}
