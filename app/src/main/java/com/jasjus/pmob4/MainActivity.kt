package com.jasjus.pmob4

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.jasjus.pmob4.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbWarga: DatabaseWarga
    private lateinit var wargaDao: WargaDao
    private lateinit var appExecutors: AppExecutor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appExecutors = AppExecutor()
        dbWarga = DatabaseWarga.getDatabase(applicationContext)
        wargaDao = dbWarga.wargaDao()

        // Setup Spinner with a prompt
        val statusOptions = arrayOf("Pilih Status Pernikahan", "Belum Menikah", "Menikah", "Cerai")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = adapter

        binding.apply {
            btnSimpan.setOnClickListener {
                if (validateInput()) {
                    saveData()
                }
            }

            btnReset.setOnClickListener {
                showResetConfirmationDialog()
            }

            observeWargaList()
        }
    }

    private fun validateInput(): Boolean {
        with(binding) {
            val nama = etNama.text.toString().trim()
            val nik = etNik.text.toString().trim()
            val kabupaten = etKabupaten.text.toString().trim()
            val kecamatan = etKecamatan.text.toString().trim()
            val desa = etDesa.text.toString().trim()
            val rt = etRt.text.toString().trim()
            val rw = etRw.text.toString().trim()
            val selectedJenisKelaminId = rgJenisKelamin.checkedRadioButtonId
            val status = spinnerStatus.selectedItem.toString()

            if (nama.isEmpty() || nik.isEmpty() || kabupaten.isEmpty() || kecamatan.isEmpty() || desa.isEmpty() || rt.isEmpty() || rw.isEmpty()) {
                Toast.makeText(this@MainActivity, "Semua data harus diisi", Toast.LENGTH_SHORT).show()
                return false
            }

            if (selectedJenisKelaminId == -1) {
                Toast.makeText(this@MainActivity, "Silakan pilih jenis kelamin", Toast.LENGTH_SHORT).show()
                return false
            }

            if (status == "Pilih Status Pernikahan") {
                Toast.makeText(this@MainActivity, "Silakan pilih status pernikahan", Toast.LENGTH_SHORT).show()
                return false
            }

            if (nik.any { !it.isDigit() }) {
                Toast.makeText(this@MainActivity, "NIK harus berupa angka", Toast.LENGTH_SHORT).show()
                return false
            }

            if (rt.any { !it.isDigit() } || rw.any { !it.isDigit() }) {
                Toast.makeText(this@MainActivity, "RT dan RW harus berupa angka", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun saveData() {
        with(binding) {
            val nama = etNama.text.toString().trim()
            val nik = etNik.text.toString().trim()
            val kabupaten = etKabupaten.text.toString().trim()
            val kecamatan = etKecamatan.text.toString().trim()
            val desa = etDesa.text.toString().trim()
            val rt = etRt.text.toString().trim()
            val rw = etRw.text.toString().trim()
            val selectedJenisKelaminId = rgJenisKelamin.checkedRadioButtonId
            val jenisKelamin = findViewById<RadioButton>(selectedJenisKelaminId).text.toString()
            val status = spinnerStatus.selectedItem.toString()

            val newWarga = Warga(
                nama = nama,
                nik = nik,
                kabupaten = kabupaten,
                kecamatan = kecamatan,
                desa = desa,
                rt = rt,
                rw = rw,
                jenisKelamin = jenisKelamin,
                status = status
            )

            appExecutors.diskIO.execute {
                wargaDao.insert(newWarga)
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                    clearFields()
                }
            }
        }
    }

    private fun clearFields() {
        with(binding) {
            etNama.text.clear()
            etNik.text.clear()
            etKabupaten.text.clear()
            etKecamatan.text.clear()
            etDesa.text.clear()
            etRt.text.clear()
            etRw.text.clear()
            rgJenisKelamin.clearCheck()
            spinnerStatus.setSelection(0)
        }
    }

    private fun showResetConfirmationDialog() {
        AlertDialog.Builder(this@MainActivity).apply {
            setTitle("Reset Data")
            setMessage("Yakin ingin menghapus semua data?")
            setPositiveButton("Ya") { _, _ ->
                appExecutors.diskIO.execute {
                    wargaDao.deleteAll()
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Semua data telah direset", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            setNegativeButton("Batal", null)
        }.create().show()
    }

    private fun observeWargaList() {
        val wargaList: LiveData<List<Warga>> = wargaDao.getAllWarga()
        wargaList.observe(this@MainActivity, Observer { list ->
            val dataLengkapWargaList = list.mapIndexed { index, warga ->
                "${index + 1}. ${warga.nama} (${warga.jenisKelamin}) - ${warga.status}\n" +
                        "NIK: ${warga.nik}\n" +
                        "Alamat: RT ${warga.rt}/RW ${warga.rw}, ${warga.desa}, ${warga.kecamatan}, ${warga.kabupaten}"
            }

            binding.lvRoomDb.adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_list_item_1,
                dataLengkapWargaList
            )

            binding.lvRoomDb.setOnItemLongClickListener { _, _, position, _ ->
                val selectedWarga = list[position]
                showDeleteConfirmationDialog(selectedWarga)
                true
            }
        })
    }

    private fun showDeleteConfirmationDialog(warga: Warga) {
        AlertDialog.Builder(this@MainActivity).apply {
            setTitle("Hapus Data")
            setMessage("Yakin ingin menghapus ${warga.nama}?")
            setPositiveButton("Ya") { _, _ ->
                appExecutors.diskIO.execute {
                    wargaDao.delete(warga)
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Data dihapus", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            setNegativeButton("Batal", null)
        }.create().show()
    }
}
