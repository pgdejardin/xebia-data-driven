package fr.xebia.blog.extract.store

import fr.xebia.blog.extract.Post

interface BlogPostsStore {
    fun store(category: String, blogPosts: List<Post>)
}
