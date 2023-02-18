package com.lj.koroutine.koroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job

/**
 * Contain methods to run coroutine jobs.
 */
interface JobRunner {

    /**
     * Default dispatcher used.
     */
    val defaultDispatcher: CoroutineDispatcher

    /**
     * Default timeout in milliseconds.
     */
    val defaultTimeout: Long

    /**
     * Set default dispatcher used.
     */
    fun setDefaultDispatcherForAllScope(dispatcherToUse: CoroutineDispatcher)

    /**
     * Set default timeout in milliseconds.
     */
    fun setDefaultTimeoutForAllScope(timeoutInMillis: Long)

    /**
     * Launches a new coroutine without blocking the current thread and returns
     * a reference to the coroutine as a Job.
     *
     * The coroutine is launched in the [dispatcherToUse] context. The default context
     * is [defaultDispatcher].
     * The coroutine timeout is [timeoutInMillis]. The default timeout is [defaultTimeout].
     * The coroutine runs [method].
     */
    fun runFireAndForgetWithTimeout(
        dispatcherToUse: CoroutineDispatcher? = defaultDispatcher,
        timeoutInMillis: Long? = defaultTimeout,
        method: suspend () -> Unit
    ): Job

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
    fun runFireAndForgetWithTimeoutAndCallbackWhenError(
        dispatcherToUse: CoroutineDispatcher? = defaultDispatcher,
        timeoutInMillis: Long? = defaultTimeout,
        method: suspend () -> Unit,
        callback: suspend () -> Unit
    ): Job

    /**
     * Cancel all jobs in the current scope.
     */
    fun cancel()
}
