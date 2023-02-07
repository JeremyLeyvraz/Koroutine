package com.lj.koroutine.koroutine

import kotlinx.coroutines.*

/**
 * Run jobs in a custom coroutine with a specific scope.
 */
class JobRunnerImpl : JobRunner {

    /**
     * Coroutine scope of the manager.
     */
    private val scope: CoroutineScope = CoroutineScope(Job())

    /**
     * Default dispatcher used.
     * Back property.
     */
    private var _defaultDispatcher = DEFAULT_DISPATCHER

    /**
     * Default dispatcher used.
     * Public property.
     */
    override val defaultDispatcher: CoroutineDispatcher get() = _defaultDispatcher

    /**
     * Default timeout in milliseconds.
     * Back property.
     */
    private var _defaultTimeout = DEFAULT_TIMEOUT // 10 seconds

    /**
     * Default timeout in milliseconds.
     * Public property.
     */
    override val defaultTimeout: Long get() = _defaultTimeout

    /**
     * Set a default dispatcher for all coroutines launched in the scope.
     *
     * @param dispatcherToUse Dispatcher to use by default.
     */
    override fun setDefaultDispatcherForAllScope(dispatcherToUse: CoroutineDispatcher) {
        _defaultDispatcher = dispatcherToUse
    }

    /**
     * Set a default timeout for all coroutines launched in the scope.
     *
     * @param timeoutInMillis Timeout in milliseconds to use by default.
     */
    override fun setDefaultTimeoutForAllScope(timeoutInMillis: Long) {
        _defaultTimeout = timeoutInMillis
    }

    /**
     * Run a fire and forget a job.
     *
     * @return The running job.
     */
    override fun runFireAndForgetWithTimeout(
        dispatcherToUse: CoroutineDispatcher?,
        timeoutInMillis: Long?,
        method: suspend () -> Unit
    ): Job {
        val dispatcher = dispatcherToUse ?: defaultDispatcher
        val timeout = timeoutInMillis ?: defaultTimeout

        return scope.launch(dispatcher) {
            withTimeout(timeout) {
                method()
            }
        }
    }

    /**
     * Cancel all jobs of the scope.
     */
    override fun cancel() {
        scope.cancel()
    }

    companion object {
        /**
         * Default timeout: 10 seconds.
         */
        const val DEFAULT_TIMEOUT: Long = 10_000

        /**
         * Default dispatchers.
         */
        val DEFAULT_DISPATCHER: CoroutineDispatcher = Dispatchers.Unconfined
    }
}