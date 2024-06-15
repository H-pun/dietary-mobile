package org.cisnux.mydietary.presentation.foodscanner

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.cisnux.mydietary.DummyActivity
import org.cisnux.mydietary.R
import org.cisnux.mydietary.commons.DUMMY_FOOD_DIARY_TITLE
import org.cisnux.mydietary.presentation.navigation.DietaryNavGraph
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Locale

@OptIn(ExperimentalTestApi::class)
@LargeTest
@HiltAndroidTest
class FoodScannerScreenTest {
    private lateinit var device: UiDevice

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<DummyActivity>()

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        hiltRule.inject()
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            DietaryTheme {
                DietaryNavGraph(
                    navController = navController
                )
            }
        }
        device.wait(
            Until.findObject(By.text(composeTestRule.activity.getString(R.string.allow))),
            5000L
        )
        device.findObject(By.text(composeTestRule.activity.getString(R.string.allow)))
            .click()
        composeTestRule.waitUntilAtLeastOneExists(
            hasText(composeTestRule.activity.getString(R.string.get_started)),
            5000L
        )
    }

    @Test
    fun addFoodDiary_Success(): Unit = runBlocking {
        with(composeTestRule) {
            val emailAddress = "j4478072@gmail.com"
            val password = "@Cisnux21"
            val cameraLabelPermission =
                if (Locale.getDefault().language != Locale("id").language) "Only this time" else "Hanya kali ini"

            onNodeWithText(composeTestRule.activity.getString(R.string.sign_in))
                .assertIsDisplayed()
                .performClick()

            waitUntilExactlyOneExists(
                hasText(composeTestRule.activity.getString(R.string.welcome_back)),
                timeoutMillis = 3000L
            )

            onNodeWithText(composeTestRule.activity.getString(R.string.email_address_label))
                .assertIsDisplayed()
                .performTextInput(emailAddress)

            onNodeWithText(composeTestRule.activity.getString(R.string.password_label))
                .assertIsDisplayed()
                .performTextInput(password)

            onNodeWithText(composeTestRule.activity.getString(R.string.sign_in))
                .assertIsDisplayed()
                .performClick()

            waitUntilExactlyOneExists(
                hasText(composeTestRule.activity.getString(R.string.diary_display)),
                timeoutMillis = 10_000L
            )

            onNodeWithContentDescription(label = composeTestRule.activity.getString(R.string.food_scanner_title))
                .assertIsDisplayed()
                .performClick()

            device.wait(
                Until.findObject(By.text(cameraLabelPermission)),
                5000L
            )
            device.findObject(By.text(cameraLabelPermission))
                .click()

            waitUntilExactlyOneExists(
                hasText(composeTestRule.activity.getString(R.string.food_diary)),
                timeoutMillis = 10_000L
            )

            onNodeWithText(composeTestRule.activity.getString(R.string.title))
                .assertIsDisplayed()
                .performTextInput(DUMMY_FOOD_DIARY_TITLE)

            onNodeWithContentDescription(composeTestRule.activity.getString(R.string.save_food_diary))
                .assertIsDisplayed()
                .performClick()

            delay(3000L)

            onNodeWithContentDescription(composeTestRule.activity.getString(R.string.take_a_picture))
                .assertIsDisplayed()
                .performClick()

            waitForIdle()

            waitUntilExactlyOneExists(
                hasContentDescription(composeTestRule.activity.getString(R.string.continue_button)),
                timeoutMillis = 60_000L
            )

            onNodeWithContentDescription(composeTestRule.activity.getString(R.string.list_of_foods))
                .performScrollToNode(hasContentDescription(composeTestRule.activity.getString(R.string.continue_button)))
                .onChildren()
                .filterToOne(hasContentDescription(composeTestRule.activity.getString(R.string.continue_button)))
                .assertHasClickAction()
                .performClick()

            waitForIdle()

            waitUntilExactlyOneExists(
                hasText(DUMMY_FOOD_DIARY_TITLE),
                timeoutMillis = 60_000L
            )

            onNodeWithText(DUMMY_FOOD_DIARY_TITLE)
                .assertIsDisplayed()
        }
    }

    @Test
    fun addFoodDiary_Failed(): Unit = runBlocking {
        with(composeTestRule) {
            val emailAddress = "j4478072@gmail.com"
            val password = "@Cisnux21"
            val cameraLabelPermission =
                if (Locale.getDefault().language != Locale("id").language) "Only this time" else "Hanya kali ini"

            onNodeWithText(composeTestRule.activity.getString(R.string.sign_in))
                .assertIsDisplayed()
                .performClick()

            waitUntilExactlyOneExists(
                hasText(composeTestRule.activity.getString(R.string.welcome_back)),
                timeoutMillis = 3000L
            )

            onNodeWithText(composeTestRule.activity.getString(R.string.email_address_label))
                .assertIsDisplayed()
                .performTextInput(emailAddress)

            onNodeWithText(composeTestRule.activity.getString(R.string.password_label))
                .assertIsDisplayed()
                .performTextInput(password)

            onNodeWithText(composeTestRule.activity.getString(R.string.sign_in))
                .assertIsDisplayed()
                .performClick()

            waitUntilExactlyOneExists(
                hasText(composeTestRule.activity.getString(R.string.diary_display)),
                timeoutMillis = 10_000L
            )

            onNodeWithContentDescription(label = composeTestRule.activity.getString(R.string.food_scanner_title))
                .assertIsDisplayed()
                .performClick()

            device.wait(
                Until.findObject(By.text(cameraLabelPermission)),
                5000L
            )
            device.findObject(By.text(cameraLabelPermission))
                .click()

            waitUntilExactlyOneExists(
                hasText(composeTestRule.activity.getString(R.string.food_diary)),
                timeoutMillis = 10_000L
            )

            onNodeWithText(composeTestRule.activity.getString(R.string.title))
                .assertIsDisplayed()
                .performTextInput(DUMMY_FOOD_DIARY_TITLE)

            onNodeWithContentDescription(composeTestRule.activity.getString(R.string.save_food_diary))
                .assertIsDisplayed()
                .performClick()

            delay(3000L)

            onNodeWithContentDescription(composeTestRule.activity.getString(R.string.take_a_picture))
                .assertIsDisplayed()
                .performClick()

            waitUntilExactlyOneExists(
                hasText("No food detected!"),
                timeoutMillis = 10_000L
            )

            onNodeWithText("No food detected!")
                .assertIsDisplayed()
        }
    }
}