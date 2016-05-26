import java.awt.*;

/**
 * Created by Weston Ford on 2/12/2016.
 */
public class Palette {
    public Color colorNES(int r, int g, int b){
        int x = nescolors.length;
        double rn, gn, bn, low, compare;
        int match = 0;

        int pixel = nescolors[0];
        rn = (pixel & 0xff0000) >> 16;
        gn = (pixel & 0xff00) >> 8;
        bn = pixel & 0xff;
        low = Math.sqrt(Math.pow(rn - r, 4) + Math.pow(gn - g, 4) + Math.pow(bn - b, 4));

        for(int i = 1; i < x; i++) {
            pixel = nescolors[i];
            rn = (pixel & 0xff0000) >> 16;
            gn = (pixel & 0xff00) >> 8;
            bn = pixel & 0xff;

            compare = Math.sqrt(Math.pow(rn - r, 2) + Math.pow(gn - g, 2) + Math.pow(bn - b, 2));

            if (compare < low) {
                low = compare;
                match = i;
            }
        }
        return new Color(nescolors[match]);
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
    public int[] nescolors =
            {0x7C7C7C,
            0x0000FC,
            0x0000BC,
            0x4428BC,
            0x940084,
            0xA80020,
            0xA81000,
            0x881400,
            0x503000,
            0x007800,
            0x006800,
            0x005800,
            0x004058,
            //0x000000,
            //0x000000,
            0x000000,
            0xBCBCBC,
            0x0078F8,
            0x0058F8,
            0x6844FC,
            0xD800CC,
            0xE40058,
            0xF83800,
            0xE45C10,
            0xAC7C00,
            0x00B800,
            0x00A800,
            0x00A844,
            0x008888,
            //0x000000,
            //0x000000,
            //0x000000,
            0xF8F8F8,
            0x3CBCFC,
            0x6888FC,
            0x9878F8,
            0xF878F8,
            0xF85898,
            0xF87858,
            0xFCA044,
            0xF8B800,
            0xB8F818,
            0x58D854,
            0x58F898,
            0x00E8D8,
            0x787878,
            //0x000000,
            //0x000000,
                    0xFCFCFC,
            0xA4E4FC,
            0xB8B8F8,
            0xD8B8F8,
            0xF8B8F8,
            0xF8A4C0,
            0xF0D0B0,
            0xFCE0A8,
            0xF8D878,
            0xD8F878,
            0xB8F8B8,
            0xB8F8D8,
            0x00FCFC,
            0xF8D8F8,
            //0x000000,
            0x000000};
}
