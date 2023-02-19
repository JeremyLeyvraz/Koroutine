package com.lj.koroutine.koroutine

import kotlinx.coroutines.*

/**
 * Contain methods to run coroutine jobs.
 */
class JobRunnerImpl : JobRunner {

    /**
     * Coroutine scope of the manager.
     */
    private val scope: CoroutineScope = CoroutineScope(Job())

    /**
     * Default dispatcher used.
     */
    override var defaultDispatcher: CoroutineDispatcher = DEFAULT_DISPATCHER

    /**
     * Default timeout in milliseconds.
     */
    override var defaultTimeout: Long = DEFAULT_TIMEOUT

    /**
     * Set a [dispatcherToUse] for all coroutines launched in the scope.
     */
    override fun setDefaultDispatcherForAllScope(dispatcherToUse: CoroutineDispatcher) {
        defaultDispatcher = dispatcherToUse
    }

    /**
     * Set a [timeoutInMillis] timeout for all coroutines launched in the scope.
     */
    override fun setDefaultTimeoutForAllScope(timeoutInMillis: Long) {
        defaultTimeout = timeoutInMillis
    }

    /**
     * Launches a new coroutine without blocking the current thread and returns
     * a reference to the coroutine as a Job.
     *
     * The coroutine is launched in the [dispatcherToUse] context. The default context
     * is [defaultDispatcher].
     * The coroutine runs [method].
     */
    override fun runFireAndForget(
        dispatcherToUse: CoroutineDispatcher?,
        method: suspend () -> Unit
    ): Job {
        val dispatcher = dispatcherToUse ?: defaultDispatcher

        return scope.launch(dispatcher) {
            method()
        }
    }

    /**
     * Launches a new coroutine without blocking the current thread and returns
     * a reference to the coroutine as a Job.
     *
     * The coroutine is launched in the [dispatcherToUse] context. The default context
     * is [defaultDispatcher].
     * The coroutine timeout is [timeoutInMillis]. The default timeout is [defaultTimeout].
     * The coroutine runs [method].
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
     * Launches a new coroutine without blocking the current thread and returns
     * a reference to the coroutine as a Job.
     *
     * The coroutine is launched in the [dispatcherToUse] context. The default context
     * is [defaultDispatcher].
     * The coroutine timeout is [timeoutInMillis]. The default timeout is [defaultTimeout].
     * The coroutine runs [method].
     * When the timeout is reached, [callback] is run.
     */
    override fun runFireAndForgetWithTimeoutAndCallbackWhenError(
        dispatcherToUse: CoroutineDispatcher?,
        timeoutInMillis: Long?,
        method: suspend () -> Unit,
        callback: suspend () -> Unit
    ): Job {
        val dispatcher = dispatcherToUse ?: defaultDispatcher
        val timeout = timeoutInMillis ?: defaultTimeout

        return scope.launch(dispatcher) {
            try {
                withTimeout(timeout) {
                    method()
                }
            } catch (e: TimeoutCancellationException) {
                callback()
            }
        }
    }

    /**
     * Cancel all jobs of the current [scope].
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
         * Default dispatcher.
         */
        val DEFAULT_DISPATCHER: CoroutineDispatcher = Dispatchers.Unconfined
    }
}