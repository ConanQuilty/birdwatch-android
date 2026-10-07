package com.example.birdwatch

import java.util.concurrent.atomic.AtomicLong

class BirdStore {

    private val birds = ArrayList<Bird>();
    private val lastId = AtomicLong(0L);

    fun findAll(): List<Bird> {
        return birds
    }

    fun create(bird: Bird) {
        bird.id = lastId.incrementAndGet()
        birds.add(bird)
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
            true
        } else {
            false
        }
    }

    fun delete(id: Long): Boolean {
        val foundBird = findOne(id)
        return if (foundBird != null) {
            birds.remove(foundBird)
            true
        } else {
            false
        }
    }

    fun findOne(id: Long): Bird? {
        return birds.find { b -> b.id == id }
    }

}