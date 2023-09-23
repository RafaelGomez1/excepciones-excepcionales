package com.example.excepcionesexcepcionales.user.domain

data class Document(
    val type: DocumentType,
    val version: Version,
    val status: DocumentStatus,
)

enum class DocumentType {
    ID_CARD,
    SELF_PORTRAIT,
    DRIVING_LICENSE
}

enum class DocumentStatus {
    PENDING,
    VERIFIED,
    REJECTED
}

data class Version(private var value: String) {

    init {
        validate()
    }

    private fun validate() {
        if (value.toInt() !in (1..50)) throw InvalidVersionException(value)
    }

    class InvalidVersionException(value: String) : Throwable("Invalid version for document $value")

    override fun toString(): String = value

    fun next() = Version((value.toInt() + 1).toString())

    companion object {
        private const val FIRST_VERSION = "1"
        fun firstVersion() = Version(FIRST_VERSION)
    }
}
