package com.kien.microservices.core.review.services

import com.kien.api.core.review.Review
import com.kien.api.core.review.ReviewService
import com.kien.api.exceptions.InvalidInputException
import com.kien.util.http.ServiceUtil
import com.kien.util.logs.logWithClass
import org.springframework.web.bind.annotation.RestController

private val LOG = logWithClass<ReviewServiceImpl>()

@RestController
class ReviewServiceImpl(
    private val serviceUtil: ServiceUtil
) : ReviewService {
    override fun getReviews(productId: Int): List<Review> {

        if (productId < 1) {
            throw InvalidInputException("Invalid productId: $productId")
        }

        if (productId == 213) {
            LOG.debug("No reviews found for productId: {}", productId)
            return ArrayList()
        }

        val list = listOf<Review>(
            Review(productId, 1, "Author 1", "Subject 1", "Content 1", serviceUtil.serviceAddress),
            Review(productId, 2, "Author 2", "Subject 2", "Content 2", serviceUtil.serviceAddress),
            Review(productId, 3, "Author 3", "Subject 3", "Content 3", serviceUtil.serviceAddress)
        )

        LOG.debug("/reviews response size: {}", list.size)

        return list
    }
}