package com.example.calculatorcompose.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp



//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun Yeah() {
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                { Text(text = "Note's") }
//            )
//        },
//        bottomBar = {
//            Row(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(8.dp)
//            ) {
//                Button(onClick = {
//                    onClearAll()
//                } ) {
//                    Text("Clear all")
//                }
//
//                Button(onClick = {
//                    onAddNote()
//                } ) {
//                    Text("add")
//                }
//            }
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .padding(paddingValues)
//                .fillMaxSize()
//                .padding(16.dp)
//        ) {
//            OutlinedTextField(
//                value = noteText,
//                onValueChange = { onAddNote() }
//            )
//        }
//
//    }
//
//}