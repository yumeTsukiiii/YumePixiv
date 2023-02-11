package fan.yumetsuki.yumepixiv.data.model

data class ImageModel(
    val original: String? = null,
    val large: String? = null,
    val medium: String? = null,
    val squareMedium: String? = null
) {

    val url: String?
        get() = medium ?: large ?: squareMedium ?: original

}