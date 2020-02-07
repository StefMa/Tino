package guru.stefma.tino.presentation.util.viewbinding

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

fun <T : Any> LifecycleOwner.bind(
    observable: Observable<T>,
    source: String = javaClass.simpleName,
    block: (value: T) -> Unit
) {
    lifecycle.addObserver(ObservableBinding(observable, block, source))
}

private class ObservableBinding<T>(
    private val observable: Observable<T>,
    private val block: (value: T) -> Unit,
    private val source: String,
    private val mainScheduler: Scheduler = AndroidSchedulers.mainThread()
) : LifecycleEventObserver, Observer<T> {

    private var disposable: Disposable? = null

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (event) {
            Lifecycle.Event.ON_START -> {
                observable
                    .observeOn(mainScheduler)
                    .subscribe(this)
            }
            Lifecycle.Event.ON_STOP -> {
                synchronized(this) {
                    disposable?.dispose()
                    disposable = null
                }
            }
        }
    }

    override fun onSubscribe(d: Disposable) {
        synchronized(this) { disposable = d }
    }

    override fun onNext(t: T) {
        block(t)
    }

    override fun onComplete() {
        synchronized(this) { disposable = null }
    }

    override fun onError(e: Throwable) {
        throw Exception(source, e)
    }
}
