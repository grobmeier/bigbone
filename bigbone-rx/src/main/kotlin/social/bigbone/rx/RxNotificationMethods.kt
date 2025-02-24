package social.bigbone.rx

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import social.bigbone.MastodonClient
import social.bigbone.api.Pageable
import social.bigbone.api.Range
import social.bigbone.api.entity.Notification
import social.bigbone.api.method.NotificationMethods

/**
 * Reactive implementation of [NotificationMethods].
 * Allows access to API methods with endpoints having an "api/vX/notifications" or "api/vX/notification" prefix.
 * @see <a href="https://docs.joinmastodon.org/methods/notifications/">Mastodon notifications API methods</a>
 */
class RxNotificationMethods(client: MastodonClient) {
    private val notificationMethods = NotificationMethods(client)

    fun getNotifications(range: Range): Single<Pageable<Notification>> {
        return Single.create {
            try {
                val notifications = notificationMethods.getNotifications(range)
                it.onSuccess(notifications.execute())
            } catch (e: Throwable) {
                it.onError(e)
            }
        }
    }

    fun getNotification(id: String): Single<Notification> {
        return Single.create {
            try {
                val notification = notificationMethods.getNotification(id)
                it.onSuccess(notification.execute())
            } catch (e: Throwable) {
                it.onError(e)
            }
        }
    }

    fun clearNotifications(): Completable {
        return Completable.create {
            try {
                notificationMethods.clearNotifications()
                it.onComplete()
            } catch (e: Throwable) {
                it.onError(e)
            }
        }
    }
}
