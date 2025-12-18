package com.training.ecommerce.data.models.user

import androidx.annotation.Keep

@Keep
data class UserDetailsModel(
    var createdAt :Long? = null,
    var id :String? = null,
    var name :String? = null,
    var email :String? = null,
    var disabler : Boolean? = null,
    var reviews : List<String>? =null
)
