package com.lj.koroutine.koroutine

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class KotestTest  : FunSpec( {
        test("toto") {
                "sammy".length shouldBe 5
                "".length shouldBe 0
            }
})