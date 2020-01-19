package io.keepcoding.eh_ho.posts


import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import io.keepcoding.eh_ho.LoadingDialogFragment
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.CreatePostModel
import io.keepcoding.eh_ho.data.PostsRepo
import io.keepcoding.eh_ho.data.RequestError
import io.keepcoding.eh_ho.topics.TAG_LOADING_DIALOG
import kotlinx.android.synthetic.main.fragment_create_post.*
import kotlinx.android.synthetic.main.fragment_create_post.parentLayout


class CreatePostFragment : Fragment() {

    var topic_id: String? = null
    var listener: CreatePostInteractionListener? = null
    lateinit var loadingDialogFragment: LoadingDialogFragment

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is CreatePostInteractionListener)
            listener = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        topic_id = arguments?.getString(EXTRA_TOPIC_ID)
        setHasOptionsMenu(true)
        loadingDialogFragment = LoadingDialogFragment.newInstance(getString(R.string.label_create_post))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_post, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_send_post, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topicTitle = arguments?.getString(EXTRA_TOPIC_TITLE)
        labelTitle.text = topicTitle
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.menu_button_send_post -> createPost()
        }
        return super.onOptionsItemSelected(item)

    }

    private fun enableLoadingDialog(enable: Boolean) {
        if (enable)
            loadingDialogFragment.show(childFragmentManager, TAG_LOADING_DIALOG)
        else
            loadingDialogFragment.dismiss()
    }

    private fun createPost() {
        if (isFormValid()) {
            postPost()
        }
        else
            showErrors()
    }

    private fun postPost() {
        val model = CreatePostModel(
            editPost.text.toString(),
            topic_id.toString().toInt()
        )
        context?.let {
            enableLoadingDialog(true)
            PostsRepo.createPost(
                it,
                model,
                {
                    enableLoadingDialog(false)
                    listener?.onPostCreated()
                },
                {
                    enableLoadingDialog(false)
                    handleError(it)
                }
            )
        }
    }

    private fun handleError(requestError: RequestError) {
        val message = if (requestError.messageId != null)
            getString(requestError.messageId)
        else if (requestError.message != null)
            requestError.message
        else
            getString(R.string.error_request_default)

        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showErrors() {
        if (editPost.text?.isEmpty() == true)
            editPost.error = getString(R.string.error_empty)
    }

    private fun isFormValid() =
        editPost.text?.isNotEmpty() ?: false

    interface CreatePostInteractionListener {
        fun onPostCreated()
    }

}
