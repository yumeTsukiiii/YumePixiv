package fan.yumetsuki.yumepixiv.data.model

data class UserModel(
        val id: Long,
        val name: String,
        val account: String,
        val avatar: String?,
        val isFollowed: Boolean
    )



