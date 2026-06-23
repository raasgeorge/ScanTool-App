package com.scantool.honda

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.scantool.honda.data.model.NetworkResult
import com.scantool.honda.databinding.ActivityMainBinding
import com.scantool.honda.viewmodel.ScanToolViewModel
import com.scantool.honda.viewmodel.SensorType

/**
 * MainActivity - Entry point aplikasi
 * Menampilkan dashboard real-time sensor data dari ESP32
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ScanToolViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup ViewModel
        viewModel = ViewModelProvider(this)[ScanToolViewModel::class.java]

        // Setup UI
        setupToolbar()
        setupSwipeRefresh()
        setupObservers()
        setupClickListeners()

        // Check WiFi connection
        checkWifiConnection()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchSensorData()
        }
        binding.swipeRefresh.setColorSchemeResources(
            R.color.primary,
            R.color.primary_variant,
            R.color.secondary
        )
    }

    private fun setupObservers() {
        // Observe sensor data
        viewModel.sensorData.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    showLoading(true)
                }
                is NetworkResult.Success -> {
                    showLoading(false)
                    updateUI()
                }
                is NetworkResult.Error -> {
                    showLoading(false)
                    showError(result.message ?: "Unknown error")
                }
            }
        }

        // Observe connection status
        viewModel.isConnected.observe(this) { isConnected ->
            updateConnectionStatus(isConnected)
        }

        // Observe latest data untuk update UI
        viewModel.latestData.observe(this) { sensorData ->
            if (sensorData != null) {
                updateSensorValues()
            }
        }
    }

    private fun setupClickListeners() {
        // Info button
        binding.btnInfo.setOnClickListener {
            showInfoDialog()
        }

        // Refresh button
        binding.btnRefresh.setOnClickListener {
            viewModel.fetchSensorData()
        }
    }

    private fun updateUI() {
        updateSensorValues()
    }

    private fun updateSensorValues() {
        // Update RPM
        binding.tvRpmValue.text = viewModel.getSensorValue(SensorType.RPM)

        // Update TPS
        binding.tvTpsMvValue.text = "${viewModel.getSensorValue(SensorType.TPS_MV)} mV"
        binding.tvTpsPctValue.text = "${viewModel.getSensorValue(SensorType.TPS_PCT)}%"

        // Update ECT (Engine Coolant Temperature)
        binding.tvEctMvValue.text = "${viewModel.getSensorValue(SensorType.ECT_MV)} mV"
        binding.tvEctCValue.text = "${viewModel.getSensorValue(SensorType.ECT_C)}°C"

        // Update IAT (Intake Air Temperature)
        binding.tvIatMvValue.text = "${viewModel.getSensorValue(SensorType.IAT_MV)} mV"
        binding.tvIatCValue.text = "${viewModel.getSensorValue(SensorType.IAT_C)}°C"

        // Update MAP (Manifold Absolute Pressure)
        binding.tvMapMvValue.text = "${viewModel.getSensorValue(SensorType.MAP_MV)} mV"
        binding.tvMapKpaValue.text = "${viewModel.getSensorValue(SensorType.MAP_KPA)} kPa"

        // Update Battery
        binding.tvBatteryValue.text = "${viewModel.getSensorValue(SensorType.BATTERY)} V"

        // Update Injector
        binding.tvInjectorValue.text = "${viewModel.getSensorValue(SensorType.INJECTOR)} ms"

        // Update Ignition
        binding.tvIgnitionValue.text = "${viewModel.getSensorValue(SensorType.IGNITION)}°"

        // Update Speed
        binding.tvSpeedValue.text = "${viewModel.getSensorValue(SensorType.SPEED)} km/h"
    }

    private fun updateConnectionStatus(isConnected: Boolean) {
        if (isConnected) {
            binding.tvConnectionStatus.text = "Connected"
            binding.tvConnectionStatus.setTextColor(getColor(R.color.success))
            binding.imgConnectionStatus.setImageResource(R.drawable.ic_connected)
        } else {
            binding.tvConnectionStatus.text = "Disconnected"
            binding.tvConnectionStatus.setTextColor(getColor(R.color.error))
            binding.imgConnectionStatus.setImageResource(R.drawable.ic_disconnected)
        }
    }

    private fun showLoading(show: Boolean) {
        binding.swipeRefresh.isRefreshing = show
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun checkWifiConnection() {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo

        if (wifiInfo.ssid.contains("ScantoolHonda")) {
            // Connected to correct WiFi
            Toast.makeText(this, "Connected to ScantoolHonda", Toast.LENGTH_SHORT).show()
        } else {
            // Not connected to ScantoolHonda
            showWifiWarningDialog()
        }
    }

    private fun showWifiWarningDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("WiFi Connection Required")
            .setMessage("Please connect to 'ScantoolHonda' WiFi network first.\n\nIP: 192.168.4.1")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showInfoDialog() {
        val message = """
            ESP32 Honda ScanTool

            Make sure you are connected to:
            • WiFi: ScantoolHonda
            • IP: 192.168.4.1

            Data updates every 300ms automatically.

            Swipe down to refresh manually.
        """.trimIndent()

        MaterialAlertDialogBuilder(this)
            .setTitle("Information")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        // Start auto refresh when activity is visible
        viewModel.startAutoRefresh()
    }

    override fun onPause() {
        super.onPause()
        // Stop auto refresh when activity is not visible
        viewModel.stopAutoRefresh()
    }
}
