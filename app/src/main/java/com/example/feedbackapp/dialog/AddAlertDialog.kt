
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.example.feedbackapp.R
import com.example.feedbackapp.activity.FeedbackHistoryActivity
import com.example.feedbackapp.activity.MainActivity
import com.example.feedbackapp.activity.PlayVideoActivity
import com.example.feedbackapp.activity.WatchPicActivity
import com.example.feedbackapp.bean.AlbumBean
import com.example.feedbackapp.bean.FeedbackRequest
import com.example.feedbackapp.bean.UploadBean
import com.example.feedbackapp.net.NetworkInstance
import com.example.feedbackapp.util.CommonUtil
import com.example.feedbackapp.util.CommonUtil.setOnSingleClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AddAlertDialog(private val activity: Activity) {

    private var tempCaptureFilePath: String? = null//拍完照临时的保存地方
    private var tempCaptureUri: Uri? = null//拍完照存在相册的uri

    private var isVideoImageIV1:ImageView?=null
    private var isVideoImageIV2:ImageView?=null
    private var isVideoImageIV3:ImageView?=null
    private var isVideoImageIV4:ImageView?=null

    private var strNumTV: TextView? = null

    private var addImageIV1: ImageView? = null
    private var addImageIV2: ImageView? = null
    private var addImageIV3: ImageView? = null
    private var addImageIV4: ImageView? = null
    private var deleteImageIV1: ImageView? = null
    private var deleteImageIV2: ImageView? = null
    private var deleteImageIV3: ImageView? = null
    private var deleteImageIV4: ImageView? = null
    private val imageViews by lazy { listOf(addImageIV1, addImageIV2, addImageIV3, addImageIV4) }
    private val deleteImageViews by lazy {
        listOf(
            deleteImageIV1,
            deleteImageIV2,
            deleteImageIV3,
            deleteImageIV4
        )
    }
    private var showImageNumTV: TextView? = null
    private var sendTV:TextView?=null

    var feedbackRequest: FeedbackRequest? = null
    private val albumUriList = mutableListOf<AlbumBean>()
    private val upLoadBeans = mutableListOf<UploadBean>()//上传file的列表
    private val permissionStorage = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val isVideoImageViews by lazy {
        listOf(
            isVideoImageIV1,
            isVideoImageIV2,
            isVideoImageIV3,
            isVideoImageIV4
        )
    }

    private fun chooseVideo(openVideoCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        intent.type = "video/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        activity.startActivityForResult(intent, openVideoCode)
    }

    private fun openVideoPicker() {
        when {
            ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                Log.w("gyk", "openImagePicker: 打开图片")
                chooseVideo(REQUEST_PICK_VIDEO_ADD)
            }
            else -> {
                Log.w("gyk", "opeFnImagePicker: 请求图片")
                // 请求权限
                ActivityCompat.requestPermissions(
                    activity,
                    permissionStorage,
                    MainActivity.STORAGE_REQUEST_CODE
                )
            }
        }
    }

//    val imageUriList = mutableListOf<Uri?>()
//    val imageUrlList = mutableListOf<String>()

    private fun showImagePickerDialog() {
        val options = arrayOf("选择照片", "拍摄照片", "选择视频")
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("添加图片")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openImagePicker()
                1 -> requestCameraPermission()
                2-> openVideoPicker()
            }
        }
        builder.show()
    }
    @SuppressLint("MissingInflatedId")
    fun show() {
        // Inflate the custom layout
        val inflater = LayoutInflater.from(activity)
        val dialogView = inflater.inflate(R.layout.add_feedback_dialog, null)

        // Find views in the custom layout
        val backIv: ImageView = dialogView.findViewById(R.id.back_iv)
        val textView: TextView = dialogView.findViewById(R.id.textView)
        strNumTV = dialogView.findViewById(R.id.str_num_textview)
        val addEt: EditText = dialogView.findViewById(R.id.add_et)
        sendTV = dialogView.findViewById(R.id.send_tv)

        addEt.doAfterTextChanged{
            feedbackRequest?.content =it.toString()
            strNumTV?.text = "${it?.length?:0}/300字"
            if (it.isNullOrEmpty()) {
                sendTV?.setTextColor(Color.parseColor("#DCDCDC"))
                sendTV?.isClickable = false
            } else {
                sendTV?.setTextColor(Color.parseColor("#0056f1"))
                sendTV?.isClickable = true
            }
        }



        addImageIV1 = dialogView.findViewById(R.id.add_image_iv1)
        addImageIV2 = dialogView.findViewById(R.id.add_image_iv2)
        addImageIV3 = dialogView.findViewById(R.id.add_image_iv3)
        addImageIV4 = dialogView.findViewById(R.id.add_image_iv4)

        deleteImageIV1 = dialogView.findViewById(R.id.delete_image_1)
        deleteImageIV2 = dialogView.findViewById(R.id.delete_image_2)
        deleteImageIV3 = dialogView.findViewById(R.id.delete_image_3)
        deleteImageIV4 = dialogView.findViewById(R.id.delete_image_4)
        showImageNumTV = dialogView.findViewById(R.id.show_image_num_tv)

        isVideoImageIV1 = dialogView.findViewById(R.id.is_video_iv1)
        isVideoImageIV2 = dialogView.findViewById(R.id.is_video_iv2)
        isVideoImageIV3 = dialogView.findViewById(R.id.is_video_iv3)
        isVideoImageIV4 = dialogView.findViewById(R.id.is_video_iv4)

        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogView)
        builder.setCancelable(true)

        // Create and show the dialog
        val dialog = builder.create()
        dialog.show()

        // Set up button click listeners
        backIv.setOnClickListener {
            dialog.dismiss()
            (activity as FeedbackHistoryActivity).refreshFeedbackHistory()
        }

        fun handleMedia(list: MutableList<AlbumBean>) {
            Log.w("gykkk", "handleMedia: ${list}")
            this.upLoadBeans.clear()
            val inputFilePaths =
                list.map { albumBean ->
                    CommonUtil.getFilePathFromUri(albumBean.uri, activity.contentResolver)
                }
            inputFilePaths.forEach { inputFilePath ->
                val inputFile = File(inputFilePath)
//                if (!inputFilePath.endsWith(".mp4")) {
//                    val compressedFile = CommonUtil.compressImage(inputFile, ScreenUtil.getWindowWidth(activity), ScreenUtil.getWindowHeight(activity))
//                    this.upLoadBeans.add(UploadBean(compressedFile))
//                } else {
//                    this.upLoadBeans.add(UploadBean(inputFile))
//                }
                this.upLoadBeans.add(UploadBean(inputFile))
            }
        }


        sendTV?.setOnSingleClickListener() {
            if (feedbackRequest != null) {
                (activity as FeedbackHistoryActivity).progressBar.visibility = View.VISIBLE
                Log.w("gyk", "show: ${feedbackRequest.toString()}")
                CoroutineScope(Dispatchers.IO).launch {
                    NetworkInstance.getFeedbackId().collectLatest {
                        if (it.returnCode == 0) {
                            val feedbackId = it.result//用于上传图片和视频
                            feedbackRequest!!.id = feedbackId
                            NetworkInstance.submitFeedback(feedbackRequest!!).collectLatest {
                                if (it.returnCode == 0) {
                                    // 将 Map 转换为 List<TypeBean>
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            activity,
                                            "追加描述成功！",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        dialog.dismiss()
                                        (activity as FeedbackHistoryActivity).refreshFeedbackHistory()
                                    }
                                } else {
                                    (activity as FeedbackHistoryActivity).progressBar.visibility = View.GONE
                                    // 处理 API 错误，例如记录日志
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            activity,
                                            "${it.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        dialog.dismiss()
                                        (activity as FeedbackHistoryActivity).refreshFeedbackHistory()
                                    }
                                }
                            }
                            handleMedia(albumUriList)
                            val uploadFilesPathList = upLoadBeans.map { it.file.absolutePath }
                            NetworkInstance.uploadFiles(feedbackId, uploadFilesPathList)
                                .collectLatest {
                                    if (it.returnCode == 0) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(
                                                activity,
                                                "添加图片和视频成功！",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        (activity as FeedbackHistoryActivity).progressBar.visibility = View.GONE
//                                        withContext(Dispatchers.Main) {
//                                            Toast.makeText(
//                                                activity,
//                                                "${it.message}",
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                        }
                                        // 处理 API 错误，例如记录日志
                                    }
                                }
                        }

                    }

                }
            } else {(activity as FeedbackHistoryActivity).progressBar.visibility = View.GONE
                Toast.makeText(activity, "追加id为空！", Toast.LENGTH_SHORT).show()
            }
            // Handle the send action here

        }

        // Handle image view clicks
        addImageIV1?.setOnClickListener { showImagePickerDialog() }
        addImageIV2?.setOnClickListener { showImagePickerDialog() }
        addImageIV3?.setOnClickListener { showImagePickerDialog() }
        addImageIV4?.setOnClickListener { showImagePickerDialog() }

        deleteImageViews?.forEachIndexed { index, deleteView ->
            deleteView?.setOnClickListener { deleteImage(index) }
        }
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Video.Media.DATA)
        val cursor = activity.contentResolver.query(contentUri, proj, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val path = cursor.getString(column_index)
            cursor.close()
            return path
        }
        return null
    }

    private fun updateImageViews() {
        imageViews.forEachIndexed { index, imageView ->
            if (index < albumUriList.size) {
                val albumUri = albumUriList[index]
                deleteImageViews[index]?.visibility = View.VISIBLE
                //1.如果是视频,则要显示播放按钮
                if (albumUri.isVideo) {
                    isVideoImageViews[index]?.visibility = View.VISIBLE
                    getRealPathFromURI(albumUri.uri)?.let { path ->
                        val thumbnail = ThumbnailUtils.createVideoThumbnail(
                            path,
                            MediaStore.Images.Thumbnails.MINI_KIND
                        )
                        imageView?.setImageBitmap(thumbnail)
                        //覆盖点击事件
                        imageView?.setOnClickListener {
                            val intent = Intent(activity, PlayVideoActivity::class.java)
                            intent.putExtra("videoFilePath",path)
                            activity.startActivity(intent)
                        }
                    }
                } else {
                    Log.w("gykuri", "updateImageViews: ${albumUri.uri}", )
                    // 显示Bitmap到ImageView
                    imageView?.setImageURI(albumUri.uri)
                    imageView?.setOnClickListener {
                        val intent = Intent(activity, WatchPicActivity::class.java)
                        intent.putExtra("photoUrl", albumUri.uri.toString())
                        activity.startActivity(intent)
                    }
                }
            } else {
                imageView?.setImageURI(null)
                imageView?.isClickable = index == albumUriList.size
                imageView?.isVisible = index == albumUriList.size
                deleteImageViews[index]?.visibility = View.GONE
                isVideoImageViews[index]?.visibility = View.GONE
                imageView?.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.add_image))
                imageView?.setOnClickListener {
                    showImagePickerDialog()
                }
            }
        }
        showImageNumTV?.text = "已添加${albumUriList.size}/4张图片或视频"
        if (albumUriList.isNotEmpty()) {
            sendTV?.setTextColor(Color.parseColor("#0056f1"))
            sendTV?.isClickable = true
        } else {
            sendTV?.setTextColor(Color.parseColor("#DCDCDC"))
            sendTV?.isClickable = false
        }
    }

    private fun chooseImage(openImageCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        activity.startActivityForResult(intent, openImageCode)
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_IMAGE_CAPTURE_ADD
            )
        } else {
            dispatchTakePictureIntent()
        }
    }


    private fun openImagePicker() {
        when {
            ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                Log.w("gyk", "openImagePicker: 打开图片")
                chooseImage(REQUEST_IMAGE_PICK_ADD)
            }
            else -> {
                Log.w("gyk", "opeFnImagePicker: 请求图片")
                // 请求权限
                ActivityCompat.requestPermissions(
                    activity,
                    permissionStorage,
                    MainActivity.STORAGE_REQUEST_CODE
                )
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_PICK_ADD && resultCode == Activity.RESULT_OK&&data!=null) {
            val clipData = data.clipData
            val imageUris = mutableListOf<Uri>()
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    if (imageUris.size + albumUriList.size >= 4) {
                        Toast.makeText(activity, "最多只能选择4张图片或视频", Toast.LENGTH_SHORT).show()
                        break
                    }
                    val imageUri = clipData.getItemAt(i).uri
                    if (CommonUtil.isImageOrVideoSizeValid(imageUri,5,activity.contentResolver)) {
                        imageUris.add(imageUri)
                    } else {
                        Toast.makeText(activity, "选择的图片单张不能超过5MB", Toast.LENGTH_SHORT).show()
                    }
                }
                imageUris.forEach {selectedImageUri->
                    if (albumUriList.size < 4) {
                        albumUriList.add(AlbumBean(selectedImageUri))
                        updateImageViews()
                    }
                }
            }

//            val imageUri: Uri? = data?.data
//            imageUri?.let {
//                if (albumUriList.size < 4) {
//                    albumUriList.add(AlbumBean(it))
//                    updateImageViews()
//                }
//            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE_ADD && resultCode == Activity.RESULT_OK){
            tempCaptureUri?.let { selectedVideoUri ->
                if (albumUriList.size < 4) {
                    val albumBean = AlbumBean(selectedVideoUri)
                    albumBean.isFromCapture = true
                    albumUriList.add(albumBean)
                    updateImageViews()
                }
            }
        }
        if ((requestCode == REQUEST_PICK_VIDEO_ADD) && resultCode == AppCompatActivity.RESULT_OK&&data!=null) {
//            data?.data?.let { selectedVideoUri ->
//                if (albumUriList.size < 4) {
//                    val albumBean = AlbumBean(selectedVideoUri)
//                    albumBean.isVideo = true
//                    albumUriList.add(albumBean)
//                    updateImageViews()
//                }
//            }
            val clipData = data.clipData
            val videoUris = mutableListOf<Uri>()
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    if (videoUris.size + albumUriList.size>=4) {
                        Toast.makeText(activity, "最多只能选择4张图片或视频", Toast.LENGTH_SHORT).show()
                        break
                    }
                    val imageUri = clipData.getItemAt(i).uri
                    if (CommonUtil.isImageOrVideoSizeValid(imageUri,10,activity.contentResolver)) {
                        videoUris.add(imageUri)
                    } else {
                        Toast.makeText(activity, "选择的视频单个不能超过10MB", Toast.LENGTH_SHORT).show()
                    }
                }
                videoUris.forEach { selectedImageUri->
                    if (albumUriList.size < 4) {
                        val albumBean = AlbumBean(selectedImageUri)
                        albumBean.isVideo = true
                        albumUriList.add(albumBean)
                        updateImageViews()
                    }
                }
            }
        }
    }
    // 拍照并存储照片路径
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity.packageManager)?.also {
                // 创建用于保存照片的文件
                val fileDir = File(Environment.getExternalStorageDirectory(), "Pictures")
                if (!fileDir.exists()) {
                    fileDir.mkdir()
                }
                val fileName = "IMG_" + System.currentTimeMillis() + ".jpg"
                tempCaptureFilePath = "${fileDir.absolutePath}/$fileName"
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Pictures")
                    } else {
                        put(MediaStore.Images.Media.DATA, tempCaptureFilePath)
                    }
                }
                tempCaptureUri = activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempCaptureUri)
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_ADD)
            }
        }
    }


    private fun deleteImage(index: Int) {
        if (index in albumUriList.indices) {
            albumUriList.removeAt(index)
            updateImageViews()
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK_ADD = 4
        private const val REQUEST_IMAGE_CAPTURE_ADD = 5
        private const val REQUEST_PICK_VIDEO_ADD = 8

    }
}
