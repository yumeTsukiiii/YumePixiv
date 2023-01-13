package fan.yumetsuki.yumepixiv.data.model

data class PKCE(
    val codeVerifier: String,
    val codeChallenge: String
)