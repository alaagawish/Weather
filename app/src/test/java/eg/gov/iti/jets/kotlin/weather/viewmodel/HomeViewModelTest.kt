package eg.gov.iti.jets.kotlin.weather.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import eg.gov.iti.jets.kotlin.weather.MainRule
import eg.gov.iti.jets.kotlin.weather.model.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainRule()

    lateinit var homeViewModel: HomeViewModel
    lateinit var repository: Repository
    val app: Application = ApplicationProvider.getApplicationContext()

    @Before
    fun setUp() {
//repository= Repository()
//        homeViewModel=HomeViewModel()

    }

    fun addDay_dayDetailsAndHoursListAndComingDaysList_DoneAdd() {

    }
}
