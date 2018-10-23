package com.inkubator.radinaldn.dpac;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ImageView imageView;
    TextView tvname, tvkategori, tvdapil;
    View header;
    private ProgressDialog pDialog;
    WebView wvutama;
    String id_calon, nama, kategori, dapil, daerah, id_daerah;
    SharedPreferences sharedPreferences;
    AlertDialog alertDialog1;

    public static final String TAG_ID = "id_calon";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_KATEGORI= "kategori";
    public static final String TAG_DAPIL = "dapil";
    public static final String TAG_DAERAH= "daerah";
    public static final String TAG_IDDAERAH= "id_daerah";

    //get server connection
    Koneksi koneksi = new Koneksi();
    String service_api = koneksi.getUrl();
    String server_api = koneksi.getServer();
    ArrayAdapter arrayAdapter;
    final List<String> namaRuangan = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get session
        sharedPreferences = getSharedPreferences(LoginActivity.my_shared_preferences, Context.MODE_PRIVATE);

        id_calon = getIntent().getStringExtra(TAG_ID);
        nama = getIntent().getStringExtra(TAG_NAMA);
        kategori = getIntent().getStringExtra(TAG_KATEGORI);
        dapil= getIntent().getStringExtra(TAG_DAPIL);
        daerah= getIntent().getStringExtra(TAG_DAERAH);
        id_daerah= getIntent().getStringExtra(TAG_IDDAERAH);

        wvutama = (WebView) findViewById(R.id.wvutama);
        WebSettings settings = wvutama.getSettings();
        settings.setJavaScriptEnabled(true);
        wvutama.setWebViewClient(new WebViewClient());
        settings.setPluginState(WebSettings.PluginState.ON);

        // check if user kategori Provinsi or Kabkota
        if (kategori.equals("BCAD Provinsi")) {
            wvutama.loadUrl(server_api + "index.php/kategori/frontendkegiatan/" + id_calon);
        } else if (kategori.equals("BCAD Kabkota")) {
            wvutama.loadUrl(server_api + "index.php/kategori/frontendkegiatankabkota/" + id_calon);
        } else {
            Toast.makeText(getApplicationContext(), "Undefined kategori (" +kategori+ ")", Toast.LENGTH_SHORT).show();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        namaRuangan.add("Tambah Kegiatan");
        namaRuangan.add("Tambah Laporan CAD");
        arrayAdapter = new ArrayAdapter<String>(
                getApplicationContext(),
                android.R.layout.simple_list_item_single_choice,
                namaRuangan
        );

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Pilihan tambah data
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final AlertDialog.Builder confirmBox = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Pilih Aksi");
                builder.setSingleChoiceItems(arrayAdapter, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int item) {

                        // jika dipilih Tambah Kegiatan
                        if (item==0){
                            // check BCAD provinsi or BCAD kabkota
                            if (kategori.equals("BCAD Provinsi")) {
                                Intent i = new Intent(MainActivity.this, LaporKegiatanProvinsiActivity.class);
                                i.putExtra(TAG_ID, id_calon);
                                startActivity(i);
                            } else if (kategori.equals("BCAD Kabkota")){
                                Intent i = new Intent(MainActivity.this, LaporKegiatanKabkotaActivity.class);
                                i.putExtra(TAG_ID, id_calon);
                                i.putExtra(TAG_IDDAERAH, id_daerah);
                                startActivity(i);
                            } else {
                                Toast.makeText(getApplicationContext(), "Kategori akun anda (" +kategori+ ") tidak terdaftar ", Toast.LENGTH_SHORT).show();
                            }

                            // Jika dipilih tambah laporan
                        } else if (item==1){
                            Intent i = new Intent(MainActivity.this, LaporLaporanCADActivity.class);
                            i.putExtra(TAG_ID, id_calon);
                            i.putExtra(TAG_KATEGORI, kategori);
                            startActivity(i);
                        }

                        alertDialog1.dismiss();
                    }
                });
                alertDialog1 = builder.create();
                alertDialog1.show();

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);


        imageView = header.findViewById(R.id.imageView);
        tvname = header.findViewById(R.id.tvname);
        tvdapil = header.findViewById(R.id.tvdapil);
        tvkategori = header.findViewById(R.id.tvkategori);
        Picasso.with(getApplicationContext()).load(R.drawable.businessman).resize(100, 100).into(imageView);
        tvname.setText(nama);
        tvdapil.setText(" ("+dapil+")");
        if (kategori.equals("BCAD Kabkota")) {
            tvkategori.setText(kategori + " (" + daerah+")");
        } else {
            tvkategori.setText(kategori);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Handling for logout action
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(LoginActivity.session_status, false);
            editor.putString(TAG_ID, null);
            editor.putString(TAG_NAMA, null);
            editor.putString(TAG_KATEGORI, null);
            editor.putString(TAG_DAPIL, null);
            editor.putString(TAG_DAERAH, null);
            editor.putString(TAG_IDDAERAH, null);
            editor.commit();

            // redirect to login page
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            finish();
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.kegiatan) {
            // Handle the kegiatan action
            Intent i = new Intent(MainActivity.this, DaftarKegiatanProvinsiActivity.class);
            i.putExtra(TAG_ID, id_calon);
            i.putExtra(TAG_KATEGORI, kategori);
            startActivity(i);
        } else if (id == R.id.laporan) {
            Intent i = new Intent(MainActivity.this, DaftarLaporanActivity.class);
            i.putExtra(TAG_ID, id_calon);
            i.putExtra(TAG_KATEGORI, kategori);
            startActivity(i);
        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_about) {
            AlertDialog alertDialog = new AlertDialog.Builder(
                    MainActivity.this).create();

            // Setting Dialog Title
            alertDialog.setTitle(R.string.license_title);

            // Setting Dialog Message
            alertDialog.setMessage("BCAD v1.0\nCopyright ©WAR ROOM DPW PKS RIAU");

            // Setting Icon to Dialog
            alertDialog.setIcon(R.drawable.ic_logo);

            // Setting OK Button
//            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    // Write your code here to execute after dialog closed
//                    Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
//                }
//            });

            // Showing Alert Message
            alertDialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
