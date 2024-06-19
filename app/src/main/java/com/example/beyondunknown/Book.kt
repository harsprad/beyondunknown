package com.example.beyondunknown

import java.io.Serializable

data class Book(
    val title: String,
    val path: String
) : Serializable