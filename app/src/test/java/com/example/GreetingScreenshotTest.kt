package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.FoodCategory
import com.example.data.model.FoodItem
import com.example.data.model.StorageLocation
import com.example.ui.components.FoodItemCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleItem = FoodItem(
      id = 1,
      name = "شیر کم چرب",
      category = FoodCategory.DAIRY,
      location = StorageLocation.FRIDGE,
      quantity = 1.0,
      unit = "لیتر",
      addedDate = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(2),
      expiryDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(4)
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        FoodItemCard(
          item = sampleItem,
          onConsumeClick = {},
          onWasteClick = {},
          onFreezeClick = {},
          onEditClick = {},
          onDeleteClick = {},
          onAddToShoppingList = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
