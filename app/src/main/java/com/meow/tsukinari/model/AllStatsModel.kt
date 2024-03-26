package com.meow.tsukinari.model


//all stats for admin only
data class AllStatsModel(
    val totalUsers: Long = 0,
    val totalFictions: Long = 0,
    val totalChapters: Long = 0,
    val totalViews: Long = 0,
    val totalLikes: Long = 0,
    val totalDislikes: Long = 0,
    val totalComments: Long = 0,
    val totalVerifiedFictions: Long = 0,
    val totalUnverifiedFictions: Long = 0,
    val totalActiveUser: Long = 0,
    val totalInactiveUser: Long = 0,
)


//stats for user to review their own stats
data class UserStatsModel(
    val myTotalFictions: Long = 0,
    val myTotalChapters: Long = 0,
    val myTotalViews: Long = 0,
    val myTotalLikes: Long = 0,
    val myTotalDislikes: Long = 0,
    val myTotalComments: Long = 0,
    val myTotalVerifiedFictions: Long = 0,
    val myTotalUnverifiedFictions: Long = 0,
)