package org.cisnux.mydietary.presentation.myprofile

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
import androidx.compose.ui.test.performTextClearance
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


@OptIn(ExperimentalTestApi::class)
@LargeTest
@HiltAndroidTest
class MyProfileScreenTest {
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
    fun displayUserProfile(): Unit = with(composeTestRule) {
        val emailAddress = "j4478072@gmail.com"
        val password = "@Cisnux21"
        val username = "jacktest21"

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
            timeoutMillis = 60_000L
        )

        Assert.assertEquals(
            AppDestination.HomeRoute.route,
            navController.currentBackStackEntry?.destination?.route
        )

        waitForIdle()

        Assert.assertEquals(
            AppDestination.HomeRoute.route,
            navController.currentBackStackEntry?.destination?.route
        )

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

        Assert.assertEquals(
            AppDestination.MyProfileRoute.route,
            navController.currentBackStackEntry?.destination?.route
        )
    }

    @Test
    fun updateUserProfile_Success(): Unit = with(composeTestRule) {
        val emailAddress = "j4478072@gmail.com"
        val password = "@Cisnux21"
        val oldUsername = "jacktest21"
        val newUsername = "jacktest22"

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
            timeoutMillis = 60_000L
        )

        Assert.assertEquals(
            AppDestination.HomeRoute.route,
            navController.currentBackStackEntry?.destination?.route
        )

        waitForIdle()

        Assert.assertEquals(
            AppDestination.HomeRoute.route,
            navController.currentBackStackEntry?.destination?.route
        )

        onNodeWithContentDescription(label = composeTestRule.activity.getString(R.string.my_profile_title))
            .assertIsDisplayed()
            .performClick()

        waitForIdle()

        waitUntilExactlyOneExists(
            hasText(oldUsername),
            timeoutMillis = 60_000L
        )

        onNodeWithText(oldUsername)
            .assertIsDisplayed()

        Assert.assertEquals(
            AppDestination.MyProfileRoute.route,
            navController.currentBackStackEntry?.destination?.route
        )

        onNodeWithTag(testTag = "edit_profile_button")
            .assertIsDisplayed()
            .performClick()

        onNodeWithTag(testTag = "my_profile_form")
            .onChildren()
            .filter(hasTestTag(testTag = "username_form"))
            .onFirst()
            .assertIsDisplayed()
            .performTextClearance()

        onNodeWithTag(testTag = "my_profile_form")
            .onChildren()
            .filter(hasTestTag(testTag = "username_form"))
            .onFirst()
            .assertIsDisplayed()
            .performTextInput(newUsername)

        onNodeWithText(activity.getString(R.string.save))
            .assertIsDisplayed()
            .performClick()

        waitForIdle()

        waitUntilExactlyOneExists(
            hasText(newUsername),
            timeoutMillis = 60_000L
        )

        onNodeWithText(newUsername)
            .assertIsDisplayed()
    }

    @Test
    fun updateUserProfile_Failed(): Unit = with(composeTestRule) {
        val emailAddress = "j4478072@gmail.com"
        val password = "@Cisnux21"
        val oldUsername = "jacktest21"
        // has been taken
        val newUsername = "yagami21"

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
            timeoutMillis = 60_000L
        )

        Assert.assertEquals(
            AppDestination.HomeRoute.route,
            navController.currentBackStackEntry?.destination?.route
        )

        waitForIdle()

        Assert.assertEquals(
            AppDestination.HomeRoute.route,
            navController.currentBackStackEntry?.destination?.route
        )

        onNodeWithContentDescription(label = composeTestRule.activity.getString(R.string.my_profile_title))
            .assertIsDisplayed()
            .performClick()

        waitForIdle()

        waitUntilExactlyOneExists(
            hasText(oldUsername),
            timeoutMillis = 60_000L
        )

        onNodeWithText(oldUsername)
            .assertIsDisplayed()

        Assert.assertEquals(
            AppDestination.MyProfileRoute.route,
            navController.currentBackStackEntry?.destination?.route
        )

        onNodeWithTag(testTag = "edit_profile_button")
            .assertIsDisplayed()
            .performClick()

        onNodeWithTag(testTag = "my_profile_form")
            .onChildren()
            .filter(hasTestTag(testTag = "username_form"))
            .onFirst()
            .assertIsDisplayed()
            .performTextClearance()

        onNodeWithTag(testTag = "my_profile_form")
            .onChildren()
            .filter(hasTestTag(testTag = "username_form"))
            .onFirst()
            .assertIsDisplayed()
            .performTextInput(newUsername)

        onNodeWithText(activity.getString(R.string.save))
            .assertIsDisplayed()
            .performClick()

        waitForIdle()

        waitUntilExactlyOneExists(
            hasText("username not available!"),
            timeoutMillis = 60_000L
        )

        onNodeWithText("username not available!")
            .assertIsDisplayed()
    }
}