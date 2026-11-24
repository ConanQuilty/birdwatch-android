package com.example.birdwatch

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.io.File
import java.util.concurrent.atomic.AtomicLong

class BirdStore(private val file: File) {

    private val birds = ArrayList<Bird>();
    private val lastId = AtomicLong(0L);
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    init {
        load()
    }

    private fun load() {
        if (!file.exists()) return

        val type = object : TypeToken<ArrayList<Bird>>() {}.type

        val loaded: ArrayList<Bird> =
            gson.fromJson(file.readText(), type) ?: arrayListOf()

        birds.clear()
        birds.addAll(loaded)

        lastId.set(birds.maxOfOrNull { it.id } ?: 0L)
    }

    private fun save() {
        Timber.d("Saving to file...")
        file.parentFile?.mkdirs()
        file.writeText(gson.toJson(birds))
    }

    fun findAll(): List<Bird> {
        Timber.v("findAll: ${birds.size} birds")
        return birds
    }

    fun create(bird: Bird) {
        bird.validate()
        bird.id = lastId.incrementAndGet()
        birds.add(bird)
        Timber.d("create: id=${bird.id}")
        save()
    }

    fun update(bird: Bird): Boolean {
        bird.validate()
        val foundBird = findOne(bird.id)
        return if (foundBird != null) {
            foundBird.name = bird.name
            foundBird.description = bird.description
            foundBird.x = bird.x
            foundBird.y = bird.y
            foundBird.imageUri = bird.imageUri   // new: keep the photo too
            Timber.d("update: id=${bird.id} updated")
            save()
            true
        } else {
            Timber.w("update: id=${bird.id} not found")
            false
        }
    }

    fun delete(id: Long): Boolean {
        val foundBird = findOne(id)
        return if (foundBird != null) {
            birds.remove(foundBird)
            Timber.d("delete: id=$id removed")
            save()
            true
        } else {
            Timber.w("delete: id=$id not found")
            false
        }
    }

    fun findOne(id: Long): Bird? {
        val bird = birds.find { b -> b.id == id }
        Timber.v("findOne: id=$id found=${bird != null}")
        return bird
    }

}