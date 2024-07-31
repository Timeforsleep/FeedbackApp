package com.example.feedbackapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feedbackapp.bean.TypeBean
import com.example.feedbackapp.util.CommonUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    //用于提交的bean类
//    private val feedbackRequest = FeedbackRequest()
//    private val imageTest by lazy{findViewById<ImageView>(R.id.imageViewTest)}
    private var alertDialog: AlertDialog? = null

    private val fucCL by lazy { findViewById<ConstraintLayout>(R.id.func_type_cl) }
    private val productCL by lazy { findViewById<ConstraintLayout>(R.id.product_type_cl) }

    private val submitTV:TextView by lazy { findViewById(R.id.submit_tv) }

    private val showImageNumTV:TextView by lazy { findViewById(R.id.show_image_num_tv) }

    private val feedbackET:EditText by lazy { findViewById(R.id.guide_et )}

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
                Log.w("gyk", "onCreate: ${it.result}")
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
                EMERGENCY_IMPORTANT->{
                    importantEmergencyTV.setBackgroundColor(Color.parseColor("#EBF4FF"))
                    normalEmergencyTV.setBackgroundColor(Color.parseColor("#f0f0f0"))
                    mostEmergencyTV.setBackgroundColor(Color.parseColor("#f0f0f0"))
                }
                EMERGENCY_NORMAL->{
                    normalEmergencyTV.setBackgroundColor(Color.parseColor("#EBF4FF"))
                    CommonUtil.setBackGroundGray(mostEmergencyTV)
                    CommonUtil.setBackGroundGray(importantEmergencyTV)
                }
                EMERGENCY_MOST->{
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
            Log.w("gyk", "onCreate: newBean${newTypeBean.id}    ${newTypeBean.typeName}", )
            questionTypeAdapter.updateSelectedTypeBean(newTypeBean)
        }

        val relationNumberObserver = Observer<String>{newRelationNumber->
            if (newRelationNumber.isNotEmpty()) {
                startTimeET.visibility = View.VISIBLE
                endTimeET.visibility = View.VISIBLE
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
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            data?.data?.let { selectedImageUri ->
                if (imageUriList.size < 4) {
                    imageUriList.add(selectedImageUri)
                    updateImageViews()
                }
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
        imageUrlList.clear() // 清空之前的文件名列表
        imageViews.forEachIndexed { index, imageView ->
            if (index < imageUriList.size) {
                val imageUri = imageUriList[index]
                imageView.setImageURI(imageUri)
                deleteImageViews[index].visibility = View.VISIBLE
                // 获取并显示文件名
                val fileName = imageUri?.let { CommonUtil.getFileNameFromUri(this, it) }
                if (fileName != null) {
                    imageUrlList.add(fileName)
                }
            } else {
                imageView.setImageURI(null)
                imageView.isClickable = index == imageUriList.size
                imageView.isVisible = index == imageUriList.size
                deleteImageViews[index].visibility = View.GONE
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.add_image))
            }
        }
        // 打印文件名列表
        Log.w("gyk", "updateImageViews: ${imageUrlList}")
        showImageNumTV.text = "已添加${imageUriList.size}/4张图片"
//        Log.w("gyk", "updateImageViews: ${imageUriList}", )
//        imageUriList.forEach { uri ->
//            uri?.let {
//                Log.w("gyk", "updateImageViews: ${CommonUtil.getFileNameFromUri(this, it)}",)
//                Glide.with(this)
//                    .load(it)
//                    .into(imageTest)
//            }
//        }


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

        // 设置点击事件
        takePhotoTV.setOnClickListener {
            // 处理拍照逻辑
            openImagePicker()
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


    private fun dismissAlertDialog(){
        alertDialog?.dismiss()
    }


    companion object {
        private const val REQUEST_PICK_IMAGE = 1
    }

}

//super.onCreate(savedInstanceState)
//enableEdgeToEdge()
//setContentView(R.layout.activity_main)
//ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//    insets
//}