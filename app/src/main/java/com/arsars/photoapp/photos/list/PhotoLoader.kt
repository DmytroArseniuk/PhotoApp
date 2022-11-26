package com.arsars.photoapp.photos.list

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.LruCache
import com.arsars.photoapp.crypto.CryptoManager
import com.arsars.photoapp.data.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.SoftReference
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class PhotoLoader(
    private val resources: Resources,
    private val cryptoManager: CryptoManager
) {
    val cache = CopyOnWriteArrayList<Pair<String, ByteArray>>()

    var reusableBitmaps: MutableSet<SoftReference<Bitmap>> =
        Collections.synchronizedSet(HashSet<SoftReference<Bitmap>>())
    private var memoryCache: LruCache<String, BitmapDrawable> =
        object : LruCache<String, BitmapDrawable>(CACHE_SIZE) {

            // Notify the removed entry that is no longer being cached.
            override fun entryRemoved(
                evicted: Boolean,
                key: String,
                oldValue: BitmapDrawable,
                newValue: BitmapDrawable?
            ) {
                oldValue.let {
                    reusableBitmaps.add(SoftReference(it.bitmap))
                }
            }
        }

    fun decodeSampledBitmapByteArray(
        byteArray: ByteArray,
    ): Bitmap {
        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inSampleSize = 1
        addInBitmapOptions(options)
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
    }

    private fun addInBitmapOptions(options: BitmapFactory.Options) {
        // inBitmap only works with mutable bitmaps, so force the decoder to
        // return mutable bitmaps.
        options.inMutable = true

        // Try to find a bitmap to use for inBitmap.
        getBitmapFromReusableSet(options)?.also { inBitmap ->
            // If a suitable bitmap has been found, set it as the value of
            // inBitmap.
            options.inBitmap = inBitmap
        }
    }

    fun getBitmapFromReusableSet(options: BitmapFactory.Options): Bitmap? {
        reusableBitmaps.takeIf { it.isNotEmpty() }?.let { reusableBitmaps ->
            synchronized(reusableBitmaps) {
                val iterator: MutableIterator<SoftReference<Bitmap>> = reusableBitmaps.iterator()
                while (iterator.hasNext()) {
                    iterator.next().get()?.let { item ->
                        if (item.isMutable) {
                            // Check to see it the item can be used for inBitmap.
                            if (canUseForInBitmap(item, options)) {
                                // Remove from reusable set so it can't be used again.
                                iterator.remove()
                                return item
                            }
                        } else {
                            // Remove from the set if the reference has been cleared.
                            iterator.remove()
                        }
                    }
                }
            }
        }
        return null
    }

    suspend fun load(photo: Photo): BitmapDrawable {
        return withContext(Dispatchers.Default) {
            val cachedPhoto = cache.firstOrNull { (id, _) -> id == photo.id.toString() }?.second
            val byteArray = if (cachedPhoto != null) {
                cachedPhoto
            } else {
                val decrypt = cryptoManager.decrypt(photo.file.readBytes())

                if (cache.size > CACHE_SIZE) {
                    cache.removeAt(0)
                }
                cache.add(photo.id.toString() to decrypt)

                decrypt
            }


            val cachedBitmap = memoryCache.get(photo.id.toString())

            return@withContext if (cachedBitmap != null) {
                cachedBitmap
            } else {
                val decodeSampledBitmapByteArray = decodeSampledBitmapByteArray(byteArray)
                val bitmapDrawable = BitmapDrawable(resources, decodeSampledBitmapByteArray)
                memoryCache.put(photo.id.toString(), bitmapDrawable)
                bitmapDrawable
            }
        }
    }

    private fun canUseForInBitmap(
        candidate: Bitmap,
        targetOptions: BitmapFactory.Options
    ): Boolean {
        val width: Int = targetOptions.outWidth / targetOptions.inSampleSize
        val height: Int = targetOptions.outHeight / targetOptions.inSampleSize
        val byteCount: Int = width * height * getBytesPerPixel(candidate.config)
        return byteCount <= candidate.allocationByteCount
    }

    private fun getBytesPerPixel(config: Bitmap.Config): Int {
        return when (config) {
            Bitmap.Config.ARGB_8888 -> 4
            Bitmap.Config.RGB_565, Bitmap.Config.ARGB_4444 -> 2
            Bitmap.Config.ALPHA_8 -> 1
            else -> 1
        }
    }

    companion object {
        private const val CACHE_SIZE = 10
    }
}