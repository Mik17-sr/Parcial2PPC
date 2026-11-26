package com.udistrital.parcial2ppc.data.repository

import com.udistrital.parcial2ppc.data.remote.TripApiService
import com.udistrital.parcial2ppc.domain.model.Category
import com.udistrital.parcial2ppc.domain.repository.CategoryRepository

class CategoryRepositoryImpl(private val api: TripApiService) : CategoryRepository {
    override suspend fun getCategories(): Result<List<Category>> {
        return try {
            Result.success(api.getCategories().map { dto ->
                Category(
                    id = dto.id ?: 0,
                    name = dto.name ?: "",
                    description = dto.description ?: ""
                )
            })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
