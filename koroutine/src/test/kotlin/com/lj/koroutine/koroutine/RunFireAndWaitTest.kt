package com.lj.koroutine.koroutine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

/**
 * Test class for the method [JobRunnerImpl.runFireAndWait].
 */
@ExperimentalCoroutinesApi
class RunFireAndWaitTest {

    /**
     * Test dispatcher to control the time.
     */
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule(testDispatcher)

    /**
     * Run and wait the end of a coroutine.
     */
    @Test
    fun runFireAndWait_whenIsFireWithDefaultDispatcher_thenWaitTheEnd() {
        /** Arrange **/
        val methodDurationInMillis: Long = 3_000 // 3 seconds
        var isMethodComplete = false
        var isJobStarted = false
        val sut = JobRunnerImpl()

        /** Act **/
        sut.runFireAndWait {
            isJobStarted = true
            delay(methodDurationInMillis)
            isMethodComplete = true
        }

        /** Assert **/
        Assert.assertTrue(isJobStarted)
        Assert.assertTrue(isMethodComplete)
    }

    /**
     * Run and wait the end of a coroutine.
     */
    @Test
    fun runFireAndWait_whenIsFireWithCustomDispatcher_thenWaitTheEnd() {
        /** Arrange **/
        val methodDurationInMillis: Long = 3_000 // 3 seconds
        var isMethodComplete = false
        var isJobStarted = false
        val sut = JobRunnerImpl()

        /** Act **/
        sut.runFireAndWait(dispatcherToUse = Dispatchers.IO) {
            isJobStarted = true
            delay(methodDurationInMillis)
            isMethodComplete = true
        }

        /** Assert **/
        Assert.assertTrue(isJobStarted)
        Assert.assertTrue(isMethodComplete)
    }
}