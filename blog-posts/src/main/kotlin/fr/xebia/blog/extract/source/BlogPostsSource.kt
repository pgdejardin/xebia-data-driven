package fr.xebia.blog.extract.source

import fr.xebia.blog.extract.Post

interface BlogPostsSource {
    fun find(tag: String?): List<Post>
//    fun find(tag: String?): String
}
