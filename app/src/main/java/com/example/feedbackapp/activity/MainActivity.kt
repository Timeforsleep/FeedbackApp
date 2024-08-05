package com.example.feedbackapp.activity


import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feedbackapp.R
import com.example.feedbackapp.adapter.QuestionTypeAdapter
import com.example.feedbackapp.adapter.SimpleGridSpacingItemDecoration
import com.example.feedbackapp.bean.AlbumBean
import com.example.feedbackapp.bean.FeedbackRequest
import com.example.feedbackapp.bean.TypeBean
import com.example.feedbackapp.bean.UploadBean
import com.example.feedbackapp.common.DEVICE_ID
import com.example.feedbackapp.common.EMERGENCY_IMPORTANT
import com.example.feedbackapp.common.EMERGENCY_MOST
import com.example.feedbackapp.common.EMERGENCY_NORMAL
import com.example.feedbackapp.common.HAVE_ADD_SCORE
import com.example.feedbackapp.common.IS_AGREE_KEY
import com.example.feedbackapp.common.USER_ID
import com.example.feedbackapp.dialog.AddScoreDialog
import com.example.feedbackapp.net.NetworkInstance
import com.example.feedbackapp.util.CommonUtil
import com.example.feedbackapp.util.CommonUtil.getFilePathFromUri
import com.example.feedbackapp.util.MMKVUtil
import com.example.feedbackapp.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class MainActivity : AppCompatActivity() {
    private var currentPhotoPath: String? = null
//    private val button: Button by lazy { findViewById(R.id.button) }
    //标识是否能上传了
    private var canUploadFile = false
    var addScoreAlertDialog = AddScoreDialog(this)

    private val checkBox by lazy { findViewById<CheckBox>(R.id.checkbox) }

    private val progressBar by lazy { findViewById<ProgressBar>(R.id.progressBar) }

    private var tempCaptureFilePath: String? = null//拍完照临时的保存地方
    private var tempCaptureUri: Uri? = null//拍完照存在相册的uri

    private var alertDialog: AlertDialog? = null

    private val fucCL by lazy { findViewById<ConstraintLayout>(R.id.func_type_cl) }
    private val productCL by lazy { findViewById<ConstraintLayout>(R.id.product_type_cl) }

    private val personalProtectTV by lazy { findViewById<TextView>(R.id.personal_protect_tv) }
    private val privatePolicyTV by lazy { findViewById<TextView>(R.id.privacy_polite_tv) }

    private val submitTV: TextView by lazy { findViewById(R.id.submit_tv) }
    private val feedbackHistoryTV: TextView by lazy { findViewById(R.id.feedback_history_tv) }

    private val backIV: ImageView by lazy { findViewById(R.id.back_iv) }

    private val showImageNumTV: TextView by lazy { findViewById(R.id.show_image_num_tv) }

    private val feedbackET: EditText by lazy { findViewById(R.id.guide_et) }

    private val startTimeET: EditText by lazy { findViewById(R.id.startTime_ed) }
    private val endTimeET: EditText by lazy { findViewById(R.id.endTime_ed) }
    private val phoneNumET: EditText by lazy { findViewById(R.id.phoneNum_editText) }

    private val normalEmergencyTV: TextView by lazy { findViewById(R.id.normal_emergency_tv) }
    private val importantEmergencyTV: TextView by lazy { findViewById(R.id.important_emergency_tv) }
    private val mostEmergencyTV: TextView by lazy { findViewById(R.id.most_emergency_tv) }

    //TODO点击加载原图
    private val addImageIV1 by lazy { findViewById<ImageView>(R.id.add_image_iv1) }
    private val addImageIV2 by lazy { findViewById<ImageView>(R.id.add_image_iv2) }
    private val addImageIV3 by lazy { findViewById<ImageView>(R.id.add_image_iv3) }
    private val addImageIV4 by lazy { findViewById<ImageView>(R.id.add_image_iv4) }

    private val deleteImageIV1 by lazy { findViewById<ImageView>(R.id.delete_image_1) }
    private val deleteImageIV2 by lazy { findViewById<ImageView>(R.id.delete_image_2) }
    private val deleteImageIV3 by lazy { findViewById<ImageView>(R.id.delete_image_3) }
    private val deleteImageIV4 by lazy { findViewById<ImageView>(R.id.delete_image_4) }

    private val isVideoImageIV1 by lazy { findViewById<ImageView>(R.id.is_video_iv1) }
    private val isVideoImageIV2 by lazy { findViewById<ImageView>(R.id.is_video_iv2) }
    private val isVideoImageIV3 by lazy { findViewById<ImageView>(R.id.is_video_iv3) }
    private val isVideoImageIV4 by lazy { findViewById<ImageView>(R.id.is_video_iv4) }

    private val imageViews by lazy { listOf(addImageIV1, addImageIV2, addImageIV3, addImageIV4) }
    private val deleteImageViews by lazy {
        listOf(
            deleteImageIV1,
            deleteImageIV2,
            deleteImageIV3,
            deleteImageIV4
        )
    }

    private val isVideoImageViews by lazy {
        listOf(
            isVideoImageIV1,
            isVideoImageIV2,
            isVideoImageIV3,
            isVideoImageIV4
        )
    }

    private val permissionStorage = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val questionTypeRV by lazy { findViewById<RecyclerView>(R.id.question_type_rv) }

    private val mainViewModel: MainViewModel by viewModels()
    private val questionTypeAdapter by lazy { QuestionTypeAdapter(mainViewModel) }

    private val albumUriList = mutableListOf<AlbumBean>()
    private val upLoadBeans = mutableListOf<UploadBean>()


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        getFeedbackId()
//        ActivityCompat.requestPermissions(
//            this@MainActivity,
//            permissionStorage,
//            STORAGE_REQUEST_CODE
//        )
        // 初始化监听
        setupListeners()
        // 初始化 UI
        updateImageViews()
        checkBox.isChecked = MMKVUtil.getBoolean(IS_AGREE_KEY,false)
        questionTypeRV.adapter = questionTypeAdapter
        questionTypeRV.layoutManager = GridLayoutManager(this, 3)
        val spacingInPixels = CommonUtil.dpToPx(4f, this) // 将dp转换为像素
        val itemDecoration = SimpleGridSpacingItemDecoration(spacingInPixels)
        questionTypeRV.addItemDecoration(itemDecoration)

        getCategory()
        //lifedata
        val errorTypeObserver = Observer<Boolean> { isFucError ->
            if (isFucError) {
                fucCL.setBackgroundColor(Color.parseColor("#EBF4FF"))
                productCL.setBackgroundColor(Color.parseColor("#f0f0f0"))
            } else {
                productCL.setBackgroundColor(Color.parseColor("#EBF4FF"))
                fucCL.setBackgroundColor(Color.parseColor("#f0f0f0"))
            }
        }

        val emergencyTypeObserver = Observer<Int> { emergencyTypeNum ->
            when (emergencyTypeNum) {
                EMERGENCY_IMPORTANT -> {
                    importantEmergencyTV.setBackgroundColor(Color.parseColor("#EBF4FF"))
                    normalEmergencyTV.setBackgroundColor(Color.parseColor("#f0f0f0"))
                    mostEmergencyTV.setBackgroundColor(Color.parseColor("#f0f0f0"))
                }

                EMERGENCY_NORMAL -> {
                    normalEmergencyTV.setBackgroundColor(Color.parseColor("#EBF4FF"))
                    CommonUtil.setBackGroundGray(mostEmergencyTV)
                    CommonUtil.setBackGroundGray(importantEmergencyTV)
                }

                EMERGENCY_MOST -> {
                    mostEmergencyTV.setBackgroundColor(Color.parseColor("#EBF4FF"))
                    CommonUtil.setBackGroundGray(importantEmergencyTV)
                    CommonUtil.setBackGroundGray(normalEmergencyTV)
                }
            }
        }

        val contentObserver = Observer<String> { newContent ->
            if (newContent.isNotEmpty()) {
                submitTV.isClickable = true
                submitTV.setBackgroundColor(Color.parseColor("#0056f1"))
                submitTV.setTextColor(Color.parseColor("#FFFFFF"))
            } else {
                submitTV.isClickable = false
                submitTV.setBackgroundColor(Color.parseColor("#bec3ce"))
                submitTV.setTextColor(Color.parseColor("#dee0e6"))
            }
        }

        val problemSceneObserver = Observer<TypeBean> { newTypeBean ->
            questionTypeAdapter.updateSelectedTypeBean(newTypeBean)
        }

        val relationNumberObserver = Observer<String> { newRelationNumber ->
            if (newRelationNumber.isNotEmpty()) {
                startTimeET.visibility = View.VISIBLE
                endTimeET.visibility = View.VISIBLE
            } else {
                startTimeET.visibility = View.GONE
                endTimeET.visibility = View.GONE
            }
        }


        mainViewModel.isFucError.observe(this, errorTypeObserver)
        mainViewModel.questionType.observe(this, emergencyTypeObserver)
        mainViewModel.feedbackContent.observe(this, contentObserver)
        mainViewModel.questionSelectedScene.observe(this, problemSceneObserver)
        mainViewModel.relationNumber.observe(this, relationNumberObserver)
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupListeners() {
        personalProtectTV.setOnClickListener {
            val intent = Intent(this@MainActivity,AgentWebViewActivity::class.java)
            intent.putExtra("webUrl","https://dealer.m.autohome.com.cn/dealer/agreement.html")
            startActivity(intent)
        }
        privatePolicyTV.setOnClickListener {
            val intent = Intent(this@MainActivity,AgentWebViewActivity::class.java)
            intent.putExtra("webUrl","https://comm.app.autohome.com.cn/baseservice/privacypolicy/protocols?a=2&showrecall=0")
            startActivity(intent)
        }
        fucCL.setOnClickListener {
            mainViewModel.isFucError.value = true
        }
        productCL.setOnClickListener {
            mainViewModel.isFucError.value = false
        }
        imageViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener { showCustomDialog(this) }
        }

        deleteImageViews.forEachIndexed { index, deleteView ->
            deleteView.setOnClickListener { deleteImage(index) }
        }
        normalEmergencyTV.setOnClickListener {
            mainViewModel.questionType.value = 0
        }
        importantEmergencyTV.setOnClickListener {
            mainViewModel.questionType.value = 1
        }
        mostEmergencyTV.setOnClickListener {
            mainViewModel.questionType.value = 2
        }
        feedbackET.doAfterTextChanged {
            mainViewModel.feedbackContent.value = it.toString()
        }
        phoneNumET.doAfterTextChanged {
            mainViewModel.relationNumber.value = it.toString()
        }
        startTimeET.doAfterTextChanged {
            mainViewModel.startTime.value = it.toString()
        }
        endTimeET.doAfterTextChanged {
            mainViewModel.endTime.value = it.toString()
        }
        feedbackHistoryTV.setOnClickListener {
            startActivity(Intent(this, FeedbackHistoryActivity::class.java))
        }
        backIV.setOnClickListener {
            onBackPressed()
        }
        submitTV.setOnClickListener {
            Log.w("gyk", "setupListeners: ")
            if (!MMKVUtil.getBoolean(IS_AGREE_KEY)) {
                Toast.makeText(this, "还没同意隐私政策和保护声明", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            handleAndUploadMedia(albumUriList)
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            MMKVUtil.putBoolean(IS_AGREE_KEY, isChecked)
        }
    }

    private fun submit() {
        if (mainViewModel.relationNumber.value == null) {
            Log.w("gyk", "submit: 联系方式为空！")
        }
        lifecycleScope.launch {
            NetworkInstance.getFeedbackId().collectLatest {
                progressBar.visibility = View.VISIBLE
                if (it.returnCode == 0) {
                    val feedbackId = it.result
                    val feedbackReq = FeedbackRequest(
                        id = feedbackId,
                        targetId = 0,
                        targetType = 0,
                        userId = USER_ID,
                        deviceId = DEVICE_ID,
                        status = mainViewModel.questionType.value!!,
                        category = if (mainViewModel.isFucError.value!!) 0 else 1,
                        tagId = mainViewModel.questionSelectedScene.value!!.id.toInt(),
                        tagName = mainViewModel.questionSelectedScene.value!!.typeName,
                        content = mainViewModel.feedbackContent.value!!,
                        relation = mainViewModel.relationNumber.value,
                        startTime = mainViewModel.startTime.value!!.toIntOrNull(),
                        endTime = mainViewModel.endTime.value!!.toIntOrNull()
                    )
                    Log.w("gykId", "getFeedbackId: ${it.result}")
                    NetworkInstance.submitFeedback(
                        feedbackReq
                    ).collectLatest {
                        if (it.returnCode == 0) {
                            progressBar.visibility = View.GONE
                            val uploadFilesPathList = upLoadBeans.map { it.file.absolutePath }
                                NetworkInstance.uploadFiles(feedbackId, uploadFilesPathList)
                                    .collectLatest {
                                        if (it.returnCode == 0) {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "添加图片和视频成功！",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            // 处理 API 错误，例如记录日志
                                            Toast.makeText(
                                                this@MainActivity,
                                                "${it.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                        } else {
                            progressBar.visibility = View.GONE
                            Toast.makeText(this@MainActivity, "${it.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    if (!MMKVUtil.getBoolean(HAVE_ADD_SCORE)) {
                        addScoreAlertDialog.show()
                    }
                } else {
                    // 处理 API 错误，例如记录日志
                    Log.e("MyViewModel", "API Error: ${it.message}")
                }
            }
        }
    }



    private fun openImagePicker() {
        when {
            ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                Log.w("gyk", "openImagePicker: 打开图片")
                chooseImage(REQUEST_PICK_IMAGE)
            }
            else -> {
                Log.w("gyk", "opeFnImagePicker: 请求图片")
                // 请求权限
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    permissionStorage,
                    STORAGE_REQUEST_CODE
                )
            }
        }
    }

    private fun openVideoPicker() {
        when {
            ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                Log.w("gyk", "openImagePicker: 打开图片")
                chooseVideo(REQUEST_PICK_VIDEO)
            }
            else -> {
                Log.w("gyk", "opeFnImagePicker: 请求图片")
                // 请求权限
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    permissionStorage,
                    STORAGE_REQUEST_CODE
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == REQUEST_PICK_IMAGE) && resultCode == RESULT_OK) {
            data?.data?.let { selectedImageUri ->
                if (albumUriList.size < 4) {
                    albumUriList.add(AlbumBean(selectedImageUri))
                    updateImageViews()
                }
            }
        }
        if ((requestCode == REQUEST_IMAGE_CAPTURE) && resultCode == RESULT_OK) {
            Log.w("gyk", "onActivityResult: 拍完照了", )
//            if (albumUriList.size < 4) {
////                val file = currentPhotoPath?.let { File(it) }
////                val uri = Uri.fromFile(file)
//                val albumBean = AlbumBean(uri)
//                albumBean.isFromCapture = true
//                albumUriList.add(albumBean)
//                updateImageViews()
//            }
            tempCaptureUri?.let { selectedVideoUri ->
                if (albumUriList.size < 4) {
                    val albumBean = AlbumBean(selectedVideoUri)
                    albumBean.isFromCapture = true
                    albumUriList.add(albumBean)
                    updateImageViews()
                }
            }
        }
        if ((requestCode == REQUEST_PICK_VIDEO) && resultCode == RESULT_OK) {
            data?.data?.let { selectedVideoUri ->
                if (albumUriList.size < 4) {
                    val albumBean = AlbumBean(selectedVideoUri)
                    albumBean.isVideo = true
                    albumUriList.add(albumBean)
                    updateImageViews()
                }
            }
        }
        dismissAlertDialog()
    }

    private fun deleteImage(index: Int) {
        if (index in albumUriList.indices) {
            albumUriList.removeAt(index)
            updateImageViews()
        }
    }

    private fun updateImageViews() {
//        imageUrlList.clear()
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
                        imageView.setImageBitmap(thumbnail)
                        //覆盖点击事件
                        imageView.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setDataAndType(albumUri.uri, "video/*")
                            startActivity(intent)
                        }
                    }
                } else {
                    // 使用ContentResolver通过URI获取输入流
//                    val inputStream = contentResolver.openInputStream(albumUri.uri)
//                    uploadImage(albumUri.uri,contentResolver)
                    // 将输入流解析为Bitmap
//                    val bitmap = BitmapFactory.decodeStream(inputStream)

                    // 关闭输入流
//                    inputStream!!.close()
                    Log.w("gykuri", "updateImageViews: ${albumUri.uri}", )
                    // 显示Bitmap到ImageView
                    imageView.setImageURI(albumUri.uri)
                    imageView.setOnClickListener {
                        val intent = Intent(this@MainActivity, WatchPicActivity::class.java)
                        intent.putExtra("photoUrl", albumUri.uri.toString())
                        startActivity(intent)
                    }
                }
            } else {
                imageView?.setImageURI(null)
                imageView?.isClickable = index == albumUriList.size
                imageView?.isVisible = index == albumUriList.size
                deleteImageViews[index]?.visibility = View.GONE
                isVideoImageViews[index]?.visibility = View.GONE
                imageView?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.add_image))
                imageView?.setOnClickListener {
                    showCustomDialog(this)
                }
            }
        }
        if (albumUriList.size != 0) {
            submitTV.isClickable = true
            submitTV.setBackgroundColor(Color.parseColor("#0056f1"))
            submitTV.setTextColor(Color.parseColor("#FFFFFF"))
        }
        showImageNumTV.text = "已添加${albumUriList.size}/4张图片或视频"
    }

    // 获取视频和图片文件的实际路径
    private fun getRealPathFromURI(contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Video.Media.DATA)
        val cursor = contentResolver.query(contentUri, proj, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val path = cursor.getString(column_index)
            cursor.close()
            return path
        }
        return null
    }

    fun showCustomDialog(context: Context) {
        // 创建布局填充器
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.custom_dialog, null)

        // 获取布局中的控件
        val takePhotoTV: TextView = dialogView.findViewById(R.id.take_photo_tv)
        val chooseAlbumTV: TextView = dialogView.findViewById(R.id.choose_album_tv)
        val cancelTV: TextView = dialogView.findViewById(R.id.cancel_tv)
        val chooseVideoTV: TextView = dialogView.findViewById(R.id.choose_video_tv)

        // 设置控件的值

        // 创建 AlertDialog
        alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()


        // 设置点击事件
        takePhotoTV.setOnClickListener {
            // 处理拍照逻辑
            // 请求相机和存储权限
            requestCameraPermission()
        }

        chooseAlbumTV.setOnClickListener {
            // 处理选择图片逻辑
            openImagePicker()
        }

        chooseVideoTV.setOnClickListener {
            openVideoPicker()
        }

        cancelTV.setOnClickListener {
            alertDialog?.dismiss() // 关闭弹窗
        }
        // 设置对话框显示在底部
        // 设置对话框的窗口属性
        alertDialog?.window?.apply {
            // 设置对话框显示在底部
            setGravity(Gravity.BOTTOM)

            // 设置对话框的宽度
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }
        // 显示对话框
        alertDialog?.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent()
        } else {
            // 相机权限被拒绝
        }
//        if (requestCode == STORAGE_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//        } else {
//
//        }
    }

    // 请求相机权限
    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_IMAGE_CAPTURE
            )
        } else {
            dispatchTakePictureIntent()
        }
    }

    private fun getCategory() {
        lifecycleScope.launch {
            NetworkInstance.getProblemScene().collectLatest {
//                mainViewModel.typeBeans.value = it.result
                if (it.returnCode == 0) {
                    // 将 Map 转换为 List<TypeBean>
                    val typeBeans = it.result.map { (id, typeName) ->
                        TypeBean(id, typeName)
                    }
                    Log.w("gyk类别", "getCategory: ${typeBeans}", )
                    questionTypeAdapter.updateTypeBeansList(typeBeans)
                    mainViewModel.questionSceneList.clear()
                    mainViewModel.questionSceneList.addAll(typeBeans.toMutableList())
                    if (typeBeans.isNotEmpty()) {
                        mainViewModel.questionSelectedScene.value = typeBeans[0]
                    }
                } else {
                    // 处理 API 错误，例如记录日志
                    Log.e("MyViewModel", "API Error: ${it.message}")
                }
            }
        }
    }


    // 拍照并存储照片路径
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
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
                tempCaptureUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempCaptureUri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }



    private fun dismissAlertDialog() {
        alertDialog?.dismiss()
    }

    private fun chooseVideo(openVideoCode: Int) {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        );
        startActivityForResult(intent, openVideoCode);
    }

    private fun chooseImage(openImageCode: Int) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, openImageCode)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun handleAndUploadMedia(list: MutableList<AlbumBean>) {
        Log.w("gykkk", "handleMedia: ${list}")
        this@MainActivity.upLoadBeans.clear()
        val inputFilePaths =
            list.map { albumBean ->
//                if (!albumBean.isFromCapture) {
                    getFilePathFromUri(albumBean.uri, contentResolver)
//                } else {
//                    albumBean.uri.toString()
//                }
        }
        inputFilePaths.forEach {
            inputFilePath->
            val inputFile = File(inputFilePath)
//            if (!inputFile.exists()) {
//                inputFile.createNewFile()
//            }
            this.upLoadBeans.add(UploadBean(inputFile))
        }
//        inputFilePaths.forEach {
//
//        }


        submit()
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        return image
    }


    companion object {
       const val REQUEST_PICK_IMAGE = 1
       const val REQUEST_PICK_VIDEO = 7
       const val REQUEST_IMAGE_CAPTURE = 2

        // 权限请求码
        const val CAMERA_REQUEST_CODE = 100
        const val STORAGE_REQUEST_CODE = 1001

    }

}


