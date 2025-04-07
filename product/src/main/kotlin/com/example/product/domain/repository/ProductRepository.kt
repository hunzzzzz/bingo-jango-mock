package com.example.product.domain.repository

import com.example.product.domain.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long>, ProductCustomRepository