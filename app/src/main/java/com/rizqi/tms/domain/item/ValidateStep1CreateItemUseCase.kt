package com.rizqi.tms.domain.item

import com.rizqi.tms.R
import com.rizqi.tms.domain.Resource
import com.rizqi.tms.shared.StringResource

interface ValidateStep1CreateItemUseCase{
    operator fun invoke(itemName : String) : Resource<Boolean>
}

class ValidateStep1CreateItemUseCaseImpl : ValidateStep1CreateItemUseCase {
    override fun invoke(itemName: String): Resource<Boolean> {
        var message : StringResource? = null
        val isValid = itemName.isNotBlank()
        if (!isValid){
            message = StringResource.StringResWithParams(R.string.this_field_cant_be_empty)
        }
        return Resource(
            isValid, isValid, mutableMapOf(null to message)
        )
    }
}