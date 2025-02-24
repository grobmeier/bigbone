package social.bigbone.api.method

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import social.bigbone.MastodonClient
import social.bigbone.MastodonRequest
import social.bigbone.api.entity.MediaAttachment
import java.io.File

/**
 * Allows access to API methods with endpoints having an "api/vX/media" prefix.
 * @see <a href="https://docs.joinmastodon.org/methods/media/">Mastodon media API methods</a>
 */
class MediaMethods(private val client: MastodonClient) {
    /**
     * Creates an attachment to be used with a new status. This method will return after the full sized media is done processing.
     * @param file the file that should be uploaded
     * @param mediaType media type of the file as a string, e.g. "image/png"
     * @see <a href="https://docs.joinmastodon.org/methods/media/#v1">Mastodon API documentation: methods/media/#v1</a>
     */
    fun uploadMedia(file: File, mediaType: String): MastodonRequest<MediaAttachment> {
        val body = file.asRequestBody(mediaType.toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", file.name, body)
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addPart(part)
            .build()
        return MastodonRequest<MediaAttachment>(
            {
                client.postRequestBody("api/v1/media", requestBody)
            },
            {
                client.getSerializer().fromJson(it, MediaAttachment::class.java)
            }
        )
    }
}
