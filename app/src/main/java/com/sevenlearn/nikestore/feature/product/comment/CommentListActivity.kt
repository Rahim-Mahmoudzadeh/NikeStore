package com.sevenlearn.nikestore.feature.product.comment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sevenlearn.nikestore.R
import com.sevenlearn.nikestore.common.EXTRA_KEY_ID
import com.sevenlearn.nikestore.common.NikeActivity
import com.sevenlearn.nikestore.data.Comment
import com.sevenlearn.nikestore.feature.product.CommentAdapter
import kotlinx.android.synthetic.main.activity_comment_list.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CommentListActivity : NikeActivity() {
    val viewModel: CommentListViewModel by viewModel {
        parametersOf(
            intent.extras!!.getInt(
                EXTRA_KEY_ID
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_list)

        viewModel.progressBarLiveData.observe(this){
            setProgressIndicator(it)
        }

        viewModel.commentsLiveData.observe(this) {
            val adapter = CommentAdapter(true)
            commentsRv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            adapter.comments = it as ArrayList<Comment>
            commentsRv.adapter = adapter
        }

        commentListToolbar.onBackButtonClickListener= View.OnClickListener {
            finish()
        }
    }
}