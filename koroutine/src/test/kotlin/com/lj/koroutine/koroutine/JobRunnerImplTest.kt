package com.lj.koroutine.koroutine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/**
 * Test class for [JobRunnerImpl].
 */
@ExperimentalCoroutinesApi
class JobRunnerImplTest {

    /**
     * Test dispatcher to control the time.
     */
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule(testDispatcher)

    //region runFireAndForgetWithTimeout tests

    /**
     * Run and forget a job with a timeout.
     * The test simulates a long time to let the method ends.
     * The job timeout is reached:
     * - The method started.
     * - The method was not completed.
     * - The coroutine job is no longer active.
     */
    @Test
    fun runFireAndForgetWithTimeout_whenIsFireAndTimeoutReach_thenVerifyJobIsRunButNotComplete() = runTest {
        /** Arrange **/
        val methodDurationInMillis: Long = 30_000 // 30 seconds
        val jobTimeoutInMillis: Long = 10_000 // 10 second -> Timeout will be reached
        val testDurationInMillis: Long = 60_000 // 60 seconds
        var isMethodComplete = false
        var isJobStarted = false
        val sut = JobRunnerImpl()
        sut.setDefaultDispatcherForAllScope(testDispatcher)

        /** Act **/
        val job = sut.runFireAndForgetWithTimeout(dispatcherToUse = testDispatcher, timeoutInMillis = jobTimeoutInMillis) {
            isJobStarted = true
            delay(methodDurationInMillis) // Virtual time: wait 'methodDurationInMillis' milliseconds -> Timeout reached
            isMethodComplete = true
        }
        // Advance virtual time: 'testDurationInMillis' milliseconds -> Job completed with timeout
        testScheduler.apply { advanceTimeBy(testDurationInMillis); runCurrent() }

        /** Assert **/
        assertTrue(isJobStarted)
        assertFalse(isMethodComplete)
        assertFalse(job.isActive)
    }

    /**
     * Run and forget a job with a timeout.
     * The test simulates a long time to let the method ends.
     * The job timeout is not reached:
     * - The method started.
     * - The method completed.
     * - The coroutine job is no longer active.
     */
    @Test
    fun runFireAndForgetWithTimeout_whenIsFireAndTimeoutNotReach_thenVerifyJobIsRunAndComplete() = runTest {
        /** Arrange **/
        val methodDurationInMillis: Long = 30_000 // 30 seconds
        val jobTimeoutInMillis: Long = 60_000 // 60 second -> Timeout will be not reached
        val testDurationInMillis: Long = 40_000 // 40 seconds
        var isMethodComplete = false
        var isJobStarted = false // Not entered in thread
        val sut = JobRunnerImpl()
        sut.setDefaultDispatcherForAllScope(testDispatcher)

        /** Act **/
        val job = sut.runFireAndForgetWithTimeout(dispatcherToUse = testDispatcher, timeoutInMillis = jobTimeoutInMillis) {
            isJobStarted = true
            delay(methodDurationInMillis) // Virtual time: wait 'methodDurationInMillis' milliseconds -> Timeout not reached
            isMethodComplete = true
        }
        // Advance virtual time: 'testDurationInMillis' milliseconds -> Action is completed
        testScheduler.apply { advanceTimeBy(testDurationInMillis); runCurrent() }

        /** Assert **/
        assertTrue(isJobStarted)
        assertTrue(isMethodComplete)
        assertFalse(job.isActive)
    }

    /**
     * Run and forget a job with a timeout.
     * The test simulates a short time to not let the method ends.
     * The job timeout is not reached:
     * - The method started.
     * - The method was not completed.
     * - The coroutine job is active.
     */
    @Test
    fun runFireAndForgetWithTimeout_whenIsFire_thenVerifyIsForget() = runTest {
        /** Arrange **/
        val methodDurationInMillis: Long = 30_000 // 30 seconds
        val jobTimeoutInMillis: Long = 60_000 // 60 second -> Timeout not reached
        val testDurationInMillis: Long = 10_000 // 10 seconds
        var isMethodComplete = false
        var isJobStarted = false // Not entered in thread
        val sut = JobRunnerImpl()
        sut.setDefaultDispatcherForAllScope(testDispatcher)

        /** Act **/
        val job = sut.runFireAndForgetWithTimeout(dispatcherToUse = testDispatcher, timeoutInMillis = jobTimeoutInMillis) {
            isJobStarted = true
            delay(methodDurationInMillis) // Virtual time: wait 'methodDurationInMillis' milliseconds -> Timeout not reached
            isMethodComplete = true
        }
        // Advance virtual time: 'testDurationInMillis' milliseconds -> Action not completed yet
        testScheduler.apply { advanceTimeBy(testDurationInMillis); runCurrent() }

        /** Assert **/
        assertTrue(isJobStarted)
        assertFalse(isMethodComplete)
        assertTrue(job.isActive)
    }

    /**
     * Run and forget a job with a timeout.
     * The test cancels the coroutine before its end, and the test waits a long time after the cancellation to be sure the job was not completed (job really cancelled).
     * The job timeout is not reached:
     * - The method started.
     * - The method was not completed.
     * - The coroutine job is no longer active.
     */
    @Test
    fun runFireAndForgetWithTimeout_whenJobIsCancelBeforeItsEnd_thenActionIsNotComplete() = runTest {
        /** Arrange **/
        val methodDurationInMillis: Long = 30_000 // 30 seconds
        val jobTimeoutInMillis: Long = 60_000 // 60 second -> Timeout not reached
        val testDurationBeforeCancelInMillis: Long = 10_000 // 10 seconds
        val testDurationAfterCancelInMillis: Long = 60_000 // 60 seconds, to be sure that the job is really cancelled (= job not completed)
        var isMethodComplete = false
        var isJobStarted = false // Not entered in thread
        val sut = JobRunnerImpl()
        sut.setDefaultDispatcherForAllScope(testDispatcher)

        /** Act **/
        val job = sut.runFireAndForgetWithTimeout(dispatcherToUse = testDispatcher, timeoutInMillis = jobTimeoutInMillis) {
            isJobStarted = true
            delay(methodDurationInMillis) // Virtual time: wait 'methodDurationInMillis' milliseconds -> Timeout not reached
            isMethodComplete = true
        }
        // Advance virtual time: 'testDurationBeforeCancelInMillis' milliseconds -> Action not completed yet
        testScheduler.apply { advanceTimeBy(testDurationBeforeCancelInMillis); runCurrent() }
        job.cancel()
        // Advance virtual time: 'testDurationAfterCancelInMillis' milliseconds -> Action not completed yet
        testScheduler.apply { advanceTimeBy(testDurationAfterCancelInMillis); runCurrent() }

        /** Assert **/
        assertTrue(isJobStarted)
        assertFalse(isMethodComplete)
        assertFalse(job.isActive)
    }

    /**
     * Run and forget a job with a timeout.
     * The test cancels the coroutine after its end. (to check no exception, no crash)
     * The job timeout is not reached:
     * - The method started.
     * - The method was completed.
     * - The coroutine job is no longer active.
     */
    @Test
    fun runFireAndForgetWithTimeout_whenJobIsCancelAfterItsEnd_thenActionIsNotComplete() = runTest {
        /** Arrange **/
        val methodDurationInMillis: Long = 30_000 // 30 seconds
        val jobTimeoutInMillis: Long = 60_000 // 60 second -> Timeout not reached
        val testDurationBeforeCancelInMillis: Long = 100_000 // 100 seconds, to be sure the job is completed before the cancellation
        var isMethodComplete = false
        var isJobStarted = false // Not entered in thread
        val sut = JobRunnerImpl()
        sut.setDefaultDispatcherForAllScope(testDispatcher)

        /** Act **/
        val job = sut.runFireAndForgetWithTimeout(dispatcherToUse = testDispatcher, timeoutInMillis = jobTimeoutInMillis) {
            isJobStarted = true
            delay(methodDurationInMillis) // Virtual time: wait 'methodDurationInMillis' milliseconds -> Timeout not reached
            isMethodComplete = true
        }
        // Advance virtual time: 'testDurationBeforeCancelInMillis' milliseconds -> Action not completed yet
        testScheduler.apply { advanceTimeBy(testDurationBeforeCancelInMillis); runCurrent() }
        job.cancel()

        /** Assert **/
        assertTrue(isJobStarted)
        assertTrue(isMethodComplete)
        assertFalse(job.isActive)
    }

    //endregion runFireAndForgetWithTimeout tests

    //region cancel tests

    /**
     * Run and forget two jobs with a timeout.
     * The test cancels all coroutines: one is completed, not the second one.
     * Jobs timeouts are not reached:
     * - Jobs started.
     * - Job 1 was completed, job 2 was not completed.
     * - Coroutine jobs are no longer active.
     */
    @Test
    fun runFireAndForgetWithTimeout_whenScopeIsCancel_thenJobsAreCancelled() = runTest {
        /** Arrange **/
        val jobTimeoutInMillis: Long = 60_000 // 60 second -> Timeout not reach
        val methodDurationInMillis_job1: Long = 1_000 // 1 second
        val methodDurationInMillis_job2: Long = 30_000 // 30 seconds
        var isMethodComplete_job1 = false
        var isMethodComplete_job2 = false
        var isJobStarted_job1 = false // Not entered in thread
        var isJobStarted_job2 = false // Not entered in thread
        val testDurationBeforeCancelInMillis: Long = 10_000 // 10 seconds
        val testDurationAfterCancelInMillis: Long = 100_000 // 100 seconds
        val sut = JobRunnerImpl()
        sut.setDefaultDispatcherForAllScope(testDispatcher)

        /** Act **/
        val job1 = sut.runFireAndForgetWithTimeout(dispatcherToUse = testDispatcher, timeoutInMillis = jobTimeoutInMillis) {
            isJobStarted_job1 = true
            delay(methodDurationInMillis_job1) // Virtual time: wait  1 second -> Timeout not reached (60 seconds)
            isMethodComplete_job1 = true
        }
        val job2 = sut.runFireAndForgetWithTimeout(dispatcherToUse = testDispatcher, timeoutInMillis = jobTimeoutInMillis) {
            isJobStarted_job2 = true
            delay(methodDurationInMillis_job2) // Virtual time: wait 30 seconds -> Timeout not reached (60 seconds)
            isMethodComplete_job2 = true
        }
        // Advance virtual time: 10 seconds -> Job 1 completed, job2 not completed
        testScheduler.apply { advanceTimeBy(testDurationBeforeCancelInMillis); runCurrent() }
        // Cancel all coroutines (job1 is already completed, job2 is in progress -> cancelled)
        sut.cancel()
        // Advance virtual time: 100 seconds -> To be sure that the job 2 is not completed if it has not been canceled (it will be a bug)
        testScheduler.apply { advanceTimeBy(testDurationAfterCancelInMillis); runCurrent() }

        /** Assert **/
        assertTrue(isJobStarted_job1)
        assertTrue(isJobStarted_job2)
        assertTrue(isMethodComplete_job1)
        assertFalse(isMethodComplete_job2)
        assertFalse(job1.isActive)
        assertFalse(job2.isActive)
    }

    //endregion cancel tests

    //region setDefaultDispatcherForAllScope tests

    /**
     * Test the change of default dispatcher.
     */
    @Test
    fun setDefaultDispatcherForAllScope_whenSetNewDefaultDispatcher_thenNewDefaultDispatcherIsSet() = runTest {
        /** Arrange **/
        val expectedDispatcher = Dispatchers.IO
        val sut = JobRunnerImpl()

        /** Act **/
        sut.setDefaultDispatcherForAllScope(expectedDispatcher)

        /** Assert **/
        assertEquals(expectedDispatcher, sut.defaultDispatcher)
    }

    /**
     * Test the default dispatcher.
     */
    @Test
    fun setDefaultDispatcherForAllScope_whenMethodIsNotCalled_thenDefaultDispatcherIsSet() = runTest {
        /** Arrange **/
        val expectedDispatcher = JobRunnerImpl.DEFAULT_DISPATCHER
        val sut = JobRunnerImpl()

        /** Assert **/
        assertEquals(expectedDispatcher, sut.defaultDispatcher)
    }

    //endregion setDefaultDispatcherForAllScope tests

    //region setDefaultTimeoutForAllScope tests

    /**
     * Test the change of the default timeout.
     */
    @Test
    fun setDefaultTimeoutForAllScope_whenSetNewDefaultDispatcher_thenNewDefaultDispatcherIsSet() = runTest {
        /** Arrange **/
        val expectedTimeout: Long = 15_000
        val sut = JobRunnerImpl()

        /** Act **/
        sut.setDefaultTimeoutForAllScope(expectedTimeout)

        /** Assert **/
        assertEquals(expectedTimeout, sut.defaultTimeout)
    }

    /**
     * Test the default timeout.
     */
    @Test
    fun setDefaultTimeoutForAllScope_whenMethodIsNotCalled_thenDefaultDispatcherIsSet() = runTest {
        /** Arrange **/
        val expectedTimeout: Long = JobRunnerImpl.DEFAULT_TIMEOUT
        val sut = JobRunnerImpl()

        /** Assert **/
        assertEquals(expectedTimeout, sut.defaultTimeout)
    }

    //endregion setDefaultTimeoutForAllScope tests
}
