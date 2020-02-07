package guru.stefma.tino.domain.usecase

interface UseCase<R> {
    operator fun invoke(): R
}

interface ParamizedUseCase<P, R> {
    operator fun invoke(param: P): R
}

interface SuspendingUseCase<R> {
    suspend operator fun invoke(): R
}