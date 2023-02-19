package com.rizqi.tms.data.datasource.storage.images

import android.content.Context
import androidx.core.graphics.drawable.toBitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.rizqi.tms.R
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

@RunWith(JUnit4::class)
@SmallTest
class ImageStorageDataSourceTest {

    val context = ApplicationProvider.getApplicationContext<Context>()
    val mainImageStorageDataSource = MainImageStorageDataSource(context)

    @Test
    @Throws(IOException::class)
    fun save_image_to_external_storage_success() {
        runBlocking{
            val bitmap = context.getDrawable(R.drawable.ic_logo)?.toBitmap()
            val imageRequest = ImageRequest("Test Image Insert", bitmap, "Test")

            val storageResult = mainImageStorageDataSource.saveImageToExternalStorage(imageRequest)

            assertThat(storageResult.status).isTrue()
            assertThat(storageResult.message).isNull()
            assertThat(storageResult.data).isNotNull()
            val imageResponse = storageResult.data
            assertThat(imageResponse?.imageId).isNotNull()
            assertThat(imageResponse?.bitmap).isNotNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun get_image_to_external_storage_success() {
        runBlocking{
            val bitmap = context.getDrawable(R.drawable.ic_logo)?.toBitmap()
            val imageRequest = ImageRequest("Test Image Get", bitmap, "Test")
            val imageId = mainImageStorageDataSource.saveImageToExternalStorage(imageRequest).data!!.imageId!!

            val storageResult = mainImageStorageDataSource.getImageFromExternalStorage(imageId)

            assertThat(storageResult.status).isTrue()
            assertThat(storageResult.message).isNull()
            assertThat(storageResult.data).isNotNull()
            val imageResponse = storageResult.data
            assertThat(imageResponse?.imageId).isNotNull()
            assertThat(imageResponse?.bitmap).isNotNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun update_image_to_external_storage_success() {
        runBlocking{
            val bitmap1 = context.getDrawable(R.drawable.ic_logo)?.toBitmap()
            val bitmap2 = context.getDrawable(R.drawable.ic_logo_no_background)?.toBitmap()
            val insertImageRequest = ImageRequest("Test Image Update", bitmap1, "Test")
            val imageId = mainImageStorageDataSource.saveImageToExternalStorage(insertImageRequest).data!!.imageId!!
            val updateImageRequest = ImageRequest("Test Image For Update", bitmap2, "Test")

            val storageResult = mainImageStorageDataSource.updateImageInExternalStorage(imageId, updateImageRequest)

            assertThat(storageResult.status).isTrue()
            assertThat(storageResult.message).isNull()
            assertThat(storageResult.data).isNotNull()
            val imageResponse = storageResult.data
            assertThat(imageResponse?.imageId).isNotNull()
            assertThat(imageResponse?.bitmap).isNotNull()
        }
    }

    @Test
    @Throws(IOException::class)
    fun delete_image_to_external_storage_success() {
        runBlocking{
            val bitmap = context.getDrawable(R.drawable.ic_logo)?.toBitmap()
            val imageRequest = ImageRequest("Test Image Delete", bitmap, "Test")
            val imageId = mainImageStorageDataSource.saveImageToExternalStorage(imageRequest).data!!.imageId!!

            mainImageStorageDataSource.deleteImageInExternalStorage(imageId)
            val storageResult = mainImageStorageDataSource.getImageFromExternalStorage(imageId)

            assertThat(storageResult.status).isFalse()
            assertThat(storageResult.message).isNotNull()
            assertThat(storageResult.data).isNull()
            val imageResponse = storageResult.data
            assertThat(imageResponse?.imageId).isNull()
            assertThat(imageResponse?.bitmap).isNull()
        }
    }
}