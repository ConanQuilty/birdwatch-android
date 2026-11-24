package com.example.birdwatch

import timber.log.Timber
import java.util.concurrent.atomic.AtomicLong

class BirdStore {

    private val birds = ArrayList<Bird>();
    private val lastId = AtomicLong(0L);

    fun findAll(): List<Bird> {
        Timber.v("findAll: ${birds.size} birds")
        return birds
    }

    fun create(bird: Bird) {
        bird.validate()
        bird.id = lastId.incrementAndGet()
        birds.add(bird)
        Timber.d("create: id=${bird.id}")
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