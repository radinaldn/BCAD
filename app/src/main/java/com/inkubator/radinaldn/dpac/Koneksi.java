package com.inkubator.radinaldn.dpac;

/**
 * Created by radinaldn on 10/02/18.
 */

public class Koneksi {
    public String getServer(){
//        String isi = "http://192.168.1.102/e-pemilu/wr-pksriau/";
        //String isi = "http://topaplikasi.com/";
        String isi = "http://wr-pksriau.id/";
        return isi;
    }

    public String getUrl()
    {
        //String isi = getServer()+"e-pemilu/API/"; //over online web
        String isi = getServer()+"API/"; //over online web
        return isi;
    }

    public String getImagesDir(){
        //String isi = getServer()+"e-pemilu/images/";
       String isi = getServer()+"images/";
        return isi;
    }

}
