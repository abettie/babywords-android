package work.makedara.babywords

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button // This Button might still be used for Sign Out or Retry
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import work.makedara.babywords.ui.components.GoogleSignInButton // Added import
import work.makedara.babywords.ui.theme.BabyWordsTheme
// import javax.inject.Inject // Not used directly in Activity fields

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BabyWordsTheme {
                AuthScreen()
            }
        }
    }
}

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    // Configure Google Sign-In
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                viewModel.signInWithGoogleCredential(credential)
            } ?: run {
                Log.e("AuthScreen", "Google Sign-In failed: idToken is null")
            }
        } catch (e: ApiException) {
            Log.e("AuthScreen", "Google Sign-In failed: ${e.statusCode}", e)
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = authState) {
                is AuthState.Loading -> {
                    CircularProgressIndicator()
                }
                is AuthState.Authenticated -> {
                    Greeting(name = state.user.displayName ?: "User")
                    Button(onClick = { viewModel.signOut() }) {
                        Text("Sign Out")
                    }
                }
                is AuthState.Unauthenticated -> {
                    LoginScreen(
                        onGoogleSignInClicked = {
                            googleSignInLauncher.launch(googleSignInClient.signInIntent)
                        }
                    )
                }
                is AuthState.Error -> {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { /* Implement retry or back logic if needed */ }) {
                        Text("Retry")
                    }
                    Button(onClick = { googleSignInLauncher.launch(googleSignInClient.signInIntent) }) {
                        Text("Try Google Sign-In Again")
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onGoogleSignInClicked: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome! Please sign in.", style = MaterialTheme.typography.headlineSmall)
        GoogleSignInButton( // Replaced Button with GoogleSignInButton
            onClick = onGoogleSignInClicked,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier,
        style = MaterialTheme.typography.headlineMedium
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BabyWordsTheme {
        Greeting("Android")
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    BabyWordsTheme {
        LoginScreen(onGoogleSignInClicked = {})
    }
}