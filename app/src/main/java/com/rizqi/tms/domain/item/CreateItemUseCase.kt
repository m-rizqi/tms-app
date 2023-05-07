package com.rizqi.tms.domain.item

import com.rizqi.tms.data.datasource.storage.images.ImageRequest
import com.rizqi.tms.data.datasource.storage.images.ImageStorageDataSource
import com.rizqi.tms.data.model.Item
import com.rizqi.tms.data.repository.item.ItemRepository
import com.rizqi.tms.domain.Resource
import com.rizqi.tms.shared.StringResource
import javax.inject.Inject

interface CreateItemUseCase {
    suspend operator fun invoke(item: Item) : Resource<Item>
}

class CreateItemUseCaseImpl @Inject constructor(
    private val itemRepository: ItemRepository,
    private val storageDataSource: ImageStorageDataSource
) : CreateItemUseCase {
    override suspend fun invoke(item: Item): Resource<Item> {
        val messages = mutableListOf<StringResource?>()
        val storageResult = storageDataSource.saveImageToExternalStorage(
            ImageRequest(
                item.name,
                item.image,
                "Items"
            )
        )
        item.imageId = storageResult.data?.imageId
        if (!storageResult.status){
            messages.add(storageResult.message)
        }
        val itemResource = itemRepository.insertItem(item)
        if (!itemResource.isSuccess){
            messages.add(itemResource.message)
        }
        return Resource(
            itemResource.isSuccess,
            itemResource.data,
            messages.mapIndexed { index, stringResource ->
                index.toString() to stringResource
            }.toMap().toMutableMap()
        )
    }
}