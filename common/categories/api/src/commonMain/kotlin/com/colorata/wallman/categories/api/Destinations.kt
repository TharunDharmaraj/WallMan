package com.colorata.wallman.categories.api

import com.colorata.wallman.core.data.Destinations
import com.colorata.wallman.core.data.destination
import com.colorata.wallman.core.data.withArgument

fun Destinations.CategoriesDestination() = destination("CategoriesList")
fun Destinations.CategoryDetailsDestination(categoryIndex: Int? = null) = destination(
    "Category/{index}", "Category/"
).withArgument(
    categoryIndex, argumentName = "index", defaultValue = "0"
) {
    it
}