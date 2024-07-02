package com.example.test4.ui.dashboard

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.test4.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class DashboardFragment : Fragment() {

    private lateinit var gridView: GridView
    private lateinit var imageAdapter: ImageAdapter
    private val imageList = ArrayList<String>()
    private lateinit var currentPhotoUri: Uri

    private val selectPhotoLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    Log.d("DashboardFragment", "Photo selected: $uri")
                    val bitmap = getBitmapFromUri(uri)
                    bitmap?.let {
                        val path = saveToInternalStorage(it)
                        imageList.add(path)
                        imageAdapter.notifyDataSetChanged()
                        saveImageList()
                    }
                }
            } else {
                Log.d("DashboardFragment", "Photo selection failed or canceled")
            }
        }

    private val requestPermissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value == true }) {
                Log.d("DashboardFragment", "Permissions granted")
                openGallery()
            } else {
                Log.d("DashboardFragment", "Permissions not granted")
                permissions.forEach { (permission, granted) ->
                    Log.d("DashboardFragment", "Permission: $permission, Granted: $granted")
                }
                showPermissionDeniedSnackbar()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        gridView = view.findViewById(R.id.gridview)
        imageAdapter = ImageAdapter(requireContext(), imageList, { imagePath ->
            // 이미지 클릭 시 처리할 내용
        }, { imagePath ->
            showDeleteConfirmationDialog(imagePath)
        })
        gridView.adapter = imageAdapter

        loadImageList()

        val btnOpenGallery: FloatingActionButton = view.findViewById(R.id.btn_open_gallery)
        btnOpenGallery.setOnClickListener {
            Log.d("DashboardFragment", "Gallery button clicked")
            if (checkPermissions()) {
                Log.d("DashboardFragment", "Permissions granted, opening gallery")
                openGallery()
            } else {
                Log.d("DashboardFragment", "Permissions not granted, requesting permissions")
            }
        }

        return view
    }

    private fun checkPermissions(): Boolean {
        val permissionsNeeded = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        val permissionsToRequest = permissionsNeeded.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }

        return if (permissionsToRequest.isNotEmpty()) {
            Log.d("DashboardFragment", "Requesting permissions: $permissionsToRequest")
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
            false
        } else {
            true
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectPhotoLauncher.launch(intent)
    }

    private fun showPermissionDeniedSnackbar() {
        Snackbar.make(
            requireView(),
            "Permissions are required to access photos. Please enable them in settings.",
            Snackbar.LENGTH_LONG
        ).setAction("Settings") {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", requireContext().packageName, null)
            }
            startActivity(intent)
        }.show()
    }

    private fun saveToInternalStorage(bitmapImage: Bitmap): String {
        val cw = ContextWrapper(context)
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        val path = File(directory, "${System.currentTimeMillis()}.jpg")

        var fos: OutputStream? = null
        try {
            fos = FileOutputStream(path)
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return path.absolutePath
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun showDeleteConfirmationDialog(imagePath: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Image")
            .setMessage("Are you sure you want to delete this image?")
            .setPositiveButton("Yes") { dialog, _ ->
                deleteImage(imagePath)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun deleteImage(imagePath: String) {
        val file = File(imagePath)
        if (file.exists()) {
            file.delete()
        }
        imageList.remove(imagePath)
        imageAdapter.notifyDataSetChanged()
        saveImageList()
    }

    private fun saveImageList() {
        val sharedPreferences = requireContext().getSharedPreferences("image_list_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val jsonArray = JSONArray(imageList)
        editor.putString("image_list", jsonArray.toString())
        editor.apply()
    }

    private fun loadImageList() {
        val sharedPreferences = requireContext().getSharedPreferences("image_list_prefs", Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString("image_list", null)
        if (jsonString != null) {
            val jsonArray = JSONArray(jsonString)
            imageList.clear()
            for (i in 0 until jsonArray.length()) {
                imageList.add(jsonArray.getString(i))
            }
            imageAdapter.notifyDataSetChanged()
        }
    }

    companion object {
        private const val REQUEST_PERMISSIONS = 10
    }
}
