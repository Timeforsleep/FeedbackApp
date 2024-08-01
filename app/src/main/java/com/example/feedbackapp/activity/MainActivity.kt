package com.example.feedbackapp.activity


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feedbackapp.R
import com.example.feedbackapp.adapter.QuestionTypeAdapter
import com.example.feedbackapp.adapter.SimpleGridSpacingItemDecoration
import com.example.feedbackapp.bean.FeedbackRequest
import com.example.feedbackapp.bean.TypeBean
import com.example.feedbackapp.common.DEVICE_ID
import com.example.feedbackapp.common.EMERGENCY_IMPORTANT
import com.example.feedbackapp.common.EMERGENCY_MOST
import com.example.feedbackapp.common.EMERGENCY_NORMAL
import com.example.feedbackapp.common.USER_ID
import com.example.feedbackapp.net.NetworkInstance
import com.example.feedbackapp.util.CommonUtil
import com.example.feedbackapp.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private var currentPhotoPath: String? = null
    //用于提交的bean类
//    private val feedbackRequest = FeedbackRequest()
//    private val imageTest by lazy{findViewById<ImageView>(R.id.imageViewTest)}
    private var alertDialog: AlertDialog? = null

    private val fucCL by lazy { findViewById<ConstraintLayout>(R.id.func_type_cl) }
    private val productCL by lazy { findViewById<ConstraintLayout>(R.id.product_type_cl) }

    private val submitTV:TextView by lazy { findViewById(R.id.submit_tv) }
    private val feedbackHistoryTV:TextView by lazy { findViewById(R.id.feedback_history_tv) }

    private val backIV:ImageView by lazy { findViewById(R.id.back_iv) }

    private val showImageNumTV:TextView by lazy { findViewById(R.id.show_image_num_tv) }

    private val feedbackET:EditText by lazy { findViewById(R.id.guide_et)}

    private val startTimeET:EditText by lazy { findViewById(R.id.startTime_ed) }
    private val endTimeET:EditText by lazy { findViewById(R.id.endTime_ed) }
    private val phoneNumET:EditText by lazy { findViewById(R.id.phoneNum_editText) }

    private val normalEmergencyTV:TextView by lazy { findViewById(R.id.normal_emergency_tv) }
    private val importantEmergencyTV:TextView by lazy { findViewById(R.id.important_emergency_tv) }
    private val mostEmergencyTV:TextView by lazy { findViewById(R.id.most_emergency_tv) }

    //TODO点击加载原图
    private val addImageIV1 by lazy { findViewById<ImageView>(R.id.add_image_iv1) }
    private val addImageIV2 by lazy { findViewById<ImageView>(R.id.add_image_iv2) }
    private val addImageIV3 by lazy { findViewById<ImageView>(R.id.add_image_iv3) }
    private val addImageIV4 by lazy { findViewById<ImageView>(R.id.add_image_iv4) }

    private val deleteImageIV1 by lazy { findViewById<ImageView>(R.id.delete_image_1) }
    private val deleteImageIV2 by lazy { findViewById<ImageView>(R.id.delete_image_2) }
    private val deleteImageIV3 by lazy { findViewById<ImageView>(R.id.delete_image_3) }
    private val deleteImageIV4 by lazy { findViewById<ImageView>(R.id.delete_image_4) }

    private val imageViews by lazy { listOf(addImageIV1, addImageIV2, addImageIV3, addImageIV4) }
    private val deleteImageViews by lazy {
        listOf(
            deleteImageIV1,
            deleteImageIV2,
            deleteImageIV3,
            deleteImageIV4
        )
    }

    private val questionTypeRV by lazy { findViewById<RecyclerView>(R.id.question_type_rv) }

    private val mainViewModel: MainViewModel by viewModels()
    private val questionTypeAdapter by lazy { QuestionTypeAdapter(mainViewModel) }

    private val imageUriList = mutableListOf<Uri?>()
    private val imageUrlList = mutableListOf<String>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 初始化监听
        setupListeners()
        // 初始化 UI
        updateImageViews()
        questionTypeRV.adapter = questionTypeAdapter
        questionTypeRV.layoutManager = GridLayoutManager(this, 3)
        val spacingInPixels = CommonUtil.dpToPx(8f, this) // 将dp转换为像素
        val itemDecoration = SimpleGridSpacingItemDecoration(spacingInPixels)
        questionTypeRV.addItemDecoration(itemDecoration)
//        Glide.with(this)
//            .load("http://club2.autoimg.cn/album/g32/M03/0E/5D/userphotos/2024/07/30/20/500_CjIFs2ao4guAKWlpAA1aDt6TgTw255.jpg")
//            .placeholder(R.drawable.add_image)
//            .into(imageTest)
        lifecycleScope.launch {
            NetworkInstance.getProblemScene().collectLatest {
//                mainViewModel.typeBeans.value = it.result
                if (it.returnCode == 0) {
                    // 将 Map 转换为 List<TypeBean>
                    val typeBeans = it.result.map { (id, typeName) ->
                        TypeBean(id, typeName)
                    }
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

        //lifedata
        val errorTypeObserver = Observer<Boolean>{isFucError->
            if (isFucError){
                fucCL.setBackgroundColor(Color.parseColor("#EBF4FF"))
                productCL.setBackgroundColor(Color.parseColor("#f0f0f0"))
            }else{
                productCL.setBackgroundColor(Color.parseColor("#EBF4FF"))
                fucCL.setBackgroundColor(Color.parseColor("#f0f0f0"))
            }
        }

        val emergencyTypeObserver = Observer<Int>{emergencyTypeNum->
            when (emergencyTypeNum) {
                EMERGENCY_IMPORTANT ->{
                    importantEmergencyTV.setBackgroundColor(Color.parseColor("#EBF4FF"))
                    normalEmergencyTV.setBackgroundColor(Color.parseColor("#f0f0f0"))
                    mostEmergencyTV.setBackgroundColor(Color.parseColor("#f0f0f0"))
                }
                EMERGENCY_NORMAL ->{
                    normalEmergencyTV.setBackgroundColor(Color.parseColor("#EBF4FF"))
                    CommonUtil.setBackGroundGray(mostEmergencyTV)
                    CommonUtil.setBackGroundGray(importantEmergencyTV)
                }
                EMERGENCY_MOST ->{
                    mostEmergencyTV.setBackgroundColor(Color.parseColor("#EBF4FF"))
                    CommonUtil.setBackGroundGray(importantEmergencyTV)
                    CommonUtil.setBackGroundGray(normalEmergencyTV)
                }
            }
        }

        val contentObserver = Observer<String>{newContent->
            if (newContent.isNotEmpty()){
                submitTV.isClickable = true
                submitTV.setBackgroundColor(Color.parseColor("#0056f1"))
                submitTV.setTextColor(Color.parseColor("#FFFFFF"))
            }else{
                submitTV.isClickable = false
                submitTV.setBackgroundColor(Color.parseColor("#bec3ce"))
                submitTV.setTextColor(Color.parseColor("#dee0e6"))
            }
        }

        val problemSceneObserver = Observer<TypeBean>{newTypeBean->
            questionTypeAdapter.updateSelectedTypeBean(newTypeBean)
        }

        val relationNumberObserver = Observer<String>{newRelationNumber->
            if (newRelationNumber.isNotEmpty()) {
                startTimeET.visibility = View.VISIBLE
                endTimeET.visibility = View.VISIBLE
            } else {
                startTimeET.visibility = View.GONE
                endTimeET.visibility = View.GONE
            }
        }


        mainViewModel.isFucError.observe(this,errorTypeObserver)
        mainViewModel.questionType.observe(this, emergencyTypeObserver)
        mainViewModel.feedbackContent.observe(this,contentObserver)
        mainViewModel.questionSelectedScene.observe(this,problemSceneObserver)
        mainViewModel.relationNumber.observe(this,relationNumberObserver)
    }

    private fun setupListeners() {
        fucCL.setOnClickListener {
            mainViewModel.isFucError.value = true
        }
        productCL.setOnClickListener {
            mainViewModel.isFucError.value = false
        }
        imageViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener { showCustomDialog(this)}
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
            Log.w("gyk", "setupListeners: ", )
            lifecycleScope.launch {
                NetworkInstance.submitFeedback(FeedbackRequest(
                    targetId = 0,
                    targetType = 0,
                    userId = USER_ID,
                    deviceId = DEVICE_ID,
                    status = mainViewModel.questionType.value!!,
                    category = if (mainViewModel.isFucError.value!!) 0 else 1,
                    tagId = mainViewModel.questionSelectedScene.value!!.id.toInt(),
                    tagName = mainViewModel.questionSelectedScene.value!!.typeName,
                    content = mainViewModel.feedbackContent.value!!,
                    relation = mainViewModel.relationNumber.value!!,
                    photos = imageUrlList,
                    video = null,
                    startTime = mainViewModel.startTime.value!!.toIntOrNull(),
                    endTime = mainViewModel.endTime.value!!.toIntOrNull()
                )).collectLatest {
                    if (it.returnCode == 0) {
                        // 将 Map 转换为 List<TypeBean>
                        Toast.makeText(this@MainActivity, "添加反馈成功！", Toast.LENGTH_SHORT).show()
                    } else {
                        // 处理 API 错误，例如记录日志
                        Toast.makeText(this@MainActivity, "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == REQUEST_PICK_IMAGE) && resultCode == RESULT_OK) {
            data?.data?.let { selectedImageUri ->
                if (imageUriList.size < 4) {
                    imageUriList.add(selectedImageUri)
                    updateImageViews()
                }
            }
        }
        if ((requestCode == REQUEST_IMAGE_CAPTURE) && resultCode == RESULT_OK){
            if (imageUriList.size < 4) {
                val file = currentPhotoPath?.let { File(it) }
                val uri = Uri.fromFile(file)
                imageUriList.add(uri)
                updateImageViews()
            }
        }
        dismissAlertDialog()
    }

    private fun deleteImage(index: Int) {
        if (index in imageUriList.indices) {
            imageUriList.removeAt(index)
            updateImageViews()
        }
    }

    private fun updateImageViews() {
        imageUrlList.clear()
        imageViews?.forEachIndexed { index, imageView ->
            if (index < imageUriList.size) {
                val imageUri = imageUriList[index]
                val base64String = imageUri?.let { CommonUtil.uriToBase64(it, this) }
                if (base64String != null) {
                    imageUrlList.add(base64String)
                    CommonUtil.loadBase64Image(base64String, imageView!!)
                }
                deleteImageViews!![index]?.visibility = View.VISIBLE
            } else {
                imageView?.setImageURI(null)
                imageView?.isClickable = index == imageUriList.size
                imageView?.isVisible = index == imageUriList.size
                deleteImageViews!![index]?.visibility = View.GONE
                imageView?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.add_image))
            }
        }
        showImageNumTV?.text = "已添加${imageUriList.size}/4张图片"
        }

    fun showCustomDialog(context: Context) {
        // 创建布局填充器
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.custom_dialog, null)

        // 获取布局中的控件
        val takePhotoTV: TextView = dialogView.findViewById(R.id.take_photo_tv)
        val chooseAlbumTV: TextView = dialogView.findViewById(R.id.choose_album_tv)
        val cancelTV: TextView = dialogView.findViewById(R.id.cancel_tv)

        // 设置控件的值

        // 创建 AlertDialog
        alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

//        // 这里设置了弹窗的背景透明
//        alertDialog!!.window?.setBackgroundDrawableResource(android.R.color.transparent)
//        val window = alertDialog!!.window
//        window?.setContentView(dialogView)
//
//        // 使弹窗的宽度占满屏幕的宽度
//        window?.setLayout(
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.WRAP_CONTENT
//        )

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

        cancelTV.setOnClickListener {
            alertDialog?.dismiss() // 关闭弹窗
        }
        // 设置对话框显示在底部
        // 设置对话框的窗口属性
        alertDialog?.window?.apply {
            // 设置对话框显示在底部
            setGravity(Gravity.BOTTOM)

            // 设置对话框的宽度
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        }
        // 显示对话框
        alertDialog?.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent()
        } else {
            // 相机权限被拒绝
        }
    }

    // 请求相机权限
    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_IMAGE_CAPTURE)
        } else {
            dispatchTakePictureIntent()
        }
    }

    // 拍照并存储照片路径
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                // 创建用于保存照片的文件
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "gyk.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }


    private fun dismissAlertDialog(){
        alertDialog?.dismiss()
    }


    companion object {
        private const val REQUEST_PICK_IMAGE = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
        // 权限请求码
        private const val CAMERA_REQUEST_CODE = 100
        private const val STORAGE_REQUEST_CODE = 101

    }

}


