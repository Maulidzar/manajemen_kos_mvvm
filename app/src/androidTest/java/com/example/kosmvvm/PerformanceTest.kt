package com.example.kosmvvm

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.StaleObjectException
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.io.File
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class PerformanceTest {
    private lateinit var device: UiDevice
    private val targetPackage = "com.example.kosmvvm"
    private val timeout = 5000L

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(targetPackage)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(targetPackage).depth(0)), timeout)
        sleep(2000)
    }

    @Test
    fun A_testSkenarioTambahDanHapus30Kali() {
        Log.d("ROBOT_TEST", "=== MULAI TEST A: TAMBAH & HAPUS ===")
        val csvFileNameTambahHapus = "TambahHapus_MVVM_30x_${System.currentTimeMillis()}.csv"
        repeat(30) { i ->
            val iterasi = i + 1
            Log.d("ROBOT_TEST", "--- Iterasi $iterasi ---")
            val btnTambah = device.wait(Until.findObject(By.res(targetPackage, "btn_tambah_kamar")), 5000)
            if (btnTambah == null) return
            btnTambah.click()
            val etNomor = device.wait(Until.findObject(By.res(targetPackage, "etNomorKamar")), 3000)
            if (etNomor == null) return
            etNomor.text = "$iterasi"
            val cbTerisi = device.wait(Until.findObject(By.res(targetPackage, "cb_terisi")), 3000)
            cbTerisi?.click()
            sleep(500)
            val btnDate = device.wait(Until.findObject(By.res(targetPackage, "btn_tanggal_bayar")), 3000)
            if (btnDate != null) {
                btnDate.click()
                device.wait(Until.findObject(By.text("OK")), 3000)?.click()
            }
            sleep(500)
            device.wait(Until.findObject(By.res(targetPackage, "etNamaPenghuni")), 3000)?.text = "Arya Maulidzar Syahriza Putra"
            device.wait(Until.findObject(By.res(targetPackage, "etNikPenghuni")), 3000)?.text = "1234567890"
            device.wait(Until.findObject(By.res(targetPackage, "etAlamatAsal")), 3000)?.text = "Batam, Kepulauan Riau"
            sleep(1000)
            var isSaveSuccess = false
            var attemptSave = 0
            while (!isSaveSuccess && attemptSave < 3) {
                try {
                    val btnSimpan = device.wait(Until.findObject(By.res(targetPackage, "btnSave")), 3000)
                    if (btnSimpan != null) {
                        btnSimpan.click()
                        val dialogSimpan = device.wait(Until.findObject(By.text("Simpan")), 3000)
                        if (dialogSimpan != null) {
                            val startTime = System.nanoTime()
                            try {
                                dialogSimpan.click()
                            } catch (e: StaleObjectException) {
                                device.wait(Until.findObject(By.text("Simpan")), 1000)?.click()
                            }
                            val isFinished = device.wait(Until.hasObject(By.res(targetPackage, "rv_kamar")), 5000)
                            val endTime = System.nanoTime()
                            if (isFinished) {
                                val durationMs = (endTime - startTime) / 1_000_000
                                sleep(2000)
                                val ramKb = getMemoryUsage()
                                val cpuPercent = getCpuUsage()
                                writeToCsv(csvFileNameTambahHapus, "$iterasi;Tambah;$durationMs;$ramKb;$cpuPercent")
                                isSaveSuccess = true
                                Log.d("ROBOT_TEST", "[END_TAMBAH] Sukses: $durationMs ms")
                            }
                        } else {
                            device.pressBack()
                            attemptSave++
                        }
                    } else {
                        device.pressBack()
                        attemptSave++
                    }
                } catch (e: Exception) {
                    attemptSave++
                }
            }
            if (!isSaveSuccess) return@repeat
            sleep(2000)
            var isDeleteSuccess = false
            var attemptDelete = 0
            while (!isDeleteSuccess && attemptDelete < 3) {
                try {
                    device.waitForIdle(3000)
                    val btnDelete = device.wait(Until.findObject(By.res(targetPackage, "btnHapusKamar")), 3000)
                    if (btnDelete != null) {
                        btnDelete.click()
                        val btnConfirm = device.wait(Until.findObject(By.text("Hapus")), 3000)
                        if (btnConfirm != null) {
                            val startTimeHapus = System.nanoTime()
                            btnConfirm.click()
                            device.wait(Until.gone(By.text("Hapus")), 3000)
                            val endTimeHapus = System.nanoTime()
                            isDeleteSuccess = true
                            val durationHapus = (endTimeHapus - startTimeHapus) / 1_000_000
                            sleep(2000)
                            val ramKb = getMemoryUsage()
                            val cpuPercent = getCpuUsage()
                            writeToCsv(csvFileNameTambahHapus, "$iterasi;Hapus;$durationHapus;$ramKb;$cpuPercent")
                            Log.d("ROBOT_TEST", "[END_HAPUS] Sukses: $durationHapus ms")
                        }
                    } else {
                        val list = device.findObject(By.res(targetPackage, "rv_kamar"))
                        list?.scroll(Direction.DOWN, 0.5f)
                        sleep(2000)
                        attemptDelete++
                    }
                } catch (e: Exception) {
                    attemptDelete++
                    sleep(2000)
                }
            }
            sleep(2000)
        }
    }

    @Test
    fun B_testSkenarioInitialLoad() {
        Log.d("ROBOT_TEST", "=== MULAI TEST B: INITIAL LOAD ===")
        val csvFileNameLoad = "InitialLoad_MVVM_30x_${System.currentTimeMillis()}.csv"
        repeat(30) { i ->
            val iterasi = i + 1
            Log.d("ROBOT_TEST", "--- Iterasi Load $iterasi ---")
            try {
                device.pressHome()
                sleep(2000)
                val context = ApplicationProvider.getApplicationContext<Context>()
                val intent = context.packageManager.getLaunchIntentForPackage(targetPackage)?.apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                val startTime = System.nanoTime()
                context.startActivity(intent)
                val isLoaded = device.wait(Until.hasObject(By.res(targetPackage, "rv_kamar")), 10000)
                val endTime = System.nanoTime()
                if (isLoaded) {
                    val durationMs = (endTime - startTime) / 1_000_000
                    sleep(2000)
                    val ramKb = getMemoryUsage()
                    val cpuPercent = getCpuUsage()
                    writeToCsv(csvFileNameLoad, "$iterasi;InitialLoad;$durationMs;$ramKb;$cpuPercent")
                    Log.d("ROBOT_TEST", "Sukses Load: $durationMs ms")
                } else {
                    Log.e("ROBOT_TEST", "Gagal Load (Timeout)")
                }
            } catch (e: Exception) {
                Log.e("ROBOT_TEST", "Error iterasi $iterasi: ${e.message}")
            }
        }
    }

    @Test
    fun C_testSkenarioSearch() {
        Log.d("ROBOT_TEST", "=== MULAI TEST C: SEARCH ===")
        val csvFileNameSearch = "Search_MVVM_30x_${System.currentTimeMillis()}.csv"
        val keyword = "11"
        repeat(30) { i ->
            val iterasi = i + 1
            Log.d("ROBOT_TEST", "--- Iterasi Search $iterasi ---")
            try {
                var searchBar = device.wait(Until.findObject(By.res("android", "search_src_text")), 3000)
                if (searchBar == null) {
                    searchBar = device.wait(Until.findObject(By.text("Cari Kamar")), 3000)
                }
                if (searchBar != null) {
                    searchBar.click()
                    searchBar.text = keyword
                    val startTime = System.nanoTime()
                    device.pressEnter()
                    val isResultFound = device.wait(Until.findObject(By.textContains(keyword)), 3000)
                    val endTime = System.nanoTime()
                    if (isResultFound != null) {
                        val durationMs = (endTime - startTime) / 1_000_000
                        sleep(2000)
                        val ramKb = getMemoryUsage()
                        val cpuPercent = getCpuUsage()
                        writeToCsv(csvFileNameSearch, "$iterasi;Search;$durationMs;$ramKb;$cpuPercent")
                        Log.d("ROBOT_TEST", "Search Sukses: $durationMs ms")
                    } else {
                        Log.e("ROBOT_TEST", "Gagal: Keyword '$keyword' tidak muncul di layar")
                    }
                    searchBar.text = ""
                    sleep(2000)
                    val rvList = device.findObject(By.res(targetPackage, "rv_kamar"))
                    if (rvList != null) {
                        rvList.fling(Direction.UP)
                        sleep(200)
                        rvList.fling(Direction.UP)
                    }
                    sleep(2000)
                } else {
                    Log.e("ROBOT_TEST", "FATAL: SearchView 'Cari Kamar' tidak ditemukan!")
                }
            } catch (e: Exception) {
                Log.e("ROBOT_TEST", "Error Search Iterasi $iterasi: ${e.message}")
            }
        }
    }

    private fun getMemoryUsage(): Long {
        try {
            val output = device.executeShellCommand("dumpsys meminfo $targetPackage")
            val regex = Regex("TOTAL\\s+(\\d+)")
            val match = regex.find(output)
            return match?.groupValues?.get(1)?.toLong() ?: 0L
        } catch (e: Exception) { return 0L }
    }

    private fun getCpuUsage(): String {
        try {
            val output = device.executeShellCommand("dumpsys cpuinfo")
            val lines = output.split("\n")
            val targetLine = lines.find { it.contains(targetPackage) } ?: return "0"
            val regex = Regex("([0-9,.]+)%")
            val match = regex.find(targetLine.trim())
            return match?.groupValues?.get(1)?.replace(".", ",") ?: "0"
        } catch (e: Exception) { return "0" }
    }

    private fun writeToCsv(fileName: String, data: String) {
        try {
            val file = File("/sdcard/Download/HasilPengujian/$fileName")
            if (!file.exists()) {
                file.appendText("Iterasi;Skenario;WaktuEksekusi(ms);RAM(KB);CPU(%)\n")
            }
            file.appendText("$data\n")
            Log.d("ROBOT_DATA", "Saved: $data")
        } catch (e: Exception) {
            Log.e("ROBOT_DATA", "Gagal tulis CSV: ${e.message}")
        }
    }
}