package com.rizqi.tms.domain.item

import com.rizqi.tms.data.repository.item.ItemRepository
import javax.inject.Inject

interface CheckItemNameAlreadyExistUseCase {
    suspend operator fun invoke(name : String) : Boolean
}

class CheckItemNameAlreadyExistUseCaseImpl @Inject constructor(
    private val itemRepository: ItemRepository
): CheckItemNameAlreadyExistUseCase{
    override suspend fun invoke(name: String): Boolean {
        return itemRepository.checkItemNameAlreadyExist(name)
    }

}