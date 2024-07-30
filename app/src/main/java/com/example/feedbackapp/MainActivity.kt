package com.example.feedbackapp

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.feedbackapp.bean.TypeBean
import com.example.feedbackapp.util.CommonUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
//    private val imageTest by lazy{findViewById<ImageView>(R.id.imageViewTest)}
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

    private val questionTypeAdapter = QuestionTypeAdapter()
    private val mainViewModel: MainViewModel by viewModels()

    private val imageUriList = mutableListOf<Uri?>()
    private val imageUrlList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 初始化监听
        setupListeners()
        // 初始化 UI
        updateImageViews()
        questionTypeRV.adapter = questionTypeAdapter
        questionTypeRV.layoutManager = GridLayoutManager(this, 3)
        lifecycleScope.launch {
            NetworkInstance.getUser().collectLatest {
                Log.w("gyk", "onCreate: ${it.result}")
//                mainViewModel.typeBeans.value = it.result
                if (it.returnCode == 0) {
                    // 将 Map 转换为 List<TypeBean>
                    val typeBeans = it.result.map { (id, typeName) ->
                        TypeBean(id, typeName)
                    }
                    // 更新 StateFlow
                    questionTypeAdapter.updateTypeBeansList(typeBeans)
                } else {
                    // 处理 API 错误，例如记录日志
                    Log.e("MyViewModel", "API Error: ${it.message}")
                }
            }
        }
    }

    private fun setupListeners() {
        imageViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener { openImagePicker() }
        }

        deleteImageViews.forEachIndexed { index, deleteView ->
            deleteView.setOnClickListener { deleteImage(index) }
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
//        Log.w("gyk", "updateImageViews: ${imageUriList}", )
//        imageUriList.forEach { uri->
//            uri?.let {
//                Log.w("gyk", "updateImageViews: ${CommonUtil.getFileNameFromUri(this,it)}", )
//                Glide.with(this)
//            .load(it)
//            .into(imageTest)
//            }

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