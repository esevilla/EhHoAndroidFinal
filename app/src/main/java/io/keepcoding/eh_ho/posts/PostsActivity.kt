package io.keepcoding.eh_ho.posts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.keepcoding.eh_ho.R
import java.lang.IllegalArgumentException

const val EXTRA_TOPIC_ID = "topic_id"
const val EXTRA_TOPIC_TITLE = "topic_title"
const val TRANSACTION_CREATE_POST = "create_post"

class PostsActivity : AppCompatActivity(), PostsFragment.PostsInteractionListener, CreatePostFragment.CreatePostInteractionListener {
    var topic_title : String? = null
    var topic_id : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        topic_id = intent.getStringExtra(EXTRA_TOPIC_ID)
        topic_title = intent.getStringExtra(EXTRA_TOPIC_TITLE)

        val args = Bundle()
        args.putString(EXTRA_TOPIC_ID, topic_id)
        val postsFragment = PostsFragment()
        postsFragment.arguments = args

        if (topic_id !=null ) {
           if (savedInstanceState == null){
               supportFragmentManager.beginTransaction()
                   .add(R.id.fragmentContainer, postsFragment)
                   .commit()
           }
        } else {
            throw IllegalArgumentException("The topic id is null")
        }
    }

    override fun onGoToCreatePost() {
        val args = Bundle()
        args.putString(EXTRA_TOPIC_TITLE, topic_title)
        args.putString(EXTRA_TOPIC_ID, topic_id)
        val createPostFragment = CreatePostFragment()
        createPostFragment.arguments = args

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, createPostFragment)
            .addToBackStack(TRANSACTION_CREATE_POST)
            .commit()
    }

    override fun onPostCreated() {
        supportFragmentManager.popBackStack()
    }
}
