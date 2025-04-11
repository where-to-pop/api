package com.wheretopop.shared.model

/**
 * 위치 정보를 표현하는 Value Object
 */
data class Location(
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        fun of(latitude: Double, longitude: Double): Location {
            require(latitude >= -90.0 && latitude <= 90.0) { "위도는 -90도에서 90도 사이여야 합니다." }
            require(longitude >= -180.0 && longitude <= 180.0) { "경도는 -180도에서 180도 사이여야 합니다." }
            return Location(latitude, longitude)
        }
    }

    /**
     * 두 지점 간의 대략적인 거리 계산 (하버사인 공식)
     * @param other 다른 위치
     * @return 거리 (킬로미터)
     */
    fun distanceTo(other: Location): Double {
        val earthRadiusKm = 6371.0
        
        val dLat = Math.toRadians(other.latitude - latitude)
        val dLon = Math.toRadians(other.longitude - longitude)
        
        val lat1 = Math.toRadians(latitude)
        val lat2 = Math.toRadians(other.latitude)
        
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        
        return earthRadiusKm * c
    }

    /**
     * 특정 반경 내에 위치하는지 확인
     * @param center 중심 위치
     * @param radiusKm 반경 (킬로미터)
     * @return 반경 내에 있으면 true
     */
    fun isWithinRadius(center: Location, radiusKm: Double): Boolean {
        return distanceTo(center) <= radiusKm
    }

    override fun toString(): String {
        return "위도: $latitude, 경도: $longitude"
    }
} 