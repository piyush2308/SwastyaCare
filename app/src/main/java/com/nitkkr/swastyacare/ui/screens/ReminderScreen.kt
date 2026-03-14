package com.nitkkr.swastyacare.ui.screens

import android.app.*
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nitkkr.swastyacare.data.AppDatabase
import com.nitkkr.swastyacare.data.ReminderEntity
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun ReminderScreen() {

    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()

    var medicine by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    var reminders by remember { mutableStateOf(listOf<ReminderEntity>()) }

    LaunchedEffect(Unit) {
        reminders = db.reminderDao().getAllReminders()
    }

    val calendar = Calendar.getInstance()

    val timePicker = TimePickerDialog(
        context,
        { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)

            time = "$hour:$minute"
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Medicine Reminder", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = medicine,
            onValueChange = { medicine = it },
            label = { Text("Medicine Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = { timePicker.show() }) {

            Text(if (time.isEmpty()) "Select Time" else "Reminder: $time")

        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {

            if (medicine.isBlank() || time.isBlank()) {

                Toast.makeText(context, "Enter medicine and time", Toast.LENGTH_SHORT).show()
                return@Button

            }

            scope.launch {

                db.reminderDao().insertReminder(
                    ReminderEntity(
                        medicineName = medicine,
                        time = time
                    )
                )

                reminders = db.reminderDao().getAllReminders()

                val alarmManager =
                    context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val intent = Intent(context, ReminderReceiver::class.java)
                intent.putExtra("medicine", medicine)

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    Random().nextInt(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )

                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )

                medicine = ""
                time = ""

            }

        }) {
            Text("Add Reminder")
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn {

            items(reminders) { reminder ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {

                    Column(modifier = Modifier.padding(16.dp)) {

                        Text("Medicine: ${reminder.medicineName}")
                        Text("Time: ${reminder.time}")

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(onClick = {

                            scope.launch {

                                db.reminderDao().deleteReminder(reminder)

                                reminders =
                                    db.reminderDao().getAllReminders()

                            }

                        }) {
                            Text("Delete Reminder")
                        }

                    }

                }

            }

        }

    }

}