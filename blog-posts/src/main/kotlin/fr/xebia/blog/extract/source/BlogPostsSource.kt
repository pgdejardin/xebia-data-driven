package fr.xebia.blog.extract.source

import fr.xebia.blog.extract.Post

interface BlogPostsSource {
    fun find(category: String, tag: String = ""): List<Post>
}
