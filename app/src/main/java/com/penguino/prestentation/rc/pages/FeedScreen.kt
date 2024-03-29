package com.penguino.prestentation.rc.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.penguino.R
import com.penguino.domain.forms.BtRequest
import com.penguino.prestentation.components.VerticalGrid
import com.penguino.ui.theme.PenguinoTheme

@Composable
fun FeedScreen(
    btSend: (String) -> Unit
) {

    val foodList = remember { mutableListOf<@Composable () -> Unit>() }
    val foods = rememberSaveable {
        listOf("Apple", "Banana", "Mango", "Orange")
    }
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    foods.forEachIndexed { index, food ->
        foodList.add {
            Button(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(width = 160.dp, height = 50.dp),
                onClick = {
                    when (index) {
                        0 -> {
                            Log.d("Row", "Fed Apple")
                            Toast.makeText(context, "Yum yum", Toast.LENGTH_SHORT).show()
                        }
                        1 -> {
                            Log.d("Row", "Fed Banana")
                            Toast.makeText(context, "Delicious", Toast.LENGTH_SHORT).show()
                        }
                        2 -> {
                            Log.d("Row", "Fed Mango")
                            Toast.makeText(context, "Scrumptious!!", Toast.LENGTH_SHORT).show()
                        }
                        3 -> {
                            Log.d("Row", "Fed Orange")
                            Toast.makeText(context, "YEEEK!", Toast.LENGTH_SHORT).show()
                        }
                    }



                    btSend(
                        BtRequest(
                            "feed",
                            food
                        ).toString()
                    )









//                    scope.launch {
//                        val times = bites.length/30;
//                        repeat(times) {
//                            Log.d("FOO", it.toString());
//                            btSend(BtRequest("feed", bites.substring(30 * (it), 30 * (it + 1))).toString())
//                            delay(1000)
//                        }
//                    }
                }
            ) {
                Text(text = food)
            }
        }
    }

    Column {
        Spacer(modifier = Modifier.weight(.5f))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.fillMaxWidth(0.8f),
                painter = painterResource(id = R.drawable.new_friend_screen),
                contentDescription = ""
            )
        }

        VerticalGrid(
            columns = 2,
            items = foodList
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Preview
@Composable
fun PreviewFeedScreen() {
    PenguinoTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            FeedScreen {}
        }
    }
}
