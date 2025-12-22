package com.example.simplenotes.ui.theme


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