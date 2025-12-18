package com.training.ecommerce.domain.models

import com.training.ecommerce.data.models.user.UserDetailsModel
import com.training.ecommerce.data.models.user.UserDetailsPreferences

fun UserDetailsPreferences.toUserDetailsModel() : UserDetailsModel {
    return UserDetailsModel(
        id = id,
        name = name,
        email = email,
        reviews = reviewsList,
    )
}

fun UserDetailsModel.toUserDetailsPreferences() : UserDetailsPreferences {
    return UserDetailsPreferences.newBuilder()
        .setId(id)
        .setName(name)
        .setEmail(email)
        .addAllReviews(reviews?.toList() ?: emptyList())
        .build()
}



