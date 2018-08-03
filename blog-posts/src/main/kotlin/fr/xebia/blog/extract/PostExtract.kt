package fr.xebia.blog.extract

data class PostExtract(
    val title: String,
    val author: String,
    val description: String,
    val category: String,
    val tags: List<String>
)
