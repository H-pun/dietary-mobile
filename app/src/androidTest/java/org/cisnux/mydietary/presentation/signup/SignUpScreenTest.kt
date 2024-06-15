package org.cisnux.mydietary.presentation.signup

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
import org.cisnux.mydietary.DummyActivity
import org.cisnux.mydietary.R
import org.cisnux.mydietary.presentation.navigation.DietaryNavGraph
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalTestApi::class)
@LargeTest
@HiltAndroidTest
class SignUpScreenTest {
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
        device.wait(Until.findObject(By.text(composeTestRule.activity.getString(R.string.allow))), 5000L)
        device.findObject(By.text(composeTestRule.activity.getString(R.string.allow)))
            .click()
        composeTestRule.waitUntilAtLeastOneExists(hasText(composeTestRule.activity.getString(R.string.get_started)), 5000L)
    }

    @Test
    fun signUp_Success(): Unit = with(composeTestRule) {
        val emailAddress = "test${UUID.randomUUID()}@gmail.com"
        val password = "@Cisnux21"
        val confirmPassword = "@Cisnux21"

        onNodeWithText(composeTestRule.activity.getString(R.string.sign_in))
            .assertIsDisplayed()
            .performClick()

        waitUntilExactlyOneExists(
            hasText(composeTestRule.activity.getString(R.string.welcome_back)),
            timeoutMillis = 3000L
        )

        onNodeWithText(composeTestRule.activity.getString(R.string.sign_up_here))
            .performClick()

        waitUntilExactlyOneExists(
            hasText(composeTestRule.activity.getString(R.string.join_us_today)),
            timeoutMillis = 3000L
        )

        onNodeWithText(composeTestRule.activity.getString(R.string.email_address_label))
            .assertIsDisplayed()
            .performTextInput(emailAddress)

        onNodeWithText(composeTestRule.activity.getString(R.string.password_label))
            .assertIsDisplayed()
            .performTextInput(password)

        onNodeWithText(composeTestRule.activity.getString(R.string.confirmation_password_label))
            .assertIsDisplayed()
            .performTextInput(confirmPassword)

        onNodeWithText(composeTestRule.activity.getString(R.string.sign_up))
            .assertIsDisplayed()
            .performClick()

        waitUntilExactlyOneExists(
            hasText(composeTestRule.activity.getString(R.string.successfully_signed_up)),
            timeoutMillis = 10_000L
        )

        onNodeWithText(composeTestRule.activity.getString(R.string.successfully_signed_up))
            .assertIsDisplayed()
    }

    @Test
    fun signUp_Failed(): Unit = with(composeTestRule) {
        val emailAddress = "fajrarisqulla@gmail.com"
        val password = "@Cisnux21"
        val confirmPassword = "@Cisnux21"

        onNodeWithText(composeTestRule.activity.getString(R.string.sign_in))
            .assertIsDisplayed()
            .performClick()

        waitUntilExactlyOneExists(
            hasText(composeTestRule.activity.getString(R.string.welcome_back)),
            timeoutMillis = 3000L
        )

        onNodeWithText(composeTestRule.activity.getString(R.string.sign_up_here))
            .performClick()

        waitUntilExactlyOneExists(
            hasText(composeTestRule.activity.getString(R.string.join_us_today)),
            timeoutMillis = 3000L
        )

        onNodeWithText(composeTestRule.activity.getString(R.string.email_address_label))
            .assertIsDisplayed()
            .performTextInput(emailAddress)

        onNodeWithText(composeTestRule.activity.getString(R.string.password_label))
            .assertIsDisplayed()
            .performTextInput(password)

        onNodeWithText(composeTestRule.activity.getString(R.string.confirmation_password_label))
            .assertIsDisplayed()
            .performTextInput(confirmPassword)

        onNodeWithText(composeTestRule.activity.getString(R.string.sign_up))
            .assertIsDisplayed()
            .performClick()

        waitUntilExactlyOneExists(
            hasText("email registered!"),
            timeoutMillis = 10_000L
        )

        onNodeWithText("email registered!")
            .assertIsDisplayed()
    }
}