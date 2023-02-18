package com.lj.koroutine.koroutine

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

/**
 * Test class for the method [JobRunnerImpl.runFireAndForgetWithTimeoutAndCallbackWhenError].
 */
@ExperimentalCoroutinesApi
class RunFireAndForgetWithTimeoutAndCallbackTest {

    /**
    * Test dispatcher to control the time.
    */
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule(testDispatcher)

    @Test
    fun runFireAndForgetWithTimeoutAndCallback_whenIsFireAndTimeoutReach_thenVerifyCallbackIsCall() = runTest {
        /** Arrange **/
        val methodDurationInMillis: Long = 30_000 // 30 seconds
        val jobTimeoutInMillis: Long = 10_000 // 10 second -> Timeout will be reached
        val callbackDurationInMillis: Long = 10_000 // 10 seconds
        val testDurationInMillis: Long = 60_000 // 60 seconds
        var isMethodComplete = false
        var isJobStarted = false
        var isCallbackStarted = false
        var isCallbackComplete = false
        val sut = JobRunnerImpl()
        sut.setDefaultDispatcherForAllScope(testDispatcher)

        /** Act **/
        val job = sut.runFireAndForgetWithTimeoutAndCallbackWhenError(
            dispatcherToUse = testDispatcher,
            timeoutInMillis = jobTimeoutInMillis,
            {
                isJobStarted = true
                delay(methodDurationInMillis) // Virtual time: wait 'methodDurationInMillis' milliseconds -> Timeout reached
                isMethodComplete = true
            },
            {
                isCallbackStarted = true
                delay(callbackDurationInMillis) // Virtual time: wait 'callbackDurationInMillis' milliseconds -> Scheduler not reached
                isCallbackComplete = true // Callback will be completed
            })
        // Advance virtual time: 'testDurationInMillis' milliseconds -> Method timeout reached, callback completed
        testScheduler.apply { advanceTimeBy(testDurationInMillis); runCurrent() }

        /** Assert **/
        Assert.assertTrue(isJobStarted)
        Assert.assertFalse(isMethodComplete)
        Assert.assertTrue(isCallbackStarted)
        Assert.assertTrue(isCallbackComplete)
        Assert.assertFalse(job.isActive)
    }
}