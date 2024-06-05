package com.duyle.roomdbex3

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.room.Room
import com.duyle.roomdbex3.ui.theme.RoomDBEx3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoomDBEx3Theme {
                Scaffold(
                    modifier = Modifier
                        .safeDrawingPadding()
                        .fillMaxSize()
                        .padding(16.dp)
                ) { innerPadding ->
                    Greeting(
                        name = "Quản ly Sinh vien",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val db = Room.databaseBuilder(
        context,
        StudentDB::class.java, "student-db2"
    ).allowMainThreadQueries().build()

    var listStudents by remember {
        mutableStateOf(db.studentDAO().getAll())
    }

    var hoten by remember {
        mutableStateOf("")
    }

    var mssv : String by remember {
        mutableStateOf("")
    }

    var diemTB : Float by remember {
        mutableFloatStateOf(0f)
    }

    var daratruong : Boolean by remember {
        mutableStateOf(false)
    }

    var showDialogThongtinSV by remember { mutableStateOf(false) }

    var dialogMessage by remember {
        mutableStateOf("")
    }

    if (showDialogThongtinSV) {
        val tatDialog = {
            showDialogThongtinSV = false
        }
        ShowDialogStudentInfor(
            onConfirmation = tatDialog,
            dialogMessage = dialogMessage
        )
    }


    Column(Modifier.fillMaxWidth()) {
        Text(
            text = "Quan ly Sinh vien",
            style = MaterialTheme.typography.titleLarge
        )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)) {
            OutlinedTextField(
                label = { Text("Ho ten") },
                value = hoten,
                onValueChange = { hoten = it },
                modifier = Modifier.weight(1f)
            )
            
            Spacer(modifier = Modifier.width(16.dp))

            OutlinedTextField(
                label = { Text("MSSV ") },
                value = mssv,
                onValueChange = { mssv = it },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)) {
            OutlinedTextField(
                label = { Text("Diem TB") },
                value = diemTB.toString(),
                onValueChange = { diemTB = it.toFloat() },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedTextField(
                label = { Text("Da ra truong?") },
                value = daratruong.toString(),
                onValueChange = { daratruong = it.toBoolean() },
                modifier = Modifier.weight(1f)
            )
        }


        Button(onClick = {
            if (hoten.trim().equals("") || mssv.trim().equals("") ||  diemTB == null || daratruong == null) {
                Toast.makeText(context, "Chua dien du thong tin!", Toast.LENGTH_SHORT).show()
            } else {
                db.studentDAO().insert(
                    StudentModel(
                        hoten = hoten,
                        mssv = mssv,
                        diemTB = diemTB,
                        daratruong = daratruong
                    )
                )
                listStudents = db.studentDAO().getAll()
            }


        }) {
            Text(text = "Thêm SV")
        }

        LazyColumn {

            items(listStudents) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable {
                            dialogMessage = it.getThongtin()
                            showDialogThongtinSV = true
                        }
                ) {
                    Text(modifier = Modifier.weight(1f), text = it.uid.toString())
                    Text(modifier = Modifier.weight(1f), text = it.hoten.toString())
                    Text(modifier = Modifier.weight(1f), text = it.mssv.toString())
                    Text(modifier = Modifier.weight(1f), text = it.diemTB.toString())
                    Text(modifier = Modifier.weight(1f), text = it.daratruong.toString())
                }
                Divider()
            }
        }
    }

}

@Composable
fun ShowDialogStudentInfor(
    onConfirmation: () -> Unit,
    dialogTitle: String = "Thong tin SV",
    dialogMessage: String,
) {
    Dialog(onDismissRequest = {}) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            modifier = Modifier.padding(20.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(dialogTitle, style =
                MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(20.dp))
                Text(dialogMessage, style =
                MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onConfirmation,

                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.DarkGray,
                        contentColor = Color.White
                    )
                ) {
                    Text("Okay")
                }
            }
        }
    }
}


