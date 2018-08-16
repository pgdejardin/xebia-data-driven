package fr.xebia.blog.extract.store

import fr.xebia.blog.extract.Post
import java.time.LocalDate

interface BlogPostsStore {
    fun store(extractDate: LocalDate = LocalDate.now(), postsDate: LocalDate, blogPosts: List<Post>)
}
