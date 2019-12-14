package com.example.myapplication.model

import java.time.LocalDateTime

data class RepoInfo(
    var name : String,
    var description : String,
    var language : String,
    var star_count : Int,
    var updated_on : LocalDateTime,
    var link : String
) {}