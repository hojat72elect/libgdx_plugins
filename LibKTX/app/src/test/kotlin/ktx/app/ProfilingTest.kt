package ktx.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.PerformanceCounter
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class ProfilingTest {
  @Before
  fun `initiate libGDX`() {
    Gdx.app = mock()
  }

  @Test
  fun `should profile operation`() {
    var repeats = 0

    val performanceCounter = profile(name = "Thread.sleep", repeats = 10) {
      repeats++
      Thread.sleep(10L)
    }

    assertEquals("Thread.sleep", performanceCounter.name)
    assertEquals(10, performanceCounter.time.mean.windowSize)
    assertEquals(10, performanceCounter.time.count)
    assertEquals(10, repeats)
    assertEquals(0.01f, performanceCounter.time.mean.mean, 0.002f)
  }

  @Test
  fun `should profile operation with existing PerformanceCounter`() {
    val performanceCounter = PerformanceCounter("Thread.sleep", 10)
    var repeats = 0

    performanceCounter.profile {
      repeats++
      Thread.sleep(10L)
    }

    assertEquals("Thread.sleep", performanceCounter.name)
    assertEquals(10, performanceCounter.time.count)
    assertEquals(10, repeats)
    assertEquals(0.01f, performanceCounter.time.mean.mean, 0.002f)
  }

  @After
  fun `destroy libGDX`() {
    Gdx.app = null
  }
}
