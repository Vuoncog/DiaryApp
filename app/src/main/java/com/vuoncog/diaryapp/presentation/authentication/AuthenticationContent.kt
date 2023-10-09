package com.vuoncog.diaryapp.presentation.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.diaryapp.R
import com.vuoncog.diaryapp.presentation.components.CustomOutlinedButton
import com.vuoncog.diaryapp.ui.theme.*

@Composable
fun AuthenticationContent() {
    Surface(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = AUTHENTICATION_TOP_PADDING,
                    bottom = AUTHENTICATION_BOT_PADDING
                )
        ) {
            AuthenticationWelcomeBack(
                modifier = Modifier.weight(1f)
            )
            CustomOutlinedButton(
                onClicked = {}
            )
        }
    }
}

@Composable
fun AuthenticationWelcomeBack(
    modifier: Modifier = Modifier,
    title: String = "Welcome back",
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    description: String = "Please sign in to create your own diary",
    descriptionColor: Color = MaterialTheme.colorScheme.outline
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = stringResource(R.string.diary_app_logo),
            modifier = Modifier.size(160.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                color = titleColor
            )

            Text(
                text = description,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = descriptionColor,
                letterSpacing = 0.025.em
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun AuthenticationContentPreview() {
    AuthenticationContent()
}