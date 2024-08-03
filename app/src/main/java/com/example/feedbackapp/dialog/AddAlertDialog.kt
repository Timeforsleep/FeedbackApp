//
//import android.Manifest
//import android.app.Activity
//import android.app.AlertDialog
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Color
//import android.net.Uri
//import android.os.Build
//import android.os.Environment
//import android.provider.MediaStore
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.core.content.ContextCompat
//import androidx.core.content.FileProvider
//import androidx.core.view.isVisible
//import androidx.core.widget.doAfterTextChanged
//import com.example.feedbackapp.R
//import com.example.feedbackapp.activity.FeedbackHistoryActivity
//import com.example.feedbackapp.bean.FeedbackRequest
//import com.example.feedbackapp.net.NetworkInstance
//import com.example.feedbackapp.util.CommonUtil
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.io.File
//import java.io.IOException
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//
//class AddAlertDialog(private val activity: Activity) {
//    private var currentPhotoPath: String? = null
//
//    private var addImageIV1: ImageView? = null
//    private var addImageIV2: ImageView? = null
//    private var addImageIV3: ImageView? = null
//    private var addImageIV4: ImageView? = null
//    private var deleteImageIV1: ImageView? = null
//    private var deleteImageIV2: ImageView? = null
//    private var deleteImageIV3: ImageView? = null
//    private var deleteImageIV4: ImageView? = null
//    private var imageViews: List<ImageView?>? = null
//    private var deleteImageViews: List<ImageView?>? = null
//    private var showImageNumTV: TextView? = null
//    private var sendTV:TextView?=null
//
//    var feedbackRequest: FeedbackRequest? = null
//
//
//    val imageUriList = mutableListOf<Uri?>()
//    val imageUrlList = mutableListOf<String>()
//
//    private fun showImagePickerDialog() {
//        val options = arrayOf("选择照片", "拍摄照片")
//        val builder = AlertDialog.Builder(activity)
//        builder.setTitle("添加图片")
//        builder.setItems(options) { _, which ->
//            when (which) {
//                0 -> openImagePicker()
//                1 -> dispatchTakePictureIntent()
//            }
//        }
//        builder.show()
//    }
//    fun show() {
//        // Inflate the custom layout
//        val inflater = LayoutInflater.from(activity)
//        val dialogView = inflater.inflate(R.layout.add_feedback_dialog, null)
//
//        // Find views in the custom layout
//        val backIv: ImageView = dialogView.findViewById(R.id.back_iv)
//        val textView: TextView = dialogView.findViewById(R.id.textView)
//        val addEt: EditText = dialogView.findViewById(R.id.add_et)
//        sendTV = dialogView.findViewById(R.id.send_tv)
//
//        addEt.doAfterTextChanged{
//            feedbackRequest?.content =it.toString()
//            if (it.isNullOrEmpty()) {
//                sendTV?.setTextColor(Color.parseColor("#DCDCDC"))
//                sendTV?.isClickable = false
//            } else {
//                sendTV?.setTextColor(Color.parseColor("#0056f1"))
//                sendTV?.isClickable = true
//            }
//        }
//
//        addImageIV1 = dialogView.findViewById(R.id.add_image_iv1)
//        addImageIV2 = dialogView.findViewById(R.id.add_image_iv2)
//        addImageIV3 = dialogView.findViewById(R.id.add_image_iv3)
//        addImageIV4 = dialogView.findViewById(R.id.add_image_iv4)
//
//        deleteImageIV1 = dialogView.findViewById(R.id.delete_image_1)
//        deleteImageIV2 = dialogView.findViewById(R.id.delete_image_2)
//        deleteImageIV3 = dialogView.findViewById(R.id.delete_image_3)
//        deleteImageIV4 = dialogView.findViewById(R.id.delete_image_4)
//        showImageNumTV = dialogView.findViewById(R.id.show_image_num_tv)
//
//        imageViews = listOf(addImageIV1, addImageIV2, addImageIV3, addImageIV4)
//        deleteImageViews = listOf(deleteImageIV1, deleteImageIV2, deleteImageIV3, deleteImageIV4)
//
//        // Create the AlertDialog
//        val builder = AlertDialog.Builder(activity)
//        builder.setView(dialogView)
//        builder.setCancelable(true)
//
//        // Create and show the dialog
//        val dialog = builder.create()
//        dialog.show()
//
//        // Set up button click listeners
//        backIv.setOnClickListener {
//            dialog.dismiss()
//            (activity as FeedbackHistoryActivity).refreshFeedbackHistory()
//        }
//
//
//        sendTV?.setOnClickListener {
//            if (feedbackRequest != null) {
//                Log.w("gyk", "show: ${feedbackRequest.toString()}", )
//                CoroutineScope(Dispatchers.IO).launch{
//                    NetworkInstance.submitFeedback(feedbackRequest!!).collectLatest {
//                        if (it.returnCode == 0) {
//                            // 将 Map 转换为 List<TypeBean>
//                            withContext(Dispatchers.Main){
//                                Toast.makeText(activity, "追加描述成功！", Toast.LENGTH_SHORT).show()
//                                dialog.dismiss()
//                                (activity as FeedbackHistoryActivity).refreshFeedbackHistory()
//                            }
//                        } else {
//                            // 处理 API 错误，例如记录日志
//                            withContext(Dispatchers.Main){
//                                Toast.makeText(activity, "${it.message}", Toast.LENGTH_SHORT).show()
//                                dialog.dismiss()
//                                (activity as FeedbackHistoryActivity).refreshFeedbackHistory()
//                            }
//                        }
//                    }
//                }
//            }
//            // Handle the send action here
//
//        }
//
//        // Handle image view clicks
//        addImageIV1?.setOnClickListener { showImagePickerDialog() }
//        addImageIV2?.setOnClickListener { showImagePickerDialog() }
//        addImageIV3?.setOnClickListener { showImagePickerDialog() }
//        addImageIV4?.setOnClickListener { showImagePickerDialog() }
//
//        deleteImageViews?.forEachIndexed { index, deleteView ->
//            deleteView?.setOnClickListener { deleteImage(index) }
//        }
//    }
//
//    private fun updateImageViews() {
//        imageUrlList.clear()
//        imageViews?.forEachIndexed { index, imageView ->
//            if (index < imageUriList.size) {
//                val imageUri = imageUriList[index]
//                val base64String = imageUri?.let { CommonUtil.uriToBase64(it, activity,activity.windowManager.defaultDisplay.width,activity.windowManager.defaultDisplay.height) }
//                if (base64String != null) {
//                    imageUrlList.add(base64String)
//                    CommonUtil.loadBase64Image(base64String, imageView!!)
//                }
//                deleteImageViews!![index]?.visibility = View.VISIBLE
//            } else {
//                imageView?.setImageURI(null)
//                imageView?.isClickable = index == imageUriList.size
//                imageView?.isVisible = index == imageUriList.size
//                deleteImageViews!![index]?.visibility = View.GONE
//                imageView?.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.add_image))
//            }
//        }
//        showImageNumTV?.text = "已添加${imageUriList.size}/4张图片"
//        feedbackRequest?.photos = imageUrlList
//
//        if (imageUriList.isNotEmpty()) {
//            sendTV?.setTextColor(Color.parseColor("#0056f1"))
//            sendTV?.isClickable = true
//        } else {
//            sendTV?.setTextColor(Color.parseColor("#DCDCDC"))
//            sendTV?.isClickable = false
//        }
//    }
//
//
//    private fun openImagePicker() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        activity.startActivityForResult(intent, REQUEST_IMAGE_PICK_ADD)
//    }
//
//    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == REQUEST_IMAGE_PICK_ADD && resultCode == Activity.RESULT_OK) {
//            val imageUri: Uri? = data?.data
//            imageUri?.let {
//                if (imageUriList.size < 4) {
//                    imageUriList.add(it)
//                    updateImageViews()
//                }
//            }
//        }
//        if (requestCode == REQUEST_IMAGE_CAPTURE_ADD && resultCode == Activity.RESULT_OK){
//            val file = File(currentPhotoPath ?: "")
//            val imageUri: Uri = Uri.fromFile(file)
//            if (imageUriList.size < 4) {
//                imageUriList.add(imageUri)
//                updateImageViews()
//            }
//        }
//    }
//    private fun dispatchTakePictureIntent() {
//        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                activity.requestPermissions(
//                    arrayOf(Manifest.permission.CAMERA),
//                    REQUEST_CAMERA_PERMISSION_ADD
//                )
//            }
//        } else {
//            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//                takePictureIntent.resolveActivity(activity.packageManager)?.also {
//                    val photoFile: File? = try {
//                        createImageFile()
//                    } catch (ex: IOException) {
//                        null
//                    }
//                    photoFile?.also {
//                        val photoURI: Uri = FileProvider.getUriForFile(
//                            activity,
//                            "gyk.fileprovider",
//                            it
//                        )
//                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                        activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_ADD)
//                    }
//                }
//            }
//        }
//    }
//
//    @Throws(IOException::class)
//    private fun createImageFile(): File {
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
//        val storageDir: File? = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        return File.createTempFile(
//            "JPEG_${timeStamp}_",
//            ".jpg",
//            storageDir
//        ).apply {
//            currentPhotoPath = absolutePath
//        }
//    }
//
//    private fun deleteImage(index: Int) {
//        if (index in imageUriList.indices) {
//            imageUriList.removeAt(index)
//            updateImageViews()
//        }
//    }
//
//    companion object {
//        private const val REQUEST_IMAGE_PICK_ADD = 4
//        private const val REQUEST_IMAGE_CAPTURE_ADD = 5
//        private const val REQUEST_CAMERA_PERMISSION_ADD = 6
//
//    }
//}
