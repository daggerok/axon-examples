package daggerok.facebook

import org.axonframework.commandhandling.TargetAggregateIdentifier

data class CreatePostCommand(val postId: String)
data class PostCreatedEvent(val postId: String? = null)

data class LikePostCommand(@TargetAggregateIdentifier val postId: String)
data class PostLikedEvent(val postId: String? = null)

data class DislikePostCommand(@TargetAggregateIdentifier val postId: String)
data class PostDislikedEvent(val postId: String? = null)
