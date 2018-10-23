package com.inkubator.radinaldn.dpac;

/**
 * Created by radinaldn on 19/09/18.
 */

public class LaporanCAD {
    String id_laporan;
    String nama;
    String nama_keldesa;
    String nama_kec;
    String nama_kabkota;
    String jumlah_peserta;
    String tanggal;
    String foto;

    public LaporanCAD(String id_laporan, String nama, String nama_keldesa, String nama_kec, String nama_kabkota, String jumlah_peserta, String tanggal, String foto) {
        this.id_laporan = id_laporan;
        this.nama = nama;
        this.nama_keldesa = nama_keldesa;
        this.nama_kec = nama_kec;
        this.nama_kabkota = nama_kabkota;
        this.jumlah_peserta = jumlah_peserta;
        this.tanggal = tanggal;
        this.foto = foto;
    }

    public String getId_laporan() {
        return id_laporan;
    }

    public void setId_laporan(String id_laporan) {
        this.id_laporan = id_laporan;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNama_keldesa() {
        return nama_keldesa;
    }

    public void setNama_keldesa(String nama_keldesa) {
        this.nama_keldesa = nama_keldesa;
    }

    public String getNama_kec() {
        return nama_kec;
    }

    public void setNama_kec(String nama_kec) {
        this.nama_kec = nama_kec;
    }

    public String getNama_kabkota() {
        return nama_kabkota;
    }

    public void setNama_kabkota(String nama_kabkota) {
        this.nama_kabkota = nama_kabkota;
    }

    public String getJumlah_peserta() {
        return jumlah_peserta;
    }

    public void setJumlah_peserta(String jumlah_peserta) {
        this.jumlah_peserta = jumlah_peserta;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
