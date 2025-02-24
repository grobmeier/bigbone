package social.bigbone.api.method

import social.bigbone.MastodonClient
import social.bigbone.MastodonRequest
import social.bigbone.Parameters
import social.bigbone.api.Pageable
import social.bigbone.api.Range
import social.bigbone.api.entity.Account
import social.bigbone.api.entity.Context
import social.bigbone.api.entity.ScheduledStatus
import social.bigbone.api.entity.Status
import social.bigbone.api.entity.Translation
import social.bigbone.api.exception.BigBoneRequestException

/**
 * Allows access to API methods with endpoints having an "api/vX/statuses" prefix.
 * @see <a href="https://docs.joinmastodon.org/methods/statuses/">Mastodon statuses API methods</a>
 */
class StatusMethods(private val client: MastodonClient) {

    /**
     * Obtain information about a status.
     * @param statusId ID of the status.
     * @see <a href="https://docs.joinmastodon.org/methods/statuses/#get">Mastodon API documentation: methods/statuses/#get</a>
     */
    @Throws(BigBoneRequestException::class)
    fun getStatus(statusId: String): MastodonRequest<Status> {
        return client.getMastodonRequest(
            endpoint = "api/v1/statuses/$statusId",
            method = MastodonClient.Method.GET
        )
    }

    /**
     * View statuses above and below this status in the thread.
     * @param statusId ID of the status.
     * @see <a href="https://docs.joinmastodon.org/methods/statuses/#context">Mastodon API documentation: methods/statuses/#context</a>
     */
    @Throws(BigBoneRequestException::class)
    fun getContext(statusId: String): MastodonRequest<Context> {
        return client.getMastodonRequest(
            endpoint = "api/v1/statuses/$statusId/context",
            method = MastodonClient.Method.GET
        )
    }

    /**
     * Translate the status content into some language.
     * @param statusId The ID of the Status in the database.
     * @param language ISO 639 language code. The status content will be translated into this language. Defaults to the user’s current locale.
     * @see <a href="https://docs.joinmastodon.org/methods/statuses/#translate">Mastodon API documentation: methods/statuses/#translate</a>
     */
    @JvmOverloads
    @Throws(BigBoneRequestException::class)
    fun translateStatus(statusId: String, language: String? = null): MastodonRequest<Translation> {
        return client.getMastodonRequest(
            endpoint = "api/v1/statuses/$statusId/translate",
            method = MastodonClient.Method.POST,
            parameters = Parameters().apply {
                language?.let { append("lang", it) }
            }
        )
    }

    /**
     * View who boosted a given status.
     * @param statusId ID of the status.
     * @param range optional Range for the pageable return value
     * @see <a href="https://docs.joinmastodon.org/methods/statuses/#reblogged_by">Mastodon API documentation: methods/statuses/#reblogged_by</a>
     */
    @JvmOverloads
    @Throws(BigBoneRequestException::class)
    fun getRebloggedBy(statusId: String, range: Range = Range()): MastodonRequest<Pageable<Account>> {
        return client.getPageableMastodonRequest(
            endpoint = "api/v1/statuses/$statusId/reblogged_by",
            method = MastodonClient.Method.GET,
            parameters = range.toParameters()
        )
    }

    /**
     * View who favourited a given status.
     * @param statusId ID of the status.
     * @param range optional Range for the pageable return value
     * @see <a href="https://docs.joinmastodon.org/methods/statuses/#favourited_by">Mastodon API documentation: methods/statuses/#favourited_by</a>
     */
    @JvmOverloads
    @Throws(BigBoneRequestException::class)
    fun getFavouritedBy(statusId: String, range: Range = Range()): MastodonRequest<Pageable<Account>> {
        return client.getPageableMastodonRequest(
            endpoint = "api/v1/statuses/$statusId/favourited_by",
            method = MastodonClient.Method.GET,
            parameters = range.toParameters()
        )
    }

    /**
     * Publish a status with the given parameters. To publish a status containing a poll, use [postPoll].
     * To schedule a status, use [scheduleStatus].
     * @param status the text of the status
     * @param inReplyToId the local id of the status you want to reply to
     * @param mediaIds the array of media ids to attach to the status (maximum 4)
     * @param sensitive set this to mark the media of the status as NSFW
     * @param spoilerText text to be shown as a warning before the actual content
     * @param visibility either "direct", "private", "unlisted" or "public"
     * @param language ISO 639 language code for this status.
     * @see <a href="https://docs.joinmastodon.org/methods/statuses/#create">Mastodon API documentation: methods/statuses/#create</a>
     */
    @JvmOverloads
    @Throws(BigBoneRequestException::class)
    fun postStatus(
        status: String,
        inReplyToId: String?,
        mediaIds: List<String>?,
        sensitive: Boolean,
        spoilerText: String?,
        visibility: Status.Visibility = Status.Visibility.Public,
        language: String?
    ): MastodonRequest<Status> {
        return client.getMastodonRequest(
            endpoint = "api/v1/statuses",
            method = MastodonClient.Method.POST,
            parameters = Parameters().apply {
                append("status", status)
                inReplyToId?.let { append("in_reply_to_id", it) }
                mediaIds?.let { append("media_ids", it) }
                append("sensitive", sensitive)
                spoilerText?.let { append("spoiler_text", it) }
                append("visibility", visibility.value)
                language?.let { append("language", it) }
            }
        )
    }

    /**
     * Publish a status containing a poll with the given parameters. To schedule a poll status, use [schedulePoll].
     * @param status the text of the status
     * @param pollOptions Possible answers to the poll.
     * @param pollExpiresIn Duration that the poll should be open, in seconds.
     * @param pollMultiple Allow multiple choices? Defaults to false.
     * @param pollHideTotals Hide vote counts until the poll ends? Defaults to false.
     * @param inReplyToId the local id of the status you want to reply to
     * @param sensitive set this to mark the media of the status as NSFW
     * @param spoilerText text to be shown as a warning before the actual content
     * @param visibility either "direct", "private", "unlisted" or "public"
     * @param language ISO 639 language code for this status.
     * @see <a href="https://docs.joinmastodon.org/methods/statuses/#create">Mastodon API documentation: methods/statuses/#create</a>
     */
    @JvmOverloads
    @Throws(BigBoneRequestException::class)
    fun postPoll(
        status: String,
        pollOptions: List<String>,
        pollExpiresIn: Int,
        pollMultiple: Boolean = false,
        pollHideTotals: Boolean = false,
        inReplyToId: String?,
        sensitive: Boolean,
        spoilerText: String?,
        visibility: Status.Visibility = Status.Visibility.Public,
        language: String?
    ): MastodonRequest<Status> {
        return client.getMastodonRequest(
            endpoint = "api/v1/statuses",
            method = MastodonClient.Method.POST,
            parameters = Parameters().apply {
                append("status", status)
                append("poll[options]", pollOptions)
                append("poll[expires_in]", pollExpiresIn)
                append("poll[multiple]", pollMultiple)
                append("poll[hide_totals", pollHideTotals)
                inReplyToId?.let { append("in_reply_to_id", it) }
                append("sensitive", sensitive)
                spoilerText?.let { append("spoiler_text", it) }
                append("visibility", visibility.value)
                language?.let { append("language", it) }
            }
        )
    }

    /**
     * Schedule a status with the given parameters. To schedule a status containing a poll, use [schedulePoll].
     * To post a status immediately, use [postStatus].
     * @param status the text of the status
     * @param inReplyToId the local id of the status you want to reply to
     * @param mediaIds the array of media ids to attach to the status (maximum 4)
     * @param sensitive set this to mark the media of the status as NSFW
     * @param spoilerText text to be shown as a warning before the actual content
     * @param visibility either "direct", "private", "unlisted" or "public"
     * @param language ISO 639 language code for this status.
     * @param scheduledAt ISO 8601 Datetime at which to schedule a status. Must be at least 5 minutes in the future.
     * @see <a href="https://docs.joinmastodon.org/methods/statuses/#create">Mastodon API documentation: methods/statuses/#create</a>
     */
    @JvmOverloads
    @Throws(BigBoneRequestException::class)
    fun scheduleStatus(
        status: String,
        inReplyToId: String?,
        mediaIds: List<String>?,
        sensitive: Boolean,
        spoilerText: String?,
        visibility: Status.Visibility = Status.Visibility.Public,
        language: String?,
        scheduledAt: String
    ): MastodonRequest<ScheduledStatus> {
        return client.getMastodonRequest(
            endpoint = "api/v1/statuses",
            method = MastodonClient.Method.POST,
            parameters = Parameters().apply {
                append("status", status)
                inReplyToId?.let { append("in_reply_to_id", it) }
                mediaIds?.let { append("media_ids", it) }
                append("sensitive", sensitive)
                spoilerText?.let { append("spoiler_text", it) }
                append("visibility", visibility.value)
                language?.let { append("language", it) }
                append("scheduled_at", scheduledAt)
            }
        )
    }

    /**
     * Schedule a status containing a poll with the given parameters. To post immediately, use [postPoll].
     * @param status the text of the status
     * @param pollOptions Possible answers to the poll.
     * @param pollExpiresIn Duration that the poll should be open, in seconds.
     * @param pollMultiple Allow multiple choices? Defaults to false.
     * @param pollHideTotals Hide vote counts until the poll ends? Defaults to false.
     * @param inReplyToId the local id of the status you want to reply to
     * @param sensitive set this to mark the media of the status as NSFW
     * @param spoilerText text to be shown as a warning before the actual content
     * @param visibility either "direct", "private", "unlisted" or "public"
     * @param language ISO 639 language code for this status.
     * @param scheduledAt ISO 8601 Datetime at which to schedule a status. Must be at least 5 minutes in the future.
     * @see <a href="https://docs.joinmastodon.org/methods/statuses/#create">Mastodon API documentation: methods/statuses/#create</a>
     */
    @JvmOverloads
    @Throws(BigBoneRequestException::class)
    fun schedulePoll(
        status: String,
        pollOptions: List<String>,
        pollExpiresIn: Int,
        pollMultiple: Boolean = false,
        pollHideTotals: Boolean = false,
        inReplyToId: String?,
        sensitive: Boolean,
        spoilerText: String?,
        visibility: Status.Visibility = Status.Visibility.Public,
        language: String?,
        scheduledAt: String
    ): MastodonRequest<ScheduledStatus> {
        return client.getMastodonRequest(
            endpoint = "api/v1/statuses",
            method = MastodonClient.Method.POST,
            parameters = Parameters().apply {
                append("status", status)
                append("poll[options]", pollOptions)
                append("poll[expires_in]", pollExpiresIn)
                append("poll[multiple]", pollMultiple)
                append("poll[hide_totals]", pollHideTotals)
                inReplyToId?.let { append("in_reply_to_id", it) }
                append("sensitive", sensitive)
                spoilerText?.let { append("spoiler_text", it) }
                append("visibility", visibility.value)
                language?.let { append("language", it) }
                append("scheduled_at", scheduledAt)
            }
        )
    }

    /**
     * Delete one of your own statuses.
     * @param statusId ID of the status.
     * @see <a href="https://docs.joinmastodon.org/methods/statuses/#delete">Mastodon API documentation: methods/statuses/#delete</a>
     */
    @Throws(BigBoneRequestException::class)
    fun deleteStatus(statusId: String): MastodonRequest<Status> {
        return client.getMastodonRequest(
            endpoint = "api/v1/statuses/$statusId",
            method = MastodonClient.Method.DELETE
        )
    }

    /**
     * Reshare a status on your own profile.
     * @param statusId ID of the status.
     * @param visibility Any visibility except limited or direct (i.e. public, unlisted, private). Defaults to public.
     *  Currently unused in UI.
     * @see <a href="https://docs.joinmastodon.org/methods/statuses/#boost">Mastodon API documentation: methods/statuses/#boost</a>
     */
    @JvmOverloads
    @Throws(BigBoneRequestException::class)
    fun reblogStatus(statusId: String, visibility: Status.Visibility = Status.Visibility.Public): MastodonRequest<Status> {
        if (visibility != Status.Visibility.Public &&
            visibility != Status.Visibility.Unlisted &&
            visibility != Status.Visibility.Private) {
            throw BigBoneRequestException("Visibility must be one of: public, unlisted, private when reblogging.")
        }
        return client.getMastodonRequest(
            endpoint = "api/v1/statuses/$statusId/reblog",
            method = MastodonClient.Method.POST,
            parameters = Parameters().apply {
                append("visibility", visibility.value)
            }
        )
    }

    /**
     * Undo a reshare of a status.
     * @param statusId ID of the status.
     * @see <a href="https://docs.joinmastodon.org/methods/statuses/#unreblog">Mastodon API documentation: methods/statuses/#unreblog</a>
     */
    @Throws(BigBoneRequestException::class)
    fun unreblogStatus(statusId: String): MastodonRequest<Status> {
        return client.getMastodonRequest(
            endpoint = "api/v1/statuses/$statusId/unreblog",
            method = MastodonClient.Method.POST
        )
    }

    /**
     * Favourite a status.
     * @param statusId ID of the status.
     * @see <a href="https://docs.joinmastodon.org/methods/statuses/#favourite">Mastodon API documentation: methods/statuses/#favourite</a>
     */
    @Throws(BigBoneRequestException::class)
    fun favouriteStatus(statusId: String): MastodonRequest<Status> {
        return client.getMastodonRequest(
            endpoint = "api/v1/statuses/$statusId/favourite",
            method = MastodonClient.Method.POST
        )
    }

    /**
     * Remove a status from your favourites list.
     * @param statusId ID of the status.
     * @see <a href="https://docs.joinmastodon.org/methods/statuses/#unfavourite">Mastodon API documentation: methods/statuses/#unfavourite</a>
     */
    @Throws(BigBoneRequestException::class)
    fun unfavouriteStatus(statusId: String): MastodonRequest<Status> {
        return client.getMastodonRequest(
            endpoint = "api/v1/statuses/$statusId/unfavourite",
            method = MastodonClient.Method.POST
        )
    }

    /**
     * Privately bookmark a status.
     * @param statusId ID of the status.
     * @see <a href="https://docs.joinmastodon.org/methods/statuses/#bookmark">Mastodon API documentation: methods/statuses/#bookmark</a>
     */
    @Throws(BigBoneRequestException::class)
    fun bookmarkStatus(statusId: String): MastodonRequest<Status> {
        return client.getMastodonRequest(
            endpoint = "api/v1/statuses/$statusId/bookmark",
            method = MastodonClient.Method.POST
        )
    }

    /**
     * Remove a status from your private bookmarks.
     * @param statusId ID of the status.
     * @see <a href="https://docs.joinmastodon.org/methods/statuses/#unbookmark">Mastodon API documentation: methods/statuses/#unbookmark</a>
     */
    @Throws(BigBoneRequestException::class)
    fun unbookmarkStatus(statusId: String): MastodonRequest<Status> {
        return client.getMastodonRequest(
            endpoint = "api/v1/statuses/$statusId/unbookmark",
            method = MastodonClient.Method.POST
        )
    }

    /**
     * Do not receive notifications for the thread that this status is part of. Must be a thread in which you are a participant.
     * @param statusId ID of the status.
     * @see <a href="https://docs.joinmastodon.org/methods/statuses/#mute">Mastodon API documentation: methods/statuses/#mute</a>
     */
    @Throws(BigBoneRequestException::class)
    fun muteConversation(statusId: String): MastodonRequest<Status> {
        return client.getMastodonRequest(
            endpoint = "api/v1/statuses/$statusId/mute",
            method = MastodonClient.Method.POST
        )
    }

    /**
     * Start receiving notifications again for the thread that this status is part of.
     * @param statusId ID of the status.
     * @see <a href="https://docs.joinmastodon.org/methods/statuses/#unmute">Mastodon API documentation: methods/statuses/#unmute</a>
     */
    @Throws(BigBoneRequestException::class)
    fun unmuteConversation(statusId: String): MastodonRequest<Status> {
        return client.getMastodonRequest(
            endpoint = "api/v1/statuses/$statusId/unmute",
            method = MastodonClient.Method.POST
        )
    }

    /**
     * Feature one of your own public statuses at the top of your profile.
     * @param statusId The local ID of the Status in the database. The status should be authored by the authorized account.
     * @see <a href="https://docs.joinmastodon.org/methods/statuses/#pin">Mastodon API documentation: methods/statuses/#pin</a>
     */
    @Throws(BigBoneRequestException::class)
    fun pinStatus(statusId: String): MastodonRequest<Status> {
        return client.getMastodonRequest(
            endpoint = "api/v1/statuses/$statusId/pin",
            method = MastodonClient.Method.POST
        )
    }

    /**
     * Unfeature a status from the top of your profile.
     * @param statusId The local ID of the Status in the database.
     * @see <a href="https://docs.joinmastodon.org/methods/statuses/#unpin">Mastodon API documentation: methods/statuses/#unpin</a>
     */
    @Throws(BigBoneRequestException::class)
    fun unpinStatus(statusId: String): MastodonRequest<Status> {
        return client.getMastodonRequest(
            endpoint = "api/v1/statuses/$statusId/unpin",
            method = MastodonClient.Method.POST
        )
    }
}
