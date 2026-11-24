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
        bird.id = lastId.incrementAndGet()
        birds.add(bird)
        Timber.d("create: id=${bird.id}")
    }

    fun update(bird: Bird): Boolean {
        val foundBird = findOne(bird.id)
        return if (foundBird != null) {
            val foundIndex = birds.indexOf(foundBird)
            birds[foundIndex] = birds[foundIndex].copy(
                name = bird.name,
                description = bird.description,
                x = bird.x,
                y = bird.y,
            )
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