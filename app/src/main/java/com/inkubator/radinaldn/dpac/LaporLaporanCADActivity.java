package com.inkubator.radinaldn.dpac;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LaporLaporanCADActivity extends AppCompatActivity{

    EditText etnamalaporan, etjmlpeserta, etket, ettanggal;
    ImageView ivImageCompress;
    DatePicker dp_tanggal;
    CheckBox cb_dp_tanggal;
    ImageButton bt_galeri;
    Button bt_kirim, bt_batal;
    Spinner sp_kabkota, sp_kec, sp_keldesa;


    //get server connection
    Koneksi lo_Koneksi = new Koneksi();
    String isi = lo_Koneksi.getUrl();
    String url_kabkota = isi+"lihat_kabkota.php";
    String url_kec= isi+"lihat_kec.php";
    String url_keldesa= isi+"lihat_keldesa.php";


    private String url = isi + "submit_laporan_cad.php";
    private static final String TAG = LaporLaporanCADActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    int success;
    String tag_json_obj = "json_obj_req";

    private String[] defaultKabkota= {
            "null",
    };

    private String[] defaultKec = {
            "-- Pilih Kabupaten/Kota dulu --",
    };

    private String[] defaultKeldesa = {
            "-- Pilih Kecamatan dulu --",
    };

    String strIdKabkota;

    ArrayList<String> namaKabkotaList;
    ArrayList<String> idKabkotaList;

    ArrayList<String> namaKecList;
    ArrayList<String> idKecList;

    ArrayList<String> namaKeldesaList;
    ArrayList<String> idKeldesaList;

    String id_calon, nama, id_kabkota, id_kec, id_keldesa, jumlah_peserta, tanggal, foto, keterangan;
    SharedPreferences sharedPreferences;

    public static final String TAG_ID = "id_calon";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_KABKOTA = "nama_kabkota";

    private ConnectionDetector cd;
    private Boolean upflag = false;
    private Uri selectedImage = null;
    private Bitmap bitmap, bitmapRotate;
    private ProgressDialog pDialog;
    String imagepath = "";
    String fname;
    File file;

    // Class Location
    LocationManager lm;
    LocationListener locationListener;
    String bestProvider;
    Criteria c = new Criteria();

    private GoogleMap mMap;

    //from upload file tutorial final
    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    public static final String IMAGE_DIRECTORY = "GaleriPascagub";
    private SimpleDateFormat dateFormatter;

    private File sourceFile;
    private File destFile;
    private Uri imageCaptureUri;
    Bitmap bmp;

    private static final String SCHEME_FILE = "file";
    private static final String SCHEME_CONTENT = "content";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //obj initiate
        cd = new ConnectionDetector(LaporLaporanCADActivity.this);
        cd = new ConnectionDetector(getApplicationContext());

        namaKabkotaList=  new ArrayList<>();
        idKabkotaList=  new ArrayList<>();

        namaKecList = new ArrayList<>();
        idKecList = new ArrayList<>();

        namaKeldesaList= new ArrayList<>();
        idKeldesaList= new ArrayList<>();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new GetAllKabKota().execute(url_kabkota);

            }
        });

        file = new File(Environment.getExternalStorageDirectory()
                + "/" + IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }

        dateFormatter = new SimpleDateFormat(
                DATE_FORMAT, Locale.US);
        showLayout();

    }

    private void showLayout() {
        setContentView(R.layout.activity_lapor_laporan_cad);
        etnamalaporan = findViewById(R.id.etnamalaporan);
        etjmlpeserta = findViewById(R.id.etjmlpeserta);
        etket = findViewById(R.id.etket);
        ettanggal = findViewById(R.id.et_tanggal);
        ivImageCompress = findViewById(R.id.ivImageCompress);
        dp_tanggal = findViewById(R.id.dp_tanggal);
        cb_dp_tanggal = findViewById(R.id.cb_dp_tanggal);
        bt_galeri = findViewById(R.id.btnGallery);
        sp_kabkota = findViewById(R.id.sp_kabkota);
        sp_kec = findViewById(R.id.sp_kec);
        sp_keldesa = findViewById(R.id.sp_keldesa);
        bt_batal = findViewById(R.id.btbatal);
        bt_kirim = findViewById(R.id.btkirim);

        // inisialisasi Array Adapter dengan memasukkan daftarTps
        final ArrayAdapter<String> adapterKabkota = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, defaultKabkota);
        final ArrayAdapter<String> adapterKec = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, defaultKec);
        final ArrayAdapter<String> adapterKeldesa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, defaultKeldesa);


        // set Array Adapter ke spinner
        sp_kabkota.setAdapter(adapterKabkota);
        sp_kec.setAdapter(adapterKec);
        sp_keldesa.setAdapter(adapterKeldesa);

        Calendar today = Calendar.getInstance();

        int month = (today.get(Calendar.MONTH)+1);

        String bulan = ((month < 10 ? "0" : "") +month);
        String tanggal = (today.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "") + today.get(Calendar.DAY_OF_MONTH);
        ettanggal.setText(today.get(Calendar.YEAR)+"-"+bulan+"-"+tanggal);

        dp_tanggal.init(today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        int month = (monthOfYear+1);

                        String tahun = String.valueOf(year);
                        String bulan = ((month< 10 ? "0" : "") +month);
                        String tanggal = (dayOfMonth < 10 ? "0" : "") + dayOfMonth;


                        ettanggal.setText(tahun+"-"+bulan+"-"+tanggal);
                        ettanggal.setEnabled(false);
                    }
                });

        sp_kabkota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strIdKabkota = sp_kabkota.getSelectedItem().toString();
//                         Toast.makeText(getApplicationContext(), "ID Kabkota : "+ idKabkotaList.get(i), Toast.LENGTH_SHORT).show();
                idKecList.clear();
                namaKecList.clear();
                if (i>0) {
                    idKeldesaList.clear();
                    namaKeldesaList.clear();
                    id_kabkota = idKabkotaList.get(i);
                    new GetAllKec().execute(url_kec + "?id_kabkota=" + idKabkotaList.get(i));
                    sp_kec.setAdapter(adapterKeldesa);
                }
//
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_kec.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(), "ID Kec: "+ idKecList.get(i), Toast.LENGTH_SHORT).show();
                idKeldesaList.clear();
                namaKeldesaList.clear();
                if (i>0) {
                    id_kec = idKecList.get(i);
                    new GetAllKelDesa().execute(url_keldesa + "?id_kec=" + idKecList.get(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_keldesa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i>0){
                    id_keldesa = idKeldesaList.get(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        bt_galeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickGaleri();
            }
        });

        bt_batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // refresh page
                ivImageCompress.setImageResource(android.R.color.transparent);
                ivImageCompress.setVisibility(view.GONE);
                fname = null;
                etnamalaporan.setText("");
                // replace with action default checked radio button kegiatan
//                etlokasikec.setText("");
//                etlokasikel.setText("");
                etjmlpeserta.setText("");
                etket.setText("");
            }
        });

        bt_kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kirimLaporan();
            }
        });

    }

    private void kirimLaporan() {
        // get session
        sharedPreferences = getSharedPreferences(LoginActivity.my_shared_preferences, Context.MODE_PRIVATE);
        id_calon = getIntent().getStringExtra(TAG_ID);

        String strnamalaporan = etnamalaporan.getText().toString();
        String strlokasikabkota = id_kabkota;
        String strlokasikec = id_kec;
        String strlokasikeldesa = id_keldesa;
        String strjmlpeserta = etjmlpeserta.getText().toString();
        String strket = etket.getText().toString();
        String strwaktu = ettanggal.getText().toString();
        String strfoto = fname;
        System.out.println("ID Calon : " + id_calon + " Nama laporan : " + strnamalaporan+ " Lokasi kabkota : " + strlokasikabkota+ " Lokasi kecamatan : " + strlokasikec + " Lokasi kelurahan : " + strlokasikeldesa + " Jumlah peserta : " + strjmlpeserta + " Tanggal : " + strwaktu + " Foto : " + strfoto + " Ket : " + strket);

        // pengecekkan form tidak boleh kosong
        if ((strnamalaporan).equals("") || (strnamalaporan.equals("")) || (strlokasikabkota == null) || (strlokasikec == null) || (strlokasikeldesa == null) || strjmlpeserta.equals("") || strfoto == null) {
            Toast.makeText(getApplicationContext(), "Form tidak boleh kosong", Toast.LENGTH_SHORT).show();
        } else {
            // upload gambar dilakukan saat tombol kirim ditekan
            if (cd.isConnectingToInternet()) {
                if (!upflag) {
                    Toast.makeText(getApplicationContext(), "Anda belum mengambil gambar..!", Toast.LENGTH_LONG).show();
                } else {
                    // lakukan upload gambar
                    saveFile(bitmapRotate, destFile);

                    // lakukan insert data kecelakaan
                    saveToServer(id_calon, strnamalaporan, strlokasikabkota, strlokasikec, strlokasikeldesa, strjmlpeserta, strwaktu, strfoto, strket);


                }
            } else {
                Toast.makeText(getApplicationContext(), "Tidak ada koneksi internet !", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void saveToServer(final String id_calon, final String nama_keg, final String lokasi_kabkota, final String lokasi_kec, final String lokasi_kel, final String jumlah_peserta, final String tanggal, final String foto, final String ket){
//        pDialog = new ProgressDialog(this);
//        pDialog.setCancelable(false);
//        pDialog.setMessage("Loading ...");
        //pDialog.show();
        //showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Submit Suara Response : "+response.toString());
                //  hideDialog();
//                pDialog.dismiss();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // check for error node in json
                    if(success == 1){
                        Toast.makeText(getApplicationContext(), "Laporan berhasil dikirim", Toast.LENGTH_LONG).show();
                        // Yeah sukses, koding aksi berikutnya di sini
                    } else if (success == 0){
                        //oh tidak seesuatu yang buruk terjadi
//                        Toast.makeText(getApplicationContext(),
//                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e){
                    //JSON Error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Submit Kegiatan Error: "+error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to submit suara url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_calon", id_calon);
                params.put("nama", nama_keg);
                params.put("id_kabkota", lokasi_kabkota);
                params.put("id_kec", lokasi_kec);
                params.put("id_keldesa", lokasi_kel);
                params.put("jumlah_peserta", jumlah_peserta);
                params.put("tanggal", tanggal);
                params.put("foto", foto);
                params.put("keterangan", ket);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    //    Saving file to the mobile internal memory
    private void saveFile(Bitmap sourceUri, File destination) {
        if (destination.exists()) destination.delete();

        try {
            FileOutputStream out = new FileOutputStream(destFile);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            if (cd.isConnectingToInternet()) {
                new DoFileUpload().execute();
            } else {
                Toast.makeText(getApplicationContext(), "Tidak ada koneksi internet..", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clickGaleri() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 102);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {
                case 102:
                    if (resultCode == Activity.RESULT_OK){
                        upflag = true;

                        Uri uriPhoto = data.getData();
                        Log.d(TAG + ".PICK_GALLERY_IMAGE", "Selected image uri path :" + uriPhoto.toString());

                        ivImageCompress.setVisibility(View.VISIBLE);
                        ivImageCompress.setImageURI(uriPhoto);

                        sourceFile = new File(getPathFromGooglePhotosUri(uriPhoto));


                        destFile = new File(file, "laporan_"
                                + dateFormatter.format(new Date()).toString() + ".png");

                        Log.d(TAG, "Source File Path :" + sourceFile);

                        try {
                            copyFile(sourceFile, destFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        bmp = decodeFile(destFile);
//                        try {
//
//                            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                            bitmap = getResizedBitmap(bmp, 50);
//                            //bmp = decodeFile(destFile);
//                            ivImageCompress.setVisibility(View.VISIBLE);
//                            ivImageCompress.setImageBitmap(bitmap);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // getting dropdown kabjota
    class GetAllKabKota extends AsyncTask<String, Integer, String>{

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(LaporLaporanCADActivity.this);
            pDialog.setMessage("Mohon menunggu, sedang mengambil data kabkota..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return readURL(params[0]);
        }

        @Override
        protected void onPostExecute(String content) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                int i;
                for (i = 0; i < jsonArray.length(); i++) {
                    JSONObject kabkotaObject = jsonArray.getJSONObject(i);
//                    arrayList.add(new Kabkota(
//                            kabkotaObject.getString("id_kabkota"),
//                            kabkotaObject.getString("nama")
//                    ));
                    if (i==0){
                        namaKabkotaList.add("Silahkan Pilih Kabupaten/Kota");
                        idKabkotaList.add("0");
                    }
                    namaKabkotaList.add(kabkotaObject.getString("nama"));
                    idKabkotaList.add(kabkotaObject.getString("id_kabkota"));
                    System.out.println("Data kabkota telah dimasukkan");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            CustomListAdapter adapter = new CustomListAdapter(
//                    getApplicationContext(), R.layout.custom_list_layout, arrayList
//            );
//            lv.setAdapter(adapter);

            sp_kabkota.setAdapter(new ArrayAdapter<>(LaporLaporanCADActivity.this, android.R.layout.simple_spinner_dropdown_item, namaKabkotaList));
        }
    }

    class GetAllKec extends AsyncTask<String, Integer, String>{

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(LaporLaporanCADActivity.this);
            pDialog.setMessage("Mohon menunggu, sedang mengambil data kec..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return readURL(params[0]);
        }

        @Override
        protected void onPostExecute(String content) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                int i;
                for (i = 0; i < jsonArray.length(); i++) {
                    JSONObject kabkotaObject = jsonArray.getJSONObject(i);
                    if (i==0){
                        namaKecList.add("Silahkan Pilih Kecamatan");
                        idKecList.add("0");
                    }
                    namaKecList.add(kabkotaObject.getString("nama"));
                    idKecList.add(kabkotaObject.getString("id_kec"));
                    System.out.println("Data kec "+kabkotaObject.getString("nama")+" dimasukkan");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            sp_kec.setAdapter(new ArrayAdapter<>(LaporLaporanCADActivity.this, android.R.layout.simple_spinner_dropdown_item, namaKecList));
        }
    }

    class GetAllKelDesa extends AsyncTask<String, Integer, String>{

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(LaporLaporanCADActivity.this);
            pDialog.setMessage("Mohon menunggu, sedang mengambil data keldesa..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return readURL(params[0]);
        }

        @Override
        protected void onPostExecute(String content) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                int i;
                for (i = 0; i < jsonArray.length(); i++) {
                    JSONObject kabkotaObject = jsonArray.getJSONObject(i);
                    if (i==0){
                        namaKeldesaList.add("Silahkan Pilih Kelurahan/Desa");
                        idKeldesaList.add("0");
                    }
                    namaKeldesaList.add(kabkotaObject.getString("nama"));
                    idKeldesaList.add(kabkotaObject.getString("id_keldesa"));
                    System.out.println("Data keldesa "+kabkotaObject.getString("nama")+" dimasukkan");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            sp_keldesa.setAdapter(new ArrayAdapter<>(LaporLaporanCADActivity.this, android.R.layout.simple_spinner_dropdown_item, namaKeldesaList));
        }
    }

    private static String readURL(String theUrl){
        StringBuilder content = new StringBuilder();
        try {
            // create url object
            URL url = new URL(theUrl);
            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();
            // wrap the urlconnection via the bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null){
                content.append(line + "\n");
            }
            bufferedReader.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return content.toString();
    }

    private Bitmap decodeFile(File f) {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int IMAGE_MAX_SIZE = 1024;
        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Width :" + b.getWidth() + " Height :" + b.getHeight());

        fname = "laporan_"
                + dateFormatter.format(new Date()).toString() + ".png";
        destFile = new File(file, fname);

        return b;



    }

    /**
     * This is useful when an image is not available in sdcard physically but it displays into photos application via google drive(Google Photos) and also for if image is available in sdcard physically.
     *
     * @param uriPhoto
     * @return
     */

    public String getPathFromGooglePhotosUri(Uri uriPhoto) {
        if (uriPhoto == null)
            return null;

        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uriPhoto, "r");
            FileDescriptor fd = pfd.getFileDescriptor();
            input = new FileInputStream(fd);

            String tempFilename = getTempFilename(this);
            output = new FileOutputStream(tempFilename);

            int read;
            byte[] bytes = new byte[4096];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }
            return tempFilename;
        } catch (IOException ignored) {
            // Nothing we can do
        } finally {
            closeSilently(input);
            closeSilently(output);
        }
        return null;
    }

    public static void closeSilently(Closeable c) {
        if (c == null)
            return;
        try {
            c.close();
        } catch (Throwable t) {
            // Do nothing
        }
    }

    private static String getTempFilename(Context context) throws IOException {
        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile("image", "tmp", outputDir);
        return outputFile.getAbsolutePath();
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }

    class DoFileUpload extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(LaporLaporanCADActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage("Mohon menunggu, sedang mengupload gambar..");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                // Set your file path here
                FileInputStream fstrm = new FileInputStream(destFile);
                // Set your server page url (and the file title/description)
                HttpFileUpload hfu = new HttpFileUpload(isi+"file_upload_laporan.php", "ftitle", "fdescription", fname);
                upflag = hfu.Send_Now(fstrm);
            } catch (FileNotFoundException e) {
                // Error: File not found
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
//            if (pDialog.isShowing()) {
//                pDialog.dismiss();
//            }
            if (upflag) {
                Toast.makeText(getApplicationContext(), "Upload gambar berhasil", Toast.LENGTH_LONG).show();
                // selesaikan activity
                finish();
                restartFirstActivity();
            } else {
                Toast.makeText(getApplicationContext(), "Sayangnya gambar tidak bisa diupload..", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void restartFirstActivity()
    {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName() );

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(i);
    }
}
