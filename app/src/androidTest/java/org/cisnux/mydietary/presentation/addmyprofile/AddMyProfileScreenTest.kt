package org.cisnux.mydietary.presentation.addmyprofile

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
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
import org.cisnux.mydietary.DummyActivity
import org.cisnux.mydietary.R
import org.cisnux.mydietary.commons.utils.AppDestination
import org.cisnux.mydietary.presentation.navigation.DietaryNavGraph
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID
import kotlin.random.Random

@OptIn(ExperimentalTestApi::class)
@LargeTest
@HiltAndroidTest
class AddMyProfileScreenTest {
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
    fun addMyProfile_Success(): Unit = with(composeTestRule) {
        val emailAddress = "test${UUID.randomUUID()}@gmail.com"
        val password = "@Cisnux21"
        val confirmPassword = "@Cisnux21"
        val username = "jacktest${Random.nextInt(from = 1000, until = 10_000)}"
        val age = "20"
        val weight = "90.0"
        val height = "170.0"
        val waistCircumference = "80.0"
        val targetWeight = "70.0"

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

        onNodeWithText(composeTestRule.activity.getString(R.string.sign_in))
            .assertIsDisplayed()
            .performClick()

        waitForIdle()

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

        waitForIdle()

        waitUntilExactlyOneExists(
            hasText(composeTestRule.activity.getString(R.string.add_my_profile_label)),
            timeoutMillis = 60_000L
        )

        onNodeWithText(composeTestRule.activity.getString(R.string.username_label))
            .assertIsDisplayed()
            .performTextInput(username)

        onNodeWithText(composeTestRule.activity.getString(R.string.age_label))
            .assertIsDisplayed()
            .performTextInput(age)

        onNodeWithText(composeTestRule.activity.getString(R.string.weight_label))
            .assertIsDisplayed()
            .performTextInput(weight)

        onNodeWithText(composeTestRule.activity.getString(R.string.height_label))
            .assertIsDisplayed()
            .performTextInput(height)

        onNodeWithText(composeTestRule.activity.getString(R.string.waist_circumference_label))
            .assertIsDisplayed()
            .performTextInput(waistCircumference)

        onNodeWithText(composeTestRule.activity.getString(R.string.target_weight_label))
            .assertIsDisplayed()
            .performTextInput(targetWeight)

        onNodeWithTag(testTag = "add_my_profile_body")
            .performScrollToNode(hasTestTag(testTag = "build_profile_button"))
            .onChildren()
            .filter(hasTestTag("build_profile_button"))
            .onFirst()
            .assertIsDisplayed()
            .performClick()

        waitUntilExactlyOneExists(
            hasText(composeTestRule.activity.getString(R.string.diary_display)),
            timeoutMillis = 60_000L
        )

        Assert.assertEquals(AppDestination.HomeRoute.route, navController.currentBackStackEntry?.destination?.route)

        onNodeWithContentDescription(label = composeTestRule.activity.getString(R.string.my_profile_title))
            .assertIsDisplayed()
            .performClick()

        waitForIdle()

        waitUntilExactlyOneExists(
            hasText(username),
            timeoutMillis = 60_000L
        )

        onNodeWithText(username)
            .assertIsDisplayed()

        Assert.assertEquals(AppDestination.MyProfileRoute.route, navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun addMyProfile_Failed(): Unit = with(composeTestRule) {
        val emailAddress = "test${UUID.randomUUID()}@gmail.com"
        val password = "@Cisnux21"
        val confirmPassword = "@Cisnux21"
        val username = "jacktest21"
        val age = "20"
        val weight = "90.0"
        val height = "170.0"
        val waistCircumference = "80.0"
        val targetWeight = "70.0"

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

        onNodeWithText(composeTestRule.activity.getString(R.string.sign_in))
            .assertIsDisplayed()
            .performClick()

        waitForIdle()

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

        waitForIdle()

        waitUntilExactlyOneExists(
            hasText(composeTestRule.activity.getString(R.string.add_my_profile_label)),
            timeoutMillis = 60_000L
        )

        onNodeWithText(composeTestRule.activity.getString(R.string.username_label))
            .assertIsDisplayed()
            .performTextInput(username)

        onNodeWithText(composeTestRule.activity.getString(R.string.age_label))
            .assertIsDisplayed()
            .performTextInput(age)

        onNodeWithText(composeTestRule.activity.getString(R.string.weight_label))
            .assertIsDisplayed()
            .performTextInput(weight)

        onNodeWithText(composeTestRule.activity.getString(R.string.height_label))
            .assertIsDisplayed()
            .performTextInput(height)

        onNodeWithText(composeTestRule.activity.getString(R.string.waist_circumference_label))
            .assertIsDisplayed()
            .performTextInput(waistCircumference)

        onNodeWithText(composeTestRule.activity.getString(R.string.target_weight_label))
            .assertIsDisplayed()
            .performTextInput(targetWeight)

        onNodeWithTag(testTag = "add_my_profile_body")
            .performScrollToNode(hasTestTag(testTag = "build_profile_button"))
            .onChildren()
            .filter(hasTestTag("build_profile_button"))
            .onFirst()
            .assertIsDisplayed()
            .performClick()

        waitUntilExactlyOneExists(
            hasText("username not available!"),
            timeoutMillis = 60_000L
        )

        onNodeWithText("username not available!")
            .assertIsDisplayed()
    }
}