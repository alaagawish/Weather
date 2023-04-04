package eg.gov.iti.jets.kotlin.weather

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class MainRule(val testCoroutineDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()) :
    TestWatcher(),
    TestCoroutineScope by TestCoroutineScope(testCoroutineDispatcher) {
    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

}