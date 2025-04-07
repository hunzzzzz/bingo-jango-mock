package com.example.product.domain.repository

import com.example.product.domain.dto.response.ProductResponse
import com.example.product.domain.model.ProductStatus
import com.example.product.domain.model.QProduct
import com.example.product.domain.model.property.Category
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : ProductCustomRepository {
    private val product = QProduct.product

    private fun productResponseProjection() = Projections.constructor(
        ProductResponse::class.java,
        product.id,
        product.category,
        product.name,
        product.description,
        product.price,
        product.totalQuantity,
        product.quantity
    )

    override fun getPopularProductsAmongUsers(): List<ProductResponse> {
        return jpaQueryFactory.select(
            productResponseProjection()
        ).from(product)
            .where(product.status.eq(ProductStatus.ON_SALE))
            .orderBy(product.orderCount.desc())
            .limit(5)
            .fetch()
    }

    override fun getProductsWithUserPattern(category: Category): List<ProductResponse> {
        return jpaQueryFactory.select(
            productResponseProjection()
        ).from(product)
            .where(
                product.category.eq(category),
                product.status.eq(ProductStatus.ON_SALE)
            )
            .orderBy(product.orderCount.desc())
            .fetch()
    }
}