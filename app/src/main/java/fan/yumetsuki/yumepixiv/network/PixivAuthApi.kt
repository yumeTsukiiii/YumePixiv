package fan.yumetsuki.yumepixiv.network

interface PixivAuthApi {

    suspend fun refreshToken()

}