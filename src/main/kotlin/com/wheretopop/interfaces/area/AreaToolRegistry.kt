package com.wheretopop.interfaces.area

import com.wheretopop.application.area.AreaFacade
import com.wheretopop.shared.domain.identifier.AreaId
import mu.KotlinLogging
import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Component


/**
 * AI tool registry for searching area information.
 * Uses Spring AI's Tool Calling feature to provide area data in natural language format.
 */
@Component
class AreaToolRegistry(
    private val areaFacade: AreaFacade
) {
    private val logger = KotlinLogging.logger {}
    
    /**
     * Retrieves all area information.
     * AI model calls this tool when the user needs a complete list of areas.
     * 
     * @return A natural language string containing all area information
     */
    @Tool(description = "Retrieves all area information.\nUse this tool when a user requests a complete list or data of areas.\n If you need to know ID of area, use this tool.\nAppropriate for questions like 'What areas are available?', 'Show me all areas', 'List of possible areas', etc.\nThe response includes ID, name, description, and location information for all areas.\nIf there are many results, you can show just a few examples to the user.")
    fun findAllArea(): String {
        logger.info("findAllArea tool was called")
        
        val areas = areaFacade.findAll()
        
        logger.info("Retrieved information for ${areas.size} areas")
        
        if (areas.isEmpty()) {
            return "There are currently no registered areas."
        }
        
        // Build detailed information for all areas
        val areaDetails = areas.map { area ->
            """
            |Area: ${area.name} (ID: ${area.id})
            |Description: ${area.description ?: "No description available"}
            |Location: Latitude ${area.location.latitude}, Longitude ${area.location.longitude}
            """.trimMargin()
        }.joinToString("\n\n")
        
        return """
        |I found ${areas.size} areas in total:
        |
        |$areaDetails
        """.trimMargin()
    }

    /**
     * Retrieves detailed information about an area with the specified ID.
     * AI model calls this tool when detailed information about a specific area is needed.
     * 
     * @param id The ID of the area to look up
     * @return A natural language string containing detailed information about the area
     */
    @Tool(description = "Retrieves detailed information about an area by ID.\nUse this tool when a user requests specific information about an area (details, population statistics, congestion level, etc.).\nWhen a user mentions a specific area name or asks questions like 'Tell me more about this area', 'How crowded is Gangnam Station?', etc.,\nfirst find the area ID and then use this tool to provide detailed information.\nThe result includes basic area information along with population statistics, congestion level, resident/non-resident ratios, and other detailed data.\nReturns an appropriate error message if the ID doesn't exist.")
    fun findAreaById(id: String): String {
        logger.info("findAreaById tool was called: id={}", id)
        
        val area = areaFacade.getAreaDetailById(AreaId.of(id))
        
        if (area == null) {
            logger.warn("Could not find area with ID {}", id)
            return "No area found with ID '$id'."
        }
        
        logger.info("Successfully retrieved information for area with ID {}: {}", id, area.name)
        
        // Basic area information
        val basicInfo = """
        |Area: ${area.name} (ID: ${area.id})
        |Description: ${area.description ?: "No description available"}
        |Location: Latitude ${area.location.latitude}, Longitude ${area.location.longitude}
        """.trimMargin()
        
        // Population insight information if available
        val populationInfo = area.populationInsight?.let { insight ->
            val peakTimes = insight.peakTimes.joinToString("\n|• ") { peak ->
                "Hour ${peak.hour}: ${peak.expectedCongestion} (Est. ${peak.populationEstimate} people)"
            }
            
            val ageDistribution = with(insight.demographicInsight.ageDistribution) {
                """
                |• Under 10: ${under10Rate * 100}%
                |• 10s: ${age10sRate * 100}%
                |• 20s: ${age20sRate * 100}%
                |• 30s: ${age30sRate * 100}%
                |• 40s: ${age40sRate * 100}%
                |• 50s: ${age50sRate * 100}%
                |• 60s: ${age60sRate * 100}%
                |• Over 70: ${over70sRate * 100}%
                |• Dominant age group: ${dominantAgeGroup}
                """.trimMargin()
            }
            
            val genderRatio = with(insight.demographicInsight.genderRatio) {
                "Male: ${maleRate * 100}%, Female: ${femaleRate * 100}%"
            }
            
            """
            |
            |Population Information:
            |• Current congestion level: ${insight.congestionLevel}
            |• Congestion message: ${insight.congestionMessage}
            |• Current population: ${insight.currentPopulation} people
            |• Population density level: ${insight.populationDensity.level}
            |• Residents: ${insight.populationDensity.residentRate * 100}%
            |• Non-residents: ${insight.populationDensity.nonResidentRate * 100}%
            |
            |Demographic Insights:
            |• Gender ratio: $genderRatio
            |• Main visitor group: ${insight.demographicInsight.mainVisitorGroup}
            |
            |Age Distribution:
            $ageDistribution
            |
            |Peak Hours:
            |• $peakTimes
            |
            |Last updated: ${insight.lastUpdatedAt}
            """.trimMargin()
        } ?: "\n\nNo population data available for this area."
        
        return basicInfo + populationInfo
    }

    /**
     * Finds the nearest area based on coordinates.
     * AI model uses this to find areas close to the user's current location.
     * 
     * @param latitude Latitude coordinate
     * @param longitude Longitude coordinate
     * @return A natural language string containing information about the nearest area
     */
    @Tool(description = "Finds the nearest area based on latitude and longitude coordinates.\nUse this tool when a user requests information about areas near their current location or near specific coordinates.\nAppropriate for questions like 'What areas are near me?', 'Show areas near these coordinates', 'What's the closest hotspot?', etc.\nLatitude and longitude values are required; if coordinates are not provided, ask the user for location information.\nThe response includes the ID, name, description, and distance from the user's location for the nearest area.")
    fun findNearestArea(latitude: Double, longitude: Double): String {
        logger.info("findNearestArea tool was called: latitude={}, longitude={}", latitude, longitude)
        
        // Actual implementation requires additional functionality in areaFacade.
        // Below is an example response format.
        return """
        |The nearest area to your location (latitude: $latitude, longitude: $longitude) is Sample Area (ID: sample-id).
        |
        |Description: This is a sample area.
        |Distance: Approximately 0.5km away
        |Location: Latitude 37.123, Longitude 127.456
        |
        |Note: For complete detailed information about this area, you can use the findAreaById tool with ID 'sample-id'.
        """.trimMargin()
    }
}

// ToolDispatcherConfig는 남겨두되 AreaToolRegistry에서는 더이상 사용하지 않음