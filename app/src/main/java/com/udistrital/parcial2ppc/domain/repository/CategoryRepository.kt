package com.udistrital.parcial2ppc.domain.repository

import com.udistrital.parcial2ppc.domain.model.Category

interface CategoryRepository {
    suspend fun getCategories(): Result<List<Category>>
}
