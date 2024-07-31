import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.example.feedbackapp.R
import com.example.feedbackapp.bean.FeedbackRequest
import com.example.feedbackapp.bean.TypeBean
import com.example.feedbackapp.net.NetworkInstance
import com.example.feedbackapp.util.CommonUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddAlertDialog(private val activity: Activity) {

    private var addImageIV1: ImageView? = null
    private var addImageIV2: ImageView? = null
    private var addImageIV3: ImageView? = null
    private var addImageIV4: ImageView? = null
    private var deleteImageIV1: ImageView? = null
    private var deleteImageIV2: ImageView? = null
    private var deleteImageIV3: ImageView? = null
    private var deleteImageIV4: ImageView? = null
    private var imageViews: List<ImageView?>? = null
    private var deleteImageViews: List<ImageView?>? = null
    private var showImageNumTV: TextView? = null

    var feedbackRequest: FeedbackRequest? = null


    val imageUriList = mutableListOf<Uri?>()
    val imageUrlList = mutableListOf<String>()

    fun show() {
        // Inflate the custom layout
        val inflater = LayoutInflater.from(activity)
        val dialogView = inflater.inflate(R.layout.add_feedback_dialog, null)

        // Find views in the custom layout
        val backIv: ImageView = dialogView.findViewById(R.id.back_iv)
        val textView: TextView = dialogView.findViewById(R.id.textView)
        val addEt: EditText = dialogView.findViewById(R.id.add_et)
        val sendTv: TextView = dialogView.findViewById(R.id.send_tv)

        addEt.doAfterTextChanged{
            feedbackRequest?.content =it.toString()
            if (it.isNullOrEmpty()) {
                sendTv.setTextColor(Color.parseColor("#DCDCDC"))
                sendTv.isClickable = false
            } else {
                sendTv.setTextColor(Color.parseColor("#0056f1"))
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

        imageViews = listOf(addImageIV1, addImageIV2, addImageIV3, addImageIV4)
        deleteImageViews = listOf(deleteImageIV1, deleteImageIV2, deleteImageIV3, deleteImageIV4)

        // Create the AlertDialog
        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogView)
        builder.setCancelable(true)

        // Create and show the dialog
        val dialog = builder.create()
        dialog.show()

        // Set up button click listeners
        backIv.setOnClickListener {
            dialog.dismiss()
        }

        sendTv.setOnClickListener {
            if (feedbackRequest != null) {
                Log.w("gykk", "show1: ", )
                CoroutineScope(Dispatchers.IO).launch{
                    NetworkInstance.submitFeedback(feedbackRequest!!).collectLatest {
                        Log.w("gyk", "onCreate: ${it.result}")
                        if (it.returnCode == 0) {
                            // 将 Map 转换为 List<TypeBean>
                            Log.w("gykrequest", "show: success", )
                        } else {
                            // 处理 API 错误，例如记录日志
                            Log.e("MyViewModel", "API Error: ${it.message}")
                        }
                    }
                }
            }
            // Handle the send action here
            dialog.dismiss()
        }

        // Handle image view clicks
        addImageIV1?.setOnClickListener { openImagePicker() }
        addImageIV2?.setOnClickListener { openImagePicker() }
        addImageIV3?.setOnClickListener { openImagePicker() }
        addImageIV4?.setOnClickListener { openImagePicker() }

        deleteImageViews?.forEachIndexed { index, deleteView ->
            deleteView?.setOnClickListener { deleteImage(index) }
        }
    }

    private fun updateImageViews() {
        imageUrlList.clear()
        imageViews?.forEachIndexed { index, imageView ->
            if (index < imageUriList.size) {
                val imageUri = imageUriList[index]
                imageView?.setImageURI(imageUri)
                deleteImageViews!![index]?.visibility = View.VISIBLE
                val fileName = imageUri?.let { CommonUtil.getFileNameFromUri(activity, it) }
                if (fileName != null) {
                    imageUrlList.add(fileName)
                }
            } else {
                imageView?.setImageURI(null)
                imageView?.isClickable = index == imageUriList.size
                imageView?.isVisible = index == imageUriList.size
                deleteImageViews!![index]?.visibility = View.GONE
                imageView?.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.add_image))
            }
        }
        Log.w("gyk", "updateImageViews: ${imageUrlList}")
        showImageNumTV?.text = "已添加${imageUriList.size}/4张图片"
        feedbackRequest?.photos = imageUrlList
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.w("gykkkk", "onActivityResult: ", )
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            imageUri?.let {
                if (imageUriList.size < 4) {
                    imageUriList.add(it)
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

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }
}
