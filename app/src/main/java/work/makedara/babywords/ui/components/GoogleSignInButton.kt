package work.makedara.babywords.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource // Required for drawable resources
import androidx.compose.ui.unit.dp
// import work.makedara.babywords.R // Import your project's R class if the logo is in res/drawable

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Sign in with Google"
    // You can add a parameter for the icon if you have multiple Google-style buttons
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(1.dp, Color.LightGray), // Common style for Google buttons
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White, // Google buttons are often white
            contentColor = Color.Black.copy(alpha = 0.87f) // Standard text color on light background
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp) // Typical padding
    ) {
        // Googleのロゴを追加する場合：
        // 1. `app/src/main/res/drawable` に Google ロゴのベクターアセット (例: `ic_google_logo.xml`) を追加します。
        // 2. 下のコメントアウトを解除し、`R.drawable.ic_google_logo` を実際のIDに置き換えます。
        // Image(
        //     painter = painterResource(id = R.drawable.ic_google_logo), // Replace with your actual drawable
        //     contentDescription = "Google logo",
        //     modifier = Modifier.size(18.dp) // Google's recommended icon size
        // )
        // Spacer(Modifier.width(24.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Black.copy(alpha = 0.54f) // Google's specified text color for buttons
            )
        )
    }
}