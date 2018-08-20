package fr.xebia.blog.extract

import java.net.URL

val noPost = emptyList<Post>()

val author1 = Author("username", "first", "last", "https://avatar.com/username")

val cat1 = listOf(Category("Data", "nosql"))
val cat2 = cat1 + listOf(Category("DevOps", "devops"))

val terms1 = Terms(cat1)
val terms2 = Terms(cat2)

val post1 = Post("title", author1, URL("https://blog.xebia.fr/1"), terms1, "description")
val post2 = Post("title2", author1, URL("https://blog.xebia.fr/2"), terms2, "description2")

val posts = listOf(post1, post2)
