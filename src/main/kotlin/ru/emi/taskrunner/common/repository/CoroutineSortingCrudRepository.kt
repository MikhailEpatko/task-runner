package ru.emi.taskrunner.common.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

interface CoroutineSortingCrudRepository<T, ID> : CoroutineSortingRepository<T, ID>, CoroutineCrudRepository<T, ID>