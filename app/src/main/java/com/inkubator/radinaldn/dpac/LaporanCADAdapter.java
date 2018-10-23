package com.inkubator.radinaldn.dpac;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by radinaldn on 19/09/18.
 */

public class LaporanCADAdapter extends ArrayAdapter<LaporanCAD> {

    //get server connection
    Koneksi koneksi = new Koneksi();
    String image_dir = koneksi.getImagesDir();

    ArrayList<LaporanCAD> laporanCADS;
    Context context;
    int resource;
    public LaporanCADAdapter(Context context, int resource, ArrayList<LaporanCAD> laporanCADS) {
        super(context, resource, laporanCADS);
        this.laporanCADS= laporanCADS;
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.custom_list_layout, null, true);
        }
        LaporanCAD laporanCADs = getItem(position);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewKegiatan);
        Picasso.with(context).load(image_dir+"laporan/"+laporanCADs.getFoto()).into(imageView);

        TextView tvnamakeg = (TextView) convertView.findViewById(R.id.tvnamakeg);
        tvnamakeg.setText(laporanCADs.getNama());

        TextView tvtanggalkeg = (TextView) convertView.findViewById(R.id.tvtanggalkeg);
        tvtanggalkeg.setText(laporanCADs.getTanggal());

        TextView tvjeniskeg = (TextView) convertView.findViewById(R.id.tvjeniskeg);
        tvjeniskeg.setText(laporanCADs.getJumlah_peserta()+" Peserta");

        TextView tvnamakeldesa = (TextView) convertView.findViewById(R.id.tvkelurahan);
        tvnamakeldesa.setText(laporanCADs.getNama_keldesa());

        return convertView;
    }
}
