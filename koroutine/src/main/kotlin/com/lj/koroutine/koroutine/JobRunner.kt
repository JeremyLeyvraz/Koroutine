package com.lj.koroutine.koroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job

/**
 * Interface for a job runner.
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
     * Run a fire and forget a job with a timeout.
     *
     * @param dispatcherToUse
     * @param timeoutInMillis
     * @param method
     *
     * @return The running job.
     */
    fun runFireAndForgetWithTimeout(
        dispatcherToUse: CoroutineDispatcher? = defaultDispatcher,
        timeoutInMillis: Long? = defaultTimeout,
        method: suspend () -> Unit
    ): Job

    /**
     * Cancel all jobs in the scope.
     */
    fun cancel()
}
