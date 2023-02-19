package com.lj.koroutine.koroutine

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

/**
 * Test class for the method [JobRunnerImpl.runFireAndForget].
 */
@ExperimentalCoroutinesApi
class RunFireAndForgetTest {

    /**
     * Test dispatcher to control the time.
     */
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule(testDispatcher)

    /**
     * Run and forget a job.
     * The test simulates a short time to not let the coroutine end:
     * - The method started.
     * - The method was not completed.
     * - The coroutine job is active.
     */
    @Test
    fun runFireAndForget_whenIsFireButJobNotComplete_thenVerifyJobIsActiveButNotComplete() = runTest {
        /** Arrange **/
        val methodDurationInMillis: Long = 10_000 // 10 seconds
        val testDurationInMillis: Long = 5_000 // 5 seconds
        var isMethodComplete = false
        var isJobStarted = false
        val sut = JobRunnerImpl()

        /** Act **/
        val job = sut.runFireAndForget(dispatcherToUse = testDispatcher) {
            isJobStarted = true
            delay(methodDurationInMillis) // Virtual time: wait 'methodDurationInMillis' milliseconds -> Timeout reached
            isMethodComplete = true
        }
        // Advance virtual time: 'testDurationInMillis' milliseconds -> Job completed with timeout
        testScheduler.apply { advanceTimeBy(testDurationInMillis); runCurrent() }

        /** Assert **/
        Assert.assertTrue(isJobStarted)
        Assert.assertFalse(isMethodComplete)
        Assert.assertTrue(job.isActive)
    }

    /**
     * Run and forget a job.
     * The test simulates a long time to let the coroutine end:
     * - The method started.
     * - The method is completed.
     * - The coroutine job is no longer active.
     */
    @Test
    fun runFireAndForget_whenIsFireAndJobComplete_thenVerifyJobIsNotActiveButComplete() = runTest {
        /** Arrange **/
        val methodDurationInMillis: Long = 5_000 // 5 seconds
        val testDurationInMillis: Long = 10_000 // 10 seconds
        var isMethodComplete = false
        var isJobStarted = false
        val sut = JobRunnerImpl()

        /** Act **/
        val job = sut.runFireAndForget(dispatcherToUse = testDispatcher) {
            isJobStarted = true
            delay(methodDurationInMillis) // Virtual time: wait 'methodDurationInMillis' milliseconds -> Timeout reached
            isMethodComplete = true
        }
        // Advance virtual time: 'testDurationInMillis' milliseconds -> Job completed with timeout
        testScheduler.apply { advanceTimeBy(testDurationInMillis); runCurrent() }

        /** Assert **/
        Assert.assertTrue(isJobStarted)
        Assert.assertTrue(isMethodComplete)
        Assert.assertFalse(job.isActive)
    }


    /**
     * Run and forget a job.
     * The test simulates a short time to not let the coroutine end:
     * - The method started.
     * - The method was not completed.
     * - The coroutine job is active.
     */
    @Test
    fun runFireAndForget_whenIsFireButJobNotCompleteWithDefaultDispatcher_thenVerifyJobIsActiveButNotComplete() = runTest {
        /** Arrange **/
        val methodDurationInMillis: Long = 10_000 // 10 seconds
        val testDurationInMillis: Long = 5_000 // 5 seconds
        var isMethodComplete = false
        var isJobStarted = false
        val sut = JobRunnerImpl()
        sut.setDefaultDispatcherForAllScope(testDispatcher)

        /** Act **/
        val job = sut.runFireAndForget {
            isJobStarted = true
            delay(methodDurationInMillis) // Virtual time: wait 'methodDurationInMillis' milliseconds -> Timeout reached
            isMethodComplete = true
        }
        // Advance virtual time: 'testDurationInMillis' milliseconds -> Job completed with timeout
        testScheduler.apply { advanceTimeBy(testDurationInMillis); runCurrent() }

        /** Assert **/
        Assert.assertTrue(isJobStarted)
        Assert.assertFalse(isMethodComplete)
        Assert.assertTrue(job.isActive)
    }

    /**
     * Run and forget a job.
     * The test simulates a long time to let the coroutine end:
     * - The method started.
     * - The method is completed.
     * - The coroutine job is no longer active.
     */
    @Test
    fun runFireAndForget_whenIsFireAndJobCompleteWithDefaultDispatcher_thenVerifyJobIsNotActiveButComplete() = runTest {
        /** Arrange **/
        val methodDurationInMillis: Long = 5_000 // 5 seconds
        val testDurationInMillis: Long = 10_000 // 10 seconds
        var isMethodComplete = false
        var isJobStarted = false
        val sut = JobRunnerImpl()
        sut.setDefaultDispatcherForAllScope(testDispatcher)

        /** Act **/
        val job = sut.runFireAndForget {
            isJobStarted = true
            delay(methodDurationInMillis) // Virtual time: wait 'methodDurationInMillis' milliseconds -> Timeout reached
            isMethodComplete = true
        }
        // Advance virtual time: 'testDurationInMillis' milliseconds -> Job completed with timeout
        testScheduler.apply { advanceTimeBy(testDurationInMillis); runCurrent() }

        /** Assert **/
        Assert.assertTrue(isJobStarted)
        Assert.assertTrue(isMethodComplete)
        Assert.assertFalse(job.isActive)
    }
}