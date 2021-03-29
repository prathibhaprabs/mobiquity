package com.mobiquity.challenge.ui.city

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class BookmarkedCitiesInfoModel {
    @Id
    var id: Long = 0

    val citiesList: String = ""
}