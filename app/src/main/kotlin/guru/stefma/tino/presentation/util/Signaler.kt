package guru.stefma.tino.presentation.util

import io.reactivex.subjects.PublishSubject

inline class Signaler<T>(val origin: PublishSubject<T> = PublishSubject.create()) {
    fun send(value: T) = origin.onNext(value)
}