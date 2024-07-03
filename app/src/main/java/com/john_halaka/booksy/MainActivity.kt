package com.john_halaka.booksy

import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.john_halaka.booksy.feature_book.domain.viewModel.BookContentViewModel
import com.john_halaka.booksy.feature_book_view.domain.bookParser.NavigationCallback
import com.john_halaka.booksy.ui.theme.BooksYTheme
import dagger.hilt.android.AndroidEntryPoint
import com.john_halaka.booksy.feature_book_view.domain.bookParser.displayBookContent
import com.john_halaka.booksy.feature_book_view.domain.bookParser.loadBookTextFromRaw
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import reader.data.Book


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationCallback {
    // By using by viewModels(), Hilt will automatically scope the ViewModel
    // to the activity's lifecycle, ensuring that onCleared()
    // is called when the activity is finished

    private val viewModel: BookContentViewModel by viewModels()

    override fun onStop() {
        super.onStop()
        // Launch a coroutine to clear the database
        lifecycleScope.launch(Dispatchers.IO) {
            Log.d("MainActivity", "onStop is called")
            // Get an instance of your repository and call deleteAllData()
            viewModel.linkRepository.deleteAllData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            //if you want to check something while loading like checking if the user token is valid and they are logged in
            // in this case maybe when the books are loaded
            //  setKeepOnScreenCondition{
            // checks every time until the condition is true
            //for example !viewModel.isReady.value
            // that means as long as this isReady is false keep the splash screen running
            // }
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.9f,
                    0.0f
                )
                zoomX.interpolator = OvershootInterpolator()
                zoomX.duration = 800L
                zoomX.doOnEnd { screen.remove() }

                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_Y,
                    0.9f,
                    0.0f
                )
                zoomY.interpolator = OvershootInterpolator()
                zoomY.duration = 800L
                zoomY.doOnEnd { screen.remove() }

                zoomX.start()
                zoomY.start()
            }
        }

        val inputStream = this.resources.openRawResource(R.raw.book_328478)
        val bookContent = inputStream.bufferedReader().use { it.readText() }
        val book = Book.instance(bookContent)

        Log.d("MainActivity", "onCreate: ${book.body.string}")

        setContent {
            BooksYTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Navigation()
                    BookContentView(
                        viewModel = viewModel,
                        navigationCallback = this,
                        book = book
                    )
                }
            }
        }
    }

    override fun navigateTo(destination: Int) {
        val bookTextView = findViewById<TextView>(R.id.bookTextView) // Get the TextView
//        val text = bookTextView.text.toString() // Get the current text content
//        var x =""
//        for (i in destination -10 until destination+ 10){
//            x += text[i]
//        }
      //  Log.d("navigateTo", "destination: $x")
        // Use withContext to switch to the main thread for UI operations
        lifecycleScope.launch(Dispatchers.Main) { // Use lifecycleScope for coroutine launch
            // Scrolling with animation
            val scrollAnimation = ObjectAnimator.ofInt(bookTextView, "scrollY", destination)
            scrollAnimation.duration = 500 // Adjust duration as needed
            scrollAnimation.interpolator = AccelerateDecelerateInterpolator() // Optional
            scrollAnimation.start()

        }
    }
}

@Composable
fun BookContentView(
    viewModel: BookContentViewModel,
    navigationCallback: NavigationCallback,
    book: Book
) {
    val currentContext = LocalContext.current

    AndroidView(
        factory = { context ->
            val view =
                LayoutInflater.from(context).inflate(R.layout.text_view_layout, null, false)
            // val parentViewGroup = view.findViewById<ViewGroup>(R.id.parent_view_group)
            val bookTextView: TextView = view.findViewById(R.id.bookTextView)
            bookTextView.movementMethod = LinkMovementMethod()
            //val bookImageView: ImageView = view.findViewById(R.id.bookImageView)

            // Load the book text from a file or a string
             // val bookText = loadBookTextFromRaw(currentContext)

            // Display the book content
            Log.d("BookContentView", "Displaying book content")
            displayBookContent(
                currentContext,
                book = book,
                bookTextView,
                viewModel,
                navigationCallback
            )

            view
        }
    )
}

fun loadBookText(): String {
    // Load the book text from a file or a string
    // For testing, you can use a hardcoded string
    return """
        This is a sample book text.
        ##W https://github.com
        **
        ##s adklsdasdas** 
        ##taxdasd**
        ##TThis text is bold and red.**
        jllka;lads
        ##LClick here for an internal link.**
        ##WClick here for a web link.**
        ##Ihttps://example.com/image.jpg**
        ##Le link **
    """.trimIndent()
}
