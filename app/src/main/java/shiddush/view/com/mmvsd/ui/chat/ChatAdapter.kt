package shiddush.view.com.mmvsd.ui.chat

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.*
import shiddush.view.com.mmvsd.model.chat.Messages
import shiddush.view.com.mmvsd.utill.*


class ChatAdapter(
    private val context: Context,
    private val messagesViewModel: ChatViewModel
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var requestList: MutableList<Messages> = ArrayList()
    private val TYPE_USER = 0
    private val TYPE_FRIEND = 1
    private val TYPE_IMAGE = 2
    private val TYPE_DOCUMENT = 3
    private val TYPE_HEADER_DATE = 4
    private var prevDate:String? = null
    private var showDate = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            TYPE_FRIEND -> {
                val binding: ItemViewFriendMessageBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.item_view_friend_message,
                        parent,
                        false
                    )
                return FriendViewHolder(binding)
            }

            TYPE_USER -> {
                val binding: ItemViewUserMessageBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.item_view_user_message,
                        parent,
                        false
                    )
                return UserViewHolder(binding)
            }
            TYPE_IMAGE -> {
                val binding: ItemViewImageBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.item_view_image,
                        parent,
                        false
                    )
                return ImageViewHolder(binding)
            }
            TYPE_DOCUMENT -> {
                val binding: ItemViewDocumentBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.item_view_document,
                        parent,
                        false
                    )
                return DocumentViewHolder(binding)
            }
            TYPE_HEADER_DATE -> {
                val binding: ItemViewHeaderDateBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.item_view_header_date,
                        parent,
                        false
                    )
                return HeaderViewHolder(binding)
            }

            else -> throw RuntimeException("There is no type that matches the type $viewType + make sure your using types correctly")
        }
    }


    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       // holder.setIsRecyclable(false);
        when (holder) {
            is FriendViewHolder -> holder.bind(requestList[position])
            is UserViewHolder -> holder.bind(requestList[position])
            is ImageViewHolder -> holder.bind(requestList[position])
            is DocumentViewHolder -> holder.bind(requestList[position])
            is HeaderViewHolder -> holder.bind(requestList[position])
        }
    }

    fun customNotify(singleChatList: MutableList<Messages>?, itemCount: Int) {
        requestList = singleChatList!!

        //requestList.addAll(singleChatList!!)

        // if (requestList.size <= messagesViewModel.limit.toInt())
        //   requestList.reverse()

        //  notifyItemInserted(0)
        if (requestList.size > 0)
            notifyItemInserted(requestList.size - 1);
        else
            notifyDataSetChanged()

//        val positionStart = requestList.size + 1;
//        if (requestList.size == 0)
//            requestList.addAll(0, singleChatList!!)
//        else
//            requestList.addAll(requestList.size - 1, singleChatList!!)
//
//        notifyItemRangeInserted(positionStart, requestList.size);

//        notifyDataSetChanged()

    }


    inner class FriendViewHolder constructor(var binding: ItemViewFriendMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Messages?) {
            binding.chat = data!!
            data.let { model ->
                if (AppInstance.userObj?.getGender()!! == 1) //Female
                    binding.ivThumbnail.setImageResource(R.drawable.ic_male)
                else
                    binding.ivThumbnail.setImageResource(R.drawable.ic_female)

                messagesViewModel.getTimeFormatted(model.createdAt!!)
                    .let { binding.textView2.text = it }

                if (showDate){
                    binding.relativeLayoutHeaderDate.relativeLayoutHeaderDate.visibility= View.VISIBLE
                    messagesViewModel.getDateFormatted(model.createdAt!!)
                        .let { binding.relativeLayoutHeaderDate.textViewTimeHeader.text = it }
                }else{
                    binding.relativeLayoutHeaderDate.relativeLayoutHeaderDate.visibility= View.GONE
                }
            }
        }
    }

    inner class UserViewHolder constructor(private var binding: ItemViewUserMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Messages?) {
            binding.chat = data!!

            data.let { model ->
                messagesViewModel.getTimeFormatted(model.createdAt!!)
                    .let {
                        Log.d("sendMessage",messagesViewModel.getTimeFormatted(model.createdAt!!))
                        binding.textView2.text = it
                    }


                if (showDate){
                    binding.relativeLayoutHeaderDate.relativeLayoutHeaderDate.visibility= View.VISIBLE
                    messagesViewModel.getDateFormatted(model.createdAt!!)
                        .let { binding.relativeLayoutHeaderDate.textViewTimeHeader.text = it }
                }else{
                    binding.relativeLayoutHeaderDate.relativeLayoutHeaderDate.visibility= View.GONE
                }
            }
        }

    }

    inner class ImageViewHolder constructor(private var binding: ItemViewImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Messages?) {
            binding.chat = data!!


            data.let { model ->

                messagesViewModel.getTimeFormatted(model.createdAt!!)
                    .let { binding.tvLastMessageTime.text = it }

                Glide.with(binding.imageView.context)
                    .load(data.message)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(binding.imageView)

                binding.imageView.setOnClickListener {
                    messagesViewModel.attachmentImage(data.message!!)
                }


                if (model.userid?.equals(getUserObject(context).getId()!!)!!) {
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.END
                    }
                    binding.llRootLayout.layoutParams = params
                    binding.tvLastMessageTime.layoutParams = params

                    binding.llImage.visibility=View.GONE

                } else {
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.START
                    }
                    binding.llRootLayout.layoutParams = params
                 //   binding.tvLastMessageTime.layoutParams = params

                    binding.llImage.visibility=View.VISIBLE
                }

                if (AppInstance.userObj?.getGender()!! == 1) //Female
                    binding.ivThumbnail.setImageResource(R.drawable.ic_male)
                else
                    binding.ivThumbnail.setImageResource(R.drawable.ic_female)

                if (showDate){
                    binding.relativeLayoutHeaderDate.relativeLayoutHeaderDate.visibility= View.VISIBLE
                    messagesViewModel.getDateFormatted(model.createdAt!!)
                        .let { binding.relativeLayoutHeaderDate.textViewTimeHeader.text = it }
                    binding.relativeLayoutHeaderDate.textViewTimeHeader
                }else{
                    binding.relativeLayoutHeaderDate.relativeLayoutHeaderDate.visibility= View.GONE
                }

            }
        }

    }

    inner class DocumentViewHolder constructor(private var binding: ItemViewDocumentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Messages?) {
            binding.chat = data!!


            data.let { model ->
                messagesViewModel.getTimeFormatted(model.createdAt!!)
                    .let { binding.tvLastMessageTime.text = it }


                messagesViewModel.documentName = model.message!!.substring(
                    model.message!!.lastIndexOf(
                        '/'
                    ) + 1
                ).trim()
                binding.tvFileName.text = messagesViewModel.documentName

                binding.imageViewDownloadDoc.setOnClickListener {
                    messagesViewModel.documentUrl = model.message!!
                    messagesViewModel.documentName = model.message!!.substring(
                        model.message!!.lastIndexOf(
                            '/'
                        ) + 1
                    ).trim()
                    messagesViewModel.documentType = "application/" +
                            model.message!!.substring(model.message!!.lastIndexOf('.') + 1).trim()

                    messagesViewModel.downloadDocument(
                        messagesViewModel.documentUrl,
                        messagesViewModel.documentType,
                        messagesViewModel.documentName
                    )
                }
                binding.imageViewSeeDoc.setOnClickListener {
                    messagesViewModel.documentUrl = model.message!!
                    messagesViewModel.documentType =
                        "application/" +
                                model.message!!.substring(model.message!!.lastIndexOf('.') + 1)
                                    .trim()

                    messagesViewModel.viewDocument(
                        messagesViewModel.documentUrl,
                        messagesViewModel.documentType
                    )
                }

                if (model.userid?.equals(getUserObject(context).getId())!!) {
                    setMargins(binding.rootLayout, 100, 0, 0, 0)
                    binding.llImage.visibility=View.GONE
                }
                else {
                    setMargins(binding.rootLayout, 0, 0, 100, 0)
                    binding.llImage.visibility=View.VISIBLE
                }

                if (AppInstance.userObj?.getGender()!! == 1) //Female
                    binding.ivThumbnail.setImageResource(R.drawable.ic_male)
                else
                    binding.ivThumbnail.setImageResource(R.drawable.ic_female)


                if (showDate){
                    binding.relativeLayoutHeaderDate.relativeLayoutHeaderDate.visibility= View.VISIBLE
                    messagesViewModel.getDateFormatted(model.createdAt!!)
                        .let { binding.relativeLayoutHeaderDate.textViewTimeHeader.text = it }
                    binding.relativeLayoutHeaderDate.textViewTimeHeader
                }else{
                    binding.relativeLayoutHeaderDate.relativeLayoutHeaderDate.visibility= View.GONE
                }
            }
        }

    }

    inner class HeaderViewHolder constructor(private var binding: ItemViewHeaderDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Messages?) {
            binding.chat = data!!

            data.let { model ->
                messagesViewModel.getDateFormatted(model.createdAt!!)
                    .let { binding.textViewTimeHeader.text = it }

            }
        }

    }



    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        if (requestList.size < 1) {
            return position
        }

        var onlyDate = ""
        if (requestList[position].createdAt!!.isNotEmpty()) {
            val dateWithoutT = requestList[position].createdAt!!.replace("T", " ")
            val dateWithoutZ = dateWithoutT.substring(0, dateWithoutT.length - 5).split(" ")
            onlyDate = dateWithoutZ[0]
        }else {
            onlyDate = DateFunctions.getCurrentDate(DateFunctions.yyyy_MM_dd)
        }
            if (prevDate == null) {
                prevDate = onlyDate
                showDate = true
            } else if (!prevDate.equals(onlyDate)) {
                prevDate = onlyDate
                showDate = true
            } else
                showDate = false

        return when {

//            requestList.size > 0 && showDate -> {
//                TYPE_HEADER_DATE
//            }

           requestList.size > 0 && requestList[position].messageType.equals(MESSAGE_TYPE_IMAGE) -> {
                TYPE_IMAGE
            }

            requestList.size > 0 && requestList[position].messageType.equals(MESSAGE_TYPE_DOCUMENT) -> {
                TYPE_DOCUMENT
            }
            requestList.size > 0 && requestList[position].userid?.equals(getUserObject(context).getId())!! -> {
                TYPE_USER
            }
            requestList.size > 0 && !requestList[position].userid?.equals(getUserObject(context).getId())!! -> {
                TYPE_FRIEND
            }
            else -> position
        }
    }

    private fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is ViewGroup.MarginLayoutParams) {
            val p = view.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }

}



